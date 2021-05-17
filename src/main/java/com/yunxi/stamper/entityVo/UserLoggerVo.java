package com.yunxi.stamper.entityVo;


import com.yunxi.stamper.entity.UserLogger;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/29 0029 10:46
 */
public class UserLoggerVo extends UserLogger {
	private String times;
	private String remark;

	public String getTimes() {
		return times;
	}

	public void setTimes(String times) {
		this.times = times;
	}

	@Override
	public String getRemark() {
		return remark;
	}

	@Override
	public void setRemark(String remark) {
		this.remark = remark;
	}
}
