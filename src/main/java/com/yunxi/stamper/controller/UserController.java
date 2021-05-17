package com.yunxi.stamper.controller;


import com.github.pagehelper.PageHelper;
import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.jwt.JwtUtil;
import com.yunxi.stamper.commons.jwt.RSA.MyKeyFactory;
import com.yunxi.stamper.commons.jwt.RSA.RSACode;
import com.yunxi.stamper.commons.jwt.UserToken;
import com.yunxi.stamper.commons.md5.MD5;
import com.yunxi.stamper.commons.other.*;
import com.yunxi.stamper.commons.regex.RigexUtil;
import com.yunxi.stamper.commons.response.Code;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.commons.sms.SendCode;
import com.yunxi.stamper.commons.sms.SmsUitls;
import com.yunxi.stamper.entity.*;
import com.yunxi.stamper.entityVo.*;
import com.yunxi.stamper.service.*;
import com.yunxi.stamper.sys.aop.annotaion.WebLogger;
import com.yunxi.stamper.sys.context.SpringContextUtils;
import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.sys.error.exception.AccountLockedException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/4/24 0024 13:56
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@Slf4j
@RestController
@Api(tags = "用户、员工相关")
@RequestMapping(value = "/auth/user", method = {RequestMethod.POST, RequestMethod.GET})
public class UserController extends BaseController {

	@Autowired
	private OrgService orgService;
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private UserService userService;
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SignetService signetService;
	@Autowired
	private FingerService fingerService;
	@Autowired
	private UserLoggerService userLoggerService;
	@Autowired
	private PermService permService;
	@Autowired
	private OrgServeService orgServeService;
	@Autowired
	private StrategyPasswordService strategyPasswordService;
	@Autowired
	private ShortcutService shortcutService;
	@Autowired
	private PhoneIconService phoneIconService;
	@Autowired
	private UserPhoneIconService userPhoneIconService;
	@Autowired
	private FileInfoService fileInfoService;
	@Autowired
	private UserInfoService userInfoService;

	@ApiOperation(value = "校验手机号", notes = "校验手机号是否在系统中已存在", httpMethod = "GET")
	@GetMapping("/checkPhone")
	public ResultVO checkPhone(@RequestParam String phone) {
		/**
		 * 与前端协商，如果返回值中data不为空，则手机号存在，否则不存在
		 */
		if (StringUtils.isBlank(phone) || !RigexUtil.isMobileNO(phone)) {
			return new ResultVO(200, null, null);
		}
		UserInfo userInfo = getUserInfo();
		Integer orgId = userInfo.getOrgId();
		SysUser sysUser = sysUserService.get(orgId, phone);
		if (sysUser == null) {
			return new ResultVO(200, null, null);
		}
		return new ResultVO(200, null, phone);
	}

	@ApiOperation(value = "报表条件-用户列表", notes = "报表条件-用户列表", httpMethod = "GET")
	@ApiImplicitParam(name = "keyword", value = "关键词", dataType = "string")
	@GetMapping("/userListForReport")
	public ResultVO userListForReport(@RequestParam(required = false) String keyword) {
		UserInfo userInfo = getUserInfo();
		/*手动检查用户权限*/
		String url = "/device/signet/getByOwner";
		if (userInfo.isOwner() || userInfo.isAdmin() || userInfo.getPermsUrls().contains(url)) {
			//属主,管理员或拥有印章管理权限的用户,拥有导出报表权限
			log.info("属主,管理员或拥有印章管理权限的用户,拥有导出报表权限");
		} else {
			return ResultVO.FAIL("无权限");
		}

		Integer orgId = userInfo.getOrgId();
		List<Integer> visualDepartmentIds = userInfo.getVisualDepartmentIds();
		List<User> users = userService.getByOrgAndDepartment(orgId, visualDepartmentIds);
//		List<User> users = userService.getUserList(orgId, keyword);

		/*前端需要的参数*/
		List<Map<String, Object>> resList = new LinkedList<>();
		for (User user : users) {
			Map<String, Object> res = new HashMap<>(2);
			res.put("id", user.getId());
			res.put("name", user.getUserName());
			resList.add(res);
		}


		return ResultVO.OK(resList);
	}

	/*
	 * 查询登录用户非管理员列表(公司,部门)
	 */
	@GetMapping("/getDepartmentManagers")
	public ResultVO getDepartmentManagers() {
		UserInfo userInfo = getUserInfo();
		//查询该公司拥有管理员权限的用户列表
		List<User> managers = userService.getByOrgAndRole(userInfo.getOrgId(), "admin");
		return ResultVO.OK(managers);
	}

	@GetMapping("/getEmplist")
	public ResultVO getEmplist() {
		UserInfo userInfo = getUserInfo();
		List<User> emps = userService.getEmps(userInfo.getOrgId());
		if (emps != null) {
			return ResultVO.OK(emps);
		}
		return ResultVO.FAIL(Code.FAIL400);
	}


	/*
	 * 员工列表
	 *
	 * @param keyword 关键词
	 * @param list    是否通讯录格式  true:是  false:不是
	 * @return
	 */
	@GetMapping("/getUsersByKeyword")
	public ResultVO getUsersByKeyword(@RequestParam(value = "keyword", required = false) String keyword,
									  @RequestParam(value = "list", required = false, defaultValue = "false") boolean list) {
		UserInfo userInfo = getUserInfo();
		List<User> users = userService.getUsersByKeyword(userInfo.getOrgId(), keyword);
		if (list) {
			List<Map<String, Object>> addressListRes = CommonUtils.getAddressList(users, "name");
			return ResultVO.OK(addressListRes);
		}
		return ResultVO.OK(users);
	}

	/*
	 * 查询登录用户所属公司组织架构中的所有用户列表
	 */
	@GetMapping("/getUsersByOrg")
	public ResultVO getUsersByOrg() {
		UserToken token = getToken();
		boolean page = setPage();
		Integer orgId = token.getOrgId();
		List<User> users = userService.getByOrg(orgId);
		return ResultVO.Page(users, page);
	}


	/*
	 * 查询公司下所有用户列表
	 */
	@GetMapping("/getByOrg")
	public ResultVO getByOrg(@RequestParam("orgId") Integer orgId) {
		Org org = orgService.get(orgId);

		//只能查询本公司用户列表
		UserToken token = getToken();
		if (token.getOrgId().intValue() != orgId) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		if (org != null) {
			boolean page = setPage();
			List<UserVo> userVos = userService.getByOrgId(orgId);

			if (userVos != null && userVos.size() > 0) {
				for (UserVo userVo : userVos) {
					//状态
					userVo.setUse(userVo.getStatus() == Global.USER_STATUS_NORMAL);
				}
			}
			return ResultVO.Page(userVos, page);
		}
		return ResultVO.FAIL("该公司不存在");
	}

	/*
	 * 短信修改密码
	 */
	@PostMapping("/changePasswordV2")
	@Transactional
	public ResultVO changePasswordV2(@RequestParam("phone") String phone,
									 @RequestParam("code") String code,
									 @RequestParam("newPassword") String newPassword) {
		/*
		 * 参数校验：手机号
		 */
		SysUser sysUser = sysUserService.getByPhone(phone);
		if (sysUser == null) {
			return ResultVO.FAIL("手机号未注册");
		}

		/*
		 * 参数校验：验证码
		 */
		String key = RedisGlobal.PHONE_VERIFI_CODE + 1 + ":" + phone;
		Object obj = redisUtil.get(key);
		if (obj == null
				|| StringUtils.isBlank(obj.toString())
				|| !obj.toString().equalsIgnoreCase(code)) {
			return ResultVO.FAIL("验证码不正确");
		}

		/*
		 * 参数校验:密码
		 */
		StrategyPassword sp = strategyPasswordService.getByOrg(properties.getDefaultOrgId());
		try {
			StrategyUtil.checkStrategy(newPassword, sp);
		} catch (Exception e) {
			return ResultVO.FAIL(Code.ERROR510, e.getMessage());
		}

		sysUser.setPassword(MD5.toMD5(newPassword));
		sysUserService.update(sysUser);

		redisUtil.del(key);

		return ResultVO.OK("修改成功");
	}


	/*
	 * 查询公司所有员工列表
	 */
	@GetMapping("/getUsers")
	public ResultVO getUsers(@RequestParam("orgId") Integer orgId) {
		if (orgId != null) {

			//只能操作本公司用户
			UserToken token = getToken();
			if (token.getOrgId().intValue() != orgId) {
				return ResultVO.FAIL(Code.FAIL403);
			}

			boolean page = setPage();
			List<User> users = userService.getByOrg(orgId);
			return ResultVO.Page(users, page);
		}
		return ResultVO.FAIL("参数错误");
	}

	/*
	 * 查询公司拥有Code权限的用户列表
	 */
	@GetMapping("/getByCode")
	public ResultVO getByCode(@RequestParam("orgId") Integer orgId,
							  @RequestParam("code") String code) {
		if (orgId != null && StringUtils.isNotBlank(code)) {

			//只能操作本公司用户
			UserToken token = getToken();
			if (token.getOrgId().intValue() != orgId) {
				return ResultVO.FAIL(Code.FAIL403);
			}

			List<User> users = userService.getByOrgAndPerms(orgId, code);
			return ResultVO.OK(users);
		}
		return ResultVO.FAIL("参数错误");
	}

	@RequestMapping("/getV2")
	public ResultVO getV2(@RequestParam("id") Integer userId) {
		User user = userService.get(userId);
		if (user != null) {

			//只能操作本公司用户
			UserToken token = getToken();
			if (token.getOrgId().intValue() != user.getOrgId()) {
				return ResultVO.FAIL(Code.FAIL403);
			}

			user.setPassword("***");
			return ResultVO.OK(user);
		}
		return ResultVO.OK("用户不存在或已注销");
	}

	/*
	 * 用户注册
	 */
	@ApiOperation(value = "账号注册", notes = "账号注册", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "value2", value = "密码", dataType = "String", required = true),
			@ApiImplicitParam(name = "value3", value = "手机号", dataType = "String", required = true),
			@ApiImplicitParam(name = "value4", value = "验证码", dataType = "String", required = true),
			@ApiImplicitParam(name = "value5", value = "集团编码", dataType = "String", required = true)
	})
	@RequestMapping("/reg")
	public ResultVO reg(@RequestParam("value2") String password,
						@RequestParam("value3") String phone,
						@RequestParam("value4") String vercode,
						@RequestParam("value5") String orgCode) {

		/*检查版本*/
		if (getVersion() > 0) {
			//对账号密码进行RSA解密原文
			String accountDecrypt = null;
			String passDecrypt = null;
			String vercodeDecrypt = null;
			try {
				byte[] decrypt = RSACode.decryptByPrivateKey(password, MyKeyFactory.str_priK);
				passDecrypt = new String(decrypt, StandardCharsets.UTF_8);

				decrypt = RSACode.decryptByPrivateKey(phone, MyKeyFactory.str_priK);
				accountDecrypt = new String(decrypt, StandardCharsets.UTF_8);

				decrypt = RSACode.decryptByPrivateKey(vercode, MyKeyFactory.str_priK);
				vercodeDecrypt = new String(decrypt, StandardCharsets.UTF_8);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (StringUtils.isAnyBlank(accountDecrypt, passDecrypt)) {
				return ResultVO.FAIL("数据有误");
			}
			password = passDecrypt;
			phone = accountDecrypt;
			vercode = vercodeDecrypt;
		}

		/*校验手机号*/
		if (!RigexUtil.isMobileNO(phone)) {
			return ResultVO.FAIL("手机号格式不正确");
		}
		/*
		 * 校验验证码
		 * num 0:注册获取验证码  1:修改密码获取验证码   2:重置密码	3:登录获取验证码   4：注册组织   5:添加员工
		 */
		String key = RedisGlobal.PHONE_VERIFI_CODE + 0 + ":" + phone;
		Object obj = redisUtil.get(key);
		if (obj == null || StringUtils.isBlank(obj.toString()) || !obj.toString().equalsIgnoreCase(vercode)) {
			return ResultVO.FAIL("验证码不正确");
		}

		/*校验用户*/
		SysUser sysUser = sysUserService.getByPhone(phone);
		if (sysUser != null) {
			return ResultVO.FAIL("该手机号已注册");
		}

		/*集团编码*/
		if (StringUtils.isBlank(orgCode)) {
			return ResultVO.FAIL("组织编码不能为空");
		}
		Org org = orgService.getByCode(orgCode);
		if (org == null) {
			return ResultVO.FAIL("该组织不存在");
		}

		userService.regUser(phone, password, org);
		redisUtil.del(key);
		return ResultVO.OK("注册成功");
	}

	/*
	 * 用户登出
	 */
	@WebLogger("登出")
	@RequestMapping("/logout")
	public ResultVO logout(@RequestParam(value = "userId", required = false) Integer userId) {
		try {
			if (userId == null) {
				UserToken token = getToken();
				userId = token.getUserId();
			}
			if (isApp()) {
				//删除缓存
				redisUtil.del(RedisGlobal.USER_INFO_TOKEN_APP + userId);
			} else {
				redisUtil.del(RedisGlobal.USER_INFO_TOKEN_WEB + userId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResultVO.OK(Code.OK);
	}

	/*
	 * 账户激活
	 */
	@WebLogger("账户激活")
	@PostMapping("/activation")
	public ResultVO activation(@RequestParam("userId") Integer userId) {
		/*
		 * 参数校验
		 */
		User user = userService.get(userId);
		if (user == null) {
			return ResultVO.FAIL("该用户不存在");
		}
		UserInfo userInfo = getUserInfo();
		if (user.getOrgId().intValue() != userInfo.getOrgId()) {
			return ResultVO.FAIL("无权限激活");
		}
		Integer type = user.getType();
		if (type != null && type != 3) {
			return ResultVO.OK("已激活");
		}

		user.setType(userInfo.getType());
		userService.update(user);

		return ResultVO.OK("激活成功");
	}

	@ApiOperation(value = "密码登录", notes = "使用密码登录系统", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "aul_value1", value = "登录名(手机号码)", dataType = "String", required = true),
			@ApiImplicitParam(name = "aul_value2", value = "登录密码", dataType = "String", required = true),
			@ApiImplicitParam(name = "aul_value3", value = "客户端CID", dataType = "String"),
			@ApiImplicitParam(name = "aul_value4", value = "要登入的组织ID", dataType = "int", required = true)
	})
	@RequestMapping("/loginV1")
	public ResultVO loginV1(@RequestParam(value = "aul_value1") String phone,
							@RequestParam(value = "aul_value2") String password,
							@RequestParam(value = "aul_value3", required = false) String cid) {
		phone = phone.trim();
		password = password.trim();

		/*检查版本*/
		if (getVersion()>0) {
			//对账号密码进行RSA解密原文
			String accountDecrypt = null;
			String passDecrypt = null;
			try {
				byte[] decrypt = RSACode.decryptByPrivateKey(phone, MyKeyFactory.str_priK);
				accountDecrypt = new String(decrypt, StandardCharsets.UTF_8);

				decrypt = RSACode.decryptByPrivateKey(password, MyKeyFactory.str_priK);
				passDecrypt = new String(decrypt, StandardCharsets.UTF_8);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (StringUtils.isAnyBlank(accountDecrypt, passDecrypt)) {
				return ResultVO.FAIL("数据有误");
			}
			password = passDecrypt;
			phone = accountDecrypt;
		}

		ResultVO resultVO;
		SysUser sysUser;

		/*检查账号是否锁定*/
		String key = RedisGlobal.user_login_fail_locked + phone;
		Object obj = redisUtil.get(key);
		if (obj != null && StringUtils.isNotBlank(obj.toString())) {
			long expire = redisUtil.getExpire(key);
			throw new AccountLockedException("账号已锁定,请在" + expire + "s后重试");
		}

		/*查找账号信息*/
		sysUser = sysUserService.getByPhone(phone);
		if (sysUser == null) {
			resultVO = ResultVO.FAIL("账号不存在");
			return resultVO;
		} else {
			/*校验登录密码*/
			String realPwd = sysUser.getPassword();
			if (StringUtils.isBlank(password) || !StringUtils.equalsIgnoreCase(MD5.toMD5(password), realPwd)) {
				resultVO = ResultVO.FAIL("密码不正确");
			} else {
				User user = null;
				//查询账号默认登录的组织
				if (sysUser.getDefaultOrgId() != null) {
					user = userService.getByOrgAndPhone(sysUser.getDefaultOrgId(), phone);
				}
				//如果账号默认组织id不存在，则让用户随机登录其他组织
				if (user == null) {
					List<User> users = userService.getBySysUserId(sysUser.getId());
					if (users == null) {
						return ResultVO.FAIL("账号不存在或已注销");
					}
					for (User u : users) {
						if (u.getType() != 3 && u.getStatus() != 1) {
							user = u;
							break;
						}
					}
				}
//				User user = userService.getPreLoginAccount(sysUser.getId());
				if (user == null) {
					resultVO = ResultVO.FAIL("账号未激活或禁用，请联系组织管理员");

				} else {
					/*执行登录逻辑、检查*/
					resultVO = loginSys(user, phone, cid, password);
				}
			}
		}

		/*此处限制登录，如用户登录失败次数超过一定值，则锁定5分钟*/
		checkAccountLockStatus(resultVO, sysUser);

		return resultVO;
	}

	/**
	 * 执行登录逻辑、检查
	 *
	 * @param user          要登入的用户信息
	 * @param cid           客户端CID
	 * @param loginPassword 密码登录输入的密码
	 * @return
	 */
	private ResultVO loginSys(User user, String phone, String cid, String loginPassword) {
		UserLogger ul = createLogger(user, phone, "***", cid);

		/*用户类型检查*/
		Integer type = user.getType();
		if (type == null) {
			return ResultVO.FAIL("用户状态异常，请重新激活");
		}
		if (type == 3) {
			updateLogger(ul, 1, "登录失败", "账号未激活");
			return ResultVO.FAIL("尊敬的用户,您的账户未激活,请联系该组织管理员激活");
		}

		/*用户状态检查*/
		Integer status = user.getStatus();
		if (status == null) {
			return ResultVO.FAIL("账户状态异常，请联系组织管理员");
		}
		if (status == Global.USER_STATUS_LOCK) {
			updateLogger(ul, 1, "登录失败", "账号已停用");
			return ResultVO.FAIL("账户已被锁定，请联系组织管理员");
		}

		/*用户密码是否匹配密码策略规则*/
		boolean isMatchStrategy = true;
		String matchStrategyMsg = null;

		/*密码登录*/
		if (StringUtils.isNotBlank(loginPassword)) {
			StrategyPassword sp = strategyPasswordService.getByOrg(properties.getDefaultOrgId());
			try {
				StrategyUtil.checkStrategy(loginPassword, sp);
			} catch (Exception e) {
				isMatchStrategy = false;
				matchStrategyMsg = "密码格式不符【" + e.getMessage() + "】请及时修改密码";
			}
		}

		/*同步用户手机端个推账号*/
		String oldCid = user.getCid();
		if (StringUtils.isNotBlank(cid)) {
			if (!StringUtils.equalsIgnoreCase(cid, oldCid)) {
				user.setCid(cid);
				userService.update(user);
			}
		}

		/*组装前端需要的返回值参数*/
		try {
			//返回值
			LoginRes loginRes = createLoginRes(user, phone);

			//记录日志
			updateLogger(ul, 0, "登录成功", "");

			UserInfo userInfo = userInfoService.get(user.getId());
			if (userInfo == null) {
				throw new PrintException("账号异常，请联系管理员");
			}

			if (isMatchStrategy) {
				//符合密码策略
				return ResultVO.OK(loginRes);
			} else {
				//不符合密码策略
				return new ResultVO(Code.ERROR510.getCode(), matchStrategyMsg, loginRes);
			}

		} catch (Exception e) {
			/*登录出错*/
			log.error("登录出错 user:{} phone:{} cid:{} loginPassword:{}", user, phone, cid, loginPassword, e);
			/*记录日志*/
			updateLogger(ul, 2, "登录失败", "令牌生成错误");
			return ResultVO.FAIL("令牌生成出现错误");
		}
	}

	private static final Map<String, Integer> loginFails = new HashMap<>();

	/*
	 * 检查账号锁定状态
	 *
	 * @param loginVo 登录结果
	 * @return
	 */
	private void checkAccountLockStatus(ResultVO loginVo, SysUser sysUser) {
		int code = loginVo.getCode();
		/*登录失败记录次数*/
		if (code == Code.FAIL400.getCode()) {
			Integer times = loginFails.get(sysUser.getPhone());
			if (times == null) {
				times = 0;
			}
			times++;

			/*检查是否登录失败超过规则值*/
			if (times > Global.USER_LOGIN_TIMES) {
				loginFails.remove(sysUser.getPhone());
				String key = RedisGlobal.user_login_fail_locked + sysUser.getPhone();
				redisUtil.set(key, DateUtil.format(new Date()), RedisGlobal.user_login_fail_locked_timeout);
				throw new AccountLockedException("账号已被锁定,请在" + RedisGlobal.user_login_fail_locked_timeout + "s后重试");
			}
			loginFails.put(sysUser.getPhone(), times);
		}
		/*登录成功后,删除失败次数和缓存*/
		else {
			loginFails.remove(sysUser.getPhone());
			redisUtil.del(RedisGlobal.PHONE_VERIFI_CODE + 3 + ":" + sysUser.getPhone());
		}
	}

	@ApiOperation(value = "短信验证码登录", notes = "使用短信验证码登录", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "au2_value1", value = "登录名(手机号码)", dataType = "String", required = true),
			@ApiImplicitParam(name = "au2_value2", value = "手机验证码", dataType = "String", required = true),
			@ApiImplicitParam(name = "au2_value3", value = "客户端CID", dataType = "String"),
			@ApiImplicitParam(name = "aul_value4", value = "要登入的组织ID", dataType = "int", required = true)
	})
	@RequestMapping("/loginV2")
	public ResultVO loginV2(@RequestParam("au2_value1") String phone,
							@RequestParam("au2_value2") String code,
							@RequestParam(value = "au2_value3", required = false) String cid,
							@RequestParam("aul_value4") Integer orgId) {
		ResultVO resultVO;
		SysUser sysUser;

		if (StringUtils.isBlank(phone)) {
			return ResultVO.FAIL("手机号不能为空");
		}
		if (StringUtils.isBlank(code)) {
			return ResultVO.FAIL("验证码不能为空");
		}

		/*检查版本*/
		if (getVersion() > 0) {
			//对账号密码进行RSA解密原文
			String accountDecrypt = null;
			String passDecrypt = null;
			try {
				byte[] decrypt = RSACode.decryptByPrivateKey(code, MyKeyFactory.str_priK);
				passDecrypt = new String(decrypt, StandardCharsets.UTF_8);

				decrypt = RSACode.decryptByPrivateKey(phone, MyKeyFactory.str_priK);
				accountDecrypt = new String(decrypt, StandardCharsets.UTF_8);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (StringUtils.isAnyBlank(accountDecrypt, passDecrypt)) {
				return ResultVO.FAIL("数据有误");
			}
			code = passDecrypt;
			phone = accountDecrypt;
		}

		/*检查账号是否锁定*/
		String key = RedisGlobal.user_login_fail_locked + phone;
		Object obj = redisUtil.get(key);
		if (obj != null && StringUtils.isNotBlank(obj.toString())) {
			long expire = redisUtil.getExpire(key);
			throw new AccountLockedException("账号已锁定,请在" + expire + "s后重试");
		}

		String codeKey = RedisGlobal.PHONE_VERIFI_CODE + 3 + ":" + phone;
		String codeObj = redisUtil.getStr(codeKey);
		if (StringUtils.isBlank(codeObj) || !StringUtils.equalsIgnoreCase(codeObj, code)) {
			return ResultVO.FAIL("短信验证码不正确");
		} else {
			sysUser = sysUserService.getByPhone(phone);
			if (sysUser == null) {
				resultVO = ResultVO.FAIL("账号不存在");
			} else {
				User user = null;
				//查询账号默认登录的组织
				if (sysUser.getDefaultOrgId() != null) {
					user = userService.getByOrgAndPhone(sysUser.getDefaultOrgId(), phone);
				}
				//如果账号默认组织id不存在，则让用户随机登录其他组织
				if (user == null) {
					List<User> users = userService.getBySysUserId(sysUser.getId());
					if (users == null) {
						return ResultVO.FAIL("账号不存在或已注销");
					}
					for (User u : users) {
						if (u.getType() != 3 && u.getStatus() != 1) {
							user = u;
							break;
						}
					}
				}
				if (user == null) {
					resultVO = ResultVO.FAIL("账号未绑定公司");
				} else {
					//执行登录逻辑、检查
					resultVO = loginSys(user, phone, cid, null);
				}
			}
		}

		/*此处限制登录，如用户登录失败次数超过一定值，则锁定5分钟*/
		checkAccountLockStatus(resultVO, sysUser);

		return resultVO;
	}

	/*
	 * 创建登录日志
	 */
	private UserLogger createLogger(User user,
									Object... params) {
		UserLogger ul;
		ul = new UserLogger();
		ul.setOrgId(user.getOrgId());
		ul.setUserId(user.getId());
		ul.setUserName(user.getUserName());
		ul.setClient(isApp() ? "移动端" : "浏览器");
		ul.setUrl("/auth/user/loginV2");
		ul.setArgs("[" + Arrays.toString(params) + "]");
		HttpServletRequest request = SpringContextUtils.getRequest();
		if (request == null) {
			throw new RuntimeException();
		}
		ul.setIp(request.getRemoteAddr());
		userLoggerService.add(ul);
		return ul;
	}

	/*
	 * 更新登录日志
	 */
	private void updateLogger(UserLogger ul,
							  int status,
							  String remark,
							  String error) {
		if (ul != null && ul.getId() != null) {
			ul.setStatus(status);
			ul.setRemark(remark);
			ul.setError(error);
			userLoggerService.update(ul);
		}
	}

	/*
	 * 生成用户token
	 */
	private String generateToken(User user) {
		UserToken token = new UserToken(user.getId(), user.getOrgId(), user.getUserName());
		String jwt = null;
		try {
			jwt = JwtUtil.createJWT(token, isApp() ? RedisGlobal.USER_INFO_TOKEN_APP_TIMEOUT : RedisGlobal.USER_INFO_TOKEN_WEB_TIMEOUT);
		} catch (Exception e) {
			log.error("生成用户token出错 user:{} ", user, e);
		}

		//存用户token,后面做客户端唯一性校验使用
		if (isApp()) {
			redisUtil.set(RedisGlobal.USER_INFO_TOKEN_APP + user.getId(), jwt, RedisGlobal.USER_INFO_TOKEN_APP_TIMEOUT / 1000);
		} else {
			redisUtil.set(RedisGlobal.USER_INFO_TOKEN_WEB + user.getId(), jwt, RedisGlobal.USER_INFO_TOKEN_WEB_TIMEOUT / 1000);
		}
		return jwt;
	}

	/*
	 * 创建登录成功响应实体
	 *
	 * @param user
	 * @return
	 */
	private LoginRes createLoginRes(User user, String phone) {
		LoginRes res = new LoginRes();

		/*用户信息*/
		res.setUserName(user.getUserName());
		res.setType(user.getType());
		res.setPhone(phone);
		res.setUserId(user.getId());
		res.setHeadImg(user.getHeadImg());

		/*登录令牌*/
		res.setToken(generateToken(user));

		/*用户公司信息*/
		Org org = orgService.get(user.getOrgId());
		res.setOrgId(org.getId());
		res.setOrgName(org.getName());

		/*用户公司服务*/
		OrgServe qss = orgServeService.getByOrgAndCode(org.getId(), "QSS");
		res.setQss(qss == null ? 0 : 1);

		return res;
	}

	/*
	 * 发送手机验证码
	 * num 0:注册获取验证码  1:修改密码获取验证码   2:重置密码	3:登录获取验证码   4:注册组织   5:添加用户
	 */
	@GetMapping("/getPhoneCode")
	public ResultVO getPhoneCode(@RequestParam("phone") String phone,
								 @RequestParam(value = "num", required = false, defaultValue = "0") Integer num) {
		if (StringUtils.isNotBlank(phone)) {
			if (RigexUtil.isMobileNO(phone)) {

				if (num != null) {
					SysUser sysUser = sysUserService.getByPhone(phone);
					if (num == 0) {

						//校验该手机号码是否已注册
						if (sysUser != null) {
							return ResultVO.FAIL("手机号码已注册");
						}

					} else if (num == 2) {
						//校验该手机号码是否已注册
						if (sysUser == null) {
							return ResultVO.FAIL("手机号码未注册");
						}
					} else if (num == 1) {
						//修改密码获取验证码
						if (sysUser == null) {
							return ResultVO.FAIL("手机号码未注册");
						}
					} else if (num == 3) {
						//登录获取验证码
						if (sysUser == null) {
							return ResultVO.FAIL("手机号码未注册");
						}
					} else if (num == 4) {
						//注册组织获取验证码
						log.info("注册组织获取验证码...");
					} else if (num == 5) {
						log.info("添加用户获取验证码...");
					} else {
						return ResultVO.FAIL("未知状态码(" + num + ")");
					}
				}

				//防止短信轰炸
				String hostName = SpringContextUtils.getRequest().getHeader("X-real-ip");
				hostName = StringUtils.isBlank(hostName) ? SpringContextUtils.getRequest().getRemoteHost() : hostName;
				String hostKey = "sms:send:" + hostName;
				Set<String> hostcount = redisUtil.keys(hostKey + "*");
				if (hostcount != null && hostcount.size() > 5) {
					return ResultVO.FAIL("短信发送限制");
				}
				hostKey = hostKey + ":" + System.currentTimeMillis();
				redisUtil.set(hostKey, 0, 600);//10分钟内超过5条短信,就限制该IP发送短信


				/*发送短信数量限制,防止用户无限请求发送短信*/
				String totalKey = RedisGlobal.phone_sms_total + phone;
				Object totalObj = redisUtil.get(totalKey);
				int total = 0;
				if (totalObj != null && StringUtils.isNotBlank(totalObj.toString())) {
					try {
						total = Integer.parseInt(totalObj.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (total >= properties.getAliyunSms().getVerifyTotal()) {
						return ResultVO.FAIL("手机号已达到发送限制！(" + total + ")");
					}
				}

				/*发送短信间隔限制，防止用户无限发送短信*/
				String limitKey = RedisGlobal.PHONE_SMS_LIMIT + phone;
				Object limitObj = redisUtil.get(limitKey);
				if (limitObj != null && StringUtils.isNotBlank(limitObj.toString())) {
					long expire = redisUtil.getExpire(limitKey);
					return ResultVO.FAIL("短信请求过于频繁，请稍后重试!(剩余" + expire + "s)");
				}

				//num 0:注册获取验证码  1:修改密码获取验证码   2:重置密码	3:登录获取验证码
				String key = RedisGlobal.PHONE_VERIFI_CODE + num + ":" + phone;
				Object obj_code = redisUtil.get(key);
				if (obj_code == null || StringUtils.isBlank(obj_code.toString())) {
					//发送短信验证码
					int randomInt = cn.hutool.core.util.RandomUtil.randomInt(9999, 99999);
					String phoneCode = String.valueOf(randomInt);
					log.info("==========>短信验证码:" + phoneCode);
					SendCode sendCode;
					try {
						/*短信验证码记录到redis中*/
						redisUtil.set(key, phoneCode, RedisGlobal.PHONE_VERIFI_CODE_TIMEOUT);
						/*发送短信间隔限制，防止用户无限发送短信*/
						redisUtil.set(limitKey, phoneCode, RedisGlobal.PHONE_SMS_LIMIT_TIMEOUT);

						sendCode = SmsUitls.SendPhoneVerifyCode(phone, phoneCode);

						/*发送成功后，记录发送数量*/
						total++;
						int timeout = (int) ((DateUtil.getTime(23, 59, 59) - System.currentTimeMillis()) / 1000);
						redisUtil.set(totalKey, total, timeout);

					} catch (Exception e) {
						e.printStackTrace();
						return ResultVO.FAIL("发送手机验证码出现错误");
					}

					if (sendCode == null) {
						return ResultVO.FAIL("短信发送失败");
					}

					if ("OK".equalsIgnoreCase(sendCode.getCode())) {
						return ResultVO.OK("验证码已发送至你的手机，请注意查收", 1);
					} else {
						return ResultVO.FAIL(sendCode.getMsg());
					}
				} else {
					Integer intTime = null;
					Double douTime = null;
					try {
						long expire = redisUtil.getExpire(key);
						douTime = Math.ceil(expire / 60.0);
						intTime = (int) Math.ceil(expire / 60.0);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return ResultVO.FAIL("验证码重复发送,请" + (intTime == null ? douTime : intTime) + "分钟后重试");
				}

			} else {
				return ResultVO.FAIL("手机号格式不正确");
			}
		} else {
			return ResultVO.FAIL("手机号码不能为空");
		}
	}


	/**
	 * 查询登录用户所属公司的审计人列表
	 */
	@GetMapping("/getAuditors")
	public ResultVO getAuditors() {
		UserToken token = getToken();
		boolean page = setPage();
		List<User> auditors = userService.getByOrgAndPerms(token.getOrgId(), "auditProcessing");
		if (isApp()) {
			List<Map<String, Object>> res = CommonUtils.getAddressList(auditors, "userName");
			return ResultVO.OK(res);
		} else {
			return ResultVO.Page(auditors, page);
		}
	}

	/**
	 * 查询该印章允许录入、删除的用户列表
	 *
	 * @param departmentId 部门ID
	 * @param signetId     印章ID
	 * @param keyword      搜索关键词(手机号、姓名)
	 * @return
	 */
	@GetMapping("/getFingerUserBySignet")
	public ResultVO getFingerUserBySignet(@RequestParam(value = "departmentId", required = false) Integer departmentId,
										  @RequestParam("signetId") Integer signetId,
										  @RequestParam(value = "keyword", required = false) String keyword,
										  @RequestParam(value = "page", required = false, defaultValue = "true") Boolean page,
										  @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
										  @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
		/*
		 * 参数校验：设备
		 */
		if (signetId == null) {
			return ResultVO.FAIL("请求设备参数有误");
		}
		UserInfo userInfo = getUserInfo();
		Signet signet = signetService.get(signetId);
		if (signet == null || signet.getOrgId() != userInfo.getOrgId().intValue()) {
			return ResultVO.FAIL("设备不存在");
		}

		/*
		 * 参数校验：组织ID
		 */
		if (departmentId != null) {
			Department department = departmentService.get(departmentId);
			if (department == null || department.getOrgId() != userInfo.getOrgId().intValue()) {
				return ResultVO.FAIL("组织搜索有误");
			}
			Integer signetDepartmentId = signet.getDepartmentId();
			if (signetDepartmentId == null) {
				return ResultVO.FAIL("设备所属组织未配置，请联系负责人更新");
			}
		}

		if (departmentId == null) {
			Integer signetDepartmentId = signet.getDepartmentId();
			Department company = departmentService.getCompanyByChildrenId(signetDepartmentId);
			if (company == null) {
				departmentId = signetDepartmentId;
			} else {
				departmentId = company.getId();
			}
		}

		/*
		 * 查询组织员工列表
		 */
		List<Integer> childrenIds = departmentService.getChildrenIdsByOrgAndParentAndType(userInfo.getOrgId(), departmentId, 0);
		childrenIds.add(departmentId);
		List<FingerEntity> fingerEntities = userService.getUsersByOrgAndDepartmentAndUserName(userInfo.getOrgId(), childrenIds, keyword);

		/*
		 * 查询每个员工是否拥有指纹
		 */
		if (fingerEntities != null && fingerEntities.size() > 0) {
			for (FingerEntity fingerEntity : fingerEntities) {
				int userId = fingerEntity.getUserId();
				Finger finger = fingerService.getByOrgAndDeviceAndUser(userInfo.getOrgId(), signetId, userId);
				if (finger != null) {
					fingerEntity.setFingerCreateDate(finger.getCreateDate());
					fingerEntity.setFinger(true);
				} else {
					fingerEntity.setFinger(false);
				}
			}
		}
		return ResultVO.Page(fingerEntities, page);
	}


	@ApiOperation(value = "修改个人信息", notes = "修改个人信息", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "phone", value = "手机号码", dataType = "String", required = true),
			@ApiImplicitParam(name = "userName", value = "用户名称", dataType = "String", required = true),
			@ApiImplicitParam(name = "remark", value = "用户备注", dataType = "String"),
			@ApiImplicitParam(name = "headImg", value = "头像文件ID", dataType = "String")
	})
	@WebLogger("修改个人信息")
	@PostMapping("/myUpdate")
	public ResultVO myUpdate(@RequestParam(value = "phone", required = false) String phone,
							 @RequestParam(value = "userName", required = false) String userName,
							 @RequestParam(value = "remark", required = false) String remark,
							 @RequestParam(value = "headImg", required = false) String fileUUID) {

		UserInfo userInfo = getUserInfo();
		/*
		 * 检测手机号
		 */
		if (StringUtils.isBlank(phone)) {
			return ResultVO.FAIL("手机号不能为空");
		}
		if (!RigexUtil.isMobileNO(phone)) {
			return ResultVO.FAIL("手机号格式不正确");
		}
		if (!phone.equalsIgnoreCase(userInfo.getPhone())) {
			SysUser sysUser = sysUserService.getByPhone(phone);
			if (sysUser != null && sysUser.getId().intValue() != userInfo.getSysUserId()) {
				return ResultVO.FAIL("手机号已注册");
			}
		}

		/*
		 * 用户名校验
		 */
		if (StringUtils.isBlank(userName)) {
			return ResultVO.FAIL("用户名不能为空");
		}
		if (EmojiFilter.containsEmoji(userName)) {
			return ResultVO.FAIL("用户名不能包含特殊字符");
		}

		/*
		 * 用户备注校验
		 */
		if (EmojiFilter.containsEmoji(remark)) {
			return ResultVO.FAIL("描述信息不能包含特殊字符");
		}

		/*
		 * 头像校验
		 */
		if (StringUtils.isNotBlank(fileUUID)) {
			FileInfo fileInfo = fileInfoService.get(fileUUID);
			if (fileInfo == null || fileInfo.getId() == null) {
				return ResultVO.FAIL("头像不存在");
			}
		}

		userService.updateOwner(phone, userName, remark, fileUUID);

		User user = userService.get(userInfo.getId());
		return loginSys(user, phone, user.getCid(), null);
	}


	//用户重置密码
	@PostMapping("/resetPwd")
	public ResultVO resetPwd(@RequestParam("phone") String phone,
							 @RequestParam("vercode") String vercode) {
		if (StringUtils.isBlank(phone)) {
			return ResultVO.FAIL("手机号码不能为空");
		}

		//验证码
		//num 0:注册获取验证码  1:修改密码获取验证码   2:重置密码	3:登录获取验证码
		String key = RedisGlobal.PHONE_VERIFI_CODE + 2 + ":" + phone;
		Object obj = redisUtil.get(key);
		if (obj == null || StringUtils.isBlank(obj.toString()) || !obj.toString().equalsIgnoreCase(vercode)) {
			return ResultVO.FAIL("验证码不正确");
		}

		SysUser sysUser = sysUserService.getByPhone(phone);
		if (sysUser != null) {
			sysUser.setPassword(CommonUtils.properties.getDefaultPwd());

			sysUserService.update(sysUser);

			redisUtil.del(key);

			return ResultVO.OK("密码重置成功,初始化密码:123456");
		}
		return ResultVO.FAIL("很抱歉,该用户不存在或已被删除");
	}

	//获取用户拥有的快捷方式列表
	@GetMapping("/getShortcutAll")
	public ResultVO getShortcutAll() {
		UserInfo userInfo = getUserInfo();
		List<Perms> perms = permService.getQuickLinkByUser(userInfo.getId());
		return ResultVO.OK(perms);
	}

	//设置快捷方式
	@PostMapping("/updateShortcut")
	@Transactional
	public ResultVO updateShortcut(@RequestParam(required = false) String[] shortcuts) {
		UserInfo userInfo = getUserInfo();
		//清空用户的图标
		shortcutService.deleteAllByUserId(userInfo.getId());

		if (shortcuts != null && shortcuts.length > 0) {
			for (String shortcut : shortcuts) {
				Integer permsId;
				try {
					permsId = Integer.parseInt(shortcut);
				} catch (NumberFormatException e) {
					e.printStackTrace();
					continue;
				}
				Perms perms = permService.get(permsId);
				if (perms != null) {
					Shortcut s = new Shortcut();
					s.setName(perms.getLabel());
					s.setPermsId(perms.getId());
					s.setUserId(userInfo.getId());
					s.setOrgId(userInfo.getOrgId());
					shortcutService.add(s);
				}
			}

		}

		userInfoService.del(userInfo.getId());
		return ResultVO.OK();
	}

	@GetMapping("/getShortcuts")
	public ResultVO getShortcuts() {
		UserInfo userInfo = getUserInfo();
		List<Perms> quickLinks = permService.getQuickLinkByUserSelected(userInfo.getId());
		return ResultVO.OK(quickLinks);
	}

	//App端图标展示
	@GetMapping("/getPhoneIcons")
	public ResultVO getPhoneIcons() {
		UserInfo userInfo = getUserInfo();
		List<PhoneIcon> one = phoneIconService.getPhoneIconByGroupId(1);//手机端'印章管理'UI界面的图标
		List<PhoneIcon> two = phoneIconService.getPhoneIconByGroupId(2);//手机端'申请单管理'UI界面的图标
		List<UserPhoneIcon> all = userPhoneIconService.getAll(userInfo.getId());//用户拥有的所有快捷方式图标
		Set<PhoneIcon> three = new LinkedHashSet<>();//用户已选择过的快捷方式图标
		for (UserPhoneIcon userPhoneIcon : all) {
			PhoneIcon phoneIcon = phoneIconService.get(userPhoneIcon.getPhoneIconId());
			three.add(phoneIcon);
			//判断集合内是否已被选，已被选就设置type=1
			for (int m = 0; m < one.size(); m++) {
				if (one.get(m).getId().intValue() == phoneIcon.getId()) {
					phoneIcon.setType(1);
					one.set(m, phoneIcon);
				}
			}
			for (int m = 0; m < two.size(); m++) {
				if (two.get(m).getId().intValue() == phoneIcon.getId()) {
					phoneIcon.setType(1);
					two.set(m, phoneIcon);
				}
			}
		}
		List<Map<String, Object>> SysCoin = new ArrayList<>();
		HashMap<String, Object> map1 = new HashMap<>();
		map1.put("name", "印章管理");
		map1.put("value", one);
		SysCoin.add(map1);
		HashMap<String, Object> map2 = new HashMap<>();
		map2.put("name", "申请单管理");
		map2.put("value", two);
		SysCoin.add(map2);
		//new一个map容器
		HashMap<String, Object> container = new HashMap<>();
		container.put("MyCoin", three);
		container.put("SysCoin", SysCoin);

		return ResultVO.OK(container);
	}

	//用户在手机端添加的图片快捷入口
	@GetMapping("/getPhoneShortcuts")
	public ResultVO getPhoneShortcuts() {
		UserInfo userInfo = getUserInfo();
		List<UserPhoneIcon> all = userPhoneIconService.getAll(userInfo.getId());
		List<PhoneIcon> list = new ArrayList<>();
		if (all != null && all.size() > 0) {
			for (UserPhoneIcon userPhoneIcon : all) {
				PhoneIcon phoneIcon = phoneIconService.get(userPhoneIcon.getPhoneIconId());
				list.add(phoneIcon);
			}
		} else {
			List<PhoneIcon> icons = phoneIconService.getAll();
			for (PhoneIcon icon : icons) {
				if ("我的申请".equalsIgnoreCase(icon.getName())) {
					list.add(icon);
				}
				if ("审批处理".equalsIgnoreCase(icon.getName())) {
					list.add(icon);
				}
				if ("授权处理".equalsIgnoreCase(icon.getName())) {
					list.add(icon);
				}
				if ("审计处理".equalsIgnoreCase(icon.getName())) {
					list.add(icon);
				}

			}
		}
		return ResultVO.OK(list);
	}

	@RequestMapping("/updatePhoneShortcuts")
	@Transactional
	public ResultVO updatePhoneShortcuts(@RequestParam(required = false) Integer[] phoneIcons) {
		UserInfo userInfo = getUserInfo();
		//删除用户的所有图标
		userPhoneIconService.deleteAllByUserId(userInfo.getId());
		if (phoneIcons != null && phoneIcons.length > 0) {
			//将新增的图标注册到数据库
			for (Integer phoneIconId : phoneIcons) {
				PhoneIcon phoneIcon = phoneIconService.get(phoneIconId);//获取传入的图标模板
				if (phoneIcon != null) {
					UserPhoneIcon userPhoneIcon = new UserPhoneIcon();
					userPhoneIcon.setUserId(userInfo.getId());
					userPhoneIcon.setPhoneIconId(phoneIcon.getId());
					userPhoneIconService.add(userPhoneIcon);
				}
			}
		}
		return ResultVO.OK();
	}

	@WebLogger("添加员工")
	@ApiOperation(value = "添加员工", notes = "添加员工", httpMethod = "POST")
	@PostMapping("/addEmployee")
	public ResultVO addEmployee(@RequestParam("userName") String userName,
								@RequestParam("phone") String phone,
								@RequestParam("roleIds") List<Integer> roleIds,
								@RequestParam(value = "remark", required = false) String remark,
								@RequestParam("code") String code,
								@RequestParam(value = "status", required = false) Integer isUse,
								@RequestParam("departmentIds") List<Integer> departmentIds) {
		UserInfo userInfo = getUserInfo();
		//校验组织
		if (departmentIds == null || departmentIds.isEmpty()) {
			return ResultVO.FAIL("员工组织不能为空");
		}
		for (Integer departmentId : departmentIds) {
			Department department = departmentService.get(departmentId);
			if (department == null || !CommonUtils.isEquals(userInfo.getOrgId(), department.getOrgId())) {
				return ResultVO.FAIL("组织选择有误");
			}
		}
		//校验角色
		if (roleIds == null || roleIds.size() == 0) {
			return ResultVO.FAIL("角色不能为空");
		}
		for (Integer roleId : roleIds) {
			Role role = roleService.get(roleId);
			if (role == null || !CommonUtils.isEquals(role.getOrgId(), userInfo.getOrgId())) {
				return ResultVO.FAIL("角色选择有误");
			}
		}

		//校验手机号+姓名
		if (!RigexUtil.isMobileNO(phone)) {
			return ResultVO.FAIL("手机号格式不正确");
		}
		if (StringUtils.isBlank(userName)) {
			return ResultVO.FAIL("用户姓名不能为空");
		}
		userName = userName.trim();

		//查询用户信息
		SysUser sysUser = sysUserService.getByPhone(phone);
		if (sysUser != null) {
			//账号已存在的情况下，检查这个账号对应的用户信息，在当前登录用户的组织中是否存在
			//如果存在，不允许添加
			//如果不存在，允许添加
			Integer orgId = userInfo.getOrgId();
			List<User> users = userService.getByOrg(orgId);
			if (users != null && !users.isEmpty()) {
				for (User user : users) {
					if (Objects.equals(user.getSysUserId(), sysUser.getId())) {
						return ResultVO.FAIL("员工已存在");
					}
				}
			}
//			//查询用户关联的集团信息
//			List<Integer> orgIds = userService.getOrgIdsBySysUserId(sysUser.getId());
//			if (orgIds != null && orgIds.contains(userInfo.getOrgId())) {
//				return ResultVO.FAIL("该员工已存在");
//			}
		}

		//校验验证码
		if (StringUtils.isBlank(code)) {
			return ResultVO.FAIL("验证码不能为空");
		}
		String key = RedisGlobal.PHONE_VERIFI_CODE + 5 + ":" + phone;
		String localCode = redisUtil.getStr(key);
		if (!StringUtils.equalsIgnoreCase(code, localCode)) {
			return ResultVO.FAIL("验证码不正确");
		}
		redisUtil.del(key);

		userService.addEmployee(sysUser, userName, phone, remark, isUse == 0, roleIds, departmentIds);

		return ResultVO.OK("添加成功");
	}


	@ApiOperation(value = "查询可作为集团(企业、部门)负责人的员工列表", notes = "查询可作为集团(企业、部门)负责人的员工列表", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "集团(企业、部门)ID", dataType = "int"),
			@ApiImplicitParam(name = "type", value = "集团(企业、部门)类型 0:部门  1:公司  2:集团", dataType = "int", required = true)
	})
	@GetMapping("/getEmployeeForManagerByDepartmentId")
	public ResultVO getEmployeeForManagerByDepartmentId(@RequestParam("type") Integer type) {
		if (type == null) {
			return ResultVO.FAIL("组织类型有误");
		}

		UserInfo userInfo = getUserInfo();
		List<User> employees = null;
		if (userInfo.isOwner()) {
			//集团负责人，查询所有员工列表
			employees = userService.getByOrg(userInfo.getOrgId());
		} else {
			//非集团负责人，查询管理的部门员工
			List<Integer> departmentIds = userInfo.getDepartmentIds();
			if (departmentIds != null && departmentIds.size() > 0) {
				employees = userService.getEmployeeForManagerByDepartments(userInfo.getOrgId(), departmentIds);
			}
		}

		return ResultVO.OK(employees);
	}


//	@ApiOperation(value = "查询组织下的员工列表", notes = "查询组织下的员工列表", httpMethod = "POST")
//	@ApiImplicitParams({
//			@ApiImplicitParam(name = "keyword", value = "员工名称、手机号关键词", dataType = "int"),
//			@ApiImplicitParam(name = "departmentId", value = "组织、企业ID", dataType = "int"),
//			@ApiImplicitParam(name = "pageNum", value = "当前页(默认:1)", dataType = "int"),
//			@ApiImplicitParam(name = "pageSize", value = "每页数(默认:10)", dataType = "int"),
//			@ApiImplicitParam(name = "page", value = "是否分页", dataType = "boolean", defaultValue = "true")
//	})
//	@GetMapping("/searchByOrgAndKeywords")
//	public ResultVO searchByOrgAndKeywords(@RequestParam(value = "keyword", required = false) String keyword,
//										   @RequestParam(value = "departmentId", required = false) Integer departmentId,
//										   @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
//										   @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
//										   @RequestParam(value = "page", required = false, defaultValue = "true") boolean isPage) {
//		/*
//		 * 参数校验：员工关键词
//		 */
//		if (EmojiFilter.containsEmoji(keyword)) {
//			return ResultVO.FAIL("不能搜索特殊字符");
//		}
//		/*
//		 * 参数校验：组织ID
//		 */
//
//		UserInfo userInfo = getUserInfo();
//		if (departmentId != null) {
//			Department department = departmentService.get(departmentId);
//			if (department == null || department.getOrgId().intValue() != userInfo.getOrgId()) {
//				return ResultVO.FAIL("搜索的组织不存在");
//			}
//		}
//
//		/*
//		 * 设置查询组织列表
//		 */
//		List<Integer> searchIds = null;
//		if (departmentId != null) {
//			searchIds = departmentService.getChildrenIdsByOrgAndParentAndType(userInfo.getOrgId(), departmentId, null);
//			searchIds.add(departmentId);
//		}
//
//		List<Employee> employess = userService.getEmployeesByKeyword(userInfo.getOrgId(), searchIds, keyword);
//
//		return ResultVO.Page(employess, isPage);
//	}

	@ApiOperation(value = "查询组织下的员工列表", notes = "查询组织下的员工列表", httpMethod = "POST")
	@GetMapping("/searchByOrgAndKeywords")
	public ResultVO searchByOrgAndKeywords(@RequestParam(required = false) String keyword,
										   @RequestParam(required = false) Integer departmentId,
										   @RequestParam(required = false, defaultValue = "1") Integer pageNum,
										   @RequestParam(required = false, defaultValue = "10") Integer pageSize,
										   @RequestParam(value = "page", required = false, defaultValue = "true") boolean isPage) {
		// 参数校验：员工关键词
		if (EmojiFilter.containsEmoji(keyword)) {
			return ResultVO.FAIL("不能搜索特殊字符");
		}

		// 参数校验：组织ID
		UserInfo userInfo = getUserInfo();
		if (departmentId != null) {
			//搜索的组织必须存在 & 并且属于同一个集团内
			Department department = departmentService.get(departmentId);
			if (department == null || department.getOrgId().intValue() != userInfo.getOrgId()) {
				return ResultVO.FAIL("搜索的组织不存在");
			}
			//搜索的组织必须在该用户可见组织列表中
			List<Integer> visualDepartmentIds = userInfo.getVisualDepartmentIds();
			if (visualDepartmentIds == null || visualDepartmentIds.isEmpty() || !visualDepartmentIds.contains(departmentId)) {
				return ResultVO.FAIL("无权限");
			}
		}

		// 设置查询组织列表
		List<Integer> searchIds;
		if (departmentId != null) {
			// 查询该组织及子孙组织ID列表
			searchIds = departmentService.getChildrenIdsByOrgAndParentAndType(userInfo.getOrgId(), departmentId, null);
			// 过滤该用户不可见的组织ID
			if (searchIds != null && !searchIds.isEmpty()) {
				searchIds.removeIf(nextDepartmentId -> !userInfo.getVisualDepartmentIds().contains(nextDepartmentId));
			}
		} else {
			searchIds = userInfo.getVisualDepartmentIds();//查询所有用户可见组织列表
		}

		// 查询数据库
		if (isPage) {
			PageHelper.startPage(pageNum, pageSize);
		}
		List<Employee> employees = userService.getEmployeesByKeyword(userInfo.getOrgId(), searchIds, keyword);

		// 组装前端需要的返回值
		if (employees != null && employees.size() > 0) {
			for (Employee employee : employees) {
				Integer userId = employee.getId();
				List<Department> departments = departmentService.getByOrgAndUser(userInfo.getOrgId(), userId);
				if (departments != null && departments.size() > 0) {
					StringBuilder sbId = new StringBuilder();
					StringBuilder sbName = new StringBuilder();
					for (int j = 0; j < departments.size(); j++) {
						Department department = departments.get(j);
						sbId.append(department.getId());
						sbName.append(department.getName());
						if (j != departments.size() - 1) {
							sbId.append(",");
							sbName.append(",");
						}
					}
					if (StringUtils.isNotBlank(sbId.toString())) {
						employee.setDepartmentIds(sbId.toString());
					}
					if (StringUtils.isNotBlank(sbName.toString())) {
						employee.setDepartmentNames(sbName.toString());
					}
				}
			}
		}

		return ResultVO.Page(employees, isPage);
	}

	@ApiOperation(value = "删除员工", notes = "删除员工", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userId", value = "员工ID", dataType = "int")
	})
	@WebLogger("删除员工")
	@PostMapping("/del")
	public ResultVO del(@RequestParam("userId") Integer userId) {
		UserInfo userInfo = getUserInfo();

		// 校验员工有效性
		User user = userService.get(userId);
		if (user == null) {
			return ResultVO.FAIL("员工不存在");
		}
		if (user.getId().intValue() == userInfo.getId()) {
			return ResultVO.FAIL("无法删除");
		}
		if (user.getOrgId().intValue() != userInfo.getOrgId()) {
			return ResultVO.FAIL("无权限操作");
		}

		// 集团负责人不能删除
		Integer orgId = user.getOrgId();
		Department root = departmentService.getRootByOrg(orgId);
		if (CommonUtils.isEquals(root.getManagerUserId(), userId)) {
			return ResultVO.FAIL("集团负责人不允许删除");
		}

		//检查员工是否拥有指纹
		List<Finger> fingers = fingerService.getByUser(userId);
		if (fingers != null && !fingers.isEmpty()) {
			return ResultVO.FAIL("请先删除该员工指纹信息");
		}

		// 校验员工权限
		List<Department> departments = departmentService.getByOrgAndManager(user.getOrgId(), user.getId());
		if (departments != null && departments.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (Department department : departments) {
				String name = department.getName();
				sb.append(name).append(",");
			}
			if (StringUtils.isNotBlank(sb.toString())) {
				String noticeMessage = sb.toString().substring(0, sb.toString().length() - 1);
				return ResultVO.FAIL("该员工正在管理组织【" + noticeMessage + "】,请先移除该员工管理职位");
			}
		}

		userService.delEmployee(user);

		return ResultVO.OK("删除成功");

	}


	/**
	 * 修改员工信息
	 */
	@ApiOperation(value = "更新员工信息", notes = "更新员工信息", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "员工ID", dataType = "int", required = true),
			@ApiImplicitParam(name = "userName", value = "真实姓名", dataType = "String", required = true),
			@ApiImplicitParam(name = "phone", value = "手机号", dataType = "String", required = true),
			@ApiImplicitParam(name = "remark", value = "员工简介", dataType = "String"),
			@ApiImplicitParam(name = "status", value = "是否可用 0:正常使用  1:禁用", dataType = "boolean", defaultValue = "true"),
			@ApiImplicitParam(name = "roleIds", value = "角色ID列表", dataType = "Integer[]", required = true),
			@ApiImplicitParam(name = "departmentIds", value = "组织列表", dataType = "Integer[]", required = true)
	})
	@WebLogger("修改员工信息")
	@PostMapping("/update")
	public ResultVO update(@RequestParam("id") Integer id,
						   @RequestParam("userName") String userName,
						   @RequestParam("phone") String phone,
						   @RequestParam(value = "code", required = false) String code,
						   @RequestParam(value = "remark", required = false) String remark,
						   @RequestParam(value = "status", required = false, defaultValue = "0") Integer status,
						   @RequestParam("roleIds") List<Integer> roleIds,
						   @RequestParam("departmentIds") List<Integer> departmentIds) {
		UserInfo userInfo = getUserInfo();
		User user = userService.get(id);
		if (user == null) {
			return ResultVO.FAIL("员工不存在");
		}
		if (!CommonUtils.isEquals(user.getOrgId(), userInfo.getOrgId())) {
			return ResultVO.FAIL("无权限操作");
		}
		//当手机发生变更时，需要向变更后的手机号发送验证码
		Integer sysUserId = user.getSysUserId();
		SysUser sUser = sysUserService.get(sysUserId);
		if (!StringUtils.equals(sUser.getPhone(), phone)) {

			/*校验手机号*/
			if (StringUtils.isBlank(phone)) {
				return ResultVO.FAIL("手机号不能为空");
			}
			if (!RigexUtil.isMobileNO(phone)) {
				return ResultVO.FAIL("手机号格式不正确");
			}
			SysUser sysUser = sysUserService.getByPhone(phone);
			if (sysUser != null && !CommonUtils.isEquals(user.getSysUserId(), sysUser.getId())) {
				return ResultVO.FAIL("该手机号已注册");
			}

			//校验验证码
			if (StringUtils.isBlank(code)) {
				return ResultVO.FAIL("验证码不能为空");
			}
			String key = RedisGlobal.PHONE_VERIFI_CODE + 5 + ":" + phone;
			String localCode = redisUtil.getStr(key);
			if (!StringUtils.equalsIgnoreCase(code, localCode)) {
				return ResultVO.FAIL("验证码不正确");
			}
			redisUtil.del(key);
		}
		/*属主账号不允许禁用*/
		Integer orgId = user.getOrgId();
		Org org = orgService.get(orgId);
		Integer managerUserId = org.getManagerUserId();
		if (status != null && status == 1 && managerUserId != null && managerUserId.intValue() == user.getId()) {
			return ResultVO.FAIL("属主账号不允许禁用");
		}

		/*校验用户名*/
		if (StringUtils.isBlank(userName)) {
			return ResultVO.FAIL("员工名称不能为空");
		}
		if (EmojiFilter.containsEmoji(userName)) {
			return ResultVO.FAIL("员工名称不能包含特殊字符");
		}

		/*校验手机号*/
		if (StringUtils.isBlank(phone)) {
			return ResultVO.FAIL("手机号不能为空");
		}
		if (!RigexUtil.isMobileNO(phone)) {
			return ResultVO.FAIL("手机号格式不正确");
		}
		SysUser sysUser = sysUserService.getByPhone(phone);
		if (sysUser != null && !CommonUtils.isEquals(user.getSysUserId(), sysUser.getId())) {
			return ResultVO.FAIL("该手机号已注册");
		}

		/*校验角色列表*/
		if (roleIds == null || roleIds.isEmpty()) {
			return ResultVO.FAIL("角色不能为空");
		}
		for (Integer roleId : roleIds) {
			Role role = roleService.get(roleId);
			if (role == null || role.getOrgId().intValue() != userInfo.getOrgId()) {
				return ResultVO.FAIL("角色有误");
			}
		}

		/*校验组织列表*/
		if (departmentIds == null || departmentIds.isEmpty()) {
			return ResultVO.FAIL("员工所属组织不能为空");
		}

		/*该员工是否可解绑原组织(如果该员工是某组织负责人，无法直接解绑该组织)*/
		List<Department> departments = departmentService.getByOrgAndManager(user.getOrgId(), user.getId());
		if (departments != null && departments.size() > 0) {
			for (Department department : departments) {
				if (!departmentIds.contains(department.getId())) {
					return ResultVO.FAIL("该员工正在管理组织【" + department.getName() + "】,请先移除该员工负责人权限");
				}
			}
		}

		userService.updateEmployee(user, userName, remark, status, phone, roleIds, departmentIds);

		return ResultVO.OK("更新成功");
	}

	@ApiOperation(value = "切换组织", notes = "切换组织", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "orgId", value = "要切换的组织ID", dataType = "int", required = true)
	})
	@WebLogger("切换组织")
	@PostMapping("/changeOrg")
	public ResultVO changeOrg(@RequestParam("orgId") Integer orgId) {

		//校验公司ID
		Org org = orgService.get(orgId);
		if (org == null) {
			return ResultVO.FAIL("该组织不存在");
		}

		UserInfo userInfo = getUserInfo();

		//校验操作人权限
		SysUser sysUser = sysUserService.get(userInfo.getSysUserId());//查询登录用户的账号
		if (sysUser == null) {
			return ResultVO.FAIL("该账号不存在");
		}

		User user = userService.getBySysUserAndOrg(sysUser.getId(), orgId);
		if (user == null) {
			return ResultVO.FAIL("信息有误，无法切换");
		}

		//更新账号默认公司
		sysUser.setDefaultOrgId(orgId);
		sysUserService.update(sysUser);

		//将原账号cid更新到新账号中
		try {
			Integer orgUserId = userInfo.getId();
			User orgUser = userService.get(orgUserId);
			if (orgUser != null) {
				String cid = orgUser.getCid();
				if (StringUtils.isNotBlank(cid) && !StringUtils.equals(cid, user.getCid())) {
					user.setCid(cid);
					userService.update(user);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		//组装返回值
		LoginRes loginRes = createLoginRes(user, sysUser.getPhone());

//		/*
//		 * 刷新缓存
//		 */
//		Object obj = redisUtil.get(RedisGlobal.USER_INFO + user.getId());
//		if (obj == null) {
//			userInfoService.synch_refresh(user.getId());
//		} else {
//			userInfoService.async_refresh(user.getId());
//		}

		return ResultVO.OK(loginRes);
	}

	@ApiOperation(value = "查询印章所在公司的审批人列表、组织架构列表 用于申请用章", notes = "查询印章所在公司的查询审批人列表、组织架构列表 用于申请用章", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "departmentId", value = "组织ID", dataType = "int"),
			@ApiImplicitParam(name = "keyword", value = "审批人名称", dataType = "string"),
			@ApiImplicitParam(name = "deviceId", value = "申请的印章ID", dataType = "int"),
	})
	@GetMapping("/getOrganizationalToApplicationForm")
	public ResultVO getOrganizationalToApplicationForm
			(@RequestParam(value = "departmentId", required = false) Integer departmentId,
			 @RequestParam(value = "keyword", required = false) String keyword,
			 @RequestParam("deviceId") Integer deviceId) {
		UserInfo userInfo = getUserInfo();
		if (StringUtils.isNotBlank(keyword)) {
			keyword = keyword.trim();
		}

		/*
		 * 参数校验:印章
		 */
		if (deviceId == null) {
			return ResultVO.FAIL("请先选择印章");
		}
		Signet signet = signetService.get(deviceId);
		if (signet == null
				|| signet.getId() == null
				|| signet.getOrgId().intValue() != userInfo.getOrgId()) {
			return ResultVO.FAIL("选择的印章有误");
		}
//		List<Integer> visualDepartmentIds = departmentService.getDepartmentIdsByAppSignet(userInfo.getId());
		List<Integer> visualDepartmentIds = userInfo.getVisualDepartmentIds();
		Integer signetDepartmentId = signet.getDepartmentId();
		if (signetDepartmentId != null && !visualDepartmentIds.contains(signetDepartmentId)) {
			return ResultVO.FAIL("无权限申请该印章");
		}

		/*
		 * 参数校验：组织ID
		 */
		Department company = departmentService.getCompanyByChildrenId(signetDepartmentId);//设备所在公司
		List<Integer> childrenIds = departmentService.getChildrenIdsByOrgAndParentAndType(company.getOrgId(), company.getId(), 0);
		childrenIds.add(company.getId());

		if (departmentId != null) {
			Department department = departmentService.get(departmentId);
			if (department == null || department.getOrgId().intValue() != userInfo.getOrgId()) {
				return ResultVO.FAIL("搜索的组织不存在");
			}
			if (childrenIds.isEmpty()
					|| !childrenIds.contains(departmentId)) {
				return ResultVO.FAIL("搜索的组织有误");
			}
		}

		/*
		 * 返回值对象
		 */
		Map<String, Object> res = new HashMap<>(2);
		List<OrganizationalEntity> departments = null;
		List<OrganizationalEntity> employees;

		/*
		 * 查询该印章所属公司的组织列表
		 */
		if (StringUtils.isBlank(keyword)) {
			Integer tempId = (departmentId == null ? company.getId() : departmentId);
			departments = departmentService.getOrganizationalByOrgAndParentToApplicationSignet(signet.getOrgId(), tempId, 0);
			employees = userService.getManagersByOrgAndDepartmentsAndKeyword(userInfo.getOrgId(), tempId, childrenIds, keyword);
		} else {
			/*
			 * 查询员工信息
			 */
			if (departmentId != null) {
				employees = userService.getManagersByOrgAndDepartmentsAndKeyword(userInfo.getOrgId(), departmentId, childrenIds, keyword);
			} else {
				employees = userService.getManagersByOrgAndParentAndKeywordToAddFlow(userInfo.getOrgId(), childrenIds, keyword);
			}
		}

		if (departments != null && departments.size() > 0) {
			res.put("departments", departments);
		}
		if (employees != null && employees.size() > 0) {
			res.put("employees", employees);
		}

		return ResultVO.OK(res);
	}


	@ApiOperation(value = "查询组织审批人列表用于添加审批流程", notes = "查询组织审批人列表用于添加审批流程", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "departmentId", value = "组织ID", dataType = "int"),
			@ApiImplicitParam(name = "keyword", value = "审批人名称", dataType = "string")
	})
	@RequestMapping("/getOrganizationalToAddFlow")
	public ResultVO getOrganizationalToAddFlow(@RequestParam(required = false) Integer departmentId, @RequestParam(required = false) String keyword) {
		UserInfo userInfo = getUserInfo();

		// 参数校验：关键词
		if (EmojiFilter.containsEmoji(keyword)) {
			return ResultVO.FAIL("名称不支持特殊字符");
		}

		// 参数校验：部门ID
		if (departmentId != null) {
			Department department = departmentService.get(departmentId);
			if (department == null || department.getOrgId().intValue() != userInfo.getOrgId()) {
				return ResultVO.FAIL("该组织不存在");
			}
		}

		// 返回值对象
		List<OrganizationalEntity> departments = null;

		// 查询员工信息
		List<OrganizationalEntity> employees = userService.getManagersByOrgAndDepartmentAndKeyword(userInfo.getOrgId(), departmentId, keyword);

		// 如果员工名称关键词是空的，就展示组织列表，否则不展示
		if (StringUtils.isBlank(keyword)) {
			departments = departmentService.getOrganizationalByOrgAndParentToAddFlow(userInfo.getOrgId(), departmentId);
		}

		Map<String, Object> res = new HashMap<>(2);
		if (departments != null && !departments.isEmpty()) {
			res.put("departments", departments);
		}
		if (employees != null && !employees.isEmpty()) {
			res.put("employees", employees);
		}

		return ResultVO.OK(res);
	}

	@ApiOperation(value = "查询组织审批人列表", notes = "查询组织审批人列表", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "departmentId", value = "组织ID", dataType = "int"),
			@ApiImplicitParam(name = "keyword", value = "审批人名称", dataType = "string")
	})
	@GetMapping("/getOrganizational")
	public ResultVO getOrganizational(@RequestParam(value = "departmentId", required = false) Integer departmentId,
									  @RequestParam(value = "keyword", required = false) String keyword) {

		UserInfo userInfo = getUserInfo();
		/*
		 * 返回值对象
		 */
		Map<String, Object> res = new HashMap<>(2);
		List<OrganizationalEntity> departments = null;

		/*
		 * 查询员工信息
		 */
		List<OrganizationalEntity> employees = userService.getManagersByOrgAndParentAndKeyword(userInfo.getOrgId(), departmentId, keyword);

		/*
		 * 如果员工名称关键词是空的，就展示组织列表，否则不展示
		 */
		if (StringUtils.isBlank(keyword)) {

			if (userInfo.isOwner()) {
				departments = departmentService.getOrganizationalByOrgAndParentForOwner(userInfo.getOrgId(), departmentId);
			} else {
//				List<Integer> departmentIds = departmentService.getDepartmentIdsByAppSignet(userInfo.getId());//查询员工可见组织列表
				List<Integer> departmentIds = userInfo.getVisualDepartmentIds();
				if (departmentIds != null && departmentIds.size() > 0) {
					departments = departmentService.getOrganizationalByOrgAndParentForUser(userInfo.getOrgId(), departmentId, departmentIds);//查询指定父节点、指定可见组织ID列表的组织列表
				}
			}

		}

		res.put("departments", departments);
		res.put("employees", employees);

		return ResultVO.OK(res);
	}

	@ApiOperation(value = "修改密码", notes = "修改密码", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "oldPassword", value = "原密码", dataType = "String", required = true),
			@ApiImplicitParam(name = "newPassword", value = "新秘钥", dataType = "String", required = true)

	})
	@WebLogger("修改密码")
	@PostMapping("/changePasswordV1")
	public ResultVO changePasswordV1(@RequestParam("oldPassword") String oldPassword,
									 @RequestParam("newPassword") String newPassword) {

		/*
		 * 校验旧密码
		 */
		if (StringUtils.isBlank(oldPassword)) {
			return ResultVO.FAIL("原密码不正确");
		}

		/*
		 * 校验新密码
		 */
		if (StringUtils.isBlank(newPassword)) {
			return ResultVO.FAIL("新密码不正确");
		}
		if (oldPassword.equals(newPassword)) {
			return ResultVO.FAIL("新旧密码不能相同");
		}

		/*
		 * 校验数据库密码匹配
		 */
		UserInfo userInfo = getUserInfo();
		Integer sysUserId = userInfo.getSysUserId();
		SysUser sysUser = sysUserService.get(sysUserId);
		if (!sysUser.getPassword().equals(MD5.toMD5(oldPassword))) {
			return ResultVO.FAIL("原密码不正确");
		}

		/*
		 * 校验密码规则
		 */
		StrategyPassword sp = strategyPasswordService.getByOrg(properties.getDefaultOrgId());
		try {
			StrategyUtil.checkStrategy(newPassword, sp);
		} catch (Exception e) {
			return ResultVO.FAIL(Code.ERROR510, e.getMessage());
		}

		sysUser.setPassword(MD5.toMD5(newPassword));
		sysUserService.update(sysUser);

		userInfoService.del(userInfo.getId());

		return ResultVO.FAIL(Code.TO_LOGIN_EXCEPTION608);
	}

	@ApiOperation(value = "查询登录用户是否拥有'负责人'权限", notes = "查询登录用户是否拥有'负责人'权限", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "组织ID（集团ID、公司ID）", dataType = "int"),
			@ApiImplicitParam(name = "type", value = "组织类型 0:部门 1：公司 2：集团", dataType = "int"),
	})
	@GetMapping("/isDirector")
	public ResultVO isDirector(@RequestParam(value = "id", required = false) Integer id,
							   @RequestParam(value = "type", required = false) Integer type) {
		UserInfo userInfo = getUserInfo();
		//集团负责人有权限
		if (userInfo.isOwner()) {
			return ResultVO.OK(true);
		}

		if (type != null && type == 2) {
			return ResultVO.OK(false);
		}

		//非集团负责人
		if (id != null && type != null) {
			//删除、修改组织
			List<Integer> departmentIds = userInfo.getDepartmentIds();
			if (departmentIds == null || departmentIds.isEmpty()) {
				return ResultVO.OK(false);
			}

			List<Integer> childrenIds = departmentService.getChildrenIdsByOrgAndParentsAndType(userInfo.getOrgId(), departmentIds, null);
			if (childrenIds.contains(id)) {
				return ResultVO.OK(true);
			} else {
				return ResultVO.OK(false);
			}
		} else {
			//查询该用户是否负责人
			int count = departmentService.getCountByManagerUserId(userInfo.getOrgId(), userInfo.getId());
			return ResultVO.OK(count > 0);
		}

	}

	@ApiOperation(value = "重置员工密码", notes = "重置员工密码", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userId", value = "员工ID", dataType = "int")
	})
	@WebLogger("重置用户密码")
	@PostMapping("/resetPassword")
	public ResultVO resetPassword(@RequestParam("userId") Integer userId) {
		/*
		 * 校验用户有效性
		 */
		User user = userService.get(userId);
		if (user == null) {
			return ResultVO.FAIL("该员工不存在");
		}
		UserInfo userInfo = getUserInfo();
		if (userInfo.getOrgId().intValue() != user.getOrgId()) {
			return ResultVO.FAIL("无权限操作");
		}

		/*
		 * 更新密码
		 */
		SysUser sysUser = sysUserService.get(user.getSysUserId());
		sysUser.setPassword(CommonUtils.properties.getDefaultPwd());
		sysUserService.update(sysUser);

		return ResultVO.OK("密码重置成功,初始化密码:123456");
	}

	/*
	 * 印章属于公司：仅本公司及下属部门可作为'管理员'，子公司、分公司、兄弟公司、父公司均无法管理;
	 * 印章属于部门：仅本部门及下属部门可作为'管理员';
	 *
	 * @param departmentId 印章所属组织ID
	 * @return
	 */
	@ApiOperation(value = "查询可作为'印章审计员'员工列表", notes = "查询可作为'印章审计员'员工列表", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "departmentId", value = "组织ID列表", dataType = "int", required = true),
			@ApiImplicitParam(name = "pageNum", value = "当前页(默认:1)", dataType = "int"),
			@ApiImplicitParam(name = "pageSize", value = "每页数(默认:10)", dataType = "int"),
			@ApiImplicitParam(name = "page", value = "是否分页", dataType = "boolean", defaultValue = "true")

	})
	@GetMapping("/getAuditorsByUpdateDevice")
	public ResultVO getAuditorsByUpdateDevice(@RequestParam("departmentId") Integer departmentId,
											  @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
											  @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
											  @RequestParam(value = "page", required = false, defaultValue = "true") boolean isPage) {
		//参数校验
		Department department = departmentService.get(departmentId);
		if (department == null) {
			return ResultVO.FAIL("所选组织不存在");
		}
		UserInfo userInfo = getUserInfo();
		if (department.getOrgId().intValue() != userInfo.getOrgId()) {
			return ResultVO.FAIL("所选组织有误");
		}

		//如果该组织是部门层级，则查询该部门的所属公司，再查询该公司下的所有部门的所有员工列表
		Integer type = department.getType();
		Department parent = null;
		if (type == 0) {
			parent = departmentService.getCompanyByChildrenId(departmentId);
		} else {
			parent = department;
		}
		List<Integer> childrenIds = departmentService.getChildrenIdsByOrgAndParentAndType(userInfo.getOrgId(), parent.getId(), 0);
		childrenIds.add(departmentId);

		//查询组织列表下的员工列表
		List<User> users = userService.getAuditorsByOrgAndDepartment(userInfo.getOrgId(), childrenIds);

		//手机端返回通讯录格式
		if (isApp()) {
			List<Map<String, Object>> addressList = CommonUtils.getAddressList(users, "userName");
			return ResultVO.OK(addressList);
		}
		return ResultVO.Page(users, isPage);
	}

	/**
	 * 印章属于公司：仅本公司及下属部门可作为'管理员'，子公司、分公司、兄弟公司、父公司均无法管理;
	 * 印章属于部门：仅本部门及下属部门可作为'管理员';
	 *
	 * @param departmentId 印章所属组织ID
	 * @return
	 */
	@ApiOperation(value = "查询可作为'印章管理员'员工列表", notes = "查询可作为'印章管理员'员工列表", httpMethod = "GET")
	@GetMapping("/getKeepersToUpdateDevice")
	public ResultVO getKeepersToUpdateDevice(@RequestParam("departmentId") Integer departmentId,
											 @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
											 @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
											 @RequestParam(value = "page", required = false, defaultValue = "true") boolean isPage) {
		//参数校验
		Department department = departmentService.get(departmentId);
		if (department == null) {
			return ResultVO.FAIL("所选组织不存在");
		}
		UserInfo userInfo = getUserInfo();
		if (department.getOrgId().intValue() != userInfo.getOrgId()) {
			return ResultVO.FAIL("所选组织有误");
		}

		//如果该组织是部门层级，则查询该部门的所属公司，再查询该公司下的所有部门的所有员工列表
		Integer type = department.getType();
		Department parent = null;
		if (type == 0) {
			parent = departmentService.getCompanyByChildrenId(departmentId);
		} else {
			parent = department;
		}
		List<Integer> childrenIds = departmentService.getChildrenIdsByOrgAndParentAndType(userInfo.getOrgId(), parent.getId(), 0);
		childrenIds.add(departmentId);

		//查询组织列表下的员工列表
		List<User> users = userService.getKeepersByOrgAndDepartment(userInfo.getOrgId(), childrenIds);

		//手机端返回通讯录格式
		if (isApp()) {
			List<Map<String, Object>> addressList = CommonUtils.getAddressList(users, "userName");
			return ResultVO.OK(addressList);
		}

		return ResultVO.Page(users, isPage);
	}

	@ApiOperation(value = "查询可作为'印章管理员'员工列表", notes = "查询可作为'印章管理员'员工列表", httpMethod = "GET")
	@GetMapping("/getKeepers")
	public ResultVO getKeepers() {
		UserToken token = getToken();
		boolean page = setPage();
		List<User> keepers = userService.getByOrgAndPerms(token.getOrgId(), "authorizationManagement");
		if (isApp()) {
			List<Map<String, Object>> res = CommonUtils.getAddressList(keepers, "userName");
			return ResultVO.OK(res);
		} else {
			return ResultVO.Page(keepers, page);
		}
	}

	@ApiOperation(value = "查询个人信息", notes = "查询个人信息", httpMethod = "GET")
	@GetMapping("/get")
	public ResultVO get() {
		UserInfo userInfo = getUserInfo();
		Integer userId = userInfo.getId();

		UserEntity userEntity = new UserEntity();

		/*
		 * 员工信息
		 */
		User user = userService.get(userId);
		if (user == null) {
			return ResultVO.FAIL("信息有误");
		}
		userEntity.setUserId(user.getId());
		String headImg = user.getHeadImg();

		FileEntity fileEntity = fileInfoService.getReduceImgURLByFileId(headImg);
		userEntity.setHeadImg(fileEntity);

		userEntity.setUserName(user.getUserName());
		userEntity.setPhone(userInfo.getPhone());
		userEntity.setRemark(user.getRemark());

		/*
		 * 集团信息
		 */
		Integer orgId = userInfo.getOrgId();
		Org org = orgService.get(orgId);
		if (org != null) {
			userEntity.setOrgName(org.getName());
		}

		/*
		 * 所属组织信息
		 */
		List<Department> departments = departmentService.getByOrgAndUser(orgId, userId);
		if (departments != null && departments.size() > 0) {
			for (Department department : departments) {
				String name = department.getName();
				userEntity.getDepartmentList().add(name);
			}
		}

		/*
		 * 角色信息
		 */
		List<Role> roles = roleService.getByUserId(user.getId());
		if (roles != null && roles.size() > 0) {
			for (Role role : roles) {
				String name = role.getName();
				userEntity.getRoleList().add(name);
			}
		}

		return ResultVO.OK(userEntity);
	}

	@ApiOperation(value = "查询用户可选择的审批人列表", notes = "查询用户可选择的审批人列表", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageNum", value = "当前页(默认:1)", dataType = "int"),
			@ApiImplicitParam(name = "pageSize", value = "每页数(默认:10)", dataType = "int"),
			@ApiImplicitParam(name = "page", value = "是否分页", dataType = "boolean", defaultValue = "true")
	})
	@GetMapping("/getManagers")
	public ResultVO getManagers(@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer
										pageNum,
								@RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
								@RequestParam(value = "page", required = false, defaultValue = "true") boolean isPage) {
		UserToken token = getToken();
		boolean page = setPage();
		List<User> managers = userService.getByOrgAndPerms(token.getOrgId(), "approvalProcessing");
		if (isApp()) {
			List<Map<String, Object>> addressList = CommonUtils.getAddressList(managers, "userName");
			return ResultVO.OK(addressList);
		}
		return ResultVO.Page(managers, page);
	}

	@ApiOperation(value = "员工列表", notes = "员工列表", httpMethod = "GET")
	@GetMapping("/userList")
	public ResultVO userList() {
		UserInfo userInfo = getUserInfo();
		Integer orgId = userInfo.getOrgId();
		List<User> userList = userService.getByOrg(orgId);
		if (userList == null || userList.isEmpty()) {
			return ResultVO.OK();
		}

		List<Map<String, Object>> mapList = new ArrayList<>(userList.size());
		for (User user : userList) {
			Map<String, Object> map = new HashMap<>(2);
			map.put("id", user.getId());
			map.put("name", user.getUserName());
			mapList.add(map);
		}
		return ResultVO.OK(mapList);
	}
}
