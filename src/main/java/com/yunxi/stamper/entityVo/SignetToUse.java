package com.yunxi.stamper.entityVo;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/7/10 0010 8:43
 */
public class SignetToUse {
	private Integer applicationId;//申请单ID
	private Integer signetId;//印章ID
	private Integer tsValue;//阈值
	private Integer useCount;//使用次数
	private Integer type = 0;//类型 0:正常用章 1:绑定申请单  2:超出申请单次数用章

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(Integer applicationId) {
		this.applicationId = applicationId;
	}

	public Integer getSignetId() {
		return signetId;
	}

	public void setSignetId(Integer signetId) {
		this.signetId = signetId;
	}

	public Integer getTsValue() {
		return tsValue;
	}

	public void setTsValue(Integer tsValue) {
		this.tsValue = tsValue;
	}

	public Integer getUseCount() {
		return useCount;
	}

	public void setUseCount(Integer useCount) {
		this.useCount = useCount;
	}
}
