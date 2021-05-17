package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.entity.TsLoggerAttribute;
import com.yunxi.stamper.mapper.TsLoggerAttributeMapper;
import com.yunxi.stamper.service.TsLoggerAttributeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2020/6/24 15:07
 */
@Slf4j
@Service
public class TsLoggerAttributeServiceImpl implements TsLoggerAttributeService {
	@Autowired
	private TsLoggerAttributeMapper mapper;

	@Override
	public void add(TsLoggerAttribute attribute) {
		mapper.insert(attribute);
	}
}
