package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.Finger;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/2 0002 22:08
 */
public interface FingerService {
	/**
	 * @param userID
	 * @param deviceId
	 * @return
	 */
	Finger getByUser(int userID, int deviceId);

	List<Finger> getByUser(Integer userId);

	/**
	 * @param finger
	 */
	void add(Finger finger);

	void delete(Finger finger);

	/**
	 * @param deivceID
	 */
	void deleteAllByDevice(int deivceID);

	/**
	 * @param deivceID
	 * @param userID
	 */
	void deleteByDeviceAndAddr(int deivceID, Integer userID);


	/**
	 * 查询该印章录入的指纹列表信息
	 * @param signetId
	 * @param orgId
	 * @return
	 */
	List<Finger> getBySignet(Integer signetId, Integer orgId);

	/**
	 * @param signetId
	 * @return
	 */
	List<Integer> getAddrByDevice(Integer signetId);

	/**
	 * 查询指定设备用户的指纹录入信息
	 *
	 * @param orgId    集团ID
	 * @param signetId 设备ID
	 * @param userId   用户ID
	 * @return
	 */
	Finger getByOrgAndDeviceAndUser(Integer orgId, Integer signetId, int userId);

	void update(Finger finger);
}
