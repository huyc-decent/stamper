package com.yunxi.stamper.commons.gk;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2020/4/15 18:03
 */
public class ResponseEntity {
	/***命令码 00- 服务加密成功 01- 入参不合法 02- 加密失败 99- 其他错误*/
	private String cmd;

	/***返回码*/
	private String code;

	/***流水号*/
	private String serialIn;

	/***响应描述信息*/
	private String message;

	/***返回加密结果*/
	private String value;

	private int keyVersion;
	private Object data;
	private String valueByte;

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSerialIn() {
		return serialIn;
	}

	public void setSerialIn(String serialIn) {
		this.serialIn = serialIn;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getKeyVersion() {
		return keyVersion;
	}

	public void setKeyVersion(int keyVersion) {
		this.keyVersion = keyVersion;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getValueByte() {
		return valueByte;
	}

	public void setValueByte(String valueByte) {
		this.valueByte = valueByte;
	}

	@Override
	public String toString() {
		String sb = "ResponseEntity{" + "cmd='" + cmd + '\'' +
				", code='" + code + '\'' +
				", serialIn='" + serialIn + '\'' +
				", message='" + message + '\'' +
				", value='" + value + '\'' +
				", keyVersion=" + keyVersion +
				", data=" + data +
				", valueByte='" + valueByte + '\'' +
				'}';
		return sb;
	}
}
