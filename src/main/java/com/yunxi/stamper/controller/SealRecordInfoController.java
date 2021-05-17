package com.yunxi.stamper.controller;


import com.github.pagehelper.PageHelper;
import com.yunxi.common.page.PageHelperUtil;
import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.gk.GKQSSUtil;
import com.yunxi.stamper.commons.gk.ResponseEntity;
import com.yunxi.stamper.commons.jwt.UserToken;
import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.other.EmojiFilter;
import com.yunxi.stamper.commons.other.Global;
import com.yunxi.stamper.commons.response.Code;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.*;
import com.yunxi.stamper.entityVo.*;
import com.yunxi.stamper.service.*;
import com.yunxi.stamper.sys.aop.annotaion.WebLogger;
import com.yunxi.stamper.sys.lock.LockGlobal;
import com.zengtengpeng.annotation.Lock;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/3 0003 3:40
 */
@Slf4j
@Api(tags = "使用记录相关")
@RestController
@RequestMapping(value = {"/device/sealRecordInfo", "/device/seal_record_info"}, method = {RequestMethod.POST, RequestMethod.GET})
public class SealRecordInfoController extends BaseController {

	@Autowired
	private ApplicationKeeperService applicationKeeperService;
	@Autowired
	private SealRecordInfoAsyncService sealRecordInfoAsyncService;
	@Autowired
	private ApplicationDeviceService applicationDeviceService;
	@Autowired
	private ApplicationManagerService applicationManagerService;
	@Autowired
	private StamperPictureService stamperPictureService;
	@Autowired
	private AttachmentService attachmentService;
	@Autowired
	private ApplicationService applicationService;
	@Autowired
	private ErrorTypeService errorTypeService;
	@Autowired
	private FileInfoService fileInfoService;
	@Autowired
	private SealRecordInfoService service;
	@Autowired
	private SignetService signetService;

	@ApiOperation(value = "标记使用记录个性化备注信息", notes = "允许管章人对使用记录进行备注个性化信息", httpMethod = "POST")
	@PostMapping("/customRemark")
	public ResultVO customRemark(@RequestParam Integer infoId, @RequestParam String remark) {
		//参数检查
		if (StringUtils.isNotBlank(remark) && remark.length() > 50) {
			return ResultVO.FAIL("备注字数超过限制长度(50)");
		}

		//记录检查
		SealRecordInfo sealRecordInfo = service.get(infoId);
		if (sealRecordInfo == null) {
			return ResultVO.FAIL(Code.FAIL400);
		}

		//权限校验
		UserInfo userInfo = getUserInfo();
		Integer deviceId = sealRecordInfo.getDeviceId();
		Signet device = signetService.get(deviceId);
		if (device == null || !Objects.equals(userInfo.getOrgId(), device.getOrgId())) {
			return ResultVO.FAIL("无权限操作");
		}
		if (!Objects.equals(userInfo.getId(), device.getAuditorId())) {
			return ResultVO.FAIL("无权限操作");
		}

		service.updateRemark(infoId, remark);
		return ResultVO.OK();
	}

	@ApiOperation(value = "追加补拍", notes = "追加补拍，不限时间，不限张数，只允许用印人以及审计人追加补拍", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "fileId", value = "图片ID", dataType = "String", required = true),
			@ApiImplicitParam(name = "infoId", value = "记录ID", dataType = "int", required = true)
	})
	@PostMapping("/replenishInfo")
	public ResultVO replenishInfo(@RequestParam String fileId, @RequestParam Integer infoId) {
		UserInfo userInfo = getUserInfo();

		//参数校验
		FileInfo fileInfo = fileInfoService.get(fileId);
		if (fileInfo == null) {
			return ResultVO.FAIL("文件不存在");
		}
		if (fileInfo.getStatus() != 0) {
			return ResultVO.FAIL("只能追加图片");
		}
		SealRecordInfo sealRecordInfo = service.get(infoId);
		if (sealRecordInfo == null) {
			return ResultVO.FAIL("记录不存在");
		}

		//权限校验
		if (sealRecordInfo.getOrgId() != userInfo.getOrgId().intValue()) {//非同一个集团无法追加
			return ResultVO.FAIL("无权限(组织不匹配)");
		}
		Signet device = signetService.get(sealRecordInfo.getDeviceId());//管章人
		Integer auditorId = device.getAuditorId();
		Integer userId = sealRecordInfo.getUserId();
		if (userInfo.isOwner() || Objects.equals(auditorId, userInfo.getId()) || Objects.equals(userId, userInfo.getId())) {//属主,用印人,审计人 允许追加图片
			/*
				为该使用记录绑定新类型(追加拍照)的图片
			 */
			StamperPicture stamperPicture = new StamperPicture();
			stamperPicture.setSignetId(sealRecordInfo.getDeviceId());
			stamperPicture.setFileName(fileInfo.getOriginalName());
			stamperPicture.setFileId(fileInfo.getId());
			stamperPicture.setType(Global.TYPE_REPLENISH);
			stamperPicture.setStatus(0);
			stamperPicture.setHash(fileInfo.getHash());
			stamperPicture.setInfoId(sealRecordInfo.getId());
			stamperPicture.setCreateBy(userInfo.getId());
			stamperPictureService.add(stamperPicture);

			return ResultVO.OK();
		} else {
			return ResultVO.FAIL("无权限(角色不匹配)");
		}
	}

	/**
	 * 查看申请单使用记录
	 */
	@ApiOperation(value = "查看申请单使用记录", notes = "查看申请单使用记录", httpMethod = "GET")
	@RequestMapping("/getByApplicationId")
	public ResultVO getByApplicationId(String userName, Integer signetId, Integer applicationId, Integer type, Integer error,
									   Integer useCount, String location, Date[] date) {
		InfoSearchVo vo = new InfoSearchVo();
		vo.setUserName(userName);
		vo.setSignetId(signetId);
		vo.setApplicationId(applicationId);
		vo.setType(type);
		vo.setError(error);
		vo.setUseCount(useCount);
		vo.setLocation(location);
		vo.setDate(date);

		UserInfo userInfo = getUserInfo();
		vo.setUserInfo(userInfo);
		boolean page = setPage(vo.isPage(), vo.getPageSize(), vo.getPageNum());
		List<SealRecordInfo> sealRecordInfos = service.getByApplication(vo);
		if (sealRecordInfos != null && sealRecordInfos.size() > 0) {
			return ResultVO.Page(sealRecordInfos, page);
		} else {
			return ResultVO.FAIL("该申请单暂无使用记录");
		}
	}


	/**
	 * 查看申请单使用记录(APP专用)
	 */
	@ApiOperation(value = "查询可绑定的申请单列表", notes = "查询可绑定的申请单列表", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "applicationId", value = "申请单ID", dataType = "int", required = true),
			@ApiImplicitParam(name = "pageNum", value = "当前页(默认:1)", dataType = "int"),
			@ApiImplicitParam(name = "pageSize", value = "每页数(默认:10)", dataType = "int"),
			@ApiImplicitParam(name = "page", value = "是否分页", dataType = "boolean", defaultValue = "true")
	})
	@GetMapping("/getVoByApplicationId")
	public ResultVO getVoByApplicationId(@RequestParam("applicationId") Integer applicationId,
										 @RequestParam(value = "page", required = false, defaultValue = "true") boolean isPage) {
		/*
		  参数校验
		 */
		if (applicationId == null) {
			return ResultVO.FAIL("请求参数有误");
		}
		UserInfo userInfo = getUserInfo();
		Application application = applicationService.get(applicationId);
		if (application == null || application.getOrgId().intValue() != userInfo.getOrgId()) {
			return ResultVO.FAIL("申请单不存在");
		}

		/*
		  开始查询
		 */
		List<SealRecordInfoVoApp> vos = service.getVoByApplication(applicationId, userInfo.getOrgId());

		/*
		  组装返回值参数
		 */
		if (vos != null && vos.size() > 0) {
			for (SealRecordInfoVoApp vo : vos) {
				Integer id = vo.getId();
				//使用记录图片
				List<FileEntity> useImgs = stamperPictureService.getBySealRecordInfoAndType(id, Global.TYPE_NORMAL);
				if (useImgs == null || useImgs.isEmpty()) {
					//超出申请单次数图片
					useImgs = stamperPictureService.getBySealRecordInfoAndType(id, Global.TYPE_OVERTIMES);
				}
				vo.setUseUrls(useImgs);

				//审计图片
				List<FileEntity> auditorImgs = stamperPictureService.getBySealRecordInfoAndType(id, Global.TYPE_AUDITOR);
				vo.setAuditorUrls(auditorImgs);

				//警告照片
				List<FileEntity> errorUrls = stamperPictureService.getBySealRecordInfoAndType(id, Global.TYPE_TIMEOUT);
				vo.setWarnUrls(errorUrls);


				//追加图片
				List<FileEntity> replenishList = stamperPictureService.getBySealRecordInfoAndType(id, Global.TYPE_REPLENISH);
				vo.setReplenishList(replenishList);
			}
		}
		return ResultVO.Page(vos, isPage);
	}

	@ApiOperation(value = "APP查询指定使用记录", notes = "APP查询指定使用记录", httpMethod = "GET")
	@ApiImplicitParam(name = "infoId", value = "使用记录ID", dataType = "int", required = true)
	@GetMapping("/searchNextAndPreInfoByKeyword")
	public ResultVO searchNextAndPreInfoByKeyword(@RequestParam("infoId") Integer infoId) {
		SealRecordInfo sealRecordInfo = service.get(infoId);
		if (sealRecordInfo == null) {
			return null;
		}
		SealRecordInfoNextAndPre res = new SealRecordInfoNextAndPre();
		BeanUtils.copyProperties(sealRecordInfo, res);

		//盖章照片
		res.setUseUrl(stamperPictureService.getBySealRecordInfoAndType(res.getId(), Global.TYPE_NORMAL));

		//审计照片
		res.setAuditorUrls(stamperPictureService.getBySealRecordInfoAndType(res.getId(), Global.TYPE_AUDITOR));

		//超时照片
		res.setWarnUrls(stamperPictureService.getBySealRecordInfoAndType(res.getId(), Global.TYPE_TIMEOUT));

		//超次照片
		res.setNumUrls(stamperPictureService.getBySealRecordInfoAndType(res.getId(), Global.TYPE_OVERTIMES));

		//超次照片
		res.setReplenishUrls(stamperPictureService.getBySealRecordInfoAndType(res.getId(), Global.TYPE_REPLENISH));

		//拼接异常信息
		Integer error = res.getError();
		if (error != null && error != 0) {
			String errorInfo = errorTypeService.getBySealRecordInfoId(infoId);
			res.setException(errorInfo);
		}
		return ResultVO.OK(res);
	}

	/**
	 * 指纹模式 盖章 记录上传【指纹模式】
	 * 返回值 0:成功  1:失败  2:图片重复 3:出错
	 * 记录类型type  0:标准版 1:量子版 2:简易版
	 *
	 * @param info 记录信息
	 * @return 结果
	 */
	@ApiOperation(value = "指纹模式 盖章 记录上传", notes = "指纹模式 盖章 记录上传", httpMethod = "POST")
	@Lock(keys = "#info.count + '_' + #info.uuid", keyConstant = LockGlobal.add_info)
	@RequestMapping("/addEasyInfo")
	public String addEasyInfo(Integer deviceID, String uuid, String identity, Integer picUseId, Integer count, Integer applicationID,
							  Integer isAudit, String time, Integer alarm, String fileName, String fileupload, Integer encryptionType,
							  Integer mqInfoType, Integer deviceMode) {
		SealRecordInfoVoUpload info = new SealRecordInfoVoUpload();
		info.setDeviceID(deviceID);
		info.setDeviceId(deviceID);
		info.setUuid(uuid);
		info.setIdentity(identity);
		info.setPicUseId(picUseId);
		info.setCount(count);
		info.setApplicationID(applicationID);
		info.setApplicationId(applicationID);
		info.setIsAudit(isAudit);
		info.setTime(time);
		info.setAlarm(alarm);
		info.setFileName(fileName);
		info.setFileupload(fileupload);
		info.setEncryptionType(encryptionType);
		info.setMqInfoType(mqInfoType);
		info.setDeviceMode(deviceMode);

		logPrintInfo(info);
		if (info == null) {
			return "1";
		}
		//用印次数
		if (count == null || count == 0) {
			return "0";
		}

		//查询对应的设备信息
		Signet signet = signetService.get(info.getDeviceID());
		if (signet == null) {
			signet = signetService.getByUUID(info.getUuid());
		}
		if (signet == null) {
			return "1";
		}

		info.setOrgId(signet.getOrgId());
		info.setDeviceName(signet.getName());

		//0:标准版 1:量子版 2:简易版
		Integer type = info.getType();
		if (type == 0) {
			info.setType(2);
		} else if (type == 1) {
			info.setType(3);
		}

		try {
			//存储图片
			StamperPicture stamperPicture = fileInfoService.saveFile(info, signet, type);
			if (info.getDeviceMode() == 9) {
				sealRecordInfoAsyncService.addNoCameraInfoWithEasy(info, signet);
				log.info("上传成功(指纹-静默) 设备:{} 次数:{} 用印人:{}", signet.getId(), info.getCount(), info.getIdentity());
			} else if (alarm == null || alarm == 0) {
				sealRecordInfoAsyncService.addEasyInfo(info, stamperPicture, signet);//异步处理一下 正常记录
				log.info("上传成功(指纹) 设备:{} 次数:{} 用印人:{}", signet.getId(), info.getCount(), info.getIdentity());
			} else if (alarm == 1) {
				sealRecordInfoAsyncService.addWarnExcessTimes(info, stamperPicture, signet);//异步处理一下 超出申请单次数报警
				log.info("上传成功(指纹-超次) 设备:{} 次数:{} 用印人:{}", signet.getId(), info.getCount(), info.getIdentity());
			} else if (alarm == 2) {
				sealRecordInfoAsyncService.addWarnDemolish(info, stamperPicture, signet);//异步处理一下 防拆卸报警
				log.info("上传成功(指纹-拆卸) 设备:{} 次数:{} 用印人:{}", signet.getId(), info.getCount(), info.getIdentity());
			} else if (alarm == 3) {
				sealRecordInfoAsyncService.addWarnTimeoutPressing(info, stamperPicture, signet);//异步处理一下 超时按压报警
				log.info("上传成功(指纹-超时) 设备:{} 次数:{} 用印人:{}", signet.getId(), info.getCount(), info.getIdentity());
			} else if (alarm == 4) {
				sealRecordInfoAsyncService.addPasswordInfo(info, stamperPicture, signet);//异步处理一下 超时按压报警
				log.info("上传成功(指纹-密码) 设备:{} 次数:{} 用印人:{}", signet.getId(), info.getCount(), info.getIdentity());
			}
			return "0";
		} catch (Exception e) {
			log.error("指纹记录上传-异常 info:{}", CommonUtils.objJsonWithIgnoreFiled(info, "fileupload"), e);
			return "3";
		}
	}

	/**
	 * 将设备上传的记录&图片信息打印日志记录下来
	 *
	 * @param info 记录信息
	 */
	private void logPrintInfo(SealRecordInfoVoUpload info) {
		//记录参数
		String params;
		Integer fileSize = null;
		String fileName = null;
		Integer count = null;
		try {
			//图片大小
			fileSize = StringUtils.isBlank(info.getFileupload()) ? 0 : info.getFileupload().length();
			//图片名称
			fileName = info.getFileName();
			//次数
			count = info.getCount();

			params = CommonUtils.objJsonWithIgnoreFiled(info, "fileupload");
		} catch (Exception e) {
			e.printStackTrace();
			params = info.toString();
		}

		log.info("记录上传\tcount:{}\tfilename:{}\tfilesize:{}\tother:{}", count, fileName, fileSize, params);
	}

	/**
	 * 审计 照片上传
	 * 返回值 0:成功  1:失败  2:图片重复 3:出错
	 *
	 * @return 结果
	 */
	@ApiOperation(value = "审计 记录上传", notes = "审计 记录上传", httpMethod = "POST")
	@Lock(keys = "#info.count + '_' + #info.uuid", keyConstant = LockGlobal.add_info)
	@RequestMapping("/addAuditInfo")
	public String addAuditInfo(Integer deviceID, String uuid, String identity, Integer picUseId, Integer count, Integer applicationID,
							   Integer isAudit, String time, Integer alarm, String fileName, String fileupload, Integer encryptionType,
							   Integer mqInfoType, Integer deviceMode) {
		SealRecordInfoVoUpload info = new SealRecordInfoVoUpload();
		info.setDeviceID(deviceID);
		info.setDeviceId(deviceID);
		info.setUuid(uuid);
		info.setIdentity(identity);
		info.setPicUseId(picUseId);
		info.setCount(count);
		info.setApplicationID(applicationID);
		info.setApplicationId(applicationID);
		info.setIsAudit(isAudit);
		info.setTime(time);
		info.setAlarm(alarm);
		info.setFileName(fileName);
		info.setFileupload(fileupload);
		info.setEncryptionType(encryptionType);
		info.setMqInfoType(mqInfoType);
		info.setDeviceMode(deviceMode);

		logPrintInfo(info);
		if (info == null) {
			return "1";
		}

		if (count == null || count == 0) {
			return "0";
		}
		if (isIgnored(info)) {
			return "0";
		}

		/*
		  查询设备ID
		 */
		Integer signetId = info.getDeviceID();
		Signet signet = signetService.get(signetId);
		if (signet == null) {
			signet = signetService.getByUUID(info.getUuid());
		}
		if (signet == null) {
			return "1";
		}

		//0:标准版 1:量子版 2:简易版
		Integer type = info.getType();
		if (type == 0) {
			info.setType(0);
		} else if (type == 1) {
			info.setType(1);
		} else if (info.getAlarm() == 4 || info.getDeviceMode() == 9) {
			info.setType(4);//密码模式审计
		}

		try {
			StamperPicture stamperPicture = fileInfoService.saveFile(info, signet, type);
			if (stamperPicture != null) {
				stamperPicture.setType(1);
			}

			sealRecordInfoAsyncService.addAuditInfo(info, stamperPicture, signet);
			log.info("上传成功(审计) 设备{} 次数{} 用印人{}", signet.getId(), info.getCount(), info.getIdentity());
			return "0";

		} catch (Exception e) {
			log.error("审计记录上传-异常 info:{}", CommonUtils.objJsonWithIgnoreFiled(info, "fileupload"), e);
			return "3";
		}

	}

	/**
	 * 校验是否需要忽略本次审计(开机直接审计的需要忽略)
	 * 忽略条件: count==0
	 */
	private boolean isIgnored(SealRecordInfoVoUpload info) {
		Integer count = info.getCount();

		if (count == 0) {
			log.info("设备：{} 次数：{} 用印人：{} 本次记录已忽略", info.getDeviceID(), info.getCount(), info.getIdentity());
			return true;
		}
		return false;
	}

	/**
	 * 申请单模式 盖章 使用记录上传申请单模式
	 * 返回值 0:成功  1:失败  2:图片重复 3:出错
	 */
	@ApiOperation(value = "申请单模式 盖章 使用记录上传", notes = "申请单模式 盖章 使用记录上传", httpMethod = "POST")
	@Lock(keys = "#info.count + '_' + #info.uuid", keyConstant = LockGlobal.add_info)
	@RequestMapping("/addNormalInfo")
	public String addNormalInfo(Integer deviceID, String uuid, String identity, Integer picUseId, Integer count, Integer applicationID,
								Integer isAudit, String time, Integer alarm, String fileName, String fileupload, Integer encryptionType,
								Integer mqInfoType, Integer deviceMode) {
		SealRecordInfoVoUpload info = new SealRecordInfoVoUpload();
		info.setDeviceID(deviceID);
		info.setDeviceId(deviceID);
		info.setUuid(uuid);
		info.setIdentity(identity);
		info.setPicUseId(picUseId);
		info.setCount(count);
		info.setApplicationID(applicationID);
		info.setApplicationId(applicationID);
		info.setIsAudit(isAudit);
		info.setTime(time);
		info.setAlarm(alarm);
		info.setFileName(fileName);
		info.setFileupload(fileupload);
		info.setEncryptionType(encryptionType);
		info.setMqInfoType(mqInfoType);
		info.setDeviceMode(deviceMode);

		logPrintInfo(info);

		if (count == null || count == 0) {
			return "0";
		}

		//查询设备ID
		Integer signetId = info.getDeviceID();
		Signet signet = signetService.get(signetId);
		if (signet == null) {
			signet = signetService.getByUUID(info.getUuid());
		}
		if (signet == null) {
			return "1";
		}

		info.setOrgId(signet.getOrgId());
		//0:标准版 1:量子版 2:简易版  3:国科版
		Integer type = info.getType();
		if (type == 0) {
			info.setType(0);
		} else if (type == 1 || type == 3) {
			info.setType(1);
		}

		try {
			StamperPicture stamperPicture = fileInfoService.saveFile(info, signet, type);
			if (info.getDeviceMode() == 9) {
				sealRecordInfoAsyncService.addNoCameraInfo(info, stamperPicture, signet);
				log.info("上传成功(指纹-静默) 设备:{} 次数:{} 用印人:{}", signet.getId(), info.getCount(), info.getIdentity());

			} else if (alarm == null || alarm == 0) {
				sealRecordInfoAsyncService.addNormalInfo(info, stamperPicture, signet);//异步处理一下
				log.info("上传成功(申请单) 设备:{} 次数:{} 申请单:{} 用印人:{}", signet.getId(), info.getCount(), info.getApplicationID(), info.getIdentity());

				//同步申请单已使用次数
				if (info.getApplicationID() != null && !Objects.equals(info.getApplicationID(), 0)) {
					int localCount = service.getCountByApplication(info.getApplicationID());
					applicationService.synchApplicationInfo(signet.getId(), info.getApplicationID(), localCount);
				}

			} else if (alarm == 1) {
				sealRecordInfoAsyncService.addWarnExcessTimes(info, stamperPicture, signet);//异步处理一下 超出申请单次数报警
				log.info("上传成功(申请单-超次) 设备:{} 次数:{} 申请单:{} 用印人:{}", signet.getId(), info.getCount(), info.getApplicationID(), info.getIdentity());

			} else if (alarm == 2) {
				sealRecordInfoAsyncService.addWarnDemolish(info, stamperPicture, signet);//异步处理一下 拆卸报警
				log.info("上传成功(申请单-拆卸) 设备:{} 次数:{} 申请单:{} 用印人:{}", signet.getId(), info.getCount(), info.getApplicationID(), info.getIdentity());

			} else if (alarm == 3) {
				sealRecordInfoAsyncService.addWarnTimeoutPressing(info, stamperPicture, signet);//异步处理一下 超时按压报警
				log.info("上传成功(申请单-超时) 设备:{} 次数:{} 申请单:{} 用印人:{}", signet.getId(), info.getCount(), info.getApplicationID(), info.getIdentity());

			}

			return "0";

		} catch (Exception e) {
			log.error("申请记录上传-异常 info:{}", CommonUtils.objJsonWithIgnoreFiled(info, "fileupload"), e);
			return "3";
		}

	}

	@ApiOperation(value = "印章使用记录列表", notes = "查看印章的使用记录列表", httpMethod = "GET")
	@GetMapping("/getBySignetId")
	public ResultVO getBySignetId(@RequestParam("signetId") Integer signetId,
								  @RequestParam(value = "userName", required = false) String identity,
								  @RequestParam(value = "location", required = false) String location,
								  @RequestParam(value = "type", required = false) Integer infoType,
								  @RequestParam(value = "error", required = false) Integer infoError,
								  @RequestParam(value = "start", required = false) Date start,
								  @RequestParam(value = "end", required = false) Date end,
								  @RequestParam(value = "page", required = false, defaultValue = "true") boolean isPage) {

		//校验操作者权限
		UserToken token = getToken();

		//校验时间参数有效性
		if (start != null && end != null) {
			long times = end.getTime() - start.getTime();
			if (times < 0) {
				return ResultVO.FAIL("搜索条件有误【" + start + "-" + end + "】");
			}
		}

		//搜索使用记录列表-app
		boolean isApp = isApp();
		if (isApp) {
			return getBySignetIdForApp(token, signetId, isPage);
		}

		//搜索使用记录列表-web
		List<SealRecordInfoEntity> sealRecordInfoEntities = service.getBySignetId(token.getOrgId(), signetId, identity, location, infoType, infoError, start, end);
		return ResultVO.Page(sealRecordInfoEntities, isPage);
	}

	/**
	 * 手机端查询使用记录列表
	 *
	 * @param token    登录用户token实体
	 * @param signetId 设备ID
	 * @param isPage   是否分页
	 * @return 结果
	 */
	private ResultVO getBySignetIdForApp(UserToken token, Integer signetId, boolean isPage) {
		List<SealRecordInfoEntity> sealRecordInfos = service.getInfoBySignet(token.getOrgId(), signetId);

		if (sealRecordInfos == null || sealRecordInfos.isEmpty()) {
			return ResultVO.OK();
		}

		//取出记录ID
		List<Integer> infoIds = new ArrayList<>();
		sealRecordInfos.forEach(info ->
				infoIds.add(info.getId())
		);

		//查询记录异常信息
		List<ErrorType> errorTypes = errorTypeService.getBySealRecordInfoIds(infoIds);

		//查询记录文件相关信息
		List<PictureFileInfo> pictureFileInfos = stamperPictureService.getByInfoIds(infoIds);

		UserInfo userInfo = userInfoService.get(token.getUserId());

		//组装返回值
		for (SealRecordInfoEntity entity : sealRecordInfos) {
			Integer entityId = entity.getId();
			String errorMsg = entity.getErrorMsg();

			//解析异常提示信息
			if (errorTypes != null && !errorTypes.isEmpty()) {
				for (int j = 0; j < errorTypes.size(); j++) {
					ErrorType et = errorTypes.get(j);
					Integer sealRecordInfoId = et.getSealRecordInfoId();
					if (entityId.intValue() != sealRecordInfoId) {
						continue;
					}

					//组装异常提示
					String name = et.getName();
					String remark = et.getRemark();
					String tip = (name + " " + (remark == null ? "" : remark)).trim();    //name:错误  remark:驳回  ===> tip:错误 驳回
					if (StringUtils.isBlank(errorMsg)) {
						errorMsg = tip;
					} else if (!errorMsg.contains(tip)) {
						errorMsg = errorMsg + "," + tip;
					}

					//处理完后从集合中删除，缩短循环时间
					errorTypes.remove(j);
					j--;
				}

				entity.setErrorMsg(errorMsg);
			}

			//解析图片相关信息
			if (pictureFileInfos == null || pictureFileInfos.isEmpty()) {
				continue;
			}

			for (int j = 0; j < pictureFileInfos.size(); j++) {
				PictureFileInfo pictureFileInfo = pictureFileInfos.get(j);
				Integer infoId = pictureFileInfo.getInfoId();

				if (entityId.intValue() != infoId) {
					continue;
				}

				//不同类型记录组装
				Integer type = pictureFileInfo.getType();        //0:使用记录图片 1:审计图片 2:超出申请单次数图片 3:长按报警图片 4:拆卸警告图片

				List<FileEntity> fileEntities = null;

				if (type == Global.TYPE_NORMAL) {
					//使用记录图片
					fileEntities = entity.getUseInfos();
					if (fileEntities == null) {
						fileEntities = new ArrayList<>();
						entity.setUseInfos(fileEntities);
					}
				} else if (type == Global.TYPE_AUDITOR) {
					//审计记录图片
					fileEntities = entity.getAuditorInfos();
					if (fileEntities == null) {
						fileEntities = new ArrayList<>();
						entity.setAuditorInfos(fileEntities);
					}
				} else if (type == Global.TYPE_OVERTIMES) {
					//超次记录图片
					fileEntities = entity.getNumoutInfos();
					if (fileEntities == null) {
						fileEntities = new ArrayList<>();
						entity.setNumoutInfos(fileEntities);
					}
				} else if (type == Global.TYPE_TIMEOUT) {
					fileEntities = entity.getTimeoutInfos();
					if (fileEntities == null) {
						fileEntities = new ArrayList<>();
						entity.setTimeoutInfos(fileEntities);
					}
				} else if (type == Global.TYPE_REPLENISH) {
					fileEntities = entity.getReplenishInfos();
					if (fileEntities == null) {
						fileEntities = new ArrayList<>();
						entity.setReplenishInfos(fileEntities);
					}
				}

				if (fileEntities != null) {
					String secretKey = pictureFileInfo.getSecretKey();
					String keyIndex = pictureFileInfo.getKeyIndex();
					try {
						if (userInfo != null && StringUtils.isNoneBlank(secretKey, keyIndex)) {
							ResponseEntity responseEntity = GKQSSUtil.transCryption(keyIndex, userInfo.getPhone() + "", secretKey);
							if (responseEntity != null && StringUtils.isNotBlank(responseEntity.getMessage())) {
								secretKey = responseEntity.getValue();
								pictureFileInfo.setSecretKey(secretKey);
							}
						}
					} catch (Exception e) {
						log.error("出现异常 ", e);
					}

					fileEntities.add(new FileEntity(pictureFileInfo));
				}

				//处理完后从集合中删除，缩短循环时间
				pictureFileInfos.remove(j);
				j--;
			}
		}

		return ResultVO.Page(sealRecordInfos, isPage);
	}

	@ApiOperation(value = "查看印章使用记录详情", notes = "查看印章使用记录详情", httpMethod = "GET")
	@ApiImplicitParam(name = "id", value = "使用记录详情", dataType = "int")
	@GetMapping("/getById")
	public ResultVO getById(@RequestParam("id") Integer sealRecordInfoId) {
		UserInfo userInfo = getUserInfo();

		//校验使用记录有效性
		SealRecordInfo info = service.get(sealRecordInfoId);
		if (info == null) {
			return ResultVO.FAIL("使用记录不存在");
		}

		//校验操作人权限:集团属主、设备所属组织管理员、申请人、用印人、审批人、授权人、管章人、审计人有权限查看
		if (checkUserHasPermissionToSeeInfo(userInfo, info)) {
			SealRecordInfoVoSelect vo = getBySealRecordInfoId(info);
			return ResultVO.OK(vo);
		} else {
			return ResultVO.FAIL("无权限查看");
		}
	}

	/**
	 * 检查用户是否有权限查看使用记录的权限
	 *
	 * @param userInfo 用户信息
	 * @param info     记录信息
	 * @return true:有权限  false:无权限
	 */
	private boolean checkUserHasPermissionToSeeInfo(UserInfo userInfo, SealRecordInfo info) {
		//非同集团下的记录无权限查看
		if (userInfo == null || info == null || userInfo.getOrgId().intValue() != info.getOrgId()) {
			return false;
		}

		//集团属主有权限
		if (userInfo.isOwner()) {
			return true;
		}

		//用印人有有权限
		if (info.getUserId() != null && userInfo.getId().intValue() == info.getUserId()) {
			return true;
		}

		//印章授权人、审计人有权限
		Integer deviceId = info.getDeviceId();
		Signet signet = signetService.get(deviceId);
		if (userInfo.getId().intValue() == signet.getKeeperId() || userInfo.getId().intValue() == signet.getAuditorId()) {
			return true;
		}

		//印章组织管理员角色有权限
		if (userInfo.isAdmin()) {
			/*
			  示例：
			  印章所属组织为部门A
			  用户所属组织为公司S。A属于公司A下的部门，同时用户拥有管理员角色，则用户有权限查看该印章记录
			 */
			Integer departmentId = signet.getDepartmentId();
			if (userInfo.getVisualDepartmentIds().contains(departmentId)) {
				return true;
			}
		}

		//审批人有权限
		Integer applicationId = info.getApplicationId();
		Application application = applicationService.get(applicationId);
		if (application != null) {
			List<ApplicationManager> ams = applicationManagerService.getByApplicationId(applicationId);
			if (ams != null && !ams.isEmpty()) {
				for (ApplicationManager applicationManager : ams) {
					if (userInfo.getId().intValue() == applicationManager.getManagerId()) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * 组织前端需要的参数
	 *
	 * @param info 记录信息
	 * @return 结果
	 */
	private SealRecordInfoVoSelect getBySealRecordInfoId(SealRecordInfo info) {
		SealRecordInfoVoSelect vo = new SealRecordInfoVoSelect();
		vo.setId(info.getId());
		vo.setDeviceName(info.getDeviceName());

		/*
		  申请单相关
		 */
		Integer applicationId = info.getApplicationId();
		if (applicationId != null && applicationId != 0) {
			vo.setApplicationId(applicationId);
			Application application = applicationService.get(applicationId);
			if (application != null) {
				vo.setTitle(application.getTitle());
				vo.setContent(application.getContent());
				vo.setUserName(application.getUserName());
				List<String> fileIds = attachmentService.getFileIdsByApplicationId(applicationId);
				if (fileIds != null && fileIds.size() > 0) {
					List<FileEntity> imgs = new ArrayList<>();
					List<FileEntity> files = new ArrayList<>();

					for (String fileId : fileIds) {
						com.yunxi.stamper.entityVo.FileEntity fileEntity = fileInfoService.getReduceImgURLByFileId(fileId);
						if (fileEntity != null) {
							int type = fileEntity.getType();
							if (type == 0) {
								imgs.add(fileEntity);
							} else {
								files.add(fileEntity);
							}
						}
					}
					vo.setApplicationUrls(imgs);//申请照片
					vo.setApplicationAtts(files);//申请附件
				}
			}
		}

		//记录备注信息
		vo.setRemark(info.getRemark());

		/*
		  警告/正常/异常
		 */
		vo.setError(info.getError());
		String errorName = errorTypeService.getBySealRecordInfoId(info.getId());
		vo.setErrorName(errorName);

		//用印人名称
		vo.setIdentity(info.getUserName());

		//使用方式
		vo.setType(info.getType());

		//用章时间
		vo.setTime(info.getRealTime());

		//用章次数
		vo.setCount(info.getUseCount());

		//用章地址
		vo.setLocation(info.getLocation());

		List<FileEntity> useUrls = stamperPictureService.getBySealRecordInfoAndType(info.getId(), Global.TYPE_NORMAL);//盖章照片
		vo.setUseUrls(useUrls);

		List<FileEntity> overTimesUrls = stamperPictureService.getBySealRecordInfoAndType(info.getId(), Global.TYPE_OVERTIMES);//如果盖章照片不存在，则查询一下是否存在 超出申请单次数 照片，它们2个只可能存在其中1个，或者一个没有
		vo.setOverTimesUrls(overTimesUrls);

		List<FileEntity> auditorUrls = stamperPictureService.getBySealRecordInfoAndType(info.getId(), Global.TYPE_AUDITOR);//审计照片
		vo.setAuditorUrls(auditorUrls);

		List<FileEntity> errorUrls = stamperPictureService.getBySealRecordInfoAndType(info.getId(), Global.TYPE_TIMEOUT);//警告照片
		vo.setWarnUrls(errorUrls);

		List<FileEntity> replenishUrls = stamperPictureService.getBySealRecordInfoAndType(info.getId(), Global.TYPE_REPLENISH);//追加照片
		vo.setReplenishUrls(replenishUrls);

		vo.setIsOos(info.getIsOos());
		return vo;
	}

	@ApiOperation(value = "查询申请单使用记录", notes = "查询申请单使用记录照片", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "申请单ID", dataType = "int", required = true, paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "当前页", dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "pageSize", value = "每页数", dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "page", value = "是否分页", dataType = "boolean", defaultValue = "true", paramType = "query")
	})
	@GetMapping("/getInfoByApplicationId")
	public ResultVO getInfoByApplicationId(@RequestParam(value = "id") Integer applicationId,
										   @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
										   @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
										   @RequestParam(value = "page", required = false, defaultValue = "true") boolean isPage) {
		Application application = applicationService.get(applicationId);
		if (application == null) {
			return ResultVO.FAIL("申请单不存在");
		}

		UserInfo userInfo = getUserInfo();
		if (!isAbleSelectApplicationInfo(userInfo, application)) {
			return ResultVO.FAIL("无权限查看");
		}

		if (isPage) {
			PageHelper.startPage(pageNum, pageSize);
		}
		List<InfoByApplication> res = service.getInfoByApplication(applicationId);
		return ResultVO.Page(res, isPage);
	}

	/**
	 * 用户是否有权限查看申请单使用记录
	 * 申请人、授权人、审批人、审计人、管理员、属主有权限查看
	 *
	 * @param userInfo    用户信息
	 * @param application 申请单信息
	 * @return true:有权限  false:无权限
	 */
	private boolean isAbleSelectApplicationInfo(UserInfo userInfo, Application application) {
		//申请人有权限
		if (application.getUserId() == userInfo.getId().intValue()) {
			return true;
		}

		//属主有权限
		if (userInfo.isOwner() || userInfo.isAdmin()) {
			return true;
		}

		//审批人、审计人、授权人有权限
		List<Integer> managerIds = applicationService.getUserIdsByApplication(application.getId());
		return (managerIds != null && managerIds.contains(userInfo.getId()));
	}

	@ApiOperation(value = "绑定申请单", notes = "绑定申请单", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "applicationId", value = "申请单ID", dataType = "int", required = true),
			@ApiImplicitParam(name = "sealRecordInfoId", value = "使用记录ID", dataType = "int", required = true),
	})
	@WebLogger("绑定申请单")
	@PostMapping("/banding")
	public ResultVO banding(@RequestParam("applicationId") Integer applicationId, @RequestParam("sealRecordInfoId") Integer sealRecordInfoId) {
		/*
		  参数校验:使用记录ID
		 */
		UserInfo userInfo = getUserInfo();
		SealRecordInfo sealRecordInfo = service.get(sealRecordInfoId);
		if (sealRecordInfo == null || sealRecordInfo.getOrgId().intValue() != userInfo.getOrgId()) {
			return ResultVO.FAIL("使用记录不存在");
		}
		if (sealRecordInfo.getUserId() == null) {
			return ResultVO.FAIL("使用记录数据异常【无用印人】");
		}
		if (sealRecordInfo.getUserId().intValue() != userInfo.getId()) {
			return ResultVO.FAIL("无权限");
		}
		Integer sealApplicationId = sealRecordInfo.getApplicationId();
		if (sealApplicationId != null && sealApplicationId != 0) {
			return ResultVO.FAIL("申请单已存在");
		}

		/*
		  参数校验：申请单有效性
		 */
		if (applicationId == null) {
			return ResultVO.FAIL("申请单不能为空");
		}
		Application application = applicationService.get(applicationId);
		if (application == null || application.getId() == null || application.getOrgId().intValue() != userInfo.getOrgId()) {
			return ResultVO.FAIL("申请单不存在");
		}
		if (application.getUserId() == null) {
			return ResultVO.FAIL("申请单数据异常【无申请人】");
		}
		if (application.getUserId().intValue() != userInfo.getId()) {
			return ResultVO.FAIL("无权限");
		}

		/*
		  参数校验：申请单状态
		 */
		//申请单状态 0:初始化提交 1:审批中 2:审批通过 3:审批拒绝  4:授权中 5:授权通过 6:授权拒绝  7:已推送  8:用章中 9:已用章 10:审计中 11:审计通过 12:审计拒绝 13:已失效
		Integer status = application.getStatus();
		List<Integer> okStatus = Arrays.asList(5, 7, 8, 9);
		if (!okStatus.contains(status)) {
			String msg = getErrorByStatus(status);
			return ResultVO.FAIL(msg);
		}

		/*
		  参数校验：申请单次数
		 */
		ApplicationDevice ad = applicationDeviceService.getByApplicationAndSignet(applicationId, sealRecordInfo.getDeviceId());
		if (ad == null || ad.getId() == null) {
			return ResultVO.FAIL("关联设备不匹配");
		}
		Integer alreadyCount = ad.getAlreadyCount();
		Integer userCount = ad.getUserCount();
		if (userCount < alreadyCount + 1) {
			return ResultVO.FAIL("申请单次数已用完");
		}

		/*
		  参数校验：申请单授权时间
		 */
		ApplicationKeeper ak = applicationKeeperService.getByApplicationOK(applicationId, ad.getDeviceId());
		if (ak == null || ak.getId() == null) {
			return ResultVO.FAIL("申请单授权信息不存在");
		}
		Date time = ak.getTime();//授权通过时间
		Date createDateByInfo = sealRecordInfo.getRealTime();//盖章时间
		if (createDateByInfo.getTime() > time.getTime()) {
			return ResultVO.FAIL("无法绑定该申请单");
		}

		service.banding(userInfo, sealRecordInfo, application);

		return ResultVO.OK("绑定成功");
	}

	private String getErrorByStatus(Integer status) {
		String msg = null;
		switch (status) {
			case 0:
				msg = "申请单状态有误";
				break;
			case 1:
				msg = "申请单未审批通过";
				break;
			case 2:
				msg = "申请单未授权通过";
				break;
			case 3:
				msg = "申请单已审批拒绝";
				break;
			case 4:
				msg = "申请单授权中";
				break;
			case 6:
				msg = "申请单已授权拒绝";
				break;
			case 10:
				msg = "申请单审计中";
				break;
			case 11:
				msg = "申请单已审计";
				break;
			case 12:
				msg = "申请单已审计拒绝";
				break;
			case 13:
				msg = "申请单已失效";
				break;
			default:
		}
		return msg;
	}

	@ApiOperation(value = "查询个人使用记录", notes = "查询个人使用记录")
	@GetMapping({"/searchInfoListByKeyword", "appInfoList"})
	public ResultVO searchInfoListByKeyword(Date[] date, String signetName, String title, Integer type, Integer error, Integer deviceType) {
		UserInfo userInfo = getUserInfo();

		//参数校验:时间
		Date start = null;
		Date end = null;
		if (date != null) {
			if (date.length == 1) {
				start = date[0];
			}
			if (date.length == 2) {
				start = date[0];
				end = date[1];
			}
		}
		if (start != null && end != null && (start.getTime() - end.getTime() > 0)) {
			return ResultVO.FAIL("日期时间选择有误");
		}

		//参数校验：印章
		if (StringUtils.isNotBlank(signetName)) {
			if (EmojiFilter.containsEmoji(signetName)) {
				return ResultVO.FAIL("印章名称不能包含特殊字符");
			}
			//如果是属主，跳过
			if (!userInfo.isOwner()) {
				//如果该用户没有该印章使用记录，直接返回空
				int count = service.getCountByUserAndSignetName(userInfo.getOrgId(), userInfo.getId(), signetName);
				if (count == 0) {
					return ResultVO.OK();
				}
			}
		}
		//参数校验：申请单
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

		boolean page = PageHelperUtil.startPage();
		List<InfoEntity> infoEntities = service.searchInfoListByKeyword(userInfo.getOrgId(), userInfo.isOwner() ? null : userInfo.getId(), start, end, signetName, title, error, type, deviceType);
		return ResultVO.Page(infoEntities, page);
	}
}
