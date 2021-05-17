package com.yunxi.stamper.controller;


import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.enums.ApplicationStatusEnum;
import com.yunxi.stamper.commons.jwt.UserToken;
import com.yunxi.stamper.commons.mq.MQPKG;
import com.yunxi.stamper.commons.other.*;
import com.yunxi.stamper.commons.response.Code;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.*;
import com.yunxi.stamper.entityVo.*;
import com.yunxi.stamper.service.*;
import com.yunxi.stamper.sys.aop.annotaion.WebLogger;
import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.sys.rabbitMq.MqGlobal;
import com.yunxi.stamper.sys.rabbitMq.MqSender;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/4 0004 17:41
 */
@Slf4j
@Api(tags = "申请单相关")
@RestController
@RequestMapping(value = "/application/application", method = {RequestMethod.POST, RequestMethod.GET})
public class ApplicationController extends BaseController {

	@Autowired
	private ApplicationService service;
	@Autowired
	private ApplicationDeviceService applicationDeviceService;
	@Autowired
	private ApplicationManagerService applicationManagerService;
	@Autowired
	private ApplicationKeeperService applicationKeeperService;
	@Autowired
	private ApplicationAuditorService applicationAuditorService;
	@Autowired
	private AttachmentService attachmentService;
	@Autowired
	private SealRecordInfoService sealRecordInfoService;
	@Autowired
	private UserService userService;
	@Autowired
	private SignetService signetService;
	@Autowired
	private FileInfoService fileInfoService;
	@Autowired
	private FlowService flowService;
	@Autowired
	private FlowNodeService flowNodeService;
	@Autowired
	private RelateDepartmentUserService relateDepartmentUserService;
	@Autowired
	private ApplicationNodeService applicationNodeService;
	@Autowired
	private MqSender mqSender;
	@Autowired
	private MessageTempService messageTempService;

	/**
	 * 申请单列表
	 *
	 * @return 结果json
	 */
	@GetMapping("/getByOwnerApplications")
	public ResultVO getByOwnerApplications(Integer applicationId, String title, Integer type,
										   String keyword, Integer status, Date[] date, Integer userId,
										   Integer pageNum, Integer pageSize, boolean isPage) {
		ApplicationVoSearch search = new ApplicationVoSearch();
		search.setApplicationId(applicationId);
		search.setTitle(title);
		search.setType(type);
		search.setKeyword(keyword);
		search.setStatus(status);
		search.setDate(date);
		search.setUserId(userId);
		search.setPageNum(pageNum);
		search.setPageSize(pageSize);
		search.setPage(isPage);

		UserToken token = getToken();
		search.setUserId(token.getUserId());
		boolean page = search.isPage();
		if (page) {
			PageHelper.startPage(search.getPageNum(), search.getPageSize());
		}
		List<ApplicationVo> res = service.getByStatusAndUser(search);

		//组装该申请单的印章列表
		if (res != null && res.size() > 0) {
			for (ApplicationVo vo : res) {
				Integer id = vo.getId();
				List<ApplicationDevice> ads = applicationDeviceService.getByApplication(id);
				if (ads != null && ads.size() > 0) {
					List<Map<String, Object>> list = new ArrayList<>();
					for (ApplicationDevice ad : ads) {
						Map<String, Object> device = new HashMap<>(2);
						device.put("id", ad.getDeviceId());
						device.put("deviceName", ad.getDeviceName());
						list.add(device);
					}
					vo.setDevices(list);
				}
			}
		}

		return ResultVO.Page(res, page);
	}

	/**
	 * 取消申请单
	 *
	 * @param applicationId 申请单ID
	 * @return 结果json
	 */
	@WebLogger("取消申请单")
	@PostMapping("/cancelApplication")
	@Transactional
	public ResultVO cancelApplication(@RequestParam("applicationId") Integer applicationId) {
		Application application = service.get(applicationId);
		if (application != null) {
			UserInfo userInfo = getUserInfo();

			//只有申请人才能取消自己的申请单
			if (application.getUserId() != userInfo.getId().intValue()) {
				return ResultVO.FAIL(Code.FAIL403);
			}

			//0:初始化提交 1:审批中 2:审批通过 3:审批拒绝  4:授权中 5:授权通过 6:授权拒绝  7:已推送  8:用章中 9:已用章 10:审计中 11:审计通过 12:审计拒绝 13:已失效
			Integer status = application.getStatus();
			if (status == Global.APP_CANCEL) {
				return ResultVO.OK("该申请单已取消");
			}
			if (status >= Global.APP_USEING) {
				return ResultVO.FAIL("该申请单已用章,当前无法取消");
			}

			//查询该申请单使用次数
			int count = sealRecordInfoService.getCountByApplication(applicationId);
			if (count > 0) {
				return ResultVO.FAIL("该申请单已用章(" + count + "次),当前无法取消");
			}

			//印章ID列表
			List<Integer> deviceIds = new LinkedList<>();

			//查询该申请单同步次数
			List<ApplicationDevice> ads = applicationDeviceService.getByApplication(applicationId);
			if (ads != null && ads.size() > 0) {
				for (ApplicationDevice ad : ads) {
					Integer userCount = ad.getAlreadyCount();
					if (userCount != null && userCount > 0) {
						//使用过了
						return ResultVO.FAIL("该申请单已用章(印章:" + ad.getDeviceName() + " | 用印:" + count + "次),当前无法取消");
					}
					deviceIds.add(ad.getDeviceId());
				}
			}

			lockApplication(applicationId);

			//更新申请单状态
			application.setStatus(Global.APP_CANCEL);
			service.update(application);

			//查询正在处理中的节点
			List<ApplicationNode> applicationNodes = applicationNodeService.getByApplicationAndHandle(applicationId, Global.HANDLE_ING);

			//查询该节点之后的所有节点,并删除
			List<ApplicationNode> nodes = applicationNodeService.getByApplicationAndGreaterThanOrderNo(applicationId, applicationNodes.get(0).getOrderNo());
			if (nodes != null && nodes.size() > 0) {
				for (ApplicationNode node : nodes) {
					//删除节点
					applicationNodeService.del(node);

					String name = node.getName();
					if ("审批".equalsIgnoreCase(name)) {
						//删除该节点对应的审批流程
						List<ApplicationManager> ams = applicationManagerService.getByApplicationAndNode(applicationId, node.getId());
						if (ams != null && ams.size() > 0) {
							for (ApplicationManager am : ams) {
								applicationManagerService.del(am);
							}
						}
					} else if ("授权".equalsIgnoreCase(name)) {
						//删除该节点对应的授权流程
						List<ApplicationKeeper> aks = applicationKeeperService.getByApplicationAndNode(applicationId, node.getId());
						if (aks != null && aks.size() > 0) {
							for (ApplicationKeeper ak : aks) {
								applicationKeeperService.del(ak);
							}
						}
					} else if ("审计".equalsIgnoreCase(name)) {
						//删除该节点对应的审计流程
						List<ApplicationAuditor> aas = applicationAuditorService.getByApplicationAndNode(applicationId, node.getId());
						if (aas != null && aas.size() > 0) {
							for (ApplicationAuditor aa : aas) {
								applicationAuditorService.del(aa);
							}
						}
					}
				}
			}

			//添加一个新的'取消'节点
			ApplicationNode cancelNode = new ApplicationNode();
			cancelNode.setHandle(Global.HANDLE_COMPLETE);
			cancelNode.setManagerIds(userInfo.getId() + "");
			cancelNode.setNodeType(Global.FLOW_CANCEL);
			cancelNode.setIcon(Global.ICON_CANCEL);
			cancelNode.setName("取消");
			cancelNode.setApplicationId(applicationId);
			cancelNode.setTitle("取消申请");
			cancelNode.setOrderNo(applicationNodes.get(0).getOrderNo());
			applicationNodeService.add(cancelNode);


			try {
				//向设备发送结束申请单指令,防止申请单被设备使用
				if (deviceIds.size() > 0) {
					for (Integer signetId : deviceIds) {
						Integer online = isOnline(signetId);
						if (online != null) {
							//组包
							MQPKG mqpkg = new MQPKG();
							mqpkg.setDeviceId(signetId);
							mqpkg.setUserId(userInfo.getId());
							mqpkg.setCmd(MqGlobal.SIGNET_APPLICATION_END);

							EndApplication ea = new EndApplication();
							ea.setApplicationId(applicationId);
							ea.setDeviceId(signetId);
							ea.setTitle(application.getTitle());
							ea.setUserId(userInfo.getId());
							ea.setUserName(userInfo.getUserName());

							mqpkg.setData(JSONObject.toJSONString(ea));

							mqSender.sendToExchange(properties.getRabbitMq().getExchangeOrder(), mqpkg);
							log.info("-\tMQ-下发指令-序列号:{}\t取消申请单\tuserId:{}\tname:{}\tmessage:{}", mqpkg.getSerialId(), userInfo.getId(), userInfo.getUserName(), CommonUtils.objJsonWithIgnoreFiled(mqpkg));
						}
					}
				}
				log.info("-\t取消申请单\t申请单id:{}\t用户id:{}\t用户名称:{}", applicationId, userInfo.getId(), userInfo.getUserName());
			} catch (Exception e) {
				log.error("取消申请单异常 applicationId:{}", applicationId, e);
				throw new PrintException("操作失败，请稍后重试");
			}

			return ResultVO.OK("已取消");
		}
		return ResultVO.FAIL("该申请单不存在");
	}


	/**
	 * 角标
	 *
	 * @return 结果json
	 */
	@GetMapping("/getDataByOwner")
	public ResultVO getDataByOwner() {
		UserToken token = getToken();
		Integer userId = token.getUserId();
		Integer orgId = token.getOrgId();

		//我的申请单(未完结)
		int myPendingApplicationCount = service.getCountByPendingApplications(userId);

		//待审批
		int myPendingManagerCount = service.getCountByPendingManagers(userId);

		//待授权
		int myPendingKeeperCount = service.getCountByPendingKeepers(userId);

		//待审计
		int myPendingAuditorCount = service.getCountByPendingAuditors(userId, orgId);

		Map<String, Integer> res = new HashMap<>(4);
		res.put("myPendingApplications", myPendingApplicationCount);
		res.put("myPendingManagers", myPendingManagerCount);
		res.put("myPendingKeepers", myPendingKeeperCount);
		res.put("myPendingAuditors", myPendingAuditorCount);

		return ResultVO.OK(res);
	}

	/**
	 * 申请单列表
	 *
	 * @param keyword 关键词
	 * @param list    true:通讯录格式展示 false:列表展示
	 * @return 结果json
	 */
	@GetMapping("/getApplicationsBykeyword")
	public ResultVO getApplicationsBykeyword(@RequestParam(value = "keyword", required = false) String keyword,
											 @RequestParam(value = "list", required = false, defaultValue = "false") boolean list) {
		UserInfo userInfo = getUserInfo();
		List<Application> res;
		if (userInfo.isOwner()) {
			//公司管理员
			res = service.getByOrgManager(userInfo, keyword);
		} else {
			//普通用户,根据角色权限查询
			res = service.getByUser(userInfo, keyword);
		}
		if (list) {
			List<Map<String, Object>> addressListRes = CommonUtils.getAddressList(res, "title");
			return ResultVO.OK(addressListRes);
		}
		return ResultVO.OK(res);
	}

	/**
	 * 锁定指定申请单id
	 *
	 * @param applicationId 申请单ID
	 */
	private void lockApplication(Integer applicationId) {
		String key = RedisGlobal.LOCK_APPLICATION + applicationId;
		Object obj = redisUtil.get(key);
		if (obj != null) {
			throw new PrintException("该申请单正在被使用,请稍后重试");
		} else {
			redisUtil.set(key, DateUtil.format(new Date()), RedisGlobal.LOCK_APPLICATION_TIMEOUT);
		}
	}


	/**
	 * 申请单列表
	 *
	 * @return 结果json
	 */
	@GetMapping("/searchMyApplication")
	public ResultVO searchMyApplication() {
		UserInfo userInfo = getUserInfo();
		List<Application> applications;
		boolean page = setPage();
		if (userInfo.isOwner()) {
			//系统管理员,查询所有
			applications = service.getByOrg(userInfo.getOrgId());
		} else {
			//查询自己
			applications = service.getByApplicationUser(userInfo.getId());
		}
		return ResultVO.Page(applications, page);
	}

	/**
	 * 已审批申请单列表
	 *
	 * @return 结果json
	 */
	@GetMapping("/getApprovedByOwn")
	public ResultVO getApprovedByOwn() {
		UserInfo userInfo = getUserInfo();
		boolean page = setPage();
		List<ApplicationVoSelect> applications;
		//公司管理员,查询所有`已审批`申请单列表
		if (userInfo.isOwner()) {
			applications = service.getByOrgManagerOK(userInfo.getOrgId());
		}
		//普通用户,查询自己`已审批`申请单列表
		else {
			applications = service.getByManagerOK(userInfo.getId(), userInfo.getOrgId());
		}
		return ResultVO.Page(applications, page);
	}

	/**
	 * 未审批申请单列表
	 *
	 * @param pageNum  分页参数
	 * @param pageSize 分页参数
	 * @param isPage   分页参数
	 * @return 结果json
	 */
	@ApiOperation(value = "查询未审批申请单", notes = "查询未审批申请单", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageNum", value = "当前页(默认:1)", dataType = "int"),
			@ApiImplicitParam(name = "pageSize", value = "每页数(默认:10)", dataType = "int"),
			@ApiImplicitParam(name = "page", value = "是否分页", dataType = "boolean", defaultValue = "true")
	})
	@GetMapping("/getNotApprovedByOwn")
	public ResultVO getNotApprovedByOwn(@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
										@RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
										@RequestParam(value = "page", required = false, defaultValue = "true") boolean isPage) {
		UserInfo userInfo = getUserInfo();
		//用户管理的组织ID列表
		List<Integer> departmentIds = null;
		if (userInfo.isOwner()) {
			departmentIds = userInfo.getDepartmentIds();
		}
		List<ApplicationVoSelect> applications = service.getNotApprovedByOrgAndDepartmentAndManager(userInfo.getOrgId(), departmentIds, userInfo.getId());
		return ResultVO.Page(applications, isPage);
	}

	/**
	 * 查询未授权申请单
	 *
	 * @param pageNum  分页参数
	 * @param pageSize 分页参数
	 * @param isPage   分页参数
	 * @return 结果json
	 */
	@ApiOperation(value = "查询未授权申请单", notes = "查询未授权申请单", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageNum", value = "当前页(默认:1)", dataType = "int"),
			@ApiImplicitParam(name = "pageSize", value = "每页数(默认:10)", dataType = "int"),
			@ApiImplicitParam(name = "page", value = "是否分页", dataType = "boolean", defaultValue = "true")
	})
	@GetMapping("/getUnAuthorizedByOwn")
	public ResultVO getUnAuthorizedByOwn(@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
										 @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
										 @RequestParam(value = "page", required = false, defaultValue = "true") boolean isPage) {
		UserInfo userInfo = getUserInfo();
		List<Integer> departmentIds = null;//userInfo.getDepartmentIds();
		if (userInfo.isOwner()) {
			departmentIds = userInfo.getDepartmentIds();
		}
		List<ApplicationVoSelect> applications = service.getUnAuthorizedByOrgAndDepartmentAndKeeper(userInfo.getOrgId(), departmentIds, userInfo.getId());
		return ResultVO.Page(applications, isPage);
	}

	/**
	 * 查询自己已授权申请单
	 *
	 * @return 结果json
	 */
	@GetMapping("/getAuthorizedByOwn")
	public ResultVO getAuthorizedByOwn() {
		UserInfo userInfo = getUserInfo();
		boolean page = setPage();
		List<ApplicationVoSelect> applications;
		if (userInfo.isOwner()) {
			applications = service.getByOrgKeeperOK(userInfo.getOrgId());
		} else {
			applications = service.getByKeeperOK(userInfo.getId(), userInfo.getOrgId());
		}
		return ResultVO.Page(applications, page);
	}

	/**
	 * 查询自己已审计申请单
	 *
	 * @return 结果json
	 */
	@GetMapping("/getAuditedByOwn")
	public ResultVO getAuditedByOwn() {
		UserInfo userInfo = getUserInfo();
		boolean page = setPage();
		List<ApplicationVoSelect> applications = service.getAuditedApplications(userInfo);
		return ResultVO.Page(applications, page);
	}

	/**
	 * 查询未审计申请单
	 *
	 * @param pageNum  分页参数
	 * @param pageSize 分页参数
	 * @param isPage   分页参数
	 * @return 结果json
	 */
	@ApiOperation(value = "查询未审计申请单", notes = "查询未审计申请单", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageNum", value = "当前页(默认:1)", dataType = "int"),
			@ApiImplicitParam(name = "pageSize", value = "每页数(默认:10)", dataType = "int"),
			@ApiImplicitParam(name = "page", value = "是否分页", dataType = "boolean", defaultValue = "true")
	})
	@GetMapping("/getNotAuditedByOwn")
	public ResultVO getNotAuditedByOwn(@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
									   @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
									   @RequestParam(value = "page", required = false, defaultValue = "true") boolean isPage) {
		UserInfo userInfo = getUserInfo();
		if (isPage) {
			PageHelper.startPage(pageNum, pageSize);
		}
		List<ApplicationVoSelect> applications = service.getNotAuditedApplications(userInfo);
		return ResultVO.Page(applications, isPage);
	}


	/**
	 * 查询审计详情
	 *
	 * @param applicationId 申请单ID
	 * @return 结果json
	 */
	@GetMapping("/getAuditorApplicationById")
	public ResultVO getAuditorApplicationById(@RequestParam("applicationId") Integer applicationId) {
		UserInfo userInfo = getUserInfo();
		ApplicationVoSelect voSelect = service.getById(applicationId);
		if (voSelect != null) {

			//仅该申请单的申请人,审计人(或管理员)可以查看
			if (!userInfo.isOwner() && !service.isAuditor(userInfo, voSelect) && voSelect.getUserId() != userInfo.getId().intValue()) {
				return ResultVO.FAIL(Code.FAIL403);
			}

			//审批流程
			List<ApplicationManagerVoSelect> applicationManagers = applicationManagerService.getByApplication(applicationId);
			voSelect.setApplicationManagers(applicationManagers);

			//附件
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
					voSelect.setFileEntities(fileEntities);
				}
			}

			//加盖印章id+名称
			List<DeviceSelectVo> vos = applicationDeviceService.getByApplicationId(applicationId);
			voSelect.setDevices(vos);

			//查询该申请单 印章已盖次数
			List<UseCountVo> useCountVos = applicationDeviceService.getUseCountByApplication(applicationId);
			voSelect.setUseCountVos(useCountVos);

			//加标记,true:前端显示操作按钮 false:前端不显示操作按钮  操作按钮:同意 驳回 转交
			List<ApplicationAuditor> aas = applicationAuditorService.getByApplicationAndAuditor(voSelect.getId(), userInfo.getId());
			boolean isAuditorFlag = false;
			for (ApplicationAuditor aa : aas) {
				if (aa != null && aa.getStatus() == 1) {
					isAuditorFlag = true;
					break;
				}
			}
			voSelect.setAuditor(isAuditorFlag);

			return ResultVO.OK(voSelect);
		}
		return ResultVO.FAIL("该申请单不存在");
	}

	/**
	 * 推送申请单
	 *
	 * @param applicationId 申请单ID
	 * @param signetId      印章ID
	 * @return 结果json
	 */
	@PostMapping("/pushApplicationToSignet")
	@Transactional
	public ResultVO pushApplicationToSignet(@RequestParam("applicationId") Integer applicationId,
											@RequestParam("signetId") Integer signetId) {
		Application application = service.get(applicationId);
		if (application == null || signetId == null) {
			return ResultVO.FAIL("该申请单不存在");
		}

		UserInfo info = getUserInfo();

		//只有申请人可以推送自己的申请单
		if (info.getId() != application.getUserId().intValue()) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		//申请单必须达到以下条件:授权通过,用章中,已用章,审计中,已推送
		Integer status = application.getStatus();
		if (status == Global.APP_KEEPER_OK
				|| status == Global.APP_PUSHED
				|| status == Global.APP_USEING
				|| status == Global.APP_USERED
				|| status == Global.APP_AUDITOR) {
			//可以推送
			log.debug("允许推送该申请单");
		} else {
			return ResultVO.FAIL("该申请单无法推送");
		}

		//印章信息
		Signet signet = signetService.get(signetId);
		if (signet == null || signet.getOrgId() != info.getOrgId().intValue()) {
			return ResultVO.FAIL("很抱歉,您无权限推送该设备");
		}
		if (Objects.equals(signet.getStatus(), Global.DEVICE_LOCK)) {
			return ResultVO.FAIL("设备已被锁定");
		}
		if (signet.getIsEnableApplication() != null && Objects.equals(signet.getIsEnableApplication(), 2)) {
			return ResultVO.FAIL("设备申请单功能禁用");
		}

		//剩余次数必须大于0
		ApplicationDevice ad = applicationDeviceService.get(applicationId, signetId);
		if (ad == null) {
			return ResultVO.FAIL(String.format("该申请单中不存在{%s}的申请记录", signet.getName()));
		}
		int overplus = ad.getUserCount() - ad.getAlreadyCount();
		if (overplus <= 0) {
			return ResultVO.FAIL("该印章次数已用完,无法继续推送");
		}

		//印章是否在线
		Integer online = isOnline(signetId);
		if (online == null) {
			return ResultVO.FAIL("该印章不在线");
		}

		//开锁状态下,无法推送申请单 0:关锁 1:开锁
		if (online != 0) {
			return ResultVO.FAIL("该设备正在使用中");
		}

		//组包
		MQPKG mqpkg = new MQPKG();
		mqpkg.setDeviceId(signetId);
		mqpkg.setUserId(info.getId());
		mqpkg.setCmd(MqGlobal.SIGNET_APPLICATION_PUSH);

		PushApplication pa = new PushApplication();
		pa.setApplicationId(applicationId);
		pa.setIsQss(application.getEncryptId());
		pa.setSignetId(signetId);
		pa.setTitle(application.getTitle());
		pa.setUseCount(overplus);//剩余次数
		pa.setTotalCount(application.getUserCount());//总次数
		pa.setNeedCount(ad.getAlreadyCount());//已该次数

		//查询申请人信息
		User applicationUser = userService.get(application.getUserId());
		if (applicationUser == null || applicationUser.getId() == null) {
			throw new PrintException("申请单申请人不存在");
		}
		pa.setUserName(applicationUser.getUserName());
		pa.setUserId(applicationUser.getId());

		mqpkg.setData(JSONObject.toJSONString(pa));

		mqSender.sendToExchange(properties.getRabbitMq().getExchangeOrder(), mqpkg);
		log.info("-\tMQ-下发指令-序列号:{}\t推送申请单\tuserId:{}\tname:{}\tmessage:{}", mqpkg.getSerialId(), info.getId(), info.getUserName(), CommonUtils.objJsonWithIgnoreFiled(mqpkg));

		//修改申请单状态为'已推送'
		if (status < Global.APP_PUSHED) {
			application.setStatus(Global.APP_PUSHED);
			service.update(application);
		}
		return ResultVO.OK("指令已下发");
	}

	/**
	 * 结束申请单
	 *
	 * @param applicationId 申请单ID
	 * @param signetId      印章ID
	 * @return 结果json
	 */
	@PostMapping("/endApplicationToSignet")
	public ResultVO endApplicationToSignet(@RequestParam("applicationId") Integer applicationId,
										   @RequestParam("signetId") Integer signetId) {
		Application application = service.get(applicationId);
		if (application == null || signetId == null) {
			return ResultVO.FAIL("该申请单不存在");
		}
		UserInfo info = getUserInfo();

		//仅用户本人可操作
		if (info.getId() != application.getUserId().intValue() || info.getOrgId() != application.getOrgId().intValue()) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		//只有申请人,管章人,管理员 可以推送
		Signet signet = signetService.get(signetId);
		;
		if (!info.isOwner() && !(application.getUserId() == info.getId().intValue())) {
			if (signet == null || signet.getId() == null || signet.getKeeperId() == null || signet.getKeeperId() != info.getId().intValue()) {
				//如果不是管理员,也不是申请人,也不是管章人
				return ResultVO.FAIL("很抱歉,您暂时无权限结束该申请单");
			}
		}

		if (signet == null || signet.getOrgId() != info.getOrgId().intValue()) {
			return ResultVO.FAIL("很抱歉,您无权限推送该设备");
		}

		if (signet.getIsEnableApplication() != null && Objects.equals(signet.getIsEnableApplication(), 2)) {
			return ResultVO.FAIL("设备申请单功能禁用");
		}

		//印章是否在线
		Integer online = isOnline(signetId);
		if (online == null) {
			return ResultVO.FAIL("该印章不在线");
		}

		//组包
		MQPKG mqpkg = new MQPKG();
		mqpkg.setDeviceId(signetId);
		mqpkg.setUserId(info.getId());
		mqpkg.setCmd(MqGlobal.SIGNET_APPLICATION_END);

		EndApplication ea = new EndApplication();
		ea.setApplicationId(applicationId);
		ea.setDeviceId(signetId);
		ea.setTitle(application.getTitle());
		ea.setUserId(info.getId());
		ea.setUserName(info.getUserName());

		mqpkg.setData(JSONObject.toJSONString(ea));

		mqSender.sendToExchange(properties.getRabbitMq().getExchangeOrder(), mqpkg);
		log.info("-\tMQ-下发指令-序列号:{}\t结束申请单\tuserId:{}\tname:{}\tmessage:{}", mqpkg.getSerialId(), info.getId(), info.getUserName(), CommonUtils.objJsonWithIgnoreFiled(mqpkg));
		return ResultVO.OK("指令已下发");
	}


	/**
	 * 检查该印章当前是否在使用，如果该印章中还存在此申请单，则结束该申请单
	 * 1.防止用户提交审计后，印章依旧使用的异常情况
	 *
	 * @param application 申请单信息
	 */
	private void sendExitApplicationToSignet(Application application) {
		if (application == null) {
			return;
		}
		List<ApplicationDevice> ads = applicationDeviceService.getByApplication(application.getId());
		if (ads == null || ads.isEmpty()) {
			return;
		}

		UserInfo userInfo = getUserInfo();
		for (ApplicationDevice ad : ads) {
			Integer deviceId = ad.getDeviceId();
			Integer applicationId = ad.getApplicationId();
			//查询印章是否在线
			Integer online = isOnline(deviceId);

			//不在线
			if (online == null) {
				return;
			}

			//印章不在使用中不管
			if (online != 1) {
				continue;
			}

			//在线并且在使用中,发送结束申请单指令
			MQPKG mqpkg = new MQPKG();
			mqpkg.setDeviceId(deviceId);
			mqpkg.setUserId(userInfo.getId());
			mqpkg.setCmd(MqGlobal.SIGNET_APPLICATION_END);

			EndApplication ea = new EndApplication();
			ea.setApplicationId(applicationId);
			ea.setDeviceId(deviceId);
			ea.setTitle(application.getTitle());
			ea.setUserId(userInfo.getId());
			ea.setUserName(userInfo.getUserName());

			mqpkg.setData(JSONObject.toJSONString(ea));

			mqSender.sendToExchange(properties.getRabbitMq().getExchangeOrder(), mqpkg);
			log.info("-\tMQ-下发指令-序列号:{}\t提交审计\tuserId:{}\tname:{}\tmessage:{}", mqpkg.getSerialId(), userInfo.getId(), userInfo.getUserName(), CommonUtils.objJsonWithIgnoreFiled(mqpkg));
		}
	}

	/**
	 * 查询最近5次申请单列表
	 *
	 * @return 结果json
	 */
	@GetMapping("/getNearestApplication")
	public ResultVO getNearestApplication() {
		UserToken token = getToken();
		List<ApplicationVo> apps = service.getNearestApplications(token.getUserId());

		if (apps.size() <= 0) {
			return ResultVO.OK("当前用户没有申请单");
		}
		ApplicationVo app = apps.get(0);
		//仅用户本人可查看
		if (token.getUserId() != app.getUserId().intValue() || token.getOrgId() != app.getOrgId().intValue()) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		if (apps.size() > 0) {
			return ResultVO.OK(apps);
		}
		return ResultVO.OK();
	}


	/**
	 * 添加申请单
	 *
	 * @param title      标题
	 * @param content    内容
	 * @param useCount   次数
	 * @param managerIds 审批人id列表
	 * @param processId  审批流程id列表
	 * @param fileIds    附件Id列表
	 * @return 结果json
	 */
	@ApiOperation(value = "用章申请", notes = "用章申请", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "applicationName", value = "用章标题", dataType = "String", required = true),
			@ApiImplicitParam(name = "content", value = "用章描述", dataType = "String"),
			@ApiImplicitParam(name = "useCount", value = "用章次数", dataType = "int", required = true),
			@ApiImplicitParam(name = "deviceId", value = "设备ID", dataType = "int", required = true),
			@ApiImplicitParam(name = "managerIds", value = "审批人ID列表", dataType = "list"),
			@ApiImplicitParam(name = "processId", value = "审批流程ID", dataType = "int"),
			@ApiImplicitParam(name = "departmentId", value = "组织ID", dataType = "int"),
			@ApiImplicitParam(name = "fileIds", value = "用章附件ID列表", dataType = "list", required = true)

	})
	@WebLogger(value = "【#】用章申请", key = 0)
	@PostMapping("/add")
	public ResultVO add(@RequestParam("applicationName") String title,
						@RequestParam("useCount") Integer useCount,
						@RequestParam("deviceId") Integer deviceId,
						@RequestParam(value = "content", required = false) String content,
						@RequestParam(value = "managerIds", required = false) Integer[] managerIds,
						@RequestParam(value = "processId", required = false) Integer processId,
						@RequestParam(value = "departmentId", required = false) Integer departmentId,
						@RequestParam(value = "fileIds", required = false) String[] fileIds) {
		//标题参数校验
		if (StringUtils.isBlank(title)) {
			return ResultVO.FAIL("用章标题不能为空");
		}
		if (EmojiFilter.containsEmoji(title)) {
			return ResultVO.FAIL("用章标题不能包含特殊字符");
		}
		if (title.length() > 50) {
			return ResultVO.FAIL("用章标题不能超过50个字符");
		}

		//内容参数校验
		if (EmojiFilter.containsEmoji(content)) {
			return ResultVO.FAIL("用章描述不能包含特殊字符");
		}
		if (StringUtils.isNotBlank(content) && content.length() > 255) {
			return ResultVO.FAIL("用章描述不能超过255个字符");
		}

		//次数参数校验
		if (useCount <= 0 || useCount > 9999) {
			return ResultVO.FAIL("用章次数有误");
		}
		int temp = Integer.parseInt(useCount.toString());
		if (useCount - temp != 0) {
			return ResultVO.FAIL("请输入整数");
		}

		UserInfo userInfo = getUserInfo();
		//申请单创建频率控制，每个用户每分钟最多创建30个申请单
		String key = RedisGlobal.APPLICATION_PREFIX_KEY + userInfo.getId();
		Set<String> addApps = redisUtil.keys(key + "*");
		if (addApps != null && addApps.size() > 0 && (addApps.size() > properties.getApplicationMinute())) {
			return ResultVO.FAIL("创建频率过高，请稍后重试");
		}

		//设备参数校验
		if (deviceId == null) {
			return ResultVO.FAIL("印章不能为空");
		}

		Signet signet = signetService.get(deviceId);
		if (signet == null || signet.getId() == null) {
			return ResultVO.FAIL("设备不存在");
		}

		if (signet.getOrgId() != userInfo.getOrgId().intValue()) {
			return ResultVO.FAIL("设备选择有误");
		}

		Integer keeperId = signet.getKeeperId();
		if (keeperId == null) {
			return ResultVO.FAIL("印章管理员未配置");
		}

		User keeper = userService.get(keeperId);
		if (keeper == null || keeper.getId() == null) {
			return ResultVO.FAIL("印章管理员不存在");
		}

		if (keeper.getOrgId() != userInfo.getOrgId().intValue()) {
			return ResultVO.FAIL("印章管理员配置有误");
		}

		Integer auditorId = signet.getAuditorId();
		if (auditorId == null) {
			return ResultVO.FAIL("印章审计员未配置");
		}

		User auditor = userService.get(auditorId);
		if (auditor == null || auditor.getId() == null) {
			return ResultVO.FAIL("印章审计员不存在");
		}

		if (auditor.getOrgId() != userInfo.getOrgId().intValue()) {
			return ResultVO.FAIL("印章审计员配置有误");
		}

		//校验审批节点
		if ((managerIds == null || managerIds.length == 0) && (processId == null)) {
			return ResultVO.FAIL("审批流程(审批员)不能为空");
		}

		//容器：每个节点信息
		List<NodeEntity> nodeEntities = new LinkedList<>();

		//校验审批人列表
		if (managerIds != null && managerIds.length > 0) {
			for (Integer managerId : managerIds) {
				User manager = userService.get(managerId);
				if (manager == null || manager.getId() == null) {
					return ResultVO.FAIL("审批员不存在");
				}
				if (manager.getOrgId() != userInfo.getOrgId().intValue()) {
					return ResultVO.FAIL("审批员选择有误");
				}

				NodeEntity entity = new NodeEntity();
				entity.setType("list");
				entity.getManagers().add(manager);
				nodeEntities.add(entity);

			}
		}

		//校验审批流程
		List<FlowVoAddEntity> flowNodes;
		Flow flow = null;
		if (processId != null) {
			flow = flowService.get(processId);
			if (flow == null) {
				return ResultVO.FAIL("审批流程不存在");
			}

			if (flow.getOrgId() != userInfo.getOrgId().intValue()) {
				return ResultVO.FAIL("审批流程选择有误");
			}

			Integer status = flow.getStatus();
			if (status != null && status == 1) {
				return ResultVO.FAIL("审批流程已被禁用");
			}

			flowNodes = flowNodeService.getVoByFlow(flow.getId());
			if (flowNodes == null || flowNodes.size() == 0) {
				return ResultVO.FAIL("该审批流程未配置完成");
			}

			List<Integer> departmentIds = relateDepartmentUserService.getDepartmentIdsByUserId(userInfo.getId());


			for (FlowVoAddEntity entity : flowNodes) {
				//解析审批人列表
				String managerJson = entity.getManagerJson();
				List<FlowVoAddEntityKV> entityManagers = JSONObject.parseArray(managerJson, FlowVoAddEntityKV.class);

				if (entityManagers == null || entityManagers.size() == 0) {
					return ResultVO.FAIL("该审批流程节点配置有误");
				}

				//创建节点
				String type = entity.getType();
				NodeEntity nodeEntity = new NodeEntity();
				nodeEntity.setType(type);

				if (type.equalsIgnoreCase(Global.FLOW_LIST)
						|| type.equalsIgnoreCase(Global.FLOW_OR)
						|| type.equalsIgnoreCase(Global.FLOW_AND)) {

					for (FlowVoAddEntityKV kv : entityManagers) {
						int managerId = kv.getKey();
						User manager = userService.get(managerId);
						if (manager == null || manager.getId() == null) {
							return ResultVO.FAIL("该审批流程配置的审批人已失效");
						}
						nodeEntity.getManagers().add(manager);
					}

				} else if (type.equalsIgnoreCase(Global.FLOW_MANAGER)) {


					if (departmentIds == null || departmentIds.isEmpty() || !departmentIds.contains(departmentId)) {
						return ResultVO.FAIL("检测您当前未匹配组织，无法申请主管审批");
					}

					for (FlowVoAddEntityKV kv : entityManagers) {
						int level = kv.getKey();
						nodeEntity.setLevel(level);
					}

				} else if (type.equalsIgnoreCase(Global.FLOW_OPTIONAL)) {

					for (FlowVoAddEntityKV kv : entityManagers) {
						int managerId = kv.getKey();
						User manager = userService.get(managerId);
						if (manager == null || manager.getId() == null) {
							return ResultVO.FAIL("该审批流程配置的审批人已失效");
						}
						nodeEntity.getManagers().add(manager);
					}

				} else {
					return ResultVO.FAIL("该审批流程节点配置有误");
				}

				nodeEntities.add(nodeEntity);
			}
		}

		//校验附件
		if (fileIds == null || fileIds.length == 0) {
			return ResultVO.FAIL("申请附件不能为空");
		}
		List<FileInfo> fileInfos = new LinkedList<>();
		for (String fileId : fileIds) {
			FileInfo fileInfo = fileInfoService.get(fileId);
			if (fileInfo == null || fileInfo.getId() == null) {
				return ResultVO.FAIL("附件不存在或已失效");
			}
			fileInfos.add(fileInfo);
		}

		service.application(title, content, useCount, signet, keeper, auditor, nodeEntities, flow, fileInfos, departmentId, userInfo);

		//存缓存60秒：申请单标题
		redisUtil.set(key + ":" + System.currentTimeMillis(), title, RedisGlobal.APPLICATION_PREFIX_KEY_TIME_OUT);

		return ResultVO.OK("申请成功");
	}

	/**
	 * 申请单信息
	 *
	 * @param applicationId 申请单ID
	 * @return 结果json
	 */
	@ApiOperation(value = "查询申请单详情", notes = "查询申请单详情", httpMethod = "GET")
	@ApiImplicitParam(name = "applicationId", value = "申请单ID", dataType = "int", required = true)
	@GetMapping("/getByMyApplication")
	public ResultVO getByMyApplication(@RequestParam("applicationId") Integer applicationId) {
		//参数校验
		if (applicationId == null) {
			return ResultVO.FAIL("无申请单");
		}
		Application application = service.get(applicationId);
		if (application == null) {
			return ResultVO.FAIL("申请单不存在");
		}
		UserInfo userInfo = getUserInfo();

		//检查权限
		if (application.getOrgId() != userInfo.getOrgId().intValue()) {
			return ResultVO.FAIL("无权限查看");
		}

		//仅集团属主、申请人、审批人、授权人、审计人有权限查看
		if (!userInfo.isOwner()) {
			List<Integer> userIds = service.getUserIdsByApplication(applicationId);
			if (userIds == null || userIds.isEmpty() || !userIds.contains(userInfo.getId())) {
				return ResultVO.FAIL("无权限查看");
			}
		}
		ApplicationVoSelect voSelect = service.getByMyApplication(applicationId);
		return ResultVO.OK(voSelect);
	}

	/**
	 * 审批流程信息
	 *
	 * @param applicationId 申请单ID
	 * @return 结果json
	 */
	@ApiOperation(value = "查询审批详情", notes = "查询审批详情", httpMethod = "GET")
	@ApiImplicitParam(name = "applicationId", value = "申请单ID", dataType = "int", required = true)
	@GetMapping("/getManagerApplicationById")
	public ResultVO getManagerApplicationById(@RequestParam("applicationId") Integer applicationId) {
		UserInfo userInfo = getUserInfo();
		ApplicationVoSelect voSelect = service.getById(applicationId);
		if (voSelect != null) {

			//仅该申请单的审批人(或组织负责人)可以查看
			if (!userInfo.isOwner()
					&& !service.isManager(userInfo, voSelect)
					&& voSelect.getUserId() != userInfo.getId().intValue()) {
				return ResultVO.FAIL(Code.FAIL403);
			}

			//审批流程
			List<ApplicationManagerVoSelect> applicationManagers = applicationManagerService.getByApplication(applicationId);
			voSelect.setApplicationManagers(applicationManagers);

			//附件
			List<FileEntity> fileEntities = new ArrayList<>();
			List<AttachmentFile> attachments = attachmentService.getFileByApplication(applicationId);
			if (attachments != null && !attachments.isEmpty()) {
				for (AttachmentFile attachmentFile : attachments) {
					String fileId = attachmentFile.getFileId();
					FileEntity entity = fileInfoService.getReduceImgURLByFileId(fileId);
					if (entity != null && StringUtils.isNotBlank(entity.getFileUrl())) {
						fileEntities.add(entity);
					}
				}
			}
			if (fileEntities.size() > 0) {
				voSelect.setFileEntities(fileEntities);
			}

			//加盖印章id+名称
			List<DeviceSelectVo> vos = applicationDeviceService.getByApplicationId(applicationId);
			voSelect.setDevices(vos);

			//查询该申请单 印章已盖次数
			List<UseCountVo> useCountVos = applicationDeviceService.getUseCountByApplication(applicationId);
			voSelect.setUseCountVos(useCountVos);

			//加标记,true:前端显示操作按钮 false:前端不显示操作按钮  操作按钮:同意 驳回 转交
			ApplicationManager am = applicationManagerService.getByApplicationAndManagerAndDealing(voSelect.getId(), userInfo.getId());
			voSelect.setManager(am != null && am.getStatus() == Global.MANAGER_HANDLING);

			return ResultVO.OK(voSelect);
		}
		return ResultVO.FAIL("该申请单不存在");
	}

	@ApiOperation(value = "查询授权详情", notes = "查询授权详情", httpMethod = "GET")
	@ApiImplicitParam(name = "applicationId", value = "申请单ID", dataType = "int", required = true)
	@GetMapping("/getKeeperApplicationById")
	public ResultVO getKeeperApplicationById(@RequestParam("applicationId") Integer applicationId) {
		UserInfo userInfo = getUserInfo();
		ApplicationVoSelect voSelect = service.getById(applicationId);
		if (voSelect != null) {

			//仅该申请单的申请人,授权人(或管理员)可以查看
			if (!userInfo.isOwner()
					&& !service.isKeeper(userInfo, voSelect)
					&& voSelect.getUserId() != userInfo.getId().intValue()) {
				return ResultVO.FAIL(Code.FAIL403);
			}

			//审批流程
			if (!isApp()) {
				List<ApplicationManagerVoSelect> applicationManagers = applicationManagerService.getByApplication(applicationId);
				voSelect.setApplicationManagers(applicationManagers);
			}

			//附件
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
					voSelect.setFileEntities(fileEntities);
				}
			}

			//加盖印章id+名称
			List<DeviceSelectVo> vos = applicationDeviceService.getByApplicationId(applicationId);
			voSelect.setDevices(vos);

			//查询该申请单 印章已盖次数
			List<UseCountVo> useCountVos = applicationDeviceService.getUseCountByApplication(applicationId);
			voSelect.setUseCountVos(useCountVos);

			//加标记,true:前端显示操作按钮 false:前端不显示操作按钮  操作按钮:同意 驳回 转交
			List<ApplicationKeeper> aks = applicationKeeperService.getByApplicationAndKeeper(voSelect.getId(), userInfo.getId());
			boolean isKeeperFlag = false;
			for (ApplicationKeeper ak : aks) {
				if (ak != null && ak.getStatus() == 1) {
					isKeeperFlag = true;
					break;
				}
			}
			voSelect.setKeeper(isKeeperFlag);
			return ResultVO.OK(voSelect);
		}
		return ResultVO.FAIL("该申请单不存在");
	}

	@ApiOperation(value = "查询可绑定的申请单列表", notes = "查询可绑定的申请单列表", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sealRecordInfoId", value = "使用记录ID", dataType = "int", required = true),
			@ApiImplicitParam(name = "pageNum", value = "当前页(默认:1)", dataType = "int"),
			@ApiImplicitParam(name = "pageSize", value = "每页数(默认:10)", dataType = "int"),
			@ApiImplicitParam(name = "page", value = "是否分页", dataType = "boolean", defaultValue = "true")
	})
	@GetMapping("/getBySealRecordInfoToBind")
	public ResultVO getBySealRecordInfoToBind(@RequestParam(value = "page", required = false, defaultValue = "true") boolean isPage,
											  @RequestParam("sealRecordInfoId") Integer sealRecordInfoId) {
		//参数校验:使用记录ID
		if (sealRecordInfoId == null) {
			return ResultVO.FAIL("提交参数有异常");
		}
		SealRecordInfo sealRecordInfo = sealRecordInfoService.get(sealRecordInfoId);
		if (sealRecordInfo == null || sealRecordInfo.getId() == null) {
			return ResultVO.FAIL("使用记录不存在");
		}
		Integer applicationId = sealRecordInfo.getApplicationId();
		if (applicationId != null && applicationId != 0) {
			return ResultVO.FAIL("使用记录已存在申请单");
		}

		//校验权限
		UserInfo userInfo = getUserInfo();
		if (sealRecordInfo.getOrgId() != userInfo.getOrgId().intValue()) {
			return ResultVO.FAIL("使用记录不存在");
		}
		if (sealRecordInfo.getUserId() == null) {
			return ResultVO.FAIL("使用记录数据有误");
		}
		if (sealRecordInfo.getUserId() != userInfo.getId().intValue()) {
			return ResultVO.FAIL("无权限");
		}
		List<ApplicationToBind> applications = service.getBySealRecordInfoToBind(userInfo.getId(), DateUtil.format(sealRecordInfo.getRealTime()), sealRecordInfo.getDeviceId());
		return ResultVO.Page(applications, isPage);
	}

	/**
	 * 用户提交申请单，进入审计流程
	 *
	 * @param applicationId 申请单ID
	 * @return 结果json
	 */
	@ApiOperation(value = "提交审计", notes = "提交审计", httpMethod = "POST")
	@ApiImplicitParam(name = "applicationId", value = "申请单ID", dataType = "int", required = true)
	@WebLogger("提交审计")
	@PostMapping("/exitApplication")
	public ResultVO exitApplication(@RequestParam("applicationId") Integer applicationId) {
		//参数校验：申请单
		UserInfo userInfo = getUserInfo();
		Application application = service.get(applicationId);
		if (application == null || application.getOrgId() != userInfo.getOrgId().intValue()) {
			return ResultVO.FAIL("申请单不存在");
		}

		//仅用户本人可操作
		UserToken token = getToken();
		if (token.getUserId() != application.getUserId().intValue()) {
			return ResultVO.FAIL(Code.FAIL403);
		}
		//校验申请单状态
		Integer status = application.getStatus();
		if (status <= Global.APP_PUSHED) {
			return ResultVO.FAIL("该申请单暂未用章,无法进入审计");
		}
		if (status == Global.APP_AUDITOR) {
			return ResultVO.FAIL("该申请单已在审计中");
		}
		if (status == Global.APP_AUDITOR_OK || status == Global.APP_AUDITOR_FAIL) {
			return ResultVO.FAIL("该申请单已审计过了");
		}
		if (status == Global.APP_CANCEL) {
			return ResultVO.FAIL("该申请单已失效");
		}

		//检查该印章当前是否在使用，如果该印章中还存在此申请单，则结束该申请单
		sendExitApplicationToSignet(application);

		//修改申请单状态
		application.setStatus(Global.APP_AUDITOR);//审计中
		service.update(application);

		//向审计人推送短信、通知提醒
		List<ApplicationAuditor> aas = applicationAuditorService.getByApplication(applicationId);
		if (aas != null && aas.size() > 0) {
			//通知审计人
			for (ApplicationAuditor aa : aas) {
				Integer userId = application.getUserId();
				User user = userService.get(userId);
				Integer orgId = user.getOrgId();
				Org org = orgService.get(orgId);
				String orgName = org.getName();
				messageTempService.auditorNotice(orgName, application.getUserName(), application.getTitle(), aa.getAuditorId());
			}
		}

		return ResultVO.OK("申请单[" + application.getTitle() + "]已进入审计流程");
	}

	@ApiOperation(value = "申请单状态列表", notes = "申请单状态列表", httpMethod = "GET")
	@GetMapping("/statusList")
	public ResultVO statusList() {
		List<Map<String, Object>> mapList = new ArrayList<>();
		ApplicationStatusEnum[] applicationStatusEnums = ApplicationStatusEnum.values();
		for (ApplicationStatusEnum applicationStatusEnum : applicationStatusEnums) {
			int code = applicationStatusEnum.getCode();
			String msg = applicationStatusEnum.getMsg();

			Map<String, Object> map = new HashMap<>(2);
			map.put("code", code);
			map.put("msg", msg);
			mapList.add(map);
		}
		return ResultVO.OK(mapList);
	}
}
