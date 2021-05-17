package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.Flow;
import com.yunxi.stamper.entityVo.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/6/5 0005 11:04
 */
public interface FlowService {
	/**
	 *
	 * @param orgId
	 * @return
	 */
	List<Flow> getByOrg(Integer orgId);

	/**
	 *
	 * @param orgId
	 * @param name
	 * @return
	 */
	Flow getByOrgAndName(Integer orgId, String name);

	/**
	 *
	 * @param flow
	 */
	void add(Flow flow);

	/**
	 *
	 * @param flowId
	 * @return
	 */
	Flow get(Integer flowId);

	/**
	 *
	 * @param flow
	 */
	void del(Flow flow);

	/**
	 * 查询审批流程信息、流程节点列表信息、所属组织列表信息
	 * @param flowId 审批流程ID
	 * @return
	 */
	FlowVoAdd getFlowInfo(Integer flowId);

	/**
	 *
	 * @param update
	 */
	void update(Flow update);

	/**
	 * 查询指定组织列表下、指定名称的审批流程数量
	 * @param departmentIds 组织ID列表
	 * @param name 名称
	 * @return
	 */
	Integer getByDepartmentAndName(@NotNull List<Integer> departmentIds, @NotEmpty String name);

	/**
	 * 添加审批流程
	 * @param flowVo 审批流程实例
	 */
	void addFlow(@NotNull UserInfo userInfo, @NotNull FlowVoAdd flowVo);

	/**
	 * 查询审批流程列表
	 * @param orgId 集团ID
	 * @param departmentIds 要搜索的组织ID列表
	 * @param keyword 要搜索的流程名称关键词
	 * @return
	 */
	List<FlowVoSelect> getFlowListByOrgAndDepartmentAndKeyword(@NotNull Integer orgId, List<Integer> departmentIds, String keyword);

	/**
	 * 查询审批流程列表
	 * @param orgId 集团ID
	 * @param departmentId 要搜索的组织ID
	 * @return
	 */
	List<FlowEntity> getByOrgAndDepartment(@NotNull Integer orgId, @NotNull Integer departmentId);

	/**
	 * 更新审批流程
	 * @param userInfo 操作人
	 * @param flow 原审批流程
	 * @param name 新名称
	 * @param remark 新简介
	 * @param status 状态
	 * @param flowNodeList 审批节点列表
	 * @param departmentIds 组织ID列表
	 */
	void updateFlow(@NotNull UserInfo userInfo,@NotNull Flow flow,@NotNull String name,String remark,@NotNull Integer status,@NotNull List<FlowVoAddEntity> flowNodeList,@NotNull List<Integer> departmentIds);

	/**
	 * 查询指定组织下的审批流程列表
	 * @param orgId 集团ID
	 * @param departmentIds 组织ID列表
	 * @return
	 */
	List<Flow> getByOrgAndDepartment(@NotNull Integer orgId,@NotNull List<Integer> departmentIds);
}
