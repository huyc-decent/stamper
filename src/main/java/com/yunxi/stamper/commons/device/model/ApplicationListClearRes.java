package com.yunxi.stamper.commons.device.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 设备申请单清空响应实体类
 */
public class ApplicationListClearRes {
    @JSONField(name = "UserID")
    public int userID;

    @JSONField(name = "DeivceID")
    public int deivceID;

    @JSONField(name = "Res")
    public int res;

    @JSONField(name = "Msg")
    public String msg;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getDeivceID() {
        return deivceID;
    }

    public void setDeivceID(int deivceID) {
        this.deivceID = deivceID;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ApplicationListClearRes{" +
                "userID=" + userID +
                ", deivceID=" + deivceID +
                ", res=" + res +
                ", msg='" + msg + '\'' +
                '}';
    }
}

