package com.yunxi.stamper.controller;


import com.github.pagehelper.PageHelper;
import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.Position;
import com.yunxi.stamper.entityVo.UserInfo;
import com.yunxi.stamper.service.PositionService;
import com.yunxi.stamper.sys.aop.annotaion.WebLogger;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/11/25 0025 13:46
 */
@Api(tags = {"职称相关"})
@RestController
@RequestMapping("/auth/position")
public class PositionController extends BaseController {

	@Autowired
	private PositionService service;

	@ApiOperation(value = "添加职称", notes = "给组织添加一个职称", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "name", value = "职位名称", dataType = "String", required = true)
	})
	@WebLogger("添加职称")
	@PostMapping("/addPosition")
	public ResultVO addPosition(@RequestParam("name") String name) {

		UserInfo userInfo = getUserInfo();

		/*
		 * 校验操作人权限
		 */
		List<Integer> departmentIds = userInfo.getDepartmentIds();
		if (departmentIds == null || departmentIds.isEmpty()) {
			return ResultVO.FAIL("无权限");
		}

		/*
		 * 校验职称名称
		 */
		if (StringUtils.isBlank(name)) {
			return ResultVO.FAIL("职称名称不能为空");
		}
		name = name.trim();
		Position position = service.getByOrgIdAndName(userInfo.getOrgId(), name);
		if (position != null) {
			return ResultVO.FAIL("该职称已存在");
		}

		service.addPosition(userInfo.getOrgId(), userInfo.getId(), name);

		return ResultVO.OK("添加成功");
	}

	@ApiOperation(value = "删除职称", notes = "删除一个职称", httpMethod = "DELETE")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "职称ID", dataType = "int", required = true, paramType = "path")
	})
	@WebLogger("删除职称")
	@DeleteMapping("/delPosition/{id}")
	public ResultVO delPosition(@PathVariable("id") Integer id) {
		UserInfo userInfo = getUserInfo();
		/*
		 * 校验操作人权限
		 */
		List<Integer> departmentIds = userInfo.getDepartmentIds();
		if (departmentIds == null || departmentIds.isEmpty()) {
			return ResultVO.FAIL("无权限");
		}

		/*
		 * 校验职称
		 */
		Position position = service.get(id);
		if (position == null || position.getOrgId().intValue() != userInfo.getOrgId().intValue()) {
			return ResultVO.FAIL("职称不存在");
		}

		service.delPosition(userInfo.getOrgId(), userInfo.getId(), id);

		return ResultVO.OK("删除成功");
	}


	@ApiOperation(value = "查询职称列表", notes = "查询登录用户所属公司的所有职称列表", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageNum", value = "当前页(默认:1)", dataType = "int"),
			@ApiImplicitParam(name = "pageSize", value = "每页数(默认:10)", dataType = "int"),
			@ApiImplicitParam(name = "page", value = "是否分页", dataType = "boolean", defaultValue = "false")
	})
	@GetMapping("/getAllPositionList")
	public ResultVO getAllPositionList(@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
									   @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
									   @RequestParam(value = "page", required = false, defaultValue = "false") boolean isPage) {
		UserInfo userInfo = getUserInfo();
		if (isPage) {
			PageHelper.startPage(pageNum, pageSize);
		}
		List<Position> positionList = service.getByOrgId(userInfo.getOrgId());
		return ResultVO.Page(positionList, isPage);
	}
}
