package com.yunxi.stamper.service.impl;


import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.other.Global;
import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.*;
import com.yunxi.stamper.mapper.ApplicationAuditorMapper;
import com.yunxi.stamper.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/5 0005 17:16
 */
@Service
public class IApplicationAuditorService implements ApplicationAuditorService {

	@Autowired
	private ApplicationAuditorMapper mapper;
	@Autowired
	private ApplicationService applicationService;
	@Autowired
	private UserService userService;
	@Autowired
	private ApplicationDeviceService applicationDeviceService;
	@Autowired
	private SignetService signetService;
	@Autowired
	private ApplicationNodeService applicationNodeService;

	@Override
	public List<ApplicationAuditor> getByApplicationAndNode(Integer applicationId, Integer nodeId) {
		if (applicationId != null && nodeId != null) {
			Example example = new Example(ApplicationAuditor.class);
			example.createCriteria()
					.andEqualTo("nodeId", nodeId)
					.andEqualTo("applicationId", applicationId)
					.andIsNull("deleteDate");
			return mapper.selectByExample(example);
		}
		return null;
	}

	@Override
	public List<ApplicationAuditor> getByApplicationAndAuditorAndNode(Integer applicationId, Integer userId, Integer nodeId) {
		if (applicationId != null && userId != null && nodeId != null) {
			Example example = new Example(ApplicationAuditor.class);
			example.createCriteria()
					.andEqualTo("nodeId", nodeId)
					.andEqualTo("auditorId", userId)
					.andEqualTo("applicationId", applicationId)
					.andIsNull("deleteDate");
			return mapper.selectByExample(example);
		}
		return null;
	}

	@Override
	@Transactional
	public void createByNode(ApplicationNode node) {
		if (node == null) {
			throw new PrintException("授权记录创建失败,处理流程不存在");
		}

		/*
		 * 校验申请单有效性
		 */
		Integer applicationId = node.getApplicationId();
		Application application = applicationService.get(applicationId);
		if (application == null || application.getStatus() == 13) {
			throw new PrintException("授权记录创建失败,该申请单不存在或已取消");
		}

		/*
		 * 校验申请人有效性
		 */
		Integer userId = application.getUserId();
		User user = userService.get(userId);
		if (user == null || user.getId() == null) {
			throw new PrintException("授权记录创建失败,申请人不存在或已注销");
		}


		/*
		 * 校验 设备,审计人 有效性
		 */
		List<Signet> signets = new LinkedList<>();
		List<User> auditors = new LinkedList<>();
		List<ApplicationDevice> ads = applicationDeviceService.getByApplication(applicationId);
		if (ads == null || ads.size() == 0) {
			throw new PrintException("该申请单关联设备数据记录异常");
		}
		for (ApplicationDevice ad : ads) {
			Integer deviceId = ad.getDeviceId();
			Signet signet = signetService.get(deviceId);
			if (signet == null || signet.getId() == null) {
				throw new PrintException("该申请单关联设备不存在或已被删除");
			}
			signets.add(signet);

			Integer auditorId = signet.getAuditorId();
			if (auditorId == null) {
				throw new PrintException("设备[" + signet.getName() + "]审计人不存在");
			}
			User auditor = userService.get(auditorId);
			if (auditor == null || auditor.getId() == null) {
				throw new PrintException("设备[" + signet.getName() + "]审计人不存在");
			}
			auditors.add(auditor);
		}

		//授权方式
		String nodeType = node.getNodeType();

		/*
		 * 单个设备
		 */
		if ("list".equalsIgnoreCase(nodeType)) {
			//该节点只有一个审计人

			Signet signet = signets.get(0);
			User auditor = auditors.get(0);

			//创建审计记录
			ApplicationAuditor aa = new ApplicationAuditor();
			aa.setOrgId(application.getOrgId());
			aa.setApplicationId(applicationId);
			aa.setNodeId(node.getId());
			aa.setDeviceId(signet.getId());
			aa.setDeviceName(signet.getName());
			aa.setAuditorId(auditor.getId());
			aa.setAuditorName(auditor.getUserName());
			aa.setStatus(Global.AUDITOR_HANDLING);
			add(aa);
		}
		/*
		 * 多个设备
		 */
		else if ("and".equalsIgnoreCase(nodeType)) {
			//该节点可能有多个授权人

			for (int i = 0; i < signets.size(); i++) {
				Signet signet = signets.get(i);
				User auditor = auditors.get(i);

				//创建授权记录
				ApplicationAuditor aa = new ApplicationAuditor();
				aa.setOrgId(application.getOrgId());
				aa.setApplicationId(applicationId);
				aa.setNodeId(node.getId());
				aa.setDeviceId(signet.getId());
				aa.setDeviceName(signet.getName());
				aa.setAuditorId(auditor.getId());
				aa.setAuditorName(auditor.getUserName());
				aa.setStatus(Global.AUDITOR_HANDLING);
				add(aa);
			}
		}

		//修改该节点状态
		node.setHandle(Global.HANDLE_ING);
		applicationNodeService.update(node);

		//修改申请单绑定的节点
		application.setNodeId(node.getId());
		applicationService.update(application);
	}

	@Override
	@Transactional
	public void update(ApplicationAuditor aa) {
		int updateCount = 0;
		if (aa != null) {
			aa.setUpdateDate(new Date());
			updateCount = mapper.updateByPrimaryKey(aa);
		}
		if (updateCount != 1) {
			throw new PrintException("审计记录更新失败");
		}
	}

	@Override
	@Transactional
	public void add(ApplicationAuditor aa) {
		int addCount = 0;
		if (aa != null) {
			aa.setCreateDate(new Date());
			addCount = mapper.insert(aa);
		}
		if (addCount != 1) {
			throw new PrintException("审计记录创建失败");
		}
	}

	@Override
	@Transactional
	public void del(ApplicationAuditor aa) {
		int delCount = 0;
		if (aa != null && aa.getId() != null) {
			delCount = mapper.deleteByPrimaryKey(aa);
		}
		if (delCount != 1) {
			throw new PrintException("审计记录删除失败");
		}
	}

	/**
	 * 查询印章对应的审计记录列表(未处理完成)
	 *
	 * @param signetId 设备ID
	 * @return 结果
	 */
	@Override
	public List<ApplicationAuditor> getBySignet(Integer signetId, Integer signetOrgId) {
		if (signetId != null) {
			Example example = new Example(ApplicationAuditor.class);
			example.createCriteria()
					.andEqualTo("status", Global.AUDITOR_HANDLING)
					.andEqualTo("deviceId", signetId)
					.andEqualTo("orgId", signetOrgId)
					.andIsNull("deleteDate");
			return mapper.selectByExample(example);
		}
		return null;
	}

	@Override
	public List<ApplicationAuditor> getByApplication(Integer applicationId) {
		if (applicationId != null) {
			Example example = new Example(ApplicationAuditor.class);
			example.createCriteria().andIsNull("deleteDate")
					.andEqualTo("applicationId", applicationId);
			return mapper.selectByExample(example);
		}
		return null;
	}

	/**
	 * 查询指定申请单id 审计人id
	 *
	 * @param applicationId 申请单ID
	 * @param auditorId     审计员ID
	 * @return 结果
	 */
	@Override
	public List<ApplicationAuditor> getByApplicationAndAuditor(Integer applicationId, Integer auditorId) {
		if (applicationId != null && auditorId != null) {
			Example example = new Example(ApplicationAuditor.class);
			example.createCriteria().andEqualTo("applicationId", applicationId)
					.andEqualTo("auditorId", auditorId)
					.andIsNull("deleteDate");
			return mapper.selectByExample(example);
		}
		return null;
	}

	/**
	 * 设备管理员发生变更审计记录
	 *
	 * @param signet  印章
	 * @param auditor 新审计员
	 */
	@Override
	@Transactional
	public void updateFromSignetDate(@NotNull Signet signet, @NotNull Integer oldAuditorId, @NotNull User auditor) {
		/*
		 * 查询关于该印章未处理的审计记录列表
		 */
		List<ApplicationNode> noHandledNodes = applicationNodeService.getNoAuditorHandleByOrgAndDeviceAndOldAuditorId(signet.getOrgId(), signet.getId(), oldAuditorId);
		if (noHandledNodes != null && noHandledNodes.size() > 0) {
			for (ApplicationNode node : noHandledNodes) {
				String nodeType = node.getNodeType();
				String nodeManagerIds = node.getManagerIds();

				if (Global.FLOW_LIST.equalsIgnoreCase(nodeType)) {
					if (nodeManagerIds.equalsIgnoreCase(oldAuditorId + "")) {
						nodeManagerIds = auditor.getId() + "";

						node.setManagerIds(nodeManagerIds);
						applicationNodeService.update(node);
					}
				} else if (Global.FLOW_OR.equalsIgnoreCase(nodeType) || Global.FLOW_AND.equalsIgnoreCase(nodeType) || Global.FLOW_OPTIONAL.equalsIgnoreCase(nodeType)) {
					List<Integer> managerIds = CommonUtils.splitToInteger(nodeManagerIds, ",");
					if (managerIds != null && managerIds.size() > 0 && managerIds.contains(oldAuditorId)) {
						managerIds.remove(oldAuditorId);
						managerIds.add(auditor.getId());
						nodeManagerIds = CommonUtils.listToString(managerIds);

						node.setManagerIds(nodeManagerIds);
						applicationNodeService.update(node);
					}
				}
			}
		}

		/*
		 * 查询关于该印章的审计记录列表(不包含已处理)
		 */
		List<ApplicationAuditor> aas = mapper.selectByOrgAndDeviceAndDealing(signet.getOrgId(), signet.getId());
		if (aas == null || aas.isEmpty()) {
			return;
		}

		for (ApplicationAuditor aa : aas) {
			Integer auditorId = aa.getAuditorId();

			/*
			 * 原审计员和新审计员相同，不处理
			 */
			if (CommonUtils.isEquals(auditorId, auditor.getId())) {
				continue;
			}

			/*
			 * 删除原审计记录信息
			 */
			del(aa);

			/*
			 * 创建新审计记录信息
			 */
			aa.setId(null);
			aa.setAuditorId(auditor.getId());
			aa.setAuditorName(auditor.getUserName());
			aa.setUpdateDate(null);
			add(aa);

			/*
			 * 查询该审计记录相关节点信息
			 */
			Integer nodeId = aa.getNodeId();
			ApplicationNode node = applicationNodeService.get(nodeId);

			/*
			 * 不处理场景：
			 * 1.节点不存在
			 * 2.节点状态不存在
			 * 3.节点已完成
			 * 4.节点非审计记录
			 */
			if (node == null
					|| node.getHandle() == null
					|| node.getHandle().intValue() == Global.HANDLE_COMPLETE
					|| !"审计".equalsIgnoreCase(node.getName())) {
				return;
			}

			/*
			 * 更新节点信息，审计节点仅有1个审计人
			 */
			node.setManagerIds(auditor.getId() + "");
			applicationNodeService.update(node);

			//TODO:向新的审计员发送短信、通知提醒
		}
	}
}
