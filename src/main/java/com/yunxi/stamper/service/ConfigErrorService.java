package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.ConfigError;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/6/28 0028 14:32
 */
public interface ConfigErrorService {
	void add(ConfigError configError);

	void del(ConfigError error);

	List<ConfigError> getByUUID(String uuid);

	ConfigError get(Integer id);
}
