package com.yunxi.stamper.entityVo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/12/19 0019 17:43
 */
@Setter
@Getter
@ToString
public class ThresholdEntity {
	private Integer id;//印章ID
	private Date createDate;//印章注册时间
	private String name;//印章名称
	private Integer thresholdValue;//阈值
}
