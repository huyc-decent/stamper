package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.Sms;
import com.yunxi.stamper.entity.User;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/14 0014 23:31
 */
public interface SMSService {
	void add(Sms sms);

	Sms get(Integer smsId);

	/**
	 * 发送短信
	 *
	 * @param receive   接收人
	 * @param smsTempId 短信模板id
	 * @param args      模板所需参数
	 */
	void sendSMS(User receive, Integer smsTempId, String... args);

	/**
	 * 查询大于指定时间的短信列表
	 * @param date
	 * @return
	 */
	List<Sms> getGreaterThanAndSendError(Date date);

	void update(Sms sms);
}
