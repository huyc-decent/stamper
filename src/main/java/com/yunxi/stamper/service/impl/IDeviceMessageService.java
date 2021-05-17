package com.yunxi.stamper.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.DeviceMessage;
import com.yunxi.stamper.mapper.DeviceMessageMapper;
import com.yunxi.stamper.service.DeviceMessageService;
import lombok.extern.slf4j.Slf4j;
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
 * @date 2019/5/17 0017 16:41
 */
@Slf4j
@Service
public class IDeviceMessageService implements DeviceMessageService {

	@Autowired
	private DeviceMessageMapper mapper;

	/**
	 * 查询最后一个未推送成功的指令(指定title)
	 */
	@Override
	public DeviceMessage getByNameAndSignetAndStatus(String title, Integer signetId, Integer status) {
		if (StringUtils.isNotBlank(title) && signetId != null && status != null) {
			return mapper.selectLastOneByTitleAndSignetAndStatus(title, signetId, status);
		}
		return null;
	}

	@Override
	@Transactional
	public void update(DeviceMessage dm) {
		int updateCount = 0;
		if (dm != null && dm.getId() != null) {
			dm.setUpdateDate(new Date());
			updateCount = mapper.updateByPrimaryKey(dm);
		}
		if (updateCount != 1) {
			log.error("离线消息更新失败:{}", dm == null ? null : JSONObject.toJSONString(dm));
			throw new PrintException("离线消息更新失败");
		}
	}

	/**
	 * 查询印章离线消息列表
	 *
	 * @param signetId 设备ID
	 * @return 结果
	 */
	@Override
	public List<DeviceMessage> getBySignet(Integer signetId) {
		if (signetId != null) {
			Example example = new Example(DeviceMessage.class);
			example.createCriteria()
					.andEqualTo("recipientId", signetId)
					.andEqualTo("pushStatus", 1)
					.andIsNull("deleteDate");
			return mapper.selectByExample(example);
		}
		return null;
	}

	@Transactional
	public void del(DeviceMessage dm) {
		int delCount = 0;
		if (dm != null && dm.getId() != null) {
			dm.setDeleteDate(new Date());
			delCount = mapper.deleteByPrimaryKey(dm.getId());
		}
		if (delCount != 1) {
			log.info("离线消息移除失败:{}", dm == null ? null : JSONObject.toJSONString(dm));
			throw new PrintException("离线消息移除失败");
		}
	}

	/**
	 * 添加或者更新离线消息(用于只能推送1次的离线消息)
	 */
	@Override
	@Transactional
	public void addOrUpdate(DeviceMessage dm) {
		if (dm != null) {
			String title = dm.getTitle();
			Integer recipientId = dm.getRecipientId();

			DeviceMessage _dm = getByNameAndSignetAndStatus(title, recipientId, 1);
			if (_dm != null) {
				del(_dm);
			}
			add(dm);
		}
	}

	@Override
	@Transactional
	public void add(DeviceMessage deviceMessage) {
		int addCount = 0;

		if (deviceMessage != null) {
			deviceMessage.setId(null);
			deviceMessage.setCreateDate(new Date());
			addCount = mapper.insert(deviceMessage);
		}

		if (addCount != 1) {
			log.info("设备离线消息初始化失败:{}", deviceMessage == null ? null : JSONObject.toJSONString(deviceMessage));
			throw new PrintException("设备离线消息初始化失败");
		}
	}
}
