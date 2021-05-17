package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.entity.FlowType;
import com.yunxi.stamper.mapper.FlowTypeMapper;
import com.yunxi.stamper.service.FlowTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/6/5 0005 11:01
 */
@Service
public class IFlowTypeService implements FlowTypeService {
	@Autowired
	private FlowTypeMapper mapper;

	@Override
	public List<FlowType> getAll() {
		Example example = new Example(FlowType.class);
		example.createCriteria().andIsNull("deleteDate");
		return mapper.selectByExample(example);
	}
}
