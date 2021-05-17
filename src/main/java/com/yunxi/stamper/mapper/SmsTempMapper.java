package com.yunxi.stamper.mapper;


import com.yunxi.stamper.entity.SmsTemp;
import com.yunxi.stamper.entityVo.SMSVoSelect;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SmsTempMapper extends MyMapper<SmsTemp> {
	List<SMSVoSelect> selectByAll();

}