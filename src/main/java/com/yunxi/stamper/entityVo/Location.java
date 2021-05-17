package com.yunxi.stamper.entityVo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhf_10@163.com
 * @Description 印章坐标实体
 * @date 2019/8/9 0009 9:03
 */
@Setter
@Getter
public class Location {
	private Integer deviceId;//印章id
	private String deviceName;//印章名称

	private Boolean online = false;//是否在线 true:在线 false:不在线
	private String addr;//印章所在详细地址信息

	//高德经纬度
	private String longitude;//经度
	private String latitude;//维度

}
