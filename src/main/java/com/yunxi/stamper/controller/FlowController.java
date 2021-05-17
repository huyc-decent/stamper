package com.yunxi.stamper.controller;

import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.other.EmojiFilter;
import com.yunxi.stamper.commons.response.Code;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.Department;
import com.yunxi.stamper.entity.Flow;
import com.yunxi.stamper.entity.Signet;
import com.yunxi.stamper.entityVo.*;
import com.yunxi.stamper.logger.threadLocal.LocalHandle;
import com.yunxi.stamper.service.DepartmentService;
import com.yunxi.stamper.service.FlowService;
import com.yunxi.stamper.service.SignetService;
import com.yunxi.stamper.sys.aop.annotaion.WebLogger;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/6/5 0005 11:04
 */
@Api(tags = "审批流程相关")
@RestController
@RequestMapping(value = "/application/flow", method = {RequestMethod.POST, RequestMethod.GET})
public class FlowController extends BaseController {

	@Autowired
	private FlowService service;
	@Autowired
	private SignetService signetService;
	@Autowired
	private DepartmentService departmentService;

	@ApiOperation(value = "查询审批流程详情", notes = "查询审批流程详情", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "flowId", value = "流程ID", dataType = "int")
	})
	@GetMapping("/getVo")
	public ResultVO getVo(@RequestParam("flowId") Integer flowId) {
		/*
		 * 参数校验
		 */
		UserInfo userInfo = getUserInfo();
		Flow flow = service.get(flowId);
		if (flow == null || flow.getOrgId().intValue() != userInfo.getOrgId()) {
			return ResultVO.FAIL("无权限");
		}

		FlowVoAdd res = service.getFlowInfo(flowId);

		return ResultVO.OK(res);
	}

	/*
	 * 删除审批流程
	 */
	@WebLogger("删除审批流程")
	@RequestMapping("/del")
	public ResultVO del(@RequestParam("flowId") Integer flowId) {
		if (flowId != null) {
			Flow flow = service.get(flowId);
			if (flow == null) {
				return ResultVO.FAIL("该审批流程不存在");
			}

			service.del(flow);
			LocalHandle.setOldObj(flow);
			LocalHandle.complete("删除审批流程");
			return ResultVO.OK("删除成功");
		}
		return ResultVO.FAIL(Code.FAIL402);
	}

	@WebLogger("修改审批流程")
	@RequestMapping("/update")
	public ResultVO update(Integer id, String name, String remark, Integer orgId, Integer userId, String userName, Integer status) {
		Flow flow = new Flow();
		flow.setId(id);
		flow.setName(name);
		flow.setRemark(remark);
		flow.setOrgId(orgId);
		flow.setUserId(userId);
		flow.setUserName(userName);
		flow.setStatus(status);

		Integer flowId = flow.getId();
		Flow update = service.get(flowId);
		if (update == null) {
			return ResultVO.FAIL("该审批流程不存在");
		}

		LocalHandle.setOldObj(update);

		if (StringUtils.isBlank(name)) {
			return ResultVO.FAIL("名称不能为空");
		}

		UserInfo userInfo = getUserInfo();
		Flow byName = service.getByOrgAndName(userInfo.getOrgId(), name);
		if (byName != null && byName.getId().intValue() != flowId) {
			return ResultVO.FAIL("该审批流程名称已存在");
		}

		update.setRemark(flow.getRemark());
		update.setName(name.trim());
		update.setStatus(flow.getStatus());
		service.update(update);

		LocalHandle.setNewObj(update);
		LocalHandle.complete("更新审批流程");
		return ResultVO.OK("更新成功");
	}

	@WebLogger("修改审批流程图")
	@PostMapping("/updateNodes")
	@Transactional
	public ResultVO updateNodes(List<FlowVoAddEntity> flowNodeList, Integer status, String name, String remark, Integer flowId, List<Integer> departmentIds) {
		//参数校验
		Flow flow = service.get(flowId);
		UserInfo userInfo = getUserInfo();
		if (flow == null || flow.getOrgId().intValue() != userInfo.getOrgId()) {
			return ResultVO.FAIL("该审批流程不存在");
		}

		//参数校验：审批流程所属组织ID
		if (departmentIds == null || departmentIds.isEmpty()) {
			return ResultVO.FAIL("审批流程归属组织不能为空");
		}

		//参数校验：审批流程名称
		if (StringUtils.isBlank(name)) {
			return ResultVO.FAIL("审批流程名称不能为空");
		}
		if (EmojiFilter.containsEmoji(name)) {
			return ResultVO.FAIL("审批流程名称不能包含特殊字符");
		}
		List<Flow> flows = service.getByOrgAndDepartment(userInfo.getOrgId(), departmentIds);
		if (flows != null && flows.size() > 0) {
			for (Flow f : flows) {
				if (CommonUtils.isEquals(f.getId(), flowId)) {
					continue;
				}
				if (name.equalsIgnoreCase(f.getName())) {
					return ResultVO.FAIL("审批流程名称重复");
				}
			}
		}

		//参数校验：审批流程简介
		if (EmojiFilter.containsEmoji(remark)) {
			return ResultVO.FAIL("审批流程简介不能包含特殊字符");
		}
		//参数校验：审批流程启用、禁用状态
		if (status == null) {
			return ResultVO.FAIL("审批流程状态有误");
		}
		if (status != 0 && status != 1) {
			return ResultVO.FAIL("审批流程状态有误");
		}

		//参数校验：审批流程节点
		if (flowNodeList == null || flowNodeList.isEmpty()) {
			return ResultVO.FAIL("审批步骤不能为空");
		}


		service.updateFlow(userInfo, flow, name, remark, status, flowNodeList, departmentIds);

		return ResultVO.OK("更新成功");
	}

	@ApiOperation(value = "查询审批流程，以通讯录格式展示", notes = "查询审批流程，以通讯录格式展示", httpMethod = "GET")
	@ApiImplicitParam(name = "deviceId", value = "印章ID", dataType = "int")
	@GetMapping("/getByOwnerToAddress")
	public ResultVO getByOwnerToAddress(@RequestParam(value = "deviceId") Integer deviceId) {

		/*
		 * 参数校验
		 */
		UserInfo userInfo = getUserInfo();
		if (deviceId == null) {
			return ResultVO.FAIL("请先选择需要使用的设备");
		}
		Signet signet = signetService.get(deviceId);
		if (signet == null || signet.getOrgId().intValue() != userInfo.getOrgId()) {
			return ResultVO.FAIL("选择的设备不存在");
		}
		Integer departmentId = signet.getDepartmentId();
		Integer orgId = userInfo.getOrgId();
		List<FlowEntity> flows = service.getByOrgAndDepartment(orgId, departmentId);
		List<Map<String, Object>> addressList = CommonUtils.getAddressList(flows, "name");
		return ResultVO.OK(addressList);
	}

	@ApiOperation(value = "查询用户集团的审批流程列表", notes = "查询用户集团的审批流程列表", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageNum", value = "当前页(默认:1)", dataType = "int"),
			@ApiImplicitParam(name = "pageSize", value = "每页数(默认:10)", dataType = "int"),
			@ApiImplicitParam(name = "page", value = "是否分页", dataType = "boolean", defaultValue = "true"),
			@ApiImplicitParam(name = "deviceId", value = "印章ID", dataType = "int")
	})
	@GetMapping("/getByOwner")
	public ResultVO getByOwner(@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
							   @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
							   @RequestParam(value = "page", required = false, defaultValue = "true") boolean isPage,
							   @RequestParam(value = "deviceId") Integer deviceId) {
		/*
		 * 参数校验
		 */
		UserInfo userInfo = getUserInfo();
		if (deviceId == null) {
			return ResultVO.FAIL("请先选择需要使用的设备");
		}
		Signet signet = signetService.get(deviceId);
		if (signet == null || signet.getOrgId().intValue() != userInfo.getOrgId()) {
			return ResultVO.FAIL("选择的设备不存在");
		}
		Integer departmentId = signet.getDepartmentId();


		Integer orgId = userInfo.getOrgId();

		List<FlowEntity> flows = service.getByOrgAndDepartment(orgId, departmentId);
		return ResultVO.Page(flows, isPage);
	}

	@ApiOperation(value = "添加审批流程", notes = "添加审批流程", httpMethod = "POST")
	@ApiImplicitParam(name = "flow", value = "flow", dataType = "int")
	@WebLogger("添加审批流程")
	@PostMapping("/add")
	public ResultVO add(List<Integer> departmentIds, String name, String remark, Integer status, List<FlowVoAddEntity> flowNodeList) {
		UserInfo userInfo = getUserInfo();
		FlowVoAdd flow = new FlowVoAdd();
		flow.setName(name);
		flow.setDepartmentIds(departmentIds);
		flow.setRemark(remark);
		flow.setStatus(status);
		flow.setFlowNodeList(flowNodeList);

		// 参数校验：审批流程所属组织ID
		if (departmentIds == null || departmentIds.isEmpty()) {
			return ResultVO.FAIL("审批流程归属组织不能为空");
		}
		for (Integer departmentId : departmentIds) {
			Department department = departmentService.get(departmentId);
			if (department == null || !CommonUtils.isEquals(department.getOrgId(), userInfo.getOrgId())) {
				return ResultVO.FAIL("组织选择有误");
			}
		}

		// 参数校验：审批流程名称
		if (StringUtils.isBlank(name)) {
			return ResultVO.FAIL("审批流程名称不能为空");
		}
		if (EmojiFilter.containsEmoji(name)) {
			return ResultVO.FAIL("审批流程名称不能包含特殊字符");
		}
		int count = service.getByDepartmentAndName(departmentIds, name);
		if (count > 0) {
			return ResultVO.FAIL("审批流程名称重复");
		}

		// 参数校验：审批流程简介
		if (EmojiFilter.containsEmoji(remark)) {
			return ResultVO.FAIL("审批流程简介不能包含特殊字符");
		}

		// 参数校验：审批流程启用、禁用状态
		if (status == null || (status != 0 && status != 1)) {
			return ResultVO.FAIL("审批流程状态有误");
		}

		// 参数校验：审批流程节点
		if (flowNodeList == null || flowNodeList.isEmpty()) {
			return ResultVO.FAIL("审批流程节点不能为空");
		}


		service.addFlow(userInfo, flow);

		return ResultVO.OK("添加成功");
	}

	@ApiOperation(value = "查询所有审批流程列表", notes = "查询所有审批流程列表", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageNum", value = "当前页", dataType = "int"),
			@ApiImplicitParam(name = "pageSize", value = "每页数", dataType = "int"),
			@ApiImplicitParam(name = "page", value = "是否分页", dataType = "boolean", defaultValue = "true"),
			@ApiImplicitParam(name = "departmentId", value = "组织ID", dataType = "int"),
			@ApiImplicitParam(name = "keyword", value = "组织ID", dataType = "int")
	})
	@GetMapping("/getAll")
	public ResultVO getAll(@RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
						   @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
						   @RequestParam(value = "page", required = false, defaultValue = "true") boolean isPage,
						   @RequestParam(value = "departmentId", required = false) Integer departmentId,
						   @RequestParam(value = "keyword", required = false) String keyword) {

		/*
		 * 参数校验：组织ID
		 */
		UserInfo userInfo = getUserInfo();

		/*
		 * 设置搜索组织列表
		 */
		List<Integer> searchDepartmentIds = null;
		if (departmentId != null) {
			searchDepartmentIds = departmentService.getChildrenIdsByOrgAndParentAndType(userInfo.getOrgId(), departmentId, null);
			searchDepartmentIds.add(departmentId);
		}

		List<FlowVoSelect> flows = service.getFlowListByOrgAndDepartmentAndKeyword(userInfo.getOrgId(), searchDepartmentIds, keyword);

		return ResultVO.Page(flows, isPage);
	}
}
