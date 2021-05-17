package com.yunxi.stamper.commons.mq;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/7/18 0018 10:35
 */
public class MQWifiList {
	private Integer deviceId;//印章id

	public MQWifiList(Integer signetId) {
		this.deviceId = signetId;
	}

	public Integer getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}
}
