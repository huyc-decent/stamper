package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.RolePerms;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/7 0007 14:43
 */
public interface RolePermsService {
	void add(RolePerms rolePerms);

	RolePerms get(Integer roleId, Integer permsId);

	void del(RolePerms rolePerms);

	void delByRole(Integer roleId);
}
