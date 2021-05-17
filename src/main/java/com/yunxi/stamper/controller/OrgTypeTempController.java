package com.yunxi.stamper.controller;


import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.OrgTypeTemp;
import com.yunxi.stamper.service.OrgTypeTempService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/7 0007 16:11
 */
@Slf4j
@Api(tags = "组织类型相关")
@RestController
@RequestMapping(value = "/auth/orgTypeTemp", method = {RequestMethod.POST, RequestMethod.GET})
public class OrgTypeTempController extends BaseController {

	@Autowired
	private OrgTypeTempService service;

	/**
	 * 查询组织类型模板
	 */
	@RequestMapping("/get")
	public ResultVO get(@RequestParam("id") Integer tempId) {
		if (tempId != null) {
			OrgTypeTemp orgTypeTemp = service.get(tempId);
			return ResultVO.OK(orgTypeTemp);
		}
		return ResultVO.OK();
	}
}
