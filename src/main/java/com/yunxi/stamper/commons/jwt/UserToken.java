package com.yunxi.stamper.commons.jwt;

/**
 * @author zhf_10@163.com
 * @Description jwt加密实体, 规范本项目中jwt加密哪些数据
 * @date 2019/4/24 0024 18:16
 */
public class UserToken implements TokenEntity {
	private Integer userId;//用户id
	private Integer orgId;//所属组织id
	private String userName;//用户名称

	public UserToken() {
	}

	public UserToken(Integer userId, Integer orgId, String userName) {
		this.userId = userId;
		this.orgId = orgId;
		this.userName = userName;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getOrgId() {
		return orgId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
