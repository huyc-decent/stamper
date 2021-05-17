package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.DeviceMigrateLog;
import com.yunxi.stamper.entityVo.MigrateVo;
import com.yunxi.stamper.mapper.DeviceMigrateLogMapper;
import com.yunxi.stamper.service.DeviceMigrateLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2020/3/12 0012 14:32
 */
@Service
public class IDeviceMigrateLogService implements DeviceMigrateLogService {
	@Autowired
	private DeviceMigrateLogMapper mapper;

	@Override
	@Transactional
	public void add(DeviceMigrateLog log) {
		log.setCreateDate(new Date());
		mapper.insert(log);
	}

	@Override
	public DeviceMigrateLog get(Integer Id) {
		if (Id == null) {
			return null;
		}
		return mapper.selectByPrimaryKey(Id);
	}

	@Override
	@Transactional
	public void update(DeviceMigrateLog log) {
		int update = 0;
		if (log != null) {
			log.setUpdateDate(new Date());
			update = mapper.updateByPrimaryKey(log);
		}
		if (update != 1) {
			throw new PrintException("迁移日志更新失败" + log.toString());
		}
	}

	@Override
	public List<MigrateVo> getList(Integer deviceId, Boolean filter) {
		if (filter) {
			return mapper.selectListDistinct(deviceId);
		}
		return mapper.selectList(deviceId);
	}

	/***
	 * 查询设备最新的迁移确认记录
	 * @param uuid
	 * @return 结果
	 */
	@Override
	public DeviceMigrateLog getLastByUUID(String uuid) {
		if (StringUtils.isBlank(uuid)) {
			return null;
		}
		return mapper.selectLastByUUID(uuid);
	}

	@Override
	@Transactional
	public void addBatch(List<DeviceMigrateLog> logs) {
		logs.forEach(log -> log.setCreateDate(new Date()));
		mapper.insertList(logs);
	}
}
