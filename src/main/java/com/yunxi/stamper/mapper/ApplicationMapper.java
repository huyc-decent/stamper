package com.yunxi.stamper.mapper;


import com.yunxi.stamper.entity.Application;
import com.yunxi.stamper.entityVo.*;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public interface ApplicationMapper extends MyMapper<Application> {
	/**
	 * 已审批列表
	 *
	 * @param managerId 审批人ID
	 * @param orgId     集团ID
	 * @return
	 */
	List<ApplicationVoSelect> selectByManagerOK(Integer managerId, Integer orgId);

	/**
	 * 属主已审批列表
	 *
	 * @param orgId 集团ID
	 * @return
	 */
	List<ApplicationVoSelect> selectByManagerOkAndOrg(Integer orgId);

	/**
	 * 已授权列表
	 *
	 * @param keeperId 管章人ID
	 * @param orgId    集团ID
	 * @return
	 */
	List<ApplicationVoSelect> selectByKeeperOK(Integer keeperId, Integer orgId);

	/**
	 * 属主已授权列表
	 *
	 * @param orgId 集团ID
	 * @return
	 */
	List<ApplicationVoSelect> selectByKeeperOKAndOrg(Integer orgId);

	/**
	 * 已审计列表
	 *
	 * @param auditorId 审计人ID
	 * @param orgId     集团ID
	 * @return
	 */
	List<ApplicationVoSelect> selectByAuditorOK(Integer auditorId, Integer orgId);

	/**
	 * 属主已审计列表
	 *
	 * @param orgId 集团ID
	 * @return
	 */
	List<ApplicationVoSelect> selectByAuditorOKAndOrg(Integer orgId);

	/**
	 * 申请单信息
	 *
	 * @param applicationId
	 * @return
	 */
	ApplicationVoSelect selectById(Integer applicationId);

	/**
	 * 申请单数量
	 *
	 * @param userId 用户ID
	 * @return
	 */
	int selectCountByPendingApplications(Integer userId);

	/**
	 * 待审批数量
	 *
	 * @param userId 用户ID
	 * @return
	 */
	int selectCountByPendingManagers(Integer userId);

	/**
	 * 待审计数量
	 *
	 * @param userId 用户ID
	 * @param orgId  集团ID
	 * @return
	 */
	int selectCountByPendingAuditors(Integer userId, Integer orgId);

	/**
	 * 待授权数量
	 *
	 * @param userId 用户ID
	 * @return
	 */
	int selectCountByPendingKeepers(Integer userId);

	/**
	 * 申请单列表
	 *
	 * @param search
	 * @return
	 */
	List<ApplicationVo> selectByStatusAndUser(ApplicationVoSearch search);

	/**
	 * 近5次申请单列表
	 *
	 * @param applicationUserId 申请人ID
	 * @return
	 */
	List<ApplicationVo> getNearestApplications(Integer applicationUserId);

	/**
	 * 未完结申请单列表
	 *
	 * @param userId
	 * @return
	 */
	List<Integer> selectApplicationsByUser(Integer userId);

	/**
	 * 申请单总数量
	 *
	 * @param orgId   集团ID
	 * @param userIds 用户ID列表
	 * @return
	 */
	Integer selectCountByOrgAndUser(Integer orgId, List<Integer> userIds);

	/**
	 * 待绑定列表
	 *
	 * @param userId    申请人ID
	 * @param startTime 授权结束日期
	 * @param deviceId  设备ID
	 * @return
	 */
	List<ApplicationToBind> selectBySealRecordInfoToBind(Integer userId, String startTime, Integer deviceId);

	/**
	 * 申请单总数量
	 *
	 * @param orgId      集团ID
	 * @param userInfoId 用户ID
	 * @param title      申请单标题关键词
	 * @return
	 */
	int selectCountByOrgAndUserAndTitle(Integer orgId, Integer userInfoId, String title);

	/**
	 * 未审批列表
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 用户管理组织ID列表
	 * @param managerId     用户ID
	 * @return
	 */
	List<ApplicationVoSelect> selectNotApprovedByOrgAndDepartmentAndManager(Integer orgId, List<Integer> departmentIds, Integer managerId);

	/**
	 * 未授权列表
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 用户管理组织ID列表
	 * @param keeperId      用户ID
	 * @return
	 */
	List<ApplicationVoSelect> selectUnAuthorizedByOrgAndDepartmentAndKeeper(Integer orgId, List<Integer> departmentIds, Integer keeperId);


	/**
	 * 未审计列表
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 用户管理组织ID列表
	 * @param auditorId     用户ID
	 * @return
	 */
	List<ApplicationVoSelect> selectNotAuditedByOrgAndDepartmentAndAuditor(Integer orgId, List<Integer> departmentIds, Integer auditorId);

	/**
	 * 申请单总数量
	 *
	 * @param orgId 集团ID
	 * @return
	 */
	Integer selectCountByOrg(Integer orgId);

	/**
	 * 未审计申请单列表
	 *
	 * @param orgId  集团ID
	 * @param userId 审计人ID
	 * @param flag   0:属主  1:非属主
	 * @return
	 */
	List<ApplicationVoSelect> getNotAuditedApplications(Integer orgId, Integer userId, Integer flag);

	/**
	 * 已审计申请单列表
	 *
	 * @param orgId  集团ID
	 * @param userId 审计人ID
	 * @param flag   0:属主  1:非属主
	 * @return
	 */
	List<ApplicationVoSelect> selectAuditedApplications(Integer orgId, Integer userId, Integer flag);

	/**
	 * 申请单ID列表
	 *
	 * @param orgId     集团ID
	 * @param userTypes 使用人类型 0:申请人  1:审批人  2:审计人
	 * @param userIds   用户ID列表
	 * @return
	 */
	List<Integer> selectList(Integer orgId, List<Integer> userTypes, List<Integer> userIds);

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
	 * @return 申请单列表
	 */
	List<DtoApplicationForm> applicationReportList(Date start, Date end, List<Integer> statuss, List<Integer> managerIds, List<Integer> keeperIds, List<Integer> auditorIds, String title, String content, List<Integer> deviceIds , List<Integer> userIds, Integer orgId);
}