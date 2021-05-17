package com.yunxi.stamper.controller;


import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.SmsTemp;
import com.yunxi.stamper.service.SmsTempService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/27 0027 18:20
 */
@Slf4j
@Api(tags = "短信模板相关")
@RestController
@RequestMapping("/auth/smsTemp")
public class SmsTempController extends BaseController {

	@Autowired
	private SmsTempService service;

	/**
	 * 查询短信模板列表
	 */
	@RequestMapping("/getList")
	public ResultVO getList(){
		boolean page = setPage();
		List<SmsTemp> all = service.getAll();
		return ResultVO.Page(all,page);
	}

}
