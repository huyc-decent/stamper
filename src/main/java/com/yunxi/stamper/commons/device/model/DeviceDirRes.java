package com.yunxi.stamper.commons.device.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author zhf_10@163.com
 * @Description getDir响应体
 * @date 2019/1/15 0015 13:12
 */
public class DeviceDirRes {
	@JsonProperty("deviceid")
	private int deviceId;

	@JsonProperty("stm32uuid")
	private String stm32uuid;

	@JsonProperty("svrurl")
	private String svrurl;

	@JsonProperty("svrhost")
	private String svrhost;

	@JsonProperty("svrhttps")
	private String svrhttps;

	@JsonProperty("orgcode")
	private String orgcode;

	@JsonProperty("sealrecordinfourl")
	private String sealRecordInfoUrl;

	public String getSealRecordInfoUrl() {
		return sealRecordInfoUrl;
	}

	public void setSealRecordInfoUrl(String sealRecordInfoUrl) {
		this.sealRecordInfoUrl = sealRecordInfoUrl;
	}

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public String getStm32uuid() {
		return stm32uuid;
	}

	public void setStm32uuid(String stm32uuid) {
		this.stm32uuid = stm32uuid;
	}

	public String getSvrurl() {
		return svrurl;
	}

	public void setSvrurl(String svrurl) {
		this.svrurl = svrurl;
	}

	public String getSvrhost() {
		return svrhost;
	}

	public void setSvrhost(String svrhost) {
		this.svrhost = svrhost;
	}

	public String getSvrhttps() {
		return svrhttps;
	}

	public void setSvrhttps(String svrhttps) {
		this.svrhttps = svrhttps;
	}

	public String getOrgcode() {
		return orgcode;
	}

	public void setOrgcode(String orgcode) {
		this.orgcode = orgcode;
	}

	@Override
	public String toString() {
		return "DeviceDirRes{" +
				"deviceId=" + deviceId +
				", stm32uuid='" + stm32uuid + '\'' +
				", svrurl='" + svrurl + '\'' +
				", svrhost='" + svrhost + '\'' +
				", svrhttps='" + svrhttps + '\'' +
				", orgcode='" + orgcode + '\'' +
				", sealRecordInfoUrl='" + sealRecordInfoUrl + '\'' +
				'}';
	}
}
