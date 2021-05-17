package com.yunxi.stamper.controller;


import com.alibaba.fastjson.JSONObject;
import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.jwt.UserToken;
import com.yunxi.stamper.commons.response.Code;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.OrgSmsTemp;
import com.yunxi.stamper.entity.Serve;
import com.yunxi.stamper.entity.SmsTemp;
import com.yunxi.stamper.entityVo.SMSVoSelect;
import com.yunxi.stamper.service.OrgSmsTempService;
import com.yunxi.stamper.service.ServeService;
import com.yunxi.stamper.service.SmsTempService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/23 0023 16:10
 */
@Slf4j
@Api(tags = "集团<==>短信模板")
@RestController
@RequestMapping("/auth/smsOrg")
public class OrgSmsTempController extends BaseController {

	@Autowired
	private OrgSmsTempService service;
	@Autowired
	private SmsTempService smsTempService;
	@Autowired
	private ServeService serveService;

	/**
	 * 查询登录用户短信模板启用状态
	 */
	@RequestMapping("/getByOwner")
	public ResultVO getByOwner() {
		UserToken token = getToken();

		//查询系统短信模板
		boolean page = setPage();
		List<SMSVoSelect> allSmsTemps = smsTempService.getAllByVo();
		if (allSmsTemps == null || allSmsTemps.isEmpty()) {
			return ResultVO.OK();
		}

		//查询该集团拥有的短信模板
		List<Integer> targetOpenSmsIds = service.getByOrg(token.getOrgId());

		//查询该集团是否开启短信服务
		Integer orgId = token.getOrgId();
		Serve serve = serveService.getSMSByOrg(orgId);

		//如果该集团开启了短信服务&该集团短信模板开启了，则展示'启用'状态
		for (SMSVoSelect sms : allSmsTemps) {
			//优化 短信话术
			try {
				String content = sms.getContent();
				content = content.replaceAll(properties.getSmsSplitRegex(), "xxx");
				sms.setContent(content);
			} catch (Exception e) {
				e.printStackTrace();
			}

			sms.setUse(serve != null && targetOpenSmsIds != null && !targetOpenSmsIds.isEmpty() && targetOpenSmsIds.contains(sms.getId()));
		}

		return ResultVO.Page(allSmsTemps, page);
	}

	@ApiOperation(value = "批量启用、禁用", notes = "批量启用、禁用")
	@PostMapping("/updateBulk")
	public ResultVO updateBulk(@RequestParam Integer[] tempIds, @RequestParam Boolean status) {
		UserToken token = getToken();
		log.info("-\t短信模板\ttempIds:{}\tstatus:{}\tlogin:{}", Arrays.toString(tempIds), status, JSONObject.toJSONString(token));
		//检查用户所属集团是否启用短信服务
		Serve serve = serveService.getSMSByOrg(token.getOrgId());
		if (serve == null) {
			return ResultVO.FAIL("短信功能未开通");
		}

		//检查模板是否存在
		List<SmsTemp> temps = smsTempService.getAll();
		if (temps == null || temps.isEmpty()) {
			return ResultVO.FAIL("短信模板列表为空");
		}

		//批量更新
		service.updateBulk(token.getOrgId(), Arrays.asList(tempIds), status);
		return ResultVO.OK();
	}

	/**
	 * 修改公司短信模板
	 */
	@RequestMapping("/update")
	@Transactional
	public ResultVO update(@RequestParam("smsTempId") Integer smsTempId, @RequestParam("isUse") boolean isUse) {
		if (smsTempId != null) {
			SmsTemp st = smsTempService.get(smsTempId);
			if (st == null) {
				return ResultVO.FAIL("短信模板不存在");
			}

			//检查该公司是否开通短信服务
			UserToken token = getToken();
			log.info("-\t短信模板\tsmsTempId:{}\tuse:{}\tlogin:{}", smsTempId, isUse, JSONObject.toJSONString(token));
			Serve serve = serveService.getSMSByOrg(token.getOrgId());
			if (serve == null) {
				return ResultVO.FAIL("短信功能未开通");
			}

			OrgSmsTemp ost = service.getByOrgAndSmstemp(token.getOrgId(), smsTempId);
			if (isUse) {
				if (ost == null) {
					ost = new OrgSmsTemp();
					ost.setOrgId(token.getOrgId());
					ost.setSmsTempId(smsTempId);
					service.add(ost);
				}
			} else {
				if (ost != null) {
					service.del(ost);
				}
			}
			return ResultVO.OK();
		}

		return ResultVO.FAIL(Code.FAIL402);
	}
}
