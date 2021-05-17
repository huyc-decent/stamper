package com.yunxi.stamper.mapper;

import com.yunxi.stamper.entity.OrgSmsTemp;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface OrgSmsTempMapper extends MyMapper<OrgSmsTemp> {
	//查询该公司启用中的短信模板id列表
	List<Integer> selectSmsTempIdByOrg(Integer orgId);
}