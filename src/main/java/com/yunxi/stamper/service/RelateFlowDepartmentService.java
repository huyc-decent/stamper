package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.RelateFlowDepartment;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/12/18 0018 14:18
 */
public interface RelateFlowDepartmentService {
	RelateFlowDepartment get(Integer departmentId, Integer flowId);

	void add(RelateFlowDepartment rfd);

	/**
	 * 查询组织ID列表
	 * @param flowId 审批流程ID
	 * @return
	 */
	List<Integer> getDepartmentByFlow(Integer flowId);

	List<RelateFlowDepartment> getByFlow(Integer flowId);

	void del(RelateFlowDepartment rfd);
}
