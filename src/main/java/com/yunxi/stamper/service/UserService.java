package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.Org;
import com.yunxi.stamper.entity.SysUser;
import com.yunxi.stamper.entity.User;
import com.yunxi.stamper.entityVo.Employee;
import com.yunxi.stamper.entityVo.FingerEntity;
import com.yunxi.stamper.entityVo.OrganizationalEntity;
import com.yunxi.stamper.entityVo.UserVo;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/7 0007 9:07
 */
public interface UserService {
	void add(User user);

	/**
	 * 添加员工
	 *
	 * @param userName      用户名
	 * @param phone         手机号(登录名)
	 * @param roleIds       角色列表
	 * @param departmentIds 组织ID
	 */
	void addEmployee(SysUser sysUser, String userName, String phone, String remark, Boolean isUse, List<Integer> roleIds, List<Integer> departmentIds);

	/**
	 * 账号注册
	 *
	 * @param phone    手机号码
	 * @param password 密码
	 * @param org      注册组织
	 */
	void regUser(String phone, String password, Org org);

	/**
	 * 删除员工
	 *
	 * @param user 员工信息
	 */
	void delEmployee(User user);

	void update(User user);

	/**
	 * 更新员工信息
	 *
	 * @param user          源员工信息
	 * @param userName      新名称
	 * @param phone         新手机号
	 * @param roleIds       新角色列表
	 * @param departmentIds 新组织列表
	 */
	void updateEmployee(User user, String userName, String remark, Integer status, String phone, List<Integer> roleIds, List<Integer> departmentIds);

	/**
	 * 修改个人信息
	 *
	 * @param phone    手机号
	 * @param userName 用户名称
	 * @param remark   备注
	 * @param fileUUID 头像
	 */
	User updateOwner(String phone, String userName, String remark, String fileUUID);

	User get(Integer id);

	/**
	 * 用户信息
	 *
	 * @param id
	 * @return
	 */
	User getWithDel(Integer id);

	/**
	 * 员工列表
	 *
	 * @param orgId
	 * @return
	 */
	List<User> getByOrg(Integer orgId);

	/**
	 * 员工列表
	 *
	 * @param orgId
	 * @return
	 */
	List<UserVo> getByOrgId(Integer orgId);

	/**
	 * 员工列表
	 *
	 * @param orgId
	 * @param permsCode
	 * @return
	 */
	List<User> getByOrgAndPerms(Integer orgId, String permsCode);

	/**
	 * 员工列表
	 *
	 * @param departmentIds
	 * @return
	 */
	List<User> getByDepartment(List<Integer> departmentIds);

	/**
	 * 员工列表
	 *
	 * @param orgId
	 * @param name
	 * @return
	 */
	List<User> getUsersByKeyword(Integer orgId, String name);

	/**
	 * 用户信息
	 *
	 * @param orgId
	 * @param name
	 * @return
	 */
	User getByOrgAndName(Integer orgId, String name);

	/**
	 * 员工列表
	 *
	 * @param orgId
	 * @param roleCode
	 * @return
	 */
	List<User> getByOrgAndRole(Integer orgId, String roleCode);

	/**
	 * 员工列表
	 *
	 * @param orgId
	 * @return
	 */
	List<User> getEmps(Integer orgId);

	/**
	 * 查询指定组织、手机号码的用户信息
	 *
	 * @param orgId 组织ID
	 * @param phone 手机号码
	 * @return
	 */
	User getByOrgAndPhone(Integer orgId, String phone);

	/**
	 * 查询指定组织下的的用户ID列表
	 *
	 * @param departmentIds
	 * @return
	 */
	List<Integer> getIdsByDepartmentIds(Collection<Integer> departmentIds);

	/**
	 * 查询员工列表
	 *
	 * @param keyword       要查询的员工名称、手机号关键词
	 * @param departmentIds 要查询的组织
	 * @return
	 */
	List<Employee> getEmployeesByKeyword(Integer orgId, List<Integer> departmentIds, String keyword);

	/**
	 * 查询指定用户拥有的集团ID列表
	 *
	 * @param sysUserId 要查询的用户ID
	 * @return
	 */
	List<Integer> getOrgIdsBySysUserId(Integer sysUserId);

	/**
	 * 查询指定账户的用户列表
	 *
	 * @param sysUserId
	 * @return
	 */
	List<User> getBySysUserId(Integer sysUserId);

	/**
	 * 查询指定组织列表下的员工ID列表
	 *
	 * @param departmentIds 组织ID列表
	 * @return
	 */
	List<Integer> getUserIdsByDepartment(List<Integer> departmentIds);

	/**
	 * 查询指定集团下、指定组织列表下的员工列表
	 *
	 * @param orgId
	 * @param departmentIds
	 * @return
	 */
	List<User> getEmployeeForManagerByDepartments(@NotNull Integer orgId, List<Integer> departmentIds);

	/**
	 * 查询指定集团下、指定组织下、指定关键词的员工列表
	 *
	 * @param orgId        集团ID
	 * @param departmentId 父组织ID
	 * @param keyword      员工关键词
	 * @return
	 */
	List<OrganizationalEntity> getManagersByOrgAndParentAndKeyword(@NotNull Integer orgId, Integer departmentId, String keyword);

	/**
	 * 查询指定集团下员工ID列表
	 *
	 * @param orgId 集团ID
	 * @return
	 */
	List<Integer> getIdsByOrg(Integer orgId);

	/**
	 * 查询指定账号ID、指定集团的用户信息
	 *
	 * @param sysUserId 账号ID
	 * @param orgId     集团ID
	 * @return
	 */
	User getBySysUserAndOrg(Integer sysUserId, Integer orgId);


	/**
	 * 查询组织下审批人列表，用于添加审批流程
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 组织ID列表
	 * @param keyword       员工名称关键词
	 * @return
	 */
	List<OrganizationalEntity> getManagersByOrgAndParentAndKeywordToAddFlow(Integer orgId, List<Integer> departmentIds, String keyword);

	/**
	 * 查询集团下员工列表
	 *
	 * @param orgId             集团ID
	 * @param searchDepartments 组织ID列表
	 * @param keyword           用户名关键词
	 * @return
	 */
	List<FingerEntity> getUsersByOrgAndDepartmentAndUserName(Integer orgId, List<Integer> searchDepartments, String keyword);

	/**
	 * 用户是否拥有'审批权限'
	 *
	 * @param userId 用户ID
	 * @return
	 */
	boolean isManager(Integer userId);

	/**
	 * 查询指定组织列表下的'印章管理员'列表
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 组织ID列表
	 * @return
	 */
	List<User> getKeepersByOrgAndDepartment(Integer orgId, List<Integer> departmentIds);

	/**
	 * 查询指定组织列表下的'印章审计员'列表
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 组织ID列表
	 * @return
	 */
	List<User> getAuditorsByOrgAndDepartment(Integer orgId, List<Integer> departmentIds);

	/**
	 * 查询组织下的员工列表
	 *
	 * @param orgId        集团ID
	 * @param departmentId 组织ID
	 * @param keyword      员工名称关键词
	 * @return
	 */
	List<OrganizationalEntity> getManagersByOrgAndDepartmentAndKeyword(Integer orgId, Integer departmentId, String keyword);

	/**
	 * 查询指定组织下的员工列表
	 *
	 * @param orgId         集团ID
	 * @param departmentId  要查询的组织ID
	 * @param departmentIds 限定查询的组织ID范围
	 * @param keyword       员工名称关键词
	 * @return
	 */
	List<OrganizationalEntity> getManagersByOrgAndDepartmentsAndKeyword(Integer orgId, Integer departmentId, List<Integer> departmentIds, String keyword);

	/**
	 * 查询指定账号的用户信息
	 *
	 * @param sysUserId 账号ID
	 * @return
	 */
	User getPreLoginAccount(Integer sysUserId);

	/**
	 * 用户列表
	 *
	 * @param orgId   集团ID
	 * @param keyword 关键词
	 * @return
	 */
	List<User> getUserList(Integer orgId, String keyword);

	/**
	 * 查询组织属主
	 *
	 * @param departmentId 组织ID
	 * @return 属主信息
	 */
	User getOwnerByDeparmtent(Integer departmentId);

	List<User> getByOrgAndDepartment(Integer orgId, List<Integer> departmentIds);

}
