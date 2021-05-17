package com.yunxi.stamper.entityVo;

import com.yunxi.stamper.commons.other.CommonUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/12/3 0003 9:53
 */
@Setter
@Getter
public class Employee {
	private Integer id;//员工ID
	private String userName;//员工名称
	private String phone;//员工手机号
	private Date createDate;//注册时间
	private int status;//员工状态
	private Integer type;//3:非激活 !3:激活状态
	private String remark;//员工简介
	private String roleNames;//角色名称列表
	private String roleIds;//角色ID列表
	private String departmentNames;//部门名称列表
	private String departmentIds;//部门ID列表

	public List<Integer> getRoleIds() {
		return StringUtils.isBlank(roleIds) ? null : CommonUtils.splitToInteger(roleIds, ",");
	}

	public List<Integer> getDepartmentIds() {
		return StringUtils.isBlank(departmentIds) ? null : CommonUtils.splitToInteger(departmentIds, ",");
	}

	public List<String> getRoleNames() {
		return StringUtils.isBlank(roleNames) ? null : Arrays.asList(roleNames.split(","));
	}

	public void setRoleNames(String roleNames) {
		this.roleNames = roleNames;
	}

	public List<String> getDepartmentNames() {
		return StringUtils.isBlank(departmentNames) ? null : Arrays.asList(departmentNames.split(","));
	}
}
