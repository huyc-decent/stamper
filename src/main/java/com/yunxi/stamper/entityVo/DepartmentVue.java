package com.yunxi.stamper.entityVo;


import com.yunxi.stamper.entity.Department;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhf_10@163.com
 * @Description 部门对象展示
 * @date 2019/5/12 0012 17:31
 */
@Setter
@Getter
public class DepartmentVue extends Department {
	private String managerUserName;//管理员名称
	private String orgName;//所属公司名称
	private String parentName;//上级部门名称

}
