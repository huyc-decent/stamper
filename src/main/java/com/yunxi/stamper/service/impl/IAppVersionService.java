package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.AppVersion;
import com.yunxi.stamper.mapper.AppVersionMapper;
import com.yunxi.stamper.service.AppVersionService;
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
 * @date 2019/5/28 0028 12:54
 */
@Service
public class IAppVersionService implements AppVersionService {

	@Autowired
	private AppVersionMapper mapper;

	/**
	 * 查询指定客户端的最后一个新版本
	 */
	@Override
	public AppVersion getLastVersion(String client) {
		if (StringUtils.isBlank(client)) {
			return null;
		}
		return mapper.selectByLastVersion(client);
	}

	@Override
	@Transactional
	public void add(AppVersion appVersion) {
		if (appVersion == null) {
			return;
		}
		mapper.insert(appVersion);
	}

	@Override
	public AppVersion getByVersion(String version) {
		if (StringUtils.isBlank(version)) {
			return null;
		}
		Example example = new Example(AppVersion.class);
		example.createCriteria().andIsNull("deleteDate").andEqualTo("version", version);
		return mapper.selectOneByExample(example);
	}

	@Override
	@Transactional
	public void delete(AppVersion appVersion) {
		int delCount = 0;
		if (appVersion != null && appVersion.getId() != null) {
			appVersion.setDeleteDate(new Date());
			delCount = mapper.updateByPrimaryKey(appVersion);
		}
		if (delCount != 1) {
			throw new PrintException("app版本记录删除失败");
		}
	}

	@Override
	public List<AppVersion> getAll() {
		Example example = new Example(AppVersion.class);
		example.createCriteria().andIsNull("deleteDate");
		example.orderBy("createDate").desc();
		return mapper.selectByExample(example);
	}
}
