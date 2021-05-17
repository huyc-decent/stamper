package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.Threshold;
import com.yunxi.stamper.entityVo.ThresholdEntity;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface ThresholdService {
    List<Threshold> get(Integer orgId, String keyword);

    Threshold getByDeviceId(Integer deviceId, Integer orgId);

    void update(Threshold byDeviceId);

    void add(Threshold threshold);

	void del(Threshold threshold);

    //查询指定公司全局默认阈值
	Threshold getDefaultByOrg(Integer orgId);

	//查询指定印章的阈值,如果印章不存在阈值,则查询印章所属公司的阈值,如果也没有,返回默认100%
	int getTsValue(Integer deviceId, Integer orgId);

	/**
	 * 查询管理的印章阈值列表
	 * @param orgId 集团ID
	 * @return
	 */
	List<ThresholdEntity> getByOrgAndDepartmentAndKeeper(Integer orgId,  String keyword);

	/**
	 * 更新阈值信息
	 * @param orgId 集团ID
	 * @param deviceId 设备ID 0：全局阈值
	 * @param thresholdValue 阈值
	 */
	void updateThreshold(@NotNull Integer orgId, @NotNull Integer deviceId, @NotNull Integer thresholdValue);

	List<Threshold> getByDevice(Integer signetId);

}
