package com.yunxi.stamper.mapper;


import com.yunxi.stamper.entity.RelateDepartmentUser;
import com.yunxi.stamper.sys.baseDao.MyMapper;

import java.util.List;

public interface RelateDepartmentUserMapper extends MyMapper<RelateDepartmentUser> {
	/**
	 * 查询员工、组织关联信息实体
	 * @param userId 员工ID
	 * @param departmentId 组织ID
	 * @return
	 */
	RelateDepartmentUser selectByDepartmentAndUser(Integer departmentId, Integer userId);

	/**
	 * 删除指定组织ID列表下的关联关系
	 * @param departmentIds
	 */
	void delByDepartmentIds(List<Integer> departmentIds);

	/**
	 * 删除组织-员工关联信息
	 * @param userId
	 */
	void delByUserId(Integer userId);

	/**
	 * 查询指定用户
	 * @param userId
	 * @return
	 */
	List<Integer> selectDepartmentIdsByUserId(Integer userId);
}