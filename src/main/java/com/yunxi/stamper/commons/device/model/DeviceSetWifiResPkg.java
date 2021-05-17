package com.yunxi.stamper.commons.device.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DeviceSetWifiResPkg {
    @JsonProperty("Head")
	public MHHead head;
    @JsonProperty("Body")
    public DeviceSetWifiRes body;
    @JsonProperty("Crc")
    public String crc;

    @JSONField(name = "Head")
    public MHHead getHead() {
        return head;
    }

    public void setHead(MHHead head) {
        this.head = head;
    }

    @JSONField(name = "Body")
    public DeviceSetWifiRes getBody() {
        return body;
    }

    public void setBody(DeviceSetWifiRes body) {
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
        return "DeviceSetWifiResPkg{" +
                "head=" + head +
                ", body=" + body +
                ", crc='" + crc + '\'' +
                '}';
    }
}