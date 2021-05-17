package com.yunxi.stamper.entityVo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author zhf_10@163.com
 * @Description 绑定申请单实例
 * @date 2019/8/5 0005 10:08
 */
@Setter
@Getter
public class ApplicationToBind {
	private Integer id;//申请单ID
	private Date time;//授权通过日期
	private String title;//申请单标题
	private String userName;//申请人
	private String deviceName;//印章名称
}
