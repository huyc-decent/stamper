package com.yunxi.stamper.entityVo;

import com.yunxi.stamper.entity.User;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/10 0010 16:46
 */
@Slf4j
public class UserVo extends User {
	private List<Integer> roleIds;//绑定的角色id列表【与前端传来的参数不对应，使用下面的参数checkedIds】
	private List<Integer> checkedIds;//绑定的角色id列表
	private String departMentName;//部门名称
	private boolean isUse = true;//true:正常 false:禁用
	private boolean finger = false;//是否存在指纹 true：存在  false：不存在
	private Date fingerCreateDate;//指纹录入时间
	private List<String> roleNames;//用户角色名称列表

	public List<String> getRoleNames() {
		return roleNames;
	}

	public void setRoleNames(Object roleNames) {
		try {
			Object obj = roleNames;
			if (obj != null) {
				if (obj instanceof String) {
					String[] split = ((String) obj).split(",");
					if (split != null && split.length > 0) {
						this.roleNames = Arrays.asList(split);
					}
				}
			} else if (obj instanceof List) {
				this.roleNames = (List) obj;
			}
		} catch (Exception e) {
			log.error("出现异常 ", e);
		}
	}

	public Date getFingerCreateDate() {
		return fingerCreateDate;
	}

	public void setFingerCreateDate(Date fingerCreateDate) {
		this.fingerCreateDate = fingerCreateDate;
	}

	public List<Integer> getCheckedIds() {
		return checkedIds;
	}

	public void setCheckedIds(List<Integer> checkedIds) {
		this.checkedIds = checkedIds;
	}

	public boolean isUse() {
		return isUse;
	}

	public void setUse(boolean use) {
		isUse = use;
	}

	public String getDepartMentName() {
		return departMentName;
	}

	public void setDepartMentName(String departMentName) {
		this.departMentName = departMentName;
	}

	public List<Integer> getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(List<Integer> roleIds) {
		this.roleIds = roleIds;
	}

	public boolean isFinger() {
		return finger;
	}

	public void setFinger(boolean finger) {
		this.finger = finger;
	}
}
