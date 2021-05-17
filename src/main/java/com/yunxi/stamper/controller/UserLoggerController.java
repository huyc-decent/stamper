package com.yunxi.stamper.controller;


import com.alibaba.fastjson.JSONObject;
import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.jwt.UserToken;
import com.yunxi.stamper.commons.other.DateUtil;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.UserLogger;
import com.yunxi.stamper.entityVo.UserLoggerVo;
import com.yunxi.stamper.service.UserLoggerService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description 用户日志控制层
 * @date 2019/5/22 0022 13:26
 */
@Slf4j
@Api(tags = "v1系统日志相关")
@RestController
@RequestMapping(value = "/auth/userLogger", method = {RequestMethod.POST, RequestMethod.GET})
public class UserLoggerController extends BaseController {

	@Autowired
	private UserLoggerService service;

	/**
	 * 查询登录用户的操作日志
	 */
	@RequestMapping("/getByUser")
	public ResultVO getByUser() {
		UserToken token = getToken();
		boolean page = setPage();
		List<UserLoggerVo> vos = service.getByUserAndNormal(token.getOrgId(),token.getUserId());
		if (vos != null && vos.size() > 0) {
			for (UserLoggerVo ul : vos) {
				//时间
				ul.setTimes(DateUtil.distanceOfTimeInWords(ul.getCreateDate()));
				Integer status = ul.getStatus();
				String remark = ul.getRemark();
				String error = ul.getError();
				if (status != 0 && StringUtils.isNotBlank(error)) {
					remark = remark + "(" + error + ")";
				}
				//描述
				ul.setRemark(remark);
			}
			return ResultVO.Page(vos, page);
		}
		return ResultVO.OK();
	}

	@RequestMapping("/update")
	public void update(@RequestParam("userLogger") String userLoggerJson) {
		UserLogger userLogger = null;
		try {
			userLogger = JSONObject.parseObject(userLoggerJson, UserLogger.class);
		} catch (Exception e) {
			log.error("出现异常 userLoggerJson:{}",userLoggerJson, e);
		}
		if (userLogger != null && userLogger.getId() != null) {
			service.update(userLogger);
		}
	}

	@RequestMapping("/add")
	public UserLogger add(@RequestParam("userLogger") String userLoggerJson) {
		UserLogger userLogger = null;
		try {
			userLogger = JSONObject.parseObject(userLoggerJson, UserLogger.class);
		} catch (Exception e) {
			log.error("出现异常 userLoggerJson:{}",userLoggerJson, e);
		}
		if (userLogger != null) {
			service.add(userLogger);
			return userLogger;
		}
		return null;
	}
}
