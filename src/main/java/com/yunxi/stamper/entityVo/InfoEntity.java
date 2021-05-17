package com.yunxi.stamper.entityVo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/12/27 0027 11:20
 */
@Setter
@Getter
public class InfoEntity {
	private Integer id;			//使用记录ID
	private Integer applicationId; //申请单ID
	private Date createDate;	//记录创建时间
	private Integer deviceId; 	//印章ID
	private String deviceName;	//印章名称
	private Integer error;		//记录状态
	private String location;	//用印地址
	private Date realTime;		//用印时间
	private Integer type;		//使用类型
	private Integer useCount;		//使用次数
	private Integer userId;		//用印人ID
	private String userName;		//用印人名称

}
