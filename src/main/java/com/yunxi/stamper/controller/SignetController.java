package com.yunxi.stamper.controller;


import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunxi.common.page.PageEntity;
import com.yunxi.common.page.PageHelperUtil;
import com.yunxi.common.utils.BeanUtil;
import com.yunxi.common.utils.ListUtils;
import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.device.MHPkg;
import com.yunxi.stamper.commons.device.model.*;
import com.yunxi.stamper.commons.device.modelVo.DeviceInit;
import com.yunxi.stamper.commons.device.modelVo.DeviceLock;
import com.yunxi.stamper.commons.device.modelVo.DeviceSleepTime;
import com.yunxi.stamper.commons.jwt.ApplicationToken;
import com.yunxi.stamper.commons.jwt.JwtUtil;
import com.yunxi.stamper.commons.jwt.UserToken;
import com.yunxi.stamper.commons.mq.MQPKG;
import com.yunxi.stamper.commons.mq.MQWifiLink;
import com.yunxi.stamper.commons.mq.MQWifiList;
import com.yunxi.stamper.commons.other.*;
import com.yunxi.stamper.commons.response.Code;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.*;
import com.yunxi.stamper.entityVo.*;
import com.yunxi.stamper.logger.threadLocal.LocalHandle;
import com.yunxi.stamper.sys.rabbitMq.MqGlobal;
import com.yunxi.stamper.sys.rabbitMq.MqSender;
import com.yunxi.stamper.service.*;
import com.yunxi.stamper.sys.aop.annotaion.WebLogger;
import com.yunxi.stamper.websocket.container.WebSocketMap;
import com.yunxi.stamper.websocket.core.WsSocket;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.Future;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/3 0003 2:35
 */
@Slf4j
@RestController
@RequestMapping(value = "/device/signet", method = {RequestMethod.POST, RequestMethod.GET})
@Api(tags = {"印章相关"})
public class SignetController extends BaseController {

	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private RelateDepartmentUserService relateDepartmentUserService;
	@Autowired
	private SignetService service;
	@Autowired
	private UserService userService;
	@Autowired
	private FileInfoService fileInfoService;
	@Autowired
	private MeterService meterService;
	@Autowired
	private OrgService orgService;
	@Autowired
	private DeviceTypeService deviceTypeService;
	@Autowired
	private AddrService addrService;
	@Autowired
	private DeviceMigrateLogService deviceMigrateLogService;
	@Autowired
	private DeviceMessageService deviceMessageService;
	@Autowired
	private ThresholdService thresholdService;
	@Autowired
	private ConfigService configService;
	@Autowired
	private MqSender mqSender;
	@Autowired
	private SealRecordInfoService sealRecordInfoService;

	@ApiOperation(value = "打开或关闭摄像头", notes = "只有属主有权限打开或关闭摄像头", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "deviceId", value = "设备ID", dataType = "int"),
			@ApiImplicitParam(name = "status", value = "0:开启摄像头  1:关闭摄像头", dataType = "int")
	})
	@PostMapping("/cameraSwitch")
	public ResultVO cameraSwitch(@RequestParam Integer deviceId, @RequestParam Integer status) {
		//检查设备
		Signet device = service.get(deviceId);
		if (device == null) {
			return ResultVO.FAIL("设备不存在");
		}

		//检查权限
		UserInfo userInfo = getUserInfo();
		if (!Objects.equals(userInfo.getOrgId(), device.getOrgId()) || !userInfo.isOwner()) {
			return ResultVO.FAIL("无权限操作");
		}

		//设备是否在线
		Integer online = isOnline(deviceId);
		if (online == null) {
			return ResultVO.FAIL("设备不在线");
		}
		if (online == 1) {
			return ResultVO.FAIL("设备正在使用中");
		}

		//下发指令
		Map<String, Object> body = Collections.singletonMap("status", status);
		MHPkg res = MHPkg.res(AppConstant.REMOTE_CAMERA_SWITCH_REQ, body);

		MQPKG mqpkg = new MQPKG();
		mqpkg.setDeviceId(deviceId);
		mqpkg.setUserId(userInfo.getId());
		mqpkg.setData(JSONObject.toJSONString(res));
		mqpkg.setUserName(userInfo.getUserName());
		mqpkg.setCmd(MqGlobal.SIGNET_CAMERA_SWITCH);

		//将消息体推送到消息队列
		mqSender.sendToExchange(CommonUtils.getProperties().getRabbitMq().getExchangeOrder(), mqpkg);

		log.info("-\tMQ-下发指令-序列号:{}\t摄像头开关\tuserId:{}\tname:{}\tmessage:{}", mqpkg.getSerialId(), userInfo.getId(), userInfo.getUserName(), CommonUtils.objJsonWithIgnoreFiled(mqpkg));
		return ResultVO.OK("指令已下发");
	}

	/**
	 * 开启、关闭摄像头
	 *
	 * @param pkg
	 */
	public void _cameraSwitch(MQPKG pkg) throws Exception {
		if (pkg == null) {
			return;
		}
		//如果消息体为空,则不管它
		String data = pkg.getData();
		if (StringUtils.isBlank(data)) {
			return;
		}

		//如果设备ID为空,则不管它
		Integer deviceId = pkg.getDeviceId();
		if (deviceId == null) {
			return;
		}

		//发送指令
		WebSocketMap.sendAes(deviceId, data);
	}

	@ApiOperation(value = "报表条件-印章列表", notes = "报表条件-印章列表", httpMethod = "GET")
	@ApiImplicitParam(name = "keyword", value = "关键词", dataType = "string")
	@GetMapping("/deviceListForReport")
	public ResultVO deviceListForReport(@RequestParam(required = false) String keyword) {
		UserInfo userInfo = getUserInfo();
		/*手动检查用户权限*/
		String url = "/device/signet/getByOwner";
		if (userInfo.isOwner() || userInfo.isAdmin() || userInfo.getPermsUrls().contains(url)) {
			//属主,管理员或拥有印章管理权限的用户,拥有导出报表权限
			log.info("属主,管理员或拥有印章管理权限的用户,拥有导出报表权限");
		} else {
			return ResultVO.FAIL("无权限");
		}

		Integer orgId = userInfo.getOrgId();

		/*查询该集团当前实际拥有的设备列表*/
		List<Signet> devices = service.getList(orgId, keyword);

		/*组装前端参数*/
		Set<Integer> repeatList = new HashSet<>();
		List<Map<String, Object>> resList = new LinkedList<>();
		for (Signet device : devices) {
			repeatList.add(device.getId());
			Map<String, Object> res = new HashMap<>(2);
			res.put("id", device.getId());
			res.put("name", device.getName());
			resList.add(res);
		}

		/*优化查询效率 查询使用记录中的设备ID列表(包含曾经拥有的设备)*/
		List<SealRecordInfo> sealRecordInfos = sealRecordInfoService.getUsedIdList(orgId, keyword);
		if (sealRecordInfos != null && !sealRecordInfos.isEmpty()) {
			for (SealRecordInfo info : sealRecordInfos) {
				Integer deviceId = info.getDeviceId();
				if (repeatList.add(deviceId)) {
					String deviceName = info.getDeviceName();
					Map<String, Object> res = new HashMap<>(2);
					res.put("id", deviceId);
					res.put("name", deviceName);
					resList.add(res);
				}
			}
		}
		return ResultVO.OK(resList);
	}

	@ApiOperation(value = "查询设备坐标信息", notes = "查询设备端坐标信息", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "uuid", value = "设备UUID", dataType = "string")
	})
	@GetMapping("/getLocation")
	public ResultVO getLocation(@RequestParam String uuid) {
		Signet signet = service.getByUUID(uuid);
		if (signet == null) {
			return ResultVO.FAIL("设备不存在");
		}

		Integer addrId = signet.getAddr();
		Addr addr = addrService.get(addrId);
		if (addr == null) {
			return ResultVO.OK();
		}

		Map<String, Object> res = new HashMap<>();
		res.put("uuid", uuid);
		res.put("longitude", addr.getLongitude());
		res.put("latitude", addr.getLatitude());
		res.put("province", addr.getProvince());
		res.put("city", addr.getCity());
		res.put("district", addr.getDistrict());
		res.put("street", addr.getStreet());
		res.put("locationDescribe", addr.getLocationdescribe());
		res.put("location", addr.getLocation());

		return ResultVO.OK(res);
	}

	@ApiOperation(value = "查询所属公司印章所在坐标/地址", notes = "查询所属公司印章所在坐标/地址", httpMethod = "GET")
	@RequestMapping("/queryLocation")
	public ResultVO queryLocation(@RequestParam(required = false) String keyword,
								  @RequestParam(required = false, defaultValue = "1") Integer pageNum,
								  @RequestParam(required = false, defaultValue = "10") Integer pageSize,
								  @RequestParam(value = "page", required = false, defaultValue = "true") boolean isPage) {
		UserToken token = getToken();

		if (isPage) {
			PageHelper.startPage(pageNum, pageSize);
		}
		List<Signet> list = service.getList(token.getOrgId(), keyword);
		if (list == null || list.isEmpty()) {
			return ResultVO.OK();
		}
		PageInfo pageInfo = new PageInfo(list);

		//查询在线印章列表
		Set<String> keys = redisUtil.keys(RedisGlobal.PING + "*");

		//组装返回值
		List<Location> locations = new ArrayList<>();
		for (Signet signet : list) {
			Location location = new Location();
			//地址信息
			Integer addrId = signet.getAddr();
			Addr addr = addrService.get(addrId);
			if (addr != null) {
				location.setAddr(addr.getLocation());
				location.setLatitude(addr.getLatitude());
				location.setLongitude(addr.getLongitude());
			}
			//设备信息
			location.setDeviceId(signet.getId());
			location.setDeviceName(signet.getName());
			location.setOnline(keys.contains(RedisGlobal.PING + signet.getId()));

			locations.add(location);
		}
		pageInfo.setList(locations);
		return ResultVO.OK(pageInfo);
	}

	/**
	 * 发送日志上传请求
	 */
	@RequestMapping("/sendCommand")
	public ResultVO sendCommand(@RequestParam Integer deviceID) {
		Signet signet = service.get(deviceID);
		if (signet != null) {
			try {
				MHPkg res = MHPkg.res(AppConstant.DEVICE_LOGGER_FILE_UPDATE_REQ, null);
				WebSocketMap.sendAes(deviceID, JSONObject.toJSONString(res));
				return ResultVO.OK("请求发送成功");
			} catch (Exception e) {
				return ResultVO.FAIL("请求发送失败");
			}
		} else {
			return ResultVO.FAIL("设备不存在");
		}
	}

	/**
	 * 设备停用
	 */
	@RequestMapping("/goOutOfService")
	public ResultVO goOutOfService(@RequestParam("signetId") Integer signetId) {
		Signet signet = service.get(signetId);
		if (signet != null) {

			//只能操作本公司印章
			if (signet.getOrgId().intValue() != getToken().getOrgId()) {
				return ResultVO.FAIL(Code.FAIL403);
			}

			signet.setStatus(Global.DEVICE_STOP);
			service.update(signet);
			return ResultVO.OK("设备已停用");
		}
		return ResultVO.FAIL("该设备不存在");
	}

	/**
	 * 印章删除
	 */
	@WebLogger("删除印章")
	@RequestMapping("/del")
	@Transactional
	public ResultVO del(@RequestParam("id") Integer signetId) {
		Signet signet = service.get(signetId);
		if (signet == null) {
			return ResultVO.FAIL("设备不存在");
		}
		//只能操作本公司印章
		if (signet.getOrgId().intValue() != getToken().getOrgId()) {
			return ResultVO.FAIL(Code.FAIL403);
		}

//		UserInfo userInfo = getUserInfo();
		service.del(signet);

		//删除该设备的配置信息
		Config config = configService.getByUUID(signet.getUuid());
		if (config != null) {
			configService.del(config);
		}

//		Org org = orgService.get(userInfo.getOrgId());
//		try {
//			messageTempService.deleteDeviceNotice(signet.getName(), userInfo.getUserName(), org.getManagerUserId());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		//删除印章 通知 原管章人
//		try {
//			messageTempService.deleteDeviceNotice(signet.getName(), userInfo.getUserName(), signet.getKeeperId());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		//删除印章 通知 原审计人
//		try {
//			messageTempService.deleteDeviceNotice(signet.getName(), userInfo.getUserName(), signet.getAuditorId());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		return ResultVO.OK("已删除");
	}

	/**
	 * 远程锁定
	 * status  0：解锁 4：锁定
	 */
	@WebLogger("远程锁定")
	@RequestMapping("/setRemoteLock")
	@Transactional
	public ResultVO setRemoteLock(@RequestParam("signetId") Integer signetId,
								  @RequestParam("status") Integer status) {
		UserInfo userInfo = getUserInfo();
		Signet signet = service.get(signetId);
		//印章必须存在 and  只能修改自己公司的印章信息
		if (signet != null && signet.getOrgId().intValue() == userInfo.getOrgId()) {

			LocalHandle.setOldObj(signet);

			//只能操作本公司印章
			if (signet.getOrgId().intValue() != userInfo.getOrgId()) {
				return ResultVO.FAIL(Code.FAIL403);
			}
			//组包
			DeviceLock dl = new DeviceLock();
			dl.setDeviceId(signetId);
			dl.setStatus(status == 4 ? 1 : 0);
			MHPkg res = MHPkg.res(AppConstant.REMOTE_LOCK_REQ, dl);

			//修改印章状态=锁定
			signet.setStatus(status == 4 ? Global.DEVICE_LOCK : Global.DEVICE_NORMAL);
			service.update(signet);
			LocalHandle.setNewObj(signet);
			LocalHandle.complete("远程锁定");

			//印章是否在线
			Integer online = isOnline(signetId);
			if (online == null) {
				//设备不在线,记录离线消息,开机自动推送
				DeviceMessage dm = new DeviceMessage();
				dm.setBody(JSONObject.toJSONString(res));
				dm.setTitle(Global.set_remote_lock);
				dm.setPushStatus(1);
				dm.setSendId(userInfo.getId());
				dm.setRecipientId(signetId);
				deviceMessageService.addOrUpdate(dm);
				return ResultVO.OK("设备当前不在线,锁定指令会在设备开机后执行");
			} else {
				//设备在线,直接推送到队列
				MQPKG mqpkg = new MQPKG();
				mqpkg.setDeviceId(signetId);
				mqpkg.setUserId(userInfo.getId());
				mqpkg.setData(JSONObject.toJSONString(res));
				mqpkg.setUserName(userInfo.getUserName());
				mqpkg.setCmd(MqGlobal.SIGNET_REMOTE_LOCK);

				//将消息体推送到消息队列
				mqSender.sendToExchange(CommonUtils.getProperties().getRabbitMq().getExchangeOrder(), mqpkg);
				log.info("-\tMQ-下发指令-序列号:{}\t远程锁定\tuserId:{}\tname:{}\tmessage:{}", mqpkg.getSerialId(), userInfo.getId(), userInfo.getUserName(), CommonUtils.objJsonWithIgnoreFiled(mqpkg));

				return ResultVO.OK("指令已下发");
			}
		}
		return ResultVO.FAIL("该设备不存在");
	}

	/**
	 * 真正远程锁定逻辑代码
	 */
	public void _setRemoteLock(MQPKG pkg) throws Exception {
		if (pkg != null) {
			//如果消息体为空,则不管它
			String data = pkg.getData();
			if (StringUtils.isBlank(data)) {
				return;
			}

			//如果设备ID为空,则不管它
			Integer deviceId = pkg.getDeviceId();
			if (deviceId == null) {
				return;
			}

			//发送指令
			if (WebSocketMap.get(deviceId) != null) {
				//设备在线,直接推送
				WebSocketMap.sendAes(deviceId, data);
			} else {
				//设备不在线,记录离线消息,开机自定推送
				DeviceMessage dm = new DeviceMessage();
				dm.setBody(data);
				dm.setTitle(Global.set_remote_lock);
				dm.setPushStatus(1);
				dm.setSendId(pkg.getUserId());
				dm.setRecipientId(pkg.getDeviceId());
				deviceMessageService.addOrUpdate(dm);
			}
		}
	}

	/**
	 * 发送打开章头指令
	 * status  2:发送指令 打开章头
	 */
	@WebLogger("打开章头")
	@RequestMapping("/installationChapterHead")
	@Transactional
	public ResultVO installationChapterHead(@RequestParam("signetId") Integer signetId) {
		UserInfo userInfo = getUserInfo();
		Signet signet = service.get(signetId);

		LocalHandle.setOldObj(signet);
		//设备必须存在
		if (signet == null) {
			return ResultVO.FAIL("该设备不存在或已注销");
		}

		//只能修改自己公司的印章信息
		if (signet.getOrgId().intValue() != userInfo.getOrgId()) {
			return ResultVO.FAIL("您无权限使用该指令");
		}

		//印章已锁定了,无法开启/关闭指纹模式
		if (signet.getStatus() != null && signet.getStatus() == 4) {
			return ResultVO.FAIL("设备已被锁定");
		}

		//正在使用中,无法开启/关闭指纹模式
		Integer online = isOnline(signetId);
		if (online != null && online == 1) {
			return ResultVO.FAIL("设备当前正在使用中，无法开启章头");
		}

		//组包
		MHPkg res = MHPkg.res(AppConstant.USE_MODEL_REQ, new SignetModel(2));

//		signet.setFingerPattern(status == 1);
//		service.update(signet);

		LocalHandle.complete("打开章头");
		if (online == null) {
			return ResultVO.OK("设备当前不在线,请开机后重试");
		} else {
			//设备在线,直接推送到队列
			MQPKG mqpkg = new MQPKG();
			mqpkg.setDeviceId(signetId);
			mqpkg.setUserId(userInfo.getId());
			mqpkg.setData(JSONObject.toJSONString(res));
			mqpkg.setUserName(userInfo.getUserName());
			mqpkg.setCmd(MqGlobal.SIGNET_OPEN_OR_CLOSE_FINGER_PATTERN);

			//将消息体推送到消息队列
			mqSender.sendToExchange(CommonUtils.getProperties().getRabbitMq().getExchangeOrder(), mqpkg);
			log.info("-\tMQ-下发指令-序列号:{}\t打开装章\tuserId:{}\tname:{}\tmessage:{}", mqpkg.getSerialId(), userInfo.getId(), userInfo.getUserName(), CommonUtils.objJsonWithIgnoreFiled(mqpkg));

			return ResultVO.OK("指令已下发");
		}
	}

	/**
	 * 开启指纹模式
	 * status  0：关闭指纹模式 1：打开指纹模式
	 */
	@WebLogger("切换使用模式")
	@RequestMapping("/openPatternFinger")
	@Transactional
	public ResultVO openPatternFinger(@RequestParam("signetId") Integer signetId,
									  @RequestParam("status") Integer status) {
		UserInfo userInfo = getUserInfo();
		Signet signet = service.get(signetId);

		//设备必须存在
		if (signet == null) {
			return ResultVO.FAIL("该设备不存在或已注销");
		}

		LocalHandle.setOldObj(signet);

		//只能修改自己公司的印章信息
		if (signet.getOrgId().intValue() != userInfo.getOrgId()) {
			return ResultVO.FAIL("您无权限修改该设备配置");
		}

		//印章已锁定了,无法开启/关闭指纹模式
		if (signet.getStatus() != null && signet.getStatus() == 4) {
			return ResultVO.FAIL("设备已被锁定,无法切换使用模式");
		}

		if (status == 0 && Objects.equals(signet.getIsEnableApplication(), 2)) {
			return ResultVO.FAIL("设备申请单功能已禁用");
		}

		//正在使用中,无法开启/关闭指纹模式
		Integer online = isOnline(signetId);
		if (online != null && online == 1) {
			return ResultVO.FAIL("设备当前正在使用中，无法设置指纹模式");
		}

		//组包
		MHPkg res = MHPkg.res(AppConstant.USE_MODEL_REQ, new SignetModel(status));

		signet.setFingerPattern(status == 1);
		service.update(signet);
		LocalHandle.setNewObj(signet);
		LocalHandle.complete("切换指纹模式");
		if (online == null) {
			//设备不在线,记录离线消息,开机自动推送
			DeviceMessage dm = new DeviceMessage();
			dm.setBody(JSONObject.toJSONString(res));
			dm.setTitle(Global.open_close_finger_pattern);
			dm.setPushStatus(1);
			dm.setSendId(userInfo.getId());
			dm.setRecipientId(signetId);
			deviceMessageService.addOrUpdate(dm);
			return ResultVO.OK("设备当前不在线,切换指令会在设备开机后执行");
		} else {
			//设备在线,直接推送到队列
			MQPKG mqpkg = new MQPKG();
			mqpkg.setDeviceId(signetId);
			mqpkg.setUserId(userInfo.getId());
			mqpkg.setData(JSONObject.toJSONString(res));
			mqpkg.setUserName(userInfo.getUserName());
			mqpkg.setCmd(MqGlobal.SIGNET_OPEN_OR_CLOSE_FINGER_PATTERN);

			//将消息体推送到消息队列
			mqSender.sendToExchange(CommonUtils.getProperties().getRabbitMq().getExchangeOrder(), mqpkg);
			log.info("-\tMQ-下发指令-序列号:{}\t切换指纹模式\tuserId:{}\tname:{}\tmessage:{}", mqpkg.getSerialId(), userInfo.getId(), userInfo.getUserName(), CommonUtils.objJsonWithIgnoreFiled(mqpkg));

			return ResultVO.OK("指令已下发");
		}
	}

	/**
	 * 真正开启指纹模式逻辑代码
	 */
	public void _openPatternFinger(MQPKG pkg) throws Exception {
		if (pkg != null) {
			//如果消息体为空,则不管它
			String data = pkg.getData();
			if (StringUtils.isBlank(data)) {
				return;
			}

			//如果设备ID为空,则不管它
			Integer deviceId = pkg.getDeviceId();
			if (deviceId == null) {
				return;
			}

			//发送指令
			WebSocketMap.sendAes(deviceId, data);
		}
	}

	/**
	 * 设置印章休眠时间
	 *
	 * @param signetId 印章ID
	 * @param value    休眠时间
	 * @return 结果
	 */
	@WebLogger("设置印章休眠")
	@RequestMapping("/setDormancy")
	@Transactional
	public ResultVO setDormancy(@RequestParam("signetId") Integer signetId,
								@RequestParam("value") Integer value) {
		if (value < 2 || value > 10) {
			return ResultVO.FAIL("设备休眠时间只能在2~10分钟");
		}
		UserInfo userInfo = getUserInfo();
		Signet signet = service.get(signetId);
		//印章必须存在 and  只能修改自己公司的印章信息
		if (signet != null && signet.getOrgId().intValue() == userInfo.getOrgId()) {
			LocalHandle.setOldObj(signet);
			if (signet.getStatus() != null && signet.getStatus() == 4) {
				//印章已锁定了,无法开启/关闭指纹模式
				return ResultVO.FAIL("设备已被锁定,无法设置休眠");
			}

			//组包
			DeviceSleepTime dd = new DeviceSleepTime();
			dd.setDeviceId(signetId);
			dd.setSleepTime(value);
			MHPkg res = MHPkg.res(AppConstant.SLEEP_TIME_REQ, dd);

			signet.setSleepTime(value);
			service.update(signet);
			LocalHandle.setNewObj(signet);
			LocalHandle.complete("设置休眠");
			//印章是否在线
			Integer online = isOnline(signetId);
			if (online == null) {
				//设备不在线,记录离线消息,开机自动推送
				DeviceMessage dm = new DeviceMessage();
				dm.setBody(JSONObject.toJSONString(res));
				dm.setTitle(Global.set_sleep_times);
				dm.setPushStatus(1);
				dm.setSendId(userInfo.getId());
				dm.setRecipientId(signetId);
				deviceMessageService.addOrUpdate(dm);
				return ResultVO.OK("设备当前不在线,休眠指令会在设备开机后执行");
			} else {
				//印章在线,直接推送
				MQPKG mqpkg = new MQPKG();
				mqpkg.setDeviceId(signetId);
				mqpkg.setUserId(userInfo.getId());
				mqpkg.setData(JSONObject.toJSONString(res));
				mqpkg.setUserName(userInfo.getUserName());
				mqpkg.setCmd(MqGlobal.SIGNET_SET_SLEEP_TIMES);

				//将消息体推送到消息队列
				mqSender.sendToExchange(CommonUtils.getProperties().getRabbitMq().getExchangeOrder(), mqpkg);
				log.info("-\tMQ-下发指令-序列号:{}\t设置休眠\tuserId:{}\tname:{}\tmessage:{}", mqpkg.getSerialId(), userInfo.getId(), userInfo.getUserName(), CommonUtils.objJsonWithIgnoreFiled(mqpkg));


				return ResultVO.OK("指令已下发");
			}
		}
		return ResultVO.FAIL("该设备不存在");
	}


	/**
	 * 真正设置印章休眠时间逻辑代码
	 */
	public void _setDormancy(MQPKG pkg) throws Exception {
		if (pkg != null) {
			//如果消息体为空,则不管它
			String data = pkg.getData();
			if (StringUtils.isBlank(data)) {
				return;
			}

			//如果设备ID为空,则不管它
			Integer deviceId = pkg.getDeviceId();
			if (deviceId == null) {
				return;
			}

			//发送指令
			if (WebSocketMap.get(deviceId) != null) {
				//设备在线
				WebSocketMap.sendAes(deviceId, data);
			} else {
				//设备不在线,记录离线消息,开机自动推送
				DeviceMessage dm = new DeviceMessage();
				dm.setBody(data);
				dm.setTitle(Global.set_sleep_times);
				dm.setPushStatus(1);
				dm.setSendId(pkg.getUserId());
				dm.setRecipientId(pkg.getDeviceId());
				deviceMessageService.addOrUpdate(dm);
			}
		}
	}


	/**
	 * 真正开始印章迁移逻辑代码
	 */
	public void _migrate(MQPKG pkg) throws Exception {
		if (pkg != null) {
			//如果消息体为空,则不管它
			String data = pkg.getData();
			if (StringUtils.isBlank(data)) {
				return;
			}

			//如果设备ID为空,则不管它
			Integer deviceId = pkg.getDeviceId();
			if (deviceId == null) {
				return;
			}

			//发送指令
			if (WebSocketMap.get(deviceId) != null) {
				//设备在线
				WebSocketMap.sendAes(deviceId, data);
			} else {
				//设备不在线,记录离线消息,开机自动推送
				DeviceMessage dm = new DeviceMessage();
				dm.setBody(data);
				dm.setTitle(Global.set_migrate);
				dm.setPushStatus(1);
				dm.setSendId(pkg.getUserId());
				dm.setRecipientId(pkg.getDeviceId());
				deviceMessageService.addOrUpdate(dm);
			}
		}
	}

	/**
	 * 等待2秒后响应结果
	 * ps:正常2秒后消息应该就推送出去了,但是不一定准确,所以前台还是需要做模糊反馈
	 */
	private ResultVO restToBack(Future future) {
		if (future != null) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				log.error("出现异常 ", e);
			}
			return ResultVO.OK(Code.OK, future.isDone());
		}
		return ResultVO.FAIL("指令下发失败,设备当前不在线,请稍后重试");
	}


	/**
	 * 清空已处理的申请单
	 */
	@RequestMapping("/cleanApplications")
	public ResultVO cleanApplications(@RequestParam("signetId") Integer signetId) throws Exception {
		if (signetId != null) {
			Signet signet = service.get(signetId);
			//只能操作本公司印章
			UserToken token = getToken();
			if (signet.getOrgId().intValue() != token.getOrgId()) {
				return ResultVO.FAIL(Code.FAIL403);
			}

			ApplicationListClearReq req = new ApplicationListClearReq();
			req.setUserID(token.getUserId());
			req.setDeviceID(signetId);
			MHPkg res = MHPkg.res(AppConstant.APPLICATION_LIST_CLEAR_REQ, req);
			WebSocketMap.sendAes(signetId, JSONObject.toJSONString(res));
			return ResultVO.OK();
		}
		return ResultVO.FAIL(Code.FAIL402);
	}


	/**
	 * 下发·链接WiFi·指令
	 *
	 * @param signetId     设备id
	 * @param wifiName     wifi名称
	 * @param wifiPassword WiFi密码
	 * @return 结果
	 */
	@WebLogger("链接wifi")
	@RequestMapping("/setWifiLink")
	public ResultVO setWifiLink(@RequestParam("signetId") Integer signetId,
								@RequestParam("wifiName") String wifiName,
								@RequestParam(value = "wifiPassword", required = false) String wifiPassword) {
		if (signetId != null && StringUtils.isNotBlank(wifiName)) {

			Signet signet = service.get(signetId);
			if (signet == null) {
				return ResultVO.FAIL("设备不存在");
			}
			LocalHandle.setOldObj(signet);
			//只能操作本公司印章
			UserToken token = getToken();
			if (signet.getOrgId().intValue() != token.getOrgId()) {
				return ResultVO.FAIL(Code.FAIL403);
			}

			if (signet.getStatus() != null && signet.getStatus() == 4) {
				//印章已锁定了,无法开启/关闭指纹模式
				return ResultVO.FAIL("设备已被锁定,无法链接wifi");
			}

			//印章是否在线
			Integer online = isOnline(signetId);
			if (online == null) {
				return ResultVO.FAIL("很抱歉,设备当前不在线");
			}

			//组包
			MQPKG mqpkg = new MQPKG();
			mqpkg.setDeviceId(signetId);
			mqpkg.setCmd(MqGlobal.SIGNET_WIFI_LINK);
			mqpkg.setUserId(getToken().getUserId());

			MQWifiLink wifiLink = new MQWifiLink();
			wifiLink.setSsid(wifiName);
			wifiLink.setPassword(wifiPassword);
			wifiLink.setDeviceId(signetId);

			mqpkg.setData(JSONObject.toJSONString(wifiLink));
			//推送消息
			mqSender.sendToExchange(CommonUtils.getProperties().getRabbitMq().getExchangeOrder(), mqpkg);
			log.info("-\tMQ-下发指令-序列号:{}\t链接wifi\tuserId:{}\tname:{}\tmessage:{}", mqpkg.getSerialId(), token.getUserId(), token.getUserName(), CommonUtils.objJsonWithIgnoreFiled(mqpkg));


			LocalHandle.complete("链接wifi");
			return ResultVO.OK("指令已下发");
		}
		return ResultVO.FAIL(Code.FAIL402);
	}

	/**
	 * 真正开始链接wifi
	 */
	public void _setWifiLink(MQPKG pkg) throws Exception {

		if (pkg != null) {

			String data = pkg.getData();
			MQWifiLink wifiLink = null;
			if (StringUtils.isNotBlank(data)) {
				wifiLink = JSONObject.parseObject(data, MQWifiLink.class);
			}
			if (wifiLink == null) {
				return;
			}

			//检查该设备是否在本服务器并在线中
			Integer deviceID = pkg.getDeviceId();
			if (WebSocketMap.get(deviceID) != null) {

				//组包
				DeviceSetWifiReq req = new DeviceSetWifiReq();
				req.setDeviceID(deviceID);
				req.setUserID(pkg.getUserId());
				req.setSsid(wifiLink.getSsid());
				req.setWifiPwd(wifiLink.getPassword());

				MHPkg mhPkg = MHPkg.res(AppConstant.DEVICE_SET_WIFI_REQ, req);
				WebSocketMap.sendAes(deviceID, JSONObject.toJSONString(mhPkg));
				log.info("设备：【{}】 wifi：【{}】 ", deviceID, req.getSsid());
			} else {
				log.info("设备：【{}】 不在线", deviceID);
			}
		}
	}

	/**
	 * 下发·获取wifi列表·指令
	 */
	@PostMapping("/getWifiList")
	public ResultVO getWifiList(@RequestParam("signetId") Integer signetId) {
		/*
		 * 参数校验:印章ID
		 */
		UserInfo userInfo = getUserInfo();
		Signet signet = service.get(signetId);
		if (signet == null || signet.getOrgId().intValue() != userInfo.getOrgId()) {
			return ResultVO.FAIL("设备不存在");
		}
		LocalHandle.setOldObj(signet);
		Integer status = signet.getStatus();
		if (status != null && status == 4) {
			return ResultVO.FAIL("设备已被锁定");
		}
		Integer online = isOnline(signetId);
		if (online == null) {
			return ResultVO.FAIL("很抱歉,设备当前不在线");
		}

		//先从redis查询,将查到的结果返回
		String key = RedisGlobal.DEVICE_WIFI_LIST + signetId;
		Object wifiListObj = redisUtil.get(key);

		//组包
		MQPKG mqpkg = new MQPKG();
		mqpkg.setData(JSONObject.toJSONString(new MQWifiList(signetId)));
		mqpkg.setDeviceId(signetId);
		mqpkg.setCmd(MqGlobal.SIGNET_WIFI_LIST);
		mqpkg.setUserId(getToken().getUserId());

		mqSender.sendToExchange(CommonUtils.getProperties().getRabbitMq().getExchangeOrder(), mqpkg);
		LocalHandle.setOldObj(signet);
		LocalHandle.complete("检索附近wifi");
		return ResultVO.OK(wifiListObj);
	}

	/**
	 * 真正获取wifi列表
	 */
	public void _getWifiList(MQPKG mqpkg) throws Exception {
		if (mqpkg != null) {
			Integer userId = mqpkg.getUserId();
			Integer deviceId = mqpkg.getDeviceId();

			WifiListReq req = new WifiListReq();
			req.setDeviceID(deviceId);
			req.setUserID(userId);
			MHPkg res = MHPkg.res(AppConstant.WIFI_LIST_REQ, req);
			WebSocketMap.sendAes(deviceId, JSONObject.toJSONString(res));
			log.info("用户ID：{} 印章ID：{} 获取wifi列表指令下发", userId, deviceId);
		}
	}


	/**
	 * 真正断开wifi链接
	 *
	 * @param pkg 消息内容
	 */
	public void _closeWifiLink(MQPKG pkg) throws Exception {

		String data = pkg.getData();
		MQWifiLink wifiLink = null;
		if (StringUtils.isNotBlank(data)) {
			wifiLink = JSONObject.parseObject(data, MQWifiLink.class);
		}
		if (wifiLink == null) {
			return;
		}

		Integer deviceId = wifiLink.getDeviceId();
		String ssid = wifiLink.getSsid();
		Integer userId = pkg.getUserId();

		/*向印章设备发送请求*/
		DeviceSetWifiReq req = new DeviceSetWifiReq();
		req.setDeviceID(deviceId);
		req.setUserID(userId);
		req.setSsid(ssid);

		MHPkg res = MHPkg.res(AppConstant.RECORD_NOTICE_REQ, req);
		WebSocketMap.sendAes(deviceId, JSONObject.toJSONString(res));
	}

	/**
	 * 下发·断开wifi链接·指令
	 */
	@WebLogger("断开wifi")
	@RequestMapping("/closeWifi")
	public ResultVO closeWifi(@RequestParam("signetId") Integer signetId,
							  @RequestParam("wifiName") String wifiName) {
		/*参数校验*/
		Signet signet = service.get(signetId);
		if (signet == null) {
			return ResultVO.FAIL("该印章不存在");
		} else {
			LocalHandle.setOldObj(signet);
			//只能操作本公司印章
			UserToken token = getToken();
			if (signet.getOrgId().intValue() != token.getOrgId()) {
				return ResultVO.FAIL(Code.FAIL403);
			}
			if (signet.getStatus() != null && signet.getStatus() == 4) {
				//印章已锁定了,无法开启/关闭指纹模式
				return ResultVO.FAIL("设备已被锁定,无法断开wifi");
			}

			//印章是否在线
			Integer online = isOnline(signetId);
			if (online == null) {
				return ResultVO.FAIL("很抱歉,设备当前不在线");
			}

			//组包
			MQPKG mqpkg = new MQPKG();
			mqpkg.setUserId(getToken().getUserId());
			mqpkg.setCmd(MqGlobal.SIGNET_WIFI_CLOSE);
			mqpkg.setDeviceId(signetId);

			MQWifiLink wifiLink = new MQWifiLink();
			wifiLink.setDeviceId(signetId);
			wifiLink.setSsid(wifiName);

			mqpkg.setData(JSONObject.toJSONString(wifiLink));

			mqSender.sendToExchange(CommonUtils.getProperties().getRabbitMq().getExchangeOrder(), mqpkg);
			log.info("-\tMQ-下发指令-序列号:{}\t断开wifi\tuserId:{}\tname:{}\tmessage:{}", mqpkg.getSerialId(), token.getUserId(), token.getUserName(), CommonUtils.objJsonWithIgnoreFiled(mqpkg));

			LocalHandle.complete("断开wifi");
			return ResultVO.OK("指令已下发");
		}
	}

	/**
	 * 手动解锁
	 *
	 * @param signetIdStr 设备id
	 * @return 结果
	 */
	@WebLogger("ID解锁")
	@RequestMapping("/unlock")
	public ResultVO unlock(@RequestParam("signetId") String signetIdStr) {
		Integer signetId;
		try {
			signetId = Integer.parseInt(signetIdStr);
		} catch (NumberFormatException e) {
			return ResultVO.FAIL("印章不存在");
		}
		Signet signet = service.get(signetId);
		if (signet == null) {
			return ResultVO.FAIL("印章不存在");
		}

		UserInfo userInfo = getUserInfo();
		LocalHandle.setOldObj(signet);
		//只能解锁本公司的章
		if (userInfo.getOrgId().intValue() != signet.getOrgId()) {
			return ResultVO.FAIL("无权限解锁该印章");
		}

		//印章已锁定了,无法开启/关闭指纹模式
		if (signet.getStatus() != null && signet.getStatus() == 4) {
			return ResultVO.FAIL("该设备已被锁定,手动开锁失败");
		}

		//印章是否在线
		Integer online = isOnline(signetId);
		if (online == null) {
			return ResultVO.FAIL("很抱歉,设备当前不在线");
		}
		if (online == 1) {
			return ResultVO.FAIL("该设备已解锁，请关锁后重试");
		}

		//解决ID解锁情况下,名称过长,导致记录无法正常上传问题,此处将截取名称前4位下发至设备端
		String userName = userInfo.getUserName();
		if (StringUtils.isNotBlank(userName) && userName.length() > 6) {
			try {
				StringBuilder tempStr = new StringBuilder();
				char[] chars = userName.toCharArray();
				int num = 0;
				for (char c : chars) {
					if (num >= 12) {
						break;
					}
					boolean chineseChar = PinyinUtil.isChineseChar(c);
					if (chineseChar) {
						num = num + 2;
					} else {
						num = num + 1;
					}
					if (num > 12) {
						break;
					}
					tempStr.append(c);
				}
				userName = tempStr.toString();
			} catch (Exception e) {
				e.printStackTrace();
				userName = userName.substring(0, 4);
			}
		}

		CSDeviceUnlockReq req = new CSDeviceUnlockReq();
		req.setUserID(userInfo.getId());
		req.setDeviceID(signetId);
		req.setUserName(userName);
		MHPkg res = MHPkg.res(AppConstant.DEVICE_UNLOCK_REQ, req);

		//组包
		MQPKG mqpkg = new MQPKG();
		mqpkg.setDeviceId(signetId);
		mqpkg.setUserId(userInfo.getId());
		mqpkg.setData(JSONObject.toJSONString(res));
		mqpkg.setCmd(MqGlobal.SIGNET_UNLOCK);

		//推送消息队列
		mqSender.sendToExchange(CommonUtils.getProperties().getRabbitMq().getExchangeOrder(), mqpkg);
		log.info("-\tMQ-下发指令-序列号:{}\tID解锁\tuserId:{}\tname:{}\tmessage:{}", mqpkg.getSerialId(), userInfo.getId(), userInfo.getUserName(), CommonUtils.objJsonWithIgnoreFiled(mqpkg));

		LocalHandle.setNewObj(signet);
		LocalHandle.complete("ID解锁");
		return ResultVO.OK("指令已下发");
	}

	/**
	 * 真正远程开锁
	 */
	public void _unlock(MQPKG mqpkg) throws Exception {
		WebSocketMap.sendAes(mqpkg.getDeviceId(), mqpkg.getData());
	}

	/**
	 * 结束申请单
	 */
	public ResultVO endApplication(MQPKG pkg) throws Exception {
		String data = pkg.getData();
		EndApplication application = null;
		if (StringUtils.isNotBlank(data)) {
			application = JSONObject.parseObject(data, EndApplication.class);
		}
		if (application != null) {
			Integer signetId = application.getDeviceId();
			Signet signet = service.get(signetId);
			if (signet == null) {
				return ResultVO.FAIL("该印章不存在");
			}
			MHPkg res = MHPkg.end(application.getApplicationId());
			WebSocketMap.sendAes(application.getDeviceId(), JSONObject.toJSONString(res));
			//异步记录申请单
			return ResultVO.OK("指令下发成功");
		}
		return ResultVO.FAIL("指令下发失败");
	}

	/**
	 * 推送申请单
	 */
	public ResultVO pushApplication(MQPKG pkg) throws Exception {
		String data = pkg.getData();
		PushApplication application = null;
		if (StringUtils.isNotBlank(data)) {
			application = JSONObject.parseObject(data, PushApplication.class);
		}

		if (application != null) {
			Integer signetId = application.getSignetId();
			Signet signet = service.get(signetId);
			if (signet == null) {
				return ResultVO.FAIL("该印章不存在");
			}
			if (signet.getStatus() != null && signet.getStatus() == 4) {
				//印章已锁定了,无法开启/关闭指纹模式
				return ResultVO.FAIL("设备已被锁定,无法推送申请单");
			}

			WsSocket wsSocket = WebSocketMap.get(signetId);
			if (wsSocket == null) {
				return ResultVO.FAIL("指令下发失败,设备当前不在线,请稍后重试");
			}

			if (wsSocket.isBusy()) {
				return ResultVO.FAIL("该设备正在使用中,请关锁后推送");
			}

			//生成token
			ApplicationToken applicationToken = new ApplicationToken();
			applicationToken.setApplication_id(application.getApplicationId());
			applicationToken.setStatus(4);
			applicationToken.setIs_qss(application.getIsQss());
			String token = JwtUtil.createJWT2(applicationToken);

			if (StringUtils.isNotBlank(token)) {
				//生成请求实体
				ApplicationStatusReq req = new ApplicationStatusReq();
				req.setApplicationToken(token);
				req.setUseCount(application.getUseCount());
				req.setIsQss(application.getIsQss());
				req.setStatus(4);
				req.setApplicationTitle(application.getTitle());
				req.setApplicationID(application.getApplicationId());
				req.setUserName(application.getUserName());
				req.setUserID(application.getUserId());
				req.setTotalCount(application.getTotalCount());
				req.setNeedCount(application.getNeedCount());
				MHPkg res = MHPkg.res(AppConstant.APPLICATION_STATUS_REQ, req);
				WebSocketMap.sendAes(application.getSignetId(), JSONObject.toJSONString(res));
				//异步记录申请单
				return ResultVO.OK("指令下发成功");
			}
		}
		return ResultVO.FAIL("指令下发失败");
	}

	/**
	 * 通知高拍仪拍照
	 */
	public void meterToUse(MQPKG pkg) throws Exception {
		WebSocketMap.sendAes(pkg.getDeviceId(), pkg.getData());
		log.info("高拍仪：【{}】 拍照指令已下发", pkg.getDeviceId());
	}

	/**
	 * 下发`设备次数清0(初始化)`指令
	 *
	 * @param signetId 设备id
	 * @return 结果
	 */
	@RequestMapping("initializtion")
	public ResultVO initializtion(@RequestParam("signetId") Integer signetId) {
		if (signetId != null) {
			Signet signet = service.get(signetId);
			if (signet == null) {
				return ResultVO.FAIL("设备不存在");
			}
			if (signet.getOrgId().intValue() != getToken().getOrgId()) {
				return ResultVO.FAIL(Code.FAIL403);
			}
			//设备是否在线
			Integer online = isOnline(signetId);
			if (online == null) {
				return ResultVO.FAIL("该设备当前不在线");
			}
			if (online == 0) {
				return ResultVO.FAIL("该设备当前正在使用中,请关锁后重试");
			}

			//组包
			MQPKG mqpkg = new MQPKG();
			mqpkg.setUserId(getToken().getUserId());
			mqpkg.setDeviceId(signetId);
			mqpkg.setCmd(MqGlobal.SIGNET_INIT);

			MHPkg res = MHPkg.res(AppConstant.DEVICE_INIT_CLEAR_REQ, null);
			mqpkg.setData(JSONObject.toJSONString(res));

			//发送消息到消息队列
			mqSender.sendToExchange(CommonUtils.getProperties().getRabbitMq().getExchangeOrder(), JSONObject.toJSONString(mqpkg));
			UserInfo userInfo = getUserInfo();
			log.info("-\tMQ-下发指令-序列号:{}\t设备清次\tuserId:{}\tname:{}\tmessage:{}", mqpkg.getSerialId(), userInfo.getId(), userInfo.getUserName(), CommonUtils.objJsonWithIgnoreFiled(mqpkg));


			return ResultVO.OK("指令已下发");
		}
		return ResultVO.FAIL(Code.FAIL402);
	}

	/**
	 * 真正开始清次
	 */
	public void _initializtion(MQPKG mqpkg) throws Exception {
		WebSocketMap.sendAes(mqpkg.getDeviceId(), mqpkg.getData());
	}

	/**
	 * 通知设备端上传日志文件
	 */
	@RequestMapping("/getLogFile")
	public ResultVO getLogFile(@RequestParam("uuid") String uuid) {
		Signet signet = service.getByUUID(uuid);
		if (signet != null) {
			//是否在线
			Integer online = isOnline(signet.getId());
			if (online == null) {
				return ResultVO.FAIL("该设备不在线");
			}
			MHPkg res = MHPkg.res(AppConstant.DEVICE_LOGGER_FILE_UPDATE_REQ, null);

			//组包
			MQPKG mqpkg = new MQPKG();
			mqpkg.setDeviceId(signet.getId());
			mqpkg.setUserId(getToken().getUserId());
			mqpkg.setData(JSONObject.toJSONString(res));
			mqpkg.setCmd(MqGlobal.SIGNET_UPLOAD_LOG);

			//推送消息队列
			mqSender.sendToExchange(CommonUtils.getProperties().getRabbitMq().getExchangeOrder(), mqpkg);
			UserInfo userInfo = getUserInfo();
			log.info("-\tMQ-下发指令-序列号:{}\t日志指令\tuserId:{}\tname:{}\tmessage:{}", mqpkg.getSerialId(), userInfo.getId(), userInfo.getUserName(), CommonUtils.objJsonWithIgnoreFiled(mqpkg));


			return ResultVO.OK("指令已下发");
		}
		return ResultVO.FAIL("该设备不存在");
	}

	/**
	 * 真正通知设备上传日志的方法
	 */
	public void _getLogFile(MQPKG mhPkg) throws Exception {
		WebSocketMap.sendAes(mhPkg.getDeviceId(), mhPkg.getData());
	}

	//获取当前阈值信息，全局阈值deviceid=0
	@RequestMapping("/getThresholdInfo")
	public ResultVO getThresholdInfo(Integer deviceId) {
		if (deviceId != null) {
			UserInfo userInfo = getUserInfo();
			if (deviceId == 0) {
				//获取全局阈值
				Threshold defaultByOrg = thresholdService.getDefaultByOrg(userInfo.getOrgId());
				defaultByOrg = getThreshold(defaultByOrg, userInfo.getOrgId());

				return ResultVO.OK(defaultByOrg);
			} else {
				Signet signet = service.get(deviceId);
				if (signet == null) {
					return ResultVO.FAIL("设备不存在");
				}
				if (signet.getOrgId().intValue() != getToken().getOrgId()) {
					return ResultVO.FAIL(Code.FAIL403);
				}

				//获取单个设备的阈值信息
				Threshold threshold = thresholdService.getByDeviceId(deviceId, userInfo.getOrgId());
				if (threshold == null) {
					//初始时没有阈值信息
					threshold = new Threshold();
					threshold.setCreateDate(new Date());
					threshold.setDeviceId(deviceId);
					threshold.setName(deviceId + "");
					threshold.setOrgId(userInfo.getOrgId());

					//查询公司阈值
					Threshold defaultByOrg = thresholdService.getDefaultByOrg(userInfo.getOrgId());
					defaultByOrg = getThreshold(defaultByOrg, userInfo.getOrgId());

					threshold.setThresholdValue(defaultByOrg.getThresholdValue());
					thresholdService.add(threshold);
				}
				return ResultVO.OK(threshold);
			}
		}
		return ResultVO.FAIL(Code.FAIL402);
	}

	/**
	 * 查询印章(通讯录格式)
	 */
	@RequestMapping("/getByAddressList")
	public ResultVO getByAddressList() {
//		UserInfo userInfo = getUserInfo();
//		List<Signet> signets;
//		if (userInfo.isOwner()) {
//			signets = service.getByOrg(userInfo.getOrgId());
//		} else {
////			List<Integer> searchDepartmentIds = departmentService.getDepartmentIdsByAppSignet(userInfo.getId());
//			signets = service.getSignetByOrgAndDepartments(userInfo.getOrgId(), userInfo.getVisualDepartmentIds(), userInfo.getId());
//		}
//		List<Map<String, Object>> list = CommonUtils.getAddressList(signets, "name");

		//查询列表
		UserInfo userInfo = getUserInfo();
		Integer orgId = userInfo.getOrgId();
		List<Integer> departmentIds = userInfo.getVisualDepartmentIds();
		List<Signet> signets = service.get(orgId, departmentIds, 1);
		List<Map<String, Object>> list = CommonUtils.getAddressList(signets, "name");
		return ResultVO.OK(list);
	}


	/*
	 * 手机端
	 * v1:除了管章人、属主以外，其他用户均无法看到设备列表
	 * v1.1.8.4
	 * 属主：允许查看所有集团下的设备
	 * 管理员：允许查看所属组织下的设备
	 * 管章人：允许查看自己管理的设备
	 * 普通用户：不允许查看
	 *
	 * @param type
	 * @param keeperName
	 * @param deviceName
	 * @return
	 */
	@RequestMapping("/getBySignetType")
	public ResultVO getBySignetType(@RequestParam(value = "type", required = false) Integer type,
									@RequestParam(value = "userName", required = false) String keeperName,
									@RequestParam(value = "deviceName", required = false) String deviceName) {
		UserInfo userInfo = getUserInfo();


		/*要查询的用户ID*/
		Integer searchUserId = null;

		/*要查询的组织ID*/
		List<Integer> searchDepartmentIds = new ArrayList<>();

		if (userInfo.isOwner()) {
			searchDepartmentIds = userInfo.getVisualDepartmentIds();

		} else if (userInfo.isAdmin()) {
			/*查询用户所属公司ID列表*/
			List<Integer> userDepartmentIds = relateDepartmentUserService.getDepartmentIdsByUserId(userInfo.getId());
			for (Integer userDepartmentId : userDepartmentIds) {
				Department company = departmentService.getCompanyByChildrenId(userDepartmentId);
				if (company != null && !searchDepartmentIds.contains(company.getId())) {
					/*将用户所属组织的公司ID添加至搜索集合*/
					searchDepartmentIds.add(company.getId());
				}
			}

			/*查询该用户所属公司下所有组织ID列表,并添加至搜索集合*/
			List<Integer> childrenIds = departmentService.getChildrenIdsByOrgAndParentsAndType(userInfo.getOrgId(), searchDepartmentIds, null);
			if (childrenIds != null && !childrenIds.isEmpty()) {
				for (Integer childrenId : childrenIds) {
					if (!searchDepartmentIds.contains(childrenId)) {
						searchDepartmentIds.add(childrenId);
					}
				}
			}
		} else {
			searchUserId = userInfo.getId();
			searchDepartmentIds = userInfo.getVisualDepartmentIds();
		}

		boolean page = setPage();
		List<Map<String, Object>> signets = service.getByType(userInfo.getOrgId(), type, keeperName, deviceName, searchDepartmentIds, searchUserId);
		if (signets != null && signets.size() > 0) {
			for (Map<String, Object> map : signets) {
				Object logo = map.get("logo");
				if (logo != null && StringUtils.isNotBlank(logo.toString())) {
					FileEntity fileEnity = fileInfoService.getReduceImgURLByFileId(logo.toString());
					map.put("fileEntity", fileEnity);
				}
			}
		}
		return ResultVO.Page(signets, page);
	}

	/**
	 * web端
	 * v1:除了管章人、属主以外，其他用户均无法看到设备列表
	 * v1.1.8.4
	 * 属主：允许查看所有集团下的设备
	 * 管理员：允许查看所属组织下的设备
	 * 管章人：允许查看自己管理的设备
	 * 普通用户：不允许查看
	 */
	@ApiOperation(value = "搜索印章列表", notes = "查询、搜索用户公司印章列表", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "keyword", value = "印章名称、ID关键词", dataType = "String"),
			@ApiImplicitParam(name = "departmentId", value = "组织ID", dataType = "int"),
			@ApiImplicitParam(name = "pageSize", value = "每页显示数", dataType = "int", defaultValue = "10"),
			@ApiImplicitParam(name = "pageNum", value = "当前页", dataType = "int", defaultValue = "1"),
			@ApiImplicitParam(name = "page", value = "是否分页", dataType = "int", defaultValue = "false")
	})
	@GetMapping("/getByOwner")
	public ResultVO getByOwner(@RequestParam(value = "keyword", required = false) String keyword,
							   @RequestParam(value = "departmentId", required = false) Integer departmentId,
							   @RequestParam(value = "page", required = false, defaultValue = "true") boolean isPage) {
		// 校验关键词
		if (StringUtils.isNotBlank(keyword)) {
			keyword = keyword.trim();
		}

		UserInfo userInfo = getUserInfo();

		// 要查询的用户ID
		Integer searchUserId = null;

		// 要查询的组织ID
		List<Integer> searchDepartmentIds = new ArrayList<>();

		if (userInfo.isOwner()) {
			searchDepartmentIds = userInfo.getVisualDepartmentIds();

		} else if (userInfo.isAdmin()) {
			// 查询用户所属公司ID列表
			List<Integer> userDepartmentIds = relateDepartmentUserService.getDepartmentIdsByUserId(userInfo.getId());
			for (Integer userDepartmentId : userDepartmentIds) {
				Department company = departmentService.getCompanyByChildrenId(userDepartmentId);
				if (company != null && !searchDepartmentIds.contains(company.getId())) {
					// 将用户所属组织的公司ID添加至搜索集合
					searchDepartmentIds.add(company.getId());
				}
			}

			// 查询该用户所属公司下所有组织ID列表,并添加至搜索集合
			List<Integer> childrenIds = departmentService.getChildrenIdsByOrgAndParentsAndType(userInfo.getOrgId(), searchDepartmentIds, null);
			if (childrenIds != null && !childrenIds.isEmpty()) {
				for (Integer childrenId : childrenIds) {
					if (!searchDepartmentIds.contains(childrenId)) {
						searchDepartmentIds.add(childrenId);
					}
				}
			}
		} else {
			searchUserId = userInfo.getId();
			searchDepartmentIds = userInfo.getVisualDepartmentIds();
		}

		// 如果用户搜索指定组织，则只查询指定组织下的组织ID列表
		if (departmentId != null && searchDepartmentIds.contains(departmentId)) {
			List<Integer> childrenIds = departmentService.getChildrenIdsByOrgAndParentAndType(userInfo.getOrgId(), departmentId, null);
			childrenIds.add(departmentId);
			searchDepartmentIds = childrenIds;
		}

		// 查询在线设备列表，查询时用作过滤条件
		Set<String> deviceIds = new LinkedHashSet<>();
		Set<String> keys = redisUtil.keys(RedisGlobal.PING + "*");
		if (keys != null && !keys.isEmpty()) {
			for (String deviceId : keys) {
				deviceIds.add(deviceId.replace(RedisGlobal.PING, ""));
			}
		}

		boolean page = PageHelperUtil.startPage();
		List<Signet> deviceList = service.find(userInfo.getOrgId(), searchUserId, searchDepartmentIds, new ArrayList<>(deviceIds), keyword);
		if (ListUtils.isEmpty(deviceList)) {
			return ResultVO.OK();
		}

		List<Map<String, Object>> list = new ArrayList<>();
		for (Signet device : deviceList) {
			// 取迁移时间作为注册时间展示给客户看
			Date transferTime = device.getTransferTime();
			if (transferTime != null) {
				device.setCreateDate(transferTime);
			}
			Map<String, Object> map = BeanUtil.safeConvertMap(device);

			// 查询设备地址
			Addr location = addrService.get(device.getAddr());
			if (location != null) {
				map.put("location", location.getLocation());
			}

			// 网络状态
			Integer online = isOnline(device.getId());
			if (online == null) {
				map.put("online", false);
				if (StringUtils.isBlank(device.getNetwork())) {
					map.put("network", "4g");
				}
			} else {
				map.put("online", true);
			}


			list.add(map);
		}

		return ResultVO.OK(new PageEntity<>(deviceList).setList(list).setPaging(page));

//		List<SignetEntity> signetEntities = service.getByOwner(userInfo.getOrgId(), searchUserId, keyword, searchDepartmentIds, deviceIds);
//
//		// 处理在线状态
//		if (signetEntities != null && signetEntities.size() > 0) {
//			for (SignetEntity signetEntity : signetEntities) {
//				Integer id = signetEntity.getId();
//
//				Integer online = isOnline(id);
//				if (online == null) {
//					signetEntity.setOnline(false);
//
//					String network = signetEntity.getNetwork();
//					if (StringUtils.isBlank(network)) {
//						signetEntity.setNetwork("4g");
//					}
//
//				} else {
//					signetEntity.setOnline(true);
//				}
//
//			}
//		}
//
//		return ResultVO.Page(signetEntities, isPage);
	}

	@ApiOperation(value = "查询印章详情", notes = "查询印章详情", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "signetId", value = "印章ID", dataType = "int")
	})
	@GetMapping("/getBySignet")
	public ResultVO getBySignet(@RequestParam("signetId") Integer signetId) {

		/*
		 * 校验印章
		 */
		Signet signet = service.get(signetId);
		if (signet == null) {
			return ResultVO.FAIL("印章不存在");
		}

		/*
		 * 处理返回值
		 */
		SignetVoSelect res = new SignetVoSelect();
		BeanUtils.copyProperties(signet, res);

		/*
		 * 处理印章LOGO
		 */
		String logoFileId = signet.getLogo();
		if (StringUtils.isNotBlank(logoFileId)) {
			FileEntity entity = fileInfoService.getReduceImgURLByFileId(logoFileId);
			if (entity != null && StringUtils.isNotBlank(entity.getFileUrl())) {
				res.setFileEntity(entity);
			}
		}

		/*
		 * 审计人
		 */
		Integer auditorId = signet.getAuditorId();
		if (auditorId != null) {
			User auditor = userService.get(auditorId);
			if (auditor != null && auditor.getId() != null) {
				res.setAuditorId(auditorId);
				res.setAuditorName(auditor.getUserName());
			} else {
				res.setAuditorId(signet.getAuditorId());
				res.setAuditorName(signet.getAuditorName());
			}
		}

		/*
		 * 管章人
		 */
		Integer keeperId = signet.getKeeperId();
		if (keeperId != null) {
			User keeper = userService.get(keeperId);
			if (keeper != null && keeper.getId() != null) {
				res.setKeeperId(keeper.getId());
				res.setKeeperName(keeper.getUserName());
			} else {
				res.setKeeperId(signet.getKeeperId());
				res.setKeeperName(signet.getKeeperName());
			}
		}


		/*
		 * 高拍仪名称
		 */
		res.setMeterId(signet.getMeterId());
		Integer meterId = signet.getMeterId();
		Meter meter = meterService.get(meterId);
		if (meter != null) {
			res.setMeterName(meter.getName());
		}

		/*
		 * 印章类型
		 */
		res.setTypeId(signet.getTypeId());
		Integer deviceTypeId = signet.getTypeId();
		DeviceType dt = deviceTypeService.get(deviceTypeId);
		if (dt != null) {
			res.setTypeName(dt.getName());
		}

		/*
		 * 休眠时间
		 */
		res.setSleepTime(signet.getSleepTime());

		/*
		 * 印章地址
		 */
		Integer addrId = signet.getAddr();
		Addr addr = addrService.get(addrId);
		if (addr != null) {
			res.setLocation(addr.getLocation());
			res.setLatitude(addr.getLatitude());
			res.setLongitude(addr.getLongitude());
		}

		/*
		 * 在线状态
		 */
		boolean app = isApp();
		Integer online = isOnline(signet.getId());// null:不在线  0:在线、关锁 1:在线、开锁
		if (app) {
			res.setIsOnline(online != null ? 1 : 0);
		}

		/*
		 * 网络状态
		 */
		res.setNetwork(online != null ? signet.getNetwork() : "4G");

		/*设备到期时间*/
		String uuid = signet.getUuid();
		DeviceMigrateLog dml = deviceMigrateLogService.getLastByUUID(uuid);
		if (dml != null) {
			res.setMigrateDate(dml.getUpdateDate());
		}

		return ResultVO.OK(res);
	}


	@ApiOperation(value = "更新印章信息", notes = "更新印章信息", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "signetId", value = "印章ID", dataType = "int"),
			@ApiImplicitParam(name = "name", value = "印章名称", dataType = "int"),
			@ApiImplicitParam(name = "keeperId", value = "印章管理员ID", dataType = "int"),
			@ApiImplicitParam(name = "auditorId", value = "审计员ID", dataType = "int"),
			@ApiImplicitParam(name = "typeId", value = "印章类型ID", dataType = "int"),
			@ApiImplicitParam(name = "meterId", value = "绑定的高拍仪ID", dataType = "int"),
			@ApiImplicitParam(name = "remark", value = "印章描述", dataType = "int"),
			@ApiImplicitParam(name = "departmentId", value = "组织ID", dataType = "int"),
			@ApiImplicitParam(name = "fileId", value = "本地存储文件UUID", dataType = "String"),
			@ApiImplicitParam(name = "fileName", value = "对象存储附件名称", dataType = "String"),
			@ApiImplicitParam(name = "bucketName", value = "对象存储容器名称", dataType = "String"),
			@ApiImplicitParam(name = "secretKey", value = "对象存储秘钥", dataType = "String"),
			@ApiImplicitParam(name = "isOos", value = "是否对象存储 true:是 false:不是", dataType = "boolean", defaultValue = "false")
	})
	@WebLogger(value = "更新印章信息")
	@PostMapping("/update")
	public ResultVO update(@RequestParam Integer signetId,
						   @RequestParam String name,
						   @RequestParam Integer departmentId,
						   @RequestParam Integer keeperId,
						   @RequestParam Integer auditorId,
						   @RequestParam(required = false) String bodyId,
						   @RequestParam(value = "typeId", required = false) Integer typeId,
						   @RequestParam(value = "meterId", required = false) Integer meterId,
						   @RequestParam(value = "remark", required = false) String remark,
						   @RequestParam(value = "fileId", required = false) String fileId) {
		/*校验印章有效性*/
		Signet signet = service.get(signetId);
		if (signet == null) {
			return ResultVO.FAIL("该设备不存在");
		}
		UserInfo userInfo = getUserInfo();

		/*校验印章名称*/
		if (StringUtils.isBlank(name)) {
			return ResultVO.FAIL("印章名称不能为空");
		}
		if (EmojiFilter.containsEmoji(name)) {
			return ResultVO.FAIL("印章名称不能包含特殊字符");
		}
		if (!name.equalsIgnoreCase(signet.getName())) {
			Signet signetByName = service.getByName(name, userInfo.getOrgId());
			if (signetByName != null && signetByName.getId().intValue() != signetId) {
				return ResultVO.FAIL("印章名称重复");
			}
		}

		/*校验印章描述*/
		if (StringUtils.isNotBlank(remark) && EmojiFilter.containsEmoji(remark)) {
			return ResultVO.FAIL("印章描述不能包含特殊字符");
		}

		/*校验印章管理员*/
		if (keeperId == null) {
			return ResultVO.FAIL("印章管理员不能为空");
		}
		User keeper = userService.get(keeperId);
		if (keeper == null || keeper.getOrgId().intValue() != userInfo.getOrgId()) {
			return ResultVO.FAIL("印章管理员不存在");
		}

		/*校验审计员*/
		if (auditorId == null) {
			return ResultVO.FAIL("印章审计员不能为空");
		}
		User auditor = userService.get(auditorId);
		if (auditor == null || auditor.getOrgId().intValue() != userInfo.getOrgId()) {
			return ResultVO.FAIL("审计员无效");
		}

		/*校验印章LOGO*/
		FileInfo fileInfo = null;
		if (StringUtils.isNotBlank(fileId)) {
			fileInfo = fileInfoService.get(fileId);
			if (fileInfo == null) {
				return ResultVO.FAIL("设备LOGO不存在");
			}
		}

		/*校验组织*/
		if (departmentId == null) {
			return ResultVO.FAIL("组织信息不能为空");
		}
		Department department = departmentService.get(departmentId);
		if (department == null) {
			return ResultVO.FAIL("组织不存在");
		}
		if (!CommonUtils.isEquals(signet.getDepartmentId(), departmentId)) {
			List<Integer> departmentIds = userInfo.getDepartmentIds();
			if (departmentIds == null || departmentIds.isEmpty()) {
				return ResultVO.FAIL("暂无权限修改设备组织信息");
			}
			if (!departmentIds.contains(departmentId)) {
				return ResultVO.FAIL("仅能迁移设备至所负责的组织中");
			}
		}

		/*校验印章类型*/
		DeviceType deviceType = null;
		if (typeId != null) {
			deviceType = deviceTypeService.get(typeId);
			if (deviceType == null || deviceType.getOrgId().intValue() != userInfo.getOrgId()) {
				return ResultVO.FAIL("印章类型有误");
			}
		}

		service.updateSignet(signet, bodyId, name, fileInfo, keeper, auditor, department, deviceType, meterId, remark);

		return ResultVO.OK("修改成功");
	}


	@ApiOperation(value = "查询可申请的印章列表", notes = "查询可申请的印章列表")
	@RequestMapping("/getByOrg")
	public ResultVO getByOrg(@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
							 @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
							 @RequestParam(value = "page", required = false, defaultValue = "true") boolean isPage) {
//		List<Signet> signets;
//		UserInfo userInfo = getUserInfo();
//		if (userInfo.isOwner()) {
//			signets = service.getByOrg(userInfo.getOrgId());
//		} else {
////			List<Integer> searchDepartmentIds = departmentService.getDepartmentIdsByAppSignet(userInfo.getId());
//			signets = service.getSignetByOrgAndDepartments(userInfo.getOrgId(), userInfo.getVisualDepartmentIds(), userInfo.getId());
//		}

		//开启分页
		if (isPage) {
			PageHelper.startPage(pageNum, pageSize);
		}

		//查询列表
		UserInfo userInfo = getUserInfo();
		Integer orgId = userInfo.getOrgId();
		List<Integer> departmentIds = userInfo.getVisualDepartmentIds();
		List<Signet> signets = service.get(orgId, departmentIds, 1);

		return ResultVO.Page(signets, isPage);
	}

	@ApiOperation(value = "获取所有的设备阈值", notes = "获取所有的设备阈值", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "keyword", value = "设备名称", dataType = "string"),
			@ApiImplicitParam(name = "pageNum", value = "当前页(默认:1)", dataType = "int"),
			@ApiImplicitParam(name = "pageSize", value = "每页数(默认:10)", dataType = "int"),
			@ApiImplicitParam(name = "departmentId", value = "组织ID", dataType = "int"),
			@ApiImplicitParam(name = "page", value = "是否分页", dataType = "boolean", defaultValue = "true")
	})
	@GetMapping("/getThresholds")
	public ResultVO getThresholds(@RequestParam(value = "keyword", required = false) String keyword, @RequestParam(value = "page", required = false, defaultValue = "true") boolean isPage) {
		UserToken token = getToken();
		Integer orgId = token.getOrgId();

		if (StringUtils.isNotBlank(keyword)) {
			keyword = keyword.trim();
		}

		//查询印章阈值列表
		List<ThresholdEntity> thresholdEntities = thresholdService.getByOrgAndDepartmentAndKeeper(token.getOrgId(), keyword);
		if (thresholdEntities == null || thresholdEntities.isEmpty()) {
			return ResultVO.OK();
		}

		//查询全局阈值
		Threshold defaultThreshold = thresholdService.getDefaultByOrg(orgId);
		defaultThreshold = getThreshold(defaultThreshold, token.getOrgId());

		//组装返回值
		for (ThresholdEntity thresholdEntity : thresholdEntities) {
			Integer thresholdValue = thresholdEntity.getThresholdValue();
			if (thresholdValue == null) {
				thresholdEntity.setThresholdValue(defaultThreshold.getThresholdValue());
			}
		}
		return ResultVO.Page(thresholdEntities, isPage);
	}

	private Threshold getThreshold(Threshold defaultThreshold, Integer orgId) {
		if (defaultThreshold == null) {
			//初始没有值,设置总阈值
			defaultThreshold = new Threshold();
			defaultThreshold.setName("全局阈值");
			defaultThreshold.setOrgId(orgId);
			defaultThreshold.setDeviceId(0);//针对所有印章，此处deviceId设置为0
			defaultThreshold.setThresholdValue(100);
			defaultThreshold.setRemark("全局阈值设置");
			defaultThreshold.setCreateDate(new Date());
			thresholdService.add(defaultThreshold);
		}
		return defaultThreshold;
	}

	@ApiOperation(value = "更新设备阈值数据", notes = "更新设备阈值数据", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "deviceId", value = "设备ID 0:总阈值 非0：指定印章", dataType = "int", required = true),
			@ApiImplicitParam(name = "thresholdValue", value = "阈值", dataType = "int", required = true)
	})
	@WebLogger("更新阈值")
	@PostMapping("/updateThreshold")
	public ResultVO updateThreshold(@RequestParam("deviceId") Integer deviceId, @RequestParam("thresholdValue") Integer thresholdValue) {

		UserInfo userInfo = getUserInfo();
		//参数校验：设备ID
		if (deviceId == null) {
			return ResultVO.FAIL("请求设备参数有误");
		}

		if (deviceId != 0) {
			Signet signet = service.get(deviceId);
			if (signet == null || signet.getOrgId() != userInfo.getOrgId().intValue()) {
				return ResultVO.FAIL("设备不存在");
			}
		}

		//参数校验：阈值
		if (thresholdValue == null) {
			return ResultVO.FAIL("请求阈值参数有误");
		}
		if (thresholdValue <= 0) {
			return ResultVO.FAIL("阈值不能小于10%");
		}
		if (thresholdValue > 100) {
			thresholdValue = 100;
		}

//		//校验操作人权限
//		if (!userInfo.isOwner() && !userInfo.isAdmin()) {
//			Integer keeperId = signet.getKeeperId();
//			Integer departmentId = signet.getDepartmentId();
//
//			//如果是印章管理员或负责人，有权限
//			if ((keeperId != null && keeperId.intValue() == userInfo.getId())
//					|| (userInfo.getDepartmentIds() != null && userInfo.getDepartmentIds().contains(departmentId))
//					|| (userInfo.isAdmin() && userInfo.getVisualDepartmentIds().contains(departmentId))) {
//				//印章管理员/组织负责人 :允许
//			} else {
//				return ResultVO.FAIL("无权限操作");
//			}
//		}

		thresholdService.updateThreshold(userInfo.getOrgId(), deviceId, thresholdValue);

		return ResultVO.OK("更新成功");
	}

	@ApiOperation(value = "印章迁移", notes = "印章迁移", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "orgId", value = "迁移的公司id", dataType = "int", required = true),
			@ApiImplicitParam(name = "signetId", value = "印章id", dataType = "int", required = true)
	})
	@WebLogger("印章迁移")
	@PostMapping("/migrate")
	@Transactional
	public ResultVO migrate(@RequestParam("orgId") Integer orgId,
							@RequestParam("signetId") Integer signetId) {
		if (getToken().getOrgId() != -1) {
			return ResultVO.FAIL(Code.FAIL403);
		}
		if (signetId == null || orgId == null) {
			return ResultVO.FAIL(Code.FAIL402);
		}
		Signet signet = service.get(signetId);
		if (signet == null) {
			return ResultVO.FAIL("设备不存在");
		}
		UserInfo info = getUserInfo();

		//印章和本公司在一起吗
		Org destOrg = orgService.get(orgId);
		if (destOrg == null || destOrg.getId() == null) {
			return ResultVO.FAIL("该公司不存在或已被删除");
		}

		//查询该印章在新公司最大次数
		int total = sealRecordInfoService.getTotalBySignetAndOrg(signet.getId(), orgId);

		//组包
		DeviceInit di = new DeviceInit();
		di.setInitCount(total);
		String newOrgName = destOrg.getName();
		di.setInitOrgName(newOrgName.length() < 4 ? newOrgName : newOrgName.substring(0, 4));

		//原公司名称
		Org org = orgService.get(signet.getOrgId());
		String oldOrgName = org == null ? null : org.getName();
		di.setOldOrgName(oldOrgName);

		//新公司名称
		di.setNewOrgName(destOrg.getName());
		di.setInitOrgId(destOrg.getId());

		//新增迁移日志
		try {
			DeviceMigrateLog log = new DeviceMigrateLog();
			log.setDeviceId(signet.getId());
			log.setUserId(info.getId());
			log.setNewOrgId(destOrg.getId());

			//查询设备源服务器
			Config config = configService.getByUUID(signet.getUuid());
			if (config != null) {
				log.setSrcHost(config.getSvrHost());
				log.setDestHost(config.getSvrHost());
			}
			log.setUuid(signet.getUuid());
			log.setOldOrgId(signet.getOrgId());
			log.setMigrateStatus(AppConstant.MIGRATE_UNKNOWN);
			deviceMigrateLogService.add(log);
			di.setTaskId(log.getId());
		} catch (Exception e) {
			log.error("出现异常 ", e);
		}

		MHPkg res = MHPkg.res(AppConstant.DEVICE_INIT_CLEAR_REQ, di);

		Integer online = isOnline(signetId);
		if (online != null && online == 1) {
			return ResultVO.FAIL("很抱歉，设备当前正在使用中，无法迁移");
		}
		if (online == null) {
			//如果设备不在线,记录离线消息,开机后推送
			DeviceMessage dm = new DeviceMessage();
			dm.setBody(JSONObject.toJSONString(res));
			dm.setTitle(Global.set_migrate);
			dm.setPushStatus(1);
			dm.setSendId(info.getId());
			dm.setRecipientId(signetId);
			deviceMessageService.addOrUpdate(dm);

			//修改数据库
			//toMigrate(signet, info, destOrg, total);

			return ResultVO.OK("设备当前不在线,迁移初始化指令会在设备开机后执行");
		}

		//设备在线,直接推送到队列
		MQPKG mqpkg = new MQPKG();
		mqpkg.setDeviceId(signetId);
		mqpkg.setUserId(info.getId());
		mqpkg.setData(JSONObject.toJSONString(res));
		mqpkg.setUserName(info.getUserName());
		mqpkg.setCmd(MqGlobal.SIGNET_MIGRATE);
		mqpkg.setOrgId(destOrg.getId());

		//将消息体推送到消息队列
		mqSender.sendToExchange(CommonUtils.getProperties().getRabbitMq().getExchangeOrder(), mqpkg);
		log.info("-\tMQ-下发指令-序列号:{}\t迁移印章\tuserId:{}\tname:{}\tmessage:{}", mqpkg.getSerialId(), info.getId(), info.getUserName(), CommonUtils.objJsonWithIgnoreFiled(mqpkg));

		//修改数据库
		//toMigrate(signet, info, destOrg, total);

		//删除设备中的缓存信息
		delSignetByCache(signet, orgId);
		return ResultVO.OK("迁移指令已下发");
	}

	/**
	 * 重置缓存中的设备相关信息
	 *
	 * @param signet 设备信息
	 */
	private void delSignetByCache(Signet signet, Integer oldOrgId) {
		String InfoKey = RedisGlobal.DEVICE_INFO + signet.getUuid();
		redisUtil.del(InfoKey);
	}

	@ApiOperation(value = "设备列表", notes = "设备列表", httpMethod = "GET")
	@GetMapping("/deviceList")
	public ResultVO deviceList() {
		UserInfo userInfo = getUserInfo();
		Integer orgId = userInfo.getOrgId();

		//TODO:权限及数据隔离性检查

		List<Signet> deviceList = service.getByOrg(orgId);
		if (deviceList == null || deviceList.isEmpty()) {
			return ResultVO.OK();
		}

		List<Map<String, Object>> mapList = new ArrayList<>(deviceList.size());
		for (Signet device : deviceList) {
			Map<String, Object> map = new HashMap<>(2);
			map.put("id", device.getId());
			map.put("name", device.getName());
			mapList.add(map);
		}
		return ResultVO.OK(mapList);
	}
}
