package com.yunxi.stamper.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yunxi.stamper.base.BaseService;
import com.yunxi.stamper.commons.device.*;
import com.yunxi.stamper.commons.device.model.*;
import com.yunxi.stamper.commons.device.modelVo.*;
import com.yunxi.stamper.commons.jwt.AES.AesUtil;
import com.yunxi.stamper.commons.jwt.RSA.MyKeyFactory;
import com.yunxi.stamper.commons.jwt.RSA.RsaUtil;
import com.yunxi.stamper.commons.other.AppConstant;
import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.other.Global;
import com.yunxi.stamper.commons.other.RedisGlobal;
import com.yunxi.stamper.entity.*;
import com.yunxi.stamper.service.*;
import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.sys.lock.LockGlobal;
import com.yunxi.stamper.websocket.container.WebSocketMap;
import com.yunxi.stamper.websocket.core.WsMessage;
import com.yunxi.stamper.websocket.core.WsSocket;
import com.yunxi.stamper.websocket.core.WsUtils;
import com.zengtengpeng.annotation.Lock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Decoder;

import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/2 0002 19:33
 */
@Slf4j
@Service
public class IDeviceWebSocketService extends BaseService implements DeviceWebSocketService {

	@Autowired
	private SignetService signetService;
	@Autowired
	private OrgService orgService;
	@Autowired
	private DeviceMigrateLogService deviceMigrateLogService;
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private UserService userService;
	@Autowired
	private SealRecordInfoService sealRecordInfoService;
	@Autowired
	private DeviceAsyncService deviceAsyncService;
	@Autowired
	private FingerService fingerService;
	@Autowired
	private AddrService addrService;
	@Autowired
	private MessageTempService messageTempService;
	@Autowired
	private ApplicationService applicationService;
	@Autowired
	private ThresholdService thresholdService;

	/**
	 * 校验WS通道的设备信息
	 *
	 * @param webSocket WS
	 * @param message   消息内容(包含心跳消息、业务消息等)
	 */
	private void checkIdentity(WsSocket webSocket, String message) {
		String uuid = webSocket.getUid();
		Integer deviceId = webSocket.getDeviceId();
		Integer orgId = webSocket.getOrgId();

		if (StringUtils.isNotBlank(uuid) && deviceId != null && orgId != null) {
			return;
		}

		if (message.startsWith("ping") || message.startsWith("pong")) {
			//心跳消息
			uuid = WsUtils.parseHeartText(message);
			Signet device = signetService.getByUUID(uuid);
			if (device != null) {
				deviceId = device.getId();
				orgId = device.getOrgId();
			}
		} else {
			//业务消息
			WsMessage wm = JSONObject.parseObject(message, WsMessage.class);
			if (wm != null && StringUtils.isNotBlank(wm.getUuid())) {
				uuid = wm.getUuid();
				Signet device = signetService.getByUUID(uuid);
				if (device != null) {
					deviceId = device.getId();
					orgId = device.getOrgId();
				}
			}
		}
		webSocket.setUid(uuid);
		webSocket.setDeviceId(deviceId);
		webSocket.setOrgId(orgId);
	}

	/**
	 * 接待websocket发送过来的请求
	 * ps:上一层做了过滤,次数message一定不会空
	 *
	 * @param message   消息
	 * @param webSocket 通道
	 */
	@Override
	@Async("wsBizAsyncTaskExecutor")
	public void doWork(String message, WsSocket webSocket) {
		try {
			working(message, webSocket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void working(String message, WsSocket webSocket) throws Exception {
		//确认WS的消息和身份是否合法
		try {
			checkIdentity(webSocket, message);
		} catch (Exception e) {
			log.info("xx\tws未知设备\tws:{}\tmessage:{}", webSocket.toString(), message, e);
			return;
		}
		String ip = webSocket.getIp();
		Integer deviceId = webSocket.getDeviceId();
		Integer orgId = webSocket.getOrgId();
		boolean busy = webSocket.isBusy();

		log.info("--\tws消息\tdeviceId:{}\tip:{}\torgId:{}\tmessage:{}", deviceId, ip, orgId, message);

		//将设备添加至Map
		WebSocketMap.add(deviceId, webSocket);

		//确认WS消息为合法消息后，更新WS心跳时间
		webSocket.setLastHeartbeat(new Date());

		//心跳逻辑处理,设备端每隔一段时间发送消息，使通道处于非空闲状态，防止nginx或tomcat等容器框架将通道无故关闭
		redisUtil.set(RedisGlobal.PING + deviceId, busy, RedisGlobal.PING_TIME_OUT);//标记设备在线
		if (message.startsWith("ping")) {
			webSocket.sendByBasic("pong");
			return;
		} else if (message.startsWith("pong")) {
			return;
		}

		//使用Aes||Rsa解密
		WsMessage wsMessage = null;
		try {
			wsMessage = parseEncrypt(message, webSocket);
		} catch (Exception e) {
			log.info("xx\tws消息-解密异常\tdeviceId:{}\tip:{}\torgId:{}\tbusy:{}\tmessage:{}\terror:{}", deviceId, ip, orgId, busy, message, e.getMessage());
			return;
		}
		if (wsMessage == null || StringUtils.isBlank(wsMessage.getContext())) {
			log.info("xx\tws消息-解密失败\tdeviceId:{}\tip:{}\torgId:{}\tbusy:{}\tmessage:{}\tcontext:{}", deviceId, ip, orgId, busy, message, wsMessage == null ? "" : wsMessage.getContext());
			return;
		}

		log.info("--\tws消息\tdeviceId:{}\tip:{}\torgId:{}\tbusy:{}\tcmd:{}\tmessage:{}", deviceId, ip, orgId, busy, wsMessage.getCmd(), wsMessage.getContext());

		//解析消息CMD协议号
		int cmd = wsMessage.getCmd();
		message = wsMessage.getContext();

		try {
			switch (cmd) {
				case AppConstant.DEVICE_REG_REQ: //注册请求
					deviceRegReq(message, webSocket);
					break;

				case AppConstant.DEVICE_LOGIN_REQ: //登录请求
					deviceLoginReq(message, webSocket);
					break;

				case AppConstant.FP_RECORD_RES:    //指纹录入返回
					fpRecordRes(message, webSocket.getUid());
					break;

				case AppConstant.FP_CLEAR_RES:    //指纹清空的返回结果
					fpClearRes(message, webSocket.getUid());
					break;

				case AppConstant.WIFI_INFO_RES:    //网络状态改变返回
					getWifiInfoRes(message, webSocket.getUid());
					break;

				case AppConstant.DEVICE_USED_RES:    //印章开关锁状态的返回
					deviceUsingRes(message, webSocket.getDeviceId());
					break;

				case AppConstant.IS_AUDIT_REQ:    //设备上传地址坐标信息
					updatePosition(message, webSocket.getUid());
					break;

				case AppConstant.WIFI_LIST_RES:    //wifi列表返回结果
					getWifiListRes(message, webSocket.getUid());
					break;

				case AppConstant.TAKE_PIC_RES:    //印章通知高拍仪进行拍照
					highDeviceOnUsing(message, webSocket.getUid());
					break;

				case AppConstant.USE_MODEL_RETURN_RES:    //开启/关闭指纹模式 状态返回
					updateSignetModel(message, webSocket.getUid());
					break;

				case AppConstant.REMOTE_LOCK_RETURN_RES:    //远程锁定功能 状态返回
					updateRemoteLock(message, webSocket.getUid());
					break;

				case AppConstant.SLEEP_TIME_RETURN_RES:    //设置休眠 状态返回
					updateSleepTime(message, webSocket.getUid());
					break;

				case AppConstant.DEVICE_MIGRATE_CALLBACK:    //迁移确认返回
					migrateCallBack(message, webSocket.getUid());
					break;

				case AppConstant.REMOTE_CAMERA_SWITCH_RES:  //摄像头设置返回
					cameraSwitch(message, webSocket.getUid());
					break;

				default:
					log.info("未知协议请求 cmd:{} message:{} device:{}", cmd, message, webSocket.getUid() + "_" + webSocket.getDeviceId());
					break;
			}
		} catch (Exception e) {
			log.error("出现异常 ", e);
			throw new PrintException(e.getMessage());
		}
	}

	/**
	 * 设备开启/关闭摄像头后返回
	 *
	 * @param message 消息内容
	 * @param uuid    设备uuid
	 */
	private void cameraSwitch(String message, String uuid) {
		Signet signet = signetService.getByUUID(uuid);
		if (signet == null) {
			return;
		}

		MHPkg pkg = JSONObject.parseObject(message, MHPkg.class);
		String data = pkg.getBody().toString();
		JSONObject jsonObject = JSONObject.parseObject(data);
		String status = jsonObject.getString("status");

		if (StringUtils.equalsIgnoreCase(status, "0")) {
			signetService.updateCamera(signet.getId(), 0);
			log.info("-\t设备:{}\t摄像头开启", signet.getId());
		} else {
			signetService.updateCamera(signet.getId(), 1);
			log.info("-\t设备:{}\t摄像头关闭", signet.getId());
		}

		//更新缓存
		try {
			String key = RedisGlobal.DEVICE_INFO + signet.getUuid();
			redisUtil.del(key);
		} catch (Exception e) {
			log.error("出现异常 ", e);
		}
	}

	/**
	 * 设备迁移成功/失败返还确认
	 *
	 * @param message 设备通道中返回的消息内容
	 * @param uuid    设备UUID
	 */
	private void migrateCallBack(String message, String uuid) {
		Signet signet = signetService.getByUUID(uuid);
		if (signet == null) {
			return;
		}

		MHPkg pkg = JSONObject.parseObject(message, MHPkg.class);
		String data = pkg.getBody().toString();

		DeviceMigrateCallback deviceMigrateCallback = JSONObject.parseObject(data, DeviceMigrateCallback.class);
		if (deviceMigrateCallback == null) {
			return;
		}

		Integer taskId = deviceMigrateCallback.getTaskId();
		DeviceMigrateLog log = deviceMigrateLogService.get(taskId);
		if (log == null) {
			return;
		}

		/*更新日志记录状态*/
		log.setMigrateStatus(deviceMigrateCallback.getStatus());
		deviceMigrateLogService.update(log);

		/*处理迁移工作*/
		if (deviceMigrateCallback.getStatus() == 1) {
			migrate(signet, log.getNewOrgId());
		}
	}

	/**
	 * 设备迁移、初始化设备参数
	 *
	 * @param signet   设备信息
	 * @param newOrgId 新组织ID
	 */
	private void migrate(Signet signet, Integer newOrgId) {
		try {
			Org destOrg = orgService.get(newOrgId);
			if (destOrg != null) {
				/*开始迁移并初始化设备信息*/
				signet.setOrgId(destOrg.getId());
				signet.setOrgName(destOrg.getName());

				Department root = departmentService.getRootByOrg(destOrg.getId());
				signet.setDepartmentId(root.getId());
				signet.setDepartmentName(root.getName());

				Integer managerUserId = root.getManagerUserId();
				User managerUser = userService.get(managerUserId);
				signet.setKeeperId(managerUser.getId());
				signet.setKeeperName(managerUser.getUserName());
				signet.setAuditorId(managerUser.getId());
				signet.setAuditorName(managerUser.getUserName());

				signet.setMeterId(null);

				//记录设备迁移时间
				signet.setTransferTime(new Date());

				/*查询该印章在新公司最大次数(最大次数不是使用记录总数，而是在该公司使用次数最大'MAX'次数值)*/
				int total = sealRecordInfoService.getTotalBySignetAndOrg(signet.getId(), destOrg.getId());
				signet.setCount(total);

				signet.setTypeId(null);
				signet.setName("印章(新" + signet.getId() + ")");
				signet.setRemark(null);
				signet.setLogo(null);
				signetService.update(signet);

				//将设备原来组织的阈值清空
				List<Threshold> thresholds = thresholdService.getByDevice(signet.getId());
				if (thresholds != null && !thresholds.isEmpty()) {
					for (Threshold threshold : thresholds) {
						thresholdService.del(threshold);
					}
				}

			} else {
				//TODO:如果设备当前组织ORG_ID在数据库不存在，如何处理
			}
		} catch (Exception e) {
			log.error("出现异常 ", e);
			//TODO:如果处理迁移记录失败，怎么办
		}
	}

	/**
	 * V2版,解析json格式密文，
	 * 如果设备未注册状态，在解析json成功状态下，尝试将该通道设置为注册状态，以便后续进行业务处理
	 *
	 * @param message 密文
	 * @return 明文
	 */
	public WsMessage parseEncrypt(String message, WsSocket socket) throws Exception {
		WsMessage wsMessage = JSONObject.parseObject(message, WsMessage.class);
		if (wsMessage == null) {
			log.error("Webscoket数据格式有误\tdeviceId:{}\tip:{}\tuuid:{}\tmessage:{}", socket.getDeviceId(), socket.getIp(), socket.getUid(), message);
			throw new RuntimeException("数据解密异常");
		}

		//客户端请求的消息参数
		String uuid = wsMessage.getUuid();
		String encrypt = wsMessage.getEncrypt();

		//AES解密
		if (wsMessage.getCmd() == 1) {
			//取出对称秘钥&AES解密
			Object symmetricKey = redisUtil.get(RedisGlobal.AES_KEY + socket.getDeviceId());
			if (symmetricKey != null && StringUtils.isNotBlank(symmetricKey.toString())) {
				message = AesUtil.decrypt(encrypt, symmetricKey.toString());
				wsMessage.setContext(message);
			}
		}
		//RSA解密
		else if (wsMessage.getCmd() == 0) {
			byte[] bytes = RsaUtil.decryptByPrivateKeyForSpilt(new BASE64Decoder().decodeBuffer(encrypt), MyKeyFactory.getPrivateKey());
			message = new String(bytes, StandardCharsets.UTF_8);
			wsMessage.setContext(message);
		}

		socket.setUid(uuid);

		//将明文解析成消息包&取出协议号
		if (StringUtils.isNotBlank(wsMessage.getContext())) {
			MHPkg mhPkg = JSONObject.parseObject(wsMessage.getContext(), MHPkg.class);
			int cmd = mhPkg.getHead().getCmd();
			wsMessage.setCmd(cmd);
		}

		String ip = socket.getIp();
		Integer deviceId = socket.getDeviceId();
		socket.setUid(uuid);
		socket.setIp(ip);
		log.info("ws消息\tdeviceId:{}\tip:{}\tuuid:{}\tmessage:{}", deviceId, ip, uuid, message);
		return wsMessage;
	}

	/**
	 * 设置休眠 状态返回
	 * {"Body":{"res":0,"sleepTime":4},"Head":{"Magic":42949207,"Cmd":83,"SerialNum":980,"Version":1}}
	 */
	@Override
	public void updateSleepTime(String message, @NotNull String uuid) {
		DeviceSleepTime sleepTime = JSONObject.parseObject(message, DeviceSleepTimePkg.class).getBody();

		if (sleepTime != null && sleepTime.getRes() != null && sleepTime.getRes() == 0) {
			Integer times = sleepTime.getSleepTime();//休眠时间 2~10 分钟

			if (StringUtils.isNotBlank(uuid)) {
				Signet signet = signetService.getByUUID(uuid);
				if (signet != null) {
					signet.setSleepTime(times);
					signetService.update(signet);
					log.info("√ 休眠状态  设备：{} 休眠：{}分钟", signet.getName(), times);
				}
			}
		}
	}

	/**
	 * 远程锁定功能 状态返回
	 * {"Body":{"res":0,"status":1},"Head":{"Magic":42949207,"Cmd":91,"SerialNum":901,"Version":1}}
	 */
	@Override
	public void updateRemoteLock(String message, @NotNull String uuid) {
		DeviceLock lock = JSONObject.parseObject(message, DeviceLockPkg.class).getBody();

		if (lock != null && lock.getRes() != null && lock.getRes() == 0) {
			Integer status = lock.getStatus();//0:解锁  1:锁定

			if (StringUtils.isNotBlank(uuid)) {
				Signet signet = signetService.getByUUID(uuid);
				if (signet != null && status != null) {
					signet.setStatus(status == 1 ? Global.DEVICE_LOCK : Global.DEVICE_NORMAL);//0:正常 1:异常 2:销毁 3:停用 4:锁定
					signetService.update(signet);
					log.info("√ 远程锁定  设备：{} 状态：{}", signet.getName(), status == 2 ? "锁定" : "解锁");
				}
			}
		}
	}

	/**
	 * 印章使用模式状态返回
	 * {"Body":{"res":0,"useModel":1},"Head":{"Magic":42949207,"Cmd":87,"SerialNum":672,"Version":1}}
	 */
	@Override
	public void updateSignetModel(String message, String uuid) {
		DeviceModel model = JSONObject.parseObject(message, DeviceModelPkg.class).getBody();

		if (model != null && model.getRes() != null && model.getRes() == 0) {
			Integer status = model.getUseModel();//0：关闭指纹模式 1：打开指纹模式

			if (StringUtils.isNotBlank(uuid)) {
				Signet signet = signetService.getByUUID(uuid);
				if (signet != null && status != null) {
					signet.setFingerPattern(status == 1);//0：不开启 1：开启
					signetService.update(signet);
					log.info("√ 模式切换 设备：{} 模式：{}", signet.getId(), signet.getFingerPattern() ? "指纹模式(开)" : "指纹模式(关)");
				} else {
					log.info("× 模式切换 设备：{} 模式：{}设备不存在", uuid, (signet != null && signet.getFingerPattern()) ? "指纹模式(开)" : "指纹模式(关)");
				}
			}
		} else {
			log.info("× 模式切换 设备：{} 消息体不存在", uuid);
		}
	}

	/**
	 * 设备上传地址坐标信息
	 *
	 * @param message 消息体
	 * @param uuid    设备UUID
	 */
	@Override
	public void updatePosition(String message, String uuid) {
		// 解析消息体
		LocationRes res = JSONObject.parseObject(message, LocationPkg.class).getBody();

		// 坐标与具体地址不为空的情况,才存储并更新设备信息
		if (!isAddOrUpdatePosition(res)) {
			log.info("× 地址同步 设备:{} 消息体：{} 消息体有误", uuid, JSONObject.toJSONString(res));
			return;
		}

		// 查询或保存坐标
		Addr addr = addrService.getByLocation(res.getAddr());
		if (addr == null) {
			addr = new Addr();
			addr.setLatitude(res.getLatitude());
			addr.setLocation(res.getAddr());
			addr.setLongitude(res.getLongitude());

			// 地址中是否存在 省市区
			String p = res.getProvince();
			String c = res.getCity();
			String d = res.getDistrict();

			if (StringUtils.isNoneBlank(p, c, d)) {
				addr.setProvince(p);
				addr.setCity(c);
				addr.setDistrict(d);
				addrService.add(addr);
			} else {
				log.info("× 地址同步 设备:{} 消息体：{} 消息体有误", uuid, JSONObject.toJSONString(res));
				return;
			}
		}

		//更新设备地址坐标信息
		Signet signet = signetService.getByUUID(uuid);
		if (signet == null) {
			log.info("- 地址同步 设备:{} 地址：{} 设备不存在", uuid, addr.getLocation());
			return;
		}
		signet.setAddr(addr.getId());
		signetService.update(signet);
		log.info("√ 地址同步 设备:{} 地址：{}", signet.getId(), addr.getLocation());
	}

	/**
	 * 校验坐标是否正确
	 *
	 * @param res 地址信息
	 * @return false:不正确  true：正确
	 */
	private boolean isAddOrUpdatePosition(LocationRes res) {
		String addr = res.getAddr();
		String latitude = res.getLatitude();
		String longitude = res.getLongitude();
		if (StringUtils.isAnyBlank(addr, latitude, longitude)) {
			return false;
		}
		return !("0.0".equalsIgnoreCase(latitude) || "0.0".equalsIgnoreCase(longitude));
	}

	/**
	 * 印章通知高拍仪进行拍照
	 */
	@Override
	public void highDeviceOnUsing(String message, @NotNull String uuid) {
		//解析消息体
		HighDeviceOnUseRes res = JSONObject.parseObject(message, HighDeviceOnUsingPkg.class).getBody();
		if (res == null) {
			log.info("× 同步高拍仪 设备：{} 消息体不存在:{}", uuid, message);
			return;
		}

		Integer applicationID = res.getApplicationID();
		Integer useTimes = res.getUseTimes();
		Signet signet = signetService.getByUUID(uuid);
		if (signet == null) {
			log.info("× 同步高拍仪 设备：{} 次数：{} 申请单：{} 设备不存在", uuid, useTimes, applicationID);
			return;
		}

		redisUtil.set(RedisGlobal.PING + signet.getId(), true, RedisGlobal.PING_TIME_OUT);
		WsSocket socket = WebSocketMap.get(signet.getId());
		if (socket != null) {
			socket.setBusy(true);
		}

		log.info("√ 同步高拍仪 设备：{} 次数：{} 申请单：{}", signet.getName(), useTimes, applicationID);

		//ver: 0:安卓3G 1:安卓3G量子 2:安卓4G 3:安卓4G量子 5:Linux-4G  6:单片机简易版
		//同步申请单已使用次数
		if (applicationID != null && applicationID != 0 && (signet.getVer() == null || signet.getVer() < 2)) {
			int localCount = sealRecordInfoService.getCountByApplication(applicationID);
			applicationService.synchApplicationInfo(signet.getId(), applicationID, localCount);
		}

	}

	/**
	 * 印章开关锁状态的返回
	 *
	 * @param message  消息体
	 * @param deviceId 设备ID
	 */
	@Override
	public void deviceUsingRes(String message, @NotNull Integer deviceId) {
		//解析返回值
		DeviceBeingUsedRes body = JSONObject.parseObject(message, DeviceBeingUsedResPkg.class).getBody();
		try {
			List<LoginApplication> loginApplicationInfos = body.getLoginApplicationInfo();
			if (loginApplicationInfos != null && loginApplicationInfos.size() > 0) {
//				Signet signet = signetService.get(deviceId);
				//同步印章最近5次申请单记录,解决印章无网情况下次数同步问题
//				synchApplicationInfo(signet, loginApplicationInfo);
				LoginApplication loginApplication = loginApplicationInfos.get(0);
				Integer applicationId = loginApplication.getApplicationId();
				if (applicationId != null && !Objects.equals(applicationId, 0)) {
					Integer useCount = loginApplication.getUseCount();
					applicationService.synchApplicationInfo(deviceId, applicationId, useCount);
				}
			}
		} catch (Exception e) {
			log.error("出现异常 ", e);
		}

		int status = body.getUsedStatus();//0:关锁 1:开锁
		redisUtil.set(RedisGlobal.PING + deviceId, status == 1, RedisGlobal.PING_TIME_OUT);

		WsSocket socket = WebSocketMap.get(deviceId);
		if (socket != null) {
			socket.setBusy(status == 1);
		}
		log.info("√ 开关锁状态 设备：{} 状态：{}", deviceId, status == 1 ? "开锁" : "关锁");
	}

	/**
	 * 网络状态改变返回
	 *
	 * @param message 消息体
	 * @param uuid    设备UUID
	 */
	@Override
	public void getWifiInfoRes(String message, String uuid) {
		//解析消息体
		WifiInfoRes body = JSONObject.parseObject(message, WifiInfoResPkg.class).getBody();
		//更新设备网络状态
		Signet signet = signetService.getByUUID(uuid);
		if (signet == null) {
			log.info("× 网络状态 设备：{} 网络：{} 设备不存在", uuid, body.getSSID());
			return;
		}

		String ssid = body.getSSID();
		int netType = body.getNetType();
		if (StringUtils.isBlank(ssid)) {
			if (netType == 2) {
				ssid = "wifi";
			} else if (netType == 1) {
				ssid = "4g";
			}
		}

		//网络状态改变后,才更新,否则不更新
		if (StringUtils.equals(ssid, signet.getNetwork())) {
			log.info("- 网络状态 设备：{} 网络：{}忽略", signet.getId(), body.getSSID());
			return;
		}

		signet.setNetwork(ssid);
		signetService.update(signet);
		log.info("√ 网络状态 设备：{} 网络：{}", signet.getId(), ssid);
	}

	/**
	 * wifi列表返回结果
	 */
	@Override
	public void getWifiListRes(String message, String uuid) {
		//解析消息体
		WifiListRes res = JSONObject.parseObject(message, WifiListResPkg.class).getBody();
		if (res != null) {
			Signet signet = null;
			try {
				signet = signetService.getByUUID(uuid);
			} catch (Exception e) {
				log.error("出现异常 ", e);
			}
			if (signet != null) {
				//解析消息体
				List<Object> wifiList = res.getWifiList();
				if (wifiList != null && wifiList.size() > 0) {
					//创建存储到redis中的返回值wifi列表对象
					List<String> wifis = new ArrayList<>();

					//遍历设备传递过来的wifi列表
					for (Object o : wifiList) {
						String ssid;
						JSONObject obj;
						try {
							obj = (JSONObject) o;
						} catch (Exception e) {
							continue;
						}
						try {
							ssid = obj.get("SSID").toString();
						} catch (Exception e) {
							continue;
						}
						try {
							Object bssid = obj.get("BSSID");
							if (bssid == null || StringUtils.isBlank(bssid.toString()) || "00:00:00:00:00:00".equalsIgnoreCase(bssid.toString())) {
								continue;
							}
						} catch (Exception e) {
							continue;
						}
						if (StringUtils.isNotBlank(ssid)) {
							wifis.add(ssid);
						}
					}

					//将wifi列表存储到redis中  存60秒
					if (wifis.size() > 0) {
						log.info("√ WIFI列表 设备：{} WIFI：{}", signet.getId(), JSONObject.toJSONString(wifis));
						redisUtil.set(RedisGlobal.DEVICE_WIFI_LIST + signet.getId(), wifis, RedisGlobal.DEVICE_WIFI_LIST_TIMEOUT);//存60秒
					} else {
						log.info("- WIFI列表 设备：{} WIFI：{} 无WIFI列表", signet.getId(), JSONObject.toJSONString(wifis));
					}
				} else {
					log.info("× WIFI列表 设备：{} 消息体不存在", signet.getId());
				}
			} else {
				log.info("× WIFI列表 设备：{} 设备不存在", uuid);
			}
		} else {
			log.info("× WIFI列表 设备：{} 消息体不存在", uuid);
		}
	}

	/**
	 * 指纹清空返回结果
	 *
	 * @param message 清空返回消息体
	 */
	@Override
	public void fpClearRes(String message, String uuid) {
		//解析消息体
		FingerPrintClearRes res = JSONObject.parseObject(message, FingerPrintClearResPkg.class).getBody();

		if (res != null) {
			int fingerAddr = res.getFingerAddr();
			int deivceID = res.getDeviceID();
			int userID = res.getUserID();

			if (fingerAddr == 0) {
				fingerService.deleteAllByDevice(deivceID);
				log.info("√ 指纹清空成功  设备：{}", deivceID);
				//向管章人发送通知
				Signet signet = signetService.get(deivceID);
				messageTempService.clearFingerNotice(signet.getName(), signet.getKeeperId());
			} else {
				fingerService.deleteByDeviceAndAddr(deivceID, userID);
				log.info("√ 指纹删除成功  设备：{}  指纹Addr：{}", deivceID, fingerAddr);

			}
		}
	}

	/**
	 * 指纹录入返回
	 * ps:(以最后一次为准,将前一次进行覆盖)
	 */
	@Override
	@Lock(keys = "#uuid", keyConstant = LockGlobal.add_finger)
	public void fpRecordRes(String message, String uuid) {
		Signet signet = signetService.getByUUID(uuid);
		if (signet != null) {
			//解析消息体
			FpRecordRes body = JSONObject.parseObject(message, FpRecordResPkg.class).getBody();

			if (body != null) {
				int res = body.getRes();
				if (res != 0) {
					log.info("× 指纹录入失败  设备:{}  消息:{}", signet.getId(), message);
					return;
				}
				boolean isSendNotice = false;
				//录入成功
				int userID = body.getUserID();
				synchronized (Global.fingerAddObject) {
					Finger finger = fingerService.getByUser(userID, signet.getId());
					if (finger == null) {
						finger = new Finger();
						finger.setCreateDate(new Date());
						finger.setAddrNum(body.getFingerAddr());
						finger.setUserId(userID);
						finger.setUserName(body.getUserName());
						finger.setUpdateDate(new Date());
						finger.setCodeId(body.getCodeID());
						finger.setDeviceId(signet.getId());
						fingerService.add(finger);
						isSendNotice = true;
						log.info("√ 指纹录入成功  设备:{}  指纹Addr:{}  指纹名:{}", signet.getId(), finger.getAddrNum(), finger.getUserName());
					} else {
						finger.setUpdateDate(new Date());
						fingerService.update(finger);
						log.info("- 指纹录入忽略  设备:{}  指纹Addr:{}  指纹名:{}", signet.getId(), finger.getAddrNum(), finger.getUserName());
					}

					if (isSendNotice) {
						try {
							messageTempService.addFingerNotice(signet.getName(), userID);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

	}

	/**
	 * 登录请求
	 */
	private void deviceLoginReq(String message, @NotNull WsSocket webSocket) throws Exception {
		//解析消息体
		DeviceLoginReq body = JSONObject.parseObject(message, DeviceLoginReqPkg.class).getBody();
		DeviceLoginInfo info = body.getDeviceLoginInfo();
		if (info == null) {
			return;
		}

		int deviceID = info.getDeviceID();
		Signet signet = signetService.get(deviceID);
		if (signet == null) {
			log.error("× 登录失败 设备：{} 该设备不存在", deviceID);
			return;
		}

		//同步印章最近5次申请单记录,解决印章无网情况下次数同步问题
		List<LoginApplication> loginApplicationInfos = info.getLoginApplicationInfo();
		try {
			if (loginApplicationInfos != null && loginApplicationInfos.size() > 0) {
				LoginApplication loginApplication = loginApplicationInfos.get(0);
				Integer applicationId = loginApplication.getApplicationId();
				if (applicationId != null && !Objects.equals(applicationId, 0)) {
					Integer useCount = loginApplication.getUseCount();
					applicationService.synchApplicationInfo(signet.getId(), applicationId, useCount);
				}
			}
		} catch (Exception e) {
			log.error("出现异常 ", e);
		}

		//构建返回值对象
		DeviceLoginRes res = new DeviceLoginRes();
		res.setJwtTokenNew("");
		res.setRet(0);//0:登录成功  非0:登录失败,进入注册

		//确定身份
		webSocket.setOrgId(signet.getOrgId());
		webSocket.setUid(signet.getUuid());
		webSocket.setDeviceId(signet.getId());
		webSocket.setBusy(info.getUsedStatus() == 1);
		WebSocketMap.add(signet.getId(), webSocket);

		//更新simNum
		String simNum = info.getSimNum();
		signet.setSimNum(simNum);

		//更新ICCID
		String iccid = info.getICCID();
		signet.setIccid(iccid);

		//更新imsi
		String imsi = info.getIMSI();
		signet.setImsi(imsi);

		signet.setVer(info.getVer());

		//网络状态同步 网络状态(0 离线 1 3G/4G 2 WiFi)
		Integer deviceNetType = info.getDeviceNetType();
		if (deviceNetType != null) {
			if (Objects.equals(deviceNetType, 1)) {
				signet.setNetwork("4G");
			} else if (Objects.equals(deviceNetType, 2)) {
				signet.setNetwork("wifi");
			}
		}

		//次数同步
		if (info.getUseCount() != 0) {
			signet.setCount(info.getUseCount());
		}

		//使用模式同步 使用模式 1 申请单模式、2指纹模式、3锁定模式、4装章模式 5密码 6OTA 7休眠 8产测
		Integer deviceMode = info.getDeviceMode();
		if (deviceMode != null) {
			if (deviceMode == 1) {
				signet.setFingerPattern(false);
				if (!Objects.equals(signet.getStatus(), 4)) {
					signet.setStatus(0);
				}
			} else if (deviceMode == 2) {
				signet.setFingerPattern(true);
				if (!Objects.equals(signet.getStatus(), 4)) {
					signet.setStatus(0);
				}
			} else if (deviceMode == 3) {
				signet.setStatus(4);
			}
		}

		//摄像头状态同步
		Boolean cameraPreviewStatus = info.getCameraPreviewStatus();
		if (cameraPreviewStatus != null) {
			signet.setCamera(cameraPreviewStatus ? 0 : 1);
		}

		//休眠时间同步
		if (!Objects.isNull(info.getDeviceSleepTimes())) {
			signet.setSleepTime(info.getDeviceSleepTimes());
		}

		//序列号同步
		if (StringUtils.isNotBlank(info.getSn())) {
			signet.setSn(info.getSn());
		}

		//同步摄像头状态
		if (!Objects.isNull(info.getCameraPreviewStatus())) {
			signet.setCamera(info.getCameraPreviewStatus() ? 0 : 1);
		}

		//同步申请单功能状态
		if (!Objects.isNull(info.getIsEnableApplication())) {
			signet.setIsEnableApplication(info.getIsEnableApplication());
		}

		//更新设备状态
		signetService.update(signet);

		//异步推送离线消息
		deviceAsyncService.pushUnline(signet);

		//登录成功时间戳
		res.setLoginTimes(System.currentTimeMillis());

		//组装登录响应返回
		MHPkg pkg_result = MHPkg.res(AppConstant.DEVICE_LOGIN_RES, res);
		String resMsg = JSONObject.toJSONString(pkg_result);
		WebSocketMap.sendAes(signet.getId(), resMsg);
		log.info("√ 登录成功 设备：{}", signet.getId());
	}

	/**
	 * 设备注册请求
	 */
	@Transactional
	public void deviceRegReq(String message, WsSocket webSocket) throws Exception {

		//解析消息
		DeviceLoginInfo info = JSONObject.parseObject(message, DeviceRegReqPkg.class).getBody().getDeviceLoginInfo();

		//业务处理
		String uuid = info.getStm32UUID();
		Signet signet;
		if (StringUtils.isBlank(uuid) || uuid.length() != 24) {
			log.error("× 注册失败 UUID不合法 设备:{}  UUID:{}", webSocket.getDeviceId(), uuid);
			return;
		}

		signet = signetService.getByUUID(uuid);
		if (signet == null) {
			//开始注册
			signet = new Signet();
			signet.setCreateDate(new Date());
			signet.setSimNum(StringUtils.isBlank(info.getSimNum()) ? info.getICCID() : info.getSimNum());
			signet.setIccid(info.getICCID());
			signet.setImsi(info.getIMSI());
			signet.setStatus(Global.DEVICE_NORMAL);//正常状态
			signet.setCount(info.getUseCount());
			signet.setUuid(info.getStm32UUID());
			signet.setFingerPattern(false);
			signet.setIsEnableApplication(1);//默认启用申请单功能 s30不支持申请单模式  p20支持申请单模式与指纹模式
			signet.setCamera(0);//摄像头默认开启

			//集团ID
			Org org = orgService.get(CommonUtils.properties.getDefaultOrgId());
			signet.setOrgId(org.getId());
			signet.setOrgName(org.getName());

			//组织ID
			Department root = departmentService.getRootByOrg(CommonUtils.properties.getDefaultOrgId());
			if (root != null && root.getId() != null) {
				signet.setDepartmentId(root.getId());
				signet.setDepartmentName(root.getName());
				//新设备管章人&审计人默认为属主
				Integer managerUserId = root.getManagerUserId();
				signet.setKeeperId(managerUserId);
				signet.setAuditorId(managerUserId);
			}

			signet.setBodyId("YX000000");
			signetService.add(signet);
			signet.setName("印章(" + signet.getId() + ")");
			signetService.update(signet);
		}

		//将注册信息中的对称密钥加入通道
		String symmetricKey = info.getSymmetricKey();
		if (StringUtils.isNotBlank(symmetricKey)) {
			try {
				redisUtil.set(RedisGlobal.AES_KEY + signet.getId(), symmetricKey);
				log.info("√ 同步秘钥成功 设备ID：{} 秘钥：{}", signet.getId(), symmetricKey);
			} catch (Exception e) {
				log.error("× 同步秘钥失败 设备ID：{} 出错：{}", signet.getId(), e.getMessage());
			}
		}


		// 此时,设备一定不为空
		webSocket.setDeviceId(signet.getId());
		webSocket.setUid(signet.getUuid());
		webSocket.setOrgId(signet.getOrgId());
		WebSocketMap.add(signet.getId(), webSocket);

		//组装注册响应对象
		DeviceRegRes res = new DeviceRegRes();
		res.setRet(0);//0:注册成功
		res.setMsg("");

		//组装DeviceLoginReq
		DeviceLoginReq dq = new DeviceLoginReq();
		dq.setJwtToken("");

		//组装DeviceLoginInfo
		DeviceLoginInfo info_new = new DeviceLoginInfo();
		info_new.setStm32UUID(uuid);
		info_new.setDeviceID(signet.getId());
		dq.setDeviceLoginInfo(info_new);

		//完成响应对象组装
		res.setDeviceLoginReq(dq);
		MHPkg resPkg = MHPkg.res(AppConstant.DEVICE_REG_RES, res);
		String resMsg = JSONObject.toJSONString(resPkg);
		WebSocketMap.sendAes(signet.getId(), resMsg);
		log.info("√ 注册成功 设备{}", signet.getId());
	}
}

