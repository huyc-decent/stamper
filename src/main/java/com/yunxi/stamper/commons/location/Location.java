package com.yunxi.stamper.commons.location;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/4/29 0029 9:46
 */
public class Location {
	private String province;//省
	private String city;//市
	private String district;//区县
	private String street;//乡村街道
	private String location;//具体地址

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

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
