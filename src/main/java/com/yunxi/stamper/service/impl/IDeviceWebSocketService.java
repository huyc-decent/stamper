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
	 * ??????WS?????????????????????
	 *
	 * @param webSocket WS
	 * @param message   ????????????(????????????????????????????????????)
	 */
	private void checkIdentity(WsSocket webSocket, String message) {
		String uuid = webSocket.getUid();
		Integer deviceId = webSocket.getDeviceId();
		Integer orgId = webSocket.getOrgId();

		if (StringUtils.isNotBlank(uuid) && deviceId != null && orgId != null) {
			return;
		}

		if (message.startsWith("ping") || message.startsWith("pong")) {
			//????????????
			uuid = WsUtils.parseHeartText(message);
			Signet device = signetService.getByUUID(uuid);
			if (device != null) {
				deviceId = device.getId();
				orgId = device.getOrgId();
			}
		} else {
			//????????????
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
	 * ??????websocket?????????????????????
	 * ps:?????????????????????,??????message???????????????
	 *
	 * @param message   ??????
	 * @param webSocket ??????
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
		//??????WS??????????????????????????????
		try {
			checkIdentity(webSocket, message);
		} catch (Exception e) {
			log.info("xx\tws????????????\tws:{}\tmessage:{}", webSocket.toString(), message, e);
			return;
		}
		String ip = webSocket.getIp();
		Integer deviceId = webSocket.getDeviceId();
		Integer orgId = webSocket.getOrgId();
		boolean busy = webSocket.isBusy();

		log.info("--\tws??????\tdeviceId:{}\tip:{}\torgId:{}\tmessage:{}", deviceId, ip, orgId, message);

		//??????????????????Map
		WebSocketMap.add(deviceId, webSocket);

		//??????WS?????????????????????????????????WS????????????
		webSocket.setLastHeartbeat(new Date());

		//??????????????????,?????????????????????????????????????????????????????????????????????????????????nginx???tomcat????????????????????????????????????
		redisUtil.set(RedisGlobal.PING + deviceId, busy, RedisGlobal.PING_TIME_OUT);//??????????????????
		if (message.startsWith("ping")) {
			webSocket.sendByBasic("pong");
			return;
		} else if (message.startsWith("pong")) {
			return;
		}

		//??????Aes||Rsa??????
		WsMessage wsMessage = null;
		try {
			wsMessage = parseEncrypt(message, webSocket);
		} catch (Exception e) {
			log.info("xx\tws??????-????????????\tdeviceId:{}\tip:{}\torgId:{}\tbusy:{}\tmessage:{}\terror:{}", deviceId, ip, orgId, busy, message, e.getMessage());
			return;
		}
		if (wsMessage == null || StringUtils.isBlank(wsMessage.getContext())) {
			log.info("xx\tws??????-????????????\tdeviceId:{}\tip:{}\torgId:{}\tbusy:{}\tmessage:{}\tcontext:{}", deviceId, ip, orgId, busy, message, wsMessage == null ? "" : wsMessage.getContext());
			return;
		}

		log.info("--\tws??????\tdeviceId:{}\tip:{}\torgId:{}\tbusy:{}\tcmd:{}\tmessage:{}", deviceId, ip, orgId, busy, wsMessage.getCmd(), wsMessage.getContext());

		//????????????CMD?????????
		int cmd = wsMessage.getCmd();
		message = wsMessage.getContext();

		try {
			switch (cmd) {
				case AppConstant.DEVICE_REG_REQ: //????????????
					deviceRegReq(message, webSocket);
					break;

				case AppConstant.DEVICE_LOGIN_REQ: //????????????
					deviceLoginReq(message, webSocket);
					break;

				case AppConstant.FP_RECORD_RES:    //??????????????????
					fpRecordRes(message, webSocket.getUid());
					break;

				case AppConstant.FP_CLEAR_RES:    //???????????????????????????
					fpClearRes(message, webSocket.getUid());
					break;

				case AppConstant.WIFI_INFO_RES:    //????????????????????????
					getWifiInfoRes(message, webSocket.getUid());
					break;

				case AppConstant.DEVICE_USED_RES:    //??????????????????????????????
					deviceUsingRes(message, webSocket.getDeviceId());
					break;

				case AppConstant.IS_AUDIT_REQ:    //??????????????????????????????
					updatePosition(message, webSocket.getUid());
					break;

				case AppConstant.WIFI_LIST_RES:    //wifi??????????????????
					getWifiListRes(message, webSocket.getUid());
					break;

				case AppConstant.TAKE_PIC_RES:    //?????????????????????????????????
					highDeviceOnUsing(message, webSocket.getUid());
					break;

				case AppConstant.USE_MODEL_RETURN_RES:    //??????/?????????????????? ????????????
					updateSignetModel(message, webSocket.getUid());
					break;

				case AppConstant.REMOTE_LOCK_RETURN_RES:    //?????????????????? ????????????
					updateRemoteLock(message, webSocket.getUid());
					break;

				case AppConstant.SLEEP_TIME_RETURN_RES:    //???????????? ????????????
					updateSleepTime(message, webSocket.getUid());
					break;

				case AppConstant.DEVICE_MIGRATE_CALLBACK:    //??????????????????
					migrateCallBack(message, webSocket.getUid());
					break;

				case AppConstant.REMOTE_CAMERA_SWITCH_RES:  //?????????????????????
					cameraSwitch(message, webSocket.getUid());
					break;

				default:
					log.info("?????????????????? cmd:{} message:{} device:{}", cmd, message, webSocket.getUid() + "_" + webSocket.getDeviceId());
					break;
			}
		} catch (Exception e) {
			log.error("???????????? ", e);
			throw new PrintException(e.getMessage());
		}
	}

	/**
	 * ????????????/????????????????????????
	 *
	 * @param message ????????????
	 * @param uuid    ??????uuid
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
			log.info("-\t??????:{}\t???????????????", signet.getId());
		} else {
			signetService.updateCamera(signet.getId(), 1);
			log.info("-\t??????:{}\t???????????????", signet.getId());
		}

		//????????????
		try {
			String key = RedisGlobal.DEVICE_INFO + signet.getUuid();
			redisUtil.del(key);
		} catch (Exception e) {
			log.error("???????????? ", e);
		}
	}

	/**
	 * ??????????????????/??????????????????
	 *
	 * @param message ????????????????????????????????????
	 * @param uuid    ??????UUID
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

		/*????????????????????????*/
		log.setMigrateStatus(deviceMigrateCallback.getStatus());
		deviceMigrateLogService.update(log);

		/*??????????????????*/
		if (deviceMigrateCallback.getStatus() == 1) {
			migrate(signet, log.getNewOrgId());
		}
	}

	/**
	 * ????????????????????????????????????
	 *
	 * @param signet   ????????????
	 * @param newOrgId ?????????ID
	 */
	private void migrate(Signet signet, Integer newOrgId) {
		try {
			Org destOrg = orgService.get(newOrgId);
			if (destOrg != null) {
				/*????????????????????????????????????*/
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

				//????????????????????????
				signet.setTransferTime(new Date());

				/*???????????????????????????????????????(???????????????????????????????????????????????????????????????????????????'MAX'?????????)*/
				int total = sealRecordInfoService.getTotalBySignetAndOrg(signet.getId(), destOrg.getId());
				signet.setCount(total);

				signet.setTypeId(null);
				signet.setName("??????(???" + signet.getId() + ")");
				signet.setRemark(null);
				signet.setLogo(null);
				signetService.update(signet);

				//????????????????????????????????????
				List<Threshold> thresholds = thresholdService.getByDevice(signet.getId());
				if (thresholds != null && !thresholds.isEmpty()) {
					for (Threshold threshold : thresholds) {
						thresholdService.del(threshold);
					}
				}

			} else {
				//TODO:????????????????????????ORG_ID????????????????????????????????????
			}
		} catch (Exception e) {
			log.error("???????????? ", e);
			//TODO:??????????????????????????????????????????
		}
	}

	/**
	 * V2???,??????json???????????????
	 * ???????????????????????????????????????json??????????????????????????????????????????????????????????????????????????????????????????
	 *
	 * @param message ??????
	 * @return ??????
	 */
	public WsMessage parseEncrypt(String message, WsSocket socket) throws Exception {
		WsMessage wsMessage = JSONObject.parseObject(message, WsMessage.class);
		if (wsMessage == null) {
			log.error("Webscoket??????????????????\tdeviceId:{}\tip:{}\tuuid:{}\tmessage:{}", socket.getDeviceId(), socket.getIp(), socket.getUid(), message);
			throw new RuntimeException("??????????????????");
		}

		//??????????????????????????????
		String uuid = wsMessage.getUuid();
		String encrypt = wsMessage.getEncrypt();

		//AES??????
		if (wsMessage.getCmd() == 1) {
			//??????????????????&AES??????
			Object symmetricKey = redisUtil.get(RedisGlobal.AES_KEY + socket.getDeviceId());
			if (symmetricKey != null && StringUtils.isNotBlank(symmetricKey.toString())) {
				message = AesUtil.decrypt(encrypt, symmetricKey.toString());
				wsMessage.setContext(message);
			}
		}
		//RSA??????
		else if (wsMessage.getCmd() == 0) {
			byte[] bytes = RsaUtil.decryptByPrivateKeyForSpilt(new BASE64Decoder().decodeBuffer(encrypt), MyKeyFactory.getPrivateKey());
			message = new String(bytes, StandardCharsets.UTF_8);
			wsMessage.setContext(message);
		}

		socket.setUid(uuid);

		//???????????????????????????&???????????????
		if (StringUtils.isNotBlank(wsMessage.getContext())) {
			MHPkg mhPkg = JSONObject.parseObject(wsMessage.getContext(), MHPkg.class);
			int cmd = mhPkg.getHead().getCmd();
			wsMessage.setCmd(cmd);
		}

		String ip = socket.getIp();
		Integer deviceId = socket.getDeviceId();
		socket.setUid(uuid);
		socket.setIp(ip);
		log.info("ws??????\tdeviceId:{}\tip:{}\tuuid:{}\tmessage:{}", deviceId, ip, uuid, message);
		return wsMessage;
	}

	/**
	 * ???????????? ????????????
	 * {"Body":{"res":0,"sleepTime":4},"Head":{"Magic":42949207,"Cmd":83,"SerialNum":980,"Version":1}}
	 */
	@Override
	public void updateSleepTime(String message, @NotNull String uuid) {
		DeviceSleepTime sleepTime = JSONObject.parseObject(message, DeviceSleepTimePkg.class).getBody();

		if (sleepTime != null && sleepTime.getRes() != null && sleepTime.getRes() == 0) {
			Integer times = sleepTime.getSleepTime();//???????????? 2~10 ??????

			if (StringUtils.isNotBlank(uuid)) {
				Signet signet = signetService.getByUUID(uuid);
				if (signet != null) {
					signet.setSleepTime(times);
					signetService.update(signet);
					log.info("??? ????????????  ?????????{} ?????????{}??????", signet.getName(), times);
				}
			}
		}
	}

	/**
	 * ?????????????????? ????????????
	 * {"Body":{"res":0,"status":1},"Head":{"Magic":42949207,"Cmd":91,"SerialNum":901,"Version":1}}
	 */
	@Override
	public void updateRemoteLock(String message, @NotNull String uuid) {
		DeviceLock lock = JSONObject.parseObject(message, DeviceLockPkg.class).getBody();

		if (lock != null && lock.getRes() != null && lock.getRes() == 0) {
			Integer status = lock.getStatus();//0:??????  1:??????

			if (StringUtils.isNotBlank(uuid)) {
				Signet signet = signetService.getByUUID(uuid);
				if (signet != null && status != null) {
					signet.setStatus(status == 1 ? Global.DEVICE_LOCK : Global.DEVICE_NORMAL);//0:?????? 1:?????? 2:?????? 3:?????? 4:??????
					signetService.update(signet);
					log.info("??? ????????????  ?????????{} ?????????{}", signet.getName(), status == 2 ? "??????" : "??????");
				}
			}
		}
	}

	/**
	 * ??????????????????????????????
	 * {"Body":{"res":0,"useModel":1},"Head":{"Magic":42949207,"Cmd":87,"SerialNum":672,"Version":1}}
	 */
	@Override
	public void updateSignetModel(String message, String uuid) {
		DeviceModel model = JSONObject.parseObject(message, DeviceModelPkg.class).getBody();

		if (model != null && model.getRes() != null && model.getRes() == 0) {
			Integer status = model.getUseModel();//0????????????????????? 1?????????????????????

			if (StringUtils.isNotBlank(uuid)) {
				Signet signet = signetService.getByUUID(uuid);
				if (signet != null && status != null) {
					signet.setFingerPattern(status == 1);//0???????????? 1?????????
					signetService.update(signet);
					log.info("??? ???????????? ?????????{} ?????????{}", signet.getId(), signet.getFingerPattern() ? "????????????(???)" : "????????????(???)");
				} else {
					log.info("?? ???????????? ?????????{} ?????????{}???????????????", uuid, (signet != null && signet.getFingerPattern()) ? "????????????(???)" : "????????????(???)");
				}
			}
		} else {
			log.info("?? ???????????? ?????????{} ??????????????????", uuid);
		}
	}

	/**
	 * ??????????????????????????????
	 *
	 * @param message ?????????
	 * @param uuid    ??????UUID
	 */
	@Override
	public void updatePosition(String message, String uuid) {
		// ???????????????
		LocationRes res = JSONObject.parseObject(message, LocationPkg.class).getBody();

		// ???????????????????????????????????????,??????????????????????????????
		if (!isAddOrUpdatePosition(res)) {
			log.info("?? ???????????? ??????:{} ????????????{} ???????????????", uuid, JSONObject.toJSONString(res));
			return;
		}

		// ?????????????????????
		Addr addr = addrService.getByLocation(res.getAddr());
		if (addr == null) {
			addr = new Addr();
			addr.setLatitude(res.getLatitude());
			addr.setLocation(res.getAddr());
			addr.setLongitude(res.getLongitude());

			// ????????????????????? ?????????
			String p = res.getProvince();
			String c = res.getCity();
			String d = res.getDistrict();

			if (StringUtils.isNoneBlank(p, c, d)) {
				addr.setProvince(p);
				addr.setCity(c);
				addr.setDistrict(d);
				addrService.add(addr);
			} else {
				log.info("?? ???????????? ??????:{} ????????????{} ???????????????", uuid, JSONObject.toJSONString(res));
				return;
			}
		}

		//??????????????????????????????
		Signet signet = signetService.getByUUID(uuid);
		if (signet == null) {
			log.info("- ???????????? ??????:{} ?????????{} ???????????????", uuid, addr.getLocation());
			return;
		}
		signet.setAddr(addr.getId());
		signetService.update(signet);
		log.info("??? ???????????? ??????:{} ?????????{}", signet.getId(), addr.getLocation());
	}

	/**
	 * ????????????????????????
	 *
	 * @param res ????????????
	 * @return false:?????????  true?????????
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
	 * ?????????????????????????????????
	 */
	@Override
	public void highDeviceOnUsing(String message, @NotNull String uuid) {
		//???????????????
		HighDeviceOnUseRes res = JSONObject.parseObject(message, HighDeviceOnUsingPkg.class).getBody();
		if (res == null) {
			log.info("?? ??????????????? ?????????{} ??????????????????:{}", uuid, message);
			return;
		}

		Integer applicationID = res.getApplicationID();
		Integer useTimes = res.getUseTimes();
		Signet signet = signetService.getByUUID(uuid);
		if (signet == null) {
			log.info("?? ??????????????? ?????????{} ?????????{} ????????????{} ???????????????", uuid, useTimes, applicationID);
			return;
		}

		redisUtil.set(RedisGlobal.PING + signet.getId(), true, RedisGlobal.PING_TIME_OUT);
		WsSocket socket = WebSocketMap.get(signet.getId());
		if (socket != null) {
			socket.setBusy(true);
		}

		log.info("??? ??????????????? ?????????{} ?????????{} ????????????{}", signet.getName(), useTimes, applicationID);

		//ver: 0:??????3G 1:??????3G?????? 2:??????4G 3:??????4G?????? 5:Linux-4G  6:??????????????????
		//??????????????????????????????
		if (applicationID != null && applicationID != 0 && (signet.getVer() == null || signet.getVer() < 2)) {
			int localCount = sealRecordInfoService.getCountByApplication(applicationID);
			applicationService.synchApplicationInfo(signet.getId(), applicationID, localCount);
		}

	}

	/**
	 * ??????????????????????????????
	 *
	 * @param message  ?????????
	 * @param deviceId ??????ID
	 */
	@Override
	public void deviceUsingRes(String message, @NotNull Integer deviceId) {
		//???????????????
		DeviceBeingUsedRes body = JSONObject.parseObject(message, DeviceBeingUsedResPkg.class).getBody();
		try {
			List<LoginApplication> loginApplicationInfos = body.getLoginApplicationInfo();
			if (loginApplicationInfos != null && loginApplicationInfos.size() > 0) {
//				Signet signet = signetService.get(deviceId);
				//??????????????????5??????????????????,?????????????????????????????????????????????
//				synchApplicationInfo(signet, loginApplicationInfo);
				LoginApplication loginApplication = loginApplicationInfos.get(0);
				Integer applicationId = loginApplication.getApplicationId();
				if (applicationId != null && !Objects.equals(applicationId, 0)) {
					Integer useCount = loginApplication.getUseCount();
					applicationService.synchApplicationInfo(deviceId, applicationId, useCount);
				}
			}
		} catch (Exception e) {
			log.error("???????????? ", e);
		}

		int status = body.getUsedStatus();//0:?????? 1:??????
		redisUtil.set(RedisGlobal.PING + deviceId, status == 1, RedisGlobal.PING_TIME_OUT);

		WsSocket socket = WebSocketMap.get(deviceId);
		if (socket != null) {
			socket.setBusy(status == 1);
		}
		log.info("??? ??????????????? ?????????{} ?????????{}", deviceId, status == 1 ? "??????" : "??????");
	}

	/**
	 * ????????????????????????
	 *
	 * @param message ?????????
	 * @param uuid    ??????UUID
	 */
	@Override
	public void getWifiInfoRes(String message, String uuid) {
		//???????????????
		WifiInfoRes body = JSONObject.parseObject(message, WifiInfoResPkg.class).getBody();
		//????????????????????????
		Signet signet = signetService.getByUUID(uuid);
		if (signet == null) {
			log.info("?? ???????????? ?????????{} ?????????{} ???????????????", uuid, body.getSSID());
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

		//?????????????????????,?????????,???????????????
		if (StringUtils.equals(ssid, signet.getNetwork())) {
			log.info("- ???????????? ?????????{} ?????????{}??????", signet.getId(), body.getSSID());
			return;
		}

		signet.setNetwork(ssid);
		signetService.update(signet);
		log.info("??? ???????????? ?????????{} ?????????{}", signet.getId(), ssid);
	}

	/**
	 * wifi??????????????????
	 */
	@Override
	public void getWifiListRes(String message, String uuid) {
		//???????????????
		WifiListRes res = JSONObject.parseObject(message, WifiListResPkg.class).getBody();
		if (res != null) {
			Signet signet = null;
			try {
				signet = signetService.getByUUID(uuid);
			} catch (Exception e) {
				log.error("???????????? ", e);
			}
			if (signet != null) {
				//???????????????
				List<Object> wifiList = res.getWifiList();
				if (wifiList != null && wifiList.size() > 0) {
					//???????????????redis???????????????wifi????????????
					List<String> wifis = new ArrayList<>();

					//???????????????????????????wifi??????
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

					//???wifi???????????????redis???  ???60???
					if (wifis.size() > 0) {
						log.info("??? WIFI?????? ?????????{} WIFI???{}", signet.getId(), JSONObject.toJSONString(wifis));
						redisUtil.set(RedisGlobal.DEVICE_WIFI_LIST + signet.getId(), wifis, RedisGlobal.DEVICE_WIFI_LIST_TIMEOUT);//???60???
					} else {
						log.info("- WIFI?????? ?????????{} WIFI???{} ???WIFI??????", signet.getId(), JSONObject.toJSONString(wifis));
					}
				} else {
					log.info("?? WIFI?????? ?????????{} ??????????????????", signet.getId());
				}
			} else {
				log.info("?? WIFI?????? ?????????{} ???????????????", uuid);
			}
		} else {
			log.info("?? WIFI?????? ?????????{} ??????????????????", uuid);
		}
	}

	/**
	 * ????????????????????????
	 *
	 * @param message ?????????????????????
	 */
	@Override
	public void fpClearRes(String message, String uuid) {
		//???????????????
		FingerPrintClearRes res = JSONObject.parseObject(message, FingerPrintClearResPkg.class).getBody();

		if (res != null) {
			int fingerAddr = res.getFingerAddr();
			int deivceID = res.getDeviceID();
			int userID = res.getUserID();

			if (fingerAddr == 0) {
				fingerService.deleteAllByDevice(deivceID);
				log.info("??? ??????????????????  ?????????{}", deivceID);
				//????????????????????????
				Signet signet = signetService.get(deivceID);
				messageTempService.clearFingerNotice(signet.getName(), signet.getKeeperId());
			} else {
				fingerService.deleteByDeviceAndAddr(deivceID, userID);
				log.info("??? ??????????????????  ?????????{}  ??????Addr???{}", deivceID, fingerAddr);

			}
		}
	}

	/**
	 * ??????????????????
	 * ps:(?????????????????????,????????????????????????)
	 */
	@Override
	@Lock(keys = "#uuid", keyConstant = LockGlobal.add_finger)
	public void fpRecordRes(String message, String uuid) {
		Signet signet = signetService.getByUUID(uuid);
		if (signet != null) {
			//???????????????
			FpRecordRes body = JSONObject.parseObject(message, FpRecordResPkg.class).getBody();

			if (body != null) {
				int res = body.getRes();
				if (res != 0) {
					log.info("?? ??????????????????  ??????:{}  ??????:{}", signet.getId(), message);
					return;
				}
				boolean isSendNotice = false;
				//????????????
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
						log.info("??? ??????????????????  ??????:{}  ??????Addr:{}  ?????????:{}", signet.getId(), finger.getAddrNum(), finger.getUserName());
					} else {
						finger.setUpdateDate(new Date());
						fingerService.update(finger);
						log.info("- ??????????????????  ??????:{}  ??????Addr:{}  ?????????:{}", signet.getId(), finger.getAddrNum(), finger.getUserName());
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
	 * ????????????
	 */
	private void deviceLoginReq(String message, @NotNull WsSocket webSocket) throws Exception {
		//???????????????
		DeviceLoginReq body = JSONObject.parseObject(message, DeviceLoginReqPkg.class).getBody();
		DeviceLoginInfo info = body.getDeviceLoginInfo();
		if (info == null) {
			return;
		}

		int deviceID = info.getDeviceID();
		Signet signet = signetService.get(deviceID);
		if (signet == null) {
			log.error("?? ???????????? ?????????{} ??????????????????", deviceID);
			return;
		}

		//??????????????????5??????????????????,?????????????????????????????????????????????
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
			log.error("???????????? ", e);
		}

		//?????????????????????
		DeviceLoginRes res = new DeviceLoginRes();
		res.setJwtTokenNew("");
		res.setRet(0);//0:????????????  ???0:????????????,????????????

		//????????????
		webSocket.setOrgId(signet.getOrgId());
		webSocket.setUid(signet.getUuid());
		webSocket.setDeviceId(signet.getId());
		webSocket.setBusy(info.getUsedStatus() == 1);
		WebSocketMap.add(signet.getId(), webSocket);

		//??????simNum
		String simNum = info.getSimNum();
		signet.setSimNum(simNum);

		//??????ICCID
		String iccid = info.getICCID();
		signet.setIccid(iccid);

		//??????imsi
		String imsi = info.getIMSI();
		signet.setImsi(imsi);

		signet.setVer(info.getVer());

		//?????????????????? ????????????(0 ?????? 1 3G/4G 2 WiFi)
		Integer deviceNetType = info.getDeviceNetType();
		if (deviceNetType != null) {
			if (Objects.equals(deviceNetType, 1)) {
				signet.setNetwork("4G");
			} else if (Objects.equals(deviceNetType, 2)) {
				signet.setNetwork("wifi");
			}
		}

		//????????????
		if (info.getUseCount() != 0) {
			signet.setCount(info.getUseCount());
		}

		//?????????????????? ???????????? 1 ??????????????????2???????????????3???????????????4???????????? 5?????? 6OTA 7?????? 8??????
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

		//?????????????????????
		Boolean cameraPreviewStatus = info.getCameraPreviewStatus();
		if (cameraPreviewStatus != null) {
			signet.setCamera(cameraPreviewStatus ? 0 : 1);
		}

		//??????????????????
		if (!Objects.isNull(info.getDeviceSleepTimes())) {
			signet.setSleepTime(info.getDeviceSleepTimes());
		}

		//???????????????
		if (StringUtils.isNotBlank(info.getSn())) {
			signet.setSn(info.getSn());
		}

		//?????????????????????
		if (!Objects.isNull(info.getCameraPreviewStatus())) {
			signet.setCamera(info.getCameraPreviewStatus() ? 0 : 1);
		}

		//???????????????????????????
		if (!Objects.isNull(info.getIsEnableApplication())) {
			signet.setIsEnableApplication(info.getIsEnableApplication());
		}

		//??????????????????
		signetService.update(signet);

		//????????????????????????
		deviceAsyncService.pushUnline(signet);

		//?????????????????????
		res.setLoginTimes(System.currentTimeMillis());

		//????????????????????????
		MHPkg pkg_result = MHPkg.res(AppConstant.DEVICE_LOGIN_RES, res);
		String resMsg = JSONObject.toJSONString(pkg_result);
		WebSocketMap.sendAes(signet.getId(), resMsg);
		log.info("??? ???????????? ?????????{}", signet.getId());
	}

	/**
	 * ??????????????????
	 */
	@Transactional
	public void deviceRegReq(String message, WsSocket webSocket) throws Exception {

		//????????????
		DeviceLoginInfo info = JSONObject.parseObject(message, DeviceRegReqPkg.class).getBody().getDeviceLoginInfo();

		//????????????
		String uuid = info.getStm32UUID();
		Signet signet;
		if (StringUtils.isBlank(uuid) || uuid.length() != 24) {
			log.error("?? ???????????? UUID????????? ??????:{}  UUID:{}", webSocket.getDeviceId(), uuid);
			return;
		}

		signet = signetService.getByUUID(uuid);
		if (signet == null) {
			//????????????
			signet = new Signet();
			signet.setCreateDate(new Date());
			signet.setSimNum(StringUtils.isBlank(info.getSimNum()) ? info.getICCID() : info.getSimNum());
			signet.setIccid(info.getICCID());
			signet.setImsi(info.getIMSI());
			signet.setStatus(Global.DEVICE_NORMAL);//????????????
			signet.setCount(info.getUseCount());
			signet.setUuid(info.getStm32UUID());
			signet.setFingerPattern(false);
			signet.setIsEnableApplication(1);//??????????????????????????? s30????????????????????????  p20????????????????????????????????????
			signet.setCamera(0);//?????????????????????

			//??????ID
			Org org = orgService.get(CommonUtils.properties.getDefaultOrgId());
			signet.setOrgId(org.getId());
			signet.setOrgName(org.getName());

			//??????ID
			Department root = departmentService.getRootByOrg(CommonUtils.properties.getDefaultOrgId());
			if (root != null && root.getId() != null) {
				signet.setDepartmentId(root.getId());
				signet.setDepartmentName(root.getName());
				//??????????????????&????????????????????????
				Integer managerUserId = root.getManagerUserId();
				signet.setKeeperId(managerUserId);
				signet.setAuditorId(managerUserId);
			}

			signet.setBodyId("YX000000");
			signetService.add(signet);
			signet.setName("??????(" + signet.getId() + ")");
			signetService.update(signet);
		}

		//?????????????????????????????????????????????
		String symmetricKey = info.getSymmetricKey();
		if (StringUtils.isNotBlank(symmetricKey)) {
			try {
				redisUtil.set(RedisGlobal.AES_KEY + signet.getId(), symmetricKey);
				log.info("??? ?????????????????? ??????ID???{} ?????????{}", signet.getId(), symmetricKey);
			} catch (Exception e) {
				log.error("?? ?????????????????? ??????ID???{} ?????????{}", signet.getId(), e.getMessage());
			}
		}


		// ??????,?????????????????????
		webSocket.setDeviceId(signet.getId());
		webSocket.setUid(signet.getUuid());
		webSocket.setOrgId(signet.getOrgId());
		WebSocketMap.add(signet.getId(), webSocket);

		//????????????????????????
		DeviceRegRes res = new DeviceRegRes();
		res.setRet(0);//0:????????????
		res.setMsg("");

		//??????DeviceLoginReq
		DeviceLoginReq dq = new DeviceLoginReq();
		dq.setJwtToken("");

		//??????DeviceLoginInfo
		DeviceLoginInfo info_new = new DeviceLoginInfo();
		info_new.setStm32UUID(uuid);
		info_new.setDeviceID(signet.getId());
		dq.setDeviceLoginInfo(info_new);

		//????????????????????????
		res.setDeviceLoginReq(dq);
		MHPkg resPkg = MHPkg.res(AppConstant.DEVICE_REG_RES, res);
		String resMsg = JSONObject.toJSONString(resPkg);
		WebSocketMap.sendAes(signet.getId(), resMsg);
		log.info("??? ???????????? ??????{}", signet.getId());
	}
}

