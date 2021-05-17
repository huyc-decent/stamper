package com.yunxi.stamper.commons.device.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lindatung on 06/12/2017.
 */

public class SealRecordInfoDelRes {
    @JsonProperty("Ret")
    public int ret;//成功就返回0
    @JsonProperty("Msg")
    public String msg;
    @JsonProperty("PicUrl")
    public String picUrl;

    @JSONField(name = "Ret")
    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }
    @JSONField(name = "Msg")
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    @JSONField(name = "PicUrl")
    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    @Override
    public String toString() {
        return "SealRecordInfoDelRes{" +
                "ret=" + ret +
                ", msg='" + msg + '\'' +
                ", picUrl='" + picUrl + '\'' +
                '}';
    }
}
