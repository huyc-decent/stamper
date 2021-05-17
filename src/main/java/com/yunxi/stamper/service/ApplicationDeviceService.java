package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.ApplicationDevice;
import com.yunxi.stamper.entityVo.DeviceSelectVo;
import com.yunxi.stamper.entityVo.UseCountVo;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/3 0003 17:02
 */
public interface ApplicationDeviceService {
	ApplicationDevice get(Integer applicationId, Integer signetId);

	void update(ApplicationDevice applicationDevice);

	void add(ApplicationDevice ad);

	List<ApplicationDevice> getByApplication(Integer applicationId);

	//查询申请单对应的印章id+名称
	List<DeviceSelectVo> getByApplicationId(Integer applicationId);

	//印章已使用,次数-1
	void signetMinus1(Integer applicationID, Integer deviceID);

	//查询该申请单 印章已盖次数
	List<UseCountVo> getUseCountByApplication(Integer applicationId);

	//查询申请单对应印章的 记录
	ApplicationDevice getByApplicationAndSignet(Integer applicationId, Integer deviceId);
}
