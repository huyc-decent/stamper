package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.DeviceMessage;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/17 0017 16:41
 */
public interface DeviceMessageService {
	void add(DeviceMessage deviceMessage);

	//添加或者更新离线消息(用于只能推送1次的离线消息)
	void addOrUpdate(DeviceMessage deviceMessage);

	//查询印章离线消息列表
	List<DeviceMessage> getBySignet(Integer signetId);

	void update(DeviceMessage dm);

	//查询最后一个未推送成功的指令(指定title)
	DeviceMessage getByNameAndSignetAndStatus(String title, Integer signetId, Integer status);

}
