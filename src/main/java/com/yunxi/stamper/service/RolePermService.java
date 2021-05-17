package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.Role;
import com.yunxi.stamper.entity.RolePerms;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/7 0007 10:48
 */
public interface RolePermService {
	//为指定角色添加权限列表
	void bindPerms(Role role, Integer[] permsIds, Integer updateAt);

	void add(RolePerms rp);

	void del(RolePerms rolePerms);

	RolePerms get(Integer roleId, Integer permsId);

	//查询该角色绑定的权限ID列表
	List<Integer> getByRoleId(Integer roleId);


}
