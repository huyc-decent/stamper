package com.yunxi.stamper.mapper;


import com.yunxi.stamper.entity.DeviceMigrateLog;
import com.yunxi.stamper.entityVo.MigrateVo;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface DeviceMigrateLogMapper extends MyMapper<DeviceMigrateLog> {
	List<MigrateVo> selectList(Integer deviceId);

	List<MigrateVo> selectListDistinct(Integer deviceId);

	DeviceMigrateLog selectLastByUUID(String uuid);
}