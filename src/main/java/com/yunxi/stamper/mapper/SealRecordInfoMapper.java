package com.yunxi.stamper.mapper;


import com.yunxi.stamper.commons.report.HistogramVo;
import com.yunxi.stamper.commons.report.KeyValue;
import com.yunxi.stamper.commons.report.MapVo;
import com.yunxi.stamper.entity.SealRecordInfo;
import com.yunxi.stamper.entityVo.*;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public interface SealRecordInfoMapper extends MyMapper<SealRecordInfo> {
	/**
	 * 查询申请单使用记录(包含图片)
	 */
	List<SealRecordInfoVoApp> selectVoByApplication(Integer applicationId, Integer orgId);

	/**
	 * 查询该印章在新公司最大次数
	 */
	Integer selectMaxCountBySignetAndOrg(Integer signetId, Integer orgId);

	void updateError(Integer id, Integer error);

	void updateRemark(Integer id, String remark);

	/**
	 * 查询使用记录列表
	 *
	 * @param orgId     集团ID
	 * @param signetId  印章ID
	 * @param identity  用印人名称
	 * @param location  地址
	 * @param infoType  记录类型
	 * @param infoError 记录状态
	 * @param start     开始时间
	 * @param end       结束时间
	 * @return
	 */
	List<SealRecordInfoEntity> selectBySignetId(Integer orgId, Integer signetId, String identity, String location, Integer infoType, Integer infoError, Date start, Date end);


	//查询申请单的使用记录列表
	List<InfoByApplication> selectInfoByApplication(Integer applicationId);

	/**
	 * 查询使用记录总次数
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 组织ID列表
	 * @return
	 */
	Integer selectCountByOrgAndDepartment(Integer orgId, List<Integer> departmentIds);

	/**
	 * 查询各省市区使用记录总数量
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 组织ID列表
	 * @param start         开始时间
	 * @param end           结束时间
	 * @param searchNum     0：全国各省 1：全身各市 2：全市各县区
	 * @return
	 */
	List<KeyValue> searchKvFromOrgAndDepartment(Integer orgId, List<Integer> departmentIds, Date start, Date end, int searchNum, String position);

	/**
	 * 查询各省、市、区县的详细地址使用记录总数量
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 组织ID列表
	 * @param start         开始时间
	 * @param end           结束时间
	 * @param searchNum     0：全国各省 1：全身各市 2：全市各县区
	 * @param position      行政中心地址，省份、市、区县
	 * @return
	 */
	List<MapVo> searchTotalFromOrgAndDepartment(Integer orgId, List<Integer> departmentIds, Date start, Date end, int searchNum, String position);

	/**
	 * 查询各个设备使用记录总次数
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 组织ID列表
	 * @param start         开始时间
	 * @param end           结束时间
	 * @return
	 */
	List<HistogramVo> selectHistogramByOrgAndDepartment(Integer orgId, List<Integer> departmentIds, Date start, Date end);

	/**
	 * 查询组织列表的使用记录 正常、异常、警告数据
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 组织ID列表
	 * @param start         开始时间
	 * @param end           结束时间
	 * @return
	 */
	List<StatusEntity> selectPieChartByOrgAndDepartment(Integer orgId, List<Integer> departmentIds, Date start, Date end);

	/**
	 * 查询用户使用指定印章记录总数量
	 *
	 * @param orgId      集团ID
	 * @param userInfoId 用户ID
	 * @param signetName 印章名称关键词
	 * @return
	 */
	int selectCountByOrgAndUserAndSignetName(Integer orgId, Integer userInfoId, String signetName);

	/**
	 * 查询用户使用记录
	 *
	 * @param orgId      集团ID
	 * @param userId     用户ID
	 * @param start      开始时间
	 * @param end        结束时间
	 * @param signetName 印章名称
	 * @param title      申请单关键词
	 * @param error      记录状态
	 * @param type       记录类型
	 * @param deviceType 印章类型
	 * @return
	 */
	List<InfoEntity> selectInfoListByKeyword(Integer orgId, Integer userId, Date start, Date end, String signetName, String title, Integer error, Integer type, Integer deviceType);

	/**
	 * 查询申请单的使用记录ID列表
	 *
	 * @param applicationId 申请单ID
	 * @return
	 */
	List<Integer> selectIdsByApplication(Integer applicationId);

	/**
	 * 使用记录列表
	 *
	 * @param orgId    集团ID
	 * @param signetId 印章ID
	 * @return
	 */
	List<SealRecordInfoEntity> selectInfoBySignet(Integer orgId, Integer signetId);

	/**
	 * 使用记录列表
	 *
	 * @param orgId      集团ID
	 * @param start      开始时间
	 * @param end        结束时间
	 * @param signetName 关键词：设备名称
	 * @param title      关键词：申请标题
	 * @param error      关键词：记录状态
	 * @param type       关键词：记录类型
	 * @param deviceType 印章类型
	 * @param userId     用印人ID
	 * @return
	 */
	List<InfoEntity> selectInfoList(Integer orgId, Date start, Date end, String signetName, String title, Integer error, Integer type, Integer deviceType, Integer userId);

	/**
	 * 使用记录列表
	 *
	 * @param orgId          集团ID
	 * @param deviceIds      设备ID列表
	 * @param applicationIds
	 * @param start          开始时间
	 * @param end            结束时间
	 * @return
	 */
	List<SealRecordInfo> selectReportList(Integer orgId, List<Integer> deviceIds, List<Integer> applicationIds, Date start, Date end);

	/**
	 * 使用记录列表-->实际查询曾经使用过,但是现在迁移掉的设备id和名称
	 *
	 * @param orgId
	 * @param devicename
	 * @return
	 */
	List<SealRecordInfo> selectUsedIdList(Integer orgId, String devicename);

	/**
	 * 使用记录列表
	 *
	 * @param orgId     集团ID
	 * @param types     参数userIds的类型 0:申请人  1:审批人  2:审计人  3:授权人  4:用印人
	 * @param deviceIds 印章ID列表
	 * @param userIds   人员ID列表
	 * @param start     开始时间
	 * @param end       结束时间
	 * @return
	 */
	List<SealRecordInfo> selectReportList2(Integer orgId, List<Integer> deviceIds, List<Integer> types, List<Integer> userIds, Date start, Date end);

}