package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.other.Global;
import com.yunxi.stamper.sys.context.SpringContextUtils;
import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.commons.report.HistogramVo;
import com.yunxi.stamper.commons.report.KeyValue;
import com.yunxi.stamper.commons.report.MapVo;
import com.yunxi.stamper.entity.*;
import com.yunxi.stamper.entityVo.*;
import com.yunxi.stamper.mapper.SealRecordInfoMapper;
import com.yunxi.stamper.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/3 0003 3:41
 */
@Slf4j
@Service
public class ISealRecordInfoService implements SealRecordInfoService {

	@Autowired
	private SealRecordInfoMapper mapper;
	@Autowired
	private FileInfoService fileInfoService;
	@Autowired
	private ErrorTypeService errorTypeService;
	@Autowired
	private ApplicationService applicationService;
	@Autowired
	private SealRecordInfoService sealRecordInfoService;

	/**
	 * 查询指定申请单用印次数
	 *
	 * @param applicationId
	 * @return
	 */
	@Override
	public int getCountByApplication(Integer applicationId) {
		if (applicationId == null) {
			return 0;
		}
		Example example = new Example(SealRecordInfo.class);
		example.createCriteria().andIsNull("deleteDate")
				.andEqualTo("applicationId", applicationId);
		return mapper.selectCountByExample(example);
	}

	/**
	 * 查询该印章在该公司使用总次数
	 *
	 * @param signetId 印章id
	 * @param orgId    公司id
	 * @return
	 */
	@Override
	public Integer getTotalBySignetAndOrg(Integer signetId, Integer orgId) {
		if (signetId == null || orgId == null) {
			return 0;
		}
		return mapper.selectMaxCountBySignetAndOrg(signetId, orgId);
	}

	/**
	 * 查询申请单使用记录(包含图片)
	 */
	@Override
	public List<SealRecordInfoVoApp> getVoByApplication(Integer applicationId, Integer orgId) {
		if (applicationId == null || orgId == null) {
			return null;
		}
		SpringContextUtils.setPage();
		return mapper.selectVoByApplication(applicationId, orgId);
	}

	@Override
	public SealRecordInfo get(Integer sealRecordInfoId) {
		if (sealRecordInfoId == null) {
			return null;
		}
		Example example = new Example(SealRecordInfo.class);
		example.createCriteria().andEqualTo("id", sealRecordInfoId)
				.andIsNull("deleteDate");
		return mapper.selectOneByExample(example);
	}


	/**
	 * 查询申请单使用记录
	 *
	 * @return
	 */
	@Override
	public List<SealRecordInfo> getByApplication(InfoSearchVo vo) {
		if (vo == null) {
			return null;
		}
		//印章id
		Integer signetId = vo.getSignetId();
		Date start = vo.getStart();
		Date end = vo.getEnd();
		Integer useCount = vo.getUseCount();
		Integer error = vo.getError();
		String location = vo.getLocation();
		String userName = vo.getUserName();
		Integer type = vo.getType();
		UserInfo userInfo = vo.getUserInfo();
		Integer level = userInfo.getType();
		Integer applicationId = vo.getApplicationId();

		Example example = new Example(SealRecordInfo.class);
		example.createCriteria().andIsNull("deleteDate")
				.andEqualTo("applicationId", applicationId);

		if (level.intValue() != 0) {
			//非平台级用户只能查询本公司使用记录
			Example.Criteria criteria = example.createCriteria().andEqualTo("orgId", userInfo.getOrgId());
			example.and(criteria);
		}

		if (start != null) {
			Example.Criteria criteria = example.createCriteria().andGreaterThanOrEqualTo("realTime", start);
			example.and(criteria);
		}

		if (end != null) {
			Example.Criteria criteria = example.createCriteria().andLessThanOrEqualTo("realTime", end);
			example.and(criteria);
		}

		if (useCount != null) {
			Example.Criteria criteria = example.createCriteria().andEqualTo("useCount", useCount);
			example.and(criteria);
		}

		if (error != null) {
			Example.Criteria criteria = example.createCriteria().andEqualTo("error", error);
			example.and(criteria);
		}

		if (type != null) {
			Example.Criteria criteria = example.createCriteria().andEqualTo("type", type);
			example.and(criteria);
		}

		if (StringUtils.isNotBlank(userName)) {
			Example.Criteria criteria = example.createCriteria().andLike("userName", "%" + userName + "%");
			example.and(criteria);
		}

		if (StringUtils.isNotBlank(location)) {
			Example.Criteria criteria = example.createCriteria().andLike("location", "%" + location + "%");
			example.and(criteria);
		}
		example.orderBy("useCount").desc();

		List<SealRecordInfo> res = mapper.selectByExample(example);
		return res;
	}

	@Override
	@Transactional
	public void updateError(Integer id, Integer error) {
		if (id != null) {
			mapper.updateError(id, error);
		}
	}

	@Override
	@Transactional
	public void updateRemark(Integer infoId, String remark) {
		if (infoId == null) {
			return;
		}
		mapper.updateRemark(infoId,remark);
	}

	@Override
	@Transactional
	public void update(SealRecordInfo info) {
		int updateCount = 0;
		if (info != null && info.getId() != null) {
			info.setUpdateDate(new Date());
			Integer applicationId = info.getApplicationId();
			if (applicationId == null || applicationId == 0) {
				info.setApplicationId(null);
			}
			updateCount = mapper.updateByPrimaryKey(info);
		}
		if (updateCount != 1) {
			//TODO:更新失败
			throw new PrintException("使用记录更新失败");
		}
	}

	@Override
	@Transactional
	public void add(SealRecordInfo info) {
		info.setCreateDate(new Date());
		int insert = mapper.insert(info);
		if (insert != 1) {
			throw new PrintException("使用记录添加失败");
		}
	}

	/**
	 * 查询是否有相同的使用记录
	 *
	 * @param signetId 设备id
	 * @param useCount 使用次数
	 * @param orgId    印章所属公司Id
	 * @return
	 */
	@Override
	public SealRecordInfo get(Integer signetId, Integer useCount, Integer orgId) {
		if (signetId == null || useCount == null) {
			return null;
		}
		Example example = new Example(SealRecordInfo.class);
		example.createCriteria()
				.andIsNull("deleteDate")
				.andEqualTo("deviceId", signetId)
				.andEqualTo("orgId", orgId)
				.andEqualTo("useCount", useCount);
		List<SealRecordInfo> infos = mapper.selectByExample(example);
		if (infos == null || infos.isEmpty()) {
			return null;
		}
		return infos.get(0);
	}

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
	@Override
	public List<SealRecordInfoEntity> getBySignetId(Integer orgId, Integer signetId, String identity, String location, Integer infoType, Integer infoError, Date start, Date end) {
		if (orgId == null || signetId == null) {
			return null;
		}

		SpringContextUtils.setPage();

		List<SealRecordInfoEntity> sealRecordInfoEntities = mapper.selectBySignetId(orgId, signetId, identity, location, infoType, infoError, start, end);
		if (sealRecordInfoEntities == null || sealRecordInfoEntities.isEmpty()) {
			return null;
		}

		for (int i = 0; i < sealRecordInfoEntities.size(); i++) {
			SealRecordInfoEntity sealRecordInfoEntity = sealRecordInfoEntities.get(i);
			Integer id = sealRecordInfoEntity.getId();
			String errorInfo = errorTypeService.getBySealRecordInfoId(id);
			sealRecordInfoEntity.setErrorMsg(errorInfo);
		}
		return sealRecordInfoEntities;
	}

	/**
	 * 查询申请单的使用记录列表
	 *
	 * @param applicationId 要查询的申请单ID
	 * @return
	 */
	@Override
	public List<InfoByApplication> getInfoByApplication(Integer applicationId) {
		if (applicationId == null) {
			return null;
		}

		/***查询数据库*/
		List<InfoByApplication> infoByApplications = mapper.selectInfoByApplication(applicationId);
		if (infoByApplications == null || infoByApplications.isEmpty()) {
			return null;
		}

		for (int i = 0; i < infoByApplications.size(); i++) {
			InfoByApplication info = infoByApplications.get(i);

			/***根据图片类型不同，组装对应图片URL*/
			String fileTypes = info.getFileTypes();
			if (StringUtils.isBlank(fileTypes)) {
				continue;
			}

			List<Integer> types = CommonUtils.splitToInteger(fileTypes, ",");
			if (types != null && types.size() > 0) {

				/***图片ID列表*/
				String fileIdsArr = info.getFileIds();
				if (StringUtils.isBlank(fileIdsArr)) {
					continue;
				}

				String[] fileIds = null;
				try {
					fileIds = fileIdsArr.split(",");
				} catch (Exception e) {
					log.error("出现异常 fileIdsArr:{}", fileIdsArr, e);
					continue;
				}

				for (int j = 0; j < types.size(); j++) {

					Integer type = types.get(j);

					//图片ID
					String fileId = null;
					try {
						fileId = fileIds[j];
					} catch (Exception e) {
						log.error("出现异常 ", e);
					}

					if (StringUtils.isNotBlank(fileId)) {
						FileEntity entity = fileInfoService.getReduceImgURLByFileId(fileId);
						addToInfoList(info, entity, type);
					}
				}
			}

			info.setFileCreates(null);
			info.setFileIds(null);
			info.setFileTypes(null);
			info.setFileNames(null);
		}
		return infoByApplications;
	}

	/**
	 * @param infoByApplication
	 * @param entity
	 * @param type              0:使用记录图片 1:审计图片 2:超出申请单次数图片 3:长按报警图片 4:拆卸警告图片
	 */
	private void addToInfoList(InfoByApplication infoByApplication, FileEntity entity, Integer type) {
		if (entity == null) {
			return;
		}
		if (type == 0) {
			//使用记录图片
			infoByApplication.getInfoList().add(entity);
		} else if (type == 1) {
			//审计图片
			infoByApplication.getAuditorList().add(entity);
		} else if (type == 2) {
			//超出次数
			infoByApplication.getExcessTimesList().add(entity);
		} else if (type == 3) {
			//警告图片
			infoByApplication.getOverTimeList().add(entity);
		} else if (type == Global.TYPE_REPLENISH) {
			//追加图片
			infoByApplication.getReplenishList().add(entity);
		}
	}

	/**
	 * 查询使用记录总次数
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 组织ID列表
	 * @return
	 */
	@Override
	public int getCountByOrgAndDepartments(Integer orgId, List<Integer> departmentIds) {
		if (orgId == null) {
			return 0;
		}
		return mapper.selectCountByOrgAndDepartment(orgId, departmentIds);
	}

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
	@Override
	public List<KeyValue> searchKvFromOrgAndDepartment(@NotNull Integer orgId, List<Integer> departmentIds, Date start, Date end, int searchNum, String position) {
		if (orgId == null) {
			return null;
		}
		return mapper.searchKvFromOrgAndDepartment(orgId, departmentIds, start, end, searchNum, position);
	}

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
	@Override
	public List<MapVo> searchTotalFromOrgAndDepartment(@NotNull Integer orgId, List<Integer> departmentIds, Date start, Date end, int searchNum, String position) {
		if (orgId == null) {
			return null;
		}
		return mapper.searchTotalFromOrgAndDepartment(orgId, departmentIds, start, end, searchNum, position);
	}

	/**
	 * 查询各个设备使用记录总次数
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 组织ID列表
	 * @param start         开始时间
	 * @param end           结束时间
	 * @return
	 */
	@Override
	public List<HistogramVo> getHistogramByOrgAndDepartment(Integer orgId, List<Integer> departmentIds, Date start, Date end) {
		if (orgId == null) {
			return null;
		}
		return mapper.selectHistogramByOrgAndDepartment(orgId, departmentIds, start, end);
	}

	/**
	 * 查询组织列表的使用记录 正常、异常、警告数据
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 组织ID列表
	 * @param start         开始时间
	 * @param end           结束时间
	 * @return
	 */
	@Override
	public List<StatusEntity> getPieChartByOrgAndDepartment(@NotNull Integer orgId, List<Integer> departmentIds, Date start, Date end) {
		if (orgId == null) {
			return null;
		}
		return mapper.selectPieChartByOrgAndDepartment(orgId, departmentIds, start, end);
	}

	/**
	 * 绑定申请单
	 *
	 * @param userInfo       操作人
	 * @param sealRecordInfo 使用记录
	 * @param application    申请单
	 */
	@Override
	@Transactional
	public void banding(UserInfo userInfo, SealRecordInfo sealRecordInfo, Application application) {
		/**
		 * 更新：使用记录异常'无申请单号'
		 */
		List<ErrorType> ets = errorTypeService.getBySealRecordInfo(sealRecordInfo.getId());
		if (ets != null && ets.size() > 0) {
			for (int i = 0; i < ets.size(); i++) {
				ErrorType errorType = ets.get(i);
				String errorName = errorType.getName();
				if (Global.ERROR04.equalsIgnoreCase(errorName)) {
					errorTypeService.del(errorType);
					ets.remove(i);
					break;
				}
			}
		}

		/**
		 * 更新：使用记录属性ApplicationID、error
		 */
		if (ets == null || ets.isEmpty()) {
			sealRecordInfo.setError(0);
		}
		sealRecordInfo.setApplicationId(application.getId());
		update(sealRecordInfo);

		/**
		 * 通知申请单系统
		 */
		Integer applicationId = application.getId();
		Integer deviceId = sealRecordInfo.getDeviceId();
		if (applicationId != null && applicationId != 0) {
			int localCount = sealRecordInfoService.getCountByApplication(applicationId);
			applicationService.synchApplicationInfo(deviceId, applicationId, localCount);
		}
	}

	/**
	 * 查询用户使用指定印章记录总数量
	 *
	 * @param orgId      集团ID
	 * @param userInfoId 用户ID
	 * @param signetName 印章名称关键词
	 * @return
	 */
	@Override
	public int getCountByUserAndSignetName(Integer orgId, Integer userInfoId, String signetName) {
		if (orgId == null || userInfoId == null) {
			return 0;
		}
		return mapper.selectCountByOrgAndUserAndSignetName(orgId, userInfoId, signetName);
	}

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
	@Override
	public List<InfoEntity> searchInfoListByKeyword(Integer orgId, Integer userId, Date start, Date end, String signetName, String title, Integer error, Integer type, Integer deviceType) {
		if (orgId == null) {
			return null;
		}
		return mapper.selectInfoListByKeyword(orgId, userId, start, end, signetName, title, error, type, deviceType);
	}

	/**
	 * 使用记录列表
	 *
	 * @param userInfo   查询用户
	 * @param start      开始时间
	 * @param end        结束时间
	 * @param signetName 关键词：设备名称
	 * @param title      关键词：申请标题
	 * @param error      关键词：记录状态
	 * @param type       关键词：记录类型
	 * @param deviceType 印章类型
	 * @return
	 */
	@Override
	public List<InfoEntity> searchInfoList(UserInfo userInfo, Date start, Date end, String signetName, String title, Integer error, Integer type, Integer deviceType) {
		if (userInfo == null) {
			return null;
		}

		Integer orgId = userInfo.getOrgId();
		Integer userId = userInfo.getId();
		boolean owner = userInfo.isOwner();

		List<InfoEntity> infoEntities = mapper.selectInfoList(orgId, start, end, signetName, title, error, type, deviceType, owner ? null : userId);

		return infoEntities;
	}

	/**
	 * 查询申请单的使用记录ID列表
	 *
	 * @param applicationId 申请单ID
	 * @return
	 */
	@Override
	public List<Integer> getIdsByApplication(Integer applicationId) {
		if (applicationId == null) {
			return null;
		}
		return mapper.selectIdsByApplication(applicationId);
	}

	/**
	 * 使用记录列表
	 *
	 * @param orgId    组织ID
	 * @param signetId 印章ID
	 * @return
	 */
	@Override
	public List<SealRecordInfoEntity> getInfoBySignet(Integer orgId, Integer signetId) {
		if (orgId == null || signetId == null) {
			return null;
		}
		SpringContextUtils.setPage();
		List<SealRecordInfoEntity> sealRecordInfos = mapper.selectInfoBySignet(orgId, signetId);
		return sealRecordInfos;
	}

	/**
	 * 使用记录列表
	 *
	 * @param isOwner true:属主  false:非属主
	 * @param orgId   集团ID
	 * @param userId  用印人ID
	 * @param start   开始时间
	 * @param end     结束时间
	 * @return
	 */
	@Override
	public List<SealRecordInfo> getReportList(boolean isOwner, Integer orgId, Integer userId, String start, String end) {
		Example example = new Example(SealRecordInfo.class);
		example.createCriteria().andLessThanOrEqualTo("realTime", end)
				.andGreaterThanOrEqualTo("realTime", start).andEqualTo("orgId", orgId);
		if (!isOwner) {
			Example.Criteria criteria = example.createCriteria().andEqualTo("userId", userId);
			example.and(criteria);
		}
		example.orderBy("realTime").orderBy("useCount");
		List<SealRecordInfo> sealRecordInfos = mapper.selectByExample(example);
		return sealRecordInfos;
	}

	/**
	 * 使用记录列表
	 *
	 * @param orgId          集团ID
	 * @param deviceIds      设备ID列表
	 * @param applicationIds 申请单ID列表
	 * @param start          开始时间
	 * @param end            结束时间
	 * @return
	 */
	@Override
	public List<SealRecordInfo> getReportList(Integer orgId, List<Integer> deviceIds, List<Integer> applicationIds, Date start, Date end) {
		if (orgId == null) {
			return null;
		}
		return mapper.selectReportList(orgId, deviceIds, applicationIds, start, end);
	}

	/**
	 * 使用记录列表-->实际查询曾经使用过,但是现在迁移掉的设备id和名称
	 *
	 * @param orgId
	 * @param devicename
	 * @return
	 */
	@Override
	public List<SealRecordInfo> getUsedIdList(Integer orgId, String devicename) {
		if (orgId == null) {
			return null;
		}
		return mapper.selectUsedIdList(orgId, devicename);
	}

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
	@Override
	public List<SealRecordInfo> getReportList2(Integer orgId, List<Integer> types, List<Integer> deviceIds, List<Integer> userIds, Date start, Date end) {
		if (orgId == null) {
			return null;
		}
		return mapper.selectReportList2(orgId, deviceIds, types, userIds, start, end);
	}
}
