package com.yunxi.stamper.mapper;


import com.yunxi.stamper.entity.Flow;
import com.yunxi.stamper.entityVo.FlowEntity;
import com.yunxi.stamper.entityVo.FlowVoAdd;
import com.yunxi.stamper.entityVo.FlowVoSelect;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Component
public interface FlowMapper extends MyMapper<Flow> {
	//查询出前台需要的审批流程格式,进行编辑
	FlowVoAdd selectByVo(Integer flowId);
	/**
	 * 查询指定组织列表下、指定名称的审批流程数量
	 *
	 * @param departmentIds 组织ID列表
	 * @param name          名称
	 * @return
	 */
	Integer selectByDepartmentAndName(@NotNull List<Integer> departmentIds, @NotEmpty String name);

	/**
	 * 查询审批流程列表
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 要搜索的组织ID列表
	 * @param keyword       要搜索的流程名称关键词
	 * @return
	 */
	List<FlowVoSelect> selectByOrgAndDepartmentAndKeyword(@NotNull Integer orgId, List<Integer> departmentIds, String keyword);

	/**
	 * 查询审批流程列表
	 *
	 * @param orgId        集团ID
	 * @param departmentId 要搜索的组织ID
	 * @return
	 */
	List<FlowEntity> selectByOrgAndDepartment(@NotNull Integer orgId, @NotNull Integer departmentId);

	/**
	 * 查询指定组织下的审批流程列表
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 组织ID列表
	 * @return
	 */
	List<Flow> selectByOrgAndDepartments(@NotNull Integer orgId, @NotNull List<Integer> departmentIds);
}