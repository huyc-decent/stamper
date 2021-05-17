package com.yunxi.stamper.controller;

import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.response.Code;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.AppVersion;
import com.yunxi.stamper.entity.FileInfo;
import com.yunxi.stamper.entityVo.UserInfo;
import com.yunxi.stamper.service.AppVersionService;
import com.yunxi.stamper.service.FileInfoService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
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
 * @date 2019/5/28 0028 12:45
 */
@Slf4j
@Api(tags = "移动APP版本")
@RestController
@RequestMapping("/auth/appVersion")
public class AppVersionController extends BaseController {

	@Autowired
	private AppVersionService service;
	@Autowired
	private FileInfoService fileInfoService;

	/**
	 * 检查版本
	 */
	@RequestMapping("/checkVersion")
	public ResultVO checkVersion(@RequestParam("version") String version, @RequestParam("client") String client) {
		if (StringUtils.isAnyBlank(version, client)) {
			return ResultVO.OK();
		}

		//查询该客户端的最新版本
		AppVersion av = service.getLastVersion(client);
		if (av == null) {
			return ResultVO.OK();
		}

		//比较客户端版本是否最新
		String localVersion = av.getVersion();
		if (StringUtils.isBlank(localVersion)) {
			return ResultVO.OK();
		}
		if (localVersion.equalsIgnoreCase(version)) {
			return ResultVO.OK();
		}

		String[] locals = localVersion.split("\\.");
		String[] versions = version.split("\\.");
		for (int i = 0; i < locals.length; i++) {
			int localInt;//当前版本号
			try {
				String local = locals[i];
				localInt = Integer.parseInt(local);
			} catch (NumberFormatException e) {
				log.error("出现异常 ", e);
				return ResultVO.OK();
			}

			int vInt;//客户端版本号
			try {
				String v = versions[i];
				vInt = Integer.parseInt(v);
			} catch (NumberFormatException e) {
				log.error("出现异常 ", e);
				return ResultVO.OK(av);
			}

			/*
			 * 如果当前版本大于客户端版本，直接返回当前版本
			 */
			if (localInt > vInt) {
				return ResultVO.OK(av);
			}

			/*
			 * 如果当前版本小于客户端版本，不返回
			 */
			if (vInt > localInt) {
				return ResultVO.OK();
			}
		}

		return ResultVO.OK();
	}

	/**
	 * 新增版本
	 *
	 * @param fileId 文件ID
	 * @return 结果json
	 */
	@RequestMapping("/add")
	public ResultVO add(String fileId, String version, String remark) {
		//仅平台用户可操作
		UserInfo userInfo = getUserInfo();
		if (userInfo.getType() == null || userInfo.getType() != 0) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		if (StringUtils.isBlank(version)) {
			return ResultVO.FAIL("版本号不能为空");
		}

		if (StringUtils.isBlank(remark)) {
			return ResultVO.FAIL("版本描述不能为空,请填写详细的版本描述信息");
		}

		FileInfo file = fileInfoService.get(fileId);
		if (file == null) {
			return ResultVO.FAIL("当前文件不存在");
		}
		AppVersion appVersion = new AppVersion();
		//获取文件地址
		appVersion.setCreateDate(new Date());
		appVersion.setRemark(remark);
		appVersion.setVersion(version);
		appVersion.setAndroid(CommonUtils.generatorURL(file.getHost(), file.getRelativePath()) + "?1=1");
		appVersion.setIos(System.currentTimeMillis()+ "");
		appVersion.setFileSize(file.getSize());
		service.add(appVersion);
		return ResultVO.OK("添加成功");
	}

	/**
	 * 删除版本
	 *
	 * @param version 版本号
	 * @return 结果json
	 */
	@RequestMapping("/del")
	public ResultVO del(String version) {
		if (StringUtils.isBlank(version)) {
			return ResultVO.FAIL(Code.FAIL402);
		}

		//仅平台用户可操作
		UserInfo userInfo = getUserInfo();
		if (userInfo.getType() == null || userInfo.getType() != 0) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		AppVersion byVersion = service.getByVersion(version);
		if (byVersion == null) {
			return ResultVO.FAIL("当前版本不存在");
		}
		service.delete(byVersion);
		return ResultVO.OK("删除成功");


	}

	/**
	 * 版本列表
	 *
	 * @return 结果json
	 */
	@RequestMapping("/getAll")
	public ResultVO getAll() {
		//仅平台用户可操作
		UserInfo userInfo = getUserInfo();
		if (userInfo.getType() == null || userInfo.getType() != 0) {
			return ResultVO.FAIL(Code.FAIL403);
		}
		//获取所有的版本列表
		List<AppVersion> all = service.getAll();
		return ResultVO.OK(all);
	}
}
