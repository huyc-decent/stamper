package com.yunxi.stamper.commons.device.modelVo;

/**
 * @author zhf_10@163.com
 * @Description 设备远程锁定实体类
 * @date 2019/8/6 0006 17:37
 */
public class DeviceLock {
	private Integer deviceId;//设备id
	private Integer status;//0:解锁  1:锁定
	private Integer res;//设备端响应专用参数 0:操作成功 1:操作失败

	public Integer getRes() {
		return res;
	}

	public void setRes(Integer res) {
		this.res = res;
	}

	public Integer getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
