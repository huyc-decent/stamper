package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.base.BaseService;
import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.entity.Role;
import com.yunxi.stamper.entity.RolePerms;
import com.yunxi.stamper.entity.RoleTemp;
import com.yunxi.stamper.service.*;
import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.Perms;
import com.yunxi.stamper.entityVo.PermsVo;
import com.yunxi.stamper.entityVo.Route;
import com.yunxi.stamper.mapper.PermsMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/4/26 0026 13:20
 */
@Service
public class IPermService extends BaseService implements PermService {

	@Autowired
	private PermsMapper mapper;
	@Autowired
	private RoleService roleService;
	@Autowired
	private RolePermsService rolePermsService;
	@Autowired
	@Lazy
	private UserInfoService userInfoService;
	@Autowired
	private RoleTempService roleTempService;

	@Override
	public List<Integer> getUserIDByPerms(Integer permsId) {
		if (permsId != null) {
			return mapper.selectUserIDByPerms(permsId);
		}
		return null;
	}

	/**
	 * 查询用户已选择的快捷方式列表
	 */
	@Override
	public List<Perms> getQuickLinkByUserSelected(Integer userId) {
		if (userId != null) {
			return mapper.selectQuickLinkByUserSelected(userId);
		}
		return null;
	}

	/**
	 * 查询用户拥有的快捷方式列表
	 */
	@Override
	public List<Perms> getQuickLinkByUser(Integer userId) {
		if (userId != null) {
			return mapper.selectQuickLinkByUser(userId);
		}
		return null;
	}

	/**
	 * 查询公司管理员(属主)动态路由
	 */
	@Override
	public List<Route> getRouteByAdmin(Integer orgId) {
		List<Route> firstRoutes = null;
		if (orgId.intValue() != -1) {
			//1级菜单(非平台级)
			firstRoutes = mapper.selectByFIrstRouteAndAdmin();
		} else {
			//1级菜单(包含平台级)
			firstRoutes = mapper.selectAllFirstRouteAndAdmin();
		}
		if (firstRoutes != null && firstRoutes.size() > 0) {
			for (int i = 0; i < firstRoutes.size(); i++) {
				Route route = firstRoutes.get(i);
				getChildrenByFirstRoute(route);
			}
		}
		return firstRoutes;
	}

	/**
	 * 用户动态路由
	 */
	@Override
	public List<Route> getRouteByUser(Integer userId) {
		if (userId != null) {
			//1级菜单
			List<Route> firstRoutes = mapper.selectByFirstRouteAndUser(userId, 1);
			if (firstRoutes != null && firstRoutes.size() > 0) {
				for (int i = 0; i < firstRoutes.size(); i++) {
					Route route = firstRoutes.get(i);
					//查询指定用户,指定父级路由,指定type类型的子级路由列表
					List<Route> childrens = mapper.selectRoutesByParentAndUser(route.getId(), userId, 1);
					route.setChildrens(childrens);
				}
			}
			return firstRoutes;
		}
		return null;
	}

	//查询1级路由下的2级路由菜单列表
	private void getChildrenByFirstRoute(Route route) {
		if (route != null) {
			List<Route> childrens = mapper.selectChildrenByFirstRoute(route.getId());
			route.setChildrens(childrens);
		}
	}

	/**
	 * 查询code的权限
	 *
	 * @param code
	 * @return
	 */
	@Override
	public Perms getByCode(String code) {
		if (StringUtils.isNotBlank(code)) {
			Example example = new Example(Perms.class);
			example.createCriteria().andIsNull("deleteDate")
					.andEqualTo("code", code);
			return mapper.selectOneByExample(example);
		}
		return null;
	}

	/**
	 * 查询指定权限级别的树列表
	 *
	 * @param level
	 * @return
	 */
	@Override
	public List<PermsVo> getTreeByLevel(Integer level) {
		if (level != null) {
			List<PermsVo> vo = mapper.selectTopPermsByLevel(level);
			if (vo != null && vo.size() > 0) {
				for (int i = 0; i < vo.size(); i++) {
					PermsVo permsVo = vo.get(i);
					getChildrens(permsVo, level);
				}
			}
			return vo;
		}
		return null;
	}

	/**
	 * 查询level级别的权限树
	 *
	 * @param permsVo
	 * @param level
	 */
	private void getChildrens(PermsVo permsVo, Integer level) {
		if (permsVo != null) {
			List<PermsVo> childrens = mapper.selectByParentAndLevel(permsVo.getId(), level);
			if (childrens != null && childrens.size() > 0) {
				permsVo.setChildren(childrens);
				for (int i = 0; i < childrens.size(); i++) {
					PermsVo vo = childrens.get(i);
					getChildrens(vo, level);
				}
			}
		}
	}

	/**
	 * 查一下用户拥有的权限列表
	 *
	 * @param userId
	 * @return
	 */
	@Override
	public List<Perms> getByUser(Integer userId) {
		if (userId != null) {
			return mapper.selectByUser(userId);
		}
		return null;
	}

	/**
	 * 将该父节点下的所有子节点parentID属性至为空
	 */
	@Override
	@Transactional
	public void setNullByParent(Integer parentId) {
		if (parentId != null) {
			Example example = new Example(Perms.class);
			example.createCriteria().andIsNull("deleteDate")
					.andEqualTo("parentId", parentId);
			List<Perms> perms = mapper.selectByExample(example);
			if (perms != null && perms.size() > 0) {
				for (int i = 0; i < perms.size(); i++) {
					Perms children = perms.get(i);
					children.setParentId(null);
					mapper.updateByPrimaryKey(children);
				}
				userInfoService.clearPool();
			}
		}
	}

	@Override
	public List<Perms> getAll() {
		Example example = new Example(Perms.class);
		example.createCriteria().andIsNull("deleteDate");
		return mapper.selectByExample(example);
	}

	/**
	 * 查询权限树
	 *
	 * @return
	 */
	@Override
	public List<PermsVo> getTree() {
		List<PermsVo> permsVo = mapper.selectRoot();
		for (int i = 0; i < permsVo.size(); i++) {
			PermsVo vo = permsVo.get(i);
			getChildrens(vo);
		}
		return permsVo;
	}

	/**
	 * 查询父节点下的所有子节点权限列表
	 */
	private void getChildrens(PermsVo parent) {
		Integer id = parent.getId();
		List<PermsVo> permsVos = mapper.selectByParent(id);
		if (permsVos != null && permsVos.size() > 0) {
			parent.setChildren(permsVos);
			for (int i = 0; i < permsVos.size(); i++) {
				PermsVo permsVo = permsVos.get(i);
				getChildrens(permsVo);
			}
		}
	}

	/**
	 * 查询指定条件的权限列表
	 *
	 * @param perms
	 * @return
	 */
	@Override
	public List<Perms> selectByParams(Perms perms) {
		if (perms != null) {
			Example example = new Example(Perms.class);
			Example.Criteria criteria = example.createCriteria().andIsNull("deleteDate");
			String name = perms.getLabel();
			if (StringUtils.isNotBlank(name)) {
				criteria.andLike("label", name);
			}
			String url = perms.getUrl();
			if (StringUtils.isNotBlank(url)) {
				criteria.andLike("url", url);
			}
			Integer level = perms.getLevel();
			if (level != null) {
				criteria.andEqualTo("level", level);
			}
			String code = perms.getCode();
			if (StringUtils.isNotBlank(code)) {
				criteria.andLike("code", code);
			}
			Integer parentId = perms.getParentId();
			if (parentId != null) {
				criteria.andEqualTo("parentId", parentId);
			}
			return mapper.selectByExample(example);
		}
		return null;
	}

	@Override
	@Transactional
	public void update(Perms perms) {
		int updateCount = 0;
		if (perms != null && perms.getId() != null) {
			updateCount = mapper.updateByPrimaryKey(perms);
		}
		if (updateCount != 1) {
			throw new PrintException("权限修改失败");
		} else {
			userInfoService.clearPool();
		}
	}

	@Override
	public Perms get(Integer id) {
		if (id != null) {
			Example example = new Example(Perms.class);
			example.createCriteria().andEqualTo("id", id)
					.andIsNull("deleteDate");
			return mapper.selectOneByExample(example);
		}
		return null;
	}

	@Override
	@Transactional
	public void del(Integer permsId) {
		int delCount = 0;
		if (permsId != null) {
			Perms perms = get(permsId);
			perms.setDeleteDate(new Date());
			delCount = mapper.updateByPrimaryKey(perms);
		}

		if (delCount != 1) {
			throw new PrintException("权限删除失败");
		} else {
			userInfoService.clearPool();
		}
	}

	@Override
	@Transactional
	public void add(Perms perms) {
		int addCount = 0;
		if (perms != null) {
			perms.setCreateDate(new Date());
			addCount = mapper.insert(perms);
		}

		if (addCount != 1) {
			throw new PrintException("权限添加失败");
		} else {
			//为所有管理员角色绑定新权限
			if (perms.getLevel() == 1) {
				//查询所有管理员角色列表
				List<Role> roles = roleService.getAllAdminList();
				if (roles != null && !roles.isEmpty()) {
					//绑定新权限
					for (Role role : roles) {
						RolePerms rolePerms = new RolePerms();
						rolePerms.setPermsId(perms.getId());
						rolePerms.setRoleId(role.getId());
						rolePermsService.add(rolePerms);
					}
				}

				//为管理员角色默认模板添加新权限
				List<RoleTemp> roleTemps = roleTempService.getAll();
				if (roleTemps != null && !roleTemps.isEmpty()) {
					for (RoleTemp roleTemp : roleTemps) {
						String code = roleTemp.getCode();
						if (StringUtils.isNotBlank(code) && "admin".equals(code)) {
							List<Integer> permIds = CommonUtils.splitToInteger(roleTemp.getPermIds(), ",");
							if (!permIds.contains(perms.getId())) {
								permIds.add(perms.getId());
								String permsIds = CommonUtils.splitToString(permIds, ",");
								roleTemp.setPermIds(permsIds);
								roleTempService.update(roleTemp);
							}
							break;
						}
					}
				}
			}

			userInfoService.clearPool();
		}
	}

}
