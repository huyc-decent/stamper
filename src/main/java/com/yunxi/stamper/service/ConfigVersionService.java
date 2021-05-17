package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.ConfigVersion;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/7/4 0004 14:29
 */
public interface ConfigVersionService {
	List<ConfigVersion> getAll(Integer type);

	void add(ConfigVersion cv);

	ConfigVersion get(Integer id);

	void del(ConfigVersion cv);

	ConfigVersion getByVersion(String version, Integer type);
}
