package com.yunxi.stamper.commons.device.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lindatung on 13/03/2018.
 */

public class FingerPrintClearRes {
    @JsonProperty("UserID")
    private int userID;
    @JsonProperty("DeviceID")
	private int deviceID;
    @JsonProperty("Res")
	private int res;
    @JsonProperty("Msg")
	private String msg;
    @JsonProperty("FingerAddr")
    private int fingerAddr;

    public int getFingerAddr() {
        return fingerAddr;
    }

    public void setFingerAddr(int fingerAddr) {
        this.fingerAddr = fingerAddr;
    }

    @JSONField(name = "UserID")
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    @JSONField(name = "DeivceID")
    public int getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(int deviceID) {
        this.deviceID = deviceID;
    }



    @JSONField(name = "Res")
    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
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
        return "FingerPrintClearRes{" +
                "userID=" + userID +
                ", deviceID=" + deviceID +
                ", res=" + res +
                ", msg='" + msg + '\'' +
                '}';
    }
}

