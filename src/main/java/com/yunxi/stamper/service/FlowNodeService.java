package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.FlowNode;
import com.yunxi.stamper.entityVo.FlowVoAddEntity;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/6/5 0005 11:17
 */
public interface FlowNodeService {
	FlowNode get(Integer nodeId);

	void add(FlowNode node);

	void del(FlowNode node);

	List<FlowVoAddEntity> getVoByFlow(Integer flowId);

	List<FlowNode> getByFlow(Integer flowId);
}
