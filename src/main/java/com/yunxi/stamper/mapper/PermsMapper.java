package com.yunxi.stamper.mapper;

import com.yunxi.stamper.entity.Perms;
import com.yunxi.stamper.entityVo.PermsVo;
import com.yunxi.stamper.entityVo.Route;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface PermsMapper extends MyMapper<Perms> {

	//查询子节点列表,组成树结构
	List<PermsVo> selectByParent(Integer parentId);

	//查询根节点
	List<PermsVo> selectRoot();

	//查一下用户的权限
	List<Perms> selectByUser(Integer userId);

	//查询level级别的1级权限列表(没有父节点的)
	List<PermsVo> selectTopPermsByLevel(Integer level);

	//查询level级别和parent的子权限列表
	List<PermsVo> selectByParentAndLevel(Integer parentId, Integer level);

	//用户动态路由1级菜单列表
	List<Route> selectByFirstRouteAndUser(Integer userId, Integer type);

	//查询1级路由下的2级路由菜单列表
	List<Route> selectChildrenByFirstRoute(Integer permsId);

	//查询公司管理员(属主,非角色管理员)的1级菜单列表
	List<Route> selectByFIrstRouteAndAdmin();

	//查询平台公司管理员(属主,非角色管理员)的1级菜单列表
	List<Route> selectAllFirstRouteAndAdmin();

	//查询指定用户,指定父级路由,指定type类型的子级路由列表
	List<Route> selectRoutesByParentAndUser(Integer permsParentId, Integer userId, Integer type);

	//查询用户拥有的快捷方式列表
	List<Perms> selectQuickLinkByUser(Integer userId);

	//查询用户已选择的快捷方式列表
	List<Perms> selectQuickLinkByUserSelected(Integer userId);

	List<Integer> selectUserIDByPerms(Integer permsId);
}