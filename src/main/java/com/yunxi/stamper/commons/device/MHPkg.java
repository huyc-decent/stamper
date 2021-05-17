package com.yunxi.stamper.commons.device;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yunxi.stamper.commons.other.AppConstant;
import com.yunxi.stamper.commons.device.model.MHHead;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/1/18 0018 9:10
 */
public class MHPkg {

	@JsonProperty("Head")
	private MHHead head;//包头

	@JsonProperty("Body")
	private Object body;//包体

	@JsonProperty("Crc")
	private String crc;    //crc head和body的校验码

	//结束印章请求实体
	public static MHPkg end(Integer applicationID) {
		MHPkg res = new MHPkg();
		res.setCrc(applicationID + "");

		MHHead head = new MHHead();
		head.setMagic(AppConstant.MHPKG_MAGIC);
		head.setCmd(AppConstant.APPLICATION_END);
		head.setVersion(AppConstant.MHPKG_VERSION);

		res.setBody(new Object());

		res.setHead(head);

		return res;
	}

	public static MHPkg res(int cmd, Object body) {
		MHPkg kg = new MHPkg();
		MHHead head = new MHHead();
		head.setMagic(AppConstant.MHPKG_MAGIC);
		head.setCmd(cmd);
		head.setVersion(AppConstant.MHPKG_VERSION);
		kg.setHead(head);
		kg.setBody(body);
		return kg;
	}

	@JSONField(name = "Head")
	public MHHead getHead() {
		return head;
	}

	public void setHead(MHHead head) {
		this.head = head;
	}

	@JSONField(name = "Body")
	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
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
		return "MHPkg{" +
				"head=" + head +
				", body=" + body +
				", crc='" + crc + '\'' +
				'}';
	}
}
