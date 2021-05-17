package com.yunxi.stamper.entityVo;

import com.yunxi.stamper.entity.ErrorType;
import com.yunxi.stamper.entity.FileInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description 移动端 设备使用记录列表 记录实体对象
 * @date 2020/7/9 9:40
 */
@Setter
@Getter
@ToString
public class SealRecordInfoEntityApp {
	private Integer id;//记录ID
	private Date realTime;//盖章时间
	private String userName;//用印人

	private Integer deviceId;//印章ID
	private String deviceName;//印章名称

	private Integer type;//使用模式

	private Integer useCount;//加盖次数

	private Integer error;//记录状态描述 -1:异常 0：正常 1：警告
	private String errorMsg;//异常信息
	private String location; //使用地址

	private List<ErrorType> errortypes;

	private List<FileInfo> useInfos;//盖章记录
	private List<FileInfo> auditorInfos;//审计记录
	private List<FileInfo> timeoutInfos;//超时记录
	private List<FileInfo> numoutInfos;//超次记录
}
