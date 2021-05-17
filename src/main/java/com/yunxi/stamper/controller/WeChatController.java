package com.yunxi.stamper.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.jwt.AES.AesUtil;
import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.WechatControl;
import com.yunxi.stamper.entityVo.UserInfo;
import com.yunxi.stamper.logger.model.LocalModel;
import com.yunxi.stamper.logger.threadLocal.LocalHandle;
import com.yunxi.stamper.service.WeChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2021/3/10 13:25
 */
@Slf4j
@Api(tags = "小程序相关")
@RestController
@RequestMapping(value = "/wechat/control")
public class WeChatController extends BaseController {

	@Autowired
	private WeChatService weChatService;

	@ApiOperation(value = "校验openId")
	@RequestMapping("/verify")
	public ResultVO verify(@RequestParam String openId) throws Exception {
		WechatControl wechatControl = weChatService.getByOpenId(openId);
		if (wechatControl == null) {
			return new ResultVO(200, "", 500);
		}
		String encrypt = AesUtil.encrypt(JSONObject.toJSONString(wechatControl), openId);

		return ResultVO.OK("", encrypt);
	}

	@ApiOperation(value = "新增小程序", notes = "新增小程序")
	@RequestMapping("/add")
	public ResultVO add(@RequestParam String customerOrganizationName,
						@RequestParam String customerName,
						@RequestParam String customerPhone,
						@RequestParam String customerWxOpenId,
						@RequestParam String customerServiceUrlPrefix,
						@RequestParam String serviceStaffName,
						@RequestParam String serviceStaffPhone,
						@RequestParam(required = false) Date expiryTime) {

		//权限校验,只有平台员工账号允许操作
		UserInfo userInfo = getUserInfo();
		Integer type = userInfo.getType();
		if (type == null || !Objects.equals(type, 0)) {
			return ResultVO.FAIL("无权限");
		}

		//openId不允许重复
		if (StringUtils.isBlank(customerWxOpenId)) {
			return ResultVO.FAIL("openId不允许为空");
		}
		if (customerWxOpenId.length() != 16) {
			return ResultVO.FAIL("openId必须保持16位");
		}
		WechatControl localByOpenId = weChatService.getByOpenId(customerWxOpenId);
		if (localByOpenId != null) {
			return ResultVO.FAIL("openId已存在");
		}

		//新增小程序控制记录
		WechatControl wechat = new WechatControl();
		wechat.setActiveTime(new Date());
		wechat.setServiceStaffPhone(serviceStaffPhone);
		wechat.setServiceStaffName(serviceStaffName);
		wechat.setCustomerServiceUrlPrefix(customerServiceUrlPrefix);
		wechat.setCustomerWxOpenId(customerWxOpenId);
		wechat.setCustomerPhone(customerPhone);
		wechat.setCustomerName(customerName);
		wechat.setCustomerOrganizationName(customerOrganizationName);
		wechat.setExpiryTime(expiryTime);
		weChatService.add(wechat);

		log.info("-\t新增小程序\t操作人:{}\t用户Id:{}\t记录:{}", userInfo.getUserName(), userInfo.getId(), CommonUtils.objJsonWithIgnoreFiled(wechat));

		//记录业务操作日志
		LocalHandle.setOperator(userInfo);
		LocalHandle.setNewObj(wechat);
		LocalHandle.complete("新增小程序");

		return ResultVO.OK();
	}


	@ApiOperation(value = "删除小程序", notes = "删除小程序")
	@RequestMapping("/delete")
	public ResultVO delete(@RequestParam Integer wechatId) {
		//权限校验,只有平台员工账号允许操作
		UserInfo userInfo = getUserInfo();
		Integer type = userInfo.getType();
		if (type == null || !Objects.equals(type, 0)) {
			return ResultVO.FAIL("无权限");
		}

		//查询小程序
		WechatControl wechat = weChatService.get(wechatId);
		if (wechat == null) {
			return ResultVO.FAIL("小程序不存在");
		}

		//记录业务操作日志
		LocalHandle.setOldObj(wechat);
		LocalHandle.setOperator(userInfo);

		//删除小程序控制记录
		weChatService.delete(wechat);

		log.info("-\t删除小程序\t操作人:{}\t用户Id:{}\t记录:{}", userInfo.getUserName(), userInfo.getId(), CommonUtils.objJsonWithIgnoreFiled(wechat));

		//记录业务操作日志
		LocalHandle.setNewObj(wechat);
		LocalHandle.complete("删除小程序");

		return ResultVO.OK();
	}

	@ApiOperation(value = "更新小程序", notes = "更新小程序")
	@RequestMapping("/update")
	public ResultVO update(@RequestParam Integer id,
						   @RequestParam String customerOrganizationName,
						   @RequestParam String customerName,
						   @RequestParam String customerPhone,
						   @RequestParam String customerWxOpenId,
						   @RequestParam String customerServiceUrlPrefix,
						   @RequestParam String serviceStaffName,
						   @RequestParam String serviceStaffPhone,
						   @RequestParam(required = false) Date expiryTime) {
		//权限校验,只有平台员工账号允许操作
		UserInfo userInfo = getUserInfo();
		Integer type = userInfo.getType();
		if (type == null || !Objects.equals(type, 0)) {
			return ResultVO.FAIL("无权限");
		}

		//查询小程序
		WechatControl wechat = weChatService.get(id);
		if (wechat == null) {
			return ResultVO.FAIL("小程序不存在");
		}

		//openId不允许重复
		if (StringUtils.isBlank(customerWxOpenId)) {
			return ResultVO.FAIL("openId不允许为空");
		}
		if (customerWxOpenId.length() != 16) {
			return ResultVO.FAIL("openId必须保持16位");
		}
		WechatControl localByOpenId = weChatService.getByOpenId(customerWxOpenId);
		if (localByOpenId != null && !Objects.equals(localByOpenId.getId(), id)) {
			return ResultVO.FAIL("openId已存在");
		}

		//记录业务操作日志
		LocalHandle.setOldObj(wechat);
		LocalHandle.setOperator(userInfo);

		//更新小程序
		wechat.setServiceStaffPhone(serviceStaffPhone);
		wechat.setServiceStaffName(serviceStaffName);
		wechat.setCustomerServiceUrlPrefix(customerServiceUrlPrefix);
		wechat.setCustomerWxOpenId(customerWxOpenId);
		wechat.setCustomerPhone(customerPhone);
		wechat.setCustomerName(customerName);
		wechat.setCustomerOrganizationName(customerOrganizationName);
		wechat.setExpiryTime(expiryTime);
		weChatService.update(wechat);

		log.info("-\t更新小程序\t操作人:{}\t用户Id:{}\t记录:{}", userInfo.getUserName(), userInfo.getId(), CommonUtils.objJsonWithIgnoreFiled(wechat));

		//记录业务操作日志
		LocalHandle.setNewObj(wechat);
		LocalHandle.complete("删除小程序");

		return ResultVO.OK();
	}

	@ApiOperation(value = "小程序列表", notes = "小程序列表")
	@RequestMapping("/list")
	public ResultVO list(@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
						 @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
						 @RequestParam(value = "page", required = false, defaultValue = "true") boolean isPage) {
		//权限校验,只有平台员工账号允许操作
		UserInfo userInfo = getUserInfo();
		Integer type = userInfo.getType();
		if (type == null || !Objects.equals(type, 0)) {
			return ResultVO.FAIL("无权限");
		}

		//查询记录
		if (isPage) {
			PageHelper.startPage(pageNum, pageSize);
		}
		List<WechatControl> list = weChatService.list();
		if (list == null || list.isEmpty()) {
			return ResultVO.OK(list);
		}

		return ResultVO.Page(list, isPage);
	}
}
