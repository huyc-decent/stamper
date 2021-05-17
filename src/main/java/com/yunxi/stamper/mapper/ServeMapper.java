package com.yunxi.stamper.mapper;


import com.yunxi.stamper.entity.Serve;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

@Component
public interface ServeMapper extends MyMapper<Serve> {
	//查询该公司短信服务实例
	Serve selectSMSByOrg(Integer orgId);
}