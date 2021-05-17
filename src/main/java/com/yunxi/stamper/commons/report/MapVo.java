package com.yunxi.stamper.commons.report;


import org.apache.commons.lang3.StringUtils;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/1/2 0002 22:51
 */
public class MapVo {
	private String position;//省/市/区
	private String name;//地理地址
	private Integer value;//印章使用数量
	private Object coord;//经纬度
	private String deviceName;//印章名称
	private String latitude;
	private String longitude;

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

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

	public Object getCoord() {
		if(StringUtils.isNotBlank(longitude) && StringUtils.isNotBlank(latitude)){
			float[] reulst = new float[2];
			reulst[0]=Float.parseFloat(longitude);
			reulst[1]=Float.parseFloat(latitude);
			return reulst;
		}else if(coord!=null && StringUtils.isNotBlank(coord.toString())){
			float[] reulst = new float[2];
			reulst[0]=Float.parseFloat(coord.toString().split(",")[0]);
			reulst[1]=Float.parseFloat(coord.toString().split(",")[1]);
			return reulst;
		}
		return null;
	}

	public void setCoord(Object coord) {
		this.coord = coord;
	}
}
