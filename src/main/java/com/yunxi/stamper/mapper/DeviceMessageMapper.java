package com.yunxi.stamper.mapper;


import com.yunxi.stamper.entity.DeviceMessage;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

@Component
public interface DeviceMessageMapper extends MyMapper<DeviceMessage> {
	//查询最后一个未推送成功的指令(指定title)
	DeviceMessage selectLastOneByTitleAndSignetAndStatus(String title, Integer signetId, Integer status);
}