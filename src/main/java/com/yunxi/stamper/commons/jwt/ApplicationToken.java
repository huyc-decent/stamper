package com.yunxi.stamper.commons.jwt;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/4/30 0030 11:59
 */
public class ApplicationToken implements TokenEntity {
	private Integer application_id;
	private Integer status;
	private Integer is_qss;

	public Integer getApplication_id() {
		return application_id;
	}

	public void setApplication_id(Integer application_id) {
		this.application_id = application_id;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getIs_qss() {
		return is_qss;
	}

	public void setIs_qss(Integer is_qss) {
		this.is_qss = is_qss;
	}
}
