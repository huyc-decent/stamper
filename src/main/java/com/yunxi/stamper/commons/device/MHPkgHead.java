package com.yunxi.stamper.commons.device;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * @author zhf_10@163.com
 * @Description 包头
 * @date 2019/1/18 0018 9:11
 */
public class MHPkgHead implements Serializable{
	private static final long serialVersionUID = 1L;
	private int magic;

	private int cmd;

	private int headLen;

	private int bodyLen;

	private int version;

	private int serialNum;

	@JSONField(name = "Magic")
	public int getMagic() {
		return magic;
	}

	public void setMagic(int magic) {
		this.magic = magic;
	}

	@JSONField(name = "Cmd")
	public int getCmd() {
		return cmd;
	}

	public void setCmd(int cmd) {
		this.cmd = cmd;
	}

	@JSONField(name = "HeadLen")
	public int getHeadLen() {
		return headLen;
	}

	public void setHeadLen(int headLen) {
		this.headLen = headLen;
	}

	@JSONField(name = "BodyLen")
	public int getBodyLen() {
		return bodyLen;
	}

	public void setBodyLen(int bodyLen) {
		this.bodyLen = bodyLen;
	}

	@JSONField(name = "Version")
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@JSONField(name = "SerialNum")
	public int getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(int serialNum) {
		this.serialNum = serialNum;
	}

	@Override
	public String toString() {
		return "MHPkgHead{" +
				"magic=" + magic +
				", cmd=" + cmd +
				", headLen=" + headLen +
				", bodyLen=" + bodyLen +
				", version=" + version +
				", serialNum=" + serialNum +
				'}';
	}
}
