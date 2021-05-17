package com.yunxi.stamper.entityVo;

import com.yunxi.stamper.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedList;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description 用户信息存入redis中的实例
 * @date 2019/5/11 0011 16:59
 */
@Setter
@Getter
@ToString
public class UserInfo extends User {
	//存储用户可访问的URL路径列表
	private List<String> permsUrls = new LinkedList<>();

	//用户管理的组织,哪些组织的主管是该用户，该列表包含了该组织下的子孙组织
	//如果该组织是公司，则包含了该组织的子孙部门和子孙公司
	//如果该组织是部门，仅包含该部门的子孙部门
	private List<Integer> departmentIds;

	//用户组织架构(树节点展示)
	private List<OrganizationalTree> tree;

	//用户可见组织ID列表
	private List<Integer> visualDepartmentIds;

	//集团属主
	private boolean owner;

	//系统管理员
	private boolean admin = false;
}
