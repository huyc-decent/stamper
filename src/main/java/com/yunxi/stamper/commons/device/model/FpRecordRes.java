package com.yunxi.stamper.commons.device.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lindatung on 26/01/2018.
 */

public class FpRecordRes {
    @JsonProperty("UserID")
    private int userID; //指纹所属人id
    @JsonProperty("DeviceID")
    private int deviceID; //设备ID
    @JsonProperty("Res")
    private int res; //状态 0:录入成功
    @JsonProperty("Msg")
    private String msg; //备注消息
    @JsonProperty("UserName")
    private String userName; //指纹所属人姓名
    @JsonProperty("FingerAddr")
    private int fingerAddr; //指纹录入地址0~3000
    @JsonProperty("CodeID")
    private int codeID; //下发指令用户id

    @JSONField(name = "UserName")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @JSONField(name = "FingerAddr")
    public int getFingerAddr() {
        return fingerAddr;
    }

    public void setFingerAddr(int fingerAddr) {
        this.fingerAddr = fingerAddr;
    }

    @JSONField(name = "CodeID")
    public int getCodeID() {
        return codeID;
    }

    public void setCodeID(int codeID) {
        this.codeID = codeID;
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
        return "FpRecordRes{" + "userID=" + userID +
                ", deviceID=" + deviceID +
                ", res=" + res +
                ", msg='" + msg + '\'' +
                ", userName='" + userName + '\'' +
                ", fingerAddr=" + fingerAddr +
                ", codeID=" + codeID +
                '}';
    }
}
