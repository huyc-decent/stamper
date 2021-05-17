package com.yunxi.stamper.entityVo;

import java.util.Date;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description 使用记录搜索关键词 实体类
 * @date 2019/5/8 0008 17:43
 */
public class SealRecordInfoVO {
	/**
	 * 支持如下条件搜索
	 */
	private String deviceName;//印章名称
	private String userName;//用印人名称
	private String location;//地址
	private Date startTime;//开始时间
	private Date endTime;//结束时间
	private List<Integer> applicationIds;//申请单id列表
	private List<Integer> orgIds;//组织id列表
	private List<Integer> departmentId;//部门id
	private Integer type;//用章类型 0:简易记录  1:标准记录  2:量子记录
	private Integer error;//异常条件 -1:查询异常/警告  0:全部 1:查询正常

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public List<Integer> getApplicationIds() {
		return applicationIds;
	}

	public void setApplicationIds(List<Integer> applicationIds) {
		this.applicationIds = applicationIds;
	}

	public List<Integer> getOrgIds() {
		return orgIds;
	}

	public void setOrgIds(List<Integer> orgIds) {
		this.orgIds = orgIds;
	}

	public List<Integer> getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(List<Integer> departmentId) {
		this.departmentId = departmentId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getError() {
		return error;
	}

	public void setError(Integer error) {
		this.error = error;
	}
}
