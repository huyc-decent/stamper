package com.yunxi.stamper.commons.device.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class  DeviceRegReq {
	@JsonProperty("DeviceLoginInfo")
	public DeviceLoginInfo deviceLoginInfo;

	public DeviceLoginInfo getDeviceLoginInfo() {
		return deviceLoginInfo;
	}

	public void setDeviceLoginInfo(DeviceLoginInfo deviceLoginInfo) {
		this.deviceLoginInfo = deviceLoginInfo;
	}

	@Override
	public String toString() {
		return "DeviceRegReq{" +
				"deviceLoginInfo=" + deviceLoginInfo +
				'}';
	}
}