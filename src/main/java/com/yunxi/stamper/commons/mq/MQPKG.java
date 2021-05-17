package com.yunxi.stamper.commons.mq;

import java.util.UUID;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/7/18 0018 9:56
 */
public class MQPKG {
	private int cmd;
	private Integer deviceId;//设备id
	private String data;//要传递的消息体(json格式)
	private Integer userId;//下发命令的用户人ID
	private String userName;//下发命令的用户人姓名
	private int orgId;    //迁移目标公司ID
	private String serialId;  //序列号

	public MQPKG() {
		try {
			//针对每一个MQ消息，生成一个序列号
			this.serialId = UUID.randomUUID().toString().toLowerCase().replace("-", "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getSerialId() {
		return serialId;
	}

	public void setSerialId(String serialId) {
		this.serialId = serialId;
	}

	public int getOrgId() {
		return orgId;
	}

	public void setOrgId(int orgId) {
		this.orgId = orgId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public int getCmd() {
		return cmd;
	}

	public void setCmd(int cmd) {
		this.cmd = cmd;
	}

	public Integer getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
