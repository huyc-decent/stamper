package com.yunxi.stamper.commons.device.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author zhf_10@163.com
 * @Description 设备登陆的请求
 * @date 2019/1/15 0015 10:54
 */
public class DeviceLoginReq {
	@JsonProperty("DeviceLoginInfo")
	public DeviceLoginInfo deviceLoginInfo;
	@JsonProperty("JwtToken")
	public String jwtToken;

	@JSONField(name = "DeviceLoginInfo")
	public DeviceLoginInfo getDeviceLoginInfo() {
		return deviceLoginInfo;
	}

	public void setDeviceLoginInfo(DeviceLoginInfo deviceLoginInfo) {
		this.deviceLoginInfo = deviceLoginInfo;
	}

	@JSONField(name = "JwtToken")
	public String getJwtToken() {
		return jwtToken;
	}

	public void setJwtToken(String jwtToken) {
		this.jwtToken = jwtToken;
	}

	@Override
	public String toString() {
		return "DeviceLoginReq{" +
				"deviceLoginInfo=" + deviceLoginInfo +
				", jwtToken='" + jwtToken + '\'' +
				'}';
	}
}
