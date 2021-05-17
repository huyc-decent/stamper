package com.yunxi.stamper.commons.device.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lindatung on 26/01/2018.
 */

public class DeviceSetWifiReq {
    @JsonProperty("UserID")
    private int userID;
    @JsonProperty("DeviceID")
    private int deviceID;
    @JsonProperty("SSID")
    private String ssid;
    @JsonProperty("WifiPwd")
    private String wifiPwd;

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

    @JSONField(name = "SSID")
    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    @JSONField(name = "WifiPwd")
    public String getWifiPwd() {
        return wifiPwd;
    }

    public void setWifiPwd(String wifiPwd) {
        this.wifiPwd = wifiPwd;
    }

    @Override
    public String toString() {
        return "DeviceSetWifiReq{" +
                "userID=" + userID +
                ", deviceID=" + deviceID +
                ", ssid='" + ssid + '\'' +
                ", wifiPwd='" + wifiPwd + '\'' +
                '}';
    }
}
