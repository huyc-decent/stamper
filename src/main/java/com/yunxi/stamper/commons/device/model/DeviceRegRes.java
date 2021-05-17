package com.yunxi.stamper.commons.device.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author zhf_10@163.com
 * @Description 设备注册响应对象
 * @date 2019/1/15 0015 10:53
 */
public class DeviceRegRes {
	@JsonProperty("Ret")
	public int ret;//返回的状态码 0表示成功
	@JsonProperty("Msg")
	public String msg;//Ret 不为0 时 返回的错误信息
	@JsonProperty("DeviceLoginReq")
	public DeviceLoginReq deviceLoginReq;	//直接返回登录的请求

	public static DeviceRegRes ok(DeviceLoginReq deviceLoginReq){
		DeviceRegRes res = new DeviceRegRes();
		res.setRet(0);
		res.setMsg("");
		res.setDeviceLoginReq(deviceLoginReq);
		return res;
	}

	@JSONField(name = "Ret")
	public int getRet() {
		return ret;
	}

	public void setRet(int ret) {
		this.ret = ret;
	}

	@JSONField(name = "Msg")
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@JSONField(name = "DeviceLoginReq")
	public DeviceLoginReq getDeviceLoginReq() {
		return deviceLoginReq;
	}

	public void setDeviceLoginReq(DeviceLoginReq deviceLoginReq) {
		this.deviceLoginReq = deviceLoginReq;
	}

	@Override
	public String toString() {
		return "DeviceRegRes{" +
				"ret=" + ret +
				", msg='" + msg + '\'' +
				", deviceLoginReq=" + deviceLoginReq +
				'}';
	}
}
