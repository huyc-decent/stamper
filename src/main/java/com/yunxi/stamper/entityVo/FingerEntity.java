package com.yunxi.stamper.entityVo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/12/11 0011 21:21
 */
@Setter
@Getter
public class FingerEntity {
	private int userId;//员工ID
	private String userName;//员工名称
	private String phone;//员工手机号
	private Date createDate;//员工注册时间
	private Date fingerCreateDate;//指纹录入时间
	private boolean finger = true;//true:有指纹 false:无指纹
	private String departmentNames;//员工所属组织列表

}
