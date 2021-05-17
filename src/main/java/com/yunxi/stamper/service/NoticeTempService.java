package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.NoticeTemp;
import com.yunxi.stamper.entityVo.NoticeVoSelect;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/7 0007 17:09
 */
public interface NoticeTempService {
	List<NoticeTemp> getAll();

	void add(NoticeTemp noticeTemp);

	NoticeTemp getByCode(String code);

	NoticeTemp getByName(String name);

	NoticeTemp get(Integer id);

	void update(NoticeTemp noticeTemp);

	void del(NoticeTemp noticeTemp);

	List<NoticeVoSelect> getAllByVo();

}
