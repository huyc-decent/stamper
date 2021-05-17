package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.Role;
import com.yunxi.stamper.entityVo.RoleEntity;
import com.yunxi.stamper.entityVo.RoleVo;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/7 0007 10:20
 */
public interface RoleService {
	Role getByNameAndOrg(String name, Integer orgId);

	void add(Role role);

	Role get(Integer roleId);

	void del(Role role);

	void update(Role role);

	//查询公司拥有的角色列表
	List<RoleVo> getVoByOrg(Integer orgId);

	Role getByCodeAndOrg(String code, Integer orgId);

	//模糊搜索
	List<RoleEntity> getByKeywordAndOrg(String keywords, Integer orgId);

	//查询用户拥有的角色Id列表
	List<Integer> getByUser(Integer userId);

	//查询所有角色id列表
	List<Integer> getAll();

	//查询用户角色列表
	List<Role> getByUserId(Integer userId);

	//查询指定code的角色列表
	List<Role> getByCode(String code);

	//查询该公司所有角色列表
	List<Role> getByOrg(Integer orgId);

	/**
	 * 删除角色
	 * @param role 要删除的角色信息
	 */
	void delRole(Role role);

	/**
	 * 获取管理员角色列表
	 * @return 角色列表
	 */
	List<Role> getAllAdminList();

}
