package com.yunxi.stamper.controller;


import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.logger.threadLocal.LocalHandle;
import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.commons.other.*;
import com.yunxi.stamper.commons.response.Code;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.*;
import com.yunxi.stamper.entityVo.ApplicationNodeVo;
import com.yunxi.stamper.entityVo.UserInfo;
import com.yunxi.stamper.service.*;
import com.yunxi.stamper.sys.aop.annotaion.WebLogger;
import com.yunxi.stamper.sys.lock.LockGlobal;
import com.zengtengpeng.annotation.Lock;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/6/19 0019 15:20
 */
@Slf4j
@Api(tags = "审批节点相关")
@RestController
@RequestMapping("/application/applicationNode")
public class ApplicationNodeController extends BaseController {

	@Autowired
	private ApplicationNodeService service;
	@Autowired
	private ApplicationService applicationService;
	@Autowired
	private SealRecordInfoService sealRecordInfoService;
	@Autowired
	private ErrorTypeService errorTypeService;
	@Autowired
	private ApplicationManagerService applicationManagerService;
	@Autowired
	private ApplicationKeeperService applicationKeeperService;
	@Autowired
	private ApplicationAuditorService applicationAuditorService;
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private UserService userService;
	@Autowired
	private PositionService positionService;
	@Autowired
	private MessageTempService messageTempService;
	@Autowired
	private OrgService orgService;

	/**
	 * 查询指定申请单审批流程
	 */
	@RequestMapping("/getByApplication")
	public ResultVO getByApplication(@RequestParam(value = "applicationId", required = false) Integer applicationId) {
		UserInfo userInfo = getUserInfo();
		Application application = applicationService.get(applicationId);
		if (application != null) {

			//无法查看非本公司的审批流程
			if (userInfo.getOrgId() != application.getOrgId().intValue()) {
				return ResultVO.FAIL(Code.FAIL403);
			}

			//如果不是自己的申请单/管理员/审批人/审计人/授权人,则无权限查看
			if (userInfo.getId() != application.getUserId().intValue()
					&& !userInfo.isOwner()
					&& !applicationService.isAuditor(userInfo, application)
					&& !applicationService.isKeeper(userInfo, application)
					&& !applicationService.isManager(userInfo, application)) {
				return ResultVO.FAIL(Code.FAIL403);
			}

			List<ApplicationNodeVo> resVo = new LinkedList<>();

			List<ApplicationNodeVo> vos = service.getByApplication(applicationId);
			if (vos != null && vos.size() > 0) {
				for (ApplicationNodeVo nodeVo : vos) {
					Integer handle = nodeVo.getHandle();

					/*
					 * '已处理完成'的节点==============================================================================================================
					 */
					List<ApplicationNodeVo> tempNodeList = null;
					if (handle == Global.HANDLE_COMPLETE) {
						tempNodeList = doHandleComplete(application, nodeVo);
					}
					/*
					 * '处理中' 节点 ===================================================================================================================
					 */
					else if (handle == Global.HANDLE_ING) {
						tempNodeList = doHandling(application, nodeVo);
					}
					/*
					 * '未处理' 节点===================================================================================================================
					 */
					else {
						tempNodeList = doNotHandle(application, nodeVo);
					}
					resVo.addAll(tempNodeList);

					Integer icon = nodeVo.getIcon();
					if (icon != null && (icon == Global.ICON_CANCEL || icon == Global.ICON_FAIL)) {
						break;
					}
				}
			}
			return ResultVO.OK(resVo);
		}
		if (isApp()) {
			return ResultVO.OK();
		} else {
			return ResultVO.FAIL("该申请单不存在或已删除");
		}
	}

	/**
	 * 解析未处理的节点
	 *
	 * @param application 申请单
	 * @param nodeVo      节点
	 */
	private List<ApplicationNodeVo> doNotHandle(Application application, ApplicationNodeVo nodeVo) {
		List<ApplicationNodeVo> tempNodeList = new ArrayList<>();
		String name = nodeVo.getName();
		String nodeType = nodeVo.getNodeType();

		//`依次审批` 该节点有1人,处理人1人
		if (nodeType.equalsIgnoreCase(Global.FLOW_LIST)) {
			User user = userService.getWithDel(Integer.parseInt(nodeVo.getManagerIds()));
			nodeVo.getUsers().add(user.getUserName());
			nodeVo.setContent(user.getUserName() + "(未" + name + ")");

			String position = positionService.getPositionByOrgAndUser(application.getOrgId(), user.getId());
			if (StringUtils.isNotBlank(position)) {
				nodeVo.setTitle(position);
			}
			tempNodeList.add(nodeVo);
		}
		//`或签` 该节点有n人,处理人1人
		else if (nodeType.equalsIgnoreCase(Global.FLOW_OR)) {
			String managers = nodeVo.getManagerIds();
			String[] managerIds = managers.split(",");
			for (String managerId : managerIds) {
				User manager = userService.getWithDel(Integer.parseInt(managerId));
				nodeVo.getUsers().add(manager.getUserName());

				String position = positionService.getPositionByOrgAndUser(manager.getOrgId(), manager.getId());
				nodeVo.getPositions().add(StringUtils.isBlank(position) ? "审批人" : position);
			}
			if (managerIds.length > 0) {
				nodeVo.setTitle("分管领导");
			}
			nodeVo.setContent(managerIds.length + "人或签(未" + name + ")");
			tempNodeList.add(nodeVo);
		}
		//`会签`,该节点有n人,处理人n人
		else if (nodeType.equalsIgnoreCase(Global.FLOW_AND)) {
			String managers = nodeVo.getManagerIds();
			String[] managerIds = managers.split(",");
			for (String managerId : managerIds) {
				User manager = userService.getWithDel(Integer.parseInt(managerId));
				nodeVo.getUsers().add(manager.getUserName());

				String position = positionService.getPositionByOrgAndUser(manager.getOrgId(), manager.getId());
				nodeVo.getPositions().add(position);
			}
			if (managerIds.length > 0) {
				nodeVo.setTitle("分管领导");
			}
			nodeVo.setContent(managerIds.length + "人会签(未" + name + ")");
			tempNodeList.add(nodeVo);
		}
		//`n级主管`审批,该节点有<=1人,处理人<=1人
		else if (nodeType.equalsIgnoreCase(Global.FLOW_MANAGER)) {
			//该节点只有一个人
			Integer managerLevel = nodeVo.getManagerLevel();
			User manager = departmentService.getManagerByDepartmentAndTopLevel(application.getDepartmentId(), managerLevel);
			if (manager != null) {
				String position = positionService.getPositionByOrgAndUser(manager.getOrgId(), manager.getId());
				if (StringUtils.isNotBlank(position)) {
					nodeVo.setTitle(position);
				}
				nodeVo.setContent(manager.getUserName() + "(未" + name + ")");
			} else {
				nodeVo.setContent((managerLevel == 1 ? "直接主管" : (managerLevel + "级主管")) + "(未" + name + ")");
			}
			tempNodeList.add(nodeVo);
		}

		//'自选'审批，该节点有>=1人，处理人1人
		else if ("optional".equalsIgnoreCase(nodeType)) {
			String managers = nodeVo.getManagerIds();
			String[] managerIds = managers.split(",");
			for (String managerId : managerIds) {
				User manager = userService.getWithDel(Integer.parseInt(managerId));
				nodeVo.getUsers().add(manager.getUserName());

				String position = positionService.getPositionByOrgAndUser(manager.getOrgId(), manager.getId());
				if (StringUtils.isNotBlank(position)) {
					nodeVo.setTitle(position);
				}
			}
			if (managerIds.length > 0) {
				nodeVo.setTitle("分管领导");
			}
			nodeVo.setContent(managerIds.length + "人自选(未" + name + ")");
			tempNodeList.add(nodeVo);
		}
		return tempNodeList;
	}

	/**
	 * 解析处理完成的节点
	 *
	 * @param application 申请单信息
	 * @param nodeVo      节点对象
	 */
	private List<ApplicationNodeVo> doHandleComplete(Application application, ApplicationNodeVo nodeVo) {
		List<ApplicationNodeVo> tempNodeList = new ArrayList<>();
		String name = nodeVo.getName();
		String nodeType = nodeVo.getNodeType();

		Integer applicationId = application.getId();
		//提交申请 该节点有1人,处理人1人
		if (nodeType.equalsIgnoreCase(Global.FLOW_INIT)) {
			nodeVo.getUsers().add(application.getUserName());
			nodeVo.setContent(application.getUserName());
			nodeVo.setTime(application.getCreateDate());
			tempNodeList.add(nodeVo);
		}
		//`依次审批` 该节点有1人,处理人1人
		else if (nodeType.equalsIgnoreCase(Global.FLOW_LIST)) {
			if ("审批".equalsIgnoreCase(name)) {
				List<ApplicationManager> ams = applicationManagerService.getByApplicationAndNode(applicationId, nodeVo.getId());
				ApplicationManager am = ams.get(0);

				nodeVo.getUsers().add(am.getManagerName());
				nodeVo.setContent(am.getManagerName() + "(已审批)");
				nodeVo.setSuggest(am.getSuggest());
				nodeVo.setTime(am.getTime());

				String position = positionService.getPositionByOrgAndUser(application.getOrgId(), am.getManagerId());
				if (StringUtils.isNotBlank(position)) {
					nodeVo.setTitle(position);
				}

				tempNodeList.add(nodeVo);
			} else if ("授权".equalsIgnoreCase(name)) {
				List<ApplicationKeeper> aks = applicationKeeperService.getByApplicationAndNode(applicationId, nodeVo.getId());
				ApplicationKeeper ak = aks.get(0);

				nodeVo.getUsers().add(ak.getKeeperName());
				nodeVo.setContent(ak.getKeeperName() + "(已授权)");
				nodeVo.setSuggest(ak.getSuggest());
				nodeVo.setTime(ak.getTime());
				tempNodeList.add(nodeVo);
			} else if ("审计".equalsIgnoreCase(name)) {
				List<ApplicationAuditor> aas = applicationAuditorService.getByApplicationAndNode(applicationId, nodeVo.getId());
				if (aas != null && aas.size() > 0) {
					ApplicationAuditor aa = aas.get(0);

					nodeVo.getUsers().add(aa.getAuditorName());
					nodeVo.setContent(aa.getAuditorName() + "(已审计)");
					nodeVo.setSuggest(aa.getSuggest());
					nodeVo.setTime(aa.getTime());
					tempNodeList.add(nodeVo);
				}
			}
		}
		//`或签` 该节点有n人,处理人1人 (只有审批类型的节点才有`或签`)
		else if (nodeType.equalsIgnoreCase(Global.FLOW_OR)) {

			List<ApplicationManager> ams = applicationManagerService.getByApplicationAndNode(applicationId, nodeVo.getId());
			ApplicationManager am = ams.get(0);

			nodeVo.getUsers().add(am.getManagerName());
			nodeVo.setContent(am.getManagerName() + "(已审批)");
			nodeVo.setSuggest(am.getSuggest());
			nodeVo.setTime(am.getTime());

			String position = positionService.getPositionByOrgAndUser(application.getOrgId(), am.getManagerId());
			if (StringUtils.isNotBlank(position)) {
				nodeVo.setTitle(position);
			}
			tempNodeList.add(nodeVo);
		}
		//`会签`,该节点有n人,处理人n人 (审批,授权,审计类型都有可能存在`会签`)
		else if (nodeType.equalsIgnoreCase(Global.FLOW_AND)) {
			if ("审批".equalsIgnoreCase(name)) {
				List<ApplicationManager> ams = applicationManagerService.getByApplicationAndNode(applicationId, nodeVo.getId());
				for (ApplicationManager am : ams) {
					ApplicationNodeVo vo = new ApplicationNodeVo();
					BeanUtils.copyProperties(nodeVo, vo);

					Integer status = am.getStatus();
					vo.getUsers().clear();
					vo.getUsers().add(am.getManagerName());

					String position = positionService.getPositionByOrgAndUser(application.getOrgId(), am.getManagerId());
					if (StringUtils.isNotBlank(position)) {
						vo.setTitle(position);
					}
					vo.setNodeType("list");
					vo.setTime(am.getTime());
					String content = "";
					switch (status) {
						//执行状态 1:审批中 2:审批同意 3:审批拒绝 4:审批转交
						case 2:
							content = "(已同意)";
							break;
						case 3:
							content = "(已拒绝)";
							break;
						case 4:
							content = "(已转交)";
							break;
						default:
					}
					vo.setContent(am.getManagerName() + content);
					vo.setSuggest(am.getSuggest());
					tempNodeList.add(vo);
				}
			} else if ("授权".equalsIgnoreCase(name)) {
				List<ApplicationKeeper> aks = applicationKeeperService.getByApplicationAndNode(applicationId, nodeVo.getId());
				for (ApplicationKeeper ak : aks) {
					ApplicationNodeVo vo = new ApplicationNodeVo();
					BeanUtils.copyProperties(nodeVo, vo);

					Integer status = ak.getStatus();
					vo.getUsers().clear();
					vo.getUsers().add(ak.getKeeperName());
					vo.setNodeType("list");
					vo.setTime(ak.getTime());
					String content = "";
					switch (status) {
						//执行状态 2:授权同意 3:授权拒绝
						case 2:
							content = "(已同意)";
							break;
						case 3:
							content = "(已拒绝)";
							break;
						default:
					}
					vo.setContent(ak.getKeeperName() + content);
					vo.setSuggest(ak.getSuggest());
					tempNodeList.add(vo);
				}

			} else if ("审计".equalsIgnoreCase(name)) {
				List<ApplicationAuditor> aas = applicationAuditorService.getByApplicationAndNode(applicationId, nodeVo.getId());
				for (ApplicationAuditor aa : aas) {
					ApplicationNodeVo vo = new ApplicationNodeVo();
					BeanUtils.copyProperties(nodeVo, vo);

					Integer status = aa.getStatus();
					vo.getUsers().clear();
					vo.getUsers().add(aa.getAuditorName());
					vo.setNodeType("list");
					vo.setTime(aa.getTime());
					String content = "";
					switch (status) {
						case 2:
							content = "(已同意)";
							break;
						case 3:
							content = "(已拒绝)";
							break;
						default:
					}
					vo.setContent(aa.getAuditorName() + content);
					vo.setSuggest(aa.getSuggest());
					tempNodeList.add(vo);
				}
			}
		}
		//`n级主管`审批,该节点有<=1人,处理人<=1人
		else if (nodeType.equalsIgnoreCase(Global.FLOW_MANAGER)) {

			//该节点只有一个人
			List<ApplicationManager> ams = applicationManagerService.getByApplicationAndNode(applicationId, nodeVo.getId());
			ApplicationManager am = ams.get(0);
			String managerIds = nodeVo.getManagerIds();
			if (StringUtils.isBlank(managerIds)) {
				managerIds = am.getManagerName();
			}
			if (managerIds.contains("默认通过")) {
				nodeVo.setContent(managerIds);
			} else {
				nodeVo.setContent(managerIds + "(已" + name + ")");
			}
			nodeVo.setSuggest(am.getSuggest());
			nodeVo.setTime(am.getTime() == null ? am.getUpdateDate() : am.getTime());
			String position = positionService.getPositionByOrgAndUser(application.getOrgId(), am.getManagerId());
			if (StringUtils.isNotBlank(position)) {
				nodeVo.setTitle(position);
			}
			tempNodeList.add(nodeVo);
		}

		//'自选'模式，该节点有>1人，处理人1人
		else if (nodeType.equalsIgnoreCase(Global.FLOW_OPTIONAL)) {
			List<ApplicationManager> ams = applicationManagerService.getByApplicationAndNode(applicationId, nodeVo.getId());
			ApplicationManager am = ams.get(0);

			nodeVo.getUsers().add(am.getManagerName());
			nodeVo.setContent(am.getManagerName() + "(已审批)");
			nodeVo.setSuggest(am.getSuggest());
			nodeVo.setTime(am.getTime());

			String position = positionService.getPositionByOrgAndUser(application.getOrgId(), am.getManagerId());
			if (StringUtils.isNotBlank(position)) {
				nodeVo.setTitle(position);
			}
			tempNodeList.add(nodeVo);
		}
		//申请人取消申请单节点,该节点有1人,处理人1人
		else if (nodeType.equalsIgnoreCase(Global.FLOW_CANCEL)) {
			Integer userId = Integer.parseInt(nodeVo.getManagerIds());
			User user = userService.getWithDel(userId);

			nodeVo.getUsers().add(user.getUserName());
			nodeVo.setContent(user.getUserName() + "(取消申请)");
			nodeVo.setTime(nodeVo.getCreateDate());
			tempNodeList.add(nodeVo);
		}
		return tempNodeList;
	}

	/**
	 * 解析正在处理中的审批节点
	 *
	 * @param application 要查询的审批单
	 * @param nodeVo      要解析的节点实例
	 */
	private List<ApplicationNodeVo> doHandling(Application application, ApplicationNodeVo nodeVo) {
		List<ApplicationNodeVo> tempNodeList = new ArrayList<>();
		String name = nodeVo.getName();
		String nodeType = nodeVo.getNodeType();

		Integer applicationId = application.getId();
		//`依次审批` 该节点有1人,处理人1人
		if (nodeType.equalsIgnoreCase(Global.FLOW_LIST)) {
			User user = userService.getWithDel(Integer.parseInt(nodeVo.getManagerIds()));
			nodeVo.getUsers().add(user.getUserName());
			nodeVo.setContent(user.getUserName() + "(" + name + "中)");

			String position = positionService.getPositionByOrgAndUser(application.getOrgId(), user.getId());
			if (StringUtils.isNotBlank(position)) {
				nodeVo.setTitle(position);
			}
			tempNodeList.add(nodeVo);
		}
		//`或签` 该节点有n人,处理人1人
		else if ("or".equalsIgnoreCase(nodeType)) {

			//审批 处理中
			if ("审批".equalsIgnoreCase(name)) {
				List<ApplicationManager> ams = applicationManagerService.getByApplicationAndNode(applicationId, nodeVo.getId());
				for (ApplicationManager am : ams) {
					Integer status = am.getStatus();
					//2:审批同意 3:审批拒绝 4:审批转交
					String statusContent = "";
					if (status == 2) {
						statusContent = "(已同意)";
					} else if (status == 3) {
						statusContent = "(已拒绝)";
					} else if (status == 4) {
						statusContent = "(已转交)";
					}
					nodeVo.setSuggest(am.getSuggest());
					nodeVo.getUsers().add(am.getManagerName() + statusContent);

					String position = positionService.getPositionByOrgAndUser(application.getOrgId(), am.getManagerId());
					nodeVo.getPositions().add(StringUtils.isBlank(position) ? "审批人" : position);
				}
				if (ams.size() > 0) {
					nodeVo.setTitle("分管领导");
				}
				nodeVo.setContent(ams.size() + "人或签(审批中)");
			}

			//授权 处理中
			else if ("授权".equalsIgnoreCase(name)) {
				List<ApplicationKeeper> aks = applicationKeeperService.getByApplicationAndNode(applicationId, nodeVo.getId());
				for (ApplicationKeeper ak : aks) {
					Integer status = ak.getStatus();
					//2:授权同意 3:授权拒绝 4:已失效
					String statusContent = "";
					if (status == 2) {
						statusContent = "(已同意)";
					} else if (status == 3) {
						statusContent = "(已拒绝)";
					}
					nodeVo.setSuggest(ak.getSuggest());
					nodeVo.getUsers().add(ak.getKeeperName() + statusContent);

				}
				if (aks.size() > 0) {
					nodeVo.setTitle("分管领导");
				}
				nodeVo.setContent(aks.size() + "人或签(授权中)");
			}

			//审计 处理中
			else if ("审计".equalsIgnoreCase(name)) {
				List<ApplicationAuditor> aas = applicationAuditorService.getByApplicationAndNode(applicationId, nodeVo.getId());
				for (ApplicationAuditor aa : aas) {
					Integer status = aa.getStatus();
					//2:审计同意 3:审计拒绝
					String statusContent = "";
					if (status == 2) {
						statusContent = "(已同意)";
					} else if (status == 3) {
						statusContent = "(已拒绝)";
					}
					nodeVo.setSuggest(aa.getSuggest());
					nodeVo.getUsers().add(aa.getAuditorName() + statusContent);

				}
				if (aas.size() > 0) {
					nodeVo.setTitle("分管领导");
				}
				nodeVo.setContent(aas.size() + "人或签(审计中)");
			}
			tempNodeList.add(nodeVo);
		}
		//`会签`,该节点有n人,处理人n人
		else if (nodeType.equalsIgnoreCase(Global.FLOW_AND)) {
			//审批 处理中
			if ("审批".equalsIgnoreCase(name)) {
				List<ApplicationManager> ams = applicationManagerService.getByApplicationAndNode(applicationId, nodeVo.getId());
				for (ApplicationManager am : ams) {
					Integer status = am.getStatus();
					//2:审批同意 3:审批拒绝 4:审批转交
					String statusContent = "";
					if (status == 2) {
						statusContent = "(已同意)";
					} else if (status == 3) {
						statusContent = "(已拒绝)";
					} else if (status == 4) {
						statusContent = "(已转交)";
					}
					nodeVo.setSuggest(am.getSuggest());
					nodeVo.getUsers().add(am.getManagerName() + statusContent);

					String position = positionService.getPositionByOrgAndUser(application.getOrgId(), am.getManagerId());
					nodeVo.getPositions().add(StringUtils.isBlank(position) ? "审批人" : position);
				}
				if (ams.size() > 0) {
					nodeVo.setTitle("分管领导");
				}
				nodeVo.setContent(ams.size() + "人会签(审批中)");
				tempNodeList.add(nodeVo);
			}

			//授权 处理中
			else if ("授权".equalsIgnoreCase(name)) {
				List<ApplicationKeeper> aks = applicationKeeperService.getByApplicationAndNode(applicationId, nodeVo.getId());
				for (ApplicationKeeper ak : aks) {
					Integer status = ak.getStatus();
					//2:授权同意 3:授权拒绝 4:已失效
					String statusContent = "";
					if (status == 2) {
						statusContent = "(已同意)";
					} else if (status == 3) {
						statusContent = "(已拒绝)";
					}
					nodeVo.setSuggest(ak.getSuggest());
					nodeVo.getUsers().add(ak.getKeeperName() + statusContent);

				}
				if (aks.size() > 0) {
					nodeVo.setTitle("分管领导");
				}
				nodeVo.setContent(aks.size() + "人会签(授权中)");
				tempNodeList.add(nodeVo);
			}

			//审计 处理中
			else if ("审计".equalsIgnoreCase(name)) {
				List<ApplicationAuditor> aas = applicationAuditorService.getByApplicationAndNode(applicationId, nodeVo.getId());
				for (ApplicationAuditor aa : aas) {
					Integer status = aa.getStatus();
					//2:审计同意 3:审计拒绝
					String statusContent = "";
					if (status == 2) {
						statusContent = "(已同意)";
					} else if (status == 3) {
						statusContent = "(已拒绝)";
					}
					nodeVo.setSuggest(aa.getSuggest());
					nodeVo.getUsers().add(aa.getAuditorName() + statusContent);

				}
				if (aas.size() > 0) {
					nodeVo.setTitle("分管领导");
				}
				nodeVo.setContent(aas.size() + "人会签(审计中)");
				tempNodeList.add(nodeVo);
			}
		}
		//`n级主管`审批,该节点有<=1人,处理人<=1人
		else if ("manager".equalsIgnoreCase(nodeType)) {
			Integer managerLevel = nodeVo.getManagerLevel();
			User manager = departmentService.getManagerByDepartmentAndTopLevel(application.getDepartmentId(), managerLevel);
			if (manager != null && manager.getId() != null) {
				String position = positionService.getPositionByOrgAndUser(manager.getOrgId(), manager.getId());
				if (StringUtils.isNotBlank(position)) {
					nodeVo.setTitle(position);
				}
				nodeVo.setContent(manager.getUserName() + "(未" + name + ")");
			} else {
				String managerIds = nodeVo.getManagerIds();
				//该节点只有一个人
				if (StringUtils.isBlank(managerIds)) {
					nodeVo.setContent((managerLevel == 1 ? "直接主管" : (managerLevel + "级主管")) + "(" + name + "中)");
				} else {
					nodeVo.setContent(managerIds + "(" + name + "中)");
				}
				List<ApplicationManager> ams = applicationManagerService.getByApplicationAndNode(applicationId, nodeVo.getId());
				nodeVo.setSuggest(ams.get(0).getSuggest());
			}
			tempNodeList.add(nodeVo);
		}

		//'自选'审批，该节点有>=1人，处理人1人
		else if ("optional".equalsIgnoreCase(nodeType)) {
			List<ApplicationManager> ams = applicationManagerService.getByApplicationAndNode(applicationId, nodeVo.getId());
			ApplicationManager am = ams.get(0);

			String managerName = am.getManagerName();
			nodeVo.getUsers().add(managerName);
			nodeVo.setContent(managerName + "(" + name + "中)");

			String position = positionService.getPositionByOrgAndUser(application.getOrgId(), am.getManagerId());
			if (StringUtils.isNotBlank(position)) {
				nodeVo.setTitle(position);
			}
			tempNodeList.add(nodeVo);
		}
		return tempNodeList;
	}

	/**
	 * 审批拒绝
	 *
	 * @param applicationId 申请单ID
	 * @param suggest       拒绝理由
	 * @return 结果
	 */
	@WebLogger("审批拒绝")
	@RequestMapping("/managerFail")
	@Lock(keys = "#applicationId", keyConstant = LockGlobal.handle_application)
	@Transactional
	public ResultVO managerFail(@RequestParam("applicationId") Integer applicationId,
								@RequestParam(value = "suggest", required = false) String suggest) {
		if (EmojiFilter.containsEmoji(suggest)) {
			return ResultVO.FAIL("审批建议不能包含特殊字符");
		}
		Application application = applicationService.get(applicationId);
		if (application == null) {
			return ResultVO.FAIL("该申请单不存在");
		}
		Integer status = application.getStatus();
		if (status > Global.APP_MANAGER) {
			return ResultVO.FAIL("该申请单已审批过了哦");
		}
		LocalHandle.setOldObj(application);

		//当前申请单处于的节点
		ApplicationNode node = service.get(application.getNodeId());
		if (node == null) {
			return ResultVO.FAIL("该申请单执行流程异常");
		}
		UserInfo userInfo = getUserInfo();

		//校验是否该申请单审批人
		List<ApplicationManager> ams = applicationManagerService.getByApplicationAndNode(applicationId, node.getId());
		if (ams == null || ams.size() == 0) {
			return ResultVO.FAIL("您无权限授权该申请单哦");
		}
		int isAM = 0;
		for (ApplicationManager am : ams) {
			if (am.getManagerId() == userInfo.getId().intValue()) {
				isAM++;
				break;
			}
		}
		if (isAM == 0) {
			return ResultVO.FAIL("您无权限授权该申请单哦");
		}

		//锁定申请单
		lockApplication(applicationId);

		/*
		 * 修改当前节点状态
		 */
		node.setHandle(-1);
		node.setIcon(2);
		service.update(node);
		LocalHandle.setNewObj(application);

		/*
		 * 修改申请单状态
		 */
		application.setStatus(Global.APP_MANAGER_FAIL);
		applicationService.update(application);

		/*
		 * 修改申请单-审批记录 状态,将当前节点其他审批记录删除
		 */
		for (ApplicationManager am : ams) {
			Integer managerId = am.getManagerId();
			if (managerId == userInfo.getId().intValue()) {
				/*
				 * 修改该用户的 申请单-审批记录 状态
				 */
				am.setStatus(Global.MANAGER_ERROR);
				am.setSuggest(suggest);
				applicationManagerService.update(am);
			} else {
				/*
				 * 将当前节点其他审批记录删除
				 */
				applicationManagerService.del(am);
			}
		}

		try {
			messageTempService.managerFAILNotice(application.getTitle(), userInfo.getUserName(), application.getUserId());
		} catch (Exception e) {
			log.error("审批拒绝-发送短信异常 title:{} userName:{} userId:{} applicationId:{}", application.getTitle(), userInfo.getUserName(), application.getUserId(), application.getId(), e);
		}
		LocalHandle.setNewObj(application);
		LocalHandle.complete("审批拒绝");
		return ResultVO.OK("已拒绝");
	}


	/**
	 * 锁定指定申请单id
	 */
	private void lockApplication(Integer applicationId) {
		String key = RedisGlobal.LOCK_APPLICATION + applicationId;
		Object obj = redisUtil.get(key);
		if (obj != null) {
			throw new PrintException("申请单正在被处理,请稍后重试");
		} else {
			redisUtil.set(key, DateUtil.format(new Date()), RedisGlobal.LOCK_APPLICATION_TIMEOUT);
		}
	}

	/**
	 * 查询申请单下一个节点是否'自选模式',如果是，将审批人列表返回，如果不是，则返回null
	 *
	 * @param applicationId 要查询的申请单ID
	 * @return 结果
	 */
	@RequestMapping("/getNextOptionalNode")
	public ResultVO getNextOptionalNode(@RequestParam("applicationId") Integer applicationId) {
		Application application = applicationService.get(applicationId);
		if (application == null) {
			return ResultVO.FAIL("申请单不存在");
		}

		//当前申请单处于的节点
		ApplicationNode node = service.get(application.getNodeId());

		//校验是否该申请单审批人
		List<ApplicationManager> ams = applicationManagerService.getByApplicationAndNode(applicationId, node.getId());
		if (ams == null || ams.size() == 0) {
			return ResultVO.OK();
		}

		//查询下一个节点
		ApplicationNode nextNode = service.getByApplicationAndOrderNo(node.getApplicationId(), node.getOrderNo() + 1);
		if (nextNode != null && "optional".equalsIgnoreCase(nextNode.getNodeType())) {
			String nodeManagerIds = nextNode.getManagerIds();
			if (StringUtils.isNotBlank(nodeManagerIds)) {
				List<Integer> managerIds = CommonUtils.splitToInteger(nodeManagerIds, ",");
				if (managerIds != null && managerIds.size() > 0) {
					List<Map<String, Object>> maps = new LinkedList<>();

					for (Integer managerId : managerIds) {
						Map<String, Object> map = new HashMap<>(2);
						map.put("id", managerId);
						User manager = userService.getWithDel(managerId);
						if (manager != null && manager.getId() != null) {
							map.put("name", manager.getUserName());
						}
						maps.add(map);
					}

					return ResultVO.OK(maps);
				}
			}
		}

		return ResultVO.OK();
	}

	/**
	 * 审批同意
	 *
	 * @param applicationId         申请单ID
	 * @param optionalManagerUserId '自选模式'指定的下一级审批人
	 * @param suggest               意见
	 * @return 结果
	 */
	@ApiOperation(value = "查询可作为'印章管理员'员工列表", notes = "查询可作为'印章管理员'员工列表", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "applicationId", value = "组织ID列表", dataType = "int", required = true),
			@ApiImplicitParam(name = "optionalManagerUserId", value = "自选审批人ID", dataType = "int", required = true),
			@ApiImplicitParam(name = "suggest", value = "每页数(默认:10)", dataType = "string", required = true)

	})
	@WebLogger("审批同意")
	@Lock(keys = "#applicationId", keyConstant = LockGlobal.handle_application)
	@RequestMapping("/managerOK")
	@Transactional
	public ResultVO managerOK(@RequestParam("applicationId") Integer applicationId,
							  @RequestParam(value = "optionalManagerUserId", required = false) Integer optionalManagerUserId,
							  @RequestParam(value = "suggest", required = false) String suggest) {
		//参数校验：意见*/
		if (EmojiFilter.containsEmoji(suggest)) {
			return ResultVO.FAIL("审批建议不能包含特殊字符");
		}

		//参数校验：自选审批人*/
		UserInfo userInfo = getUserInfo();
		User optionalManagerUser;
		if (optionalManagerUserId != null) {
			optionalManagerUser = userService.get(optionalManagerUserId);
			if (optionalManagerUser == null
					|| optionalManagerUser.getOrgId() != userInfo.getOrgId().intValue()) {
				return ResultVO.FAIL("自选审批人不存在");
			}
		}

		//参数校验：申请单*/
		Application application = applicationService.get(applicationId);
		if (application == null
				|| application.getOrgId() != userInfo.getOrgId().intValue()) {
			return ResultVO.FAIL("该申请单不存在");
		}
		LocalHandle.setOldObj(application);
		return excuteManagerOk(optionalManagerUserId, suggest, userInfo, application);
	}

	public ResultVO excuteManagerOk(Integer optionalManagerUserId, String suggest, UserInfo userInfo, Application application) {
		Integer applicationId = application.getId();
		Integer status = application.getStatus();
		if (status > Global.APP_MANAGER) {
			return ResultVO.FAIL("该申请单已审批过了哦");
		}

		//当前申请单处于的节点
		ApplicationNode node = service.get(application.getNodeId());

		//校验是否该申请单审批人
		List<ApplicationManager> ams = applicationManagerService.getByApplicationAndNode(applicationId, node.getId());
		if (ams == null || ams.size() == 0) {
			return ResultVO.FAIL("您无权限授权该申请单哦");
		}
		int isAM = 0;
		for (ApplicationManager am : ams) {
			if (am.getManagerId() == userInfo.getId().intValue()) {
				isAM++;
				break;
			}
		}
		if (isAM == 0) {
			return ResultVO.FAIL("您无权限授权该申请单哦");
		}

		//锁定申请单
		lockApplication(applicationId);

		boolean isNextCall = false;//标记 true:通知下一组审批  false:不通知下一组审批

		Integer handle = node.getHandle();
		if (handle == Global.HANDLE_COMPLETE) {
			return ResultVO.FAIL("该审批记录已失效,可能存在其他领导已审批通过/拒绝,请稍后重试");
		}
		String nodeType = node.getNodeType();

		//修改申请单-审批记录 状态
		ApplicationManager am = applicationManagerService.getByApplicationAndManagerAndNode(applicationId, userInfo.getId(), node.getId());
		if (am == null) {
			return ResultVO.FAIL("该审批记录已失效");
		}
		am.setStatus(Global.MANAGER_SUCCESS);
		am.setSuggest(suggest);
		am.setTime(new Date());
		applicationManagerService.update(am);

		/*
		 * 依次审批+主管审批+或签-->只要一个审批人同意了,则当前节点就同意了
		 */
		if ("list".equalsIgnoreCase(nodeType) || "manager".equalsIgnoreCase(nodeType) || "or".equalsIgnoreCase(nodeType)) {

			/*
			 * 或签--->有多个审批人,只要一个人同意,当前节点就同意,所以删除其他同级节点
			 */
			if ("or".equalsIgnoreCase(nodeType)) {
				if (ams.size() > 0) {
					for (ApplicationManager otherAM : ams) {
						if (otherAM.getId().intValue() != am.getId()) {
							otherAM.setStatus(Global.MANAGER_CANCEL);
							applicationManagerService.del(otherAM);
						}
					}
				}
			}

			//修改当前节点状态
			node.setHandle(Global.HANDLE_COMPLETE);
			node.setIcon(Global.ICON_SUCCESS);
			service.update(node);

			//通知下一组审批
			isNextCall = true;

		}
		/*
		 * 会签-->有多个审批人,必须所有人同意,当前节点才同意
		 */
		else if ("and".equalsIgnoreCase(nodeType)) {
			int managerOKNum = 0;
			ams = applicationManagerService.getByApplicationAndNode(applicationId, node.getId());
			for (ApplicationManager otherAM : ams) {
				if (otherAM.getStatus() == Global.MANAGER_SUCCESS || otherAM.getStatus() == Global.MANAGER_TRANS) {
					managerOKNum++;
				}
			}
			if (managerOKNum == ams.size()) {
				//修改当前节点状态
				node.setHandle(Global.HANDLE_COMPLETE);
				node.setIcon(Global.ICON_SUCCESS);
				service.update(node);
				//通知下一组审批
				isNextCall = true;
			}
		}
		/*
		 * 自选-->仅有1个审批人
		 */
		else if ("optional".equalsIgnoreCase(nodeType)) {
			//修改当前节点状态
			node.setHandle(Global.HANDLE_COMPLETE);
			node.setIcon(Global.ICON_SUCCESS);
			service.update(node);

			//通知下一组审批
			isNextCall = true;

		} else {
			throw new PrintException("当前审批节点类型有误");
		}

		/*
		 * 通知下一组审批
		 */
		if (isNextCall) {
			ApplicationNode nextNode = service.getByApplicationAndOrderNo(node.getApplicationId(), node.getOrderNo() + 1);
			String name = nextNode.getName();
			if ("审批".equalsIgnoreCase(name)) {

				String nextNodeType = nextNode.getNodeType();
				if (StringUtils.isNotBlank(nextNodeType) && "optional".equalsIgnoreCase(nextNodeType)) {
					if (optionalManagerUserId == null) {
						throw new PrintException("未指定下一节点审批人");
					}
					String nodeManagerIds = nextNode.getManagerIds();
					if (!nodeManagerIds.contains(optionalManagerUserId + "")) {
						return ResultVO.FAIL("指定的审批人不存在");
					}
					applicationManagerService.createByNodeByOptional(nextNode, optionalManagerUserId);
				} else {
					applicationManagerService.createByNode(nextNode);
				}
			} else if ("授权".equalsIgnoreCase(name)) {

				//已全部审批完成,修改申请单状态
				application.setStatus(Global.APP_KEEPER);
				application.setNodeId(nextNode.getId());
				applicationService.update(application);

				applicationKeeperService.createByNode(nextNode);
			}
			/*
			 * 通知申请人，审批完成
			 */
			try {
				messageTempService.managerOKNotice(application.getTitle(), am.getManagerName(), application.getUserId());
			} catch (Exception e) {
				log.error("审批通过-发送通知异常 title:{} managerName:{} userId:{} applicationId:{}", application.getTitle(), am.getManagerName(), application.getUserId(), applicationId, e);
			}
		}

		LocalHandle.setNewObj(application);
		LocalHandle.complete("审批同意");
		return ResultVO.OK("已审批同意");
	}

	@ApiOperation(value = "审批转交", notes = "审批转交", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "applicationId", value = "申请单ID", dataType = "int"),
			@ApiImplicitParam(name = "pushUserId", value = "转交审批人ID", dataType = "int"),
			@ApiImplicitParam(name = "suggest", value = "意见", dataType = "string")
	})
	@WebLogger("审批转交")
	@Lock(keys = "#applicationId", keyConstant = LockGlobal.handle_application)
	@PostMapping("/managerTrans")
	public ResultVO managerTrans(@RequestParam("applicationId") Integer applicationId,
								 @RequestParam("pushUserId") Integer pushUserId,
								 @RequestParam(value = "suggest", required = false) String suggest) {
		UserInfo userInfo = getUserInfo();
		/*
		 * 参数校验：申请单
		 */
		Application application = applicationService.get(applicationId);
		if (application == null || application.getOrgId().intValue() != userInfo.getOrgId()) {
			return ResultVO.FAIL("申请单不存在");
		}
		Integer status = application.getStatus();
		if (status == null || status != 1) {
			return ResultVO.FAIL("申请单状态有误");
		}
		LocalHandle.setOldObj(application);

		/*
		 * 参数校验：转交审批人
		 */
		User user = userService.get(pushUserId);
		if (user == null || user.getOrgId().intValue() != userInfo.getOrgId()) {
			return ResultVO.FAIL("转交审批人不存在");
		}
		boolean isManager = userService.isManager(user.getId());
		if (!isManager) {
			return ResultVO.FAIL("被转交人无'审批处理'权限");
		}

		/*
		 * 参数校验：意见
		 */
		if (EmojiFilter.containsEmoji(suggest)) {
			return ResultVO.FAIL("描述信息不能包含特殊字符");
		}

		/*
		 * 权限校验：操作人是否拥有审批权限
		 */
		Integer nodeId = application.getNodeId();
		ApplicationNode node = service.get(nodeId);
		String nodeType = node.getNodeType();
		if (Global.FLOW_AND.equalsIgnoreCase(nodeType)
				|| Global.FLOW_OR.equalsIgnoreCase(nodeType)
				|| Global.FLOW_LIST.equalsIgnoreCase(nodeType)
				|| Global.FLOW_OPTIONAL.equalsIgnoreCase(nodeType)) {
			String nodeManagerIds = node.getManagerIds();
			List<Integer> managerIds = CommonUtils.splitToInteger(nodeManagerIds, ",");
			if (managerIds == null
					|| managerIds.isEmpty()
					|| !managerIds.contains(userInfo.getId())) {
				return ResultVO.FAIL("您暂无审批权限");
			}
		} else if (Global.FLOW_MANAGER.equalsIgnoreCase(nodeType)) {
			Integer departmentId = application.getDepartmentId();
			Integer level = node.getManagerLevel();
			User manager = departmentService.getManagerByDepartmentAndTopLevel(departmentId, level);
			if (manager == null || manager.getId().intValue() != userInfo.getId()) {
				return ResultVO.FAIL("您暂无审批权限");
			}
		} else {
			return ResultVO.FAIL("申请单状态有误");
		}

		/*
		 * 权限校验：被转交审批人是否有效
		 */
		List<Integer> managerIds = service.getManagersByApplication(applicationId);
		if (managerIds != null
				&& !managerIds.isEmpty()
				&& managerIds.contains(pushUserId)) {
			return ResultVO.FAIL("被转交审批人已在审批列表中");
		}

		service.managerTrans(userInfo, user, application, suggest, node);
		LocalHandle.complete("审批转交");
		return ResultVO.OK("转交成功");
	}

	/**
	 * 快速转交
	 *
	 * @param userId      被转交人ID
	 * @param transUserId 转交给谁
	 * @return 结果
	 */
	@WebLogger("快速转交")
	@RequestMapping("/fastTrans")
	@Transactional
	public ResultVO fastTrans(@RequestParam("userId") Integer userId,
							  @RequestParam("transUserId") Integer transUserId) {
		if (transUserId == null) {
			return ResultVO.FAIL("转交目标不存在，无法转交");
		}
		if (userId.intValue() == transUserId) {
			return ResultVO.FAIL("无法转交给同一个人");
		}
		User user = userService.get(userId);
		if (user == null || user.getId() == null) {
			return ResultVO.FAIL("被转交人不存在，无法转交");
		}
		User transUser = userService.get(transUserId);
		if (transUser == null || transUser.getId() == null) {
			return ResultVO.FAIL("转交目标不存在或已注销，无法转交");
		}
		Integer status = transUser.getStatus();
		if (status != null && status == 1) {
			return ResultVO.FAIL("转交目标账号已被禁用，无法转交");
		}
		Integer type = transUser.getType();
		if (type != null && type == 3) {
			return ResultVO.FAIL("转交目标账号未激活，无法转交");
		}
		List<Integer> applications = applicationService.getApplicationsByUser(userId);
		if (applications != null && applications.size() > 0) {
			for (Integer applicationId : applications) {
				ResultVO resultVO = _managerTrans(userId, user.getUserName(), applicationId, transUserId);
				if (resultVO == null || resultVO.getCode() != Code.OK.getCode()) {
					throw new PrintException("快速转交失败，请稍后重试或手动转交");
				}
			}

			return ResultVO.OK("转交成功!");
		} else {
			return ResultVO.FAIL("该用户没有待处理任务，无需转交");
		}

	}


	private ResultVO _managerTrans(Integer userId, String userName, Integer applicationId, Integer pushUserId) {
		String suggest = "审批员已被管理员删除，任务转交";
		Application application = applicationService.get(applicationId);
		if (application == null) {
			return ResultVO.FAIL("该申请单不存在");
		}
		Integer status = application.getStatus();
		if (status > Global.APP_MANAGER) {
			return ResultVO.FAIL("该申请单已审批过了哦");
		}
		if (pushUserId == null) {
			return ResultVO.FAIL("转交审批人不存在");
		}
		User pushManager = userService.get(pushUserId);
		if (pushManager == null) {
			return ResultVO.FAIL("转交审批人不存在或已注销");
		}
		Integer type = pushManager.getType();
		if (type != null && type == 3) {
			return ResultVO.FAIL("被转交人未激活，无法转交");
		}
		Integer userStatus = pushManager.getStatus();
		if (userStatus != null && userStatus == 1) {
			return ResultVO.FAIL("被转交人账号已被禁用，无法转交");
		}

		//转交人本人
		if (pushManager.getId().intValue() == userId) {
			return ResultVO.OK("不能转交给自己");
		}

		//当前申请单处于的节点
		ApplicationNode node = service.get(application.getNodeId());

		//校验是否该申请单审批人
		List<ApplicationManager> ams = applicationManagerService.getByApplicationAndNode(applicationId, node.getId());
		if (ams == null || ams.size() == 0) {
			return ResultVO.FAIL("您无权限授权该申请单哦");
		}
		int isAM = 0;
		for (ApplicationManager am : ams) {
			if (am.getManagerId().intValue() == userId) {
				isAM++;
				break;
			}
		}
		if (isAM == 0) {
			return ResultVO.FAIL("您无权限授权该申请单哦");
		}

		//锁定申请单
		lockApplication(applicationId);

		/*
		 * 转交相当于该审批人同意,在当前orderNo处,添加一个审批节点
		 */
		Integer handle = node.getHandle();
		if (handle == Global.HANDLE_COMPLETE) {
			return ResultVO.FAIL("该审批记录已失效,可能存在其他领导已审批通过/拒绝,请稍后重试");
		}
		String nodeType = node.getNodeType();

		/*
		 * 单人
		 */
		if ("list".equalsIgnoreCase(nodeType) || "manager".equalsIgnoreCase(nodeType)) {
			/*
			 * 转交人在下一个节点,则不需要转交
			 */
			if ("list".equalsIgnoreCase(nodeType)) {
				ApplicationNode nextNode = service.getByApplicationAndOrderNo(applicationId, node.getOrderNo() + 1);
				String nodeManagerIds = nextNode.getManagerIds();
				String name = nextNode.getName();
				if ("审批".equalsIgnoreCase(name) && nodeManagerIds.contains(pushUserId + "")) {
					return ResultVO.FAIL("转交审批人已在当前审批队列,无需转交");
				}
			}

			/*
			 * 更新 申请单-审批 记录状态
			 */
			ApplicationManager am = applicationManagerService.getByApplicationAndManagerAndNode(applicationId, userId, node.getId());
			if (am == null) {
				return ResultVO.FAIL("该审批记录已失效");
			}
			am.setStatus(Global.MANAGER_TRANS);
			am.setPushUserId(pushUserId);
			am.setSuggest(suggest);
			am.setPushUserName(pushManager.getUserName());
			am.setTime(new Date());
			applicationManagerService.update(am);

			/*
			 * 更新该节点状态
			 */
			node.setHandle(Global.HANDLE_COMPLETE);
			node.setIcon(Global.ICON_TRANS);
			service.update(node);

			/*
			 * 将当前节点(包含当前)之后的所有节点order值加1
			 */
			List<ApplicationNode> nodes = service.getByApplicationAndGreaterThanOrderNo(node.getApplicationId(), node.getOrderNo() + 1);
			for (ApplicationNode n : nodes) {
				n.setOrderNo(n.getOrderNo() + 1);
				service.update(n);
			}

			/*
			 * 在下一个节点位置插入新的节点
			 */
			ApplicationNode managerNode = new ApplicationNode();
			managerNode.setApplicationId(application.getId());
			managerNode.setHandle(Global.HANDLE_ING);//处理中
			managerNode.setTitle("审批人");
			managerNode.setManagerIds(pushManager.getId() + "");
			managerNode.setName("审批");
			managerNode.setOrderNo(node.getOrderNo() + 1);
			managerNode.setNodeType("list");
			managerNode.setIcon(Global.ICON_WAIT);
			service.add(managerNode);

			/*
			 * 创建审批记录
			 */
			applicationManagerService.createByNode(managerNode);

			/*
			 * 修改申请单状态
			 */
			application.setNodeId(managerNode.getId());
			applicationService.update(application);
		}
		/*
		 * 多人
		 */
		else if ("and".equalsIgnoreCase(nodeType) || "or".equalsIgnoreCase(nodeType)) {
			/*
			 * 转交人在当前节点,则无需转交
			 */
			String managerIds = node.getManagerIds();
			if (managerIds.contains(pushUserId + "")) {
				return ResultVO.FAIL("转交人已存在当前节点,无需转交");
			}

			/*
			 * 更新 当前审批人的 申请单-审批 记录状态
			 */
			ApplicationManager am = applicationManagerService.getByApplicationAndManagerAndNode(applicationId, userId, node.getId());
			if (am == null) {
				return ResultVO.FAIL("该审批记录已失效");
			}
			am.setStatus(Global.MANAGER_TRANS);
			am.setPushUserId(pushUserId);
			am.setPushUserName(pushManager.getUserName());
			am.setTime(new Date());
			applicationManagerService.update(am);

			/*
			 * 将当前节点中的审批人id替换为被审批人id
			 */
			String nodeManagerIds = node.getManagerIds();
			List<Integer> ids = CommonUtils.splitToInteger(nodeManagerIds, ",");
			if (ids == null || ids.isEmpty()) {
				throw new PrintException("审批人列表有误");
			}
			List<Integer> resultIds = new ArrayList<>();
			for (Integer nodeManagerId : ids) {
				if (nodeManagerId.intValue() == pushUserId) {
					nodeManagerId = pushUserId;
				}
				resultIds.add(nodeManagerId);
			}
			node.setManagerIds(CommonUtils.listToString(resultIds));
			service.update(node);

			/*
			 * 创建被转交人的审批记录
			 */
			ApplicationManager pushAM = new ApplicationManager();
			pushAM.setNodeId(node.getId());
			pushAM.setManagerId(pushManager.getId());
			pushAM.setManagerName(pushManager.getUserName());
			pushAM.setFromUserId(userId);
			pushAM.setStatus(Global.MANAGER_HANDLING);
			pushAM.setApplicationId(applicationId);
			pushAM.setOrgId(application.getOrgId());
			applicationManagerService.add(pushAM);

			/*
			 * 将当前节点(包含当前)之后的所有节点order值加1
			 */
			List<ApplicationNode> nodes = service.getByApplicationAndGreaterThanOrderNo(node.getApplicationId(), node.getOrderNo());
			for (ApplicationNode n : nodes) {
				n.setOrderNo(n.getOrderNo() + 1);
				service.update(n);
			}

			/*
			 * 将当前审批人 作为一个新的节点插入上一个位置
			 */
			ApplicationNode preNode = new ApplicationNode();
			preNode.setApplicationId(application.getId());
			preNode.setHandle(Global.HANDLE_COMPLETE);//标记已处理
			preNode.setTitle("审批人");
			preNode.setManagerIds(userId + "");
			preNode.setName("审批");
			preNode.setOrderNo(node.getOrderNo());
			preNode.setNodeType("list");
			preNode.setIcon(Global.ICON_TRANS);
			service.add(preNode);

			try {
				Integer orgId = getUserInfo().getOrgId();
				Org org = orgService.get(orgId);
				String orgName = org.getName();
				messageTempService.transferManagerNotice(orgName, userName, pushAM.getManagerId());
			} catch (Exception e) {
				log.error("审批转发-发送通知异常 userId:{} userName:{} applicationId:{} pushUserId:{} suggest:{}", userId, userName, applicationId, pushUserId, suggest, e);
			}

			try {
				messageTempService.approvalTransferNotice(application.getTitle(), userName, pushAM.getManagerName(), application.getUserId());
			} catch (Exception e) {
				log.error("审批转发-发送通知异常 userId:{} userName:{} applicationId:{} pushUserId:{} suggest:{}", userId, userName, applicationId, pushUserId, suggest, e);
			}
		} else {
			throw new PrintException("当前审批节点类型有误");
		}
		return ResultVO.OK("已转交");
	}


	/**
	 * 授权通过
	 */
	@WebLogger("授权通过")
	@PostMapping("/keeperOK")
	@Lock(keys = "#applicationId", keyConstant = LockGlobal.handle_application)
	@Transactional
	public ResultVO keeperOK(@RequestParam("applicationId") Integer applicationId,
							 @RequestParam(value = "suggest", required = false) String suggest) {
		if (EmojiFilter.containsEmoji(suggest)) {
			return ResultVO.FAIL("审批建议不能包含特殊字符");
		}
		Application application = applicationService.get(applicationId);
		if (application == null) {
			return ResultVO.FAIL("该申请单不存在");
		}
		LocalHandle.setOldObj(application);
		if (application.getStatus() > Global.APP_KEEPER) {
			return ResultVO.FAIL("该申请单已授权过了哦");
		}

		//当前申请单处于的节点
		ApplicationNode node = service.get(application.getNodeId());

		UserInfo userInfo = getUserInfo();

		//校验是否该申请单授权人
		List<ApplicationKeeper> aks = applicationKeeperService.getByApplicationAndNode(applicationId, node.getId());
		if (aks == null || aks.size() == 0) {
			return ResultVO.FAIL("您无权限授权该申请单哦");
		}
		int isAk = 0;
		for (ApplicationKeeper ak : aks) {
			if (ak.getKeeperId().intValue() == userInfo.getId()) {
				isAk++;
				break;
			}
		}
		if (isAk == 0) {
			return ResultVO.FAIL("您无权限授权该申请单哦");
		}

		//锁定申请单
		lockApplication(applicationId);

		String nodeType = node.getNodeType();

		/*
		 * 授权只有 `list` 与 `and` 两种
		 */
		if ("list".equalsIgnoreCase(nodeType)) {
			/*
			 * 修改该管章人的 申请单-授权 状态
			 */
			for (ApplicationKeeper ak : aks) {
				ak.setStatus(Global.KEEPER_SUCCESS);
				ak.setSuggest(suggest);
				ak.setTime(new Date());
				applicationKeeperService.update(ak);
			}

			/*
			 * 修改当前节点状态
			 */
			node.setIcon(Global.ICON_SUCCESS);
			node.setHandle(Global.HANDLE_COMPLETE);
			service.update(node);

			//查询下一个节点
			ApplicationNode nextNode = service.getByApplicationAndOrderNo(applicationId, node.getOrderNo() + 1);

			/*
			 * 修改申请单状态
			 */
			application.setStatus(Global.APP_KEEPER_OK);
			application.setNodeId(nextNode.getId());
			applicationService.update(application);

			/*
			 * 创建下一个节点审计记录
			 */
			applicationAuditorService.createByNode(nextNode);

			//通知申请人 申请单已授权通过
			try {
				messageTempService.keeperOKNotice(application.getTitle(), userInfo.getUserName(), application.getUserId());
			} catch (Exception e) {
				log.error("授权通过-发送通知异常 applicationId:{} suggest:{}", applicationId, suggest, e);
			}
		} else {
			/*
			 * 修改 申请单-授权 状态
			 */
			for (int i = 0; i < aks.size(); i++) {
				ApplicationKeeper ak = aks.get(i);
				Integer keeperId = ak.getKeeperId();

				if (keeperId.intValue() == userInfo.getId()) {
					ak.setStatus(Global.KEEPER_SUCCESS);
					ak.setSuggest(suggest);
					ak.setTime(new Date());
					applicationKeeperService.update(ak);
					aks.remove(i);
					i--;
				}
			}

			//通知下一组审批
			if (aks.size() == 0) {
				/*
				 * 修改当前节点状态
				 */
				node.setIcon(Global.ICON_SUCCESS);
				node.setHandle(Global.HANDLE_COMPLETE);
				service.update(node);

				//查询下一个节点
				ApplicationNode nextNode = service.getByApplicationAndOrderNo(applicationId, node.getOrderNo() + 1);

				/*
				 * 修改申请单状态
				 */
				application.setStatus(Global.APP_KEEPER_OK);
				application.setNodeId(nextNode.getId());
				applicationService.update(application);

				/*
				 * 创建下一个节点审计记录
				 */
				applicationAuditorService.createByNode(nextNode);
			}
		}
		LocalHandle.setNewObj(application);
		LocalHandle.complete("授权同意");
		return ResultVO.OK("已授权通过");
	}

	/**
	 * 授权拒绝
	 */
	@WebLogger("授权拒绝")
	@RequestMapping("/keeperFAIL")
	@Lock(keys = "#applicationId", keyConstant = LockGlobal.handle_application)
	@Transactional
	public ResultVO keeperFAIL(@RequestParam("applicationId") Integer applicationId,
							   @RequestParam(value = "suggest", required = false) String suggest) {
		if (EmojiFilter.containsEmoji(suggest)) {
			return ResultVO.FAIL("审批建议不能包含特殊字符");
		}
		Application application = applicationService.get(applicationId);
		if (application == null) {
			return ResultVO.FAIL("该申请单不存在");
		}
		LocalHandle.setOldObj(application);
		Integer status = application.getStatus();
		if (status > Global.APP_KEEPER) {
			return ResultVO.FAIL("该申请单已授权过了哦");
		}

		//当前申请单处于的节点
		ApplicationNode node = service.get(application.getNodeId());

		UserInfo userInfo = getUserInfo();

		//校验是否该申请单授权人
		List<ApplicationKeeper> aks = applicationKeeperService.getByApplication(applicationId);
		if (aks == null || aks.size() == 0) {
			return ResultVO.FAIL("您无权限授权该申请单哦");
		}
		int isAk = 0;
		for (ApplicationKeeper ak : aks) {
			if (ak.getKeeperId().intValue() == userInfo.getId()) {
				isAk++;
				break;
			}
		}
		if (isAk == 0) {
			return ResultVO.FAIL("您无权限授权该申请单哦");
		}

		//锁定申请单
		lockApplication(applicationId);

		/*
		 * 修改 申请单-授权 状态,将其他授权记录删除
		 */
		for (ApplicationKeeper ak : aks) {
			Integer keeperId = ak.getKeeperId();

			if (keeperId.intValue() == userInfo.getId()) {
				ak.setStatus(Global.KEEPER_ERROR);
				ak.setSuggest(suggest);
				ak.setTime(new Date());
				applicationKeeperService.update(ak);
			} else {
				/*
				 * 删除其他同nodeId的节点记录
				 */
				applicationKeeperService.del(ak);
			}
		}

		/*
		 * 修改当前节点状态
		 */
		node.setIcon(Global.ICON_FAIL);
		node.setHandle(Global.HANDLE_COMPLETE);
		service.update(node);

		/*
		 * 修改申请单状态
		 */
		application.setStatus(Global.APP_KEEPER_FAIL);
		applicationService.update(application);

		//通知申请人 申请单已授权拒绝
		try {
			messageTempService.keeperFAILNotice(application.getTitle(), userInfo.getUserName(), application.getUserId());
		} catch (Exception e) {
			log.error("授权拒绝-发送通知异常 applicationId:{} suggest:{}", applicationId, suggest, e);
		}
		LocalHandle.setNewObj(application);
		LocalHandle.complete("授权拒绝");
		return ResultVO.OK("已授权拒绝");
	}

	/**
	 * 审计通过
	 * ps:
	 * 1.修改审计状态
	 * 2.修改申请单状态
	 * 3.事物管理
	 */
	@WebLogger("审计通过")
	@RequestMapping("/auditorOK")
	@Lock(keys = "#applicationId", keyConstant = LockGlobal.handle_application)
	@Transactional
	public ResultVO auditorOK(@RequestParam("applicationId") Integer applicationId,
							  @RequestParam(value = "suggest", required = false) String suggest) {
		if (EmojiFilter.containsEmoji(suggest)) {
			return ResultVO.FAIL("审批建议不能包含特殊字符");
		}
		Application application = applicationService.get(applicationId);
		if (application == null) {
			return ResultVO.FAIL("该申请单不存在");
		}
		LocalHandle.setOldObj(application);
		if (application.getStatus() > Global.APP_AUDITOR) {
			return ResultVO.FAIL("该申请单已审计过了哦");
		}

		//当前申请单处于的节点
		ApplicationNode node = service.get(application.getNodeId());
		String nodeType = node.getNodeType();

		UserInfo userInfo = getUserInfo();

		//校验是否该申请单审计人
		List<ApplicationAuditor> aas = applicationAuditorService.getByApplicationAndAuditorAndNode(applicationId, userInfo.getId(), node.getId());
		if (aas == null || aas.size() == 0) {
			return ResultVO.FAIL("您无权限审计该申请单哦");
		}

		//锁定申请单
		lockApplication(applicationId);

		/*
		 * 审计只有 `list` 与 `and` 两种
		 */
		if ("list".equalsIgnoreCase(nodeType)) {
			/*
			 * 修改 申请单-审计 状态
			 */
			for (ApplicationAuditor aa : aas) {
				//检查印章是否正在使用中
				Integer deviceId = aa.getDeviceId();
				Integer online = isOnline(deviceId);//null:不在线  0:在线、关锁 1:在线、开锁
				if (online != null && online == 1) {
					throw new PrintException("当前设备正在使用中，请稍后审计");
				}

				aa.setStatus(Global.AUDITOR_SUCCESS);
				aa.setSuggest(suggest);
				aa.setTime(new Date());
				applicationAuditorService.update(aa);
			}


			/*
			 * 修改当前节点状态
			 */
			node.setIcon(Global.ICON_SUCCESS);
			node.setHandle(Global.HANDLE_COMPLETE);
			service.update(node);

			/*
			 * 修改申请单状态
			 */
			application.setStatus(Global.APP_AUDITOR_OK);
			applicationService.update(application);

			//审计通过,通知申请人
			try {
				messageTempService.auditorOKNotice(application.getTitle(), userInfo.getUserName(), application.getUserId());
			} catch (Exception e) {
				log.error("审计通过-发送通知异常 applicationId:{} suggest:{}", applicationId, suggest, e);
			}

			//审计通过,通知BOSS
//			Org org = orgService.get(userInfo.getOrgId());
//			try {
//				messageTempService.auditorOKToBossNotice(application.getTitle(), org.getManagerUserId());
//			} catch (Exception e) {
//				log.error("审计通过-发送通知异常 applicationId:{} suggest:{}", applicationId, suggest, e);
//			}

		} else {
			/*
			 * 修改 申请单-授权 状态
			 */
			for (int i = 0; i < aas.size(); i++) {
				ApplicationAuditor aa = aas.get(i);
				Integer auditorId = aa.getAuditorId();
				if (auditorId.intValue() == userInfo.getId()) {
					aa.setStatus(Global.AUDITOR_SUCCESS);
					aa.setSuggest(suggest);
					aa.setTime(new Date());
					applicationAuditorService.update(aa);
					aas.remove(i);
					i--;
				}
			}

			//审计通过,通知申请人
			try {
				messageTempService.auditorOKNotice(application.getTitle(), userInfo.getUserName(), application.getUserId());
			} catch (Exception e) {
				log.error("审计通过-发送通知异常 applicationId:{} suggest:{}", applicationId, suggest, e);
			}

			/*
			 * 是否已全部审计完成
			 */
			if (aas.size() == 0) {
				/*
				 * 修改当前节点状态
				 */
				node.setIcon(Global.ICON_SUCCESS);
				node.setHandle(Global.HANDLE_COMPLETE);
				service.update(node);

				/*
				 * 修改申请单状态
				 */
				application.setStatus(Global.APP_AUDITOR_OK);
				applicationService.update(application);

//				//审计通过,通知BOSS
//				Org org = orgService.get(userInfo.getOrgId());
//				try {
//					messageTempService.auditorOKToBossNotice(application.getTitle(), org.getManagerUserId());
//				} catch (Exception e) {
//					log.error("审计通过-发送通知异常 applicationId:{} suggest:{}", applicationId, suggest, e);
//				}
			}
		}
		LocalHandle.setNewObj(application);
		LocalHandle.complete("审计同意");
		return ResultVO.OK("已审计通过");
	}

	/**
	 * 审计拒绝
	 * ps:
	 * 1.修改所有审计记录状态
	 * 2.修改申请单状态
	 */
	@WebLogger("审计拒绝")
	@RequestMapping("/auditorFAIL")
	@Lock(keys = "#applicationId", keyConstant = LockGlobal.handle_application)
	@Transactional
	public ResultVO auditorFAIL(@RequestParam("applicationId") Integer applicationId,
								@RequestParam(value = "suggest", required = false) String suggest) {
		if (EmojiFilter.containsEmoji(suggest)) {
			return ResultVO.FAIL("审批建议不能包含特殊字符");
		}
		Application application = applicationService.get(applicationId);
		if (application == null) {
			return ResultVO.FAIL("该申请单不存在");
		}
		LocalHandle.setOldObj(application);
		Integer status = application.getStatus();
		if (status > Global.APP_AUDITOR) {
			return ResultVO.FAIL("该申请单已审计过了哦");
		}

		//当前申请单处于的节点
		ApplicationNode node = service.get(application.getNodeId());

		//操作用户
		UserInfo userInfo = getUserInfo();

		//校验是否该申请单审计人
		List<ApplicationAuditor> aas = applicationAuditorService.getByApplicationAndAuditorAndNode(applicationId, userInfo.getId(), node.getId());
		if (aas == null || aas.size() == 0) {
			return ResultVO.FAIL("您无权限审计该申请单哦");
		}

		//锁定申请单
		lockApplication(applicationId);

		/*
		 * 修改 申请单-审计 状态
		 */
		for (ApplicationAuditor aa : aas) {
			//印章正在使用中，无法进入审计
			Integer deviceId = aa.getDeviceId();
			Integer online = isOnline(deviceId);//null:不在线  0:在线、关锁 1:在线、开锁
			if (online != null && online == 1) {
				throw new PrintException("当前设备正在使用中，请稍后审计");
			}

			Integer auditorId = aa.getAuditorId();

			if (auditorId == userInfo.getId().intValue()) {
				aa.setStatus(Global.AUDITOR_ERROR);
				aa.setSuggest(suggest);
				aa.setTime(new Date());
				applicationAuditorService.update(aa);
			} else {
				/*
				 * 删除其他同nodeId的节点记录
				 */
				applicationAuditorService.del(aa);
			}
		}

		/*
		 * 修改当前节点状态
		 */
		node.setIcon(Global.ICON_FAIL);
		node.setHandle(Global.HANDLE_COMPLETE);
		service.update(node);

		/*
		 * 修改申请单状态
		 */
		application.setStatus(Global.APP_AUDITOR_FAIL);
		applicationService.update(application);

		/*
		 * 修改使用记录状态
		 */
		List<Integer> sealRecordInfoIds = sealRecordInfoService.getIdsByApplication(application.getId());
		if (sealRecordInfoIds != null && sealRecordInfoIds.size() > 0) {
			for (Integer sealRecordInfoId : sealRecordInfoIds) {
				sealRecordInfoService.updateError(sealRecordInfoId, Global.ERROR);

				/*
				 * 添加异常信息
				 */
				ErrorType errorType = new ErrorType();
				errorType.setName(Global.ERROR11);
				errorType.setRemark(suggest);
				errorType.setSealRecordInfoId(sealRecordInfoId);
				errorType.setOrgId(application.getOrgId());
				errorTypeService.add(errorType);
			}
		}

		//通知申请人
		try {
			messageTempService.auditorFAILNotice(application.getTitle(), userInfo.getUserName(), application.getUserId());
		} catch (Exception e) {
			log.error("审计拒绝-发送通知异常 applicationId:{} suggest:{}", applicationId, suggest, e);
		}
		LocalHandle.setNewObj(application);
		LocalHandle.complete("审计拒绝");
		return ResultVO.OK("已审计驳回");
	}
}
