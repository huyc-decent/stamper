package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.entity.ReduceFileInfo;
import com.yunxi.stamper.mapper.ReduceFileInfoMapper;
import com.yunxi.stamper.service.ReduceFileInfoService;
import com.yunxi.stamper.sys.config.ProjectProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.UUID;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/12/4 0004 15:50
 */
@Service
public class IReduceFileInfoService implements ReduceFileInfoService {
	@Autowired
	private ReduceFileInfoMapper mapper;
	@Autowired
	private ProjectProperties properties;

	/**
	 * 查询指定原图的缩略图信息
	 *
	 * @param fileId 原图ID
	 * @return
	 */
	@Override
	public ReduceFileInfo getByFileInfo(String fileId) {
		if (StringUtils.isBlank(fileId)) {
			return null;
		}
		return mapper.selectByFileInfoId(fileId);
	}

	@Override
	public ReduceFileInfo get(String fileId) {
		if (StringUtils.isNotBlank(fileId)) {
			Example example = new Example(ReduceFileInfo.class);
			example.createCriteria().andEqualTo("id", fileId);
			return mapper.selectOneByExample(example);
		}
		return null;
	}

	/**
	 * 添加缩率图信息
	 *
	 * @param reduceFileInfo 缩率图信息
	 */
	@Override
	@Transactional
	public void add(ReduceFileInfo reduceFileInfo) {
		if (reduceFileInfo != null) {
			reduceFileInfo.setId(UUID.randomUUID().toString().toLowerCase().replace("-", ""));
			if (StringUtils.isBlank(reduceFileInfo.getHost())) {
				reduceFileInfo.setHost(properties.getFile().getHost());
			}
			mapper.insert(reduceFileInfo);
		}
	}

}
