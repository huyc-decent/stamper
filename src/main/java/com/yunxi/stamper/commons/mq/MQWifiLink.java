package com.yunxi.stamper.commons.mq;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/7/18 0018 10:38
 */
public class MQWifiLink {
	private Integer deviceId;//设备id
	private String ssid;//wifi名称
	private String password;//wifi密码

	public Integer getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
