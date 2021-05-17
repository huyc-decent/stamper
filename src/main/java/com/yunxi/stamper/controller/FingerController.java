package com.yunxi.stamper.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.device.MHPkg;
import com.yunxi.stamper.commons.device.model.*;
import com.yunxi.stamper.commons.jwt.UserToken;
import com.yunxi.stamper.commons.mq.MQFinger;
import com.yunxi.stamper.commons.mq.MQPKG;
import com.yunxi.stamper.commons.other.AppConstant;
import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.other.PinyinUtil;
import com.yunxi.stamper.commons.other.RedisGlobal;
import com.yunxi.stamper.commons.response.Code;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.Finger;
import com.yunxi.stamper.entity.Signet;
import com.yunxi.stamper.entity.User;
import com.yunxi.stamper.entityVo.UserInfo;
import com.yunxi.stamper.service.FingerService;
import com.yunxi.stamper.service.SignetService;
import com.yunxi.stamper.service.UserService;
import com.yunxi.stamper.sys.aop.annotaion.WebLogger;
import com.yunxi.stamper.sys.rabbitMq.MqGlobal;
import com.yunxi.stamper.sys.rabbitMq.MqSender;
import com.yunxi.stamper.websocket.container.WebSocketMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/2 0002 22:49
 */
@Slf4j
@Api(tags = "指纹相关")
@RestController
@RequestMapping(value = {"/device/finger"}, method = {RequestMethod.POST, RequestMethod.GET})
public class FingerController extends BaseController {

	@Autowired
	private FingerService fingerService;
	@Autowired
	private SignetService signetService;
	@Autowired
	private UserService userService;
	@Autowired
	private MqSender mqSender;

	@ApiOperation(value = "查询用户拥有的指纹列表", notes = "查询用户拥有的指纹列表", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userId", value = "用户ID", dataType = "int"),
			@ApiImplicitParam(name = "pageSize", value = "每页显示数", dataType = "int", defaultValue = "10"),
			@ApiImplicitParam(name = "pageNum", value = "当前页", dataType = "int", defaultValue = "1"),
			@ApiImplicitParam(name = "page", value = "是否分页", dataType = "int", defaultValue = "false")
	})
	@GetMapping("/searchFingers")
	public ResultVO searchFingers(@RequestParam Integer userId,
								  @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
								  @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
								  @RequestParam(value = "page", required = false, defaultValue = "true") boolean isPage) {
		//查询数据
		if (isPage) {
			PageHelper.startPage(pageNum, pageSize);
		}
		List<Finger> fingers = fingerService.getByUser(userId);
		if (fingers == null) {
			return ResultVO.OK();
		}

		PageInfo pageInfo = null;
		if (isPage) {
			pageInfo = new PageInfo(fingers);
		}

		//组装前端需要的参数
		List<Map<String, Object>> infos = new LinkedList<>();
		for (Finger finger : fingers) {
			Map<String, Object> map = new HashMap<>();
			map.put("id", finger.getId());
			map.put("createDate", finger.getCreateDate());

			Integer deviceId = finger.getDeviceId();
			map.put("deviceId", deviceId);
			map.put("online", isOnline(deviceId) != null);

			//查询设备信息
			Signet signet = signetService.get(deviceId);
			if (signet != null) {
				map.put("deviceName", signet.getName());
			}

			infos.add(map);
		}

		if (isPage) {
			pageInfo.setList(infos);
		}
		return ResultVO.OK(isPage ? pageInfo : infos);
	}

	/**
	 * 查询已录入的用户列表信息
	 */
	@RequestMapping("/getBySignetId")
	public ResultVO getBySignetId(Integer signetId) {
		Signet signet = signetService.get(signetId);

		if (signet != null) {
			UserToken token = getToken();
			//只能查询本公司的设备信息
			if (signet.getOrgId().intValue() != token.getOrgId()) {
				return ResultVO.FAIL(Code.FAIL403);
			}

			boolean page = setPage();
			List<Finger> fingers = fingerService.getBySignet(signetId, token.getOrgId());
			return ResultVO.Page(fingers, page);
		}
		return ResultVO.FAIL("该印章不存在");
	}

	/**
	 * 真正的指纹清空
	 *
	 * @param pkg
	 */
	public void _cleanAll(MQPKG pkg) throws Exception {
		Integer signetId = pkg.getDeviceId();

		//组包
		FingerPrintClearReq req = FingerPrintClearReq.clearnAll(signetId);
		req.setCodeID(pkg.getUserId());
		MHPkg res = MHPkg.res(AppConstant.FP_CLEAR_REQ, req);
		WebSocketMap.sendAes(signetId, JSONObject.toJSONString(res));
	}

	/**
	 * 下发·指纹清空·指令
	 *
	 * @param signetId 设备id
	 * @return
	 */
	@WebLogger("清空指纹")
	@PostMapping("/cleanAll")
	public ResultVO cleanAll(@RequestParam("signetId") Integer signetId) {
		/*
		 * 参数校验:印章ID
		 */
		UserInfo userInfo = getUserInfo();
		Signet signet = signetService.get(signetId);
		if (signet == null || signet.getOrgId().intValue() != userInfo.getOrgId()) {
			return ResultVO.FAIL("设备不存在");
		}

		Integer status = signet.getStatus();
		if (status != null && status == 4) {
			return ResultVO.FAIL("设备已被锁定");
		}
		Integer online = isOnline(signetId);
		if (online == null) {
			return ResultVO.FAIL("很抱歉,设备当前不在线");
		}
		if (online == 1) {
			return ResultVO.FAIL("很抱歉，设备当前正在使用中，无法清空指纹");
		}


		/*
		 * 检查设备操作频率
		 */
		String key = RedisGlobal.DEVICE_CONTROL + signet.getId();
		Object obj = redisUtil.get(key);
		if (obj != null) {
			return ResultVO.FAIL("设备操作过于频繁，请稍后重试");
		}

		//组包推送消息
		MQPKG mqpkg = new MQPKG();
		mqpkg.setUserId(getToken().getUserId());
		mqpkg.setDeviceId(signetId);
		mqpkg.setCmd(MqGlobal.SIGNET_FINGER_CLEAN);

		MQFinger mf = new MQFinger();
		mf.setDeviceId(signetId);

		mqpkg.setData(JSONObject.toJSONString(mf));

		//发送
		mqSender.sendToExchange(properties.getRabbitMq().getExchangeOrder(), mqpkg);
		log.info("-\tMQ-下发指令-序列号:{}\t清空指纹\tuserId:{}\tname:{}\tmessage:{}", mqpkg.getSerialId(), userInfo.getId(), userInfo.getUserName(), CommonUtils.objJsonWithIgnoreFiled(mqpkg));

		/*
		 * 设置该设备操作频率
		 */
		redisUtil.set(key, System.currentTimeMillis(), RedisGlobal.DEVICE_CONTROL_TIMEOUT);

		return ResultVO.OK("指令已下发");


	}

	/**
	 * 真正删除指纹
	 */
	public void _cleanOne(MQPKG pkg) throws Exception {
		String data = pkg.getData();
		MQFinger mqFinger = null;
		if (StringUtils.isNotBlank(data)) {
			mqFinger = JSONObject.parseObject(data, MQFinger.class);
		}
		if (mqFinger == null) {
			return;
		}
		Integer userId = mqFinger.getUserId();
		Integer signetId = mqFinger.getDeviceId();


		//设备是否在线
		if (WebSocketMap.get(signetId) == null) {
			log.info("设备：【{}】 当前不在线，指纹删除失败", signetId);
			return;
		}

		//组包
		Finger finger = fingerService.getByUser(mqFinger.getUserId(), mqFinger.getDeviceId());
		if (finger != null) {
			FingerPrintClearReq req = FingerPrintClearReq.clearnOne(signetId, userId, finger.getAddrNum());
			req.setCodeID(pkg.getUserId());
			req.setCodeName(pkg.getUserName());
			req.setFingerId(finger.getId());
			req.setFingerUserId(userId);
			MHPkg res = MHPkg.res(AppConstant.FP_CLEAR_REQ, req);
			WebSocketMap.sendAes(signetId, JSONObject.toJSONString(res));
		}
	}

	/**
	 * 下发·指纹删除·指令
	 *
	 * @param signetId 设备ID
	 * @param userId   指纹所属人id列表，以逗号分隔
	 * @return
	 */
	@WebLogger("删除指纹")
	@PostMapping("/cleanOne")
	public ResultVO cleanOne(@RequestParam("signetId") Integer signetId, @RequestParam("userId") Integer userId) {
		UserInfo userInfo = getUserInfo();

		// 查询指纹记录
		Finger finger = fingerService.getByUser(userId, signetId);
		if (finger == null) {
			return ResultVO.FAIL("指纹不存在");
		}

		// 查询设备信息
		Signet signet = signetService.get(signetId);
		if (signet == null) {
			//设备不存在了,直接删除指纹记录
			finger.setDeleteBy(userInfo.getId());
			finger.setDeleteName(userInfo.getUserName());
			fingerService.delete(finger);

			return ResultVO.FAIL("指纹已删除");
		}

		// 其他组织下的设备指纹不允许删除
		if (signet.getOrgId().intValue() != userInfo.getOrgId()) {
			return ResultVO.FAIL("设备不存在");
		}

		// 锁定的设备不允许任何操作
		Integer status = signet.getStatus();
		if (status != null && status == 4) {
			return ResultVO.FAIL("设备已被锁定");
		}

		// 设备不在线,无法删除
		Integer online = isOnline(signetId);
		if (online == null) {
			return ResultVO.FAIL("很抱歉,设备当前不在线");
		}

		// 使用中的设备不允许操作指纹
		if (online == 1) {
			return ResultVO.FAIL("很抱歉，设备当前正在使用中，无法删除指纹");
		}

		// 查询指纹所属人信息
		User user = userService.get(userId);
		if (user == null || user.getOrgId().intValue() != userInfo.getOrgId()) {
			return ResultVO.FAIL("该用户不存在");
		}

		// 检查设备操作频率
		String key = RedisGlobal.DEVICE_CONTROL + signet.getId();
		Object obj = redisUtil.get(key);
		if (obj != null) {
			return ResultVO.FAIL("设备操作过于频繁，请稍后重试");
		}

		// 组包推送消息
		MQPKG mqpkg = new MQPKG();
		mqpkg.setUserId(userInfo.getId());
		mqpkg.setDeviceId(signetId);
		mqpkg.setCmd(MqGlobal.SIGNET_FINGER_DEL);

		MQFinger mf = new MQFinger();
		mf.setDeviceId(signetId);
		mf.setUserId(userId);
		mf.setUserName(user.getUserName());

		mqpkg.setData(JSONObject.toJSONString(mf));

		// 发送
		mqSender.sendToExchange(properties.getRabbitMq().getExchangeOrder(), mqpkg);
		log.info("-\tMQ-下发指令-序列号:{}\t删除指纹\tuserId:{}\tname:{}\tmessage:{}", mqpkg.getSerialId(), userInfo.getId(), userInfo.getUserName(), CommonUtils.objJsonWithIgnoreFiled(mqpkg));

		// 设置该设备操作频率
		redisUtil.set(key, System.currentTimeMillis(), RedisGlobal.DEVICE_CONTROL_TIMEOUT);

		return ResultVO.OK("指令已下发");
	}

	/**
	 * 真正的指纹录入
	 */
	public void _fingerPrint(MQPKG pkg) throws Exception {
		String data = pkg.getData();
		if (StringUtils.isBlank(data)) {
			return;
		}

		//解包
		MQFinger fp = JSONObject.parseObject(data, MQFinger.class);
		Integer signetId = fp.getDeviceId();
		Integer userId = fp.getUserId();
		String userName = fp.getUserName();
		if (WebSocketMap.get(signetId) != null) {
			String key = RedisGlobal.DEVICE_FINGER + signetId;
			int maxAddr = 1;
			synchronized (this) {
				//查询数据库该设备指纹地址列表
				List<Integer> fingerNums = fingerService.getAddrByDevice(signetId);
				if (fingerNums == null) {
					fingerNums = new ArrayList<>();
				}

				//查询缓存该设备指纹地址列表
				Object fingerNumsByRedis = redisUtil.get(key);
				List<Integer> fingerNumList = null;
				if (fingerNumsByRedis != null && StringUtils.isNotBlank(fingerNumsByRedis.toString())) {
					fingerNumList = JSONObject.parseArray(fingerNumsByRedis.toString(), Integer.class);
				}
				if (fingerNumList != null && fingerNumList.size() > 0) {
					fingerNums.addAll(fingerNumList);
				}

				if (fingerNums.size() > 0) {
					for (int i = 1; i < 2000; i++) {
						if (!fingerNums.contains(maxAddr)) {
							break;
						}
						maxAddr++;
					}
				}

				/*
				 * 将指纹存缓存
				 */
				if (fingerNumList == null) {
					fingerNumList = new ArrayList<>();
				}
				fingerNumList.add(maxAddr);

				redisUtil.set(key, JSONObject.toJSONString(fingerNumList), RedisGlobal.DEVICE_FINGER_TIMEOUT);

			}

			//组包
			FingerPrintRecordReq req = new FingerPrintRecordReq();
			req.setUserID(userId);
			req.setDeviceID(signetId);
			req.setUserName(userName);
			req.setCodeId(pkg.getUserId());
			req.setFingerAddr(maxAddr);
			MHPkg res = MHPkg.res(AppConstant.FP_RECORD_REQ, req);
			WebSocketMap.sendAes(signetId, JSONObject.toJSONString(res));
		}
	}

	/**
	 * 向队列推送·指纹录入·指令
	 *
	 * @param signetId 设备ID
	 * @param userId   指纹录入人id
	 * @return
	 */
	@WebLogger("录入指纹")
	@PostMapping("/fingerPrint")
	public ResultVO fingerPrint(@RequestParam("signetId") Integer signetId, @RequestParam("userId") Integer userId) {

		// 参数校验:印章ID
		UserInfo userInfo = getUserInfo();
		Signet signet = signetService.get(signetId);
		if (signet == null || signet.getOrgId().intValue() != userInfo.getOrgId()) {
			return ResultVO.FAIL("设备不存在");
		}

		Integer status = signet.getStatus();
		if (status != null && status == 4) {
			return ResultVO.FAIL("设备已被锁定");
		}
		Integer online = isOnline(signetId);
		if (online == null) {
			return ResultVO.FAIL("很抱歉,设备当前不在线");
		}
		if (online == 1) {
			return ResultVO.FAIL("很抱歉，设备当前正在使用中，无法录入指纹");
		}

		/*
		 * 参数校验：指纹所属人
		 */
		User user = userService.get(userId);
		if (user == null || user.getOrgId().intValue() != userInfo.getOrgId()) {
			return ResultVO.FAIL("该用户不存在");
		}
		if (StringUtils.isBlank(user.getUserName())) {
			return ResultVO.FAIL("该用户未填写真实姓名,无法录入指纹");
		}
		Integer type = user.getType();
		if (type != null && type == 3) {
			return ResultVO.FAIL("该用户未激活,无法录入指纹");
		}
		Finger finger = fingerService.getByUser(user.getId(), signetId);
		if (finger != null) {
			return ResultVO.FAIL("该用户指纹已存在");
		}

		// * 检查设备操作频率
		String key = RedisGlobal.DEVICE_CONTROL + signet.getId();
		Object obj = redisUtil.get(key);
		if (obj != null) {
			return ResultVO.FAIL("设备操作过于频繁，请稍后重试");
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			log.error("出现异常", e);
		}

		//组包，发往队列中
		MQPKG mqpkg = new MQPKG();
		mqpkg.setCmd(MqGlobal.SIGNET_FINGER_ADD);
		mqpkg.setDeviceId(signetId);
		mqpkg.setUserId(getToken().getUserId());

		/*处理用户名超过6个汉字或12个字母,仅截取前6个汉字或12个字母*/
		String userName = user.getUserName();
		if (userName.length() > 6) {
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
		}

		MQFinger fp = new MQFinger();
		fp.setDeviceId(signetId);
		fp.setUserId(userId);
		fp.setUserName(userName);

		mqpkg.setData(JSONObject.toJSONString(fp));

		//发送--->
		mqSender.sendToExchange(properties.getRabbitMq().getExchangeOrder(), mqpkg);
		log.info("-\tMQ-下发指令-序列号:{}\t录入指纹\tuserId:{}\tname:{}\tmessage:{}", mqpkg.getSerialId(), userInfo.getId(), userInfo.getUserName(), CommonUtils.objJsonWithIgnoreFiled(mqpkg));

		/*
		 * 设置该设备操作频率
		 */
		redisUtil.set(key, System.currentTimeMillis(), RedisGlobal.DEVICE_CONTROL_TIMEOUT);

		return ResultVO.OK("指纹录入指令已下发");
	}

}
