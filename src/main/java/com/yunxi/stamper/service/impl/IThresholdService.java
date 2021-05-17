package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.base.BaseService;
import com.yunxi.stamper.commons.other.RedisGlobal;
import com.yunxi.stamper.entity.Threshold;
import com.yunxi.stamper.entityVo.ThresholdEntity;
import com.yunxi.stamper.logger.threadLocal.LocalHandle;
import com.yunxi.stamper.mapper.ThresholdMapper;
import com.yunxi.stamper.service.ThresholdService;
import com.yunxi.stamper.sys.context.SpringContextUtils;
import com.yunxi.stamper.sys.error.base.PrintException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Service
public class IThresholdService extends BaseService implements ThresholdService {
	@Autowired
	private ThresholdMapper mapper;

	@Override
	public List<Threshold> get(Integer orgId, String keyword) {
		if (orgId != null) {
			Example example = new Example(Threshold.class);
			Example.Criteria criteria = example.createCriteria()
					.andIsNull("deleteDate")
					.andEqualTo("orgId", orgId)
					.andNotEqualTo("deviceId", 0);
			if (StringUtils.isNotBlank(keyword)) {
				criteria.andLike("name", "%" + keyword + "%");
			}
			return mapper.selectByExample(example);
		}
		return null;
	}

	@Override
	public Threshold getByDeviceId(Integer deviceId, Integer orgId) {
		if (deviceId != null) {
			Example example = new Example(Threshold.class);
			example.createCriteria()
					.andIsNull("deleteDate")
					.andEqualTo("orgId", orgId)
					.andEqualTo("deviceId", deviceId);
			return mapper.selectOneByExample(example);
		}
		return null;
	}

	@Override
	@Transactional
	public void update(Threshold threshold) {
		int updateCount = 0;
		if (threshold != null && threshold.getId() != null) {
			threshold.setUpdateDate(new Date());
			updateCount = mapper.updateByPrimaryKey(threshold);
		}
		if (updateCount != 1) {
			throw new PrintException("阈值更新失败");
		}
	}

	/**
	 * 查询指定印章的阈值,如果印章不存在阈值,则查询印章所属公司的阈值,如果也没有,返回默认100%
	 */
	@Override
	public int getTsValue(Integer deviceId, Integer orgId) {
		int tsValue = 100;
		if (deviceId != null && orgId != null) {
			//从缓存查询
			String key = RedisGlobal.THRESHOLD_SIGNET + deviceId;
			Object tsObj = redisUtil.get(key);
			if (tsObj != null && StringUtils.isNotBlank(tsObj.toString())) {
				return Integer.parseInt(tsObj.toString());
			}

			//缓存没有从数据查询
			Threshold ts = getByDeviceId(deviceId, orgId);
			if (ts == null) {
				//查询该公司全局阈值
				ts = getDefaultByOrg(orgId);
			}
			if (ts != null) {
				tsValue = ts.getThresholdValue();
			}

			//存缓存
			redisUtil.set(key, tsValue, RedisGlobal.THRESHOLD_SIGNET_TIME_OUT);
		}
		return tsValue;
	}

	/**
	 * 查询指定公司全局默认阈值
	 */
	@Override
	public Threshold getDefaultByOrg(Integer orgId) {
		if (orgId != null) {
			Example example = new Example(Threshold.class);
			example.createCriteria().andIsNull("deleteDate")
					.andEqualTo("orgId", orgId)
					.andEqualTo("deviceId", 0);
			return mapper.selectOneByExample(example);
		}
		return null;
	}

	@Override
	@Transactional
	public void add(Threshold threshold) {
		int addCount = 0;
		if (threshold != null) {
			threshold.setCreateDate(new Date());
			addCount = mapper.insert(threshold);
		}
		if (addCount != 1) {
			throw new PrintException("阈值更新失败");
		}
	}

	@Override
	@Transactional
	public void del(Threshold threshold) {
		int delete = 0;
		if (threshold != null) {
			threshold.setDeleteDate(new Date());
			delete = mapper.updateByPrimaryKey(threshold);
		}
		if (delete != 1) {
			throw new PrintException("阈值删除失败");
		}
	}

	@Override
	public List<Threshold> getByDevice(Integer signetId) {
		Example example = new Example(Threshold.class);
		example.createCriteria().andIsNull("deleteDate")
				.andEqualTo("deviceId", signetId);
		return mapper.selectByExample(example);
	}

	/**
	 * 查询管理的印章阈值列表
	 *
	 * @param orgId 集团ID
	 * @return
	 */
	@Override
	public List<ThresholdEntity> getByOrgAndDepartmentAndKeeper(Integer orgId, String keyword) {
		if (orgId == null) {
			return null;
		}
		SpringContextUtils.setPage();
		return mapper.selectByOrgAndDepartmentAndKeeper(orgId, keyword);
	}

	/**
	 * 更新阈值信息
	 *
	 * @param orgId          集团ID
	 * @param deviceId       设备ID 0：全局阈值
	 * @param thresholdValue 阈值
	 */
	@Override
	@Transactional
	public void updateThreshold(@NotNull Integer orgId, @NotNull Integer deviceId, @NotNull Integer thresholdValue) {

		Threshold threshold = getByDeviceId(deviceId, orgId);
		if (threshold == null) {
			threshold = new Threshold();
			threshold.setDeviceId(deviceId);
			threshold.setOrgId(orgId);
			threshold.setThresholdValue(thresholdValue);
			add(threshold);
			LocalHandle.setNewObj(threshold);
			LocalHandle.complete("新增阈值");
		} else {
			LocalHandle.setOldObj(threshold);
			threshold.setDeviceId(deviceId);
			threshold.setOrgId(orgId);
			threshold.setThresholdValue(thresholdValue);
			update(threshold);
			LocalHandle.setNewObj(threshold);
			LocalHandle.complete("更新阈值");
		}

	}

}
