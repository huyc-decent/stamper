package com.yunxi.stamper.commons.device.modelVo;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/7/26 0026 15:56
 */
public class DeviceSleepTime {
	private Integer deviceId;//印章id
	private Integer sleepTime;//休眠时间 2~10 分钟
	private Integer res;//0成功 1失败

	public Integer getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}

	public Integer getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(Integer sleepTime) {
		this.sleepTime = sleepTime;
	}

	public Integer getRes() {
		return res;
	}

	public void setRes(Integer res) {
		this.res = res;
	}
}
