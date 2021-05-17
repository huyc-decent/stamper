package com.yunxi.stamper.commons.device.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.yunxi.stamper.commons.other.AppConstant;

public class MHHead {
	@JSONField(name = "Magic")
	public long magic;

	@JSONField(name = "Cmd")
	public int cmd;

	@JSONField(name = "Version")
	public int version;

	@JSONField(name = "SerialNum")
	public int serialNum;

	public static MHHead ok(int cmd){
		MHHead head = new MHHead();
		head.setCmd(cmd);
		head.setVersion(AppConstant.MH_VERSION);
		head.setMagic(AppConstant.MH_MAGIC);
		return head;
	}

	public long getMagic() {
		return magic;
	}

	public void setMagic(long magic) {
		this.magic = magic;
	}

	public int getCmd() {
		return cmd;
	}

	public void setCmd(int cmd) {
		this.cmd = cmd;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(int serialNum) {
		this.serialNum = serialNum;
	}

	@Override
	public String toString() {
		return "MHHead{" +
				"magic=" + magic +
				", cmd=" + cmd +
				", version=" + version +
				", serialNum=" + serialNum +
				'}';
	}
}