package com.yunxi.stamper.commons.device.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lindatung on 28/02/2018.
 */

public class SealRecordInfoRes {
    @JsonProperty("Ret")
    public String ret;//
    @JsonProperty("RecordID")
    public int recordID;

    @JSONField(name = "Ret")
    public String getRet() {
        return ret;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }

    @JSONField(name = "RecordID")
    public int getRecordID() {
        return recordID;
    }

    public void setRecordID(int recordID) {
        this.recordID = recordID;
    }

    @Override
    public String toString() {
        return "SealRecordInfoRes{" +
                "ret='" + ret + '\'' +
                ", recordID=" + recordID +
                '}';
    }
}
