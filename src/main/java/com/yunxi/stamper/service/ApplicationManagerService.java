package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.ApplicationManager;
import com.yunxi.stamper.entity.ApplicationNode;
import com.yunxi.stamper.entityVo.ApplicationManagerVoSelect;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/4 0004 19:05
 */
public interface ApplicationManagerService {
	void add(ApplicationManager am);

	void update(ApplicationManager manager);

	void del(ApplicationManager am);

	//查询申请单审批流程
	List<ApplicationManagerVoSelect> getByApplication(Integer applicationId);

	//查询申请单的审批流程,按审批顺序由小到大
	List<ApplicationManager> getByApplicationId(Integer applicationId);

	//查询指定申请单+审批人 正在处理的审批记录
	ApplicationManager getByApplicationAndManagerAndDealing(Integer applicationId, Integer userId);

	//根据审批节点,创建审批记录
	void createByNode(ApplicationNode node);

	//查询指定申请单id+审批id+节点id 的审批记录
	ApplicationManager getByApplicationAndManagerAndNode(Integer applicationId, Integer userId, Integer nodeId);

	List<ApplicationManager> getByApplicationAndNode(Integer applicationId, Integer nodeId);

	/**
	 * 创建下一级'自选审批'
	 *
	 * @param node          要创建的节点
	 * @param managerUserId 下级一个自选审批人ID
	 */
	void createByNodeByOptional(ApplicationNode node, Integer managerUserId);
}
