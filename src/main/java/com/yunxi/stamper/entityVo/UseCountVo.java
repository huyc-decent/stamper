package com.yunxi.stamper.entityVo;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/29 0029 11:05
 */
public class UseCountVo {
	private int id;//印章id
	private String deviceName;//印章名称
	private int useCount;//已使用次数
	private int totalCount;//总次数

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public int getUseCount() {
		return useCount;
	}

	public void setUseCount(int useCount) {
		this.useCount = useCount;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
}
