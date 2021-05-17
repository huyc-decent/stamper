package com.yunxi.stamper.controller;

import com.alibaba.fastjson.JSONObject;
import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.device.MHPkg;
import com.yunxi.stamper.commons.jwt.AES.AesUtil;
import com.yunxi.stamper.commons.jwt.RSA.MyKeyFactory;
import com.yunxi.stamper.commons.jwt.RSA.RsaUtil;
import com.yunxi.stamper.commons.other.AppConstant;
import com.yunxi.stamper.commons.other.RedisGlobal;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.Config;
import com.yunxi.stamper.entity.Signet;
import com.yunxi.stamper.service.ConfigService;
import com.yunxi.stamper.service.DeviceWebSocketService;
import com.yunxi.stamper.service.SignetService;
import com.yunxi.stamper.websocket.core.WsMessage;
import com.yunxi.stamper.websocket.core.WsSocket;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Decoder;


/**
 * @author zhf_10@163.com
 * @Description 设备响应回调控制层
 * @date 2019/8/13 0013 9:52
 */
@Slf4j
@Api(tags = "设备回调相关")
@RestController
@RequestMapping("/device/deviceCallBack")
public class CallBackController extends BaseController {
	@Autowired
	private DeviceWebSocketService deviceWebSocketService;
	@Autowired
	private SignetService signetService;

	public WsMessage parseEncrypt(String message, String uuid, Integer deviceId) throws Exception {
		String context = null;
		int cmd = 0;

		//取出对称秘钥&AES解密
		Object symmetricKey = redisUtil.get(RedisGlobal.AES_KEY + deviceId);
		if (symmetricKey != null && StringUtils.isNotBlank(symmetricKey.toString())) {
			context = AesUtil.decrypt(message, symmetricKey.toString());
		}

		//将明文解析成消息包&取出协议号
		if (null != context) {
			MHPkg mhPkg = null;
			try {
				mhPkg = JSONObject.parseObject(context, MHPkg.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (mhPkg != null && mhPkg.getHead() != null) {
				cmd = mhPkg.getHead().getCmd();
			}
		}

		log.info("http消息\tdeviceId:{}\tuuid:{}\tmessage:{}", deviceId, uuid, context);

		if (StringUtils.isNotBlank(context)) {
			return new WsMessage(context, cmd);
		}

		return null;
	}

	/**
	 * 设备响应第三方回调接口(设备专用)
	 * http://114.214.170.66:9030/device/deviceCallBack/res?message=****&uuid=****
	 */
	@RequestMapping("/res")
	public ResultVO res(String message, String uuid) {
		if (StringUtils.isBlank(message)) {
			return ResultVO.FAIL("消息体不能为空");
		}

		//设备信息
		Signet device = signetService.getByUUID(uuid);
		Integer deviceId = null;
		if (device != null) {
			deviceId = device.getId();
		}

		//解析消息
		WsMessage wsMessage;
		try {
			wsMessage = parseEncrypt(message, uuid, deviceId);
		} catch (Exception e) {
			log.error("X\thttp消息-解密异常\tuuid:{}\tmessage:{}\terror:{}", uuid, message, e.getMessage());
			return ResultVO.FAIL("消息体解析异常");
		}
		if (wsMessage == null || StringUtils.isBlank(wsMessage.getContext())) {
			log.error("X\thttp消息-解密失败\tuuid:{}\tmessage:{}", uuid, message);
			return ResultVO.OK();
		}

		message = wsMessage.getContext();
		int cmd = wsMessage.getCmd();

		try {
			if (StringUtils.isNoneBlank(message, uuid)) {
				switch (cmd) {
					case AppConstant.FP_RECORD_RES://指纹录入返回
						deviceWebSocketService.fpRecordRes(message, uuid);
						break;
					case AppConstant.FP_CLEAR_RES://指纹清空(删除)返回
						deviceWebSocketService.fpClearRes(message, uuid);
						break;
					case AppConstant.WIFI_INFO_RES:    //网络状态改变返回
						deviceWebSocketService.getWifiInfoRes(message, uuid);
						break;
					case AppConstant.DEVICE_USED_RES:    //印章开关锁状态的返回
						deviceWebSocketService.deviceUsingRes(message, deviceId);
						break;
					case AppConstant.IS_AUDIT_REQ:    //设备上传地址坐标信息
						deviceWebSocketService.updatePosition(message, uuid);
						break;
					case AppConstant.WIFI_LIST_RES:    //wifi列表返回结果
						deviceWebSocketService.getWifiListRes(message, uuid);
						break;
					case AppConstant.TAKE_PIC_RES:    //印章通知高拍仪进行拍照
						deviceWebSocketService.highDeviceOnUsing(message, uuid);
						break;
					case AppConstant.USE_MODEL_RETURN_RES:    //开启/关闭指纹模式 状态返回
						deviceWebSocketService.updateSignetModel(message, uuid);
						break;
					case AppConstant.REMOTE_LOCK_RETURN_RES:    //远程锁定功能 状态返回
						deviceWebSocketService.updateRemoteLock(message, uuid);
						break;
					case AppConstant.SLEEP_TIME_RETURN_RES:    //设置休眠 状态返回
						deviceWebSocketService.updateSleepTime(message, uuid);
						break;
					default:
						break;
				}
			}
		} catch (Exception e) {
			log.error("出现异常 ", e);
		}
		return ResultVO.OK();
	}
}
