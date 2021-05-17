package com.yunxi.stamper.entityVo;

import com.yunxi.stamper.entity.Signet;

/**
 * @author zhf_10@163.com
 * @Description 设备概况实例
 * @date 2019/8/9 0009 16:42
 */
public class SignetDetail extends Signet {
	private String batteryCapacity;//电量
	private String location;//地址
	private String lastIdentity;//最近用印人名称
	private boolean online;//在线状态 true:在线 false:不在线

	public String getBatteryCapacity() {
		return batteryCapacity;
	}

	public void setBatteryCapacity(String batteryCapacity) {
		this.batteryCapacity = batteryCapacity;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLastIdentity() {
		return lastIdentity;
	}

	public void setLastIdentity(String lastIdentity) {
		this.lastIdentity = lastIdentity;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}
}
