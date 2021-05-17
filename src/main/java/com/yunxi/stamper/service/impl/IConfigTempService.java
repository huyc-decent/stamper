package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.ConfigTemp;
import com.yunxi.stamper.mapper.ConfigTempMapper;
import com.yunxi.stamper.service.ConfigTempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/8/12 0012 16:23
 */
@Service
public class IConfigTempService implements ConfigTempService {
	@Autowired
	private ConfigTempMapper mapper;

	@Override
	public ConfigTemp getByDefault() {
		Example example = new Example(ConfigTemp.class);
		example.createCriteria().andIsNull("deleteDate").andEqualTo("type", 0);
		return mapper.selectOneByExample(example);
	}

	@Override
	public List<ConfigTemp> getList() {
		Example example = new Example(ConfigTemp.class);
		example.createCriteria().andIsNull("deleteDate");
		example.orderBy("createDate").desc();
		return mapper.selectByExample(example);
	}

	@Override
	@Transactional
	public void update(ConfigTemp configTemp) {
		int updateCount = 0;
		if (configTemp != null && configTemp.getId() != null) {
			configTemp.setUpdateDate(new Date());
			updateCount = mapper.updateByPrimaryKey(configTemp);
		}
		if (updateCount != 1) {
			throw new PrintException("模板更新失败");
		}
	}

	@Override
	public ConfigTemp get(Integer configTempId) {
		if (configTempId != null) {
			return mapper.selectByPrimaryKey(configTempId);
		}
		return null;
	}

	@Override
	@Transactional
	public void del(ConfigTemp configTemp) {
		int delCount = 0;
		if (configTemp != null && configTemp.getId() != null) {
			configTemp.setDeleteDate(new Date());
			delCount = mapper.updateByPrimaryKey(configTemp);
		}
		if (delCount != 1) {
			throw new PrintException("模板删除失败");
		}
	}

	@Override
	@Transactional
	public void add(ConfigTemp configTemp) {
		int addCount = 0;
		if (configTemp != null) {
			configTemp.setId(null);
			configTemp.setCreateDate(new Date());
			configTemp.setDeleteDate(null);
			configTemp.setUpdateDate(null);
			addCount = mapper.insert(configTemp);
		}
		if (addCount != 1) {
			throw new PrintException("模板添加失败");
		}
	}
}
