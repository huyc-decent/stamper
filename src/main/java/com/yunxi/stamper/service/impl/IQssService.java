package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.commons.other.Global;
import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.Qss;
import com.yunxi.stamper.mapper.QssMapper;
import com.yunxi.stamper.service.QssService;
import com.yunxi.stamper.base.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/6/10 0010 20:13
 */
@Service
public class IQssService extends BaseService implements QssService {
	@Autowired
	private QssMapper mapper;

	@Override
	public Qss getByUrl(String url) {
		if (StringUtils.isNotBlank(url)) {
			Example example = new Example(Qss.class);
			example.createCriteria().andIsNull("deleteDate")
					.andEqualTo("url", url);
			return mapper.selectOneByExample(example);
		}
		return null;
	}

	@Override
	public Qss getByUrl(String url, Integer type) {
		if (StringUtils.isNotBlank(url)) {
			Example example = new Example(Qss.class);
			example.createCriteria().andIsNull("deleteDate")
					.andEqualTo("url", url).andEqualTo("type",type);
			return mapper.selectOneByExample(example);
		}
		return null;
	}

	@Override
	public Qss getByName(String name) {
		if (StringUtils.isNotBlank(name)) {
			Example example = new Example(Qss.class);
			example.createCriteria().andIsNull("deleteDate")
					.andEqualTo("name", name);
			return mapper.selectOneByExample(example);
		}
		return null;
	}

	@Override
	public List<String> getByArray(Integer type) {
		if (type != null) {
			//从缓存查
			String key = Global.QSS_URL + type;
			Object urlObj = redisUtil.get(key);
			//如果缓存有
			if (urlObj == null || StringUtils.isBlank(urlObj.toString())) {
				List<String> urls = mapper.selectByArray(type);
				redisUtil.set(key, urls, Global.QSS_URL_TIME_OUT);
				return urls;
			}
			//如果缓存没有
			else {
				return (ArrayList<String>) urlObj;
			}
		}
		return null;
	}

	@Override
	@Transactional
	public void update(Qss qss) {
		int updateCount = 0;
		if (qss != null && qss.getId() != null) {
			qss.setUpdateDate(new Date());
			updateCount = mapper.updateByPrimaryKey(qss);
		}
		if (updateCount != 1) {
			throw new PrintException("量子加密URL更新失败");
		}

		//删除缓存
		Integer type = qss.getType();
		String key = Global.QSS_URL + type;
		redisUtil.del(key);
	}

	@Override
	@Transactional
	public void add(Qss qss) {
		int addCount = 0;
		if (qss != null) {
			qss.setCreateDate(new Date());
			addCount = mapper.insert(qss);
		}
		if (addCount != 1) {
			throw new PrintException("量子加密URL添加失败");
		}

		//删除缓存
		Integer type = qss.getType();
		String key = Global.QSS_URL + type;
		redisUtil.del(key);
	}

	@Override
	@Transactional
	public void del(Qss qss) {
		int delCount = 0;
		if (qss != null && qss.getId() != null) {
			qss.setDeleteDate(new Date());
			delCount = mapper.updateByPrimaryKey(qss);
		}
		if (delCount != 1) {
			throw new PrintException("量子加密URL删除失败");
		}

		//删除缓存
		Integer type = qss.getType();
		String key = Global.QSS_URL + type;
		redisUtil.del(key);
	}

	@Override
	public Qss get(Integer qssId) {
		if (qssId != null) {
			Example example = new Example(Qss.class);
			example.createCriteria().andIsNull("deleteDate")
					.andEqualTo("id", qssId);
			return mapper.selectOneByExample(example);
		}
		return null;
	}

	@Override
	public List<Qss> getAll() {
		Example example = new Example(Qss.class);
		example.createCriteria().andIsNull("deleteDate");
		return mapper.selectByExample(example);
	}
}
