package com.yunxi.stamper.entityVo;

import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.Department;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/11/29 0029 18:01
 */
@Setter
@Getter
public class OrganizationalTree {
	private int id;//组织、公司ID
	private String name;//组织、公司名称
	private int type;//组织类型 0:部门 1:公司 2:集团公司
	private List<OrganizationalTree> childrens;//子组织列表
	private boolean disabled = false;//true:禁用 false:启用

	public OrganizationalTree() {
	}

	public OrganizationalTree(Department department) {
		if (department == null) {
			throw new PrintException("组织架构初始化失败");
		}
		this.id = department.getId();
		this.name = department.getName();
		this.type = department.getType();
	}
}
