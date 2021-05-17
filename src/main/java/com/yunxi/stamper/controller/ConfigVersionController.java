package com.yunxi.stamper.controller;


import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.response.Code;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.Config;
import com.yunxi.stamper.entity.ConfigVersion;
import com.yunxi.stamper.entity.FileInfo;
import com.yunxi.stamper.entityVo.UserInfo;
import com.yunxi.stamper.service.ConfigService;
import com.yunxi.stamper.service.ConfigVersionService;
import com.yunxi.stamper.service.FileInfoService;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/7/4 0004 14:28
 */
@Api(tags = "设备版本配置相关")
@RestController
@RequestMapping("/device/configVersion")
public class ConfigVersionController extends BaseController {

	@Autowired
	private ConfigVersionService service;
	@Autowired
	private FileInfoService fileInfoService;
	@Autowired
	private ConfigService configService;

	/**
	 * 查询版本列表
	 *
	 * @param type 0:查询安卓 1：查询Linux
	 * @return 结果
	 */
	@RequestMapping("/getList")
	public ResultVO getList(@RequestParam(value = "type", required = false) Integer type) {
		//仅平台账户可用
		UserInfo userInfo = getUserInfo();
		if (userInfo.getType() != 0) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		boolean page = setPage();
		List<ConfigVersion> configVersions = service.getAll(type);
		return ResultVO.Page(configVersions, page);
	}

	/**
	 * 添加新版本APK
	 *
	 * @param version 版本号
	 * @param remark  备注
	 * @param fileId  文件ID
	 * @param type    0:安卓系统更新包 1:Linux系统更新包 2:单片机
	 * @return 结果
	 */
	@RequestMapping("/add")
	public ResultVO add(@RequestParam("version") String version,
						@RequestParam("remark") String remark,
						@RequestParam("fileId") String fileId,
						@RequestParam(value = "type", required = false, defaultValue = "0") Integer type) {
		//仅平台账户可用
		UserInfo userInfo = getUserInfo();
		if (userInfo.getType() != 0) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		if (StringUtils.isBlank(remark)) {
			return ResultVO.FAIL("版本描述不能为空,请填写详细的版本描述信息");
		}

		if (StringUtils.isBlank(version)) {
			return ResultVO.FAIL("版本号不能为空");
		}

		if (fileId == null) {
			return ResultVO.FAIL("更新APK文件不能为空");
		}

		FileInfo fileInfo = fileInfoService.get(fileId);
		if (fileInfo == null) {
			return ResultVO.FAIL("更新APK文件不能为空");
		}
		//检查版本是否存在
		ConfigVersion configVersion = service.getByVersion(version, type);
		if (configVersion != null) {
			return ResultVO.FAIL("版本[" + version + "]已存在");
		}

		//创建版本对象信息
		ConfigVersion cv = new ConfigVersion();
		cv.setCreateDate(new Date());
		cv.setRemark(remark);
		cv.setUrl(CommonUtils.generatorURL(fileInfo.getHost(), fileInfo.getRelativePath()));
		cv.setVersion(version);
		cv.setType(type);
		cv.setHash(fileInfo.getHash());
		service.add(cv);
		return ResultVO.OK("版本[" + version + "]添加成功");
	}

	@RequestMapping("/get")
	public ResultVO get(@RequestParam("uuid") String uuid) {
		//仅平台账户可用
		UserInfo userInfo = getUserInfo();
		if (userInfo.getType() != 0) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		Config config = configService.getByUUID(uuid);
		if (config == null) {
			config = configService.getDefaultConfig();
		}

		ConfigVersion configVersion = new ConfigVersion();
		configVersion.setVersion(config.getVersion());
		configVersion.setUrl(config.getVersionUrl());
		configVersion.setRemark(config.getApkName());

		return ResultVO.OK(configVersion);
	}

	@RequestMapping("/del")
	public ResultVO del(@RequestParam("version") String version, @RequestParam(value = "type", required = false, defaultValue = "0") Integer type) {
		//仅平台账户可用
		UserInfo userInfo = getUserInfo();
		if (userInfo.getType() != 0) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		ConfigVersion cv = service.getByVersion(version, type);
		if (cv == null) {
			return ResultVO.FAIL("该版本APK不存在");
		}
		service.del(cv);
		return ResultVO.OK("删除成功");
	}
}
