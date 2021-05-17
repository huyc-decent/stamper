package com.yunxi.stamper.commons.device.model;


import com.alibaba.fastjson.annotation.JSONField;

public class ApplicationStatusReq {
	public int ApplicationID; //申请单id
	public String ApplicationTitle;//申请单标题
	public String ApplicationToken; //token
	public Integer isQss;//加密方式
	public int status;//申请单当前状态
	public int totalCount;//总次数
	public int needCount;//已盖次数
	public int UseCount; //当前次数
	public int UserID; //推送人id
	public String UserName; //推送人姓名
	public int FingerAddr;

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getNeedCount() {
		return needCount;
	}

	public void setNeedCount(int needCount) {
		this.needCount = needCount;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Integer getIsQss() {
		return isQss;
	}

	public void setIsQss(Integer isQss) {
		this.isQss = isQss;
	}

	public String getApplicationTitle() {
		return ApplicationTitle;
	}

	public void setApplicationTitle(String applicationTitle) {
		ApplicationTitle = applicationTitle;
	}

	@JSONField(name = "ApplicationID")
	public int getApplicationID() {
		return ApplicationID;
	}

	public void setApplicationID(int applicationID) {
		ApplicationID = applicationID;
	}

	@JSONField(name = "ApplicationToken")
	public String getApplicationToken() {
		return ApplicationToken;
	}

	public void setApplicationToken(String applicationToken) {
		ApplicationToken = applicationToken;
	}

	@JSONField(name = "UseCount")
	public int getUseCount() {
		return UseCount;
	}

	public void setUseCount(int useCount) {
		UseCount = useCount;
	}

	@JSONField(name = "UserID")
	public int getUserID() {
		return UserID;
	}

	public void setUserID(int userID) {
		UserID = userID;
	}

	@JSONField(name = "UserName")
	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	@JSONField(name = "FingerAddr")
	public int getFingerAddr() {
		return FingerAddr;
	}

	public void setFingerAddr(int fingerAddr) {
		FingerAddr = fingerAddr;
	}

	@Override
	public String toString() {
		return "ApplicationStatusReq{" +
				"ApplicationID=" + ApplicationID +
				", ApplicationToken='" + ApplicationToken + '\'' +
				", UseCount=" + UseCount +
				", UserID=" + UserID +
				", UserName='" + UserName + '\'' +
				", FingerAddr=" + FingerAddr +
				'}';
	}
}