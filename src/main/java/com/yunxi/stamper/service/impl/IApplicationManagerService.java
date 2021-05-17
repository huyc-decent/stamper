package com.yunxi.stamper.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yunxi.stamper.commons.other.Global;
import com.yunxi.stamper.entityVo.ParentCode;
import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.*;
import com.yunxi.stamper.entityVo.ApplicationManagerVoSelect;
import com.yunxi.stamper.mapper.ApplicationManagerMapper;
import com.yunxi.stamper.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/4 0004 19:05
 */
@Slf4j
@Service
public class IApplicationManagerService implements ApplicationManagerService {

	@Autowired
	private ApplicationManagerMapper mapper;
	@Autowired
	private ApplicationService applicationService;
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private UserService userService;
	@Autowired
	private ApplicationKeeperService applicationKeeperService;
	@Autowired
	private MessageTempService messageTempService;
	@Autowired
	private ApplicationNodeService applicationNodeService;

	@Override
	public List<ApplicationManager> getByApplicationAndNode(Integer applicationId, Integer nodeId) {
		if (applicationId != null && nodeId != null) {
			Example example = new Example(ApplicationManager.class);
			example.createCriteria().andEqualTo("applicationId", applicationId)
					.andEqualTo("nodeId", nodeId)
					.andIsNull("deleteDate");
			example.orderBy("time");
			return mapper.selectByExample(example);
		}
		return null;
	}

	/**
	 * 查询指定申请单id+审批id+节点id 的审批记录
	 */
	@Override
	public ApplicationManager getByApplicationAndManagerAndNode(Integer applicationId, Integer userId, Integer nodeId) {
		if (applicationId != null && userId != null && nodeId != null) {
			Example example = new Example(ApplicationManager.class);
			example.createCriteria().andEqualTo("applicationId", applicationId)
					.andEqualTo("nodeId", nodeId)
					.andEqualTo("managerId", userId)
					.andIsNull("deleteDate");
			return mapper.selectOneByExample(example);
		}
		return null;
	}

	/**
	 * 创建审批节点(list、or、and)
	 *
	 * @param node         节点
	 * @param application  申请单
	 * @param managerIdStr 审批人列表
	 * @return 当前审批人
	 */
	private Integer createNodeForNormal(ApplicationNode node, Application application, String managerIdStr) {
		Integer managerId = Integer.parseInt(managerIdStr);
		User manager = userService.get(managerId);

		//创建审批记录
		ApplicationManager am = new ApplicationManager();
		am.setApplicationId(application.getId());
		am.setManagerName(manager.getUserName());
		am.setManagerId(managerId);
		am.setOrgId(application.getOrgId());
		am.setStatus(1);//审批中
		am.setNodeId(node.getId());
		add(am);
		return managerId;
	}

	@Override
	@Transactional
	public void createByNode(ApplicationNode node) {
		if (node == null) {
			throw new PrintException("审批记录创建失败,处理流程不存在");
		}
		Integer applicationId = node.getApplicationId();
		Application application = applicationService.get(applicationId);
		if (application == null) {
			throw new PrintException("审批记录创建失败,该申请单不存在或已失效");
		}
		Integer userId = application.getUserId();
		User user = userService.get(userId);
		if (user == null || user.getId() == null) {
			throw new PrintException("审批记录创建失败,申请人不存在或已注销");
		}

		String nodeManagerIds = node.getManagerIds();    //当前节点审批人id

		String nodeType = node.getNodeType();

		//依次
		if (nodeType.equalsIgnoreCase(Global.FLOW_LIST)) {
			Integer managerId = createNodeForNormal(node, application, nodeManagerIds);
			//通知审批人审批
			try {
				messageTempService.approvalNotice(application.getTitle(), user.getUserName(), managerId);
			} catch (Exception e) {
				log.error("出现异常 ", e);
			}

		}
		//或签 || 会签
		else if (nodeType.equalsIgnoreCase(Global.FLOW_AND) || nodeType.equalsIgnoreCase(Global.FLOW_OR)) {
			String[] managerids = nodeManagerIds.split(",");
			for (String managerIdStr : managerids) {
				//创建审批记录
				Integer managerId = createNodeForNormal(node, application, managerIdStr);
				//通知审批人审批
				try {
					messageTempService.approvalNotice(application.getTitle(), user.getUserName(), managerId);
				} catch (Exception e) {
					log.error("出现异常 ", e);
				}
			}
		}
		//主管
		else if (nodeType.equalsIgnoreCase(Global.FLOW_MANAGER)) {
			//查询指定层级的主管对象
			Integer departmentId = application.getDepartmentId();
			Integer level = node.getManagerLevel();
			User manager = departmentService.getManagerByDepartmentAndTopLevel(departmentId, level);

			//主管存在，创建审批记录
			if (manager != null) {
				ApplicationManager am = new ApplicationManager();
				am.setApplicationId(application.getId());
				am.setManagerName(manager.getUserName());
				am.setManagerId(manager.getId());
				am.setOrgId(application.getOrgId());
				am.setStatus(Global.MANAGER_HANDLING);
				am.setNodeId(node.getId());
				add(am);

				application.setStatus(Global.APP_MANAGER);

				//通知审批人审批
				try {
					messageTempService.approvalNotice(application.getTitle(), user.getUserName(), manager.getId());
				} catch (Exception e) {
					log.error("出现异常 ", e);
				}
			}
			/*
				默认情况下，主管不存在时，该节点会自动通过，进入下1个审批节点
				但是有可能会出现下1个节点是'自选'模式，导致流程无法进行下去，
				因此当检测到下一个节点是审批自选节点时，当前节点不允许自动通过，将向上递增查找审批人审批
			 */
			else {
				//下一个节点
				ApplicationNode nextNode = applicationNodeService.getByApplicationAndOrderNo(applicationId, node.getOrderNo() + 1);
				String nextNodeName = nextNode.getName();
				String nextNodeType = nextNode.getNodeType();

				/*
					下个节点是'审批'节点 & '自选'模式,当前节点不允许'自动通过'
					将当前节点转交给其他的审批人审批
				 */
				if (nextNodeName.equals(Global.NODE_NAME_BY_MANAGER) && nextNodeType.equals(Global.FLOW_OPTIONAL)) {

					//从当前层级依次递增查找审批人，直到属主为止
					User targetManager = findManager(departmentId, level);

					//修改当前节点状态
					node.setManagerIds((level == 1 ? "直接主管" : level + "级主管") + "不存在,已转交:" + targetManager.getUserName());
					node.setDepartmentId(departmentId);
					node.setHandle(Global.HANDLE_ING);
					node.setIcon(Global.ICON_WAIT);
					applicationNodeService.update(node);

					//managerTrans(application, node, targetManager);

					//创建审批记录
					ApplicationManager am = new ApplicationManager();
					am.setApplicationId(application.getId());
					am.setManagerName(targetManager.getUserName());
					am.setManagerId(targetManager.getId());
					am.setOrgId(application.getOrgId());
					am.setStatus(Global.MANAGER_HANDLING);
					am.setNodeId(node.getId());
					add(am);

					//修改申请单状态
					application.setStatus(Global.APP_MANAGER);
					application.setNodeId(node.getId());
					applicationService.update(application);

					//通知审批人审批
					try {
						messageTempService.approvalNotice(application.getTitle(), user.getUserName(), targetManager.getId());
					} catch (Exception e) {
						log.error("出现异常 ", e);
					}

					//为查找到的审批人创建审批记录
//					ApplicationManager am = new ApplicationManager();
//					am.setApplicationId(application.getId());
//					am.setManagerName(targetManager.getUserName());
//					am.setManagerId(targetManager.getId());
//					am.setOrgId(application.getOrgId());
//					am.setStatus(Global.MANAGER_HANDLING);
//					am.setNodeId(node.getId());
//					add(am);

					//修改申请状态
//					application.setStatus(Global.APP_KEEPER);
//					application.setNodeId(nextNode.getId());
//					applicationService.update(application);

					//通知审批人审批
//					try {
//						messageTempService.approvalNotice(application.getTitle(), user.getUserName(), targetManager.getId());
//					} catch (Exception e) {
//						log.error("出现异常 ", e);
//					}

					return;
				}

				ApplicationManager am = new ApplicationManager();
				am.setApplicationId(application.getId());
				am.setManagerName(null);
				am.setManagerId(null);
				am.setOrgId(application.getOrgId());
				am.setStatus(Global.MANAGER_SUCCESS);
				am.setNodeId(node.getId());
				add(am);

				//修改当前节点状态
				node.setDepartmentId(departmentId);
				node.setManagerIds((level == 1 ? "直接主管" : level + "级主管") + "不存在,默认通过");
				node.setHandle(Global.HANDLE_COMPLETE);
				node.setIcon(Global.ICON_SUCCESS);
				applicationNodeService.update(node);

				//修改申请状态
				application.setStatus(Global.APP_KEEPER);
				application.setNodeId(nextNode.getId());
				applicationService.update(application);
				return;
			}
		} else {
			throw new PrintException("申请单处理流程类型[" + nodeType + "]异常");
		}

		//修改当前节点状态
		node.setHandle(Global.HANDLE_ING);
		applicationNodeService.update(node);

		//修改申请单状态
		application.setStatus(Global.APP_MANAGER);
		application.setNodeId(node.getId());
		applicationService.update(application);
	}

	private void managerTrans(Application application, ApplicationNode node, User targetManager) {
		/*
		 * 将当前节点(包含当前)之后的所有节点order值加1
		 */
		List<ApplicationNode> nodes = applicationNodeService.getByApplicationAndGreaterThanOrderNo(node.getApplicationId(), node.getOrderNo() + 1);
		for (ApplicationNode n : nodes) {
			n.setOrderNo(n.getOrderNo() + 1);
			applicationNodeService.update(n);
		}

		/*
		 * 在下一个节点位置插入新的节点
		 */
		ApplicationNode managerNode = new ApplicationNode();
		managerNode.setApplicationId(application.getId());
		managerNode.setHandle(Global.HANDLE_ING);//处理中
		managerNode.setTitle("审批人");
		managerNode.setManagerIds(targetManager.getId() + "");
		managerNode.setName("审批");
		managerNode.setOrderNo(node.getOrderNo() + 1);
		managerNode.setNodeType("list");
		managerNode.setIcon(Global.ICON_WAIT);
		applicationNodeService.add(managerNode);

		//修改申请状态
		application.setNodeId(managerNode.getId());
		applicationService.update(application);
	}

	/**
	 * 查询指定组织的父节点主管人
	 */
	private User findManager(Integer departmentId, Integer level) {
		List<User> managers = departmentService.getManagersByDepartmentToTopLevel(departmentId, level);
		User targetManager = null;
		if (managers != null && !managers.isEmpty()) {
			//如果主管列表不为空,则递交给最高级并且存在的主管
			for (int i = managers.size() - 1; i >= 0; i--) {
				User tempManager = managers.get(i);
				if (tempManager != null && tempManager.getId() != null) {
					targetManager = tempManager;
					break;
				}
			}
		}

		//如果主管列表为空,则查询该组织父层级的主管人员
		if (targetManager == null) {
			Department department = departmentService.get(departmentId);
			String parentCode = department.getParentCode();
			List<ParentCode> parentCodes = JSONObject.parseArray(parentCode, ParentCode.class);
			for (int i = parentCodes.size() - 1; i >= 0; i--) {
				ParentCode code = parentCodes.get(i);
				int parentId = code.getId();
				Department parent = departmentService.get(parentId);
				Integer managerUserId = parent.getManagerUserId();
				if (managerUserId != null) {
					targetManager = userService.get(managerUserId);
				}
			}
		}

		//如果主管还是空，则直接提交给属主
		if (targetManager == null) {
			targetManager = userService.getOwnerByDeparmtent(departmentId);
		}
		return targetManager;
	}
//    @Override
//    @Transactional
//    public void createByNode(ApplicationNode node) {
//        if (node == null) {
//            throw new PrintException("审批记录创建失败,处理流程不存在");
//        }
//        Integer applicationId = node.getApplicationId();
//        Application application = applicationService.get(applicationId);
//        if (application == null) {
//            throw new PrintException("审批记录创建失败,该申请单不存在或已失效");
//        }
//        Integer userId = application.getUserId();
//        User user = userService.get(userId);
//        if (user == null || user.getId() == null) {
//            throw new PrintException("审批记录创建失败,申请人不存在或已注销");
//        }
//
//        String nodeManagerIds = node.getManagerIds();    //当前节点审批人id
//
//        String nodeType = node.getNodeType();
//
//        if (nodeType.equalsIgnoreCase(Global.FLOW_LIST)) {
//            Integer managerId = Integer.parseInt(nodeManagerIds);
//            User manager = userService.get(managerId);
//
//            //创建审批记录
//            ApplicationManager am = new ApplicationManager();
//            am.setApplicationId(application.getId());
//            am.setManagerName(manager.getUserName());
//            am.setManagerId(managerId);
//            am.setOrgId(application.getOrgId());
//            am.setStatus(1);//审批中
//            am.setNodeId(node.getId());
//            add(am);
//
//            //通知审批人审批
//            try {
//                messageTempService.approvalNotice(application.getTitle(), user.getUserName(), managerId);
//            } catch (Exception e) {
//                log.error("出现异常 ", e);
//            }
//
//        } else if (nodeType.equalsIgnoreCase(Global.FLOW_AND) || nodeType.equalsIgnoreCase(Global.FLOW_OR)) {
//            String[] managerids = nodeManagerIds.split(",");
//
//            //创建审批记录
//            for (String managerIdStr : managerids) {
//                Integer managerId = Integer.parseInt(managerIdStr);
//                User manager = userService.get(managerId);
//
//                //创建审批记录
//                ApplicationManager am = new ApplicationManager();
//                am.setApplicationId(application.getId());
//                am.setManagerName(manager.getUserName());
//                am.setManagerId(managerId);
//                am.setOrgId(application.getOrgId());
//                am.setStatus(1);//审批中
//                am.setNodeId(node.getId());
//                add(am);
//
//                //通知审批人审批
//                try {
//                    messageTempService.approvalNotice(application.getTitle(), user.getUserName(), managerId);
//                } catch (Exception e) {
//                    log.error("出现异常 ", e);
//                }
//            }
//
//        } else if (nodeType.equalsIgnoreCase(Global.FLOW_MANAGER)) {
//            //查询指定层级的主管对象
//            Integer departmentId = application.getDepartmentId();
//            //当前节点主管层级
//            Integer level = node.getManagerLevel();
//            User manager = departmentService.getManagerByDepartmentAndTopLevel(departmentId, level);
//            if (manager != null) {
//                ApplicationManager am = new ApplicationManager();
//                am.setApplicationId(application.getId());
//                am.setManagerName(manager.getUserName());
//                am.setManagerId(manager.getId());
//                am.setOrgId(application.getOrgId());
//                am.setStatus(Global.AUDITOR_HANDLING);
//                am.setNodeId(node.getId());
//                add(am);
//
//                application.setStatus(Global.APP_MANAGER);
//
//                //通知审批人审批
//                try {
//                    messageTempService.approvalNotice(application.getTitle(), user.getUserName(), manager.getId());
//                } catch (Exception e) {
//                    log.error("出现异常 ", e);
//                }
//            } else {
//                /*
//                 * 主管不存在，默认通过
//                 */
//                ApplicationManager am = new ApplicationManager();
//                am.setApplicationId(application.getId());
//                am.setManagerName(null);
//                am.setManagerId(null);
//                am.setOrgId(application.getOrgId());
//                am.setStatus(Global.MANAGER_SUCCESS);
//                am.setNodeId(node.getId());
//                add(am);
//
//                //修改当前节点状态
//                node.setDepartmentId(departmentId);
//                node.setManagerIds((level == 1 ? "直接主管" : level + "级主管") + "不存在,默认通过");
//                node.setHandle(Global.HANDLE_COMPLETE);
//                node.setIcon(Global.ICON_SUCCESS);
//                applicationNodeService.update(node);
//
//                //查询下一个节点
//                Integer orderNo = node.getOrderNo();
//                ApplicationNode nextNode = applicationNodeService.getByApplicationAndOrderNo(applicationId, orderNo + 1);
//                if (nextNode != null) {
//
//                    String name = nextNode.getName();
//                    if ("审批".equalsIgnoreCase(name)) {
//
//                        String nextNodeType = nextNode.getNodeType();
//                        if (Global.FLOW_OPTIONAL.equalsIgnoreCase(nextNodeType)) {
//                            throw new PrintException("该申请单数据有误");
//                        } else {
//                            createByNode(nextNode);
//                        }
//
//                    } else if ("授权".equalsIgnoreCase(name)) {
//
//                        /*
//                         * 修改申请状态
//                         */
//                        application.setStatus(Global.APP_KEEPER);
//                        application.setNodeId(nextNode.getId());
//                        applicationService.update(application);
//
//                        applicationKeeperService.createByNode(nextNode);
//
//                    } else {
//                        throw new PrintException("该申请单数据有误");
//                    }
//
//                    return;
//                } else {
//                    throw new PrintException("该申请单审批节点有误");
//                }
//            }
//        } else {
//            throw new PrintException("申请单处理流程类型[" + nodeType + "]异常");
//        }
//
//        /*
//         * 修改当前节点状态
//         */
//        node.setHandle(Global.HANDLE_ING);
//        applicationNodeService.update(node);
//
//        /*
//         * 修改申请单状态
//         */
//        application.setStatus(Global.APP_MANAGER);
//        application.setNodeId(node.getId());
//        applicationService.update(application);
//    }

	/**
	 * 查询指定申请单+审批人 正在处理的审批记录
	 */
	@Override
	public ApplicationManager getByApplicationAndManagerAndDealing(Integer applicationId, Integer userId) {
		if (applicationId != null && userId != null) {
			return mapper.selectByApplicationAndManagerAndDealing(applicationId, userId);
		}
		return null;
	}

	/**
	 * 查询申请单的审批流程,按审批顺序由小到大
	 */
	@Override
	public List<ApplicationManager> getByApplicationId(Integer applicationId) {
		if (applicationId != null) {
			Example example = new Example(ApplicationManager.class);
			example.createCriteria().andEqualTo("applicationId", applicationId)
					.andIsNull("deleteDate");
			example.orderBy("time");
			return mapper.selectByExample(example);
		}
		return null;
	}

	/**
	 * 查询申请单审批流程
	 */
	@Override
	public List<ApplicationManagerVoSelect> getByApplication(Integer applicationId) {
		if (applicationId != null) {
			return mapper.selectByApplication(applicationId);
		}
		return null;
	}

	@Override
	@Transactional
	public void del(ApplicationManager am) {
		int delCount = 0;
		if (am != null && am.getId() != null) {
			am.setDeleteDate(new Date());
			delCount = mapper.updateByPrimaryKey(am);
		}
		if (delCount != 1) {
			throw new PrintException("审批记录删除操作失败");
		}
	}

	@Override
	@Transactional
	public void update(ApplicationManager manager) {
		mapper.updateByPrimaryKey(manager);
	}

	@Override
	@Transactional
	public void add(ApplicationManager am) {
		int addCount = 0;
		if (am != null) {
			am.setCreateDate(new Date());
			addCount = mapper.insert(am);
		}
		if (addCount != 1) {
			throw new PrintException("申请单审批记录关联失败");
		}
	}

	/**
	 * 创建下一级'自选审批'
	 *
	 * @param node      要创建的节点
	 * @param managerId 下级一个自选审批人ID
	 */
	@Override
	public void createByNodeByOptional(ApplicationNode node, Integer managerId) {
		if (node == null) {
			throw new PrintException("审批记录创建失败,处理流程不存在");
		}
		if (managerId == null) {
			throw new PrintException("未指定下一节点审批人");
		}
		Integer applicationId = node.getApplicationId();
		Application application = applicationService.get(applicationId);
		if (application == null) {
			throw new PrintException("审批记录创建失败,该申请单不存在或已失效");
		}
		Integer userId = application.getUserId();
		User user = userService.get(userId);
		if (user == null || user.getId() == null) {
			throw new PrintException("审批记录创建失败,申请人不存在或已注销");
		}

		User manager = userService.get(managerId);
		if (manager == null || manager.getId() == null) {
			throw new PrintException("指定的审批人不存在");
		}

		//创建审批记录
		ApplicationManager am = new ApplicationManager();
		am.setApplicationId(application.getId());
		am.setManagerName(manager.getUserName());
		am.setManagerId(managerId);
		am.setOrgId(application.getOrgId());
		am.setStatus(1);//审批中
		am.setNodeId(node.getId());
		add(am);

		//通知审批人审批
		try {
			messageTempService.approvalNotice(application.getTitle(), user.getUserName(), managerId);
		} catch (Exception e) {
			log.error("出现异常 ", e);
		}

		/*
		 * 修改当前节点状态
		 */
		node.setHandle(Global.HANDLE_ING);
		applicationNodeService.update(node);

		/*
		 * 修改申请单节点id
		 */
		application.setNodeId(node.getId());
		applicationService.update(application);
	}
}
