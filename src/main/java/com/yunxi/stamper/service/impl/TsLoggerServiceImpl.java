package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.entity.TsLogger;
import com.yunxi.stamper.mapper.TsLoggerMapper;
import com.yunxi.stamper.service.TsLoggerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2020/6/24 14:52
 */
@Slf4j
@Service
public class TsLoggerServiceImpl implements TsLoggerService {
	@Autowired
	private TsLoggerMapper mapper;

	@Override
	public void add(TsLogger logger) {
		logger.setCreateDate(new Date());
		mapper.insert(logger);
	}
}
