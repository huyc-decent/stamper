package com.yunxi.stamper.commons.device;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/3 0003 2:27
 */
public class HighDeviceOnUseRes {
	@JsonProperty("UseTimes")
	private Integer useTimes;

	@JsonProperty("ApplicationID")
	private Integer applicationID;

	@JsonProperty("DeviceId")
	private Integer deviceId;

	public Integer getUseTimes() {
		return useTimes;
	}

	public void setUseTimes(Integer useTimes) {
		this.useTimes = useTimes;
	}

	public Integer getApplicationID() {
		return applicationID;
	}

	public void setApplicationID(Integer applicationID) {
		this.applicationID = applicationID;
	}

	public Integer getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}
}
