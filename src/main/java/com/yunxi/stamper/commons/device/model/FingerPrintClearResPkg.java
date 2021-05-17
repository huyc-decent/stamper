package com.yunxi.stamper.commons.device.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FingerPrintClearResPkg {
    @JsonProperty("Head")
	private MHHead head;
    @JsonProperty("Body")
	private FingerPrintClearRes body;
    @JsonProperty("Crc")
	private String crc;

    @JSONField(name = "Head")
    public MHHead getHead() {
        return head;
    }

    public void setHead(MHHead head) {
        this.head = head;
    }

    @JSONField(name = "Body")
    public FingerPrintClearRes getBody() {
        return body;
    }

    public void setBody(FingerPrintClearRes body) {
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
		return "FingerPrintClearResPkg{" +
				"head=" + head +
				", body=" + body +
				", crc='" + crc + '\'' +
				'}';
	}
}