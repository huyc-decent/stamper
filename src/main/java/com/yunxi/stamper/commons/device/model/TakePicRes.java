package com.yunxi.stamper.commons.device.model;

import com.alibaba.fastjson.annotation.JSONField;

public class TakePicRes {
	@JSONField(name = "UseTimes")
	public Integer useTimes;
	@JSONField(name = "DeviceID")
	public Integer deviceID;

	@JSONField(name = "ApplicationID")
	public Integer applicationID;

	@JSONField(name = "Res")
	public Integer res;

	@Override
	public String toString() {
		return "TakePicRes{" +
				"useTimes=" + useTimes +
				", deviceID=" + deviceID +
				", applicationID=" + applicationID +
				", res=" + res +
				'}';
	}

	public Integer getUseTimes() {
		return useTimes;
	}

	public void setUseTimes(Integer useTimes) {
		this.useTimes = useTimes;
	}

	public Integer getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(Integer deviceID) {
		this.deviceID = deviceID;
	}

	public Integer getApplicationID() {
		return applicationID;
	}

	public void setApplicationID(Integer applicationID) {
		this.applicationID = applicationID;
	}

	public Integer getRes() {
		return res;
	}

	public void setRes(Integer res) {
		this.res = res;
	}
}