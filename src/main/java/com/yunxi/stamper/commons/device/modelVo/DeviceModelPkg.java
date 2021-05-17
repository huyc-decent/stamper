package com.yunxi.stamper.commons.device.modelVo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yunxi.stamper.commons.device.model.MHHead;

/**
 * @author zhf_10@163.com
 * @Description 使用模式返回数据包实例
 * @date 2019/8/6 0006 17:32
 */
public class DeviceModelPkg {
	@JsonProperty("Head")
	private MHHead head;//包头

	@JsonProperty("Body")
	private DeviceModel body;//包体

	@JsonProperty("Crc")
	private String crc;

	public MHHead getHead() {
		return head;
	}

	public void setHead(MHHead head) {
		this.head = head;
	}

	public DeviceModel getBody() {
		return body;
	}

	public void setBody(DeviceModel body) {
		this.body = body;
	}

	public String getCrc() {
		return crc;
	}

	public void setCrc(String crc) {
		this.crc = crc;
	}
}
