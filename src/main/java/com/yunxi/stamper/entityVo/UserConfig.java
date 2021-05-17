package com.yunxi.stamper.entityVo;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/10/19 0019 11:10
 */
public class UserConfig {

	private String svrHost;//业务IP
	private String svrIp;//socketIP

	//初始化wifi配置
	private String wifiSsid;
	private String wifiPwd;


	private Integer deviceId;//印章ID
	private String deviceName;//印章名称
	private String deviceUuid;//印章UUID
	private String deviceIccid;//印章电话卡号码
	private String deviceVersion;//APK版本号

	public String getSvrHost() {
		return svrHost;
	}

	public void setSvrHost(String svrHost) {
		this.svrHost = svrHost;
	}

	public String getSvrIp() {
		return svrIp;
	}

	public void setSvrIp(String svrIp) {
		this.svrIp = svrIp;
	}

	public String getWifiSsid() {
		return wifiSsid;
	}

	public void setWifiSsid(String wifiSsid) {
		this.wifiSsid = wifiSsid;
	}

	public String getWifiPwd() {
		return wifiPwd;
	}

	public void setWifiPwd(String wifiPwd) {
		this.wifiPwd = wifiPwd;
	}

	public Integer getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceUuid() {
		return deviceUuid;
	}

	public void setDeviceUuid(String deviceUuid) {
		this.deviceUuid = deviceUuid;
	}

	public String getDeviceIccid() {
		return deviceIccid;
	}

	public void setDeviceIccid(String deviceIccid) {
		this.deviceIccid = deviceIccid;
	}

	public String getDeviceVersion() {
		return deviceVersion;
	}

	public void setDeviceVersion(String deviceVersion) {
		this.deviceVersion = deviceVersion;
	}
}
