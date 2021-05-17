package com.yunxi.stamper.controller;


import com.github.pagehelper.PageInfo;
import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.response.Code;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.Perms;
import com.yunxi.stamper.entityVo.PermsVo;
import com.yunxi.stamper.entityVo.Route;
import com.yunxi.stamper.entityVo.UserInfo;
import com.yunxi.stamper.service.PermService;
import com.yunxi.stamper.sys.aop.annotaion.WebLogger;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/4/26 0026 14:27
 */
@Slf4j
@Api(tags = "权限相关")
@RestController
@RequestMapping(value = "/auth/perms", method = {RequestMethod.POST, RequestMethod.GET})
public class PermsController extends BaseController {

	@Autowired
	private PermService service;
	@Autowired
	private PermService permService;

	/**
	 * 查询用户动态路由(当前项目仅有2层路由)
	 */
	@RequestMapping("/getRoute")
	public ResultVO getRoute() {
		UserInfo userInfo = getUserInfo();

		List<Route> routes;
		if (userInfo.isOwner()) {
			routes = permService.getRouteByAdmin(userInfo.getOrgId());
		} else {
			routes = permService.getRouteByUser(userInfo.getId());
		}

		return ResultVO.OK(routes);
	}

	/**
	 * 查询指定权限详情
	 */
	@RequestMapping("/get")
	public ResultVO get(@RequestParam("id") Integer id) {
		if (id != null) {
			Perms perms = service.get(id);
			return ResultVO.OK(perms);
		}
		return ResultVO.OK();
	}

	/**
	 * 查询权限树列表
	 */
	@RequestMapping("/getTree")
	public ResultVO getTree() {
		List<PermsVo> res = service.getTree();
		return ResultVO.OK(res);
	}

	/**
	 * 查询所有权限
	 */
	@RequestMapping("/getAll")
	public ResultVO getAll() {
		List<Perms> perms = service.getAll();
		return ResultVO.OK(perms);
	}

	/**
	 * 多条件查询权限列表
	 */
	@RequestMapping("/getList")
	public ResultVO getList(Integer parentId, String label, String url, Integer level, String code) {
		Perms perms = new Perms();
		perms.setParentId(parentId);
		perms.setLabel(label);
		perms.setUrl(url);
		perms.setLevel(level);

		setPage();
		List<Perms> permsList = service.selectByParams(perms);
		PageInfo<Perms> pageInfo = new PageInfo<>(permsList);
		return ResultVO.OK(pageInfo);
	}

	/**
	 * 修改权限
	 */
	@WebLogger("更新权限信息")
	@RequestMapping("/update")
	@Transactional
	public ResultVO update(Integer id,
						   Integer parentId, String code, Integer isShortcut,
						   @RequestParam String label,
						   @RequestParam String url,
						   @RequestParam Integer level,
						   @RequestParam Integer type,
						   @RequestParam String icon,
						   @RequestParam Integer orderNo) {
		//仅平台账户可用
		UserInfo userInfo = getUserInfo();
		if (userInfo.getType() == null || userInfo.getType() != 0) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		Perms perms = new Perms();
		perms.setCode(code);
		perms.setParentId(parentId);
		perms.setIsShortcut(isShortcut);
		perms.setLabel(label);
		perms.setLevel(level);
		perms.setType(type);
		perms.setUrl(url);
		perms.setIcon(icon);
		perms.setOrderNo(orderNo);

		//权限必须存在
		Perms update = service.get(id);
		if (update == null) {
			return ResultVO.FAIL("该权限不存在");
		}
		//权限名称不能为空
		if (StringUtils.isBlank(label)) {
			return ResultVO.FAIL("权限名称不能为空");
		}
		//code存在的情况下不能重复
		if (StringUtils.isNotBlank(code)) {
			Perms byCode = service.getByCode(code);
			if (byCode != null && byCode.getId().intValue() != id.intValue()) {
				return ResultVO.FAIL("权限编码已存在");
			}
		}
		//type类型值不能为空
		if (type == null) {
			return ResultVO.FAIL("权限类型不能为空");
		}
		//权限级别不能为空
		if (level == null) {
			return ResultVO.FAIL("权限级别不能为空");
		}
		//父节点存在的情况下必须存在
		if (parentId != null) {
			Perms parent = service.get(parentId);
			if (parent == null) {
				return ResultVO.FAIL("父权限不存在");
			}
		}
		//该权限是否用作快捷入口
		if (perms.getIsShortcut() == null || perms.getIsShortcut() == 0) {
			update.setIsShortcut(0);
		} else {
			update.setIsShortcut(1);
		}

		update.setType(type);
		update.setLabel(perms.getLabel());
		update.setLevel(perms.getLevel());
		update.setParentId(perms.getParentId());
		update.setUrl(perms.getUrl());
		update.setRemark(perms.getRemark());
		update.setCode(perms.getCode());
		update.setIcon(perms.getIcon());
		update.setOrderNo(perms.getOrderNo());
		service.update(update);

		//查询该拥有该权限的用户列表
		List<Integer> userIds = service.getUserIDByPerms(update.getId());
		if (userIds != null && userIds.size() > 0) {
			for (Integer userId : userIds) {
				userInfoService.del(userId);
			}
		}

		return ResultVO.OK("修改成功");
	}

	/**
	 * 删除权限
	 * ps:子级权限在不指定的情况下默认随父级权限
	 */
	@WebLogger("删除权限")
	@RequestMapping("/del")
	public ResultVO del(@RequestParam("permsId") Integer permsId) {
		//仅平台账户可用
		UserInfo userInfo = getUserInfo();
		if (userInfo.getType() == null || userInfo.getType() != 0) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		if (permsId != null) {
			//查询是否存在该节点
			Perms perms = service.get(permsId);
			if (perms != null) {
				service.del(permsId);

				//将所有该节点的子节点parentId至为null
				service.setNullByParent(permsId);

				return ResultVO.OK();
			} else {
				return ResultVO.FAIL("该权限不存在");
			}
		}
		return ResultVO.FAIL(Code.FAIL402);
	}

	/**
	 * 添加权限
	 */
	@WebLogger("添加权限")
	@RequestMapping("/add")
	public ResultVO add(Integer parentId, String code, Integer isShortcut,
						@RequestParam String label,
						@RequestParam Integer level,
						@RequestParam String url,
						@RequestParam Integer type,
						@RequestParam String icon,
						@RequestParam Integer orderNo) {
		//仅平台账户可用
		UserInfo userInfo = getUserInfo();
		if (userInfo.getType() == null || userInfo.getType() != 0) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		//父节点存在的情况下,必须存在
		if (parentId != null) {
			Perms parent = service.get(parentId);
			if (parent == null) {
				return ResultVO.FAIL("父节点不存在");
			}
		}

		//code存在的情况下不能重复
		if (StringUtils.isNotBlank(code)) {
			Perms permsByCode = service.getByCode(code);
			if (permsByCode != null) {
				return ResultVO.FAIL("该权限编码已存在");
			}
		}

		Perms perms = new Perms();
		perms.setCode(code);
		perms.setParentId(parentId);
		perms.setIsShortcut(isShortcut);
		perms.setLabel(label);
		perms.setLevel(level);
		perms.setType(type);
		perms.setUrl(url);
		perms.setIcon(icon);
		perms.setOrderNo(orderNo);
		service.add(perms);

		return ResultVO.OK("添加成功");
	}
}
