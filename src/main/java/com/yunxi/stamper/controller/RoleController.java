package com.yunxi.stamper.controller;


import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.other.Global;
import com.yunxi.stamper.commons.jwt.UserToken;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.Role;
import com.yunxi.stamper.entity.User;
import com.yunxi.stamper.entityVo.RoleEntity;
import com.yunxi.stamper.entityVo.RoleVo;
import com.yunxi.stamper.entityVo.UserInfo;
import com.yunxi.stamper.logger.threadLocal.LocalHandle;
import com.yunxi.stamper.service.RoleService;
import com.yunxi.stamper.service.UserService;
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
import java.util.UUID;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/6 0006 21:39
 */
@Slf4j
@Api(tags = "角色相关")
@RestController
@RequestMapping("/auth/role")
public class RoleController extends BaseController {

	@Autowired
	private RoleService service;
	@Autowired
	private UserService userService;

	/**
	 * 搜索角色
	 */
	@ApiOperation(value = "搜索角色列表", notes = "搜索角色列表", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "keyword", value = "角色名称关键词", dataType = "String"),
			@ApiImplicitParam(name = "pageNum", value = "当前页(默认:1)", dataType = "int"),
			@ApiImplicitParam(name = "pageSize", value = "每页数(默认:10)", dataType = "int"),
			@ApiImplicitParam(name = "page", value = "是否分页", dataType = "boolean", defaultValue = "true")
	})
	@GetMapping("/searchByRoleWeb")
	public ResultVO searchByRoleWeb(@RequestParam(value = "keyword", required = false) String keyword, @RequestParam(value = "page", required = false, defaultValue = "true") boolean isPage) {
		UserInfo userInfo = getUserInfo();
		List<RoleEntity> roles = service.getByKeywordAndOrg(StringUtils.isBlank(keyword) ? null : keyword.trim(), userInfo.getOrgId());
		return ResultVO.Page(roles, isPage);
	}

	/**
	 * 搜索角色
	 */
	@ApiOperation(value = "搜索角色列表", notes = "搜索角色列表", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "keyword", value = "角色名称关键词", dataType = "String"),
			@ApiImplicitParam(name = "pageNum", value = "当前页(默认:1)", dataType = "int"),
			@ApiImplicitParam(name = "pageSize", value = "每页数(默认:10)", dataType = "int"),
			@ApiImplicitParam(name = "page", value = "是否分页", dataType = "boolean", defaultValue = "true")
	})
	@GetMapping("/searchByRole")
	public ResultVO searchByRole(@RequestParam(value = "keyword", required = false) String keyword, @RequestParam(value = "page", required = false, defaultValue = "true") boolean isPage) {
		UserInfo userInfo = getUserInfo();
		List<RoleEntity> roles = service.getByKeywordAndOrg(StringUtils.isBlank(keyword) ? null : keyword.trim(), userInfo.getOrgId());
		return ResultVO.Page(roles, isPage);
	}

	/**
	 * 查询公司的角色列表
	 */
	@RequestMapping("/getByOrg")
	public ResultVO getByOrg() {
		UserToken token = getToken();
		boolean page = setPage();
		List<RoleVo> orgs = service.getVoByOrg(token.getOrgId());
		if (orgs != null && orgs.size() > 0) {
			for (RoleVo roleVo : orgs) {
				String code = roleVo.getCode();
				if (StringUtils.isNotBlank(code) && Global.codes.contains(code)) {
					roleVo.setWriter(false);
				}
			}
		}
		return ResultVO.Page(orgs, page);
	}

	/**
	 * 查询用户拥有的角色列表(有权限的属性为true,没有的为false)
	 */
	@RequestMapping("/getByUser")
	public ResultVO getByUser(@RequestParam("userId") Integer userId) {
		User user = userService.get(userId);
		if (user != null) {
			List<Integer> checkedIds = service.getByUser(userId);
			List<Role> all = service.getByOrg(user.getOrgId());
			Map<String, Object> res = new HashMap<>(2);
			res.put("roles", all);
			res.put("checkedIds", checkedIds);
			return ResultVO.OK(res);
		}
		return ResultVO.FAIL("该用户不存在");
	}

	@ApiOperation(value = "更新角色", notes = "更新角色", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "角色ID", dataType = "int", required = true),
			@ApiImplicitParam(name = "name", value = "角色名称", dataType = "String", required = true),
			@ApiImplicitParam(name = "remark", value = "角色描述", dataType = "String")
	})
	@WebLogger("更新角色")
	@PostMapping("/update")
	public ResultVO update(@RequestParam("id") Integer roleId, @RequestParam("name") String name, @RequestParam(value = "remark", required = false) String remark) {
		/*
		 * 校验ID
		 */
		Role role = service.get(roleId);
		if (role == null) {
			return ResultVO.FAIL("角色不存在");
		}
		LocalHandle.setOldObj(role);
		/*
		 * 校验名称
		 */
		if (StringUtils.isBlank(name)) {
			return ResultVO.FAIL("角色名称不能为空");
		}
		Role roleByName = service.getByNameAndOrg(name, role.getOrgId());
		if (roleByName != null && roleByName.getId().intValue() != roleId.intValue()) {
			return ResultVO.FAIL("角色名称重复");
		}

		/*
		 * 校验权限
		 */
		UserInfo userInfo = getUserInfo();
		if (userInfo.getOrgId().intValue() != role.getOrgId().intValue()) {
			return ResultVO.FAIL("无权限");
		}

		role.setName(name);
		role.setRemark((StringUtils.isBlank(remark) || "undefined".equalsIgnoreCase(remark) || "null".equalsIgnoreCase(remark)) ? null : remark);

		role.setUpdateId(userInfo.getId());
		service.update(role);
		LocalHandle.setNewObj(role);
		LocalHandle.complete("更新角色");
		return ResultVO.OK("更新成功");
	}

	@ApiOperation(value = "删除角色", notes = "删除角色", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "角色ID", dataType = "int", required = true)
	})
	@WebLogger("删除角色")
	@PostMapping("/del")
	public ResultVO del(@RequestParam("id") Integer roleId) {
		/*
		 * 校验ID
		 */
		Role role = service.get(roleId);
		if (role == null) {
			return ResultVO.FAIL("角色不存在");
		}

		/*
		 * 校验权限
		 */
		UserInfo userInfo = getUserInfo();
		if (userInfo.getOrgId().intValue() != role.getOrgId().intValue()) {
			return ResultVO.FAIL("无权限操作");
		}

		/*
		 * 校验角色有效性
		 */
		String code = role.getCode();
		if (StringUtils.isNotBlank(code) && Global.codes.contains(code)) {
			return ResultVO.FAIL("系统角色无法删除");
		}

		service.delRole(role);
		LocalHandle.setOldObj(role);
		LocalHandle.complete("删除角色");
		return ResultVO.OK("删除成功");
	}

	@ApiOperation(value = "添加角色", notes = "添加角色", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "name", value = "角色名称", dataType = "String", required = true),
			@ApiImplicitParam(name = "remark", value = "角色描述", dataType = "String")
	})
	@WebLogger("添加角色")
	@PostMapping("/add")
	public ResultVO add(@RequestParam("name") String roleName, @RequestParam(value = "remark", required = false) String remark) {
		if (StringUtils.isBlank(roleName)) {
			return ResultVO.FAIL("角色名称不能为空");
		}

		UserInfo userInfo = getUserInfo();
		Role role = service.getByNameAndOrg(roleName, userInfo.getOrgId());
		if (role != null) {
			return ResultVO.FAIL("角色已存在");
		}

		role = new Role();
		role.setOrgId(userInfo.getOrgId());
		role.setCode(UUID.randomUUID().toString().replace("-", ""));
		role.setCreateId(userInfo.getId());
		role.setName(roleName);
		role.setRemark((StringUtils.isBlank(remark) || "undefined".equalsIgnoreCase(remark) || "null".equalsIgnoreCase(remark)) ? null : remark);
		service.add(role);
		LocalHandle.setNewObj(role);
		LocalHandle.complete("新增角色");

		return ResultVO.OK("添加成功");
	}

}
