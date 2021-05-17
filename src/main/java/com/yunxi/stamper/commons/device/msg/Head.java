package com.yunxi.stamper.commons.device.msg;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/1/17 0017 10:33
 */
public class Head {
	private int Magic;
	private int  Cmd;
	private int  Version;
	private int  SerialNum;

	public int getMagic() {
		return Magic;
	}

	public void setMagic(int magic) {
		Magic = magic;
	}

	public int getCmd() {
		return Cmd;
	}

	public void setCmd(int cmd) {
		Cmd = cmd;
	}

	public int getVersion() {
		return Version;
	}

	public void setVersion(int version) {
		Version = version;
	}

	public int getSerialNum() {
		return SerialNum;
	}

	public void setSerialNum(int serialNum) {
		SerialNum = serialNum;
	}

	@Override
	public String toString() {
		return "Head{" +
				"Magic=" + Magic +
				", Cmd=" + Cmd +
				", Version=" + Version +
				", SerialNum=" + SerialNum +
				'}';
	}
}
