package com.yunxi.stamper.commons.device.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * wifi信息响应实体类
 */
public class WifiInfoRes {
	@JSONField(name = "netType")
	public int netType;//0-无网络  1-数据  2-wifi

	@JSONField(name = "SSID")
	public String SSID;

	@JSONField(name = "WifiPwd")
	public String wifiPwd;

	@JSONField(name = "uuid")
	public String uuid;

	public String getWifiPwd() {
		return wifiPwd;
	}

	public void setWifiPwd(String wifiPwd) {
		this.wifiPwd = wifiPwd;
	}

	public int deviceId;//设备id

	public int getNetType() {
		return netType;
	}

	public void setNetType(int netType) {
		this.netType = netType;
	}

	public String getSSID() {
		return SSID;
	}

	public void setSSID(String SSID) {
		this.SSID = SSID;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	@Override
	public String toString() {
		return "WifiInfoRes{" +
				"netType=" + netType +
				", SSID='" + SSID + '\'' +
				", uuid=" + uuid +
				'}';
	}
}