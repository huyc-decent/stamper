package com.yunxi.stamper.mapper;


import com.yunxi.stamper.entity.Department;
import com.yunxi.stamper.entityVo.*;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Component
public interface DepartmentMapper extends MyMapper<Department> {
	//查询部门下所有子部门
	List<DepartmentVo> selectByParent(Integer departmentId);

	//查询公司下1级部门
	List<DepartmentVo> selectTopDepartMentsByOrg(Integer orgId);

	//查询公司下(+父级组织)组织列表
	List<DepartmentVo> selectByOrgAndParent(Integer orgId, Integer parentId);

	/**
	 * 查询用户管理的部门ID列表
	 *
	 * @param orgId
	 * @param userId
	 * @return
	 */
	List<Integer> selectDepartmentIdsByManager(Integer orgId, Integer userId);

	/**
	 * 查询指定集团下、指定父组织下、指定名称的组织信息
	 *
	 * @param orgId
	 * @param parentId
	 * @param name
	 * @return
	 */
	Department selectByOrgIdAndParentIdAndName(Integer orgId, Integer parentId, String name);

	/**
	 * 查询指定集团下、指定父组织ID下的子组织列表
	 *
	 * @param orgId    集团公司ID
	 * @param parentId 父组织ID
	 * @return
	 */
	List<OrganizationalTree> selectOrganizationalTreeByOrgAndParent(Integer orgId, Integer parentId);

	/**
	 * 查询指定集团下、指定父组织下、指定类型的组织列表
	 *
	 * @param orgId              集团ID
	 * @param parentDepartmentId 父组织ID
	 * @param type               组织类型 0:部门 1:公司 NULL:查询所有类型
	 * @return
	 */
	List<Integer> selectByOrgAndParentAndType(Integer orgId, Integer parentDepartmentId, Integer type);

	/**
	 * 查询指定集团、指定负责人负责的组织ID列表
	 *
	 * @param orgId         组织集团ID
	 * @param managerUserId 负责人ID
	 * @return
	 */
	List<Integer> selectIdsByOrgAndManager(Integer orgId, Integer managerUserId);

	/**
	 * 删除指定组织ID列表
	 *
	 * @param childrenIds
	 */
	void deleteByDepartmentIds(List<Integer> childrenIds);

	/**
	 * 查询用户所属组织列表
	 *
	 * @param orgId  集团ID
	 * @param infoId 用户ID
	 * @return
	 */
	List<Department> selectDepartmentsByOrgAndUser(Integer orgId, Integer infoId);

	/**
	 * 属主：查询集团、父组织下组织列表
	 *
	 * @param orgId    集团ID
	 * @param parentId 父组织ID
	 * @return
	 */
	List<OrganizationalEntity> selectOrganizationalByOrgAndParentForOwner(Integer orgId, Integer parentId);

	/**
	 * 非属主：查询集团、父组织下组织列表(仅可见组织)
	 *
	 * @param orgId               集团ID
	 * @param parentId            父组织ID
	 * @param visualDepartmentIds 可见组织ID列表
	 * @return
	 */
	List<OrganizationalEntity> selectOrganizationalByOrgAndParentForUser(Integer orgId, Integer parentId, List<Integer> visualDepartmentIds);

	int selectCountByManagerUserId(Integer orgId, Integer userId);

	/**
	 * 查询指定集团下、指定组织类型的组织列表
	 *
	 * @param orgId 集团ID
	 * @param type  组织类型
	 * @return
	 */
	List<Department> selectDepartmentsByOrgAndType(Integer orgId, Integer type);

	/**
	 * 查询指定集团下、指定组织ID的信息
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 要查询的组织ID列表
	 * @return
	 */
	List<DepartmentKV> selectByOrgAndDepartmentIds(Integer orgId, List<Integer> departmentIds);

	/**
	 * 查询指定集团下的所有组织ID列表
	 *
	 * @param orgId
	 * @return
	 */
	List<Integer> selectDepartmentIdsByOrg(Integer orgId);

	/**
	 * 查询指定集团下、指定用户关联的组织信息列表
	 *
	 * @param orgId  集团ID
	 * @param userId 用户ID
	 * @return
	 */
	List<Department> selectByOrgAndUser(Integer orgId, Integer userId);

	/**
	 * 查询指定集团下、指定职称的组织列表
	 *
	 * @param orgId
	 * @param positionId
	 * @return
	 */
	List<Department> selectByOrgAndPosition(Integer orgId, Integer positionId);

	/**
	 * 查询指定code为前缀的子组织ID列表
	 *
	 * @param orgId      集团ID
	 * @param type       组织类型
	 * @param parentCode code前缀
	 * @return
	 */
	List<Integer> selectChildrenIdsByOrgAndTypeAndPrefixCode(@NotNull Integer orgId, Integer type, @NotEmpty String parentCode);

	/**
	 * 查询子组织列表
	 *
	 * @param orgId    集团ID
	 * @param parentId 父组织ID
	 * @param type     组织类型
	 * @return
	 */
	List<OrganizationalTree> selectOrganizationalTreeByOrgAndParentAndType(Integer orgId, Integer parentId, Integer type);

	/**
	 * 查询子组织列表
	 *
	 * @param orgId    集团ID
	 * @param parentId 父组织ID
	 * @return
	 */
	List<OrganizationalEntity> selectOrganizationalByOrgAndParentToAddFlow(@NotNull Integer orgId, Integer parentId);

	/**
	 * 查询子组织列表
	 *
	 * @param orgId    集团ID
	 * @param parentId 父组织ID
	 * @param type     组织类型
	 * @return
	 */
	List<OrganizationalEntity> selectOrganizationalByOrgAndParentToApplicationSignet(Integer orgId, Integer parentId, Integer type);

	/**
	 * 查询子组织ID列表
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 父组织ID列表
	 * @param type          组织类型
	 * @return
	 */
	List<Integer> selectChildrenIdsByOrgAndParentsAndType(Integer orgId, Set<Integer> departmentIds, Integer type);

	/**
	 * 查询以父ID链表为前缀的子公司ID列表(不包含子部门)
	 *
	 * @param orgId      集团ID
	 * @param parentCode 父ID链表前缀字符串
	 * @return
	 */
	List<Integer> selectCompanyIdsByOrgAndPrefixCode(@NotNull Integer orgId, @NotEmpty String parentCode);

	/**
	 * 查询子组织信息列表
	 *
	 * @param orgId    集团ID
	 * @param parentId 父组织ID
	 * @param type     组织类型
	 * @return
	 */
	List<Department> selectDepartmentsByOrgAndParentAndType(Integer orgId, Integer parentId, Integer type);

	/**
	 * 查询集团下组织ID列表
	 *
	 * @param orgId 集团ID
	 * @param type  类型
	 * @return
	 */
	List<Integer> selectChildrenIdsByOrgAndType(Integer orgId, Integer type);
}