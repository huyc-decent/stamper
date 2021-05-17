package com.yunxi.stamper.mapper;

import com.yunxi.stamper.entity.FlowNode;
import com.yunxi.stamper.entityVo.FlowVoAddEntity;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface FlowNodeMapper extends MyMapper<FlowNode> {
	List<FlowVoAddEntity> selectVoByFlow(Integer flowId);
}