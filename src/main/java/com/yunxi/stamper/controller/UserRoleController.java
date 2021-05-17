package com.yunxi.stamper.controller;


import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.Role;
import com.yunxi.stamper.entity.User;
import com.yunxi.stamper.entityVo.UserInfo;
import com.yunxi.stamper.service.*;
import com.yunxi.stamper.sys.aop.annotaion.WebLogger;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/7 0007 10:52
 */
@Slf4j
@Api(tags = "用户<==>角色")
@RestController
@RequestMapping(value = "/auth/userRole", method = {RequestMethod.POST, RequestMethod.GET})
public class UserRoleController extends BaseController {

	@Autowired
	private UserRoleService service;
	@Autowired
	private UserService userService;
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private RoleService roleService;

	/**
	 * 为指定员工绑定角色列表
	 */
	@ApiOperation(value = "修改员工角色", notes = "修改员工角色", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userId", value = "员工ID", dataType = "Integer", required = true),
			@ApiImplicitParam(name = "roleIds", value = "角色ID列表", dataType = "Integer[]", required = true)
	})
	@WebLogger("修改员工角色")
	@RequestMapping("/bindRoles")
	@Transactional
	public ResultVO bindRoles(@RequestParam("userId") Integer userId,
							  @RequestParam("roleIds") Integer[] roleIds) {
		/*
		 * 校验员工信息
		 */
		User user = userService.get(userId);
		if (user == null) {
			return ResultVO.FAIL("员工不存在");
		}

		/*
		 * 校验操作人权限
		 */
		UserInfo userInfo = getUserInfo();
		if (user.getOrgId().intValue() != userInfo.getOrgId().intValue()) {
			return ResultVO.FAIL("无权限操作");
		}
		if (!userInfo.isOwner()) {
			List<Integer> departmentIds = departmentService.getChildrenIdsByOrgAndParentsAndType(userInfo.getOrgId(), userInfo.getDepartmentIds(), null);
			if (departmentIds == null || departmentIds.isEmpty()) {
				return ResultVO.FAIL("无权限操作");
			}
			List<Integer> ableUserIds = userService.getIdsByDepartmentIds(departmentIds);
			if (ableUserIds == null || ableUserIds.isEmpty() || !ableUserIds.contains(userId)) {
				return ResultVO.FAIL("无权限操作");
			}
		}

		/*
		 * 校验角色列表
		 */
		if (roleIds == null || roleIds.length == 0) {
			return ResultVO.FAIL("员工角色不能为空");
		}
		for (Integer roleId : roleIds) {
			Role role = roleService.get(roleId);
			if (role == null || role.getOrgId().intValue() != userInfo.getOrgId().intValue()) {
				return ResultVO.FAIL("角色有误");
			}
		}

		service.bindRoles(userId, roleIds);

		return ResultVO.OK("更新成功");
	}

}
