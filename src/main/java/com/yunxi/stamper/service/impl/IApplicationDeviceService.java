package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.ApplicationDevice;
import com.yunxi.stamper.entityVo.DeviceSelectVo;
import com.yunxi.stamper.entityVo.UseCountVo;
import com.yunxi.stamper.mapper.ApplicationDeviceMapper;
import com.yunxi.stamper.service.ApplicationDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/3 0003 17:02
 */
@Service
public class IApplicationDeviceService implements ApplicationDeviceService {

	@Autowired
	private ApplicationDeviceMapper mapper;


	/**
	 * 查询申请单对应印章的 记录
	 */
	@Override
	public ApplicationDevice getByApplicationAndSignet(Integer applicationId, Integer deviceId) {
		if (applicationId != null && deviceId != null) {
			Example example = new Example(ApplicationDevice.class);
			example.createCriteria().andEqualTo("applicationId", applicationId)
					.andEqualTo("deviceId", deviceId)
					.andIsNull("deleteDate");
			return mapper.selectOneByExample(example);
		}
		return null;
	}

	/**
	 * 查询该申请单 印章已盖次数
	 */
	@Override
	public List<UseCountVo> getUseCountByApplication(Integer applicationId) {
		if (applicationId != null) {
			return mapper.selectUseCountByApplication(applicationId);
		}
		return null;
	}

	/**
	 * 印章已使用,次数-1
	 */
	@Override
	public void signetMinus1(Integer applicationID, Integer deviceID) {
		mapper.signetMinus1(applicationID, deviceID);
	}

	/**
	 * 查询申请单对应的印章id+名称
	 */
	@Override
	public List<DeviceSelectVo> getByApplicationId(Integer applicationId) {
		if (applicationId != null) {
			return mapper.selectByApplicationID(applicationId);
		}
		return null;
	}

	@Override
	public List<ApplicationDevice> getByApplication(Integer applicationId) {
		if (applicationId != null) {
			Example example = new Example(ApplicationDevice.class);
			example.createCriteria().andIsNull("deleteDate")
					.andEqualTo("applicationId", applicationId);
			return mapper.selectByExample(example);
		}
		return null;
	}

	@Override
	@Transactional
	public void add(ApplicationDevice ad) {
		int addCount = 0;
		if (ad != null) {
			ad.setCreateDate(new Date());
			addCount = mapper.insert(ad);
		}
		if (addCount != 1) {
			throw new PrintException("申请单印章创建失败");
		}
	}

	@Override
	@Transactional
	public void update(ApplicationDevice applicationDevice) {
		int updateCount = 0;
		if (applicationDevice != null && applicationDevice.getId() != null) {
			applicationDevice.setUpdateDate(new Date());
			updateCount = mapper.updateByPrimaryKey(applicationDevice);
		}
		if (updateCount != 1) {
			throw new PrintException("操作失败(申请单-印章)");
		}
	}

	@Override
	public ApplicationDevice get(Integer applicationId, Integer signetId) {
		if (applicationId != null && signetId != null) {
			Example example = new Example(ApplicationDevice.class);
			example.createCriteria()
					.andEqualTo("applicationId", applicationId)
					.andEqualTo("deviceId", signetId);
			return mapper.selectOneByExample(example);
		}
		return null;
	}
}
