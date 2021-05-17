package com.yunxi.stamper.mapper;

import com.yunxi.stamper.entity.RolePerms;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface RolePermsMapper extends MyMapper<RolePerms> {
	//查询该角色绑定的权限ID列表
	List<Integer> selectByRoleId(Integer roleId);

	void deleteByRole(Integer roleId);
}