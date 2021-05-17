package com.yunxi.stamper.entityVo;


import com.yunxi.stamper.entity.Department;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/10 0010 11:08
 */
@Setter
@Getter
public class DepartmentVo extends Department {

	//子部门
	private List<DepartmentVo> childrens;
	private int type = 1;//部门
	private String managerUserName;//组织主管名称

	@Override
	public Integer getType() {
		return type;
	}
}
