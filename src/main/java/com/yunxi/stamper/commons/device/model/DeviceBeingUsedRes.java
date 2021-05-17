package com.yunxi.stamper.commons.device.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yunxi.stamper.commons.device.modelVo.LoginApplication;

import java.util.List;

public class DeviceBeingUsedRes {
	@JsonProperty("UsedStatus")
	public int usedStatus;//0:关锁 1:开锁

	@JsonProperty("FingerUserId")
	public int fingerUserId;//开锁人id(仅开锁状态存在)

	private List<LoginApplication> loginApplicationInfo;//印章最近1次申请单已使用记录

	public int getUsedStatus() {
		return usedStatus;
	}

	public void setUsedStatus(int usedStatus) {
		this.usedStatus = usedStatus;
	}

	public int getFingerUserId() {
		return fingerUserId;
	}

	public void setFingerUserId(int fingerUserId) {
		this.fingerUserId = fingerUserId;
	}

	public List<LoginApplication> getLoginApplicationInfo() {
		return loginApplicationInfo;
	}

	public void setLoginApplicationInfo(List<LoginApplication> loginApplicationInfo) {
		this.loginApplicationInfo = loginApplicationInfo;
	}

	@Override
	public String toString() {
		String sb = "DeviceBeingUsedRes{" + "usedStatus=" + usedStatus +
				", fingerUserId=" + fingerUserId +
				", loginApplicationInfo=" + loginApplicationInfo +
				'}';
		return sb;
	}
}
