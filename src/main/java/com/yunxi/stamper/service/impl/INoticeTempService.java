package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.NoticeTemp;
import com.yunxi.stamper.entityVo.NoticeVoSelect;
import com.yunxi.stamper.mapper.NoticeTempMapper;
import com.yunxi.stamper.service.NoticeTempService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/7 0007 17:09
 */
@Service
public class INoticeTempService implements NoticeTempService {

	@Autowired
	private NoticeTempMapper mapper;

	@Override
	public List<NoticeVoSelect> getAllByVo() {
		return mapper.selectByVo();
	}

	@Override
	@Transactional
	public void del(NoticeTemp noticeTemp) {
		int delCount = 0;

		noticeTemp.setDeleteDate(new Date());
		delCount = mapper.updateByPrimaryKey(noticeTemp);

		if(delCount!=1){
			throw new PrintException("模板删除失败");
		}
	}

	@Override
	@Transactional
	public void update(NoticeTemp noticeTemp) {
		int updateCount = 0;

		noticeTemp.setUpdateDate(new Date());
		noticeTemp.setDeleteDate(null);
		updateCount = mapper.updateByPrimaryKey(noticeTemp);

		if (updateCount != 1) {
			throw new PrintException("模板更新失败");
		}
	}

	@Override
	public NoticeTemp get(Integer id) {
		if (id != null) {
			Example example = new Example(NoticeTemp.class);
			example.createCriteria().andIsNull("deleteDate")
					.andEqualTo("id", id);
			return mapper.selectOneByExample(example);
		}
		return null;
	}

	@Override
	public NoticeTemp getByName(String name) {
		if (StringUtils.isNotBlank(name)) {
			Example example = new Example(NoticeTemp.class);
			example.createCriteria().andEqualTo("name", name)
					.andIsNull("deleteDate");
			return mapper.selectOneByExample(example);
		}
		return null;
	}

	@Override
	public NoticeTemp getByCode(String code) {
		if (StringUtils.isNotBlank(code)) {
			Example example = new Example(NoticeTemp.class);
			example.createCriteria().andEqualTo("code", code)
					.andIsNull("deleteDate");
			return mapper.selectOneByExample(example);
		}
		return null;
	}

	@Override
	@Transactional
	public void add(NoticeTemp noticeTemp) {
		int addCount = 0;
		if (noticeTemp != null) {
			noticeTemp.setCreateDate(new Date());
			noticeTemp.setUpdateDate(null);
			noticeTemp.setDeleteDate(null);
			addCount = mapper.insert(noticeTemp);
		}
		if (addCount != 1) {
			throw new PrintException("消息模板添加失败");
		}
	}

	@Override
	public List<NoticeTemp> getAll() {
		Example example = new Example(NoticeTemp.class);
		example.createCriteria().andIsNull("deleteDate");
		return mapper.selectByExample(example);
	}
}
