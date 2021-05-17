package com.yunxi.stamper.controller;

import com.github.pagehelper.PageHelper;
import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.response.Code;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.ConfigTemp;
import com.yunxi.stamper.entityVo.UserInfo;
import com.yunxi.stamper.service.ConfigTempService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description 配置模板控制层
 * @date 2019/8/12 0012 16:05
 */
@Api(tags = "设备配置模板")
@RestController
@RequestMapping("/device/configTemp")
public class ConfigTempController extends BaseController {

	@Autowired
	private ConfigTempService service;

	/**
	 * 添加配置模板
	 */
	@RequestMapping("/add")
	public ResultVO add(@RequestParam("configUrl") String configUrl,
						@RequestParam("serviceUrl") String serviceUrl,
						@RequestParam("thirdUrl") String thirdUrl,
						@RequestParam("sealRecordInfoNormalUrl") String sealRecordInfoNormal,
						@RequestParam("sealRecordInfoAuditorUrl") String sealRecordInfoAuditor,
						@RequestParam("sealRecordInfoEasyUrl") String sealRecordInfoEasy,
						@RequestParam("status") Integer status,
						@RequestParam("name") String name,
						@RequestParam("remark") String remark,
						@RequestParam("type") Integer type) {

		//仅平台账户可用
		UserInfo userInfo = getUserInfo();
		if (userInfo.getType() != 0) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		if (type == 0) {
			//默认模板只能存在一个
			ConfigTemp defaultConfigTemp = service.getByDefault();
			if (defaultConfigTemp != null) {
				return ResultVO.FAIL("默认模板已存在(默认类型只能存在一个)");
			}
		}

		ConfigTemp configTemp = new ConfigTemp();
		configTemp.setConfigUrl(configUrl);
		configTemp.setServiceUrl(serviceUrl);
		configTemp.setThirdUrl(thirdUrl);
		configTemp.setSealRecordInfoAuditorUrl(sealRecordInfoAuditor);
		configTemp.setSealRecordInfoEasyUrl(sealRecordInfoEasy);
		configTemp.setSealRecordInfoNormalUrl(sealRecordInfoNormal);
		configTemp.setStatus(status);
		configTemp.setName(name);
		configTemp.setRemark(remark);
		configTemp.setType(type);
		service.add(configTemp);
		return ResultVO.OK("模板添加成功");
	}

	/**
	 * 服务器新旧版本切换
	 *
	 * @param configTempId 配置信息ID
	 * @param status       0：旧版本 1：新版本
	 * @return
	 */
	@RequestMapping("/changeVersions")
	public ResultVO changeVersions(@RequestParam("configTempId") Integer configTempId,
								   @RequestParam("status") Integer status) {

		//仅平台账户可用
		UserInfo userInfo = getUserInfo();
		if (userInfo.getType() != 0) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		ConfigTemp configTemp = service.get(configTempId);

		if (configTemp == null) {
			return ResultVO.FAIL("该配置模板不存在");
		} else {
			configTemp.setStatus(status);
			service.update(configTemp);
			return ResultVO.OK("已更新");
		}
	}

	/**
	 * 删除配置模板
	 */
	@RequestMapping("/del")
	public ResultVO del(@RequestParam("id") Integer id) {
		//仅平台账户可用
		UserInfo userInfo = getUserInfo();
		if (userInfo.getType() != 0) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		ConfigTemp configTemp = service.get(id);
		if (configTemp != null) {
			service.del(configTemp);
			return ResultVO.OK();
		}
		return ResultVO.FAIL("配置模板不存在");
	}

	/**
	 * 修改配置模板
	 */
	@RequestMapping("/update")
	public ResultVO update(@RequestParam("id") Integer id,
						   @RequestParam("configUrl") String configUrl,
						   @RequestParam("serviceUrl") String serviceUrl,
						   @RequestParam("thirdUrl") String thirdUrl,
						   @RequestParam("sealRecordInfoNormalUrl") String sealRecordInfoNormal,
						   @RequestParam("sealRecordInfoAuditorUrl") String sealRecordInfoAuditor,
						   @RequestParam("sealRecordInfoEasyUrl") String sealRecordInfoEasy,
						   @RequestParam("status") Integer status,
						   @RequestParam("name") String name,
						   @RequestParam("remark") String remark,
						   @RequestParam("type") Integer type) {
		//仅平台账户可用
		UserInfo userInfo = getUserInfo();
		if (userInfo.getType() != 0) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		ConfigTemp configTemp = service.get(id);
		if (configTemp != null) {

			if (type == 0) {
				//默认模板只能存在一个
				ConfigTemp defaultConfigTemp = service.getByDefault();
				if (defaultConfigTemp != null) {
					return ResultVO.FAIL("默认模板已存在(默认类型只能存在一个)");
				}
			}

			configTemp.setConfigUrl(configUrl);
			configTemp.setServiceUrl(serviceUrl);
			configTemp.setThirdUrl(thirdUrl);
			configTemp.setSealRecordInfoAuditorUrl(sealRecordInfoAuditor);
			configTemp.setSealRecordInfoEasyUrl(sealRecordInfoEasy);
			configTemp.setSealRecordInfoNormalUrl(sealRecordInfoNormal);
			configTemp.setStatus(status);
			configTemp.setType(type);
			configTemp.setName(name);
			configTemp.setRemark(remark);
			service.update(configTemp);
			return ResultVO.OK();
		}
		return ResultVO.FAIL("配置模板不存在");
	}

	/**
	 * 查询配置模板
	 */
	@RequestMapping("/get")
	public ResultVO get() {
		return ResultVO.OK();
	}

	/**
	 * 查询配置模板列表
	 */
	@RequestMapping("/getList")
	public ResultVO getList(@RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
							@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
							@RequestParam(value = "page", required = false, defaultValue = "true") boolean isPage) {
		//仅平台账户可用
		UserInfo userInfo = getUserInfo();
		if (userInfo.getType() != 0) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		if (isPage) {
			PageHelper.startPage(pageNum, pageSize);
		}
		List<ConfigTemp> configTemps = service.getList();
		return ResultVO.Page(configTemps, isPage);
	}
}
