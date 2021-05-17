package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.Config;
import com.yunxi.stamper.entityVo.ConfigVo;
import com.yunxi.stamper.entityVo.UserConfig;

import java.util.List;
import java.util.Set;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/6/28 0028 14:11
 */
public interface ConfigService {
	void add(Config uuidVo);

	void update(Config config);

	void del(Config config);

	Config getByUUID(String uuid);

	Config getDefaultConfig();

	List<ConfigVo> getByKeyword(String keyword, Set<String> deviceIds);

	List<UserConfig> getByOrgIdAndKeyword(Integer orgId, String keyword);

	Config get(Integer id);

	void addBatch(List<Config> configs);

	void updateBatch(List<Config> updateConfigs);
}
