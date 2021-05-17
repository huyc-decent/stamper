package com.yunxi.stamper.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yunxi.stamper.base.BaseService;
import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.sys.error.base.ToLoginException;
import com.yunxi.stamper.entity.*;
import com.yunxi.stamper.entityVo.OrganizationalTree;
import com.yunxi.stamper.entityVo.ParentCode;
import com.yunxi.stamper.entityVo.UserInfo;
import com.yunxi.stamper.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/7/30 0030 13:32
 */
@Slf4j
@Service
public class IUserInfoService extends BaseService implements UserInfoService {

	@Autowired
	private PermService permService;
	@Autowired
	private UserService userService;
	@Autowired
	private RelateDepartmentUserService relateDepartmentUserService;
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private RoleService roleService;
	@Autowired
	@Lazy
	private OrgService orgService;
	@Autowired
	private SysUserService sysUserService;

	private static final String userInfoKey = "userInfo:";//userInfo:${userId} == userInfo

	@Override
	public void add(UserInfo userInfo) {
		if (userInfo == null || userInfo.getId() == null) {
			return;
		}

		Integer userId = userInfo.getId();
		String key = userInfoKey + userId;
		redisUtil.set(key, userInfo);
	}

	@Override
	public void del(UserInfo userInfo) {
		if (userInfo == null || userInfo.getId() == null) {
			return;
		}
		del(userInfo.getId());
	}

	@Override
	public void del(Integer userId) {
		if (userId == null) {
			return;
		}
		String key = userInfoKey + userId;
		redisUtil.del(key);
	}

	@Override
	public UserInfo get(Integer userId) {
		if (userId == null) {
			return null;
		}
		UserInfo userInfo;

		String key = userInfoKey + userId;
		Object obj = redisUtil.get(key);

		/*如果缓存不存在，重新生成*/
		if (obj == null || StringUtils.isBlank(obj.toString())) {
			userInfo = generatorUserInfo(userId);
			add(userInfo);
		} else {
			userInfo = (UserInfo) obj;
		}
		return userInfo;
	}

	@Override
	public void clearPool() {
		try {
			Set<String> keys = redisUtil.keys(userInfoKey + "*");
			redisUtil.del(keys.toArray(new String[0]));
		} catch (Exception e) {
			log.error("出现异常 ", e);
		}
	}

	private UserInfo generatorUserInfo(Integer userId) {
		/*查询员工信息*/
		User user = userService.get(userId);
		if (user == null) {
			throw new ToLoginException("员工信息不存在或已删除\tuserId:" + userId);
		}

		/*查询用户信息*/
		SysUser sysUser = sysUserService.get(user.getSysUserId());
		if (sysUser == null) {
			throw new ToLoginException("该账号不存在或已被删除");
		}

		/*初始化返回值对象*/
		UserInfo info = new UserInfo();
		BeanUtils.copyProperties(user, info);
		info.setPhone(sysUser.getPhone());

		/*集团负责人*/
		Integer orgId = user.getOrgId();
		Department root = orgService.getRoot(orgId);
		info.setOwner(root != null && root.getManagerUserId() == user.getId().intValue());

		/*用户角色*/
		List<Role> roles = roleService.getByUserId(userId);
		if (roles != null) {
			for (Role role : roles) {
				String code = role.getCode();
				if (code.contains("admin")) {
					info.setAdmin(true);
					break;
				}
			}
		}

		/*查询权限URL列表*/
		List<Perms> permses = permService.getByUser(user.getId());
		List<String> paths = new LinkedList<>();
		if (permses != null) {
			for (Perms perms : permses) {
				String urls = perms.getUrl();
				if (StringUtils.isBlank(urls)) {
					continue;
				}
				if (urls.contains(",")) {
					String[] urlArr = urls.split(",");
					Collections.addAll(paths, urlArr);
				} else {
					paths.add(urls);
				}
			}
		}
		info.setPermsUrls(paths);

		/*用户管理的组织ID列表*/
		List<Integer> departmentIds = getManagerDepartmentIds(info);
		info.setDepartmentIds(departmentIds);

		/*用户可见的组织ID列表*/
		List<Integer> visualDepartmentIds = getVisualDepartmentIds(info);
		info.setVisualDepartmentIds(visualDepartmentIds);

		/*组织架构(树结构)*/
		List<OrganizationalTree> orgTree = getOrgTree(info);
		info.setTree(orgTree);

//		redisUtil.set(RedisGlobal.USER_INFO + info.getId(), JSONObject.toJSONString(info));

		return info;
	}

	/**
	 * 处理用户可见组织：
	 * 1.如果可见组织为部门，查询该组织父层级以上，直至公司层级
	 * 2.1如果可见组织是公司，且用户是公司主管，查询该公司下所有组织(包含部门&子孙公司)
	 * 2.2如果可见组织是公司，但用户非主管，查询该公司下所有部门&子孙部门
	 */
	private List<Integer> getVisualDepartmentIds(UserInfo info) {
		Integer orgId = info.getOrgId();
		// 集团负责人，集团下所有组织均可见
		if (info.isOwner()) {
			return info.getDepartmentIds();
		}

		//容器：用户可见组织ID列表
		Set<Integer> pools = new HashSet<>();

		// 用户的管理组织,哪些组织的主管是该用户
		List<Integer> managerDepartmentIds = info.getDepartmentIds();
		if (managerDepartmentIds != null && managerDepartmentIds.size() > 0) {
			pools.addAll(managerDepartmentIds);
		}

		// 用户的所属组织
		List<Integer> departmentIds = relateDepartmentUserService.getDepartmentIdsByUserId(info.getId());
		//查询该组织下的子孙组织(部门或公司)
		if (departmentIds != null) {
			for (Integer departmentId : departmentIds) {
				pools.add(departmentId);
				//如果该组织已经在用户管理组织列表中，则不需要再向下查找子孙组织了，因为在查询管理组织列表时，已经查询过所有子孙组织
				if (managerDepartmentIds != null && managerDepartmentIds.size() > 0 && managerDepartmentIds.contains(departmentId)) {
					continue;
				}
				//查询该组织下的子孙部门
				List<Integer> childIds = departmentService.getChildrenIdsByOrgAndParentAndType(orgId, departmentId, 0);
				if (childIds != null && !childIds.isEmpty()) {
					pools.addAll(childIds);
				}
			}
		}

		//可见组织ID容器
		Set<Integer> res = new HashSet<>();

		/*
		  遍历上面代码中查询到的该用户所有可见组织，解析出这些组织的树根节点(公司层级)
		  1.查出所有Level最小的值
		  2.检查这些组织是否有部门类型
		  3.如果有部门类型，则查询这些部门顶层公司层级，这些(个)公司层级即是用户可见组织的根节点
		 */
		Set<Integer> minLevelIds = new HashSet<>();
		int minLevel = 100;
		for (Integer departmentId : pools) {
			//不存在的组织不需要处理
			Department department = departmentService.get(departmentId);
			if (department == null) {
				continue;
			}
			res.add(departmentId);
			Integer level = department.getLevel();
			if (level == minLevel) {
				minLevelIds.add(departmentId);
				continue;
			}
			if (level < minLevel) {
				minLevel = level;
				minLevelIds.clear();
				minLevelIds.add(departmentId);
			}
		}
		for (Integer minLevelId : minLevelIds) {
			Department minLevelDepartment = departmentService.get(minLevelId);
			String parentCode = minLevelDepartment.getParentCode();//没有父公司，无需检查上级
			if (StringUtils.isBlank(parentCode)) {
				continue;
			}
			Integer type = minLevelDepartment.getType();
			if (type != 0) {
				continue;
			}

			List<ParentCode> parentCodes;
			try {
				parentCodes = JSONObject.parseArray(parentCode, ParentCode.class);//父公司ID链表解析失败，无需检查上级
			} catch (Exception e) {
				log.error("解析组织ID链有误\terror:{}\tparentCode:{}", e.getMessage(), parentCode);
				continue;
			}
			//父公司解析为空，无需检查上级
			if (parentCodes == null) {
				continue;
			}

			for (int i = parentCodes.size() - 1; i >= 0; i--) {
				ParentCode code = parentCodes.get(i);
				int parentId = code.getId();
				Department parent = departmentService.get(parentId);
				if (parent != null) {
					//该父节点就是公司、集团,查到该节点为止
					Integer parentType = parent.getType();
					if (parentType == 1 || parentType == 2) {
						//查询该公司下所有子孙部门
						res.add(parentId);
						List<Integer> childIds = departmentService.getChildrenIdsByOrgAndParentAndType(parent.getOrgId(), parentId, 0);
						if (childIds != null && !childIds.isEmpty()) {
							res.addAll(childIds);
						}
						break;
					}
				}
			}
		}

		return new ArrayList<>(res);
	}

	/**
	 * 刷新指定集团下员工的缓存信息(如果存在该缓存)
	 *
	 * @param orgId
	 */
	@Override
	public void refreshByOrg(Integer orgId) {
		if (orgId == null) {
			return;
		}
		List<Integer> userIds = userService.getIdsByOrg(orgId);
		if (userIds == null || userIds.isEmpty()) {
			return;
		}
		userIds.forEach(this::del);
	}

	/**
	 * 查询用户管理的(子、孙)组织ID列表
	 *
	 * @param info
	 */
	@Override
	public List<Integer> getManagerDepartmentIds(UserInfo info) {
		Integer orgId = info.getOrgId();
		Integer userId = info.getId();

		// 属主，所有子孙组织均有权限管理
		if (info.isOwner()) {
			return departmentService.getDepartmentIdByOrg(info.getOrgId());
		}

		// 非属主，查询用户管理的组织
		List<Integer> departmentIds = departmentService.getDepartmentIdsByManager(orgId, userId);
		if (departmentIds == null || departmentIds.isEmpty()) {
			return departmentIds;
		}

		// 查询子孙组织，因为用户是该组织管理者，所以不管该组织的子孙组织是公司类型，还是部门类型，该用户都有权限管理
		List<Integer> childrenIds = departmentService.getChildrenIdsByOrgAndParentsAndType(orgId, departmentIds, null);
		if (childrenIds != null) {
			for (Integer childrenId : childrenIds) {
				if (!departmentIds.contains(childrenId)) {
					departmentIds.add(childrenId);
				}
			}
		}
		return departmentIds;
	}

	/**
	 * 刷新该用户组织架构
	 */
	public List<OrganizationalTree> getOrgTree(UserInfo userInfo) {
		Integer orgId = userInfo.getOrgId();
		Org org = orgService.get(orgId);

		OrganizationalTree tree = generatorTreeByOrg(org.getId());//整个集团的组织架构

		/*
		  集团 属主
		 */
		if (userInfo.isOwner()) {
			return Collections.singletonList(tree);
		} else {
			/*
			  递归遍历，过滤登录用户不可见的组织ID列表
			 */
			CommonUtils.recursion_includeIds(tree, userInfo.getVisualDepartmentIds());
			/*
			  如果根节点是集团、同时第1层级无部门节点，将根节点集团删除
			 */
			int type = tree.getType();
			//标记  false:无部门  true：有部门
			boolean hasDepartment = true;
			if (type == 2) {

				hasDepartment = false;

				List<OrganizationalTree> childrens = tree.getChildrens();
				if (childrens != null) {
					for (OrganizationalTree node : childrens) {
						if (node.getType() == 0) {
							hasDepartment = true;
							break;
						}
					}
				}
			}

			//如果无部门，删除根节点为集团层级的节点
			if (!hasDepartment) {
				List<OrganizationalTree> childrens = tree.getChildrens();
				if (childrens != null && childrens.size() > 0) {//如果根节点下无子节点，显示根节点
					return childrens;
				} else {
					return Collections.singletonList(tree);
				}
			}
			//如果有部门，根节点不变
			else {
				return Collections.singletonList(tree);
			}
		}
	}

	/**
	 * 以集团为根节点，生成组织树
	 *
	 * @param orgId 集团ID
	 * @return
	 */
	@Override
	public OrganizationalTree generatorTreeByOrg(Integer orgId) {
		/*
		  查询该集团根组织信息
		 */
		Department root = departmentService.getRootByOrg(orgId);

		/*
		  组织根节点
		 */
		OrganizationalTree tree = new OrganizationalTree();
		tree.setId(root.getId());
		tree.setName(root.getName());
		tree.setType(root.getType());

		/*
		  查询根节点以下组织树
		 */
		List<OrganizationalTree> childrens = departmentService.getDepartmentTreeByOrgAndParent(orgId, root.getId());
		tree.setChildrens(childrens);
		return tree;
	}
}
