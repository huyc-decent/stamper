package com.yunxi.stamper.commons.device.modelVo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yunxi.stamper.commons.device.model.MHHead;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/8/6 0006 17:58
 */
public class DeviceSleepTimePkg {

	@JsonProperty("Head")
	private MHHead head;//包头

	@JsonProperty("Body")
	private DeviceSleepTime body;//包体

	@JsonProperty("Crc")
	private String crc;

	public MHHead getHead() {
		return head;
	}

	public void setHead(MHHead head) {
		this.head = head;
	}

	public DeviceSleepTime getBody() {
		return body;
	}

	public void setBody(DeviceSleepTime body) {
		this.body = body;
	}

	public String getCrc() {
		return crc;
	}

	public void setCrc(String crc) {
		this.crc = crc;
	}
}
