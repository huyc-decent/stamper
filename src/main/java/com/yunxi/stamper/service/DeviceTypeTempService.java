package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.DeviceTypeTemp;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/4/29 0029 16:02
 */
public interface DeviceTypeTempService {
	DeviceTypeTemp getByName(String typeName);

	void add(DeviceTypeTemp temp);

	//查询系统默认模板列表
	List<DeviceTypeTemp> getAllTemps();

}
