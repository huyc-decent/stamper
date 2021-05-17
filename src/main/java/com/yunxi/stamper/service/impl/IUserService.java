package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.base.BaseService;
import com.yunxi.stamper.commons.md5.MD5;
import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.other.Global;
import com.yunxi.stamper.commons.other.RedisGlobal;
import com.yunxi.stamper.entity.*;
import com.yunxi.stamper.entityVo.*;
import com.yunxi.stamper.logger.threadLocal.LocalHandle;
import com.yunxi.stamper.mapper.UserMapper;
import com.yunxi.stamper.service.*;
import com.yunxi.stamper.sys.context.SpringContextUtils;
import com.yunxi.stamper.sys.error.base.PrintException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/7 0007 9:07
 */
@Slf4j
@Service
public class IUserService extends BaseService implements UserService {
	@Autowired
	private UserMapper mapper;
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private RelateDepartmentUserService relateDepartmentUserService;
	@Autowired
	private PhoneIconService phoneIconService;
	@Autowired
	private UserPhoneIconService userPhoneIconService;
	@Autowired
	private PermService permService;
	@Autowired
	private ShortcutService shortcutService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private UserRoleService userRoleService;
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	@Lazy
	private UserInfoService userInfoService;

	/**
	 * 查询该公司拥有管理员权限的用户列表
	 */
	@Override
	public List<User> getByOrgAndRole(Integer orgId, String roleCode) {
		if (orgId == null || StringUtils.isBlank(roleCode)) {
			return null;
		}
		return mapper.selectByOrgAndRole(orgId, roleCode);
	}

	//获取公司的员工列表
	@Override
	public List<User> getEmps(Integer orgId) {
		if (orgId == null) {
			return null;
		}
		return mapper.getEmpList(orgId);
	}

	@Override
	public User getByOrgAndName(Integer orgId, String name) {
		if (orgId == null || StringUtils.isBlank(name)) {
			return null;
		}
		Example example = new Example(User.class);
		example.createCriteria().andIsNull("deleteDate")
				.andEqualTo("userName", name)
				.andEqualTo("orgId", orgId);
		return mapper.selectOneByExample(example);
	}

	/**
	 * 模糊查询用印人
	 */
	@Override
	public List<User> getUsersByKeyword(Integer orgId, String name) {
		Example example = new Example(User.class);
		Example.Criteria criteria = example.createCriteria().andIsNull("deleteDate")
				.andEqualTo("orgId", orgId);
		if (StringUtils.isNotBlank(name)) {
			criteria.andLike("userName", '%' + name + '%');
		}
		return mapper.selectByExample(example);
	}

	/**
	 * 查询部门id集合下的所有用户列表
	 */
	@Override
	public List<User> getByDepartment(List<Integer> departmentIds) {
		if (departmentIds == null || departmentIds.isEmpty()) {
			return null;
		}
		return mapper.selectByDepartmentIds(departmentIds);
	}

	/**
	 * 查询公司下拥有code权限的用户列表
	 *
	 * @param orgId     公司id
	 * @param permsCode 权限Code
	 */
	@Override
	public List<User> getByOrgAndPerms(Integer orgId, String permsCode) {
		if (orgId == null || StringUtils.isBlank(permsCode)) {
			return null;
		}
		return mapper.selectByOrgAndPerms(orgId, permsCode);
	}

	/**
	 * 查询公司下员工列表
	 */
	@Override
	public List<UserVo> getByOrgId(Integer orgId) {
		if (orgId == null) {
			return null;
		}
		List<UserVo> userVos = mapper.selectByOrgId(orgId);
		return userVos;
	}

	/**
	 * 查询公司员工列表
	 */
	@Override
	public List<User> getByOrg(Integer orgId) {
		if (orgId == null) {
			return null;
		}
		Example example = new Example(User.class);
		example.createCriteria().andIsNull("deleteDate")
				.andEqualTo("orgId", orgId);
		return mapper.selectByExample(example);
	}

	@Override
	public User get(Integer id) {
		if (id == null) {
			return null;
		}
		Example example = new Example(User.class);
		example.createCriteria().andIsNull("deleteDate")
				.andEqualTo("id", id);
		return mapper.selectOneByExample(example);
	}

	@Override
	public User getWithDel(Integer id) {
		if (id == null) {
			return null;
		}
		Example example = new Example(User.class);
		example.createCriteria().andEqualTo("id", id);
		return mapper.selectOneByExample(example);
	}

	@Override
	@Transactional
	public void update(User user) {
		int updateCount = 0;
		if (user != null && user.getId() != null) {
			user.setUpdateDate(new Date());
			updateCount = mapper.updateByPrimaryKey(user);
		}
		if (updateCount != 1) {
			throw new PrintException("用户修改失败");
		}
		//删除缓存
//		redisUtil.del(RedisGlobal.USER_INFO + user.getId());
		userInfoService.del(user.getId());
	}

	@Override
	@Transactional
	public void add(User user) {
		int addCount = 0;
		if (user != null) {
			user.setCreateDate(new Date());
			user.setUpdateDate(new Date());
			user.setDeleteDate(null);
			addCount = mapper.insert(user);
		}
		if (addCount != 1) {
			throw new PrintException("用户添加失败");
		}
	}

	/**
	 * 查询指定组织、手机号码的用户信息
	 *
	 * @param orgId 组织ID
	 * @param phone 手机号码
	 * @return
	 */
	@Override
	public User getByOrgAndPhone(Integer orgId, String phone) {
		if (orgId == null || StringUtils.isBlank(phone)) {
			return null;
		}
		return mapper.selectByOrgAndPhone(orgId, phone);
	}

	/**
	 * 查询指定组织下的的用户ID列表
	 *
	 * @param departmentIds 组织ID列表
	 * @return
	 */
	@Override
	public List<Integer> getIdsByDepartmentIds(Collection<Integer> departmentIds) {
		if (departmentIds == null || departmentIds.isEmpty()) {
			return null;
		}
		return mapper.selectIdsByDepartmentIds(departmentIds);
	}

	/**
	 * 添加员工
	 *
	 * @param userName      用户名
	 * @param phone         手机号(登录名)
	 * @param roleIds       角色列表
	 * @param departmentIds 组织ID
	 * @param isUse         true：正常  false：禁用
	 */
	@Override
	@Transactional
	public void addEmployee(SysUser sysUser, String userName, String phone, String remark, Boolean isUse, List<Integer> roleIds, List<Integer> departmentIds) {
		UserInfo userInfo = userInfoService.get(SpringContextUtils.getToken().getUserId());
		if (sysUser == null) {
			sysUser = new SysUser();
			sysUser.setPhone(phone);
			sysUser.setPassword(CommonUtils.properties.getDefaultPwd());
			sysUserService.add(sysUser);
		}

		//添加员工
		User employee = new User();
		employee.setUserName(userName);
		employee.setType(userInfo.getType());
		employee.setSysUserId(sysUser.getId());
		employee.setOrgId(userInfo.getOrgId());
		employee.setStatus(isUse ? Global.USER_STATUS_NORMAL : Global.USER_STATUS_LOCK);
		employee.setRemark((StringUtils.isBlank(remark) || "undefined".equalsIgnoreCase(remark) || "null".equalsIgnoreCase(remark)) ? null : remark);
		add(employee);

		//绑定角色列表
		for (Integer roleId : roleIds) {
			UserRole userRole = new UserRole();
			userRole.setRoleId(roleId);
			userRole.setUserId(employee.getId());
			userRoleService.add(userRole);
		}

		//添加组织-员工关联信息
		if (departmentIds != null && departmentIds.size() > 0) {
			for (Integer departmentId : departmentIds) {
				RelateDepartmentUser departmentUser = new RelateDepartmentUser();
				departmentUser.setDepartmentId(departmentId);
				departmentUser.setUserId(employee.getId());
				relateDepartmentUserService.add(departmentUser);
			}
		}

		//初始化用户手机图标
		List<PhoneIcon> phoneIcons = phoneIconService.getAll();
		if (phoneIcons != null && phoneIcons.size() > 0) {
			for (PhoneIcon phoneIcon : phoneIcons) {
				//初始化手机图标
				UserPhoneIcon userPhoneIcon = new UserPhoneIcon();
				userPhoneIcon.setPhoneIconId(phoneIcon.getId());
				userPhoneIcon.setUserId(employee.getId());
				userPhoneIconService.add(userPhoneIcon);
			}
		}

		//初始化web端快捷方式
		List<Perms> permsList = permService.getAll();
		if (permsList != null && permsList.size() > 0) {
			for (Perms perms : permsList) {
				Integer isShortcut = perms.getIsShortcut();
				//如果该权限允许作为快捷方式，则为管理员创建快捷方式
				if (isShortcut != null && Objects.equals(isShortcut, 1)) {
					Shortcut shortcut = new Shortcut();
					shortcut.setName(perms.getLabel());
					shortcut.setPermsId(perms.getId());
					shortcut.setUserId(employee.getId());
					shortcut.setOrgId(employee.getOrgId());
					shortcutService.add(shortcut);
				}
			}
		}
	}

	/**
	 * 查询员工列表
	 *
	 * @param keyword       要查询的员工名称、手机号关键词
	 * @param departmentIds 要查询的组织
	 * @return
	 */
	@Override
	public List<Employee> getEmployeesByKeyword(Integer orgId, List<Integer> departmentIds, String keyword) {
		keyword = StringUtils.isBlank(keyword) ? null : keyword.trim();
		return mapper.selectEmployeesByKeyword(orgId, departmentIds, keyword);
	}

	/**
	 * 删除员工
	 *
	 * @param user 员工信息
	 */
	@Override
	@Transactional

	public void delEmployee(User user) {

		//删除组织-员工关联信息
		relateDepartmentUserService.delByEmployeeId(user.getId());

		//置空该员工管理的组织负责人字段
		//departmentService.delByManagerUserId(user.getId());

		//删除角色-员工关联信息
		userRoleService.delByUserId(user.getId());

		//删除web端快捷方式
		userPhoneIconService.deleteAllByUserId(user.getId());

		//删除手机端图标
		shortcutService.deleteAllByUserId(user.getId());

		//删除账号默认组织属性
		SysUser sysUser = sysUserService.get(user.getSysUserId());
		if (sysUser != null && sysUser.getDefaultOrgId() != null && Objects.equals(sysUser.getDefaultOrgId(), user.getOrgId())) {
			sysUser.setDefaultOrgId(null);
			sysUserService.update(sysUser);
		}

		//删除用户信息
		del(user);

		/***检查该用户账号是否有其他绑定账号*/
		Integer sysUserId = user.getSysUserId();
		List<User> users = getBySysUserId(sysUserId);
		if (users == null || users.isEmpty() || (users.size() == 1 && Objects.equals(users.get(0).getId(), user.getId()))) {
			sysUserService.del(sysUser);
		}
	}


	private void del(User user) {
		int delCount = 0;
		if (user != null && user.getId() != null) {
			user.setDeleteDate(new Date());
			delCount = mapper.updateByPrimaryKey(user);
		}
		if (delCount != 1) {
			throw new PrintException("用户删除失败");
		}
		//删除缓存
		userInfoService.del(user.getId());
		redisUtil.del(RedisGlobal.USER_INFO_TOKEN_WEB + user.getId(), RedisGlobal.USER_INFO_TOKEN_APP + user.getId());
	}

	/**
	 * 更新员工信息
	 *
	 * @param user          源员工信息
	 * @param userName      新名称
	 * @param phone         新手机号
	 * @param roleIds       新角色列表
	 * @param departmentIds 新组织列表
	 */
	@Override
	@Transactional
	public void updateEmployee(User user, String userName, String remark, Integer status, String phone, List<Integer> roleIds, List<Integer> departmentIds) {
		LocalHandle.setOldObj(user);

		/**
		 * 更新员工信息
		 */
		user.setUserName(userName);
		user.setStatus(status);
		user.setRemark((StringUtils.isBlank(remark) || "undefined".equalsIgnoreCase(remark) || "null".equalsIgnoreCase(remark)) ? null : remark);
		update(user);

		/**
		 * 更新账号信息
		 */
		SysUser sysUser = sysUserService.get(user.getSysUserId());
		if (sysUser == null) {
			throw new PrintException("员工账号信息有误，请联系管理员");
		}
		sysUser.setPhone(phone);
		sysUserService.update(sysUser);

		/**
		 * 更新角色-员工关联信息
		 */
		userRoleService.delByUserId(user.getId());
		for (int i = 0; i < roleIds.size(); i++) {
			Integer roleId = roleIds.get(i);
			UserRole userRole = new UserRole();
			userRole.setUserId(user.getId());
			userRole.setRoleId(roleId);

			userRoleService.add(userRole);
		}

		/**
		 * 更新员工原组织负责人信息
		 */
		List<Department> departments = departmentService.getByOrgAndManager(user.getOrgId(), user.getId());
		if (departments != null && !departments.isEmpty()) {
			for (int i = 0; i < departments.size(); i++) {
				Department department = departments.get(i);

				if (departmentIds == null || departmentIds.isEmpty() || !departmentIds.contains(department.getId())) {
					department.setManagerUserId(null);
					departmentService.update(department);
				}
			}
		}

		/**
		 * 更新组织-员工关联信息
		 */
		relateDepartmentUserService.delByEmployeeId(user.getId());
		for (int i = 0; i < departmentIds.size(); i++) {
			Integer departmentId = departmentIds.get(i);

			RelateDepartmentUser departmentUser = new RelateDepartmentUser();
			departmentUser.setDepartmentId(departmentId);
			departmentUser.setUserId(user.getId());

			relateDepartmentUserService.add(departmentUser);
		}

		//删除用户缓存信息
//		redisUtil.del(RedisGlobal.USER_INFO + user.getId());
		userInfoService.del(user.getId());

		LocalHandle.setNewObj(user);
		LocalHandle.complete("更新人员信息");
	}

	/**
	 * 查询指定用户拥有的集团ID列表
	 *
	 * @param sysUserId 要查询的用户ID
	 * @return
	 */
	@Override
	public List<Integer> getOrgIdsBySysUserId(Integer sysUserId) {
		if (sysUserId == null) {
			return null;
		}
		return mapper.selectOrgIdsBySysUserId(sysUserId);
	}

	/**
	 * 账号注册
	 *
	 * @param phone    手机号码
	 * @param password 密码
	 * @param org      注册组织
	 */
	@Override
	@Transactional
	public void regUser(String phone, String password, Org org) {
		SysUser sysUser = new SysUser();
		sysUser.setPassword(MD5.toMD5(password));
		sysUser.setPhone(phone);
		sysUserService.add(sysUser);

		User user = new User();
		user.setSysUserId(sysUser.getId());
		user.setOrgId(org.getId());
		user.setType(3);
		user.setStatus(0);
		add(user);

		//初始化用户手机图标
		List<PhoneIcon> phoneIcons = phoneIconService.getAll();
		if (phoneIcons != null && phoneIcons.size() > 0) {
			for (int i = 0; i < phoneIcons.size(); i++) {
				PhoneIcon phoneIcon = phoneIcons.get(i);

				//初始化手机图标
				UserPhoneIcon userPhoneIcon = new UserPhoneIcon();
				userPhoneIcon.setPhoneIconId(phoneIcon.getId());
				userPhoneIcon.setUserId(user.getId());
				userPhoneIconService.add(userPhoneIcon);
			}
		}

		//初始化web端快捷方式
		List<Perms> permsList = permService.getAll();
		if (permsList != null && permsList.size() > 0) {
			for (int i = 0; i < permsList.size(); i++) {
				Perms perms = permsList.get(i);
				Integer isShortcut = perms.getIsShortcut();
				//如果该权限允许作为快捷方式，则为管理员创建快捷方式
				if (isShortcut != null && isShortcut.intValue() == 1) {
					Shortcut shortcut = new Shortcut();
					shortcut.setName(perms.getLabel());
					shortcut.setPermsId(perms.getId());
					shortcut.setUserId(user.getId());
					shortcut.setOrgId(org.getId());
					shortcutService.add(shortcut);
				}
			}
		}

		//初始化默认角色"普通用户"
		Role userRole = roleService.getByCodeAndOrg("user", org.getId());
		if (userRole != null) {
			UserRole ur = userRoleService.getByUserAndRole(user.getId(), userRole.getId());
			if (ur == null) {
				ur = new UserRole();
				ur.setUserId(user.getId());
				ur.setRoleId(userRole.getId());
				userRoleService.add(ur);
			}
		}

		//绑定根组织'root'
		Department root = departmentService.getRootByOrg(org.getId());
		if (root != null) {
			RelateDepartmentUser rdu = relateDepartmentUserService.get(user.getId(), root.getId());
			if (rdu == null) {
				rdu = new RelateDepartmentUser();
				rdu.setDepartmentId(root.getId());
				rdu.setUserId(user.getId());
				relateDepartmentUserService.add(rdu);
			}
		}
	}

	/**
	 * 查询指定账户的用户列表
	 *
	 * @param sysUserId
	 * @return
	 */
	@Override
	public List<User> getBySysUserId(Integer sysUserId) {
		if (sysUserId == null) {
			return null;
		}
		Example example = new Example(User.class);
		example.createCriteria().andIsNull("deleteDate").andEqualTo("sysUserId", sysUserId);
		return mapper.selectByExample(example);
	}

	/**
	 * 查询指定组织列表下的员工ID列表
	 *
	 * @param departmentIds 组织ID列表
	 * @return
	 */
	@Override
	public List<Integer> getUserIdsByDepartment(List<Integer> departmentIds) {
		if (departmentIds == null || departmentIds.isEmpty()) {
			return null;
		}
		return mapper.selectUserIdsByDepartmentIds(departmentIds);
	}

	/**
	 * 修改个人信息
	 *
	 * @param phone    手机号
	 * @param userName 用户名称
	 * @param remark   备注
	 * @param fileUUID 头像
	 */
	@Override
	@Transactional
	public User updateOwner(String phone, String userName, String remark, String fileUUID) {
//		UserInfo userInfo = SpringContextUtils.getUserInfo();
		UserInfo userInfo = userInfoService.get(SpringContextUtils.getToken().getUserId());
		User user = get(userInfo.getId());

		Integer sysUserId = userInfo.getSysUserId();
		SysUser sysUser = sysUserService.get(sysUserId);
		log.info("phone:{}\tusername:{}\tremark:{}\tfileUUID:{}\tsysuser:{}", phone, userName, remark, fileUUID, CommonUtils.objToJson(sysUser));
		if (!phone.equalsIgnoreCase(sysUser.getPhone())) {
			sysUser.setPhone(phone);
			sysUserService.update(sysUser);
		}

		if (!userName.equals(user.getUserName())) {
			user.setUserName(userName);
		}

		user.setRemark((StringUtils.isBlank(remark) || "undefined".equalsIgnoreCase(remark) || "null".equalsIgnoreCase(remark)) ? null : remark);
		user.setHeadImg((StringUtils.isBlank(fileUUID) || "undefined".equalsIgnoreCase(fileUUID) || "null".equalsIgnoreCase(fileUUID)) ? null : fileUUID);

		update(user);

		return user;
	}

	/**
	 * 查询指定集团下、指定组织列表下的员工列表
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 组织ID
	 * @return
	 */
	@Override
	public List<User> getEmployeeForManagerByDepartments(@NotNull Integer orgId, @NotNull List<Integer> departmentIds) {
		if (orgId == null || departmentIds == null || departmentIds.isEmpty()) {
			return null;
		}
		return mapper.selectEmployeeForManagerByDepartments(orgId, departmentIds);
	}

	/**
	 * 查询指定集团下、指定组织下、指定关键词的员工列表
	 *
	 * @param orgId        集团ID
	 * @param departmentId 父组织ID
	 * @param keyword      员工关键词
	 * @return
	 */
	@Override
	public List<OrganizationalEntity> getManagersByOrgAndParentAndKeyword(@NotNull Integer orgId, Integer departmentId, String keyword) {
		if (orgId == null) {
			return null;
		}
		return mapper.selectManagersByOrgAndParentAndKeyword(orgId, departmentId, keyword);
	}

	/**
	 * 查询指定集团下员工ID列表
	 *
	 * @param orgId 集团ID
	 * @return
	 */
	@Override
	public List<Integer> getIdsByOrg(Integer orgId) {
		if (orgId == null) {
			return null;
		}
		return mapper.selectIdsByOrg(orgId);
	}

	/**
	 * 查询指定账号ID、指定集团的用户信息
	 *
	 * @param sysUserId 账号ID
	 * @param orgId     集团ID
	 * @return
	 */
	@Override
	public User getBySysUserAndOrg(Integer sysUserId, Integer orgId) {
		if (sysUserId == null || orgId == null) {
			return null;
		}
		return mapper.selectBySysUserAndOrg(sysUserId, orgId);
	}

	/**
	 * 查询组织下审批人列表，用于添加审批流程
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 组织ID列表
	 * @param keyword       员工名称关键词
	 * @return
	 */
	@Override
	public List<OrganizationalEntity> getManagersByOrgAndParentAndKeywordToAddFlow(Integer orgId, List<Integer> departmentIds, String keyword) {
		if (orgId == null || departmentIds == null || departmentIds.isEmpty()) {
			return null;
		}
		return mapper.selectManagersByOrgAndParentAndKeywordToAddFlow(orgId, departmentIds, keyword);
	}

	/**
	 * 查询集团下员工列表
	 *
	 * @param orgId             集团ID
	 * @param searchDepartments 组织ID列表
	 * @param keyword           用户名关键词
	 * @return
	 */
	@Override
	public List<FingerEntity> getUsersByOrgAndDepartmentAndUserName(Integer orgId, List<Integer> searchDepartments, String keyword) {
		if (orgId == null || searchDepartments == null) {
			return null;
		}
		SpringContextUtils.setPage();
		return mapper.selectUsersByOrgAndDepartmentAndUserName(orgId, searchDepartments, keyword);
	}

	@Override
	public boolean isManager(Integer userId) {
		if (userId == null) {
			return false;
		}
		int count = mapper.selectByOrgAndUserAndPerms(userId, 22);
		return count >= 1;
	}

	/**
	 * 查询指定组织列表下的'印章管理员'列表
	 *
	 * @param orgId         组织ID列表
	 * @param departmentIds 组织ID列表
	 * @return
	 */
	@Override
	public List<User> getKeepersByOrgAndDepartment(Integer orgId, List<Integer> departmentIds) {
		if (departmentIds == null || departmentIds.isEmpty()) {
			return null;
		}
		checkUserInfo();
		SpringContextUtils.setPage();
		return mapper.selectKeeperByOrgAndDepartment(orgId, departmentIds);

	}

	/**
	 * 查询指定组织列表下的'印章审计员'列表
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 组织ID列表
	 * @return
	 */
	@Override
	public List<User> getAuditorsByOrgAndDepartment(Integer orgId, List<Integer> departmentIds) {
		if (departmentIds == null || departmentIds.isEmpty()) {
			return null;
		}
		SpringContextUtils.setPage();
		return mapper.selectAuditorByOrgAndDepartment(orgId, departmentIds);
	}

	/**
	 * 查询组织下的员工列表
	 *
	 * @param orgId        集团ID
	 * @param departmentId 组织ID
	 * @param keyword      员工名称关键词
	 * @return
	 */
	@Override
	public List<OrganizationalEntity> getManagersByOrgAndDepartmentAndKeyword(Integer orgId, Integer departmentId, String keyword) {
		if (orgId == null) {
			return null;
		}
		return mapper.selectEmployeesByOrgAndDepartment(orgId, departmentId, keyword);
	}

	/**
	 * 查询指定组织下的员工列表
	 *
	 * @param orgId         集团ID
	 * @param departmentId  要查询的组织ID
	 * @param departmentIds 限定查询的组织ID范围
	 * @param keyword       员工名称关键词
	 * @return
	 */
	@Override
	public List<OrganizationalEntity> getManagersByOrgAndDepartmentsAndKeyword(Integer orgId, Integer departmentId, List<Integer> departmentIds, String keyword) {
		if (orgId == null) {
			return null;
		}
		return mapper.selectManagersByOrgAndDepartmentsAndKeyword(orgId, keyword, departmentId, departmentIds);
	}

	/**
	 * 查询指定账号的用户信息
	 *
	 * @param sysUserId 账号ID
	 * @return
	 */
	@Override
	public User getPreLoginAccount(Integer sysUserId) {
		if (sysUserId == null) {
			return null;
		}
		return mapper.selectPreLoginAccount(sysUserId);
	}

	/**
	 * 用户列表
	 *
	 * @param orgId   集团ID
	 * @param keyword 关键词
	 * @return
	 */
	@Override
	public List<User> getUserList(Integer orgId, String keyword) {
		if (orgId == null) {
			return null;
		}

		Example example = new Example(User.class);
		example.createCriteria().andIsNull("deleteDate")
				.andEqualTo("orgId", orgId);
		if (StringUtils.isNotBlank(keyword)) {
			Example.Criteria criteria = example.createCriteria().orLike("id", keyword)
					.orLike("userName", "%" + keyword + "%")
					.orLike("phone", "%" + keyword + "%");
			example.and(criteria);
		}

		List<User> users = mapper.selectByExample(example);
		return users;
	}

	/**
	 * 查询组织属主
	 *
	 * @param departmentId 组织ID
	 * @return 属主信息
	 */
	@Override
	public User getOwnerByDeparmtent(Integer departmentId) {
		Department department = departmentService.get(departmentId);
		if (department == null) {
			return null;
		}

		Integer orgId = department.getOrgId();
		Department root = departmentService.getRootByOrg(orgId);
		if (root == null) {
			return null;
		}

		Integer managerUserId = root.getManagerUserId();
		return get(managerUserId);
	}

	@Override
	public List<User> getByOrgAndDepartment(Integer orgId, List<Integer> departmentIds) {
		return mapper.selectByOrgAndDepartment(orgId, departmentIds);
	}
}
