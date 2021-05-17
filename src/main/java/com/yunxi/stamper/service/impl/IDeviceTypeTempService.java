package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.entity.DeviceTypeTemp;
import com.yunxi.stamper.mapper.DeviceTypeTempMapper;
import com.yunxi.stamper.service.DeviceTypeTempService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/4/29 0029 16:02
 */
@Service
public class IDeviceTypeTempService implements DeviceTypeTempService {
	@Autowired
	private DeviceTypeTempMapper mapper;

	/**
	 * 查询系统默认模板列表
	 *
	 * @return
	 */
	@Override
	public List<DeviceTypeTemp> getAllTemps() {
		return mapper.selectAll();
	}

	@Override
	@Transactional
	public void add(DeviceTypeTemp temp) {
		mapper.insert(temp);
	}

	/**
	 * 查询指定类型名称的类型模板实例
	 *
	 * @param typeName 类型名称
	 * @return
	 */
	@Override
	public DeviceTypeTemp getByName(String typeName) {
		if (StringUtils.isNotBlank(typeName)) {
			DeviceTypeTemp dtt = new DeviceTypeTemp();
			dtt.setName(typeName);
			return mapper.selectOne(dtt);
		}
		return null;
	}
}
