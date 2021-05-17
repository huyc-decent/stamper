package com.yunxi.stamper.service;


import com.yunxi.stamper.entity.Perms;
import com.yunxi.stamper.entityVo.PermsVo;
import com.yunxi.stamper.entityVo.Route;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/4/26 0026 13:20
 */
public interface PermService {
	void add(Perms perms);

	void del(Integer permsId);

	Perms get(Integer id);

	void update(Perms perms);

	List<Perms> selectByParams(Perms perms);

	//查询权限树
	List<PermsVo> getTree();

	//查询所有权限
	List<Perms> getAll();

	//将该父节点下的所有子节点parentID属性至为空
	void setNullByParent(Integer parentId);

	//查一下用户拥有的权限列表
	List<Perms> getByUser(Integer userId);

	//查询指定权限级别的树列表
	List<PermsVo> getTreeByLevel(Integer level);

	//查询code的权限
	Perms getByCode(String code);

	//用户动态路由
	List<Route> getRouteByUser(Integer userId);

	//查询公司管理员(属主)动态路由
	List<Route> getRouteByAdmin(Integer orgId);

	//查询用户拥有的快捷方式列表
	List<Perms> getQuickLinkByUser(Integer userId);

	//查询用户已选择的快捷方式列表
	List<Perms> getQuickLinkByUserSelected(Integer userId);

	List<Integer> getUserIDByPerms(Integer permsId);
}
