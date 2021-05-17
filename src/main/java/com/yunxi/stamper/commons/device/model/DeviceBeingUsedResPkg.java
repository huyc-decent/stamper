package com.yunxi.stamper.commons.device.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeviceBeingUsedResPkg {
	@JsonProperty("Head")
	public MHHead head;
	@JsonProperty("Body")
	public DeviceBeingUsedRes body;
	@JsonProperty("Crc")
    public String crc;

	public MHHead getHead() {
		return head;
	}

	public void setHead(MHHead head) {
		this.head = head;
	}

	public DeviceBeingUsedRes getBody() {
		return body;
	}

	public void setBody(DeviceBeingUsedRes body) {
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
		return "DeviceBeingUsedResPkg{" +
				"head=" + head +
				", body=" + body +
				", crc='" + crc + '\'' +
				'}';
	}
}
