package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.FlowNode;
import com.yunxi.stamper.entityVo.FlowVoAddEntity;
import com.yunxi.stamper.mapper.FlowNodeMapper;
import com.yunxi.stamper.service.FlowNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/6/5 0005 11:17
 */
@Service
public class IFlowNodeService implements FlowNodeService {
	@Autowired
	private FlowNodeMapper mapper;

	@Override
	public List<FlowNode> getByFlow(Integer flowId) {
		if(flowId!=null){
			Example example = new Example(FlowNode.class);
			example.createCriteria().andEqualTo("flowId", flowId).andIsNull("deleteDate");
			return mapper.selectByExample(example);
		}
		return null;
	}

	@Override
	public List<FlowVoAddEntity> getVoByFlow(Integer flowId) {
		if(flowId!=null){
			return mapper.selectVoByFlow(flowId);
		}
		return null;
	}

	@Override
	@Transactional
	public void del(FlowNode node) {
		int delCount = 0;
		if (node != null&&node.getId()!=null) {
			delCount = mapper.delete(node);
		}
		if (delCount != 1) {
			throw new PrintException("审批节点删除失败");
		}
	}

	@Override
	@Transactional
	public void add(FlowNode node) {
		int addCount = 0;
		if (node != null) {
			node.setCreateDate(new Date());
			addCount = mapper.insert(node);
		}
		if (addCount != 1) {
			throw new PrintException("审批节点添加失败");
		}
	}

	@Override
	public FlowNode get(Integer nodeId) {
		if (nodeId != null) {
			Example example = new Example(FlowNode.class);
			example.createCriteria().andEqualTo("id", nodeId).andIsNull("deleteDate");
			return mapper.selectOneByExample(example);
		}
		return null;
	}
}
