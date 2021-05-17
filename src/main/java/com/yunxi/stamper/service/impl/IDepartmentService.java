package com.yunxi.stamper.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.other.RedisGlobal;
import com.yunxi.stamper.logger.threadLocal.LocalHandle;
import com.yunxi.stamper.sys.context.SpringContextUtils;
import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.*;
import com.yunxi.stamper.entityVo.*;
import com.yunxi.stamper.mapper.DepartmentMapper;
import com.yunxi.stamper.service.*;
import com.yunxi.stamper.base.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/8 0008 22:29
 */
@Service
public class IDepartmentService extends BaseService implements DepartmentService {

	@Autowired
	private DepartmentMapper mapper;
	@Autowired
	private RelateDepartmentUserService relateDepartmentUserService;
	@Autowired
	private UserService userService;
	@Autowired
	@Lazy
	private UserInfoService userInfoService;
	@Autowired
	private SignetService signetService;

	@Override
	@Transactional
	public void update(Department department) {
		int updateCount = 0;
		if (department != null && department.getId() != null) {
			updateCount = mapper.updateByPrimaryKey(department);
		}
		if (updateCount != 1) {
			throw new PrintException("部门更新失败");
		}
		redisUtil.del(RedisGlobal.DEPARTMENT_BY_ID + department.getId());
	}

	/**
	 * 删除指定组织
	 *
	 * @param department 部门信息
	 */
	@Override
	@Transactional
	public void delDepartment(Department department) {
		if (department == null || department.getId() == null) {
			return;
		}
		//删除组织列表&子孙组织
		List<Integer> childrenIds = getChildrenIdsByOrgAndParentAndType(department.getOrgId(), department.getId(), null);
		childrenIds.add(department.getId());
		mapper.deleteByDepartmentIds(childrenIds);

		//删除组织列表的缓存信息
		for (Integer childrenId : childrenIds) {
			redisUtil.del(RedisGlobal.DEPARTMENT_BY_ID + childrenId);
		}

		//删除该组织ID列表的用户组织关联信息
		relateDepartmentUserService.delByDepartmentIds(childrenIds);

		//刷新该组织下员工信息
		userInfoService.refreshByOrg(department.getOrgId());

		//将该组织列表下所有设备组织迁移至父组织下
		Integer parentId = department.getParentId();
		Department parent = get(parentId);
		List<Signet> signets = signetService.getByDepartment(childrenIds);
		if (signets != null) {
			for (Signet signet : signets) {
				signet.setDepartmentId(parent.getId());
				signet.setDepartmentName(parent.getName());
				signetService.update(signet);
			}
		}
	}

	@Override
	@Transactional
	public void del(Department department) {
		int delCount = 0;
		if (department != null && department.getId() != null) {
			department.setDeleteDate(new Date());
			delCount = mapper.updateByPrimaryKey(department);
		}
		if (delCount != 1) {
			throw new PrintException("部门删除失败");
		}

		//删除redis
		redisUtil.del(RedisGlobal.DEPARTMENT_BY_ID + department.getId());
	}

	@Override
	@Transactional
	public void add(Department department) {
		int addCount = 0;
		if (department != null) {
			department.setCreateDate(new Date());
			addCount = mapper.insert(department);
		}
		if (addCount != 1) {
			throw new PrintException("组织添加失败");
		}
	}

	/**
	 * 查询指定名称的部门实例
	 *
	 * @param name 组织名称
	 * @return 结果
	 */
	@Override
	public Department getByName(String name) {
		if (StringUtils.isBlank(name)) {
			return null;
		}
		Example example = new Example(Department.class);
		example.createCriteria().andEqualTo("name", name)
				.andIsNull("deleteDate");
		return mapper.selectOneByExample(example);
	}

	/**
	 * 查询该部门的子部门
	 */
	private void getByParentV2(DepartmentVo departmentVo) {
		if (departmentVo == null) {
			return;
		}
		List<DepartmentVo> vos = mapper.selectByParent(departmentVo.getId());
		if (vos == null || vos.isEmpty()) {
			return;
		}
		departmentVo.setChildrens(vos);
		for (DepartmentVo vo : vos) {
			getByParentV2(vo);
		}
	}

	/**
	 * 查询该公司下的组织树
	 */
	@Override
	public List<DepartmentVo> getOrganizational(Integer orgId) {
		if (orgId != null) {
			//查询公司下(+父级组织)组织列表
			List<DepartmentVo> departmentVos = mapper.selectByOrgAndParent(orgId, null);
			if (departmentVos != null && departmentVos.size() > 0) {
				for (DepartmentVo departmentVo : departmentVos) {
					getByOrgAndParent(orgId, departmentVo);
				}
			}
			return departmentVos;
		}
		return null;
	}

	//查询公司下(+父级组织)组织列表
	@Override
	public void getByOrgAndParent(Integer orgId, DepartmentVo parent) {
		if (orgId == null || parent == null) {
			return;
		}

		List<DepartmentVo> departmentVos = mapper.selectByOrgAndParent(orgId, parent.getId());
		if (departmentVos == null || departmentVos.isEmpty()) {
			return;
		}
		parent.setChildrens(departmentVos);

		for (DepartmentVo departmentVo : departmentVos) {
			getByOrgAndParent(orgId, departmentVo);
		}
	}

	/**
	 * 查询该公司下的部门树
	 */
	@Override
	public List<DepartmentVo> getTreeByOrg(Integer orgId) {
		if (orgId == null) {
			return null;
		}
		//查询公司下的1级部门
		List<DepartmentVo> topDepartments = mapper.selectTopDepartMentsByOrg(orgId);
		if (topDepartments == null || topDepartments.isEmpty()) {
			return topDepartments;
		}

		for (DepartmentVo dv : topDepartments) {
			getByParentV2(dv);
		}
		return topDepartments;
	}

	/**
	 * 查询公司下所有部门id列表
	 *
	 * @param orgId 组织ID
	 * @return 结果
	 */
	@Override
	public List<Integer> getDepartmentIdByOrg(Integer orgId) {
		if (orgId == null) {
			return null;
		}
		return mapper.selectDepartmentIdsByOrg(orgId);
	}

	/**
	 * 部门管理员,查看所有子级部门
	 *
	 * @param orgId 组织ID
	 * @return 结果
	 */
	@Override
	public List<Department> getByOrgId(Integer orgId) {
		if (orgId == null) {
			return null;
		}
		Example example = new Example(Department.class);
		example.createCriteria()
				.andIsNull("deleteDate")
				.andEqualTo("orgId", orgId);
		return mapper.selectByExample(example);
	}

	/**
	 * 查询公司下(包含子公司)所有部门id集合
	 *
	 * @param orgId 组织ID
	 * @return 结果
	 */
	@Override
	public List<Department> getByOrg(Integer orgId) {
		if (orgId == null) {
			return null;
		}
		Example example = new Example(Department.class);
		example.createCriteria().andIsNull("deleteDate")
				.andEqualTo("orgId", orgId);
		return mapper.selectByExample(example);
	}

	@Override
	public Department get(Integer departmentId) {
		if (departmentId == null) {
			return null;
		}

		String key = RedisGlobal.DEPARTMENT_BY_ID + departmentId;
		Object departmentRedis = redisUtil.get(key);
		if (departmentRedis != null) {
			return (Department) departmentRedis;
		}

		Example example = new Example(Department.class);
		example.createCriteria().andEqualTo("id", departmentId)
				.andIsNull("deleteDate");
		Department department = mapper.selectOneByExample(example);

		if (department != null) {
			redisUtil.set(key, department, RedisGlobal.DEPARTMENT_BY_ID_TIMEOUT);
		}
		return department;
	}

	/**
	 * 查询用户管理的部门ID列表
	 *
	 * @param orgId  组织ID
	 * @param userId 用户ID
	 * @return 结果
	 */
	@Override
	public List<Integer> getDepartmentIdsByManager(Integer orgId, Integer userId) {
		return mapper.selectDepartmentIdsByManager(orgId, userId);
	}

	/**
	 * 查询指定集团下、指定父组织下、指定名称的组织信息
	 *
	 * @param orgId    集团ID
	 * @param parentId 父组织ID
	 * @param name     组织名称
	 * @return 结果
	 */
	@Override
	public Department getByOrgIdAndParentIdAndName(Integer orgId, Integer parentId, String name) {
		if (orgId == null || StringUtils.isBlank(name)) {
			return null;
		}
		return mapper.selectByOrgIdAndParentIdAndName(orgId, parentId, name);
	}

	/**
	 * 增加组织
	 *
	 * @param name        组织名称
	 * @param remark      组织简介
	 * @param managerUser 组织负责人
	 * @param parent      父组织
	 * @param type        组织类型 0:部门 1:公司
	 */
	@Override
	@Transactional
	public void addDepartment(Integer orgId, String name, String remark, User managerUser, Department parent, Position position, Integer type) {
		Department department = new Department();
		department.setName(name);
		department.setRemark(remark);
		department.setType(type);
		department.setOrgId(orgId);
		if (managerUser != null) {
			department.setManagerUserId(managerUser.getId());
		}

		if (parent != null) {
			//设置父组织ID
			department.setParentId(parent.getId());

			//设置父组织ID链表
			department.setParentCode(CommonUtils.generatorParentCode(parent));

			Integer level = parent.getLevel();
			if (level != null) {
				department.setLevel(level + 1);
			} else {
				department.setLevel(1);
			}
		} else if (type != 2) {
			throw new PrintException("父组织不能为空");
		}

		if (position != null) {
			department.setPositionId(position.getId());
		}
		add(department);
		LocalHandle.setNewObj(department);
		LocalHandle.complete("新增组织");

		if (managerUser != null) {
			//添加组织-员工关联信息
			RelateDepartmentUser rdu = relateDepartmentUserService.get(managerUser.getId(), department.getId());
			if (rdu == null) {
				rdu = new RelateDepartmentUser();
				rdu.setUserId(managerUser.getId());
				rdu.setDepartmentId(department.getId());
				relateDepartmentUserService.add(rdu);
			}
		}

		userInfoService.refreshByOrg(department.getOrgId());
	}

	/**
	 * 查询指定组织ID下、指定负责人负责的组织列表
	 *
	 * @param orgId         组织集团ID
	 * @param managerUserId 负责人ID
	 * @return 结果
	 */
	@Override
	public List<Department> getByOrgAndManager(Integer orgId, Integer managerUserId) {
		if (orgId == null || managerUserId == null) {
			return null;
		}
		Example example = new Example(Department.class);
		example.createCriteria().andIsNull("deleteDate")
				.andEqualTo("orgId", orgId)
				.andEqualTo("managerUserId", managerUserId);
		return mapper.selectByExample(example);
	}

	/**
	 * 查询指定集团、指定负责人负责的组织ID列表(包含负责组织的子组织列表)
	 *
	 * @param orgId         组织集团ID
	 * @param managerUserId 负责人ID
	 * @return 结果
	 */
	@Override
	public List<Integer> getIdsByOrgAndManager(Integer orgId, Integer managerUserId) {
		if (orgId == null || managerUserId == null) {
			return null;
		}
		List<Integer> res = new ArrayList<>();
		List<Integer> managerDepartmentIds = mapper.selectIdsByOrgAndManager(orgId, managerUserId);
		//查询下属组织ID列表
		if (managerDepartmentIds == null || managerDepartmentIds.isEmpty()) {
			return res;
		}
		res.addAll(managerDepartmentIds);
		for (Integer departmentId : managerDepartmentIds) {
			List<Integer> childrenIds = getChildrenIdsByOrgAndParentAndType(orgId, departmentId, null);
			if (childrenIds != null && !childrenIds.isEmpty()) {
				res.addAll(childrenIds);
			}
		}
		return res;
	}

	/**
	 * 查询父组织下指定类型的子组织ID(包含子孙组织)
	 * 查询子孙组织时，都是以父组织ID为前缀进行匹配，而不是递归查询
	 *
	 * @param orgId    集团ID
	 * @param parentId 父组织ID
	 * @param type     组织类型 0:部门  1:公司 NULL：查询所有类型
	 * @return 结果
	 */
	@Override
	public List<Integer> getChildrenIdsByOrgAndParentAndType(Integer orgId, Integer parentId, Integer type) {
		if (orgId == null) {
			return null;
		}
		checkUserInfo();
		List<Integer> departmentIds = null;

		/*查询全公司*/
		if (parentId == null) {
			departmentIds = mapper.selectChildrenIdsByOrgAndType(orgId, type);
			return departmentIds == null ? new ArrayList<>() : departmentIds;
		}

		/*查询父组织*/
		Department parent = get(parentId);
		if (parent == null) {
			return null;
		}


		/*level 组织层级 0:集团层级*/
		Integer level = parent.getLevel();

		/*type 组织类型 0:部门 1:公司 2:集团*/
		Integer parentType = parent.getType();

		if ((level != null && level == 0) || (parentType != null && parentType == 2)) {
			/*查询集团组织下所有组织ID列表*/
			departmentIds = mapper.selectChildrenIdsByOrgAndType(orgId, type);

			return departmentIds == null ? new ArrayList<>() : departmentIds;
		}

		/*组装父组织ID前缀*/
		String parentCode = getPreFixeByDepartment(parent);

		/*查询所有组织类型*/
		if (type == null) {
			departmentIds = mapper.selectChildrenIdsByOrgAndTypeAndPrefixCode(orgId, null, parentCode);
		}

		/*仅查询部门类型*/
		else if (type == 0) {

			/*父组织是部门类型*/
			if (parentType == 0) {
				/*查询子组织(必定是部门类型)*/
				departmentIds = mapper.selectChildrenIdsByOrgAndTypeAndPrefixCode(orgId, type, parentCode);
			}

			/*父组织是公司类型*/
			else {
				/*查询1级子部门*/
				List<Department> firstDepartments = mapper.selectDepartmentsByOrgAndParentAndType(orgId, parentId, 0);

				/*查询1级部门的子组织ID列表*/
				if (firstDepartments != null && firstDepartments.size() > 0) {
					departmentIds = new ArrayList<>();
					for (Department department : firstDepartments) {
						departmentIds.add(department.getId());
						parentCode = getPreFixeByDepartment(department);

						List<Integer> ids = mapper.selectChildrenIdsByOrgAndTypeAndPrefixCode(orgId, 0, parentCode);
						if (ids == null || ids.isEmpty()) {
							continue;
						}
						departmentIds.addAll(ids);
					}
				}
			}
		}

		/*仅查询公司类型*/
		else if (type == 1) {
			departmentIds = mapper.selectCompanyIdsByOrgAndPrefixCode(orgId, parentCode);
		}
		return departmentIds == null ? new ArrayList<>() : departmentIds;
	}

	/**
	 * 根据父组织信息，生成MYSQL可查询的父组织ID链表前缀字符串
	 *
	 * @param parent 父节点
	 * @return 结果
	 */
	private String getPreFixeByDepartment(Department parent) {
		String parentCode = parent.getParentCode();
		if (StringUtils.isNotBlank(parentCode)) {
			List<ParentCode> parentCodes = JSONObject.parseArray(parentCode, ParentCode.class);
			parentCodes.add(new ParentCode(parent));
			parentCode = JSONObject.toJSONString(parentCodes);
		} else {
			parentCode = "[" + JSONObject.toJSONString(new ParentCode(parent));
		}
		parentCode = parentCode.substring(0, parentCode.length() - 1);
		return parentCode;
	}

	/**
	 * 更新组织信息
	 *
	 * @param department       要更新的原组织
	 * @param name             新名称
	 * @param remark           新简介
	 * @param positionId       新称谓
	 * @param newManagerUserId 新主管
	 * @param parent           新父组织ID
	 * @param type             新组织类型
	 */
	@Override
	@Transactional
	public void updateDepartment(Department department, String name, String remark, Integer positionId, Integer newManagerUserId, Department parent, Integer type) {
		if (department == null) {
			return;
		}

		department.setName(name);
		department.setRemark((StringUtils.isBlank(remark) || "undefined".equalsIgnoreCase(remark) || "null".equalsIgnoreCase(remark)) ? null : remark);
		department.setPositionId(positionId);
		//更新原负责人-组织关联信息
		Integer oldManagerUserId = department.getManagerUserId();

		if (!CommonUtils.isEquals(oldManagerUserId, newManagerUserId)) {
			//更新原负责人信息
//			if (oldManagerUserId != null)
//				relateDepartmentUserService.del(department.getId(), department.getManagerUserId());

			//更新新负责人信息
			if (newManagerUserId != null) {
				//查询是否存在，不存在就添加
				RelateDepartmentUser departmentUser = relateDepartmentUserService.get(newManagerUserId, department.getId());
				if (departmentUser == null) {
					departmentUser = new RelateDepartmentUser();
					departmentUser.setUserId(newManagerUserId);
					departmentUser.setDepartmentId(department.getId());
					relateDepartmentUserService.add(departmentUser);
				}
			}
		}

		department.setManagerUserId(newManagerUserId);
		Integer oldParentId = department.getParentId();
		if (parent != null) {
			department.setParentId(parent.getId());
			department.setParentCode(CommonUtils.generatorParentCode(parent));
		} else {
			if (type != 2) {
				throw new PrintException("父组织不能为空");
			}
		}

		department.setType(type);
		update(department);
		LocalHandle.setNewObj(department);
		LocalHandle.complete("更新组织");
		/*
		 * 如果父组织发生改变,修改该组织下所有子组织列表的parentCode属性
		 */
		if (!CommonUtils.isEquals(department.getParentId(), oldParentId)) {
			int recursionNum = 1000;
			recursion_updateChildrensParentCode(department, recursionNum);
		}

		//刷新该集团员工的缓存信息
		userInfoService.refreshByOrg(department.getOrgId());
	}

	/**
	 * 递归更新子组织列表的ParentCode属性
	 *
	 * @param parent       父组织
	 * @param recursionNum 递归极限次数,防止程序死循环
	 */
	private void recursion_updateChildrensParentCode(Department parent, int recursionNum) {
		recursionNum--;
		if (recursionNum <= 0) {
			throw new RuntimeException("系统DG出现异常，请联系管理员解决");
		}

		List<Integer> childrenIds = mapper.selectByOrgAndParentAndType(parent.getOrgId(), parent.getId(), null);
		if (childrenIds == null || childrenIds.isEmpty()) {
			return;
		}

		for (Integer childrenId : childrenIds) {
			Department department = get(childrenId);
			department.setParentCode(CommonUtils.generatorParentCode(parent));
			department.setLevel(parent.getLevel() + 1);
			update(department);
			recursion_updateChildrensParentCode(department, recursionNum);
		}
	}

	/**
	 * 查询指定组织ID列表下、指定类型的子组织ID
	 *
	 * @param departmentIds 组织ID列表
	 * @param type          组织类型 0:部门 1:公司 null:所有
	 * @return 结果
	 */
	@Override
	public List<Integer> getChildrenIdsByOrgAndParentsAndType(Integer orgId, List<Integer> departmentIds, Integer type) {
		List<Integer> pools = new LinkedList<>();

		if (departmentIds == null || departmentIds.isEmpty()) {
			return pools;
		}
		for (Integer departmentId : departmentIds) {
			/*查询子孙ID列表*/
			List<Integer> childrenIds = getChildrenIdsByOrgAndParentAndType(orgId, departmentId, type);
			if (childrenIds != null && !childrenIds.isEmpty()) {
				pools.addAll(childrenIds);
			}
		}
		return pools;
	}

	/**
	 * 查询用户所属组织列表
	 *
	 * @param orgId  集团ID
	 * @param infoId 用户ID
	 * @return 结果
	 */
	@Override
	public List<Department> getDepartmentsByOrgAndUser(Integer orgId, Integer infoId) {
		if (orgId == null || infoId == null) {
			return null;
		}
		SpringContextUtils.setPage();
		return mapper.selectDepartmentsByOrgAndUser(orgId, infoId);
	}

	/**
	 * 属主：查询集团、父组织下组织列表
	 *
	 * @param orgId    集团ID
	 * @param parentId 父组织ID
	 * @return 结果
	 */
	@Override
	public List<OrganizationalEntity> getOrganizationalByOrgAndParentForOwner(Integer orgId, Integer parentId) {
		if (orgId == null) {
			return null;
		}
		return mapper.selectOrganizationalByOrgAndParentForOwner(orgId, parentId);
	}

	/**
	 * 非属主：查询集团、父组织下组织列表(仅可见组织)
	 *
	 * @param orgId               集团ID
	 * @param parentId            父组织ID
	 * @param visualDepartmentIds 可见组织ID列表
	 * @return 结果
	 */
	@Override
	public List<OrganizationalEntity> getOrganizationalByOrgAndParentForUser(Integer orgId, Integer parentId, List<Integer> visualDepartmentIds) {
		if (orgId == null || visualDepartmentIds == null || visualDepartmentIds.isEmpty()) {
			return null;
		}
		return mapper.selectOrganizationalByOrgAndParentForUser(orgId, parentId, visualDepartmentIds);
	}


	/**
	 * 查询用户可申请用章的组织列表ID
	 *
	 * @param userId 查询人ID
	 * @return 结果 用户所属组织ID列表+父组织ID列表
	 */
	@Override
	public List<Integer> getDepartmentIdsByAppSignet(Integer userId) {
		//临时容器
		Set<Integer> tempDepartmentIds = new HashSet<>();

		/*
		 * 用户可见组织ID列表
		 */
		UserInfo userInfo = userInfoService.get(userId);
		List<Integer> visualDepartmentIds = userInfo.getVisualDepartmentIds();
		if (visualDepartmentIds == null || visualDepartmentIds.isEmpty()) {
			return null;
		}
		tempDepartmentIds.addAll(visualDepartmentIds);

		/*
		 * 查询所有可见组织的父节点链表
		 */
		for (Integer departmentId : visualDepartmentIds) {
			Department department = get(departmentId);
			if (department == null) {
				continue;
			}

			String parentCode = department.getParentCode();
			if (StringUtils.isBlank(parentCode)) {
				continue;
			}

			List<ParentCode> parents = null;
			try {
				parents = JSONObject.parseArray(parentCode, ParentCode.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (parents == null || parents.isEmpty()) {
				continue;
			}
			for (ParentCode parent : parents) {
				int id = parent.getId();
				tempDepartmentIds.add(id);
			}
		}

		//结果容器
		Set<Integer> resDepartmentIds = new HashSet<>();

		/*
		 * 查询临时容器中所有组织的子组织(仅部门)ID，并添加值结果容器中
		 */
		if (!tempDepartmentIds.isEmpty()) {
			List<Integer> childrenIds = mapper.selectChildrenIdsByOrgAndParentsAndType(userInfo.getOrgId(), tempDepartmentIds, 0);//getChildrenIdsByOrgAndParentAndType(userInfo.getOrgId(), departmentId, 0);
			resDepartmentIds.addAll(childrenIds);
		}
		resDepartmentIds.addAll(tempDepartmentIds);
		return new ArrayList<>(resDepartmentIds);
	}

	/**
	 * 查询指定员工负责的组织数量
	 *
	 * @param orgId  集团ID
	 * @param userId 员工iD
	 * @return 结果
	 */
	@Override
	public int getCountByManagerUserId(Integer orgId, Integer userId) {
		return mapper.selectCountByManagerUserId(orgId, userId);
	}


	/**
	 * 查询指定集团下、指定组织类型的组织列表
	 *
	 * @param orgId 集团ID
	 * @param type  组织类型
	 * @return 结果
	 */
	@Override
	public List<Department> getByOrgAndType(Integer orgId, Integer type) {
		return mapper.selectDepartmentsByOrgAndType(orgId, type);
	}

	/**
	 * 查询指定集团下、指定父节点以下的组织架构树(不包含父节点)
	 *
	 * @param orgId    集团ID
	 * @param parentId 父组织
	 * @return 结果
	 */
	@Override
	public List<OrganizationalTree> getDepartmentTreeByOrgAndParent(Integer orgId, Integer parentId) {
		if (orgId == null || parentId == null) {
			return null;
		}

		List<OrganizationalTree> organizationalTrees = mapper.selectOrganizationalTreeByOrgAndParent(orgId, parentId);
		if (organizationalTrees == null || organizationalTrees.isEmpty()) {
			return null;
		}

		for (OrganizationalTree treeNode : organizationalTrees) {
			recursion_getDepartmentTreeByOrgAndParent(treeNode, orgId);
		}

		return organizationalTrees;
	}

	/**
	 * 递归：查询指定集团下、指定父节点以下的组织架构树(不包含父节点)
	 */
	private void recursion_getDepartmentTreeByOrgAndParent(OrganizationalTree parentNode, Integer orgId) {
		if (parentNode == null || orgId == null) {
			return;
		}

		List<OrganizationalTree> childrenNodes = mapper.selectOrganizationalTreeByOrgAndParent(orgId, parentNode.getId());
		if (childrenNodes == null || childrenNodes.isEmpty()) {
			return;
		}
		parentNode.setChildrens(childrenNodes);

		for (OrganizationalTree organizationalTree : childrenNodes) {
			recursion_getDepartmentTreeByOrgAndParent(organizationalTree, orgId);
		}
	}

	/**
	 * 查询指定集团下、指定组织ID的信息
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 要查询的组织ID列表
	 * @return 结果
	 */
	@Override
	public List<DepartmentKV> getByOrgAndDepartmentIds(Integer orgId, List<Integer> departmentIds) {
		if (orgId == null || departmentIds == null || departmentIds.isEmpty()) {
			return null;
		}

		return mapper.selectByOrgAndDepartmentIds(orgId, departmentIds);
	}

	/**
	 * 查询指定集团下、指定用户关联的组织信息列表
	 *
	 * @param orgId  集团ID
	 * @param userId 用户ID
	 * @return 结果
	 */
	@Override
	public List<Department> getByOrgAndUser(Integer orgId, Integer userId) {
		if (orgId == null || userId == null) {
			return null;
		}
		return mapper.selectByOrgAndUser(orgId, userId);
	}

	/**
	 * 查询指定组织类型、指定名称的组织信息列表
	 *
	 * @param type 类型
	 * @param name 组织名称
	 * @return 结果
	 */
	@Override
	public List<Department> getByTypeAndName(Integer type, String name) {
		if (type == null || StringUtils.isBlank(name)) {
			return null;
		}
		Example example = new Example(Department.class);
		example.createCriteria().andIsNull("deleteDate")
				.andEqualTo("type", type)
				.andEqualTo("name", name);
		return mapper.selectByExample(example);
	}

	/**
	 * 查询指定集团下的根节点组织信息
	 *
	 * @param orgId 集团ID
	 * @return 结果
	 */
	@Override
	public Department getRootByOrg(Integer orgId) {
		List<Department> roots = getByOrgAndType(orgId, 2);
		if (roots == null || roots.isEmpty()) {
			return null;
		}

		Department root = null;
		try {
			root = roots.get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return root;
	}

	/**
	 * 查询指定集团下、指定职称的组织列表
	 *
	 * @param orgId      集团ID
	 * @param positionId 职称ID
	 * @return 结果
	 */
	@Override
	public List<Department> getByOrgAndPosition(Integer orgId, Integer positionId) {
		if (orgId == null || positionId == null) {
			return null;
		}
		return mapper.selectByOrgAndPosition(orgId, positionId);
	}

	/**
	 * 查询指定组织上级(level)组织负责人
	 *
	 * @param departmentId 组织ID
	 * @param level        1:当前组织  2:上级组织  3:上上级组织  ..
	 * @return 结果
	 */
	@Override
	public User getManagerByDepartmentAndTopLevel(Integer departmentId, Integer level) {
		if (departmentId == null || level == null) {
			return null;
		}
		level = level - 1;

		Department department = get(departmentId);
		if (department == null) {
			return null;
		}
		if (level == 0) {
			Integer managerUserId = department.getManagerUserId();
			return userService.get(managerUserId);
		}
		if (level > 0) {
			String parentCode = department.getParentCode();
			List<ParentCode> parentCodes = JSONObject.parseArray(parentCode, ParentCode.class);
			if (parentCodes != null && parentCodes.size() > 0) {
				int tempLevel = 1;
				for (int i = parentCodes.size() - 1; i >= 0; i--) {

					if (tempLevel == level) {
						ParentCode parentJson = parentCodes.get(i);
						int parentDepartmentId = parentJson.getId();
						department = get(parentDepartmentId);

						Integer managerUserId = department.getManagerUserId();
						return userService.get(managerUserId);
					}

					tempLevel++;
				}
			}
		}
		return null;
	}

	/**
	 * 查询组织的父公司信息
	 *
	 * @param childrenDepartmentId 要查询的组织ID
	 * @return 结果
	 */
	@Override
	public Department getCompanyByChildrenId(Integer childrenDepartmentId) {
		if (childrenDepartmentId == null) {
			return null;
		}
		Department department = get(childrenDepartmentId);
		if (department == null) {
			return null;
		}

		Integer type = department.getType();
		if (type != null && (type == 1 || type == 2)) {
			return department;
		}
		Integer level = department.getLevel();
		if (level != null && level == 0) {
			return department;
		}

		//无父组织，代表该组织就是root组织
		String parentCodeJson = department.getParentCode();
		if (StringUtils.isBlank(parentCodeJson)) {
			return department;
		}

		List<ParentCode> parentCodes = JSONObject.parseArray(parentCodeJson, ParentCode.class);
		if (parentCodes == null || parentCodes.isEmpty()) {
			return null;
		}

		for (int i = parentCodes.size() - 1; i >= 0; i--) {
			ParentCode parentCode = parentCodes.get(i);
			int id = parentCode.getId();
			Department parent = get(id);
			if (parent != null && parent.getType() != 0) {
				return parent;
			}
		}

		return null;
	}

	/**
	 * 查询组织架构树
	 *
	 * @param orgId    集团ID
	 * @param parentId 父组织ID
	 * @param type     组织类型
	 * @return 结果
	 */
	@Override
	public List<OrganizationalTree> getOrganizationalByOrgAndParentAndType(Integer orgId, Integer parentId, Integer type) {
		if (orgId == null) {
			return null;
		}

		List<OrganizationalTree> organizationalTrees = mapper.selectOrganizationalTreeByOrgAndParentAndType(orgId, parentId, type);
		if (organizationalTrees == null || organizationalTrees.isEmpty()) {
			return null;
		}

		for (OrganizationalTree parent : organizationalTrees) {
			recursion_getOrganizationalByOrgAndParentAndType(parent, orgId, type);
		}

		return organizationalTrees;
	}

	/**
	 * 递归查询组织架构树
	 *
	 * @param orgId  组织ID
	 * @param parent 父节点
	 * @param type   组织类型
	 */
	private void recursion_getOrganizationalByOrgAndParentAndType(OrganizationalTree parent, Integer orgId, Integer type) {
		List<OrganizationalTree> childrens = mapper.selectOrganizationalTreeByOrgAndParentAndType(orgId, parent.getId(), type);
		if (childrens == null || childrens.isEmpty()) {
			return;
		}
		parent.setChildrens(childrens);
		for (OrganizationalTree children : childrens) {
			recursion_getOrganizationalByOrgAndParentAndType(children, orgId, type);
		}
	}

	/**
	 * 查询子组织列表
	 *
	 * @param orgId    集团ID
	 * @param parentId 父组织ID
	 * @return 结果
	 */
	@Override
	public List<OrganizationalEntity> getOrganizationalByOrgAndParentToAddFlow(Integer orgId, Integer parentId) {
		if (orgId == null) {
			return null;
		}
		/*
		 * 查询可见组织中，层级最高的组织列表
		 */
		return mapper.selectOrganizationalByOrgAndParentToAddFlow(orgId, parentId);
	}

	/**
	 * 查询子组织列表
	 *
	 * @param orgId    集团ID
	 * @param parentId 父组织ID
	 * @param type     组织类型
	 * @return 结果
	 */
	@Override
	public List<OrganizationalEntity> getOrganizationalByOrgAndParentToApplicationSignet(Integer orgId, Integer parentId, Integer type) {
		if (orgId == null) {
			return null;
		}
		/*
		 * 查询可见组织中，层级最高的组织列表
		 */
		return mapper.selectOrganizationalByOrgAndParentToApplicationSignet(orgId, parentId, type);
	}

	/**
	 * 查询数据库中所有组织列表信息
	 *
	 * @return 结果
	 */
	@Override
	public List<Department> getAll() {
		Example example = new Example(Department.class);
		example.createCriteria().andIsNull("deleteDate");
		return mapper.selectByExample(example);
	}

	/**
	 * 以level为top层级,查询0,1,2...level各层级的主管列表,不存在的主管列表为空
	 *
	 * @param departmentId 基点,组织id
	 * @param topLevel     top层级
	 * @return 各层级组织主管列表
	 */
	@Override
	public List<User> getManagersByDepartmentToTopLevel(Integer departmentId, Integer topLevel) {
		if (departmentId == null || topLevel == null) {
			return null;
		}
		List<User> managers = new ArrayList<>();
		//递增个层级主管
		Department department = get(departmentId);
		if (department == null) {
			return null;
		}
		String parentCode = department.getParentCode();
		List<ParentCode> parentCodes = JSONObject.parseArray(parentCode, ParentCode.class);
		if (parentCodes != null && parentCodes.size() > 0) {
			int tempLevel = 1;
			for (int i = parentCodes.size() - 1; i >= 0; i--) {
				if (tempLevel == topLevel) {
					ParentCode parentJson = parentCodes.get(i);
					int parentDepartmentId = parentJson.getId();
					department = get(parentDepartmentId);

					Integer managerUserId = department.getManagerUserId();
					User manager = userService.get(managerUserId);
					managers.add(manager == null ? (new User()) : manager);
				}
				tempLevel++;
			}
		}
		return managers;
	}

//	/**
//	 * 以level为top层级,查询0,1,2...level各层级的主管列表,不存在的主管列表为空
//	 *
//	 * @param departmentId 基点,组织id
//	 * @param topLevel     top层级
//	 * @return 各层级组织主管列表
//	 */
//	@Override
//	public List<User> getManagersByDepartmentToTopLevel(Integer departmentId, Integer topLevel) {
//		if (departmentId == null || topLevel == null) return null;
//		topLevel = topLevel - 1;
//
//		List<User> managers = new ArrayList<>();
//
//		//第1个层级主管
//		Department department = get(departmentId);
//		if (department == null) return null;
//		User firstManager = userService.get(department.getManagerUserId());
//		managers.add(firstManager);
//
//		//递增个层级主管
//		if (topLevel > 0) {
//			String parentCode = department.getParentCode();
//			List<ParentCode> parentCodes = JSONObject.parseArray(parentCode, ParentCode.class);
//			if (parentCodes != null && parentCodes.size() > 0) {
//				int tempLevel = 1;
//				for (int i = parentCodes.size() - 1; i >= 0; i--) {
//					if (tempLevel == topLevel) {
//						ParentCode parentJson = parentCodes.get(i);
//						int parentDepartmentId = parentJson.getId();
//						department = get(parentDepartmentId);
//
//						Integer managerUserId = department.getManagerUserId();
//						User manager = userService.get(managerUserId);
//						managers.add(manager == null ? (new User()) : manager);
//					}
//					tempLevel++;
//				}
//			}
//		}
//		return managers;
//	}
}

