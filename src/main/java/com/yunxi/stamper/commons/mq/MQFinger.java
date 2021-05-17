package com.yunxi.stamper.commons.mq;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/7/18 0018 10:12
 */
public class MQFinger {
	private Integer deviceId;//印章id
	private Integer userId;//被录入、删除的用户人id
	private String userName;//被录入、删除的用户人姓名

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}
}
