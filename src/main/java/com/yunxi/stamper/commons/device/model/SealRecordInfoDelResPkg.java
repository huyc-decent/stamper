package com.yunxi.stamper.commons.device.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yunxi.stamper.commons.other.AppConstant;

public class SealRecordInfoDelResPkg {
    @JsonProperty("Head")
	public MHHead head;
    @JsonProperty("Body")
    public SealRecordInfoDelRes body;
    @JsonProperty("Crc")
    public String crc;

    public static SealRecordInfoDelResPkg repeat(String picUrl){
        SealRecordInfoDelResPkg pkg = new SealRecordInfoDelResPkg();
        pkg.setHead(MHHead.ok(AppConstant.SEAL_RECORD_INFO_DEL_RES));
		SealRecordInfoDelRes body = new SealRecordInfoDelRes();
		body.setRet(0);
		body.setPicUrl(picUrl);
        pkg.setBody(body);
        return pkg;
    }

    public static SealRecordInfoDelResPkg ok(SealRecordInfoDelRes body){
		SealRecordInfoDelResPkg pkg = new SealRecordInfoDelResPkg();
		pkg.setHead(MHHead.ok(AppConstant.SEAL_RECORD_INFO_DEL_RES));
		pkg.setBody(body);
		return pkg;
	}

    @JSONField(name = "Head")
    public MHHead getHead() {
        return head;
    }

    public void setHead(MHHead head) {
        this.head = head;
    }

    @JSONField(name = "Body")
    public SealRecordInfoDelRes getBody() {
        return body;
    }

    public void setBody(SealRecordInfoDelRes body) {
        this.body = body;
    }

    @JSONField(name = "Crc")
    public String getCrc() {
        return crc;
    }

    public void setCrc(String crc) {
        this.crc = crc;
    }

    @Override
    public String toString() {
        return "SealRecordInfoDelResPkg{" +
                "head=" + head +
                ", body=" + body +
                ", crc='" + crc + '\'' +
                '}';
    }
}