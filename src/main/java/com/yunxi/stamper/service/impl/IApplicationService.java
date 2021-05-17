package com.yunxi.stamper.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yunxi.stamper.commons.device.modelVo.LoginApplication;
import com.yunxi.stamper.commons.md5.MD5;
import com.yunxi.stamper.commons.other.*;
import com.yunxi.stamper.logger.threadLocal.LocalHandle;
import com.yunxi.stamper.sys.context.SpringContextUtils;
import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.*;
import com.yunxi.stamper.entityVo.*;
import com.yunxi.stamper.mapper.ApplicationMapper;
import com.yunxi.stamper.service.*;
import com.zengtengpeng.annotation.Lock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.util.*;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/4 0004 17:42
 */
@Slf4j
@Service
public class IApplicationService implements ApplicationService {

	@Autowired
	private ApplicationMapper mapper;
	@Autowired
	private UserService userService;
	@Autowired
	private OrgService orgService;
	@Autowired
	private MessageTempService messageTempService;
	@Autowired
	private ApplicationNodeService applicationNodeService;
	@Autowired
	private ApplicationManagerService applicationManagerService;
	@Autowired
	private ApplicationKeeperService applicationKeeperService;
	@Autowired
	private ApplicationAuditorService applicationAuditorService;
	@Autowired
	private ApplicationDeviceService applicationDeviceService;
	@Autowired
	private ThresholdService thresholdService;
	@Autowired
	private SignetService signetService;
	@Autowired
	private AttachmentService attachmentService;
	@Autowired
	private FileInfoService fileInfoService;
	@Autowired
	private RedisUtil redisUtil;

	/**
	 * 申请单信息
	 *
	 * @param id 申请单ID
	 * @return 结果
	 */
	@Override
	public Application get(Integer id) {
		if (id == null) {
			return null;
		}
		return mapper.selectByPrimaryKey(id);
	}

	/**
	 * 新增申请单
	 *
	 * @param application 申请单信息
	 */
	@Override
	@Transactional
	public void add(Application application) {
		int addCount = 0;
		if (application != null) {
			application.setCreateDate(new Date());
			addCount = mapper.insert(application);
		}

		if (addCount != 1) {
			throw new PrintException("申请单创建失败");
		}
	}

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
	@Override
	@Transactional
	public void application(String title, String content, Integer useCount, Signet signet, User keeper, User auditor, List<NodeEntity> nodeEntities, Flow flow, List<FileInfo> fileInfos, Integer departmentId, UserInfo userInfo) {
		//初始化申请单
		Application application = new Application();
		application.setTitle(title);
		application.setContent(content);
		application.setUserCount(useCount);
		application.setUserId(userInfo.getId());
		application.setUserName(userInfo.getUserName());
		application.setOrgId(userInfo.getOrgId());
		application.setDepartmentId(departmentId);
		application.setStatus(Global.APP_INIT);
		if (flow != null) {
			application.setProcessId(flow.getId());
		}
		add(application);

		//创建申请单-设备关联信息
		ApplicationDevice ad = new ApplicationDevice();
		ad.setApplicationId(application.getId());
		ad.setDeviceId(signet.getId());
		ad.setDeviceName(signet.getName());
		ad.setAlreadyCount(0);
		ad.setUserCount(application.getUserCount());
		ad.setKeeperId(signet.getKeeperId());
		ad.setKeeperName(signet.getKeeperName());
		ad.setAuditorId(signet.getAuditorId());
		ad.setAuditorName(signet.getAuditorName());
		ad.setOrgId(signet.getOrgId());
		applicationDeviceService.add(ad);

		//创建附件列表
		for (int i = 0; i < fileInfos.size(); i++) {
			FileInfo fileInfo = fileInfos.get(i);
			String id = fileInfo.getId();
			String originalName = fileInfo.getOriginalName();
			Attachment attachment = new Attachment();
			attachment.setFileId(id);
			attachment.setFileName(originalName);
			attachment.setApplicationId(application.getId());
			attachment.setScaling(fileInfo.getScaling());
			attachment.setStatus(fileInfo.getStatus());
			//检查附件是否是 word2007 如果是，则添加校验码
			boolean word2007 = WordUtil.isWord2007(fileInfo.getOriginalName());
			if (word2007) {
				try {
					//生成查验码
					String verfyCode = DateUtil.format(new Date()).replace("-", "").replace(":", "").replace(" ", "")
							+ "-" + UUID.randomUUID().toString().replace("-", "").toUpperCase()
							+ (i > 9 ? "-0" + (i + 1) : "-00" + (i + 1));
					WordUtil.addHeader(new File(fileInfo.getAbsolutePath()), verfyCode);

					//更新文件附件信息
					attachment.setVerifCode(verfyCode);

					//更新文件HASH
					String hash = MD5.md5HashCode(fileInfo.getAbsolutePath());
					fileInfo.setHash(hash);
					fileInfoService.update(fileInfo);
				} catch (Exception e) {
					log.error("出现异常 ", e);
				}
			}
			attachmentService.add(attachment);
		}

		int orderNo = 0;

		//创建初始化节点
		ApplicationNode initNode = new ApplicationNode();
		initNode.setApplicationId(application.getId());
		initNode.setHandle(Global.HANDLE_COMPLETE);
		initNode.setTitle("提交申请");
		initNode.setManagerIds(userInfo.getId() + "");
		initNode.setName("申请");
		initNode.setOrderNo(orderNo++);
		initNode.setNodeType(Global.FLOW_INIT);
		initNode.setIcon(Global.ICON_INIT);
		initNode.setUpdateDate(new Date());
		applicationNodeService.add(initNode);

		//创建审批节点
		for (NodeEntity nodeEntity : nodeEntities) {
			String type = nodeEntity.getType();

			if (type.equalsIgnoreCase(Global.FLOW_LIST)
					|| type.equalsIgnoreCase(Global.FLOW_AND)
					|| type.equalsIgnoreCase(Global.FLOW_OR)
					|| type.equalsIgnoreCase(Global.FLOW_OPTIONAL)) {

				List<User> managers = nodeEntity.getManagers();
				ApplicationNode managerNode = new ApplicationNode();
				managerNode.setApplicationId(application.getId());
				managerNode.setHandle(Global.HANDLE_COMPLETE_NO);
				managerNode.setTitle("审批员");
				managerNode.setName("审批");
				managerNode.setOrderNo(orderNo++);
				managerNode.setNodeType(type);
				managerNode.setIcon(Global.ICON_WAIT);
				managerNode.setUpdateDate(new Date());

				StringBuilder managerIds = new StringBuilder();
				for (int j = 0; j < managers.size(); j++) {
					User manager = managers.get(j);
					if (j == managers.size() - 1) {
						managerIds.append(manager.getId());
					} else {
						managerIds.append(manager.getId()).append(",");
					}
				}
				managerNode.setManagerIds(managerIds.toString());
				applicationNodeService.add(managerNode);
			} else if (type.equalsIgnoreCase(Global.FLOW_MANAGER)) {
				int level = nodeEntity.getLevel();
				ApplicationNode managerNode = new ApplicationNode();
				managerNode.setApplicationId(application.getId());
				managerNode.setHandle(Global.HANDLE_COMPLETE_NO);//未处理
				managerNode.setTitle("审批人");
				managerNode.setName("审批");
				managerNode.setOrderNo(orderNo++);
				managerNode.setNodeType(type);
				managerNode.setManagerLevel(level);
				managerNode.setIcon(Global.ICON_WAIT);
				applicationNodeService.add(managerNode);

			} else {
				throw new PrintException("审批流程节点类型有误");
			}
		}

		//创建授权节点
		ApplicationNode keeperNode = new ApplicationNode();
		keeperNode.setApplicationId(application.getId());
		keeperNode.setHandle(Global.HANDLE_COMPLETE_NO);//未处理
		keeperNode.setTitle("印章管理员");
		keeperNode.setManagerIds(keeper.getId() + "");
		keeperNode.setName("授权");
		keeperNode.setOrderNo(orderNo++);
		keeperNode.setNodeType(Global.FLOW_LIST);
		keeperNode.setIcon(Global.ICON_WAIT);
		applicationNodeService.add(keeperNode);

		//创建审计节点
		ApplicationNode auditorNode = new ApplicationNode();
		auditorNode.setApplicationId(application.getId());
		auditorNode.setHandle(Global.HANDLE_COMPLETE_NO);//未处理
		auditorNode.setTitle("印章审计员");
		auditorNode.setManagerIds(auditor.getId() + "");
		auditorNode.setName("审计");
		auditorNode.setOrderNo(orderNo);
		auditorNode.setNodeType(Global.FLOW_LIST);
		auditorNode.setIcon(Global.ICON_WAIT);
		applicationNodeService.add(auditorNode);

		//开始第一次执行申请单 处理流程(从orderNo=1 的节点开始处理)
		ApplicationNode node = applicationNodeService.getByApplicationAndOrderNo(application.getId(), 1);
		applicationManagerService.createByNode(node);

		LocalHandle.setNewObj(application);
		LocalHandle.complete("新增申请单");
	}


	/**
	 * 更新申请单
	 *
	 * @param application 申请单信息
	 */
	@Override
	@Transactional
	public void update(Application application) {
		int updateCount = 0;
		if (application != null && application.getId() != null) {
			application.setUpdateDate(new Date());
			updateCount = mapper.updateByPrimaryKey(application);
		}
		if (updateCount != 1) {
			throw new PrintException("申请单更新失败");
		}
	}

	/**
	 * 已审批列表
	 *
	 * @param managerId 审批人ID
	 * @param orgId     集团ID
	 * @return 结果
	 */
	@Override
	public List<ApplicationVoSelect> getByManagerOK(Integer managerId, Integer orgId) {
		if (managerId == null) {
			return null;
		}
		return mapper.selectByManagerOK(managerId, orgId);
	}

	/**
	 * 查询已审计列表(公司管理员)
	 */
	@Override
	public List<ApplicationVoSelect> getByOrgAuditorOK(Integer orgId) {
		if (orgId == null) {
			return null;
		}
		return mapper.selectByAuditorOKAndOrg(orgId);
	}

	/**
	 * 已审计列表
	 *
	 * @param auditorId 审计人ID
	 * @param orgId     集团ID
	 * @return 结果
	 */
	@Override
	public List<ApplicationVoSelect> getByAuditorOK(Integer auditorId, Integer orgId) {
		if (auditorId == null) {
			return null;
		}
		return mapper.selectByAuditorOK(auditorId, orgId);
	}


	/**
	 * 查询用户待授权申请单数量
	 */
	@Override
	public int getCountByPendingKeepers(Integer userId) {
		if (userId == null) {
			return 0;
		}
		return mapper.selectCountByPendingKeepers(userId);
	}

	/**
	 * App端获取用户的最近5次的申请单记录
	 *
	 * @param applicationUserId 申请人ID
	 * @return 结果
	 */
	@Override
	public List<ApplicationVo> getNearestApplications(Integer applicationUserId) {
		if (applicationUserId == null) {
			return null;
		}
		return mapper.getNearestApplications(applicationUserId);
	}

	/**
	 * 查询待完结/已完结申请单
	 */
	@Override
	public List<ApplicationVo> getByStatusAndUser(ApplicationVoSearch search) {
		if (search == null) {
			return null;
		}
		return mapper.selectByStatusAndUser(search);
	}

	/**
	 * 查询用户待审计申请单数量
	 */
	@Override
	public int getCountByPendingAuditors(Integer userId, Integer orgId) {
		if (userId == null) {
			return 0;
		}
		return mapper.selectCountByPendingAuditors(userId, orgId);
	}

	/**
	 * 查询用户待审批申请单数量
	 */
	@Override
	public int getCountByPendingManagers(Integer userId) {
		if (userId == null) {
			return 0;
		}
		return mapper.selectCountByPendingManagers(userId);
	}

	/**
	 * 查询我的申请单数量
	 */
	@Override
	public int getCountByPendingApplications(Integer userId) {
		if (userId == null) {
			return 0;
		}
		return mapper.selectCountByPendingApplications(userId);
	}

	/**
	 * 模糊查询自己的申请单
	 */
	@Override
	public List<Application> getByUser(UserInfo userInfo, String title) {
		Integer orgId = userInfo.getOrgId();
		Integer id = userInfo.getId();
		Example example = new Example(Application.class);
		Example.Criteria criteria = example.createCriteria()
				.andIsNull("deleteDate")
				.andEqualTo("orgId", orgId)
				.andEqualTo("userId", id);
		if (StringUtils.isNotBlank(title)) {
			criteria.andLike("title", '%' + title + '%');
		}
		return mapper.selectByExample(example);
	}

	/**
	 * 模糊查询公司下申请单
	 *
	 * @param userInfo 用户信息
	 * @param title    标题
	 * @return 结果
	 */
	@Override
	public List<Application> getByOrgManager(UserInfo userInfo, String title) {
		Integer orgId = userInfo.getOrgId();

		Example example = new Example(Application.class);
		Example.Criteria criteria = example.createCriteria().andIsNull("deleteDate")
				.andEqualTo("orgId", orgId);
		if (StringUtils.isNotBlank(title)) {
			criteria.andLike("title", '%' + title + '%');
		}
		return mapper.selectByExample(example);
	}

	/**
	 * 查询申请单
	 *
	 * @param applicationId 申请单ID
	 * @return 结果
	 */
	@Override
	public ApplicationVoSelect getById(Integer applicationId) {
		if (applicationId == null) {
			return null;
		}
		return mapper.selectById(applicationId);
	}

	/**
	 * 查询公司申请单集合列表
	 *
	 * @param orgId 组织ID
	 * @return 结果
	 */
	@Override
	public List<Application> getByOrg(Integer orgId) {
		if (orgId == null) {
			return null;
		}
		Example example = new Example(Application.class);
		example.createCriteria().andEqualTo("orgId", orgId)
				.andIsNull("deleteDate");
		example.orderBy("createDate").desc();
		return mapper.selectByExample(example);
	}

	/**
	 * 查询申请人的申请单列表
	 *
	 * @param applicationUserId 申请单ID
	 * @return 结果
	 */
	@Override
	public List<Application> getByApplicationUser(Integer applicationUserId) {
		if (applicationUserId == null) {
			return null;
		}
		Example example = new Example(Application.class);
		example.createCriteria().andEqualTo("userId", applicationUserId)
				.andIsNull("deleteDate");
		example.orderBy("createDate").desc();
		return mapper.selectByExample(example);
	}

	/**
	 * 属主已授权列表
	 *
	 * @param orgId 组织ID
	 * @return 结果
	 */
	@Override
	public List<ApplicationVoSelect> getByOrgKeeperOK(Integer orgId) {
		if (orgId == null) {
			return null;
		}
		return mapper.selectByKeeperOKAndOrg(orgId);
	}

	/**
	 * 已授权列表
	 *
	 * @param keeperId 授权人ID
	 * @param orgId    集团ID
	 * @return 结果
	 */
	@Override
	public List<ApplicationVoSelect> getByKeeperOK(Integer keeperId, Integer orgId) {
		if (keeperId == null) {
			return null;
		}
		return mapper.selectByKeeperOK(keeperId, orgId);
	}

	/**
	 * 属主已审批列表
	 *
	 * @param orgId 集团ID
	 * @return 结果
	 */
	@Override
	public List<ApplicationVoSelect> getByOrgManagerOK(Integer orgId) {
		if (orgId == null) {
			return null;
		}
		return mapper.selectByManagerOkAndOrg(orgId);
	}

	/**
	 * 该用户是否是指定申请单的审计人
	 *
	 * @param user        用户
	 * @param application 申请单
	 * @return 结果
	 */
	@Override
	public boolean isAuditor(User user, Application application) {
		if (user == null || application == null) {
			return false;
		}
		//如果不是本公司的申请单,不允许查看
		if (user.getOrgId() != application.getOrgId().intValue()) {
			return false;
		}

		//是该申请单的审计人,有权限查看
		List<ApplicationAuditor> aas = applicationAuditorService.getByApplication(application.getId());
		if (aas != null && aas.size() > 0) {
			for (ApplicationAuditor aa : aas) {
				Integer auditorId = aa.getAuditorId();
				if (auditorId != null && auditorId == user.getId().intValue()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 未完结申请单ID列表
	 *
	 * @param userId 用户ID
	 * @return 结果
	 */
	@Override
	public List<Integer> getApplicationsByUser(Integer userId) {
		if (userId == null) {
			return null;
		}
		return mapper.selectApplicationsByUser(userId);
	}

	/**
	 * 该用户是否是指定申请单的授权人
	 *
	 * @param user        用户
	 * @param application 申请单
	 * @return 结果
	 */
	@Override
	public boolean isKeeper(User user, Application application) {
		if (user == null || application == null) {
			return false;
		}
		//如果不是本公司的申请单,不允许查看
		if (user.getOrgId() != application.getOrgId().intValue()) {
			return false;
		}

		//是该申请单的授权人,有权限查看
		List<ApplicationKeeper> aks = applicationKeeperService.getByApplication(application.getId());
		if (aks != null && aks.size() > 0) {
			for (ApplicationKeeper ak : aks) {
				Integer keeperId = ak.getKeeperId();
				if (keeperId != null && keeperId == user.getId().intValue()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 该用户是否是指定申请单的审批人
	 *
	 * @param user        用户
	 * @param application 申请单
	 * @return 结果
	 */
	@Override
	public boolean isManager(User user, Application application) {
		if (user == null || application == null) {
			return false;
		}
		//如果不是本公司的申请单,不允许查看
		if (user.getOrgId() != application.getOrgId().intValue()) {
			return false;
		}

		//是该申请单的审批人,有权限查看
		List<ApplicationManagerVoSelect> ams = applicationManagerService.getByApplication(application.getId());
		if (ams != null && ams.size() > 0) {
			for (ApplicationManagerVoSelect am : ams) {
				Integer managerId = am.getManagerId();
				if (managerId != null && managerId == user.getId().intValue()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 查询该申请单的申请人ID、审批人ID、授权人ID、审计人ID列表
	 *
	 * @param applicationId 申请单ID
	 * @return 结果
	 */
	@Override
	public List<Integer> getUserIdsByApplication(Integer applicationId) {
		Application application = get(applicationId);
		if (application == null) {
			return null;
		}

		List<Integer> userIds = new ArrayList<>();
		userIds.add(application.getUserId());

		//审批人列表
		List<ApplicationManager> ams = applicationManagerService.getByApplicationId(applicationId);
		if (ams != null && ams.size() > 0) {
			for (ApplicationManager am : ams) {
				Integer managerId = am.getManagerId();
				userIds.add(managerId);
			}
		}

		//授权人列表
		List<ApplicationKeeper> aks = applicationKeeperService.getByApplication(applicationId);
		if (aks != null && aks.size() > 0) {
			for (ApplicationKeeper ak : aks) {
				Integer keeperId = ak.getKeeperId();
				userIds.add(keeperId);
			}
		}


		//审计人列表
		List<ApplicationAuditor> aas = applicationAuditorService.getByApplication(applicationId);
		if (aas != null && aas.size() > 0) {
			for (ApplicationAuditor aa : aas) {
				Integer auditorId = aa.getAuditorId();
				userIds.add(auditorId);
			}
		}

		return userIds;
	}

	/**
	 * 申请单信息
	 *
	 * @param applicationId 申请单ID
	 * @return 结果
	 */
	@Override
	public ApplicationVoSelect getByMyApplication(Integer applicationId) {
		ApplicationVoSelect application = mapper.selectById(applicationId);
		if (application == null) {
			return null;
		}

		//附件列表
		List<AttachmentFile> attachmentFiles = attachmentService.getFileByApplication(applicationId);
		if (attachmentFiles != null && !attachmentFiles.isEmpty()) {
			List<FileEntity> fileEntities = new ArrayList<>();
			for (AttachmentFile attachmentFile : attachmentFiles) {
				String fileId = attachmentFile.getFileId();
				if (StringUtils.isNotBlank(fileId)) {
					FileEntity entity = fileInfoService.getReduceImgURLByFileId(fileId);
					if (entity != null && StringUtils.isNotBlank(entity.getFileUrl())) {
						fileEntities.add(entity);
					}
				}
			}
			if (fileEntities.size() > 0) {
				application.setFileEntities(fileEntities);
			}
		}

		//印章列表
		List<ApplicationDevice> ads = applicationDeviceService.getByApplication(applicationId);
		if (ads != null && ads.size() > 0) {
			List<DeviceSelectVo> vos = new ArrayList<>();
			for (ApplicationDevice ad : ads) {
				DeviceSelectVo vo = new DeviceSelectVo();

				Integer deviceId = ad.getDeviceId();
				vo.setDeviceId(deviceId);

				Signet signet = signetService.get(deviceId);
				if (signet != null && signet.getId() != null) {
					vo.setDeviceNames(signet.getName());
				} else {
					//如果没有该设备，就用旧设备名称
					vo.setDeviceNames(ad.getDeviceName());
				}
			}

			application.setDevices(vos);
		}

		//已盖次数
		List<UseCountVo> useCountVos = applicationDeviceService.getUseCountByApplication(applicationId);
		application.setUseCountVos(useCountVos);

		return application;
	}


	/**
	 * 申请单总数量
	 *
	 * @param orgId   集团ID
	 * @param userIds 用户ID列表
	 * @return 结果
	 */
	@Override
	public int getCountByOrgAndUser(Integer orgId, List<Integer> userIds) {
		if (orgId == null || userIds.isEmpty()) {
			return 0;
		}
		return mapper.selectCountByOrgAndUser(orgId, userIds);
	}

	/**
	 * 查询待绑定申请单列表
	 *
	 * @param userId    申请人ID
	 * @param startTime 授权结束日期
	 * @param deviceId  设备ID
	 * @return 结果
	 */
	@Override
	public List<ApplicationToBind> getBySealRecordInfoToBind(Integer userId, String startTime, Integer deviceId) {
		if (userId == null || StringUtils.isBlank(startTime) || deviceId == null) {
			return null;
		}
		return mapper.selectBySealRecordInfoToBind(userId, startTime, deviceId);
	}

	/**
	 * 查询用户申请的申请单总数量
	 *
	 * @param orgId      集团ID
	 * @param userInfoId 用户ID
	 * @param title      申请单标题关键词
	 * @return 结果
	 */
	@Override
	public int getCountByUserAndTitle(Integer orgId, Integer userInfoId, String title) {
		if (orgId == null || userInfoId == null || StringUtils.isBlank(title)) {
			return 0;
		}
		return mapper.selectCountByOrgAndUserAndTitle(orgId, userInfoId, title);
	}

	/**
	 * 查询未审批申请单列表
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 用户管理组织ID列表
	 * @param managerId     用户ID
	 * @return 结果
	 */
	@Override
	public List<ApplicationVoSelect> getNotApprovedByOrgAndDepartmentAndManager(Integer orgId, List<Integer> departmentIds, Integer managerId) {
		if (orgId == null || managerId == null) {
			return null;
		}
		SpringContextUtils.setPage();
		return mapper.selectNotApprovedByOrgAndDepartmentAndManager(orgId, departmentIds, managerId);
	}

	/**
	 * 查询未授权申请单列表
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 用户管理组织ID列表
	 * @param keeperId      用户ID
	 * @return 结果
	 */
	@Override
	public List<ApplicationVoSelect> getUnAuthorizedByOrgAndDepartmentAndKeeper(Integer orgId, List<Integer> departmentIds, Integer keeperId) {
		if (orgId == null || keeperId == null) {
			return null;
		}
		SpringContextUtils.setPage();
		return mapper.selectUnAuthorizedByOrgAndDepartmentAndKeeper(orgId, departmentIds, keeperId);
	}

	/**
	 * 查询未审计申请单列表
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 用户管理组织ID列表
	 * @param auditorId     用户ID
	 * @return 结果
	 */
	@Override
	public List<ApplicationVoSelect> getNotAuditedByOrgAndDepartmentAndAuditor(Integer orgId, List<Integer> departmentIds, Integer auditorId) {
		if (orgId == null || auditorId == null) {
			return null;
		}
		SpringContextUtils.setPage();
		return mapper.selectNotAuditedByOrgAndDepartmentAndAuditor(orgId, departmentIds, auditorId);
	}

	/**
	 * 申请单总数量
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 组织ID列表
	 * @return 结果
	 */
	@Override
	public Integer getCountByDepartment(Integer orgId, List<Integer> departmentIds) {
		if (orgId == null) {
			return 0;
		}
		if (departmentIds == null || departmentIds.isEmpty()) {
			return mapper.selectCountByOrg(orgId);
		}
		//查询该组织列表中所有用户ID列表
		List<Integer> userIds = userService.getIdsByDepartmentIds(departmentIds);
		if (userIds == null || userIds.isEmpty()) {
			return null;
		}
		return getCountByOrgAndUser(orgId, userIds);
	}

	/**
	 * 设备用章、使用记录上传触发该方法，对应申请单次数减1次，更新申请单状态
	 *
	 * @param applicationId 申请单ID
	 * @param deviceId      设备ID
	 * @param useCount      当前次数
	 */
	@Override
	@Transactional
	public void updateApplicationFromsignetToUse(Integer applicationId, Integer deviceId, Integer useCount) {
		if (applicationId == null || applicationId == 0 || deviceId == null) {
			return;
		}

		Application application = get(applicationId);
		if (application == null) {
			return;
		}

		String key;
		synchronized (IApplicationService.class) {
			key = RedisGlobal.APPLICATION_IS_USER + applicationId + ":" + deviceId + ":" + useCount;
			Object obj = redisUtil.get(key);
			if (obj != null) {
				return;
			}

			//加入缓存，防止印章重复请求
			redisUtil.set(key, applicationId + "-" + deviceId + "-" + useCount, RedisGlobal.APPLICATION_IS_USER_TIMEOUT);
		}

		//修改申请单状态
		try {
			//修改次数-1
			applicationDeviceService.signetMinus1(applicationId, deviceId);

			Integer oldStatus = application.getStatus();    //取出原申请单状态，最后做对比是否需要更新数据库

			/*
			 * 逻辑处理
			 * 1.查询该申请单对应的印章已用次数是否达到系统阈值
			 * 2.达到阈值的申请单状态改为'已用章'
			 * 3.未达到阈值的申请单状态改为'用章中'
			 */
			ApplicationDevice ad = applicationDeviceService.get(applicationId, deviceId);
			if (ad == null) {
				return;
			}

			Integer alreadyCount = ad.getAlreadyCount();    //已使用次数
			Integer userCount = ad.getUserCount();    //申请的总次数


			//设备阈值
			int tsValue = thresholdService.getTsValue(deviceId, application.getOrgId());

			//如果已用次数达到总次数、已用次数达到阈值，将申请单状态改为'已用章'
			if (alreadyCount > userCount || ((double) alreadyCount / userCount >= (double) tsValue / 100)) {
				application.setStatus(Global.APP_USERED);
			}
			//如果以用次数未达到阈值，将申请单状态改为'用章中'
			else {
				application.setStatus(Global.APP_USEING);
			}

			//如果申请单状态已改变，就修改数据库
			if (oldStatus != application.getStatus().intValue()) {
				//如果修改申请单状态为`已用章`,则向审计人发送短信通知
				if (Objects.equals(application.getStatus(), Global.APP_USERED)) {
					//申请人
					Integer userId = application.getUserId();
					User user = userService.get(userId);
					//组织名称
					Integer orgId = user.getOrgId();
					Org org = orgService.get(orgId);
					String orgName = org.getName();
					//审计人
					List<ApplicationAuditor> aas = applicationAuditorService.getByApplication(applicationId);
					if (aas != null && !aas.isEmpty()) {
						ApplicationAuditor aa = aas.get(0);
						messageTempService.auditorNotice(orgName, application.getUserName(), application.getTitle(), aa.getAuditorId());
					}
				}
				update(application);
			}
		} catch (Exception e) {
			log.info("出现异常 ", e);

			//申请单次数减1、申请单状态更改出现异常，将缓存删除
			redisUtil.del(key);
		}

	}

	/**
	 * 同步申请单使用次数
	 *
	 * @param deviceId      设备ID
	 * @param applicationId 申请单ID
	 * @param useCount      已用次数(申请单使用的总次数)
	 */
	@Override
	@Transactional
	@Lock(keys = "#deviceId + '_' + #applicationId + '_' + #useCount", keyConstant = "_synchApplicationInfo")
	public void synchApplicationInfo(Integer deviceId, Integer applicationId, Integer useCount) {
		if (applicationId == null || deviceId == null || useCount == null || Objects.equals(applicationId, 0) || useCount <= 0) {
			log.info("X\t同步次数-参数有误\tdeviceId:{}\tapplicationId:{}\tuseCount:{}", deviceId, applicationId, useCount);
			return;
		}

		//利用redis缓存，记录处理过的  申请单Id:设备Id:已用次数
		String key = RedisGlobal.APPLICATION_ASYNC + applicationId + ":" + deviceId + ":" + useCount;
		String value = redisUtil.getStr(key);
		if (StringUtils.isNotBlank(value)) {
			log.info("X\t同步次数-已处理过了，本次忽略\ttotalCount:{}\tdeviceId:{}\tapplicationId:{}\tuseCount:{}", value, deviceId, applicationId, useCount);
			return;
		}

		log.info("-\t同步次数\tdeviceId:{}\tapplicationId:{}\tuseCount:{}", deviceId, applicationId, useCount);

		//对比数据库已用次数,如果新的次数 > 数据库存储的已用次数,则校验是否需要修改申请单状态
		ApplicationDevice applicationDevice = applicationDeviceService.get(applicationId, deviceId);
		if (applicationDevice == null) {
			log.info("X\t同步次数-申请单关联信息不存在\tdeviceId:{}\tapplicationId:{}\tuseCount:{}", deviceId, applicationId, useCount);
			return;
		}
		Integer alreadyCount = applicationDevice.getAlreadyCount();
		Integer totalCount = applicationDevice.getUserCount();
		if (useCount > alreadyCount) {
			Integer status = null;

			//如果 已用次数 >= 申请单申请的总次数,则修改该申请单状态为 '审计中',并向审计人发送短信通知
			if (useCount >= totalCount) {
				status = Global.APP_AUDITOR;
			} else {
				//如果 已用次数/申请总次数 >= 印章阈值,则修改申请单状态'已用章',并向审计人发送短信通知
				int threshold = thresholdService.getTsValue(deviceId, applicationDevice.getOrgId());
				if ((double) useCount / totalCount >= (double) threshold / 100) {
					status = Global.APP_USERED;
				} else {
					//如果 未达到阈值状态 ,则修状态改为 '用章中'
					status = Global.APP_USEING;
				}
			}

			//修改申请单-设备关联关系表
			applicationDevice.setAlreadyCount(useCount);
			applicationDeviceService.update(applicationDevice);

			//如果 申请单的状态 < status ,则需要修改申请单状态为status
			Application application = get(applicationId);
			if (application == null) {
				log.info("X\t同步次数-申请单不存在\tdeviceId:{}\tapplicationId:{}\tuseCount:{}", deviceId, applicationId, useCount);
				return;
			}
			if (application.getStatus() != null && application.getStatus() < status) {
				application.setStatus(status);
				update(application);
				log.info("-\t同步次数-申请单状态-已更新\talreadyCount:{}\tstatus:{}\tdeviceId:{}\tapplicationId:{}\tuseCount:{}", alreadyCount, status, deviceId, applicationId, useCount);
				try {
					//当申请单状态 >= '已用章' 时，需要向审计人发送短信通知
					Integer userId = application.getUserId();//申请人
					User user = userService.get(userId);
					if (user == null) {
						log.info("X\t同步次数-审计通知短信发送失败,申请人不存在\tuserId:{}\tdeviceId:{}\tapplicationId:{}\tuseCount:{}", userId, deviceId, applicationId, useCount);
						return;
					}
					Integer orgId = user.getOrgId();
					Org org = orgService.get(orgId);
					if (org == null) {
						log.info("X\t同步次数-审计通知短信发送失败,申请人所属组织不存在\torgId:{}\tuserId:{}\tdeviceId:{}\tapplicationId:{}\tuseCount:{}", orgId, userId, deviceId, applicationId, useCount);
						return;
					}
					String orgName = org.getName();//组织名称
					//审计流程节点列表,业务中，审计人只有一个，因此节点也只有一个
					List<ApplicationAuditor> aas = applicationAuditorService.getByApplication(applicationId);
					if (aas == null || aas.isEmpty() || aas.get(0) == null) {
						log.info("X\t同步次数-审计通知短信发送失败,申请单-审计流程节点不存在\torgId:{}\tuserId:{}\tdeviceId:{}\tapplicationId:{}\tuseCount:{}", orgId, userId, deviceId, applicationId, useCount);
						return;
					}
					ApplicationAuditor aa = aas.get(0);
					messageTempService.auditorNotice(orgName, application.getUserName(), application.getTitle(), aa.getAuditorId());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				log.info("-\t同步次数-申请单状态-未更新\talreadyCount:{}\tstatus:{}\tdeviceId:{}\tapplicationId:{}\tuseCount:{}", alreadyCount, application.getStatus(), deviceId, applicationId, useCount);
			}
		}

		//处理完成后，记录该操作到redis缓存中，防止重复执行，提高运行效率
		redisUtil.set(key, totalCount);
	}

	/**
	 * 同步申请单使用次数,解决无网情况下,使用记录次数同步问题
	 *
	 * @param applicationsInfos 申请单json
	 */
	@Override
	@Transactional
	public void synchApplicationInfo(String applicationsInfos) {

		List<LoginApplication> applications = null;
		if (StringUtils.isNotBlank(applicationsInfos)) {
			applications = JSONObject.parseArray(applicationsInfos, LoginApplication.class);
		}
		if (applications == null || applications.size() == 0) {
			return;
		}

		for (LoginApplication loginApplication : applications) {
			Integer deviceId = loginApplication.getSignet();//印章ID
			Integer applicationId = loginApplication.getApplicationId();//申请单ID
			Integer tsValue = loginApplication.getTsValue();//阈值
			Integer useCount = loginApplication.getUseCount();//对应申请单-已使用次数

			String key = RedisGlobal.APPLICATION_ASYNC + applicationId + ":" + useCount;
			Object obj = redisUtil.get(key);
			if (obj != null) {
				continue;
			}

			if (deviceId == null || applicationId == null || useCount == null) {
				continue;
			}
			ApplicationDevice ad = applicationDeviceService.getByApplicationAndSignet(applicationId, deviceId);
			if (ad == null) {
				continue;
			}

			//如果印章同步的'申请单已用次数'与数据库中的'申请单已用次数'不相符，则同步数据库
			if (ad.getAlreadyCount() != null && ad.getAlreadyCount() <= useCount) {

				//同步数据库中的已用次数，以印章设备同步记录为准
				ad.setAlreadyCount(useCount);

				//查询数据库是否存在申请单
				Application application = get(applicationId);
				if (application == null) {
					continue;
				}

				//检查该申请单是否需要跳过同步
				if (isPassToSynchApplicationInfo(application)) {
					continue;
				}

				//取出当前申请单状态
				int status;

				//取出最新的 已盖次数
				Integer alreadyCount = ad.getAlreadyCount();

				//申请单'总次数'
				Integer totalCount = ad.getUserCount();

				//如果 已用次数>=申请总次数
				if (alreadyCount >= totalCount) {

					//将申请单状态置为'审计中'
					status = Global.APP_AUDITOR;

				} else {

					//如果 已用次数/申请总次数 >= 印章阈值
					if ((double) alreadyCount / totalCount >= (double) tsValue / 100) {

						//修改申请单状态'审计中'
						status = Global.APP_USERED;

					} else {
						//如果 未达到阈值状态 ,则修状态改为 '用章中'

						status = Global.APP_USEING;
					}
				}

				//因为印章记录的'已盖次数'>数据库记录的'已盖次数',所以不管怎样，ad都需要更新
				applicationDeviceService.update(ad);

				//如果状态值发生了改变，更新申请单状态
				if (!Objects.equals(status, application.getStatus())) {
					application.setStatus(status);
					update(application);
				}

			}
			redisUtil.set(key, JSONObject.toJSONString(loginApplication), RedisGlobal.APPLICATION_ASYNC_TIMEOUT);
		}
	}

	/**
	 * 检查该申请单是否需要跳过同步
	 *
	 * @param application 申请单信息
	 * @return 结果 true:跳过，不需要同步 false:不跳过，正常同步
	 */
	private boolean isPassToSynchApplicationInfo(Application application) {
		//申请单不存在，跳过
		if (application == null) {
			return true;
		}

		Integer status = application.getStatus();
		//申请单状态异常，跳过
		if (status == null) {
			return true;
		}

		//审批拒绝、授权拒绝、审计之后的单子不需要同步
		return (status == Global.APP_MANAGER_FAIL || status == Global.APP_KEEPER_FAIL || status >= Global.APP_AUDITOR);
	}

	/**
	 * 为审计申请单列表
	 *
	 * @param userInfo 要查询的目标用户
	 * @return 结果
	 */
	@Override
	public List<ApplicationVoSelect> getNotAuditedApplications(UserInfo userInfo) {
		if (userInfo == null) {
			return null;
		}

		//查询的集团ID
		Integer orgId = userInfo.getOrgId();

		//查询的用户id
		Integer userId = userInfo.getId();

		return mapper.getNotAuditedApplications(orgId, userId, userInfo.isOwner() ? 0 : 1);
	}

	/**
	 * 已审计申请单列表
	 *
	 * @param userInfo 要查询的目标用户
	 * @return 结果
	 */
	@Override
	public List<ApplicationVoSelect> getAuditedApplications(UserInfo userInfo) {
		if (userInfo == null) {
			return null;
		}

		//查询的集团ID
		Integer orgId = userInfo.getOrgId();

		//查询的用户id
		Integer userId = userInfo.getId();

		return mapper.selectAuditedApplications(orgId, userId, userInfo.isOwner() ? 0 : 1);
	}

	/**
	 * 申请单列表
	 *
	 * @param orgId     集团ID
	 * @param userTypes 使用人类型 0:申请人  1:审批人  2:审计人
	 * @param userIds   用户ID列表
	 * @return 结果
	 */
	@Override
	public List<Integer> getList(Integer orgId, List<Integer> userTypes, List<Integer> userIds) {
		if (orgId == null) {
			return null;
		}
		return mapper.selectList(orgId, userTypes, userIds);
	}

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
	 * @param orgId      集团Id
	 * @return 申请单列表
	 */
	@Override
	public List<DtoApplicationForm> applicationReportList(Date start, Date end, List<Integer> statuss, List<Integer> managerIds, List<Integer> keeperIds, List<Integer> auditorIds, String title, String content, List<Integer> deviceIds, List<Integer> userIds, Integer orgId) {
		return mapper.applicationReportList(start, end, statuss, managerIds, keeperIds, auditorIds, title, content, deviceIds, userIds, orgId);
	}
}
