package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.Department;
import com.yunxi.stamper.entity.Position;
import com.yunxi.stamper.entity.User;
import com.yunxi.stamper.entityVo.*;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/8 0008 22:29
 */
public interface DepartmentService {
	Department get(Integer departmentId);

	void add(Department department);

	void del(Department department);

	void update(Department department);

	/**
	 * 删除组织
	 *
	 * @param department 组织信息
	 */
	void delDepartment(Department department);

	/**
	 * 增加组织
	 *
	 * @param orgId       集团ID
	 * @param name        组织名称
	 * @param remark      组织描述
	 * @param managerUser 组织负责人
	 * @param parent      父组织
	 * @param position    负责人职称
	 * @param type        组织类型
	 */
	void addDepartment(Integer orgId, String name, String remark, User managerUser, Department parent, Position position, Integer type);

	/**
	 * 更新组织
	 *
	 * @param department       组织信息
	 * @param name             新组织名称
	 * @param remark           新组织描述
	 * @param positionId       新负责人职称
	 * @param newManagerUserId 新负责人ID
	 * @param parent           新父组织
	 * @param type             新类型
	 */
	void updateDepartment(Department department, String name, String remark, Integer positionId, Integer newManagerUserId, Department parent, Integer type);

	/**
	 * 组织信息
	 *
	 * @param name 组织名称
	 * @return
	 */
	Department getByName(String name);

	/**
	 * 组织列表
	 *
	 * @param orgId 集团ID
	 * @return
	 */
	List<Department> getByOrg(Integer orgId);

	/**
	 * 组织列表
	 *
	 * @param orgId 集团ID
	 * @return
	 */
	List<Department> getByOrgId(Integer orgId);

	/**
	 * 组织ID列表
	 *
	 * @param orgId 集团ID
	 * @return
	 */
	List<Integer> getDepartmentIdByOrg(Integer orgId);

	/**
	 * 组织树
	 *
	 * @param orgId 集团ID
	 * @return
	 */
	List<DepartmentVo> getTreeByOrg(Integer orgId);

	/**
	 * 组织树
	 *
	 * @param orgId 集团ID
	 * @return
	 */
	List<DepartmentVo> getOrganizational(Integer orgId);

	/**
	 * 组织列表
	 *
	 * @param orgId  集团ID
	 * @param parent 父组织
	 */
	void getByOrgAndParent(Integer orgId, DepartmentVo parent);

	/**
	 * 组织ID列表
	 *
	 * @param orgId  组织ID
	 * @param userId 用户ID
	 * @return
	 */
	List<Integer> getDepartmentIdsByManager(Integer orgId, Integer userId);

	/**
	 * 组织信息
	 *
	 * @param orgId    集团ID
	 * @param parentId 父组织ID
	 * @param name     组织名称
	 * @return
	 */
	Department getByOrgIdAndParentIdAndName(Integer orgId, Integer parentId, String name);

	/**
	 * 组织列表
	 *
	 * @param orgId         组织集团ID
	 * @param managerUserId 负责人ID
	 * @return
	 */
	List<Department> getByOrgAndManager(Integer orgId, Integer managerUserId);

	/**
	 * 组织ID列表
	 *
	 * @param orgId         组织集团ID
	 * @param managerUserId 负责人ID
	 * @return
	 */
	List<Integer> getIdsByOrgAndManager(Integer orgId, Integer managerUserId);

	/**
	 * 组织ID列表
	 *
	 * @param parentDepartmentId 父组织ID
	 * @param type               组织类型 0:部门 1:公司 NULL:查询所有类型
	 * @return
	 */
	List<Integer> getChildrenIdsByOrgAndParentAndType(Integer orgId, Integer parentDepartmentId, Integer type);

	/**
	 * 组织ID列表
	 *
	 * @param departmentIds 组织ID列表
	 * @param type          组织类型 0:部门 1:公司 null:所有
	 * @return
	 */
	List<Integer> getChildrenIdsByOrgAndParentsAndType(Integer orgId, List<Integer> departmentIds, Integer type);

	/**
	 * 组织列表
	 *
	 * @param orgId  集团ID
	 * @param infoId 用户ID
	 * @return
	 */
	List<Department> getDepartmentsByOrgAndUser(Integer orgId, Integer infoId);

	/**
	 * 组织列表
	 *
	 * @param orgId    集团ID
	 * @param parentId 父组织ID
	 * @return
	 */
	List<OrganizationalEntity> getOrganizationalByOrgAndParentForOwner(Integer orgId, Integer parentId);

	/**
	 * 组织列表
	 *
	 * @param orgId               集团ID
	 * @param parentId            父组织ID
	 * @param visualDepartmentIds 可见组织ID列表
	 * @return
	 */
	List<OrganizationalEntity> getOrganizationalByOrgAndParentForUser(Integer orgId, Integer parentId, List<Integer> visualDepartmentIds);

	/**
	 * 组织ID列表
	 *
	 * @param userId
	 * @return
	 */
	List<Integer> getDepartmentIdsByAppSignet(Integer userId);

	/**
	 * 组织数量
	 *
	 * @param orgId  集团ID
	 * @param userId 员工iD
	 * @return
	 */
	int getCountByManagerUserId(Integer orgId, Integer userId);

	/**
	 * 组织列表
	 *
	 * @param orgId 集团ID
	 * @param type  组织类型
	 * @return
	 */
	List<Department> getByOrgAndType(Integer orgId, Integer type);

	/**
	 * 组织树
	 *
	 * @param orgId    集团ID
	 * @param parentId 父组织
	 * @return
	 */
	List<OrganizationalTree> getDepartmentTreeByOrgAndParent(Integer orgId, Integer parentId);

	/**
	 * 组织KV列表
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 要查询的组织ID列表
	 * @return
	 */
	List<DepartmentKV> getByOrgAndDepartmentIds(Integer orgId, List<Integer> departmentIds);

	/**
	 * 组织列表
	 *
	 * @param orgId  集团ID
	 * @param userId 用户ID
	 * @return
	 */
	List<Department> getByOrgAndUser(Integer orgId, Integer userId);

	/**
	 * 组织列表
	 *
	 * @param type 类型
	 * @param name 组织名称
	 * @return
	 */
	List<Department> getByTypeAndName(Integer type, String name);

	/**
	 * 组织信息
	 *
	 * @param orgId 集团ID
	 * @return
	 */
	Department getRootByOrg(Integer orgId);

	/**
	 * 组织列表
	 *
	 * @param orgId      集团ID
	 * @param positionId 职称ID
	 * @return
	 */
	List<Department> getByOrgAndPosition(Integer orgId, Integer positionId);

	/**
	 * 组织负责人信息
	 *
	 * @param departmentId 组织ID
	 * @param level        1:当前组织  2:上级组织  3:上上级组织  ..
	 * @return
	 */
	User getManagerByDepartmentAndTopLevel(Integer departmentId, Integer level);

	/**
	 * 组织信息
	 *
	 * @param childrenDepartmentId 要查询的组织ID
	 * @return
	 */
	Department getCompanyByChildrenId(Integer childrenDepartmentId);

	/**
	 * 组织树
	 *
	 * @param orgId    集团ID
	 * @param parentId 父组织ID
	 * @param type     组织类型
	 * @return
	 */
	List<OrganizationalTree> getOrganizationalByOrgAndParentAndType(Integer orgId, Integer parentId, Integer type);

	/**
	 * 组织列表
	 *
	 * @param orgId    集团ID
	 * @param parentId 父组织ID
	 * @return
	 */
	List<OrganizationalEntity> getOrganizationalByOrgAndParentToAddFlow(Integer orgId, Integer parentId);

	/**
	 * 组织列表
	 *
	 * @param orgId    集团ID
	 * @param parentId 父组织ID
	 * @param type     组织类型
	 * @return
	 */
	List<OrganizationalEntity> getOrganizationalByOrgAndParentToApplicationSignet(Integer orgId, Integer parentId, Integer type);

	/**
	 * 查询数据库中所有组织列表信息
	 *
	 * @return
	 */
	List<Department> getAll();

	/**
	 * 以level为top层级,查询0,1,2...level各层级的主管列表,不存在的主管id为空
	 *
	 * @param departmentId 基点,组织id
	 * @param topLevel     top层级
	 * @return 各层级组织主管列表
	 */
	List<User> getManagersByDepartmentToTopLevel(Integer departmentId, Integer topLevel);
}