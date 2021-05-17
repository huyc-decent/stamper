package com.yunxi.stamper.commons.jwt;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/4/28 0028 21:29
 */
public class DeviceToken implements TokenEntity {
	private String uuid;
	private int orgId;
	private int id;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getOrgId() {
		return orgId;
	}

	public void setOrgId(int orgId) {
		this.orgId = orgId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
