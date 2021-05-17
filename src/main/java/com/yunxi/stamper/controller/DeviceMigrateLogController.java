package com.yunxi.stamper.controller;

import com.github.pagehelper.PageHelper;
import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entityVo.MigrateVo;
import com.yunxi.stamper.service.DeviceMigrateLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2020/3/20 0020 9:45
 */
@Api(tags = "设备迁移日志相关")
@RestController
@RequestMapping("/deviceMigrateLog")
public class DeviceMigrateLogController extends BaseController {

	@Autowired
	private DeviceMigrateLogService service;

	@ApiOperation(value = "查询组织下的员工列表", notes = "查询组织下的员工列表", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageNum", value = "当前页(默认:1)", dataType = "int"),
			@ApiImplicitParam(name = "pageSize", value = "每页数(默认:10)", dataType = "int"),
			@ApiImplicitParam(name = "page", value = "是否分页", dataType = "boolean", defaultValue = "true"),
			@ApiImplicitParam(name = "filter", value = "是否筛选过滤重复数据", dataType = "boolean", defaultValue = "true"),
			@ApiImplicitParam(name = "deviceId", value = "设备ID", dataType = "int")
	})
	@GetMapping("/getList")
	public ResultVO getList(@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
							@RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
							@RequestParam(value = "page", required = false, defaultValue = "false") boolean isPage,
							@RequestParam(value = "filter", required = false, defaultValue = "true") boolean filter,
							@RequestParam Integer deviceId) {

		if (isPage) {
			PageHelper.startPage(pageNum, pageSize);
		}
		List<MigrateVo> migrateVos = service.getList(deviceId,filter);
		return ResultVO.Page(migrateVos, isPage);
	}
}
