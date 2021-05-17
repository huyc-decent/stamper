package com.yunxi.stamper.mapper;


import com.yunxi.stamper.entity.User;
import com.yunxi.stamper.entityVo.Employee;
import com.yunxi.stamper.entityVo.FingerEntity;
import com.yunxi.stamper.entityVo.OrganizationalEntity;
import com.yunxi.stamper.entityVo.UserVo;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

@Component
public interface UserMapper extends MyMapper<User> {

	//查询公司下拥有code权限的用户列表
	List<User> selectByOrgAndPerms(Integer orgId, String permsCode);

	//查询公司下所有员工列表
	List<UserVo> selectByOrgId(Integer orgId);

	//查询该公司拥有管理员权限的用户列表
	List<User> selectByOrgAndRole(Integer orgId, String roleCode);

	//获取公司所有员工
	List<User> getEmpList(Integer orgId);

	/**
	 * 查询指定组织、手机号码的用户信息
	 *
	 * @param orgId 组织ID
	 * @param phone 手机号码
	 * @return
	 */
	User selectByOrgAndPhone(Integer orgId, String phone);

	/**
	 * 查询指定组织下的的用户ID列表
	 *
	 * @param departmentIds 组织ID列表
	 * @return
	 */
	List<Integer> selectIdsByDepartmentIds(Collection<Integer> departmentIds);


	/**
	 * 查询指定集团ID下的员工ID列表
	 *
	 * @param orgId
	 * @return
	 */
	List<Integer> selectIdsByOrg(Integer orgId);

	/**
	 * 查询员工列表
	 *
	 * @param keyword       要查询的员工名称、手机号关键词
	 * @param departmentIds 要查询的组织列表
	 * @return
	 */
	List<Employee> selectEmployeesByKeyword(Integer orgId, List<Integer> departmentIds, String keyword);

	/**
	 * 查询指定组织列表下的员工信息
	 *
	 * @param departmentIds 要查询的组织ID列表
	 * @return
	 */
	List<User> selectByDepartmentIds(List<Integer> departmentIds);

	/**
	 * 查询指定用户拥有的集团ID列表
	 *
	 * @param sysUserId 要查询的用户ID
	 * @return
	 */
	List<Integer> selectOrgIdsBySysUserId(Integer sysUserId);

	/**
	 * 查询指定组织列表下的员工ID列表
	 *
	 * @param departmentIds
	 * @return
	 */
	List<Integer> selectUserIdsByDepartmentIds(List<Integer> departmentIds);

	/**
	 * 查询指定集团下、指定组织列表下的员工列表
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 组织ID
	 * @return
	 */
	List<User> selectEmployeeForManagerByDepartments(@NotNull Integer orgId, @NotNull List<Integer> departmentIds);

	/**
	 * 查询指定集团下、指定组织下、指定关键词的员工列表
	 *
	 * @param orgId        集团ID
	 * @param departmentId 父组织ID
	 * @param keyword      员工关键词
	 * @return
	 */
	List<OrganizationalEntity> selectManagersByOrgAndParentAndKeyword(@NotNull Integer orgId, Integer departmentId, String keyword);

	/**
	 * 查询指定账号ID、指定集团的用户信息
	 *
	 * @param sysUserId 账号ID
	 * @param orgId     集团ID
	 * @return
	 */
	User selectBySysUserAndOrg(Integer sysUserId, Integer orgId);

	/**
	 * 查询组织下的审批人列表
	 *
	 * @param orgId        集团ID
	 * @param departmentId 组织ID
	 * @param keyword      员工名称关键词
	 * @return
	 */
	List<OrganizationalEntity> selectEmployeesByOrgAndDepartment(@NotNull Integer orgId, Integer departmentId, String keyword);

	/**
	 * 查询组织下审批人列表，用于添加审批流程
	 * @param orgId 集团ID
	 * @param departmentIds 组织ID列表
	 * @param keyword 员工名称关键词
	 * @return
	 */
	List<OrganizationalEntity> selectManagersByOrgAndParentAndKeywordToAddFlow(Integer orgId, List<Integer> departmentIds, String keyword);

	/**
	 * 查询集团下员工列表
	 * @param orgId 集团ID
	 * @param searchDepartments 组织ID列表
	 * @param keyword 用户名关键词
	 * @return
	 */
	List<FingerEntity> selectUsersByOrgAndDepartmentAndUserName(Integer orgId, List<Integer> searchDepartments, String keyword);

	/**
	 * 查询用户是否拥有指定权限
	 * @param userId 用户ID
	 * @param permsId 权限ID
	 * @return
	 */
	int selectByOrgAndUserAndPerms(Integer userId, Integer permsId);

	/**
	 * 查询指定组织列表下的'印章管理员'列表
	 *
	 * @param orgId 组织ID列表
	 * @param departmentIds 组织ID列表
	 * @return
	 */
	List<User> selectKeeperByOrgAndDepartment(Integer orgId, List<Integer> departmentIds);

	/**
	 * 查询指定组织列表下的'印章审计员'列表
	 * @param orgId 集团ID
	 * @param departmentIds 组织ID列表
	 * @return
	 */
	List<User> selectAuditorByOrgAndDepartment(Integer orgId, List<Integer> departmentIds);

	/**
	 * 查询指定组织下的员工列表
	 * @param orgId 集团ID
	 * @param departmentId 要查询的组织ID
	 * @param departmentIds 限定查询的组织ID范围
	 * @param keyword 员工名称关键词
	 * @return
	 */
	List<OrganizationalEntity> selectManagersByOrgAndDepartmentsAndKeyword(Integer orgId, String keyword, Integer departmentId, List<Integer> departmentIds);

	/**
	 * 查询指定账号的用户信息
	 * @param sysUserId
	 * @return
	 */
	User selectPreLoginAccount(Integer sysUserId);

	List<User> selectByOrgAndDepartment(Integer orgId, List<Integer> departmentIds);
}