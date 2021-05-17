package com.yunxi.stamper.entityVo;

import com.yunxi.stamper.entity.FileInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/12/24 0024 10:49
 */
public class UserEntity {
	private int userId;        //员工ID
	private String userName;    //员工姓名
	private FileEntity headImg;        //头像
	private String phone;    //员工手机号
	private String remark;    //简介
	private List<String> roleList = new ArrayList<>();    //角色列表
	private List<String> departmentList = new ArrayList<>();    //员工所属组织列表
	private String orgName;    //集团名称

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public FileEntity getHeadImg() {
		return headImg;
	}

	public void setHeadImg(FileEntity headImg) {
		this.headImg = headImg;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<String> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<String> roleList) {
		this.roleList = roleList;
	}

	public List<String> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<String> departmentList) {
		this.departmentList = departmentList;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
}
