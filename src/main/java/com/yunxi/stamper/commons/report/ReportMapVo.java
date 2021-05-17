package com.yunxi.stamper.commons.report;

import java.util.Date;

/**
 * @author zhf_10@163.com
 * @Description 地图查询参数实体
 * @date 2019/5/21 0021 14:29
 */
public class ReportMapVo {
	private Integer orgId;    //查询哪个公司
	private String province;    //查询哪个省
	private String city;    //查询哪个市
	private Date start;    //开始时间
	private Date end;    //结束时间

	public Integer getOrgId() {
		return orgId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}
}
