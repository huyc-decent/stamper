package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.SmsTemp;
import com.yunxi.stamper.entityVo.SMSVoSelect;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/14 0014 23:12
 */
public interface SmsTempService {
	void add(SmsTemp smsTemp);

	List<SmsTemp> getAll();

	SmsTemp getByCode(String smsCode);

	SmsTemp get(Integer smsTempId);

	List<SMSVoSelect> getAllByVo();

}
