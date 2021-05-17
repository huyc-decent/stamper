package com.yunxi.stamper.controller;

import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.jwt.RSA.MyKeyFactory;
import com.yunxi.stamper.commons.jwt.RSA.RSACode;
import com.yunxi.stamper.commons.jwt.UserToken;
import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.other.RedisGlobal;
import com.yunxi.stamper.commons.response.Code;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.*;
import com.yunxi.stamper.entityVo.*;
import com.yunxi.stamper.logger.threadLocal.LocalHandle;
import com.yunxi.stamper.service.*;
import com.yunxi.stamper.sys.aop.annotaion.WebLogger;
import com.zengtengpeng.annotation.Lock;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/6 0006 21:43
 */
@Slf4j
@RestController
@RequestMapping(value = "/auth/org", method = {RequestMethod.POST, RequestMethod.GET})
@Api(tags = "集团相关")
public class OrgController extends BaseController {

	@Autowired
	private OrgService service;
	@Autowired
	private UserService userService;
	@Autowired
	private PositionService positionService;
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private FileInfoService fileInfoService;
	@Autowired
	private SignetService signetService;

	/**
	 * 查询平台公司列表(仅平台账户可用)
	 */
	@RequestMapping("/getAllOrgs")
	public ResultVO getAllOrgs() {
		UserInfo userInfo = getUserInfo();
		return ResultVO.OK(userInfo.getTree());
	}

	/**
	 * 用户注册时,获取平台所有公司列表(不包含平台公司)
	 */
	@RequestMapping("/getAllOrgsByReg")
	public ResultVO getAllOrgsByReg() {
		List<Map<String, Object>> res = new LinkedList<>();
		List<Org> orgs = service.getAll();
		if (orgs != null && orgs.size() > 0) {
			for (Org org : orgs) {
				if (org.getId() == -1) {
					continue;
				}
				Map<String, Object> map = new HashMap<>();
				map.put("id", org.getId());
				map.put("name", org.getName());
				res.add(map);
			}
		}
		return ResultVO.OK(res);
	}

	/**
	 * 获取公司，用于印章迁移
	 */
	@RequestMapping("/getChildrens")
	public ResultVO getChildrens() {
		UserInfo userInfo = getUserInfo();
		Integer type = userInfo.getType();
		List<Org> orgs = new ArrayList<>();
		if (type == 0) {
			//平台级用户
			orgs = service.getAll();
		}
		return ResultVO.OK(orgs);
	}


	@ApiOperation(value = "查询组织架构树节点信息", notes = "查询组织架构树节点信息", httpMethod = "GET")
	@ApiImplicitParam(name = "departmentId", value = "组织ID", dataType = "int", required = true)
	@GetMapping("/getOrganizationalTreeNode")
	public ResultVO getOrganizationalTreeNode(@RequestParam("id") Integer departmentId) {
		//防止越权访问
		UserInfo userInfo = getUserInfo();
		List<Integer> visualDepartmentIds = userInfo.getVisualDepartmentIds();
		if (!visualDepartmentIds.contains(departmentId)) {
			return ResultVO.FAIL("无权限");
		}

		OrganizationalTreeNode node = new OrganizationalTreeNode();
		Department department = departmentService.get(departmentId);
		if (department == null) {
			return ResultVO.OK();
		}
		node.setId(department.getId());
		node.setName(department.getName());
		node.setPositionId(department.getPositionId());
		if (department.getPositionId() != null) {
			Position position = positionService.get(department.getPositionId());
			if (position != null) {
				node.setPositionName(position.getName());
			}
		}
		node.setManagerUserId(department.getManagerUserId());
		if (department.getManagerUserId() != null) {
			User managerUser = userService.get(department.getManagerUserId());
			if (managerUser != null) {
				node.setManagerUserName(managerUser.getUserName());
			}
		}
		node.setCreateDate(department.getCreateDate());
		node.setRemark(department.getRemark());

		if (department.getParentId() != null) {
			Department parent = departmentService.get(department.getParentId());
			if (parent != null) {
				node.setParentId(department.getParentId());
				node.setParentName(parent.getName());
				node.setParentType(department.getType());
			}
		}

		node.setType(department.getType());
		node.setCode(department.getCode());
		String logo = department.getLogo();
		if (StringUtils.isNotBlank(logo)) {
			FileEntity fileEntity = fileInfoService.getReduceImgURLByFileId(logo);
			node.setLogo(fileEntity);
		}

		return ResultVO.OK(node);
	}

	@ApiOperation(value = "查询指定设备的组织架构，用于录入指纹", notes = "查询指定设备的组织架构，用于录入指纹", httpMethod = "GET")
	@ApiImplicitParam(name = "deviceId", value = "设备ID", dataType = "int", required = true)
	@GetMapping("/getOrganizationalToAddFinger")
	public ResultVO getOrganizationalToAddFinger(@RequestParam("deviceId") Integer deviceId) {
		/*
		 * 参数校验：设备ID
		 */
		UserInfo userInfo = getUserInfo();
		Signet signet = signetService.get(deviceId);
		if (signet == null || signet.getOrgId().intValue() != userInfo.getOrgId().intValue()) {
			return ResultVO.FAIL("设备不存在");
		}
		Integer signetDepartmentId = signet.getDepartmentId();
		Department department = departmentService.get(signetDepartmentId);
		if (department == null) {
			return ResultVO.FAIL("该设备未配置所属组织，请联系管理员更新");
		}
		if (department.getOrgId().intValue() != userInfo.getOrgId().intValue()) {
			return ResultVO.FAIL("无权限");
		}

		/*
		 * 权限校验：管章人、负责人
		 */
		Integer keeperId = signet.getKeeperId();
		if (!userInfo.isOwner()
				&& !CommonUtils.isEquals(keeperId, userInfo.getId())
				&& !userInfo.getDepartmentIds().contains(signetDepartmentId)) {
			return ResultVO.FAIL("无权限");
		}

		/*
		 * 查询印章所属组织架构(不包含公司层级)
		 */
		List<OrganizationalTree> organizationalTrees = departmentService.getOrganizationalByOrgAndParentAndType(userInfo.getOrgId(), signetDepartmentId, 0);

		//组装返回值
		OrganizationalTree root = new OrganizationalTree(department);
		root.setChildrens(organizationalTrees);

		return ResultVO.OK(root);
	}

	@ApiOperation(value = "查询组织架构用于录入指纹", notes = "查询组织架构用于录入指纹", httpMethod = "GET")
	@ApiImplicitParam(name = "deviceId", value = "查询的印章ID", dataType = "int")
	@GetMapping("/getOrganizationalToSelectSignet")
	public ResultVO getOrganizationalToSelectSignet(@RequestParam("deviceId") Integer deviceId) {
		/*
		 * 查询校验
		 */
		UserToken token = getToken();
		Signet signet = signetService.get(deviceId);
		if (signet == null || !CommonUtils.isEquals(token.getOrgId(), signet.getOrgId())) {
			return ResultVO.OK("设备不存在");
		}

		Integer signetDepartmentId = signet.getDepartmentId();
		Department company = departmentService.getCompanyByChildrenId(signetDepartmentId);

		DepartmentVo root = new DepartmentVo();
		BeanUtils.copyProperties(company, root);
		root.setType(company.getType());

		departmentService.getByOrgAndParent(signet.getOrgId(), root);

		return ResultVO.OK(root);
	}

	/*
	 * 查询登录用户所属组织架构
	 */
	@RequestMapping("/getOrganizational")
	public ResultVO getOrganizational() {
		UserInfo userInfo = getUserInfo();
		Integer orgId = userInfo.getOrgId();
		Org org = service.get(orgId);
		if (org != null) {
			OrgStructure os = new OrgStructure();
			BeanUtils.copyProperties(org, os);

			Integer managerUserId = org.getManagerUserId();
			User managerUser = userService.get(managerUserId);
			if (managerUser != null) {
				os.setManagerUserName(managerUser.getUserName());
			}

			List<DepartmentVo> departMents = departmentService.getOrganizational(os.getId());
			os.setChildrens(departMents);

			return ResultVO.OK(os);
		}
		return ResultVO.FAIL("组织不存在");
	}

	/*
	 * 查询登录用户的公司信息
	 */
	@RequestMapping("/getByOwner")
	public ResultVO getByOwner() {
		UserToken token = getToken();
		Org org = service.get(token.getOrgId());
		if (org == null) {
			return ResultVO.FAIL("所属公司不存在");
		}
		Map<String, Object> res = new HashMap<>();
		res.put("name", org.getName());
		res.put("code", org.getCode());
		res.put("createDate", org.getCreateDate());
		res.put("isOos", false);

		/*
		 * 组织图标
		 */
		String logo = org.getLogo();
		if (StringUtils.isNotBlank(logo)) {
			FileEntity fileEntity = fileInfoService.getReduceImgURLByFileId(logo);
			res.put("logo", fileEntity);
		}

		User managerUser = userService.get(org.getManagerUserId());
		if (managerUser != null) {
			res.put("managerName", managerUser.getUserName());
		}
		return ResultVO.OK(res);

	}

	@ApiOperation(value = "修改集团公司信息", notes = "修改集团公司信息", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "集团ID", dataType = "int", required = true),
			@ApiImplicitParam(name = "name", value = "集团名称", dataType = "String", required = true),
			@ApiImplicitParam(name = "code", value = "集团编码", dataType = "String", required = true),
			@ApiImplicitParam(name = "fileId", value = "集团LOGO图片ID", dataType = "int"),
			@ApiImplicitParam(name = "managerUserId", value = "负责人ID", dataType = "int", required = true),
			@ApiImplicitParam(name = "remark", value = "集团简介", dataType = "String"),
			@ApiImplicitParam(name = "positionId", value = "负责人称谓ID", dataType = "int"),
			@ApiImplicitParam(name = "location", value = "集团所在地址", dataType = "String")
	})
	@WebLogger("修改组织信息")
	@PostMapping("/updateOrg")
	public ResultVO updateOrg(@RequestParam("id") Integer departmentId,
							  @RequestParam("name") String name,
							  @RequestParam("code") String code,
							  @RequestParam("managerUserId") Integer managerUserId,
							  @RequestParam(value = "fileId", required = false) String fileId,
							  @RequestParam(value = "remark", required = false) String remark,
							  @RequestParam(value = "positionId", required = false) Integer positionId,
							  @RequestParam(value = "location", required = false) String location) {
		UserInfo userInfo = getUserInfo();
		Department department = departmentService.get(departmentId);
		if (department == null) {
			return ResultVO.FAIL("该组织不存在");
		}
		if (!Objects.equals(department.getOrgId(), userInfo.getOrgId())) {
			return ResultVO.FAIL("无权限修改");
		}
		Org org = service.get(userInfo.getOrgId());
		if (org == null) {
			return ResultVO.FAIL("该集团公司不存在");
		}
		LocalHandle.setOldObj(org);

		// 校验权限，只有集团属主有权限编辑集团信息
		if (!Objects.equals(org.getManagerUserId(), userInfo.getId())) {
			return ResultVO.FAIL("您无权限编辑该公司信息");
		}

		// 校验LOGO
		if (StringUtils.isNotBlank(fileId)) {
			FileInfo fileInfo = fileInfoService.get(fileId);
			if (fileInfo == null) {
				return ResultVO.FAIL("LOGO不存在或已失效");
			}
		}

		// 校验公司名称
		if (!name.equals(org.getName())) {
			List<Org> exists = service.getByName(name);
			for (Org exist : exists) {
				if (!Objects.equals(exist.getId(), org.getId())) {
					return ResultVO.FAIL("该集团名称已存在");
				}
			}
		}

		// 校验公司编码
		if (!code.equals(org.getCode())) {
			Org orgByCode = service.getByCode(code);
			if (orgByCode != null) {
				return ResultVO.FAIL("该集团编码已存在");
			}
		}

		// 校验称谓
		Position position;
		if (positionId != null) {
			position = positionService.get(positionId);
			if (position == null || position.getOrgId().intValue() != userInfo.getOrgId().intValue()) {
				return ResultVO.FAIL("该负责人称谓不存在");
			}
		}

		//校验负责人
		User user = userService.get(managerUserId);
		if (user == null || !Objects.equals(user.getOrgId(), userInfo.getOrgId())) {
			return ResultVO.FAIL("该负责人不存在");
		}

		service.updateOrg(org, name, remark, code, managerUserId, fileId, positionId, location);

		return ResultVO.OK("更新成功");
	}

	@ApiOperation(value = "注册组织", notes = "注册组织", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "orgName", value = "组织名称", dataType = "String", required = true),
			@ApiImplicitParam(name = "phone", value = "属主手机号", dataType = "String", required = true),
			@ApiImplicitParam(name = "orgCode", value = "组织编码", dataType = "String", required = true),
			@ApiImplicitParam(name = "verCode", value = "短信验证码", dataType = "String", required = true)
	})
	@PostMapping("/regV2")
	@Lock(keys = "#orgName + '_' + #phone + '_' + #orgCode + '_' + #verCode")
	public ResultVO regV2(@RequestParam String orgName, @RequestParam String phone, @RequestParam String orgCode, @RequestParam String verCode) {
		if (StringUtils.isBlank(verCode)) {
			return ResultVO.FAIL("验证码不能为空");
		}

		//检查版本
		if (getVersion() > 0) {
			//对账号密码进行RSA解密原文
			String accountDecrypt = null;
			String passDecrypt = null;
			try {
				byte[] decrypt = RSACode.decryptByPrivateKey(verCode, MyKeyFactory.str_priK);
				passDecrypt = new String(decrypt, StandardCharsets.UTF_8);

				decrypt = RSACode.decryptByPrivateKey(phone, MyKeyFactory.str_priK);
				accountDecrypt = new String(decrypt, StandardCharsets.UTF_8);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (StringUtils.isAnyBlank(accountDecrypt, passDecrypt)) {
				return ResultVO.FAIL("数据有误");
			}
			verCode = passDecrypt;
			phone = accountDecrypt;
		}

		String key = RedisGlobal.PHONE_VERIFI_CODE + 4 + ":" + phone;
		Object obj = redisUtil.get(key);
		if (obj == null || StringUtils.isBlank(obj.toString())) {
			return ResultVO.FAIL("短信验证码不正确");
		}
		if (!StringUtils.equalsIgnoreCase(verCode, obj.toString())) {
			return ResultVO.FAIL("短信验证码不正确");
		}

		if (StringUtils.isBlank(orgName)) {
			return ResultVO.FAIL("组织名称不能为空");
		}
		List<Org> orgs = service.getByName(orgName);
		if (orgs != null && orgs.size() > 0) {
			return ResultVO.FAIL("该集团名称已存在");
		}

		if (StringUtils.isBlank(orgCode)) {
			return ResultVO.FAIL("组织编码不能为空");
		}
		Org org = service.getByCode(orgCode);
		if (org != null) {
			return ResultVO.FAIL("该组织编码已注册");
		}

		//注册公司
		service.regOrg(orgName, phone, orgCode);

		//删除短信缓存
		redisUtil.del(key);

		return ResultVO.OK("注册成功");
	}

	//移动端组织编码验证
	@RequestMapping("/dir")
	public ResultVO verifyOrgCode(String orgCode) {
		if (StringUtils.isNotBlank(orgCode)) {
			String code = orgCode.toUpperCase();
			Org byCode = service.getByCode(code);
			if (byCode != null) {
				//判断组织状态
				//公司状态 0:正常  1:停用  2:注销
				Integer status = byCode.getStatus();
				if (status == null || status == 0) {
					HashMap<String, String> map = new HashMap<>();
					map.put("code", byCode.getCode());
					return ResultVO.OK(map);
				} else {
					return ResultVO.OK("当前组织已停用或已注销");
				}
			}
			return ResultVO.OK("组织编码不存在");
		}
		return ResultVO.FAIL(Code.FAIL402);
	}

	@ApiOperation(value = "查询组织列表", notes = "查询登录用户所属组织架构的公司列表", httpMethod = "GET")
	@GetMapping("/getOrgsForSearchSignetList")
	public ResultVO getOrgsForSearchSignetList() {
		List<Org> orgs = service.getOrgsForSearchSignetList();
		return ResultVO.OK(orgs);
	}

	@ApiOperation(value = "获取指定手机号所在的公司列表", notes = "查询该手机号拥有的组织列表", httpMethod = "GET")
	@ApiImplicitParam(name = "phone", value = "登录名(手机号码)", dataType = "String", required = true, paramType = "query")
	@GetMapping("/getOrgsByPhone")
	public ResultVO getOrgsByPhone(@RequestParam("phone") String phone) {
//		List<Org> orgs = new ArrayList<>();
//		SysUser sysUser = sysUserService.getByPhone(phone);
//		if (sysUser == null) {
//			return ResultVO.FAIL("该手机号未注册");
//		}
//		Integer defaultOrgId = sysUser.getDefaultOrgId();
//		Org org = service.get(defaultOrgId);
//		if (org != null) {
//			orgs.add(org);
//		} else {
//			orgs = service.getOrgsByPhone(phone);
//		}
//
//		return ResultVO.OK(orgs);
		Integer[] ids = new Integer[]{-1, -2, -3};
		return ResultVO.OK(ids);
	}

	@ApiOperation(value = "查询登录用户拥有的集团列表", notes = "查询登录用户拥有的集团列表", httpMethod = "GET")
	@GetMapping("/getOrgsByLogin")
	public ResultVO getOrgsByLogin() {
		UserInfo userInfo = getUserInfo();
		Integer orgId = userInfo.getOrgId();

		//查询该用户的账户ID
		Integer sysUserId = userInfo.getSysUserId();

		//查询该账户绑定的所属集团ID列表信息
		List<Integer> orgIds = userService.getOrgIdsBySysUserId(sysUserId);

		//查询该用户ID列表所属集团列表信息
		if (orgIds != null && orgIds.size() > 0) {
			List<Org> orgs = new LinkedList<>();
			for (Integer sysUserOrgId : orgIds) {
				if (sysUserOrgId.intValue() != orgId.intValue()) {
					Org org = service.get(sysUserOrgId);
					if (org != null) {
						orgs.add(org);
					}
				}
			}
			return ResultVO.OK(orgs);
		}

		return ResultVO.OK();
	}

	@ApiOperation(value = "查询登录用户组织树", notes = "查询登录用户组织树", httpMethod = "GET")
	@GetMapping("/getOrgTree")
	public ResultVO getOrgTree() {
		UserInfo userInfo = getUserInfo();
		return ResultVO.OK(userInfo.getTree());
	}
}
