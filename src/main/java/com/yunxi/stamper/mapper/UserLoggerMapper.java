package com.yunxi.stamper.mapper;


import com.yunxi.stamper.entity.UserLogger;
import com.yunxi.stamper.entityVo.UserLoggerVo;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserLoggerMapper extends MyMapper<UserLogger> {
	List<UserLoggerVo> selectByUserAndNormal(Integer orgId,Integer userId);
}