package com.yunxi.stamper.commons.report;


/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/4/8 0008 11:21
 */
public class POIEntity {
	/**
	 * 设备相关
	 */
	@Excel("设备id")
	private Integer deviceId;//设备id
	@Excel("设备名称")
	private String deviceName;//设备名称

	/**
	 * 使用记录相关
	 */
	@Excel("用印日期")
	private String realTime;//用印日期
	@Excel("当前记录次数")
	private Integer count;//当前记录次数
	@Excel("用印人名称")
	private String identity;//用印人名称
	@Excel("使用记录类型")
	private String type;//使用记录类型 0:申请单模式 2:指纹模式
	@Excel("用印地址")
	private String location;//用印地址
	@Excel("异常信息")
	private String error;//异常信息


	/**
	 * 关联申请单相关
	 */
	@Excel("申请单id")
	private Integer applicationId;//申请单id
	@Excel("申请单标题")
	private String title;//申请单标题
	@Excel("申请单说明")
	private String content;//申请单说明
	@Excel("申请次数")
	private Integer userCount;//申请次数
	@Excel("申请人名称")
	private String userName;//申请人名称

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

	public String getRealTime() {
		return realTime;
	}

	public void setRealTime(String realTime) {
		this.realTime = realTime;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Integer getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(Integer applicationId) {
		this.applicationId = applicationId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getUserCount() {
		return userCount;
	}

	public void setUserCount(Integer userCount) {
		this.userCount = userCount;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
