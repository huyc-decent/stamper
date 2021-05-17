package com.yunxi.stamper.mapper;


import com.yunxi.stamper.entity.AppVersion;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

@Component
public interface AppVersionMapper extends MyMapper<AppVersion> {
	//查询指定客户端的最后一个新版本
	AppVersion selectByLastVersion(String client);
}