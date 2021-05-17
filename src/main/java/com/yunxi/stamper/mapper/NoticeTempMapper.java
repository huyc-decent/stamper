package com.yunxi.stamper.mapper;

import com.yunxi.stamper.entity.NoticeTemp;
import com.yunxi.stamper.entityVo.NoticeVoSelect;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface NoticeTempMapper extends MyMapper<NoticeTemp> {
	List<NoticeVoSelect> selectByVo();
}