package com.yunxi.stamper.mapper;


import com.yunxi.stamper.entity.ApplicationDevice;
import com.yunxi.stamper.entityVo.DeviceSelectVo;
import com.yunxi.stamper.entityVo.UseCountVo;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import io.lettuce.core.dynamic.annotation.CommandNaming;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ApplicationDeviceMapper extends MyMapper<ApplicationDevice> {

	//查询申请单对应的印章id+名称
	List<DeviceSelectVo> selectByApplicationID(Integer applicationId);

	//查询该申请单 印章已盖次数
	List<UseCountVo> selectUseCountByApplication(Integer applicationId);

	//指定申请单使用次数+1
	void signetMinus1(Integer applicationID, Integer deviceID);

}