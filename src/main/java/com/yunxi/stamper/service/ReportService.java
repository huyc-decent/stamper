package com.yunxi.stamper.service;


import com.yunxi.stamper.entity.Report;
import com.yunxi.stamper.entityVo.ReportEntity;
import com.yunxi.stamper.entityVo.StatusEntity;
import com.yunxi.stamper.entityVo.UserInfo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/6/25 0025 17:05
 */
public interface ReportService {
	void add(Report report);

	void update(Report report);

	List<Report> getByUser(Integer userId);

	Report get(Integer id);

	void del(Report report);

	/**
	 * 申请单数量、印章数量、使用次数 数据统计
	 *
	 * @param departmentIds 要查询的组织ID列表
	 * @return
	 */
	Map<String, Integer> getStatistics(Integer orgId, List<Integer> departmentIds);

	/**
	 * 查询地图数据
	 *
	 * @param userInfo     查询用户
	 * @param departmentId 要查询的组织ID
	 * @param province     省
	 * @param city         市
	 * @param start        开始时间
	 * @param end          结束时间
	 * @return
	 */
	Map<String, Object> getMapChart(UserInfo userInfo, Integer departmentId, String province, String city, Date start, Date end);

	/**
	 * 查询柱状图数据
	 * @param userInfo 查询用户
	 * @param departmentId 要查询的组织ID
	 * @param start 开始时间
	 * @param end 结束时间
	 * @return
	 */
	Map<String,Object> getHistogram(UserInfo userInfo, Integer departmentId, Date start, Date end);

	/**
	 * 查询饼状图数据
	 * @param userInfo 查询用户
	 * @param departmentId 要查询的组织ID
	 * @param start 开始时间
	 * @param end 结束时间
	 * @return
	 */
	List<StatusEntity> getErrorOrNormal(UserInfo userInfo, Integer departmentId, Date start, Date end);

	/**
	 * 查询仪表盘数据
	 * @param userInfo 查询用户
	 * @param departmentId 要查询的组织ID
	 * @return
	 */
	Map<String,Integer> getgetTotalByOnline(UserInfo userInfo, Integer departmentId);

	/**
	 * 查询用户的报表记录
	 * @param userInfoId 用户ID
	 * @return
	 */
	List<ReportEntity> getReportEntitiesByUser(Integer userInfoId);
}
