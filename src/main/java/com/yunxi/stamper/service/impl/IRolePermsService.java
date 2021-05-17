package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.RolePerms;
import com.yunxi.stamper.mapper.RolePermsMapper;
import com.yunxi.stamper.service.RolePermsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/7 0007 14:43
 */
@Service
public class IRolePermsService implements RolePermsService {
	@Autowired
	private RolePermsMapper mapper;

	@Override
	@Transactional
	public void delByRole(Integer roleId) {
		if (roleId != null) {
			mapper.deleteByRole(roleId);
		}
	}

	@Override
	@Transactional
	public void del(RolePerms rolePerms) {
		int delCount = 0;
		if (rolePerms != null) {
			delCount = mapper.delete(rolePerms);
		}

		if (delCount != 1) {
			throw new PrintException("角色-权限信息移除失败");
		}
	}

	@Override
	public RolePerms get(Integer roleId, Integer permsId) {
		if (roleId != null && permsId != null) {
			Example example = new Example(RolePerms.class);
			example.createCriteria().andEqualTo("roleId", roleId).andEqualTo("permsId", permsId);
			return mapper.selectOneByExample(example);
		}
		return null;
	}

	@Override
	@Transactional
	public void add(RolePerms rolePerms) {
		int insert = 0;
		if (rolePerms != null) {
			insert = mapper.insert(rolePerms);
		}
		if (insert != 1) {
			throw new PrintException("角色-权限绑定失败");
		}
	}
}
