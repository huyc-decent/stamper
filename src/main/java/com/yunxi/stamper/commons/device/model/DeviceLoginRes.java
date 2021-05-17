package com.yunxi.stamper.commons.device.model;

import com.alibaba.fastjson.annotation.JSONField;

public class DeviceLoginRes {

	public int ret;

    public String msg;

    public String jwtTokenNew="";

    public int isOos = 0;//是否使用天翼云 0:不适用天翼云 1:使用天翼云

	public long loginTimes;//登录成功时间戳

	public String preFix;//天翼云存储路径文件夹前缀

	public String bucketName;//容器名称

	public String getPreFix() {
		return preFix;
	}

	public void setPreFix(String preFix) {
		this.preFix = preFix;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public long getLoginTimes() {
		return loginTimes;
	}

	public void setLoginTimes(long loginTimes) {
		this.loginTimes = loginTimes;
	}

	public int getIsOos() {
		return isOos;
	}

	public void setIsOos(int isOos) {
		this.isOos = isOos;
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

	@JSONField(name = "JwtTokenNew")
	public String getJwtTokenNew() {
		return jwtTokenNew;
	}

	public void setJwtTokenNew(String jwtTokenNew) {
		this.jwtTokenNew = jwtTokenNew;
	}

	@Override
	public String toString() {
		return "DeviceLoginRes{" +
				"ret=" + ret +
				", msg='" + msg + '\'' +
				", jwtTokenNew='" + jwtTokenNew + '\'' +
				'}';
	}
}