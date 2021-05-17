package com.yunxi.stamper.commons.report;

/**
 * @author zhf_10@163.com
 * @Description 地图专用,统计各 省(或市区)使用总次数
 * @date 2019/5/21 0021 14:55
 */
public class KeyValue {
	private String name;//省/市/区 名称
	private Integer value;//使用总次数

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
}
