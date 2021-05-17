package com.yunxi.stamper.controller;


import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.other.Global;
import com.yunxi.stamper.commons.response.Code;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.Perms;
import com.yunxi.stamper.entity.Role;
import com.yunxi.stamper.entityVo.PermsVo;
import com.yunxi.stamper.entityVo.UserInfo;
import com.yunxi.stamper.service.PermService;
import com.yunxi.stamper.service.RolePermService;
import com.yunxi.stamper.service.RoleService;
import com.yunxi.stamper.sys.aop.annotaion.WebLogger;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/7 0007 10:49
 */
@Slf4j
@RestController
@Api(tags = "角色<=>权限")
@RequestMapping(value = "/auth/rolePerms", method = {RequestMethod.POST, RequestMethod.GET})
public class RolePermController extends BaseController {

	@Autowired
	private RolePermService service;
	@Autowired
	private RoleService roleService;
	@Autowired
	private PermService permService;

	/**
	 * 查询角色绑定的权限列表
	 */
	@RequestMapping("/getByRole")
	public ResultVO getByRole(@RequestParam("roleId") Integer roleId) {
		Role role = roleService.get(roleId);
		UserInfo userInfo = getUserInfo();
		if (role != null) {
			//只能查询本公司的角色-权限列表信息
			if (userInfo.getOrgId().intValue() != role.getOrgId().intValue()) {
				return ResultVO.FAIL(Code.FAIL403);
			}

			//查询该角色绑定的权限ID列表
			List<Integer> selected = service.getByRoleId(role.getId());

			//查询一共有哪些权限(平台账户可查询>=平台级的权限)
			List<PermsVo> permsVos = permService.getTreeByLevel(userInfo.getType());

			Map<String, Object> res = new HashMap<>(2);
			res.put("selected", selected);
			res.put("perms", permsVos);

			return ResultVO.OK(res);
		}
		return ResultVO.FAIL("该角色不存在");
	}

	@ApiOperation(value = "更新角色的权限信息", notes = "更新角色的权限信息", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "roleId", value = "角色ID", dataType = "int", required = true),
			@ApiImplicitParam(name = "permsIds", value = "权限ID列表", dataType = "Integer[]")
	})
	@WebLogger("修改权限信息")
	@PostMapping("/bindPerms")
	public ResultVO bindPerms(@RequestParam("roleId") Integer roleId,
							  @RequestParam(value = "permsIds", required = false) Integer[] permsIds) {
		/*
		 * 校验角色信息
		 */
		Role role = roleService.get(roleId);
		if (role == null) {
			return ResultVO.FAIL("角色不存在");
		}
		String code = role.getCode();
		if (StringUtils.isNotBlank(code) && Global.codes.contains(code)) {
			return ResultVO.FAIL("系统角色无法修改");
		}
		UserInfo userInfo = getUserInfo();
		if (userInfo.getOrgId().intValue() != role.getOrgId().intValue()) {
			return ResultVO.FAIL("无权限操作");
		}

		/*
		 * 校验权限列表
		 */
		if (permsIds != null && permsIds.length > 0) {
			for (Integer permsId : permsIds) {
				Perms perms = permService.get(permsId);
				if (perms == null) {
					return ResultVO.FAIL("权限有误");
				}
			}
		}

		service.bindPerms(role, permsIds, userInfo.getId());

		return ResultVO.OK("更新成功");
	}
}
