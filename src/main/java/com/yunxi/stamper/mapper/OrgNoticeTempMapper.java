package com.yunxi.stamper.mapper;

import com.yunxi.stamper.entity.OrgNoticeTemp;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface OrgNoticeTempMapper extends MyMapper<OrgNoticeTemp> {
	List<Integer> selectByOrg(Integer orgId);
}