package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.ConfigError;
import com.yunxi.stamper.mapper.ConfigErrorMapper;
import com.yunxi.stamper.service.ConfigErrorService;
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
 * @date 2019/6/28 0028 14:32
 */
@Service
public class IConfigErrorService implements ConfigErrorService {
	@Autowired
	private ConfigErrorMapper mapper;

	@Override
	public List<ConfigError> getByUUID(String uuid) {
		if (StringUtils.isBlank(uuid)) {
			return null;
		}
		Example example = new Example(ConfigError.class);
		example.createCriteria().andIsNull("deleteDate")
				.andEqualTo("uuid", uuid);
		example.orderBy("createDate").desc();
		return mapper.selectByExample(example);
	}

	@Override
	@Transactional
	public void del(ConfigError error) {
		int delCount = 0;
		if (error != null && error.getId() != null) {
			error.setDeleteDate(new Date());
			delCount = mapper.delete(error);
		}
		if (delCount != 1) {
			throw new PrintException("设备日志记录删除失败");
		}
	}

	@Override
	public ConfigError get(Integer id) {
		if (id == null) {
			return null;
		}
		Example example = new Example(ConfigError.class);
		example.createCriteria().andIsNull("deleteDate").andEqualTo("id", id);
		return mapper.selectOneByExample(example);
	}

	@Override
	@Transactional
	public void add(ConfigError configError) {
		int addCount = 0;
		if (configError != null) {
			configError.setCreateDate(new Date());
			addCount = mapper.insert(configError);
		}
		if (addCount != 1) {
			throw new PrintException("配置错误信息添加失败");
		}
	}
}
