package com.yunxi.stamper.mapper;

import com.yunxi.stamper.entity.MessageTemp;
import com.yunxi.stamper.entityVo.MessageTempVo;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface MessageTempMapper extends MyMapper<MessageTemp> {
	List<MessageTempVo> selectByAll();
}