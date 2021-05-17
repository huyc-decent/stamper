package com.yunxi.stamper.commons.device.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 印章解锁请求实体类
 */
public class CSDeviceUnlockReq {
    @JSONField(name = "UserID")
    public int userID;

    @JSONField(name = "DeviceID")
    public int deviceID;

    @JSONField(name = "UserName")
    public String userName;

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

    @Override
    public String toString() {
        return "CSDeviceUnlockReq{" +
                "userID=" + userID +
                ", deviceID=" + deviceID +
                ", userName='" + userName + '\'' +
                '}';
    }
}
