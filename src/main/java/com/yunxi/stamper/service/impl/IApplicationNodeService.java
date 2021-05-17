package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.entity.*;
import com.yunxi.stamper.logger.threadLocal.LocalHandle;
import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.other.DateUtil;
import com.yunxi.stamper.commons.other.Global;
import com.yunxi.stamper.commons.other.RedisGlobal;
import com.yunxi.stamper.entityVo.ApplicationNodeVo;
import com.yunxi.stamper.entityVo.UserInfo;
import com.yunxi.stamper.mapper.ApplicationNodeMapper;
import com.yunxi.stamper.service.*;
import com.yunxi.stamper.base.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/6/19 0019 14:31
 */
@Slf4j
@Service
public class IApplicationNodeService extends BaseService implements ApplicationNodeService {
	@Autowired
	private ApplicationNodeMapper mapper;
	@Autowired
	private ApplicationManagerService applicationManagerService;
	@Autowired
	private ApplicationService applicationService;
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private MessageTempService messageTempService;
	@Autowired
	private OrgService orgService;

	@Override
	@Transactional
	public void add(ApplicationNode applicationNode) {
		int addCount = 0;
		if (applicationNode != null) {
			applicationNode.setCreateDate(new Date());
			addCount = mapper.insert(applicationNode);
		}
		if (addCount != 1) {
			throw new PrintException("审批流程处理节点创建失败");
		}
	}


	@Override
	@Transactional
	public void update(ApplicationNode node) {
		int updateCount = 0;
		if (node != null) {
			node.setUpdateDate(new Date());
			updateCount = mapper.updateByPrimaryKey(node);
		}
		if (updateCount != 1) {
			throw new PrintException("审批流程处理节点更新失败");
		}
	}

	@Override
	@Transactional
	public void del(ApplicationNode node) {
		int delCount = 0;
		if (node != null) {
			delCount = mapper.delete(node);
		}
		if (delCount != 1) {
			throw new PrintException("流程节点删除失败");
		}
	}


	@Override
	public ApplicationNode get(Integer nodeId) {
		if (nodeId == null) {
			return null;
		}
		return mapper.selectByPrimaryKey(nodeId);
	}


	@Override
	public ApplicationNode getByApplicationAndOrderNo(Integer applicationId, Integer orderNo) {
		if (applicationId == null || orderNo == null) {
			return null;
		}
		do {
			ApplicationNode node = getByNextNode(applicationId, orderNo);
			if (node != null) {
				return node;
			}
			orderNo++;
		} while (orderNo <= 15);

		return null;
	}


	//查询指定节点
	private ApplicationNode getByNextNode(Integer applicationId, Integer orderNo) {
		Example example = new Example(ApplicationNode.class);
		example.createCriteria().andIsNull("deleteDate")
				.andEqualTo("applicationId", applicationId)
				.andEqualTo("orderNo", orderNo);
		return mapper.selectOneByExample(example);
	}


	@Override
	public List<ApplicationNodeVo> getByApplication(Integer applicationId) {
		if (applicationId == null) {
			return null;
		}
		return mapper.selectByApplication(applicationId);
	}


	@Override
	public List<ApplicationNode> getByApplicationAndGreaterThanOrderNo(Integer applicationId, Integer orderNo) {
		if (applicationId == null || orderNo == null) {
			return null;
		}
		Example example = new Example(ApplicationNode.class);
		example.createCriteria().andIsNull("deleteDate")
				.andEqualTo("applicationId", applicationId)
				.andGreaterThanOrEqualTo("orderNo", orderNo);
		return mapper.selectByExample(example);
	}


	/**
	 * 查询指定申请单+指定状态的节点
	 */
	@Override
	public List<ApplicationNode> getByApplicationAndHandle(Integer applicationId, Integer handle) {
		if (applicationId == null || handle == null) {
			return null;
		}
		Example example = new Example(ApplicationNode.class);
		example.createCriteria().andIsNull("deleteDate")
				.andEqualTo("applicationId", applicationId)
				.andEqualTo("handle", handle);
		return mapper.selectByExample(example);
	}


	/**
	 * 查询指定orderNo的下一个节点
	 */
	@Override
	public Integer getNextNode(Integer applicationId, Integer orderNo) {
		if (applicationId == null || orderNo == null) {
			return null;
		}
		return mapper.selectByNextNode(applicationId, orderNo);
	}


	/**
	 * 查询申请单的审批人ID列表
	 *
	 * @param applicationId 申请单ID
	 * @return 结果
	 */
	@Override
	public List<Integer> getManagersByApplication(Integer applicationId) {
		Application application = applicationService.get(applicationId);
		if (application == null) {
			return null;
		}
		List<ApplicationNodeVo> nodes = getByApplication(applicationId);
		if (nodes == null || nodes.isEmpty()) {
			return null;
		}
		Set<Integer> managerIds = new HashSet<>();
		for (ApplicationNodeVo node : nodes) {
			String name = node.getName();
			if (!"审批".equalsIgnoreCase(name)) {
				continue;
			}

			String nodeType = node.getNodeType();
			if (Global.FLOW_MANAGER.equalsIgnoreCase(nodeType)) {
				User manager = departmentService.getManagerByDepartmentAndTopLevel(application.getDepartmentId(), node.getManagerLevel());
				if (manager == null) {
					continue;
				}
				managerIds.add(manager.getId());
			} else {
				String nodeManagerIds = node.getManagerIds();
				List<Integer> userIds = CommonUtils.splitToInteger(nodeManagerIds, ",");
				if (userIds == null || userIds.isEmpty()) {
					continue;
				}
				managerIds.addAll(userIds);
			}
		}

		return new ArrayList<>(managerIds);
	}

	/**
	 * 锁定指定申请单id
	 */
	private void lockApplication(Integer applicationId) {
		String key = RedisGlobal.LOCK_APPLICATION + applicationId;
		Object obj = redisUtil.get(key);
		if (obj != null) {
			throw new PrintException("申请单正在被处理,请稍后重试");
		}
		redisUtil.set(key, DateUtil.format(new Date()), RedisGlobal.LOCK_APPLICATION_TIMEOUT);
	}


	/**
	 * 审批转交
	 *
	 * @param userInfo    操作人
	 * @param pushUser    被转交人
	 * @param application 申请单
	 * @param suggest     意见
	 * @param node        当前流程节点
	 */
	@Override
	@Transactional
	public void managerTrans(UserInfo userInfo, User pushUser, Application application, String suggest, ApplicationNode node) {
		//锁定申请单
		lockApplication(application.getId());

		/*
		 * 转交相当于该审批人同意,在下一个orderNo处,添加一个审批节点
		 */
		String nodeType = node.getNodeType();

		if ("list".equalsIgnoreCase(nodeType) || "manager".equalsIgnoreCase(nodeType) || nodeType.equalsIgnoreCase(Global.FLOW_OPTIONAL)) {//当前节点只有1个审批人的情况
			/*
			 * 更新 申请单-审批 记录状态
			 */
			ApplicationManager am = applicationManagerService.getByApplicationAndManagerAndNode(application.getId(), userInfo.getId(), node.getId());
			am.setStatus(Global.MANAGER_TRANS);
			am.setPushUserId(pushUser.getId());
			am.setSuggest(suggest);
			am.setPushUserName(pushUser.getUserName());
			am.setTime(new Date());
			applicationManagerService.update(am);

			/*
			 * 更新当前节点状态
			 */
			node.setHandle(Global.HANDLE_COMPLETE);
			node.setIcon(Global.ICON_TRANS);
			update(node);

			/*
			 * 将当前节点(包含当前)之后的所有节点order值加1
			 */
			List<ApplicationNode> nodes = getByApplicationAndGreaterThanOrderNo(node.getApplicationId(), node.getOrderNo() + 1);
			for (ApplicationNode n : nodes) {
				n.setOrderNo(n.getOrderNo() + 1);
				update(n);
			}

			/*
			 * 在下一个节点位置插入新的节点
			 */
			ApplicationNode managerNode = new ApplicationNode();
			managerNode.setApplicationId(application.getId());
			managerNode.setHandle(Global.HANDLE_ING);//处理中
			managerNode.setTitle("审批人");
			managerNode.setManagerIds(pushUser.getId() + "");
			managerNode.setName("审批");
			managerNode.setOrderNo(node.getOrderNo() + 1);
			managerNode.setNodeType("list");
			managerNode.setIcon(Global.ICON_WAIT);
			add(managerNode);

			/*
			 * 创建审批记录
			 */
			applicationManagerService.createByNode(managerNode);

			/*
			 * 修改申请单状态
			 */
			application.setNodeId(managerNode.getId());
			applicationService.update(application);
		} else if (nodeType.equalsIgnoreCase(Global.FLOW_AND) || nodeType.equalsIgnoreCase(Global.FLOW_OR)) {//当前节点有多人审批的情况
			/*
			 * 更新 当前审批人的 申请单-审批 记录状态
			 */
			ApplicationManager am = applicationManagerService.getByApplicationAndManagerAndNode(application.getId(), userInfo.getId(), node.getId());
			am.setStatus(Global.MANAGER_TRANS);
			am.setPushUserId(pushUser.getId());
			am.setPushUserName(pushUser.getUserName());
			am.setTime(new Date());
			applicationManagerService.update(am);

			/*
			 * 在当前节点中，添加新审批人ID
			 */
			String nodeManagerIds = node.getManagerIds();
			node.setManagerIds(nodeManagerIds + "," + pushUser.getId());
			update(node);

			/*
			 * 创建被转交人的审批记录
			 */
			ApplicationManager pushAM = new ApplicationManager();
			pushAM.setNodeId(node.getId());
			pushAM.setManagerId(pushUser.getId());
			pushAM.setManagerName(pushUser.getUserName());
			pushAM.setFromUserId(userInfo.getId());
			pushAM.setStatus(Global.MANAGER_HANDLING);
			pushAM.setApplicationId(application.getId());
			pushAM.setOrgId(application.getOrgId());
			applicationManagerService.add(pushAM);
		} else {
			throw new PrintException("当前审批节点类型有误");
		}

		/*
		 * 发送通知、短信
		 */
		try {
			Integer orgId = userInfo.getOrgId();
			Org org = orgService.get(orgId);
			String orgName = org.getName();
			messageTempService.transferManagerNotice(orgName, userInfo.getUserName(), pushUser.getId());
		} catch (Exception e) {
			log.error("出现异常 ", e);
		}

		try {
			messageTempService.approvalTransferNotice(application.getTitle(), userInfo.getUserName(), pushUser.getUserName(), application.getUserId());
		} catch (Exception e) {
			log.error("出现异常 ", e);
		}

		LocalHandle.setNewObj(application);
	}


	/**
	 * 查询设备未处理的节点记录列表
	 *
	 * @param orgId       集团ID
	 * @param deviceId    设备ID
	 * @param oldKeeperId 节点名称
	 * @return 结果
	 */
	@Override
	public List<ApplicationNode> getNoKeeperHandleByOrgAndDeviceAndOldKeeperId(Integer orgId, Integer deviceId, Integer oldKeeperId) {
		if (orgId == null || deviceId == null) {
			return null;
		}
		return mapper.selectNoKeeperHandleByOrgAndDeviceAndOldKeeperId(orgId, deviceId, oldKeeperId);
	}


	/**
	 * 查询设备未处理的节点记录列表
	 *
	 * @param orgId        集团ID
	 * @param deviceId     设备ID
	 * @param oldAuditorId 节点名称
	 * @return 结果
	 */
	@Override
	public List<ApplicationNode> getNoAuditorHandleByOrgAndDeviceAndOldAuditorId(Integer orgId, Integer deviceId, Integer oldAuditorId) {
		if (orgId == null || deviceId == null) {
			return null;
		}
		return mapper.selectNoAuditorHandleByOrgAndDeviceAndOldAuditorId(orgId, deviceId, oldAuditorId);
	}

}
