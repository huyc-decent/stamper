package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.entity.Finger;
import com.yunxi.stamper.mapper.FingerMapper;
import com.yunxi.stamper.service.FingerService;
import com.yunxi.stamper.sys.error.base.PrintException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/2 0002 22:08
 */
@Slf4j
@Service
public class IFingerService implements FingerService {

	@Autowired
	private FingerMapper mapper;

	/**
	 * 清除指定设备的所有指纹
	 *
	 * @param deivceID
	 */
	@Override
	@Transactional
	public void deleteAllByDevice(int deivceID) {
		List<Finger> fingers = mapper.selectAllByDevice(deivceID);
		if (fingers == null || fingers.isEmpty()) {
			return;
		}
		for (int i = 0; i < fingers.size(); i++) {
			Finger finger = fingers.get(i);
			finger.setDeleteDate(new Date());
			finger.setRemarks(UUID.randomUUID().toString());
			mapper.updateByPrimaryKey(finger);
		}
	}

	/**
	 * 查询该印章录入的指纹列表信息
	 */
	@Override
	public List<Finger> getBySignet(Integer signetId,
									Integer orgId) {
		if (signetId == null || orgId == null) {
			return null;
		}
		return mapper.selectBySignetAndOrg(signetId, orgId);
	}

	/**
	 * 清除指定设备的指定位置指纹
	 */
	@Override
	@Transactional
	public void deleteByDeviceAndAddr(int deivceID,
									  Integer userID) {
		Example example = new Example(Finger.class);
		Example.Criteria criteria = example.createCriteria()
				.andIsNull("deleteDate")
				.andEqualTo("deviceId", deivceID);
		if (userID != null) {
			criteria.andEqualTo("userId", userID);
		}
		List<Finger> fingers = mapper.selectByExample(example);
		if (fingers != null && fingers.size() > 0) {
			for (int i = 0; i < fingers.size(); i++) {
				Finger finger = fingers.get(i);
				finger.setDeleteDate(new Date());
				mapper.updateByExampleSelective(finger, example);
			}
		}
	}

	@Override
	@Transactional
	public void add(Finger finger) {
		int insert = 0;
		if (finger != null) {
			Date now = new Date();
			finger.setCreateDate(now);
			finger.setUpdateDate(now);
			insert = mapper.insert(finger);
		}
		if (insert != 1) {
			log.error("指纹入库失败\tfinger:" + CommonUtils.objToJson(finger));
			throw new PrintException("指纹添加失败");
		}
	}

	@Override
	@Transactional
	public void update(Finger finger) {
		int update = 0;
		if (finger != null) {
			finger.setUpdateDate(new Date());
			update = mapper.updateByPrimaryKeySelective(finger);
		}
		if (update != 1) {
			log.error("指纹更新入库失败\tfinger:" + CommonUtils.objToJson(finger));
			throw new RuntimeException("指纹更新失败");
		}
	}

	@Override
	@Transactional
	public void delete(Finger finger) {
		int delete = 0;
		if (finger != null) {
			finger.setDeleteDate(new Date());
			delete = mapper.updateByPrimaryKey(finger);
		}
		if (delete != 1) {
			log.error("指纹信息删除失败\tfinger:{}", CommonUtils.objToJson(finger));
			throw new PrintException("指纹删除失败");
		}
	}


	/**
	 * 查询指定用户在设备上的指纹信息
	 *
	 * @param userID 指纹所属人id
	 * @return
	 */
	@Override
	public Finger getByUser(int userID, int deviceId) {
		return mapper.selectByUserAndDevice(userID, deviceId);
	}

	@Override
	public List<Finger> getByUser(Integer userId) {
		if (userId == null) {
			return null;
		}
		Example example = new Example(Finger.class);
		example.createCriteria().andIsNull("deleteDate")
				.andEqualTo("userId", userId);
		return mapper.selectByExample(example);
	}

	@Override
	public List<Integer> getAddrByDevice(Integer signetId) {
		return mapper.selectAddrByDevice(signetId);
	}

	/**
	 * 查询指定设备用户的指纹录入信息
	 *
	 * @param orgId    集团ID
	 * @param signetId 设备ID
	 * @param userId   用户ID
	 * @return
	 */
	@Override
	public Finger getByOrgAndDeviceAndUser(Integer orgId, Integer signetId, int userId) {
		if (orgId == null || signetId == null) {
			return null;
		}
		return mapper.selectByOrgAndDeviceAndUser(orgId, signetId, userId);
	}
}

