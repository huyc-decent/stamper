package com.yunxi.stamper.commons.device.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Wifi请求实体类
 */
public class WifiListReq {
	@JsonProperty("UserID")
	public int userID;

	@JsonProperty("DeviceID")
    public int deviceID;

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public int getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(int deviceID) {
		this.deviceID = deviceID;
	}

	@Override
	public String toString() {
		return "WifiListReq{" +
				"userID=" + userID +
				", deviceID=" + deviceID +
				'}';
	}
}