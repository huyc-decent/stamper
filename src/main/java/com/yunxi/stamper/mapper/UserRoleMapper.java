package com.yunxi.stamper.mapper;


import com.yunxi.stamper.entity.UserRole;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserRoleMapper extends MyMapper<UserRole> {
	//查询角色id对应的用户id列表
	List<Integer> selectByRole(Integer roleId);

	//删除角色-员工关联信息
	void deleteByUserId(Integer userId);

	/**
	 * 查询拥有指定角色ID的员工ID列表
	 * @param roleId
	 * @return
	 */
	List<Integer> selectUserIdsByRoleId(Integer roleId);

	/**
	 * 删除指定角色关联的员工信息
	 *
	 * @param roleId
	 */
	void deleteByRoleId(Integer roleId);
}