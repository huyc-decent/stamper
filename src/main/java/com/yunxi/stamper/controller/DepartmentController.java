package com.yunxi.stamper.controller;

import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.other.EmojiFilter;
import com.yunxi.stamper.commons.jwt.UserToken;
import com.yunxi.stamper.commons.response.Code;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.Department;
import com.yunxi.stamper.entity.Org;
import com.yunxi.stamper.entity.Position;
import com.yunxi.stamper.entity.User;
import com.yunxi.stamper.entityVo.*;
import com.yunxi.stamper.logger.threadLocal.LocalHandle;
import com.yunxi.stamper.service.*;
import com.yunxi.stamper.sys.aop.annotaion.WebLogger;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/8 0008 18:11
 */
@Slf4j
@Api(tags = "组织相关")
@RestController
@RequestMapping(value = "/auth/department", method = {RequestMethod.POST, RequestMethod.GET})
public class DepartmentController extends BaseController {

	@Autowired
	private DepartmentService service;
	@Autowired
	private PositionService positionService;
	@Autowired
	private OrgService orgService;
	@Autowired
	private UserService userService;
	@Autowired
	private UserInfoService userInfoService;


	@ApiOperation(value = "查询组织架构(级联)用于添加审批流程", notes = "查询组织架构(级联)用于添加审批流程", httpMethod = "GET")
	@GetMapping("/getLevelsByOrgForAddAndUpdateFlow")
	public ResultVO getLevelsByOrgForAddAndUpdateFlow() {
		UserInfo userInfo = getUserInfo();
		/*查询整个集团的组织架构树*/
		OrganizationalTree orgTree = userInfoService.generatorTreeByOrg(userInfo.getOrgId());
		return ResultVO.OK(orgTree);
	}

//	@ApiOperation(value = "查询组织架构(级联形式)用于添加(更新)员工信息", notes = "查询组织架构(级联形式)用于添加(更新)员工信息", httpMethod = "GET")
//	@GetMapping("/getLevelsByOrgForAddAndUpdateEmployee")
//	public ResultVO getLevelsByOrgForAddAndUpdateEmployee() {
//		UserInfo userInfo = getUserInfo();
//		Integer orgId = userInfo.getOrgId();
//		OrganizationalTree root = userInfoService.generatorTreeByOrg(orgId);
//		return ResultVO.OK(root);
//	}

	@ApiOperation(value = "查询用户所属公司树结构", notes = "查询用户所属公司树结构", httpMethod = "GET")
	@GetMapping("/getLevelsByOrgForAddAndUpdateEmployee")
	public ResultVO getLevelsByOrgForAddAndUpdateEmployee() {
		UserInfo userInfo = getUserInfo();
		return ResultVO.OK(userInfo.getTree());
	}

	@ApiOperation(value = "查询组织架构(级联形式)用于更新印章信息", notes = "查询组织架构(级联形式)用于更新印章信息", httpMethod = "GET")
	@GetMapping("/getLevelsByOrgToUpdateSignet")
	public ResultVO getLevelsByOrgToUpdateSignet() {
		UserInfo userInfo = getUserInfo();
		return ResultVO.OK(userInfo.getTree());
	}

	@ApiOperation(value = "查询组织架构(级联形式)", notes = "查询组织架构(级联形式)", httpMethod = "GET")
	@GetMapping("/getLevelsByOrg")
	public ResultVO getLevelsByOrg() {
		UserInfo userInfo = getUserInfo();
		return ResultVO.OK(userInfo.getTree());
	}

	/**
	 * 查询指定公司下1级部门
	 */
	@RequestMapping("/getChildrensByOrg")
	public ResultVO getChildrensByOrg(@RequestParam("orgId") Integer orgId) {
		if (orgId == null) {
			return ResultVO.OK();
		}
		UserToken token = getToken();
		if (token.getOrgId() != orgId.intValue()) {
			return ResultVO.FAIL(Code.FAIL403);
		}
		List<Department> departments = service.getByOrgId(orgId);
		return ResultVO.OK(departments);
	}


	/**
	 * 查询该公司下所有部门列表
	 */
	@RequestMapping("/getListByOrg")
	public ResultVO getListByOrg() {
		UserToken token = getToken();
		boolean page = setPage();
		List<Department> parents = service.getByOrg(token.getOrgId());
		return ResultVO.Page(parents, page);
	}

	/**
	 * 查询公司下所有部门树
	 */
	@RequestMapping("/getTree")
	public ResultVO getTree(@RequestParam("orgId") Integer orgId) {
		if (orgId == null) {
			return ResultVO.OK();
		}

		UserToken token = getToken();
		//仅能获取本公司组织架构
		if (token.getOrgId() != orgId.intValue()) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		List<DepartmentVo> departMentsByOrg = service.getTreeByOrg(orgId);
		return ResultVO.OK(departMentsByOrg);
	}

	@ApiOperation(value = "添加企业、团队、组织", notes = "添加企业、团队、组织", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "name", value = "组织名称", dataType = "String", required = true),
			@ApiImplicitParam(name = "remark", value = "组织描述", dataType = "String"),
			@ApiImplicitParam(name = "managerUserId", value = "组织负责人ID", dataType = "int"),
			@ApiImplicitParam(name = "positionId", value = "称谓ID", dataType = "int"),
			@ApiImplicitParam(name = "parentId", value = "父组织ID", dataType = "int"),
			@ApiImplicitParam(name = "parentType", value = "父组织类型", dataType = "int"),
			@ApiImplicitParam(name = "type", value = "组织类型 0:部门 1:机构", dataType = "int", defaultValue = "0")
	})
	@WebLogger("添加组织")
	@PostMapping("/addDepartment")
	public ResultVO addDepartment(@RequestParam("name") String name,
								  @RequestParam(value = "remark", required = false) String remark,
								  @RequestParam(value = "managerUserId", required = false) Integer managerUserId,
								  @RequestParam(value = "positionId", required = false) Integer positionId,
								  @RequestParam(value = "parentId") Integer parentId,
								  @RequestParam(value = "type", required = false, defaultValue = "0") Integer type) {
		UserInfo userInfo = getUserInfo();

		/*
		 * 父组织不能为空
		 */
		if (parentId == null) {
			return ResultVO.FAIL("父组织不能为空");
		}
		Department parent = service.get(parentId);
		if (parent == null) {
			return ResultVO.FAIL("父组织不存在");
		}
		if (parent.getOrgId().intValue() != userInfo.getOrgId()) {
			return ResultVO.FAIL("您无权限设置该父组织");
		}
		List<Integer> departmentIds = userInfoService.getManagerDepartmentIds(userInfo);
		if (departmentIds == null || departmentIds.isEmpty() || !departmentIds.contains(parentId)) {
			return ResultVO.FAIL("父组织设置有误");
		}

		/*
		 * 检查权限,集团属主、公司负责人、部门负责人有权限
		 */
		List<Integer> managerDepartmentIds = null;
		if (!userInfo.isOwner()) {
			managerDepartmentIds = service.getIdsByOrgAndManager(userInfo.getOrgId(), userInfo.getId());
			if (managerDepartmentIds == null || managerDepartmentIds.isEmpty()) {
				return ResultVO.FAIL("您无权限添加组织信息");
			}
		}

		/*
		 * 校验负责人
		 */
		User managerUser = null;
		if (managerUserId != null) {
			if (managerUserId.intValue() == userInfo.getId()) {
				managerUser = userInfo;
			} else {
				managerUser = userService.get(managerUserId);
				if (managerUser == null || managerUser.getOrgId().intValue() != userInfo.getOrgId()) {
					return ResultVO.FAIL("该负责人不存在");
				}
				if (!userInfo.isOwner() && managerUser.getId().intValue() != userInfo.getId()) {
					//查询负责的组织列表下所有员工ID列表
					List<Integer> employeeIds = userService.getIdsByDepartmentIds(managerDepartmentIds);
					if (employeeIds == null || employeeIds.isEmpty() || !employeeIds.contains(managerUserId)) {
						return ResultVO.FAIL("您无权限设置该负责人");
					}
				}
			}
		}

		/*
		 * 校验组织名称
		 */
		if (StringUtils.isBlank(name.trim())) {
			return ResultVO.FAIL("组织名称不能为空");
		}
		name = name.trim();
		Department department = service.getByOrgIdAndParentIdAndName(userInfo.getOrgId(), parentId, name);
		if (department != null) {
			return ResultVO.FAIL("该组织名称已存在");
		}

		/*
		 * 校验职称
		 */
		Position position = null;
		if (positionId != null) {
			position = positionService.get(positionId);
			if (position == null) {
				return ResultVO.FAIL("职称不存在");
			}
		}

		//添加组织
		service.addDepartment(userInfo.getOrgId(), name, remark, managerUser, parent, position, type);

		//刷新操作人缓存信息
		userInfoService.del(userInfo.getId());

		return ResultVO.OK("添加成功");
	}

	/**
	 * 查询部门信息
	 */
	@RequestMapping("/get")
	public ResultVO get(@RequestParam("id") Integer departmentId) {
		Department department = service.get(departmentId);
		if (department == null) {
			return ResultVO.FAIL("组织不存在");
		}

		//只能查看本公司所属部门信息
		UserToken token = getToken();
		if (department.getOrgId().intValue() != token.getOrgId()) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		DepartmentVue vue = new DepartmentVue();
		BeanUtils.copyProperties(department, vue);
		//上级部门
		Integer parentId = department.getParentId();
		Department parent = service.get(parentId);
		if (parent != null) {
			vue.setParentName(parent.getName());
		}

		//公司名称
		Integer orgId = department.getOrgId();
		Org org = orgService.get(orgId);
		if (org != null) {
			vue.setOrgName(org.getName());
		}

		//管理员名称
		User user = userService.get(department.getManagerUserId());
		vue.setManagerUserName(user.getUserName());
		return ResultVO.OK(vue);

	}

	@ApiOperation(value = "修改企业、团队、组织", notes = "修改企业、团队、组织", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "组织ID", dataType = "int", required = true),
			@ApiImplicitParam(name = "name", value = "组织名称", dataType = "String", required = true),
			@ApiImplicitParam(name = "remark", value = "组织描述", dataType = "String"),
			@ApiImplicitParam(name = "managerUserId", value = "组织负责人ID", dataType = "int"),
			@ApiImplicitParam(name = "positionId", value = "称谓ID", dataType = "int"),
			@ApiImplicitParam(name = "parentId", value = "父组织ID", dataType = "int", required = true),
			@ApiImplicitParam(name = "type", value = "组织类型 0:部门 1:机构", dataType = "int", defaultValue = "0")
	})
	@WebLogger("修改组织信息")
	@PostMapping("/updateDepartment")
	public ResultVO updateDepartment(@RequestParam("id") Integer departmentId,
									 @RequestParam("name") String name,
									 @RequestParam(value = "remark", required = false) String remark,
									 @RequestParam(value = "managerUserId", required = false) Integer managerUserId,
									 @RequestParam(value = "positionId", required = false) Integer positionId,
									 @RequestParam(value = "parentId", required = false) Integer parentId,
									 @RequestParam(value = "type", required = false, defaultValue = "0") Integer type) {
		UserInfo userInfo = getUserInfo();

		Department updateDepartment = service.get(departmentId);
		if (updateDepartment == null) {
			return ResultVO.FAIL("组织不存在");
		}
		LocalHandle.setOldObj(updateDepartment);

		List<Integer> departmentIds = userInfo.getDepartmentIds();

		Integer updateType = updateDepartment.getType();

		/*
		 * 校验负责人称谓
		 */
		if (positionId != null) {
			Position position = positionService.get(positionId);
			if (position == null || position.getOrgId().intValue() != userInfo.getOrgId()) {
				return ResultVO.FAIL("该负责人称谓不存在");
			}
		}

		Department parent = null;

		/*
		 * 修改集团信息
		 */
		if (updateType != null && updateType == 2) {
			if (!userInfo.isOwner()) {
				return ResultVO.FAIL("无权限修改该组织信息");
			}

			/*
			 * 集团节点名称不能存在
			 */
			if (StringUtils.isBlank(name)) {
				return ResultVO.FAIL("组织名称不能为空");
			}
			if (!name.equals(updateDepartment.getName())) {
				List<Department> departments = service.getByTypeAndName(2, name);
				if (departments != null && departments.size() > 0) {
					return ResultVO.FAIL("组织名称已存在");
				}
			}

			/*
			 * 组织描述不能包含特殊字符
			 */
			if (EmojiFilter.containsEmoji(remark)) {
				return ResultVO.FAIL("描述信息不能包含特殊字符");
			}

			/*
			 * 组织负责人不能为空
			 */
			if (managerUserId == null) {
				return ResultVO.FAIL("该组织负责人不能为空");
			}
			if (managerUserId.intValue() != updateDepartment.getManagerUserId()) {
				User managerUser = userService.get(managerUserId);
				if (managerUser == null || managerUser.getOrgId().intValue() != userInfo.getOrgId()) {
					return ResultVO.FAIL("该组织负责人不存在");
				}
			}
		} else {

			/*
			 * 父组织不能为空
			 */
			if (parentId == null) {
				return ResultVO.FAIL("父组织不能为空");
			}
			parent = service.get(parentId);
			if (parent == null) {
				return ResultVO.FAIL("父组织不能为空");
			}
			if (departmentIds == null || departmentIds.isEmpty() || !departmentIds.contains(parentId)) {
				return ResultVO.FAIL("父组织设置有误");
			}

			/*
			 * 校验组织名称
			 */
			if (StringUtils.isBlank(name)) {
				return ResultVO.FAIL("组织名称不能为空");
			}
			if (!name.equals(updateDepartment.getName())) {
				Department department = service.getByOrgIdAndParentIdAndName(userInfo.getOrgId(), parentId, name);
				if (department != null) {
					return ResultVO.FAIL("该组织名称已存在");
				}
			}

			/*
			 * 检查权限,组织负责人仅能编辑子组织信息,无法编辑负责的组织信息
			 */
			List<Integer> childrenIds = new ArrayList<>();
			if (!userInfo.isOwner()) {
				if (departmentIds.isEmpty()) {
					return ResultVO.FAIL("您无权限编辑组织信息");
				}
				childrenIds = service.getChildrenIdsByOrgAndParentsAndType(userInfo.getOrgId(), departmentIds, null);
				if (childrenIds == null || childrenIds.isEmpty() || !childrenIds.contains(departmentId)) {
					return ResultVO.FAIL("您无权限编辑组织信息");
				}
			}

			/*
			 * 校验负责人
			 */
			if (managerUserId != null && !userInfo.isOwner() && userInfo.getId().intValue() != managerUserId) {
				childrenIds.add(updateDepartment.getId());
				List<Integer> employeeIds = userService.getUserIdsByDepartment(childrenIds);
				if (employeeIds == null || employeeIds.isEmpty() || !employeeIds.contains(managerUserId)) {
					return ResultVO.FAIL("您无权限设置该负责人");
				}
			}
		}

		service.updateDepartment(updateDepartment, name, remark, positionId, managerUserId, parent, type);


		//刷新操作人信息
		userInfoService.del(userInfo.getId());

		return ResultVO.OK("修改成功");
	}

	@ApiOperation(value = "删除组织", notes = "删除组织信息", httpMethod = "POST")
	@WebLogger("删除组织")
	@PostMapping("/delDepartment")
	public ResultVO delDepartment(@RequestParam("id") Integer departmentId) {
		UserInfo userInfo = getUserInfo();

		Department department = service.get(departmentId);
		if (department == null) {
			return ResultVO.FAIL("该组织不存在");
		}
		if (department.getLevel() == 0) {
			return ResultVO.FAIL("不允许删除");
		}
		if (department.getOrgId().intValue() != department.getOrgId()) {
			return ResultVO.FAIL("您无权限删除该组织");
		}

		LocalHandle.setOldObj(department);

		//只有属主 或 组织负责人有权限删除组织
		if (!userInfo.isOwner()) {
			List<Integer> departmentIds = userInfo.getDepartmentIds();
			if (departmentIds == null || departmentIds.isEmpty() || !departmentIds.contains(departmentId)) {
				return ResultVO.FAIL("您无权限删除该组织");
			}
		}

		service.delDepartment(department);

		LocalHandle.complete("删除组织");
		userInfoService.del(userInfo.getId());

		return ResultVO.OK("删除成功");
	}

	@ApiOperation(value = "查询可作为企业(部门)父组织的组织列表", notes = "查询可作为企业(部门)父组织的组织列表", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "集团(企业、部门)ID", dataType = "int"),
			@ApiImplicitParam(name = "type", value = "集团(企业、部门)类型 0:部门  1:公司  2:集团", dataType = "int", required = true)
	})
	@GetMapping("/getParents")
	public ResultVO getParents(@RequestParam(value = "id", required = false) Integer departmentId,
							   @RequestParam("type") Integer type) {
		UserInfo userInfo = getUserInfo();

		/*
		 * 查询用户可选择的组织ID列表
		 */
		List<Integer> departmentIds = userInfo.getDepartmentIds();
		if (departmentIds == null || departmentIds.isEmpty()) {
			return ResultVO.OK();
		}

		/*
		 * 修改组织:将修改的组织及子组织从列表中过滤去
		 */
		if (departmentId != null) {
			List<Integer> childrenIds = service.getChildrenIdsByOrgAndParentAndType(userInfo.getOrgId(), departmentId, null);
			if (childrenIds != null && childrenIds.size() > 0 && departmentIds.size() > 0) {
				departmentIds.removeAll(childrenIds);
			}
		}

		if (departmentIds.isEmpty()) {
			return ResultVO.OK();
		}

		List<DepartmentKV> departmentKvs = service.getByOrgAndDepartmentIds(userInfo.getOrgId(), departmentIds);

		/*
		 * 过滤：添加/修改组织(公司)时，父节点只能是公司
		 */
		if (departmentKvs != null && departmentKvs.size() > 0 && type == 1) {
			for (int i = 0; i < departmentKvs.size(); i++) {
				DepartmentKV departmentKV = departmentKvs.get(i);
				int kvType = departmentKV.getType();
				if (kvType == 0) {
					departmentKvs.remove(i);
					i--;
				}
			}
		}

		return ResultVO.OK(departmentKvs);
	}


	@ApiOperation(value = "查询用户所属组织列表", notes = "查询用户所属组织列表", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageNum", value = "当前页(默认:1)", dataType = "int"),
			@ApiImplicitParam(name = "pageSize", value = "每页数(默认:10)", dataType = "int"),
			@ApiImplicitParam(name = "page", value = "是否分页", dataType = "boolean", defaultValue = "true")

	})
	@GetMapping("/getDepartmentsByLogin")
	public ResultVO getDepartmentsByLogin(@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
										  @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
										  @RequestParam(value = "page", required = false, defaultValue = "true") boolean isPage) {
		UserInfo userInfo = getUserInfo();
		List<Department> departments = service.getDepartmentsByOrgAndUser(userInfo.getOrgId(), userInfo.getId());
		if (departments == null || departments.isEmpty()) {
			//如果该用户直属集团，没有组织，则返回该集团所有组织列表
			setPage();
			departments = service.getByOrg(userInfo.getOrgId());
		}
		return ResultVO.Page(departments, isPage);
	}
}
