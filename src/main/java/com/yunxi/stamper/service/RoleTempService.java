package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.RoleTemp;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/7 0007 14:32
 */
public interface RoleTempService {
	List<RoleTemp> getAll();

	void add(RoleTemp roleTemp);

	void update(RoleTemp roleTemp);
}
