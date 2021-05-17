package com.yunxi.stamper.commons.gk;

/**
 * @author zhf_10@163.com
 * @Description 国科量子解密——请求参数实体
 * @date 2020/4/14 10:08
 */
public class RequestEntity {

	/***待解密数据,必须，长度0-1024*/
	private String decryptData;

	/***设备类型,必须，长度1*/
	private String deviceType;        //USBKEY设备 U ，USBKEY软算法  SU， 加密模块 M ，TF卡 T

	/***密钥索引（可以是通道标识、设备标识等，由安全管理平台统一分配），必须，长度1-32*/
	private String keyIndex;

	/***流水号，必须，长度32*/
	private String serialIn;

	/***解密后返回数据类型，必须，长度1*/
	private String msgType;        //1：十六进制; 2: BASE64; 3:可显示的原来的明文

	/***模式,必须，长度2*/
	private String opMode;    //01：ECB解密，02 CBC解密

	/***应用服务密钥标识（由安全管理平台统一分配）,必须，长度3-5*/
	private String appId;

	/***向量值(使用CBC模式时，才需要输入)，非必须，长度32*/
	private String iv;

	public String getDecryptData() {
		return decryptData;
	}

	public void setDecryptData(String decryptData) {
		this.decryptData = decryptData;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getKeyIndex() {
		return keyIndex;
	}

	public void setKeyIndex(String keyIndex) {
		this.keyIndex = keyIndex;
	}

	public String getSerialIn() {
		return serialIn;
	}

	public void setSerialIn(String serialIn) {
		this.serialIn = serialIn;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getOpMode() {
		return opMode;
	}

	public void setOpMode(String opMode) {
		this.opMode = opMode;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getIv() {
		return iv;
	}

	public void setIv(String iv) {
		this.iv = iv;
	}
}
