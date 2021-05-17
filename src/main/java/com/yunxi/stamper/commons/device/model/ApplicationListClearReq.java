package com.yunxi.stamper.commons.device.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by lindatung on 13/03/2018.
 */

public class ApplicationListClearReq {
    @JSONField(name = "UserID")
    public int userID;
    @JSONField(name = "DeviceID")
    public int deviceID;

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

    @Override
    public String toString() {
        return "ApplicationListClearReq{" +
                "userID=" + userID +
                ", deviceID=" + deviceID +
                '}';
    }
}
