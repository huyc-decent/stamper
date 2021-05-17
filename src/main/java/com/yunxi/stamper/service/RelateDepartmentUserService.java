package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.RelateDepartmentUser;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/12/2 0002 17:30
 */
public interface RelateDepartmentUserService {
	/**
	 * 添加员工与组织关联关系
	 * @param departmentUser
	 */
	void add(RelateDepartmentUser departmentUser);

	/**
	 * 查询员工、组织关联信息实体
	 * @param userId 员工ID
	 * @param departmentId 组织ID
	 * @return
	 */
	RelateDepartmentUser get(Integer userId, Integer departmentId);

	/**
	 * 删除组织、员工关联信息
	 * @param departmentId 组织ID
	 * @param managerUserId 员工ID
	 */
	void del(Integer departmentId, Integer managerUserId);

	/**
	 * 删除指定组织ID列表下的关联关系
	 * @param childrenIds
	 */
	void delByDepartmentIds(List<Integer> childrenIds);

	void delByEmployeeId(Integer userId);

	/**
	 * 查询指定用户
	 * @param userId 用户ID
	 * @return
	 */
	List<Integer> getDepartmentIdsByUserId(Integer userId);
}
