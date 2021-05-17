package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.DeviceType;
import com.yunxi.stamper.entity.DeviceTypeTemp;
import com.yunxi.stamper.mapper.DeviceTypeMapper;
import com.yunxi.stamper.service.DeviceTypeService;
import com.yunxi.stamper.service.DeviceTypeTempService;
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
 * @date 2019/5/2 0002 20:30
 */
@Service
public class IDeviceTypeService implements DeviceTypeService {

	@Autowired
	private DeviceTypeMapper mapper;
	@Autowired
	private DeviceTypeTempService deviceTypeTempService;

	/**
	 * 查询公司印章类型列表
	 *
	 * @param orgId
	 * @return
	 */
	@Override
	public List<DeviceType> getByOrg(Integer orgId) {
		if (orgId == null) {
			return null;
		}
		Example example = new Example(DeviceType.class);
		example.createCriteria().andIsNull("deleteDate")
				.andEqualTo("orgId", orgId);
		return mapper.selectByExample(example);
	}

	@Override
	public DeviceType get(Integer deviceTypeId) {
		if (deviceTypeId == null) {
			return null;
		}
		return mapper.selectByPrimaryKey(deviceTypeId);
	}

	@Override
	@Transactional
	public void add(DeviceType type) {
		if (type != null) {
			mapper.insert(type);
		}
	}

	/**
	 * 查询指定公司下的指定设备名称的类型实例
	 *
	 * @param name  类型名称
	 * @param orgId 公司id
	 * @return
	 */
	@Override
	public DeviceType getByNameFromOrg(String name, Integer orgId) {
		if (StringUtils.isBlank(name) || orgId == null) {
			return null;
		}
		DeviceType dt = new DeviceType();
		dt.setName(name);
		dt.setOrgId(orgId);
		return mapper.selectOne(dt);
	}

	@Override
	@Transactional
	public void createTypesByOrgInit(Integer orgId) {
		List<DeviceTypeTemp> temps = deviceTypeTempService.getAllTemps();
		if (temps == null || temps.isEmpty()) {
			throw new PrintException("设备类型列表不存在");
		}

		for (int i = 0; i < temps.size(); i++) {
			DeviceTypeTemp temp = temps.get(i);
			String name = temp.getName();
			synchronized (this) {

				DeviceType type = getByNameFromOrg(name, orgId);
				if (type == null) {
					type = new DeviceType();
					type.setCreateDate(new Date());
					type.setName(name);
					type.setRemark("公司初始化");
					type.setOrgId(orgId);
					add(type);
				}
			}
		}
	}
}
