package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.ConfigVersion;
import com.yunxi.stamper.mapper.ConfigVersionMapper;
import com.yunxi.stamper.service.ConfigVersionService;
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
 * @date 2019/7/4 0004 14:29
 */
@Service
public class IConfigVersionService implements ConfigVersionService {
	@Autowired
	private ConfigVersionMapper mapper;

	@Override
	public ConfigVersion getByVersion(String version, Integer type) {
		if (StringUtils.isBlank(version)) {
			return null;
		}
		Example example = new Example(ConfigVersion.class);
		example.createCriteria().andIsNull("deleteDate")
				.andEqualTo("version", version)
				.andEqualTo("type", type);
		return mapper.selectOneByExample(example);
	}

	@Override
	@Transactional
	public void del(ConfigVersion cv) {
		int delCount = 0;
		if (cv != null && cv.getId() != null) {
			cv.setDeleteDate(new Date());
			delCount = mapper.updateByPrimaryKey(cv);
		}
		if (delCount != 1) {
			throw new PrintException("版本删除失败");
		}
	}

	@Override
	public ConfigVersion get(Integer id) {
		Example example = new Example(ConfigVersion.class);
		example.createCriteria().andIsNull("deleteDate").andEqualTo("id", id);
		return mapper.selectOneByExample(example);
	}

	@Override
	@Transactional
	public void add(ConfigVersion cv) {
		int addCount = 0;
		if (cv != null) {
			cv.setCreateDate(new Date());
			addCount = mapper.insert(cv);
		}
		if (addCount != 1) {
			throw new PrintException("版本添加失败");
		}
	}

	@Override
	public List<ConfigVersion> getAll(Integer type) {
		Example example = new Example(ConfigVersion.class);
		if (type != null) {
			example.createCriteria().andIsNull("deleteDate").andEqualTo("type", type);
		} else {
			example.createCriteria().andIsNull("deleteDate");
		}
		example.orderBy("createDate").desc();
		return mapper.selectByExample(example);
	}
}
