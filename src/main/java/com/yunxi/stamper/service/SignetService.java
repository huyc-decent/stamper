package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.*;
import com.yunxi.stamper.entityVo.Location;
import com.yunxi.stamper.entityVo.SignetEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/2 0002 19:39
 */
public interface SignetService {
	/**
	 * 新增印章
	 *
	 * @param signet
	 */
	void add(Signet signet);

	/**
	 * 更新印章
	 *
	 * @param signet
	 */
	void update(Signet signet);

	/**
	 * 更新设备摄像头状态
	 *
	 * @param deviceId 设备ID
	 * @param status   0：开启  1：关闭
	 */
	void updateCamera(Integer deviceId, int status);

	/**
	 * 更新印章
	 *
	 * @param signet     原印章信息
	 * @param bodyId     章身ID
	 * @param name       新名称
	 * @param fileInfo   新LOGO
	 * @param keeper     新印章管理员
	 * @param auditor    新印章审计员
	 * @param department 新所属组织
	 * @param deviceType 新设备类型
	 */
	void updateSignet(Signet signet, String bodyId, String name, FileInfo fileInfo, User keeper, User auditor, Department department, DeviceType deviceType, Integer meterId, String remark);


	/**
	 * 删除印章
	 *
	 * @param signet
	 */
	void del(Signet signet);

	/**
	 * 印章信息
	 *
	 * @param signetId 印章ID
	 * @return
	 */
	Signet get(Integer signetId);

	/**
	 * 印章信息
	 *
	 * @param uuid 印章UUID
	 * @return
	 */
	Signet getByUUID(String uuid);

	/**
	 * 印章信息
	 *
	 * @param name  印章名称
	 * @param orgId 集团ID
	 * @return
	 */
	Signet getByName(String name, Integer orgId);

	/**
	 * 印章信息
	 *
	 * @param orgId    集团ID
	 * @param deviceId 印章ID
	 * @return
	 */
	Signet getByOrgAndDevice(Integer orgId, Integer deviceId);

	/**
	 * 印章列表
	 *
	 * @param orgId 集团ID
	 * @return
	 */
	List<Signet> getByOrg(Integer orgId);

	/**
	 * 印章列表
	 *
	 * @param orgId         公司ID
	 * @param type          设备类型
	 * @param keeperName    管章人姓名
	 * @param deviceName    印章名称
	 * @param departmentIds 组织ID列表
	 * @param keeperId      管章人ID
	 * @return
	 */
	List<Map<String, Object>> getByType(Integer orgId, Integer type, String keeperName, String deviceName, List<Integer> departmentIds, Integer keeperId);

	/**
	 * 所有印章的位置信息列表
	 *
	 * @param orgId 集团ID
	 * @return
	 */
	List<Location> getLocationByOrg(Integer orgId);

	/**
	 * 印章列表
	 *
	 * @param orgId         集团ID
	 * @param keeperId      管章人ID
	 * @param keyword       印章名称、ID关键词
	 * @param departmentIds 要查询的组织ID列表
	 * @param onlines       在线印章ID列表
	 * @return
	 */
	List<SignetEntity> getByOwner(Integer orgId, Integer keeperId, String keyword, List<Integer> departmentIds, Set<String> onlines);

	/**
	 * 印章列表
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 组织ID列表
	 * @return
	 */
	List<Signet> getSignetByOrgAndDepartments(Integer orgId, List<Integer> departmentIds, Integer userId);

	/**
	 * 印章总数
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 组织ID列表
	 * @return
	 */
	int getCountByOrgAndDepartments(Integer orgId, List<Integer> departmentIds);

	/**
	 * 印章ID列表
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 组织ID列表
	 * @return
	 */
	List<Integer> getIdByOrgAndDepartment(Integer orgId, List<Integer> departmentIds);

	List<Integer> getByBodyId(String bodyId);

	/**
	 * 印章列表
	 *
	 * @param orgId   集团ID
	 * @param keyword 搜索关键词
	 * @return
	 */
	List<Signet> getList(Integer orgId, String keyword);

	/**
	 * 印章列表
	 *
	 * @param departmentIds 组织ID
	 * @return 印章列表
	 */
	List<Signet> getByDepartment(List<Integer> departmentIds);

	/**
	 * 批量增加印章
	 *
	 * @param signets 印章列表
	 */
	void addBatch(List<Signet> signets);

	/**
	 * 批量修改印章
	 *
	 * @param signets 印章列表
	 */
	void updateBatch(List<Signet> signets);

	/**
	 * 查询设备列表
	 *
	 * @param orgId              集团ID
	 * @param departmentIds      组织ID
	 * @param isApplicationAbled 印章是否支持申请单
	 * @return 印章列表
	 */
	List<Signet> get(Integer orgId, List<Integer> departmentIds, Integer isApplicationAbled);

	/**
	 * 查询设备列表
	 *
	 * @param orgId         租户Id
	 * @param keeperId        管章人Id
	 * @param departmentIds 组织Id
	 * @param onlineIds     在线设备Id
	 * @param keyword       关键词
	 * @return 设备列表
	 */
	List<Signet> find(Integer orgId, Integer keeperId, List<Integer> departmentIds, List<String> onlineIds, String keyword);
}
