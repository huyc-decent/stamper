package com.yunxi.stamper.commons.device.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/1/18 0018 11:37
 */
public class FingerPrintRecordReq {
	@JSONField(name = "UserID")
	private int userID;  //指纹对应的用章人id

	@JSONField(name = "DeviceID")
	private int deviceID;//印章id

	@JSONField(name = "UserName")
	private String userName;//用章人姓名 指纹验证通过后屏幕显示的名字

	@JSONField(name = "FingerAddr")
	private int fingerAddr;//用章人指纹存储位置

	@JSONField(name = "CodeId")
	private int codeId;//发起指纹录入指令的用户id

	@JSONField(name = "CodeName")
	private String codeName;//发起指纹录入指令的用户姓名

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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getFingerAddr() {
		return fingerAddr;
	}

	public void setFingerAddr(int fingerAddr) {
		this.fingerAddr = fingerAddr;
	}

	public int getCodeId() {
		return codeId;
	}

	public void setCodeId(int codeId) {
		this.codeId = codeId;
	}

	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}

	@Override
	public String toString() {
		return "FingerPrintRecordReq{" + "userID=" + userID +
				", deviceID=" + deviceID +
				", userName='" + userName + '\'' +
				", fingerAddr=" + fingerAddr +
				", codeId=" + codeId +
				", codeName='" + codeName + '\'' +
				'}';
	}
}
