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
			throw new PrintException("??????????????????");
		}
	}

	/**
	 * ???????????????????????????,???????????????????????????,????????????????????????????????????,???????????????,????????????100%
	 */
	@Override
	public int getTsValue(Integer deviceId, Integer orgId) {
		int tsValue = 100;
		if (deviceId != null && orgId != null) {
			//???????????????
			String key = RedisGlobal.THRESHOLD_SIGNET + deviceId;
			Object tsObj = redisUtil.get(key);
			if (tsObj != null && StringUtils.isNotBlank(tsObj.toString())) {
				return Integer.parseInt(tsObj.toString());
			}

			//???????????????????????????
			Threshold ts = getByDeviceId(deviceId, orgId);
			if (ts == null) {
				//???????????????????????????
				ts = getDefaultByOrg(orgId);
			}
			if (ts != null) {
				tsValue = ts.getThresholdValue();
			}

			//?????????
			redisUtil.set(key, tsValue, RedisGlobal.THRESHOLD_SIGNET_TIME_OUT);
		}
		return tsValue;
	}

	/**
	 * ????????????????????????????????????
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
			throw new PrintException("??????????????????");
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
			throw new PrintException("??????????????????");
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
	 * ?????????????????????????????????
	 *
	 * @param orgId ??????ID
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
	 * ??????????????????
	 *
	 * @param orgId          ??????ID
	 * @param deviceId       ??????ID 0???????????????
	 * @param thresholdValue ??????
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
			LocalHandle.complete("????????????");
		} else {
			LocalHandle.setOldObj(threshold);
			threshold.setDeviceId(deviceId);
			threshold.setOrgId(orgId);
			threshold.setThresholdValue(thresholdValue);
			update(threshold);
			LocalHandle.setNewObj(threshold);
			LocalHandle.complete("????????????");
		}

	}

}
