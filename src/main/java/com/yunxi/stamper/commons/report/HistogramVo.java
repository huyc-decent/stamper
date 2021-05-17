package com.yunxi.stamper.commons.report;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/21 0021 18:01
 */
public class HistogramVo {
	private Integer total;//使用总次数
	private String deviceName;//印章名称
	private Integer deviceId;//印章ID

	public Integer getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
}
