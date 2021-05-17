package com.yunxi.stamper.entityVo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/12/4 0004 17:58
 */
@Setter
@Getter
@ToString
public class SealRecordInfoEntity {
	private Integer id;//记录ID
	private Date realTime;//盖章时间
	private String userName;//用印人

	private Integer deviceId;//印章ID
	private String deviceName;//印章名称

	private Integer type;//使用模式  0:申请单模式  1:申请单模式(量子)  2:指纹模式  3:指纹模式(量子)  4:密码模式

	private Integer useCount;//加盖次数
	private Integer error;//记录状态描述 -1:异常 0：正常 1：警告
	private String errorMsg;//异常信息
	private String location; //使用地址
	private String remark;//记录备注信息


	private List<FileEntity> useInfos = null;//盖章记录
	private List<FileEntity> auditorInfos = null;//审计记录
	private List<FileEntity> timeoutInfos = null;//超时记录
	private List<FileEntity> numoutInfos = null;//超次记录
	private List<FileEntity> replenishInfos = null;//追加记录

}
