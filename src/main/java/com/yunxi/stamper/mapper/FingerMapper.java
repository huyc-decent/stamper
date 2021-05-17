package com.yunxi.stamper.mapper;


import com.yunxi.stamper.entity.Finger;
import com.yunxi.stamper.entityVo.FingerEntity;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;

@Component
public interface FingerMapper extends MyMapper<Finger> {
	/**
	 * 查询指定指定印章+公司下的指纹列表
	 * @param signetId
	 * @param orgId
	 * @return
	 */
	List<Finger> selectBySignetAndOrg(Integer signetId, Integer orgId);

	/**
	 *
	 * @param signetId
	 * @return
	 */
	List<Integer> selectAddrByDevice(Integer signetId);

	/**
	 * 查询指定设备用户的指纹录入信息
	 *
	 * @param orgId    集团ID
	 * @param signetId 设备ID
	 * @param userId   用户ID
	 * @return
	 */
	Finger selectByOrgAndDeviceAndUser(Integer orgId,Integer signetId,int userId);

	/**
	 * 查询所有设备的指纹信息
	 *
	 * @param deivceID 设备ID
	 * @return
	 */
	List<Finger> selectAllByDevice(Integer deivceID);

	/***查找指定用户指纹信息*/
	Finger selectByUserAndDevice(Integer userID, Integer deviceId);
}