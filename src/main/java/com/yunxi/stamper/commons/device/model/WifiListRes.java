package com.yunxi.stamper.commons.device.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * wifi响应实体类
 */
public class WifiListRes {
	@JSONField(name = "Ret")
	public  int ret;

	@JSONField(name = "Msg")
    public String msg;

	@JSONField(name = "UserID")
    public int userID;

	@JSONField(name = "WifiList")
    public List<Object> wifiList;

	public int getRet() {
		return ret;
	}

	public void setRet(int ret) {
		this.ret = ret;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public List<Object> getWifiList() {
		return wifiList;
	}

	public void setWifiList(List<Object> wifiList) {
		this.wifiList = wifiList;
	}

	@Override
	public String toString() {
		return "WifiListRes{" +
				"ret=" + ret +
				", msg='" + msg + '\'' +
				", userID=" + userID +
				", wifiList=" + wifiList +
				'}';
	}
}