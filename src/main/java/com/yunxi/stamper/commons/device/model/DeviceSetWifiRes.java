package com.yunxi.stamper.commons.device.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lindatung on 26/01/2018.
 */

public class DeviceSetWifiRes {
    @JsonProperty("Ret")
    private int ret;
    @JsonProperty("UserID")
	private int userID;
    @JsonProperty("DeviceID")
	private int deviceID;
    @JsonProperty("Msg")
	private String msg;


    @JSONField(name = "Ret")
    public int getRet() {
        return ret;
    }
    public void setRet(int ret) {
        this.ret = ret;
    }

    @JSONField(name = "UserID")
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    @JSONField(name = "DeviceID")
    public int getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(int deviceID) {
        this.deviceID = deviceID;
    }

    @JSONField(name = "Msg")
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "DeviceSetWifiRes{" +
                "ret=" + ret +
                ", userID=" + userID +
                ", deviceID=" + deviceID +
                ", msg='" + msg + '\'' +
                '}';
    }
}
