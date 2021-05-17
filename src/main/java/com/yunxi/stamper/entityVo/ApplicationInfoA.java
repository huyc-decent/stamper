package com.yunxi.stamper.entityVo;

import com.yunxi.stamper.entity.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description 申请单相关信息
 * @date 2020/9/25 14:30
 */
@Setter
@Getter
@ToString
public class ApplicationInfoA {

	/***申请单ID*/
	private Integer applicationId;

	/***申请单信息*/
	private Application application;

	/***申请人*/
	private User user;

	/***审批列表*/
	private List<ApplicationManager> applicationManagers;

	/***授权列表*/
	private List<ApplicationKeeper> applicationKeepers;

	/***审计列表*/
	private List<ApplicationAuditor> applicationAuditors;

}
