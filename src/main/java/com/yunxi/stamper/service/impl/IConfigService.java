package com.yunxi.stamper.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yunxi.stamper.commons.other.Global;
import com.yunxi.stamper.commons.other.RedisGlobal;
import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.Config;
import com.yunxi.stamper.entityVo.ConfigVo;
import com.yunxi.stamper.entityVo.UserConfig;
import com.yunxi.stamper.mapper.ConfigMapper;
import com.yunxi.stamper.service.ConfigService;
import com.yunxi.stamper.base.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/6/28 0028 14:11
 */
@Service
public class IConfigService extends BaseService implements ConfigService {
	@Autowired
	private ConfigMapper mapper;

	@Override
	@Transactional
	public void del(Config config) {
		int delCount = 0;
		if (config != null && config.getId() != null) {
			config.setDeleteDate(new Date());
			delCount = mapper.deleteByPrimaryKey(config.getId());
		}
		if (delCount != 1) {
			throw new PrintException("配置重置失败");
		}
	}

	@Override
	public Config get(Integer id) {
		if (id == null) {
			return null;
		}
		return mapper.selectByPrimaryKey(id);
	}

	/**
	 * 查询指定公司所有印章的配置信息,如果印章没有配置信息，以全局配置为准
	 *
	 * @param orgId 公司ID
	 * @return 结果
	 */
	@Override
	public List<UserConfig> getByOrgIdAndKeyword(Integer orgId, String keyword) {
		if (orgId == null) {
			return null;
		}
		//查询公司配置列表
		List<UserConfig> configs = mapper.selectByOrgAndKeyword(orgId, keyword);

		if (configs == null || configs.isEmpty()) {
			return configs;
		}
		//查询默认配置
		Config defaultConfig = getDefaultConfig();

		for (UserConfig userConfig : configs) {
			String deviceVersion = userConfig.getDeviceVersion();

			if (StringUtils.isBlank(deviceVersion)) {
				userConfig.setDeviceVersion(defaultConfig.getVersion());
			}

			String svrHost = userConfig.getSvrHost();
			if (StringUtils.isBlank(svrHost)) {
				userConfig.setSvrHost(defaultConfig.getSvrHost());
			}

			String svrIp = userConfig.getSvrIp();

			if (StringUtils.isBlank(svrIp)) {
				userConfig.setSvrIp(defaultConfig.getSvrIp());
			}
		}
		return configs;
	}

	@Override
	public List<ConfigVo> getByKeyword(String keyword, Set<String> deviceIds) {
		if (deviceIds == null || deviceIds.isEmpty()) {
			deviceIds = null;
		}
		return mapper.selectByKeyword(keyword, deviceIds);
	}

	@Override
	@Transactional
	public void update(Config config) {
		int updateCount = 0;
		if (config != null && config.getId() != null) {
			config.setUpdateDate(new Date());
			updateCount = mapper.updateByPrimaryKey(config);
		}
		if (updateCount != 1) {
			throw new PrintException("配置信息更新失败");
		}
	}

	@Override
	@Transactional
	public void add(Config uuidVo) {
		int addCount = 0;
		if (uuidVo != null) {
			uuidVo.setId(null);
			uuidVo.setCreateDate(new Date());
			addCount = mapper.insert(uuidVo);
		}
		if (addCount != 1) {
			throw new PrintException("配置信息添加失败");
		}
	}

	/**
	 * 查询默认配置
	 */
	@Override
	public Config getDefaultConfig() {
		return getByUUID(Global.defaultUUID);
	}

	@Override
	public Config getByUUID(String uuid) {
		if (StringUtils.isBlank(uuid)) {
			return null;
		}
		//从数据库查
		Example example = new Example(Config.class);
		example.createCriteria().andIsNull("deleteDate")
				.andEqualTo("uuid", uuid);
		Config config = mapper.selectOneByExample(example);

		return config;
	}


	/**
	 * 批量增加配置信息
	 * @param configs  配置信息列表
	 */
	@Override
	@Transactional
	public void addBatch(List<Config> configs) {
		int addCount = 0;
		configs.forEach(uuidVo -> uuidVo.setCreateDate(new Date()));
		addCount = mapper.insertList(configs);
		if (addCount == 0) {
			throw new PrintException("配置信息添加失败");
		}
	}

	/**
	 * 批量修改配置信息
	 * @param updateConfigs 配置信息列表
	 */
	@Override
	@Transactional
	public void updateBatch(List<Config> updateConfigs) {
		int updateCount = 0;
		updateConfigs.forEach(config -> config.setUpdateDate(new Date()));
		updateCount = mapper.updateBatch(updateConfigs);
		if (updateCount == 0) {
			throw new PrintException("配置信息更新失败");
		}
	}
}
