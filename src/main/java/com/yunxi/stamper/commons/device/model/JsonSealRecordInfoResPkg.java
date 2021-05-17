package com.yunxi.stamper.commons.device.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JsonSealRecordInfoResPkg {
    @JsonProperty("ReqAuthData")
    public SealRecordInfoRes data;
    @JsonProperty("Error")
    public int error;
    @JsonProperty("Msg")
    public String msg;

    public static JsonSealRecordInfoResPkg ok(){
        JsonSealRecordInfoResPkg pkg = new JsonSealRecordInfoResPkg();
        pkg.setData(new SealRecordInfoRes());
        pkg.setError(0);
        pkg.setMsg("");
        return pkg;
    }

    public static JsonSealRecordInfoResPkg no(){
        JsonSealRecordInfoResPkg pkg = new JsonSealRecordInfoResPkg();
        pkg.setData(null);
        pkg.setError(1);
        pkg.setMsg(null);
        return pkg;
    }

    @JSONField(name = "ReqAuthData")
    public SealRecordInfoRes getData() {
        return data;
    }

    public void setData(SealRecordInfoRes data) {
        this.data = data;
    }

    @JSONField(name = "Error")
    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
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
        return "JsonSealRecordInfoResPkg{" +
                "data=" + data +
                ", error=" + error +
                ", msg='" + msg + '\'' +
                '}';
    }
}