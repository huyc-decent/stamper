package com.yunxi.stamper.controller;


import com.github.pagehelper.PageHelper;
import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.jwt.UserToken;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.Config;
import com.yunxi.stamper.entity.Signet;
import com.yunxi.stamper.entityVo.UserConfig;
import com.yunxi.stamper.entityVo.UserInfo;
import com.yunxi.stamper.service.ConfigService;
import com.yunxi.stamper.service.SignetService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/10/19 0019 10:39
 */
@Slf4j
@RestController
@RequestMapping("/device/userConfig")
public class UserConfigController extends BaseController {

	@Autowired
	private ConfigService service;
	@Autowired
	private SignetService signetService;

	/**
	 * 查询本公司所有印章配置列表
	 */
	@RequestMapping("/getList")
	public ResultVO getList(@RequestParam(value = "keyword", required = false) String keyword,
							@RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
							@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
							@RequestParam(value = "page", required = false, defaultValue = "true") boolean isPage) {
		UserInfo userInfo = getUserInfo();
		Integer orgId = userInfo.getOrgId();
		if (isPage) {
			PageHelper.startPage(pageNum, pageSize);
		}
		List<UserConfig> configs = service.getByOrgIdAndKeyword(orgId, keyword);
		return ResultVO.Page(configs, isPage);
	}

	/**
	 * 修改配置信息
	 *
	 * @param deviceId 印章ID
	 * @param svrHost  回调接口
	 * @param svrIp    通信接口
	 * @param wifiSsid wifi名称
	 * @param wifiPwd  wifi密码
	 * @return 结果
	 */
	@RequestMapping("/updateConfig")
	public ResultVO updateConfig(@RequestParam(value = "deviceId") Integer deviceId,
								 @RequestParam("svrHost") String svrHost,
								 @RequestParam("svrIp") String svrIp,
								 @RequestParam(value = "wifiSsid", required = false) String wifiSsid,
								 @RequestParam(value = "wifiPwd", required = false) String wifiPwd) {
		if (deviceId == null) {
			return ResultVO.FAIL("该设备不存在");
		}
		UserToken token = getToken();
		Signet signet = signetService.getByOrgAndDevice(token.getOrgId(), deviceId);
		if (signet == null) {
			return ResultVO.FAIL("该设备不存在");
		}

		if (StringUtils.isBlank(svrHost)) {
			return ResultVO.FAIL("设备回调接口不能为空");
		}
		if (StringUtils.isBlank(svrIp)) {
			return ResultVO.FAIL("设备通信接口不能为空");
		}

		Config config = service.getByUUID(signet.getUuid());

		if (config == null) {
			config = service.getDefaultConfig();
		}

		config.setUuid(signet.getUuid());
		config.setWifiSsid(wifiSsid);
		config.setWifiPwd(wifiPwd);
		config.setSvrHost(svrHost);
		config.setSvrIp(svrIp);

		if (config.getId() == null) {
			service.add(config);
		} else {
			service.update(config);
		}
		return ResultVO.OK();
	}
}
