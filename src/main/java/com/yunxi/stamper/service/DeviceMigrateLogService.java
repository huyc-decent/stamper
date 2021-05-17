package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.DeviceMigrateLog;
import com.yunxi.stamper.entityVo.MigrateVo;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2020/3/12 0012 14:32
 */
public interface DeviceMigrateLogService {
	void add(DeviceMigrateLog log);

	DeviceMigrateLog get(Integer Id);

	void update(DeviceMigrateLog log);

	List<MigrateVo> getList(Integer deviceId,Boolean filter);

	/***查询设备最新的迁移确认记录*/
	DeviceMigrateLog getLastByUUID(String uuid);

	/*批量新增迁徙记录*/
    void addBatch(List<DeviceMigrateLog> logs);
}
