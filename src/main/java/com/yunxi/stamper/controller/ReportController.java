package com.yunxi.stamper.controller;


import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.jwt.UserToken;
import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.other.EmojiFilter;
import com.yunxi.stamper.commons.other.Global;
import com.yunxi.stamper.commons.other.RedisGlobal;
import com.yunxi.stamper.commons.response.Code;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.*;
import com.yunxi.stamper.entityVo.*;
import com.yunxi.stamper.service.*;
import com.yunxi.stamper.sys.aop.annotaion.WebLogger;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.*;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/21 0021 11:24
 */
@Slf4j
@RestController
@Api(tags = "报表相关")
@RequestMapping(value = "/device/report", method = {RequestMethod.POST, RequestMethod.GET})
public class ReportController extends BaseController {

	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private ReportAsyncService reportAsyncService;
	@Autowired
	private SealRecordInfoService sealRecordInfoService;
	@Autowired
	private UserService userService;
	@Autowired
	private ApplicationService applicationService;
	@Autowired
	private ApplicationManagerService applicationManagerService;
	@Autowired
	private ApplicationKeeperService applicationKeeperService;
	@Autowired
	private ApplicationDeviceService applicationDeviceService;
	@Autowired
	private ApplicationAuditorService applicationAuditorService;
	@Autowired
	private ErrorTypeService errorTypeService;
	@Autowired
	private StamperPictureService stamperPictureService;
	@Autowired
	private ReportService service;

	@ApiOperation(value = "查询申请单报表", notes = "查询申请单列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageNum", value = "当前页(默认:1)", dataType = "int"),
			@ApiImplicitParam(name = "pageSize", value = "每页数(默认:10)", dataType = "int"),
			@ApiImplicitParam(name = "page", value = "是否分页", dataType = "boolean", defaultValue = "true"),
			@ApiImplicitParam(name = "start", value = "开始时间"),
			@ApiImplicitParam(name = "end", value = "结束时间"),
			@ApiImplicitParam(name = "statuss", value = "状态"),
			@ApiImplicitParam(name = "managerIds", value = "审批人Id列表"),
			@ApiImplicitParam(name = "keeperIds", value = "授权人Id列表"),
			@ApiImplicitParam(name = "auditorIds", value = "审计人Id列表"),
			@ApiImplicitParam(name = "title", value = "标题"),
			@ApiImplicitParam(name = "content", value = "内容"),
			@ApiImplicitParam(name = "deviceIds", value = "设备名称")
	})
	@GetMapping("/applicationFormList")
	public ResultVO applicationFormList(@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
										@RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
										@RequestParam(value = "page", required = false, defaultValue = "true") boolean isPage,
										@RequestParam(value = "start", required = false) Date start,
										@RequestParam(value = "end", required = false) Date end,
										@RequestParam(required = false) Integer[] statuss,
										@RequestParam(required = false) Integer[] userIds,
										@RequestParam(required = false) Integer[] managerIds,
										@RequestParam(required = false) Integer[] keeperIds,
										@RequestParam(required = false) Integer[] auditorIds,
										@RequestParam(required = false) String title,
										@RequestParam(required = false) String content,
										@RequestParam(required = false) Integer[] deviceIds) {

		//TODO:权限&数据可见性校验
		UserInfo userInfo = getUserInfo();

		//参数处理
		List<Integer> statusList = toList(statuss);
		List<Integer> managerIdList = toList(managerIds);
		List<Integer> keeperIdList = toList(keeperIds);
		List<Integer> auditorIdList = toList(auditorIds);
		List<Integer> deviceIdList = toList(deviceIds);
		List<Integer> userIdList = toList(userIds);
		Integer orgId = userInfo.getOrgId();

		//查询SQL
		if (isPage) {
			PageHelper.startPage(pageNum, pageSize);
		}
		List<DtoApplicationForm> dtoApplicationFormList = applicationService.applicationReportList(start, end, statusList, managerIdList, keeperIdList, auditorIdList, title, content, deviceIdList, userIdList, orgId);
		if (dtoApplicationFormList == null || dtoApplicationFormList.isEmpty()) {
			return ResultVO.OK();
		}

		if (isPage) {
			PageInfo pageInfo = new PageInfo(dtoApplicationFormList);
			return ResultVO.OK(pageInfo);
		}
		return ResultVO.OK(dtoApplicationFormList);
	}

	private List<Integer> toList(Integer[] array) {
		return array == null ? null : Arrays.asList(array);
	}

	@ApiOperation(value = "查询报表列表记录", notes = "查询报表列表记录", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageNum", value = "当前页(默认:1)", dataType = "int"),
			@ApiImplicitParam(name = "pageSize", value = "每页数(默认:10)", dataType = "int"),
			@ApiImplicitParam(name = "page", value = "是否分页", dataType = "boolean", defaultValue = "true"),
			@ApiImplicitParam(name = "start", value = "开始时间"),
			@ApiImplicitParam(name = "end", value = "结束时间"),
			@ApiImplicitParam(name = "types", value = "使用人类型  0:申请人  1:审批人  2:审计人  3:授权人  4:用印人"),
			@ApiImplicitParam(name = "t", value = "使用人类型 是否全选  1:是全选"),
			@ApiImplicitParam(name = "deviceIds", value = "印章ID列表 all:全选"),
			@ApiImplicitParam(name = "d", value = "印章ID列表 是否全选  1:是全选"),
			@ApiImplicitParam(name = "userIds", value = "使用人ID列表 all:全选"),
			@ApiImplicitParam(name = "u", value = "使用人ID列表 是否全选  1:是全选")
	})
	@RequestMapping(value = "/getReportList", method = {RequestMethod.POST, RequestMethod.GET})
	public ResultVO getReportList(@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
								  @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
								  @RequestParam(value = "page", required = false, defaultValue = "true") boolean isPage,
								  @RequestParam(value = "start", required = false) Date start,
								  @RequestParam(value = "end", required = false) Date end,
								  @RequestParam(name = "types", required = false) List<Integer> types,
								  @RequestParam(name = "t", required = false, defaultValue = "0") Integer t,
								  @RequestParam(name = "deviceIds", required = false) List<Integer> deviceIds,
								  @RequestParam(name = "d", required = false, defaultValue = "0") Integer d,
								  @RequestParam(name = "userIds", required = false) List<Integer> userIds,
								  @RequestParam(name = "u", required = false, defaultValue = "0") Integer u) {
		//手动检查用户权限
		String url = "/device/signet/getByOwner";
		UserInfo userInfo = getUserInfo();
		if (userInfo.isOwner() || userInfo.isAdmin() || userInfo.getPermsUrls().contains(url)) {
			//属主,管理员或拥有印章管理权限的用户,拥有导出报表权限
			log.info("属主,管理员或拥有印章管理权限的用户,拥有导出报表权限");
		} else {
			return ResultVO.FAIL("无权限");
		}

		//处理参数
		if (d == 1) {
			deviceIds = null;
		}
		if (t == 1) {
			types = null;
		} else {
			if (userIds == null || userIds.isEmpty()) {
				return ResultVO.FAIL("请指定用户");
			}
		}
		if (u == 1) {
			userIds = null;
		}


		Integer orgId = userInfo.getOrgId();

		//查询使用记录
		if (isPage) {
			PageHelper.startPage(pageNum, pageSize);
		}
		List<SealRecordInfo> sealRecordInfos = sealRecordInfoService.getReportList2(orgId, types, deviceIds, userIds, start, end);
		if (sealRecordInfos == null || sealRecordInfos.isEmpty()) {
			return ResultVO.OK("无记录");
		}

		PageInfo pageInfo = null;
		if (isPage) {
			pageInfo = new PageInfo(sealRecordInfos);
		}

		/*
			组装前端需要的参数
		 */
		Map<Integer, ApplicationInfoA> tempMap = new HashMap<>();
		List<SealRecordInfoReport> reports = new ArrayList<>(sealRecordInfos.size());
		for (SealRecordInfo sealRecordInfo : sealRecordInfos) {
			/*
				申请单相关
			 */
			Integer applicationId = sealRecordInfo.getApplicationId();
			ApplicationInfoA applicationInfo = null;
			if (applicationId != null && applicationId != 0) {    //申请单ID为0相当于为空或者为NULL的
				//先从临时容器中查询
				applicationInfo = tempMap.get(applicationId);
				//查询数据库
				if (applicationInfo == null) {
					applicationInfo = searchApplicationInfoById(applicationId);
					//缓存到临时容器中,提高程序效率
					if (applicationInfo != null) {
						tempMap.put(applicationId, applicationInfo);
					}
				}
			}

			/*
				用印异常相关
			 */
			Integer sealRecordInfoId = sealRecordInfo.getId();
			List<ErrorType> errors = errorTypeService.getBySealRecordInfo(sealRecordInfoId);

			/*
			 	type 0:使用记录图片 1:审计图片 2:超出申请单次数图片 3:长按报警图片 4:拆卸警告图片
			 */

			//用印图片
			List<FileEntity> useUrls = stamperPictureService.getBySealRecordInfoAndType(sealRecordInfoId, 0);

			//审计图片
			List<FileEntity> auditorUrls = stamperPictureService.getBySealRecordInfoAndType(sealRecordInfoId, 1);

			//超次图片
			List<FileEntity> excessUrls = stamperPictureService.getBySealRecordInfoAndType(sealRecordInfoId, 2);

			//超时图片
			List<FileEntity> timeoutUrls = stamperPictureService.getBySealRecordInfoAndType(sealRecordInfoId, 3);

			//追加图片
			List<FileEntity> replenishUrls = stamperPictureService.getBySealRecordInfoAndType(sealRecordInfoId, Global.TYPE_REPLENISH);

			Application application = applicationInfo == null ? null : applicationInfo.getApplication();
			SealRecordInfoReport report = new SealRecordInfoReport(sealRecordInfo, application, errors, useUrls, auditorUrls, excessUrls, timeoutUrls, replenishUrls);

			if (applicationInfo != null) {
				//申请人
				User user = applicationInfo.getUser();
				if (user != null) {
					report.setApplicationUserName(user.getUserName());
				}

				//审批人
				List<ApplicationManager> applicationManagers = applicationInfo.getApplicationManagers();
				if (applicationManagers != null && !applicationManagers.isEmpty()) {
					List<String> applicationManagernames = new LinkedList<>();
					for (ApplicationManager applicationManager : applicationManagers) {
						String managerName = applicationManager.getManagerName();
						applicationManagernames.add(managerName);
					}
					report.setManagerUsernames(applicationManagernames);
				}

				//授权人
				List<ApplicationKeeper> applicationKeepers = applicationInfo.getApplicationKeepers();
				if (applicationKeepers != null && !applicationKeepers.isEmpty()) {
					String keeperName = applicationKeepers.get(0).getKeeperName();
					report.setKeepername(keeperName);
				}

				//审计人
				List<ApplicationAuditor> applicationAuditors = applicationInfo.getApplicationAuditors();
				if (applicationAuditors != null && !applicationAuditors.isEmpty()) {
					String auditorName = applicationAuditors.get(0).getAuditorName();
					report.setAuditorname(auditorName);
				}
			}

			reports.add(report);
		}

		pageInfo.setList(reports);

		return ResultVO.OK(isPage ? pageInfo : reports);
	}

	/**
	 * 查询申请单相关信息
	 *
	 * @param applicationId 申请单ID
	 * @return 结果
	 */
	private ApplicationInfoA searchApplicationInfoById(Integer applicationId) {
		if (applicationId == null) {
			return null;
		}
		ApplicationInfoA applicationInfoA = new ApplicationInfoA();

		Application application = applicationService.get(applicationId);

		if (application != null) {
			Integer userId = application.getUserId();
			User user = userService.get(userId);

			List<ApplicationManager> applicationManagers = applicationManagerService.getByApplicationId(applicationId);
			List<ApplicationKeeper> applicationKeepers = applicationKeeperService.getByApplication(applicationId);
			List<ApplicationAuditor> applicationAuditors = applicationAuditorService.getByApplication(applicationId);
			applicationInfoA.setUser(user);
			applicationInfoA.setApplicationManagers(applicationManagers);
			applicationInfoA.setApplicationKeepers(applicationKeepers);
			applicationInfoA.setApplicationAuditors(applicationAuditors);
		}
		applicationInfoA.setApplicationId(applicationId);
		applicationInfoA.setApplication(application);
		return applicationInfoA;
	}

	@ApiOperation(value = "查询报表列表记录", notes = "查询报表列表记录", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageNum", value = "当前页(默认:1)", dataType = "int"),
			@ApiImplicitParam(name = "pageSize", value = "每页数(默认:10)", dataType = "int"),
			@ApiImplicitParam(name = "page", value = "是否分页", dataType = "boolean", defaultValue = "true")
	})
	@GetMapping("/getReport")
	public ResultVO getReport(@RequestParam(value = "page", required = false, defaultValue = "true") boolean isPage) {
		UserInfo userInfo = getUserInfo();
		List<ReportEntity> reportEntities = service.getReportEntitiesByUser(userInfo.getId());

		/*组装报表请求URL*/
		if (reportEntities != null && reportEntities.size() > 0) {
			for (ReportEntity re : reportEntities) {
				String fileUrl = re.getFileUrl();
				String host = re.getHost();
				if (StringUtils.isNoneBlank(fileUrl, host)) {
					//该文件在备份机上
					fileUrl = "http://" + host + ":" + CommonUtils.properties.getFile().getPort() + fileUrl;
					re.setFileUrl(fileUrl);
				}
			}
		}

		return ResultVO.Page(reportEntities, isPage);
	}


	@ApiOperation(value = "查询导出记录列表", notes = "申请报表", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "start", value = "开始时间(yyyy-MM-dd HH:mm:ss)", dataType = "int"),
			@ApiImplicitParam(name = "end", value = "结束时间(yyyy-MM-dd HH:mm:ss)", dataType = "int"),
			@ApiImplicitParam(name = "pageNum", value = "当前页(默认:1)", dataType = "int"),
			@ApiImplicitParam(name = "pageSize", value = "每页数(默认:10)", dataType = "int"),
			@ApiImplicitParam(name = "page", value = "是否分页", dataType = "boolean", defaultValue = "true")
	})
	@GetMapping("/applicationReportList")
	public ResultVO applicationReportList(@RequestParam(value = "start") String start,
										  @RequestParam(value = "end") String end,
										  @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
										  @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
										  @RequestParam(value = "page", required = false, defaultValue = "true") boolean isPage) {
		/*
		 * 属主：允许查看集团所有记录
		 * 非属主：仅允许查看个人使用记录
		 */
		UserInfo userInfo = getUserInfo();
		boolean owner = userInfo.isOwner();
		Integer orgId = userInfo.getOrgId();
		Integer userId = userInfo.getId();

		/*分页查询*/
		if (isPage) {
			PageHelper.startPage(pageNum, pageSize);
		}
		List<SealRecordInfo> sealRecordInfos = sealRecordInfoService.getReportList(owner, orgId, userId, start, end);
		if (sealRecordInfos == null || sealRecordInfos.isEmpty()) {
			return ResultVO.OK();
		}

		PageInfo pageInfo = new PageInfo(sealRecordInfos);

		/*组装前端需要的参数*/
		List<SealRecordInfoReport> reports = new ArrayList<>(sealRecordInfos.size());
		for (SealRecordInfo sealRecordInfo : sealRecordInfos) {

			/*申请单相关*/
			Integer applicationId = sealRecordInfo.getApplicationId();
			Application application = applicationService.get(applicationId);

			/*用印异常相关*/
			Integer sealRecordInfoId = sealRecordInfo.getId();
			List<ErrorType> errors = errorTypeService.getBySealRecordInfo(sealRecordInfoId);

			/*
			 * type 0:使用记录图片 1:审计图片 2:超出申请单次数图片 3:长按报警图片 4:拆卸警告图片
			 */

			//用印图片
			List<FileEntity> useUrls = stamperPictureService.getBySealRecordInfoAndType(sealRecordInfoId, Global.TYPE_NORMAL);

			//审计图片
			List<FileEntity> auditorUrls = stamperPictureService.getBySealRecordInfoAndType(sealRecordInfoId, Global.TYPE_AUDITOR);

			//超次图片
			List<FileEntity> excessUrls = stamperPictureService.getBySealRecordInfoAndType(sealRecordInfoId, Global.TYPE_OVERTIMES);

			//超时图片
			List<FileEntity> timeoutUrls = stamperPictureService.getBySealRecordInfoAndType(sealRecordInfoId, Global.TYPE_TIMEOUT);

			//追加图片
			List<FileEntity> replenishUrls = stamperPictureService.getBySealRecordInfoAndType(sealRecordInfoId, Global.TYPE_REPLENISH);

			SealRecordInfoReport report = new SealRecordInfoReport(sealRecordInfo, application, errors, useUrls, auditorUrls, excessUrls, timeoutUrls, replenishUrls);
			reports.add(report);
		}

		pageInfo.setList(reports);
		return ResultVO.OK(pageInfo);
	}

	@ApiOperation(value = "申请报表", notes = "申请报表", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageNum", value = "当前页(默认:1)", dataType = "int"),
			@ApiImplicitParam(name = "pageSize", value = "每页数(默认:10)", dataType = "int"),
			@ApiImplicitParam(name = "page", value = "是否分页", dataType = "boolean", defaultValue = "true")
	})
	@WebLogger("申请报表")
	@PostMapping(value = "/applicationReport")
	public ResultVO applicationReport(Integer infoId, Integer orgId, List<Integer> departmentIds,
									  Integer searchUserId, String searchUserName, Integer searchClient,
									  List<Integer> applicationId, List<Integer> signetId, Date[] dates,
									  String title, String signetName, Integer deviceType, String userName,
									  Integer error, Integer type, List<Integer> userId, Integer pageNum, Integer pageSize, Boolean page) {
		UserInfo userInfo = getUserInfo();
		SealRecordInfoVoSearch search = new SealRecordInfoVoSearch();
		search.setInfoId(infoId);
		search.setOrgId(orgId);
		search.setDepartmentIds(departmentIds);
		search.setSearchUserId(searchUserId);
		search.setSearchClient(searchClient);
		search.setSearchUserName(searchUserName);
		search.setApplicationId(applicationId);
		search.setSignetId(signetId);
		search.setUserId(userId);
		search.setTitle(title);
		search.setSignetName(signetName);
		search.setDeviceType(deviceType);
		search.setUserName(userName);
		search.setError(error);
		search.setType(type);
		search.setDate(dates);
		search.setPage(page);
		search.setPageNum(pageNum);
		search.setPageSize(pageSize);

		// 参数校验:时间
		Date start = search.getStart();
		Date end = search.getEnd();
		if (start != null
				&& end != null
				&& (start.getTime() - end.getTime() > 0)) {
			return ResultVO.FAIL("日期时间选择有误");
		}

		/*
		 * 参数校验：印章
		 */
		if (StringUtils.isNotBlank(signetName)) {
			if (EmojiFilter.containsEmoji(signetName)) {
				return ResultVO.FAIL("印章名称不能包含特殊字符");
			}
			//如果是属主，跳过
			if (!userInfo.isOwner()) {
				//如果该用户没有该印章使用记录，直接返回空
				int count = sealRecordInfoService.getCountByUserAndSignetName(userInfo.getOrgId(), userInfo.getId(), signetName);
				if (count == 0) {
					return ResultVO.OK();
				}
			}
		}

		/*
		 * 参数校验：申请单
		 */
		if (StringUtils.isNotBlank(title)) {
			if (EmojiFilter.containsEmoji(title)) {
				return ResultVO.FAIL("申请单标题不能包含特殊字符");
			}
			//如果是属主，跳过
			if (!userInfo.isOwner()) {
				//如果该用户没有该申请单,直接返回空
				int count = applicationService.getCountByUserAndTitle(userInfo.getOrgId(), userInfo.getId(), title);
				if (count == 0) {
					return ResultVO.OK();
				}
			}
		}

		/*查询申请频率*/
		String key = RedisGlobal.REPORT_USER + userInfo.getId();
		Set<String> addApps = redisUtil.keys(key + "*");
		if (addApps != null && addApps.size() > 0) {
			if (addApps.size() > properties.getReportMinute()) {
				return ResultVO.FAIL("申请频率过高，请稍后重试");
			}
		}

		/*创建报表文件*/
		Date date = new Date();
		String reportPath = CommonUtils.getProperties().getFile().getFilePath() + File.separator + "report" + File.separator + userInfo.getOrgId() + File.separator;
		File path = new File(reportPath);
		if (!path.exists()) {
			path.mkdirs();
		}

		String fileName = date.getTime() + ".xlsx";
		File file = new File(reportPath, fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return ResultVO.FAIL("报表创建失败");
			}
		}

		try (OutputStream out = new FileOutputStream(file);
			 InputStream in = ReportController.class.getClassLoader().getResourceAsStream("rt.xlsx")) {
			if (in == null) {
				throw new RuntimeException();
			}
			FileCopyUtils.copy(in, out);
		} catch (IOException e) {
			e.printStackTrace();
			return ResultVO.FAIL("拷贝报表模板失败");
		}

		/*创建报表记录*/
		Report report = new Report();
		report.setOrgId(userInfo.getOrgId());
		report.setUserId(userInfo.getId());
		report.setStatus(0);
		report.setFileName(fileName);
		report.setRestrict(JSONObject.toJSONString(search));
		report.setAbsolutePath(file.getAbsolutePath());
		report.setRelativePath(File.separator + report.getAbsolutePath().substring(report.getAbsolutePath().indexOf("upload")));
		report.setHost(properties.getFile().getHost());
		service.add(report);

		/*加入任务列表：异步处理*/
		reportAsyncService.downloadReport(search, userInfo, report);

		/*记录申请频率*/
		redisUtil.set(key + System.currentTimeMillis(), report.getId(), RedisGlobal.REPORT_USER_TIMEOUT);

		return ResultVO.OK("申请成功");
	}

	/**
	 * 删除报表
	 */
	@RequestMapping("/del")
	public ResultVO del(@RequestParam("id") Integer id) {
		Report report = service.get(id);
		if (report != null) {
			//只能删除自己的报表
			UserToken token = getToken();
			if (token.getOrgId().intValue() != report.getOrgId()
					|| token.getUserId().intValue() != report.getUserId()) {
				return ResultVO.FAIL(Code.FAIL403);
			}

			String absolutePath = report.getAbsolutePath();
			File file = null;
			try {
				file = new File(absolutePath);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (file != null && file.exists()) {
					boolean hasDelete = file.delete();
					if (!hasDelete) {
						log.error("文件删除有误");
					}
				}

			}

			service.del(report);
			return ResultVO.OK("删除成功");
		}
		return ResultVO.FAIL("该记录不存在");
	}

	@ApiOperation(value = "申请单数量、印章数量、使用次数 数据统计", notes = "申请单数量、印章数量、使用次数 数据统计", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "departmentId", value = "组织ID", dataType = "int")
	})
	@RequestMapping("/getStatistics")
	public ResultVO getStatistics(@RequestParam(value = "departmentId", required = false) Integer departmentId) {
		UserInfo userInfo = getUserInfo();
		/*
		 * 参数校验
		 */
		if (departmentId != null) {
			Department department = departmentService.get(departmentId);
			if (department == null || !CommonUtils.isEquals(userInfo.getOrgId(), department.getOrgId())) {
				return ResultVO.FAIL("该组织不存在");
			}
			Integer level = department.getLevel();
			Integer type = department.getType();
			if ((level != null && level == 0)
					|| (type != null && type == 2)) {
				departmentId = null;
			}
		}

		/*
		 * 解析参数：要搜索的组织ID列表
		 */
		List<Integer> departmentIds = null;
		if (departmentId != null) {
			departmentIds = departmentService.getChildrenIdsByOrgAndParentAndType(userInfo.getOrgId(), departmentId, null);
			departmentIds.add(departmentId);
		}

		Map<String, Integer> res = service.getStatistics(userInfo.getOrgId(), departmentIds);

		return ResultVO.OK(res);
	}

	@ApiOperation(value = "地图数据", notes = "地图数据", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "departmentId", value = "组织ID", dataType = "int"),
			@ApiImplicitParam(name = "province", value = "要查询的省", dataType = "string"),
			@ApiImplicitParam(name = "city", value = "要查询的市", dataType = "string"),
			@ApiImplicitParam(name = "start", value = "开始时间", dataType = "date"),
			@ApiImplicitParam(name = "end", value = "结束时间", dataType = "date")
	})
	@GetMapping("/getMapChart")
	public ResultVO getMapChart(@RequestParam(value = "departmentId", required = false) Integer departmentId,
								@RequestParam(value = "province", required = false) String province,
								@RequestParam(value = "city", required = false) String city,
								@RequestParam(value = "start", required = false) Date start,
								@RequestParam(value = "end", required = false) Date end) {
		UserInfo userInfo = getUserInfo();

		/*
		 * 参数校验
		 */
		if (departmentId != null) {
			Department department = departmentService.get(departmentId);
			if (department == null || department.getId() == null) {
				return ResultVO.FAIL("该组织不存在");
			}
			Integer level = department.getLevel();
			Integer type = department.getType();
			if ((level != null && level == 0)
					|| (type != null && type == 2)) {
				departmentId = null;
			}
		}

		/*
		 * 参数校验：时间区域
		 */
		if (start != null && end != null) {
			if (end.getTime() - start.getTime() < 0) {
				return ResultVO.FAIL("时间区域选择有误");
			}
		}

		Map<String, Object> res = service.getMapChart(userInfo, departmentId, province, city, start, end);
		return ResultVO.OK(res);
	}

	@ApiOperation(value = "柱状图数据 各印章使用总次数", notes = "柱状图数据 各印章使用总次数", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "departmentId", value = "组织ID", dataType = "int"),
			@ApiImplicitParam(name = "start", value = "开始时间", dataType = "date"),
			@ApiImplicitParam(name = "end", value = "结束时间", dataType = "date")
	})
	@GetMapping("/getHistogram")
	public ResultVO getHistogram(@RequestParam(value = "departmentId", required = false) Integer departmentId,
								 @RequestParam(value = "start", required = false) Date start,
								 @RequestParam(value = "end", required = false) Date end) {

		UserInfo userInfo = getUserInfo();

		/*
		 * 参数校验
		 */
		if (departmentId != null) {
			Department department = departmentService.get(departmentId);
			if (department == null || department.getId() == null) {
				return ResultVO.FAIL("该组织不存在");
			}
			Integer level = department.getLevel();
			Integer type = department.getType();
			if ((level != null && level == 0)
					|| (type != null && type == 2)) {
				departmentId = null;
			}
		}

		/*
		 * 参数校验：时间区域
		 */
		if (start != null && end != null) {
			if (end.getTime() - start.getTime() < 0) {
				return ResultVO.FAIL("时间区域选择有误");
			}
		}

		Map<String, Object> res = service.getHistogram(userInfo, departmentId, start, end);
		return ResultVO.OK(res);
	}

	/**
	 * 公司  异常/正常/警告 统计
	 */
	@ApiOperation(value = "饼状图 正常、异常、警告数据统计", notes = "饼状图 正常、异常、警告数据统计", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "departmentId", value = "组织ID", dataType = "int"),
			@ApiImplicitParam(name = "start", value = "开始时间", dataType = "date"),
			@ApiImplicitParam(name = "end", value = "结束时间", dataType = "date")
	})
	@GetMapping("/getErrorOrNormal")
	public ResultVO getErrorOrNormal(@RequestParam(value = "departmentId", required = false) Integer departmentId,
									 @RequestParam(value = "start", required = false) Date start,
									 @RequestParam(value = "end", required = false) Date end) {

		UserInfo userInfo = getUserInfo();

		/*
		 * 参数校验
		 */
		if (departmentId != null) {
			Department department = departmentService.get(departmentId);
			if (department == null || department.getId() == null) {
				return ResultVO.FAIL("该组织不存在");
			}
			Integer level = department.getLevel();
			Integer type = department.getType();
			if ((level != null && level == 0)
					|| (type != null && type == 2)) {
				departmentId = null;
			}
		}

		/*
		 * 参数校验：时间区域
		 */
		if (start != null && end != null) {
			if (end.getTime() - start.getTime() < 0) {
				return ResultVO.FAIL("时间区域选择有误");
			}
		}

		List<StatusEntity> statusEntities = service.getErrorOrNormal(userInfo, departmentId, start, end);

		//填充假数据，防止前端不能展示
		if (statusEntities != null && !statusEntities.isEmpty()) {
			List<Integer> errnums = new ArrayList<>(Arrays.asList(-1, 0, 1));
			for (StatusEntity statusEntity : statusEntities) {
				Integer error = statusEntity.getError();
				errnums.remove(error);
			}
			if (!errnums.isEmpty()) {
				for (Integer errnum : errnums) {
					statusEntities.add(new StatusEntity(errnum, 0));
				}
			}
		}

		return ResultVO.OK(statusEntities);
	}


	@ApiOperation(value = "查询印章在线数量", notes = "查询印章在线数量", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "departmentId", value = "组织ID", dataType = "int")
	})
	@GetMapping("/getTotalByOnline")
	public ResultVO getTotalByOnline(@RequestParam("departmentId") Integer departmentId) {
		UserInfo userInfo = getUserInfo();

		// 参数校验
		if (departmentId != null) {
			Department department = departmentService.get(departmentId);
			if (department == null || !CommonUtils.isEquals(department.getOrgId(), userInfo.getOrgId())) {
				return ResultVO.FAIL("该组织不存在");
			}
			Integer level = department.getLevel();
			Integer type = department.getType();
			if ((level != null && level == 0) || (type != null && type == 2)) {
				departmentId = null;
			}
		}

		Map<String, Integer> res = service.getgetTotalByOnline(userInfo, departmentId);
		return ResultVO.OK(res);
	}
}
