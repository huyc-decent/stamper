package com.yunxi.stamper.mapper;


import com.yunxi.stamper.entity.Sms;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public interface SmsMapper extends MyMapper<Sms> {
	/**
	 * 查询大于指定时间的短信列表
	 * @param date
	 * @return
	 */
	List<Sms> selectByGreaterThanAndSendError(Date date);
}