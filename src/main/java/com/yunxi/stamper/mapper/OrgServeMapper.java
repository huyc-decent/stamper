package com.yunxi.stamper.mapper;

import com.yunxi.stamper.entity.OrgServe;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

@Component
public interface OrgServeMapper extends MyMapper<OrgServe> {
	//查询公司指定服务编码的实例
	OrgServe selectByOrgAndCode(Integer orgId, String serveCode);
}