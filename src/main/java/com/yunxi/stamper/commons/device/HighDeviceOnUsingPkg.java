package com.yunxi.stamper.commons.device;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yunxi.stamper.commons.device.model.MHHead;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/3 0003 2:26
 */
public class HighDeviceOnUsingPkg {
	@JsonProperty("Head")
	private MHHead head;//包头

	@JsonProperty("Body")
	private HighDeviceOnUseRes body;//包体

	@JsonProperty("Crc")
	private String crc;	//crc head和body的校验码

	public MHHead getHead() {
		return head;
	}

	public void setHead(MHHead head) {
		this.head = head;
	}

	public HighDeviceOnUseRes getBody() {
		return body;
	}

	public void setBody(HighDeviceOnUseRes body) {
		this.body = body;
	}

	public String getCrc() {
		return crc;
	}

	public void setCrc(String crc) {
		this.crc = crc;
	}
}
