package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.UserRole;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/7 0007 10:52
 */
public interface UserRoleService {
	//给指定用户绑定角色列表
	void bindRoles(Integer userId, Integer[] roleIds);

	void add(UserRole userRole);

	void del(UserRole userRole);

	//查询用户角色列表
	List<UserRole> getByUser(Integer userId);

	UserRole getByUserAndRole(Integer userId, Integer roleId);

	//查询角色id对应的用户id列表
	List<Integer> getByRole(Integer roleId);

	/**
	 * 删除角色-员工关联信息
	 * @param userId
	 */
	void delByUserId(Integer userId);

	/**
	 * 查询拥有指定角色ID的员工ID列表
	 * @param roleId 角色ID
	 * @return
	 */
	List<Integer> getUserIdsByRoleId(Integer roleId);

	/**
	 * 删除指定角色关联的员工信息
	 * @param roleId
	 */
	void delByRole(Integer roleId);
}
