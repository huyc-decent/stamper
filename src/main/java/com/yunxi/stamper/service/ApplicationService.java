package com.yunxi.stamper.service;


import com.yunxi.stamper.entity.*;
import com.yunxi.stamper.entityVo.*;

import java.util.Date;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/4 0004 17:42
 */
public interface ApplicationService {
	/**
	 * 申请单信息
	 *
	 * @param id 申请单ID
	 * @return 结果
	 */
	Application get(Integer id);

	/**
	 * 新增申请单
	 *
	 * @param application 申请单信息
	 */
	void add(Application application);

	/**
	 * 新增申请单
	 *
	 * @param title        标题
	 * @param content      内容
	 * @param useCount     次数
	 * @param signet       印章
	 * @param keeper       授权人
	 * @param auditor      审计人
	 * @param nodeEntities 流程节点
	 * @param flow         流程模板
	 * @param fileInfos    附件
	 * @param departmentId 组织ID
	 * @param userInfo     申请人
	 */
	void application(String title, String content, Integer useCount, Signet signet, User keeper, User auditor, List<NodeEntity> nodeEntities, Flow flow, List<FileInfo> fileInfos, Integer departmentId, UserInfo userInfo);


	/**
	 * 更新申请单
	 *
	 * @param application 申请单信息
	 */
	void update(Application application);

	/**
	 * 已审批列表
	 *
	 * @param managerId 审批人ID
	 * @param orgId     集团ID
	 * @return 结果
	 */
	List<ApplicationVoSelect> getByManagerOK(Integer managerId, Integer orgId);

	/**
	 * 属主已审批列表
	 *
	 * @param orgId 集团ID
	 * @return 结果
	 */
	List<ApplicationVoSelect> getByOrgManagerOK(Integer orgId);

	/**
	 * 已授权列表
	 *
	 * @param keeperId 授权人ID
	 * @param orgId    集团ID
	 * @return 结果
	 */
	List<ApplicationVoSelect> getByKeeperOK(Integer keeperId, Integer orgId);

	/**
	 * 属主已授权列表
	 *
	 * @param orgId 集团ID
	 * @return 结果
	 */
	List<ApplicationVoSelect> getByOrgKeeperOK(Integer orgId);

	/**
	 * 已审计列表
	 *
	 * @param auditorId 审计人ID
	 * @param orgId     集团ID
	 * @return 结果
	 */
	List<ApplicationVoSelect> getByAuditorOK(Integer auditorId, Integer orgId);

	/**
	 * 属主已审计列表
	 *
	 * @param orgId 集团ID
	 * @return 结果
	 */
	List<ApplicationVoSelect> getByOrgAuditorOK(Integer orgId);

	/**
	 * 申请单列表
	 *
	 * @param applicationUserId 申请人ID
	 * @return 结果
	 */
	List<Application> getByApplicationUser(Integer applicationUserId);

	/**
	 * 申请单列表
	 *
	 * @param orgId 集团ID
	 * @return 结果
	 */
	List<Application> getByOrg(Integer orgId);

	/**
	 * 申请单信息
	 *
	 * @param applicationId 申请单ID
	 * @return 结果
	 */
	ApplicationVoSelect getById(Integer applicationId);

	/**
	 * 申请单列表
	 *
	 * @param userInfo 用户
	 * @param title    标题
	 * @return 结果
	 */
	List<Application> getByOrgManager(UserInfo userInfo, String title);

	/**
	 * 申请单列表
	 *
	 * @param userInfo 用户
	 * @param title    标题
	 * @return 结果
	 */
	List<Application> getByUser(UserInfo userInfo, String title);

	/**
	 * 申请单数量
	 *
	 * @param userId 用户ID
	 * @return 结果
	 */
	int getCountByPendingApplications(Integer userId);

	/**
	 * 待审批数量
	 *
	 * @param userId 用户ID
	 * @return 结果
	 */
	int getCountByPendingManagers(Integer userId);

	/**
	 * 待授权数量
	 *
	 * @param userId 用户ID
	 * @return 结果
	 */
	int getCountByPendingKeepers(Integer userId);

	/**
	 * 待审计数量
	 *
	 * @param userId 用户ID
	 * @param orgId  集团ID
	 * @return 结果
	 */
	int getCountByPendingAuditors(Integer userId, Integer orgId);

	/**
	 * 申请单列表
	 *
	 * @param search 搜索条件
	 * @return 结果
	 */
	List<ApplicationVo> getByStatusAndUser(ApplicationVoSearch search);

	/**
	 * 近5次申请单列表
	 *
	 * @param applicationUserId 申请人ID
	 * @return 结果
	 */
	List<ApplicationVo> getNearestApplications(Integer applicationUserId);

	/**
	 * 匹配申请单审批人
	 *
	 * @param user        用户
	 * @param application 申请单
	 * @return 结果
	 */
	boolean isManager(User user, Application application);

	/**
	 * 匹配申请单审计人
	 *
	 * @param user        用户
	 * @param application 申请单
	 * @return 结果
	 */
	boolean isAuditor(User user, Application application);

	/**
	 * 匹配申请单授权人
	 *
	 * @param user        用户
	 * @param application 申请单
	 * @return 结果
	 */
	boolean isKeeper(User user, Application application);

	/**
	 * 未完结申请单ID列表
	 *
	 * @param userId 用户ID
	 * @return 结果
	 */
	List<Integer> getApplicationsByUser(Integer userId);

	/**
	 * 查询该申请单的申请人ID、审批人ID、授权人ID、审计人ID列表
	 *
	 * @param applicationId 申请单ID
	 * @return 结果
	 */
	List<Integer> getUserIdsByApplication(Integer applicationId);

	/**
	 * 申请单信息
	 *
	 * @param applicationId 申请单ID
	 * @return 结果
	 */
	ApplicationVoSelect getByMyApplication(Integer applicationId);

	/**
	 * 申请单总数量
	 *
	 * @param orgId   集团ID
	 * @param userIds 用户ID列表
	 * @return 结果
	 */
	int getCountByOrgAndUser(Integer orgId, List<Integer> userIds);

	/**
	 * 待绑定列表
	 *
	 * @param userId    申请人ID
	 * @param startTime 授权结束日期
	 * @param deviceId  设备ID
	 * @return 结果
	 */
	List<ApplicationToBind> getBySealRecordInfoToBind(Integer userId, String startTime, Integer deviceId);

	/**
	 * 申请单总数量
	 *
	 * @param orgId      集团ID
	 * @param userInfoId 用户ID
	 * @param title      申请单标题关键词
	 * @return 结果
	 */
	int getCountByUserAndTitle(Integer orgId, Integer userInfoId, String title);

	/**
	 * 未审批列表
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 用户管理组织ID列表
	 * @param managerId     用户ID
	 * @return 结果
	 */
	List<ApplicationVoSelect> getNotApprovedByOrgAndDepartmentAndManager(Integer orgId, List<Integer> departmentIds, Integer managerId);

	/**
	 * 未授权列表
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 用户管理组织ID列表
	 * @param keeperId      用户ID
	 * @return 结果
	 */
	List<ApplicationVoSelect> getUnAuthorizedByOrgAndDepartmentAndKeeper(Integer orgId, List<Integer> departmentIds, Integer keeperId);

	/**
	 * 未审计列表
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 用户管理组织ID列表
	 * @param auditorId     用户ID
	 * @return 结果
	 */
	List<ApplicationVoSelect> getNotAuditedByOrgAndDepartmentAndAuditor(Integer orgId, List<Integer> departmentIds, Integer auditorId);

	/**
	 * 申请单总数量
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 组织ID列表
	 * @return 结果
	 */
	Integer getCountByDepartment(Integer orgId, List<Integer> departmentIds);

	/**
	 * 设备用章、使用记录长传触发该方法，对应申请单次数减1次，更新申请单状态
	 *
	 * @param applicationId 申请单ID
	 * @param deviceId      设备ID
	 * @param useCount      当前次数
	 */
	void updateApplicationFromsignetToUse(Integer applicationId, Integer deviceId, Integer useCount);

	/**
	 * 同步申请单使用次数
	 *
	 * @param deviceId      设备ID
	 * @param applicationId 申请单ID
	 * @param useCount      已用次数
	 */
	void synchApplicationInfo(Integer deviceId, Integer applicationId, Integer useCount);

	/**
	 * 同步申请单使用次数,解决无网情况下,使用记录次数同步问题
	 *
	 * @param applicationsInfos 申请单json
	 */
	void synchApplicationInfo(String applicationsInfos);

	/**
	 * 待审计申请单列表
	 *
	 * @param userInfo 要查询的目标用户
	 * @return 结果
	 */
	List<ApplicationVoSelect> getNotAuditedApplications(UserInfo userInfo);

	/**
	 * 已审计申请单列表
	 *
	 * @param userInfo 要查询的目标用户
	 * @return 结果
	 */
	List<ApplicationVoSelect> getAuditedApplications(UserInfo userInfo);

	/**
	 * 申请单列表
	 *
	 * @param orgId     集团ID
	 * @param userTypes 使用人类型 0:申请人  1:审批人  2:审计人
	 * @param userIds   用户ID列表
	 * @return 结果
	 */
	List<Integer> getList(Integer orgId, List<Integer> userTypes, List<Integer> userIds);

	/**
	 * 查询申请单列表
	 *
	 * @param start      申请日期
	 * @param end        申请日期
	 * @param statuss    申请单状态
	 * @param managerIds 审批人Id列表
	 * @param keeperIds  管章人Id列表
	 * @param auditorIds 审计人Id列表
	 * @param title      申请单标题
	 * @param content    申请单内容
	 * @param deviceIds  印章Id列表
	 * @param userIds    申请人Id列表
	 * @return 申请单列表
	 */
	List<DtoApplicationForm> applicationReportList(Date start, Date end, List<Integer> statuss, List<Integer> managerIds, List<Integer> keeperIds, List<Integer> auditorIds, String title, String content, List<Integer> deviceIds, List<Integer> userIds,Integer orgId);

}
