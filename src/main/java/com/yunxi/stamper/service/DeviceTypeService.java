package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.DeviceType;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/2 0002 20:29
 */
public interface DeviceTypeService {
	DeviceType getByNameFromOrg(String name, Integer orgId);

	void add(DeviceType type);

	DeviceType get(Integer deviceTypeId);

	List<DeviceType> getByOrg(Integer orgId);

	void createTypesByOrgInit(Integer orgId);
}
