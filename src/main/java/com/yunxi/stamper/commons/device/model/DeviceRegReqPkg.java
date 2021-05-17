package com.yunxi.stamper.commons.device.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeviceRegReqPkg {
    @JsonProperty("Head")
	public MHHead head;

    @JsonProperty("Body")
    public DeviceRegReq body;

    @JsonProperty("Crc")
    public String crc;

	public MHHead getHead() {
		return head;
	}

	public void setHead(MHHead head) {
		this.head = head;
	}

	public DeviceRegReq getBody() {
		return body;
	}

	public void setBody(DeviceRegReq body) {
		this.body = body;
	}

	public String getCrc() {
		return crc;
	}

	public void setCrc(String crc) {
		this.crc = crc;
	}

	@Override
	public String toString() {
		return "DeviceRegReqPkg{" +
				"head=" + head +
				", body=" + body +
				", crc='" + crc + '\'' +
				'}';
	}
}