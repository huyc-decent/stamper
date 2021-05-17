package com.yunxi.stamper.entityVo;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/12/4 0004 12:57
 */
public class SignetEntity {
	private Integer id;
	private String name;
	private Integer camera;
	private Integer status;		//0:正常 1:异常 2:销毁 3:停用 4:锁定
	private Date createDate;
	private Integer typeId;
	private String typeName;
	private Integer count;
	private Boolean online;
	private String network;
	private String location;
	private Integer sleepTime;
	private boolean fingerPattern;
	private Integer isEnableApplication;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getIsEnableApplication() {
		return isEnableApplication;
	}

	public void setIsEnableApplication(Integer isEnableApplication) {
		this.isEnableApplication = isEnableApplication;
	}

	public Integer getCamera() {
		return camera;
	}

	public void setCamera(Integer camera) {
		this.camera = camera;
	}

	public Integer getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(Integer sleepTime) {
		this.sleepTime = sleepTime;
	}

	public boolean isFingerPattern() {
		return fingerPattern;
	}

	public void setFingerPattern(boolean fingerPattern) {
		this.fingerPattern = fingerPattern;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Boolean getOnline() {
		return online;
	}

	public void setOnline(Boolean online) {
		this.online = online;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		if (StringUtils.isNotBlank(network)) {
			network = network.startsWith("\"") ? network.substring(1) : network;
			network = network.endsWith("\"") ? network.substring(0, network.length() - 1) : network;
			this.network = network;
		}
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
