package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.ConfigTemp;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/8/12 0012 16:23
 */
public interface ConfigTempService {
	void add(ConfigTemp configTemp);

	void del(ConfigTemp configTemp);

	ConfigTemp get(Integer configTempId);

	void update(ConfigTemp configTemp);

	List<ConfigTemp> getList();

	ConfigTemp getByDefault();

}
