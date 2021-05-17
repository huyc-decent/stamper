package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.other.Global;
import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.*;
import com.yunxi.stamper.mapper.ApplicationKeeperMapper;
import com.yunxi.stamper.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
 * @date 2019/5/5 0005 13:48
 */
@Slf4j
@Service
public class IApplicationKeeperService implements ApplicationKeeperService {

	@Autowired
	private ApplicationKeeperMapper mapper;
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
	@Autowired
	private MessageTempService messageTempService;
	@Autowired
	private OrgService orgService;

	@Override
	public List<ApplicationKeeper> getByApplicationAndNode(Integer applicationId, Integer nodeId) {
		if (applicationId != null && nodeId != null) {
			Example example = new Example(ApplicationKeeper.class);
			example.createCriteria()
					.andIsNull("deleteDate")
					.andEqualTo("applicationId", applicationId)
					.andEqualTo("nodeId", nodeId);
			return mapper.selectByExample(example);
		}
		return null;
	}

	/**
	 * 根据授权节点,创建授权记录
	 */
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
		if (user == null) {
			throw new PrintException("授权记录创建失败,申请人不存在或已注销");
		}

		/*
		 * 校验 设备,授权人 有效性
		 */
		List<Signet> signets = new LinkedList<>();
		List<User> keepers = new LinkedList<>();
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

			Integer keeperId = signet.getKeeperId();
			if (keeperId == null) {
				throw new PrintException("设备[" + signet.getName() + "]管章人不存在");
			}
			User keeper = userService.get(keeperId);
			if (keeper == null) {
				throw new PrintException("设备[" + signet.getName() + "]管章人不存在");
			}
			keepers.add(keeper);
		}

		//授权方式
		String nodeType = node.getNodeType();

		/*
		 * 单个设备
		 */
		if (nodeType.equalsIgnoreCase(Global.FLOW_LIST)) {
			//该节点只有一个授权人

			Signet signet = signets.get(0);
			User keeper = keepers.get(0);

			//创建授权记录
			ApplicationKeeper ak = new ApplicationKeeper();
			ak.setOrgId(application.getOrgId());
			ak.setDeviceId(signet.getId());
			ak.setNodeId(node.getId());
			ak.setKeeperId(keeper.getId());
			ak.setKeeperName(keeper.getUserName());
			ak.setApplicationId(node.getApplicationId());
			ak.setStatus(1);
			add(ak);

			try {
				Integer orgId = application.getOrgId();
				Org org = orgService.get(orgId);
				String orgName = org.getName();
				messageTempService.authorizationNotice(orgName, application.getUserName(), application.getTitle(), keeper.getId());
			} catch (Exception e) {
				log.error("出现异常 ", e);
			}
		}
		/*
		 * 多个设备
		 */
		else if (nodeType.equalsIgnoreCase(Global.FLOW_AND)) {
			//该节点有多个授权人

			for (int i = 0; i < signets.size(); i++) {
				Signet signet = signets.get(i);
				User keeper = keepers.get(i);

				//创建授权记录
				ApplicationKeeper ak = new ApplicationKeeper();
				ak.setOrgId(application.getOrgId());
				ak.setDeviceId(signet.getId());
				ak.setNodeId(node.getId());
				ak.setKeeperId(keeper.getId());
				ak.setKeeperName(keeper.getUserName());
				ak.setApplicationId(node.getApplicationId());
				ak.setStatus(1);
				add(ak);

				try {
					Integer orgId = application.getOrgId();
					Org org = orgService.get(orgId);
					String orgName = org.getName();
					messageTempService.authorizationNotice(orgName, application.getUserName(), application.getTitle(), keeper.getId());
				} catch (Exception e) {
					log.error("出现异常 ", e);
				}
			}
		}

		//修改该节点状态
		node.setHandle(Global.HANDLE_ING);
		applicationNodeService.update(node);
	}

	@Override
	@Transactional
	public void del(ApplicationKeeper ak) {
		int delCount = 0;
		if (ak != null && ak.getId() != null) {
			delCount = mapper.deleteByPrimaryKey(ak);
		}
		if (delCount != 1) {
			throw new PrintException("授权记录删除失败");
		}
	}

	/**
	 * 查询印章的授权记录(正在授权中)
	 */
	@Override
	public List<ApplicationKeeper> getBySignet(Integer signetId, Integer orgId) {
		if (signetId != null) {
			Example example = new Example(ApplicationKeeper.class);
			example.createCriteria()
					.andIsNull("deleteDate")
					.andEqualTo("status", Global.KEEPER_HANDLING)
					.andEqualTo("orgId", orgId)
					.andEqualTo("deviceId", signetId);
			return mapper.selectByExample(example);
		}
		return null;
	}

	/**
	 * 查询指定申请单id+授权人Id 的记录列表
	 *
	 * @param applicationId 申请单ID
	 * @param keeperId      管章员ID
	 * @return 结果
	 */
	@Override
	public List<ApplicationKeeper> getByApplicationAndKeeper(Integer applicationId, Integer keeperId) {
		if (applicationId != null && keeperId != null) {
			Example example = new Example(ApplicationKeeper.class);
			example.createCriteria().andEqualTo("applicationId", applicationId)
					.andEqualTo("keeperId", keeperId)
					.andIsNull("deleteDate");
			return mapper.selectByExample(example);
		}
		return null;
	}

	/**
	 * 查询指定申请单id的授权列表
	 */
	@Override
	public List<ApplicationKeeper> getByApplication(Integer applicationId) {
		if (applicationId != null) {
			Example example = new Example(ApplicationKeeper.class);
			example.createCriteria().andIsNull("deleteDate")
					.andEqualTo("applicationId", applicationId);
			return mapper.selectByExample(example);
		}
		return null;
	}

	@Override
	@Transactional
	public void update(ApplicationKeeper applicationKeeper) {
		int addCount = 0;
		if (applicationKeeper != null && applicationKeeper.getId() != null) {
			applicationKeeper.setUpdateDate(new Date());
			addCount = mapper.updateByPrimaryKey(applicationKeeper);
		}
		if (addCount != 1) {
			throw new PrintException("授权失败");
		}
	}

	@Override
	@Transactional
	public void add(ApplicationKeeper ak) {
		int addCount = 0;
		if (ak != null) {
			ak.setCreateDate(new Date());
			addCount = mapper.insert(ak);
		}
		if (addCount != 1) {
			throw new PrintException("授权记录初始化失败");
		}
	}


	/**
	 * 设备管理员发生变更
	 *
	 * @param signet 设备
	 * @param keeper 新设备管理员
	 */
	@Override
	@Transactional
	public void updateFromSignetDate(@NotNull Signet signet, @NotNull Integer oldKeeperId, @NotNull User keeper) {
		/*
		 * 查询设备未授权的记录列表
		 */
		List<ApplicationNode> noHandledNodes = applicationNodeService.getNoKeeperHandleByOrgAndDeviceAndOldKeeperId(signet.getOrgId(), signet.getId(), oldKeeperId);
		if (noHandledNodes != null && noHandledNodes.size() > 0) {
			for (ApplicationNode node : noHandledNodes) {
				String nodeType = node.getNodeType();
				String nodeManagerIds = node.getManagerIds();

				if (Global.FLOW_LIST.equalsIgnoreCase(nodeType)) {
					if (nodeManagerIds.equalsIgnoreCase(oldKeeperId + "")) {
						nodeManagerIds = keeper.getId() + "";

						node.setManagerIds(nodeManagerIds);
						applicationNodeService.update(node);
						log.info("0-更新节点:{}", CommonUtils.objToJson(node));
					}
				} else if (Global.FLOW_OR.equalsIgnoreCase(nodeType) || Global.FLOW_AND.equalsIgnoreCase(nodeType) || Global.FLOW_OPTIONAL.equalsIgnoreCase(nodeType)) {
					List<Integer> managerIds = CommonUtils.splitToInteger(nodeManagerIds, ",");
					if (managerIds != null && managerIds.size() > 0 && managerIds.contains(oldKeeperId)) {
						managerIds.remove(oldKeeperId);
						managerIds.add(keeper.getId());
						nodeManagerIds = CommonUtils.listToString(managerIds);

						node.setManagerIds(nodeManagerIds);
						applicationNodeService.update(node);
						log.info("1-更新节点:{}", CommonUtils.objToJson(node));
					}
				}
			}
		}

		/*
		 * 查询设备正在授权中的记录列表
		 */
		List<ApplicationKeeper> aks = mapper.selectByOrgAndDeviceAndDealing(signet.getOrgId(), signet.getId());
		for (ApplicationKeeper ak : aks) {
			Integer keeperId = ak.getKeeperId();
			/*
			 * 印章管理员相同，不做处理
			 */
			if (keeperId.intValue() == keeper.getId().intValue()) {
				continue;
			}
			/*
			 * 删除原印章管理员授权记录
			 */
			del(ak);
			log.info("删除原授权记录:{}", CommonUtils.objToJson(ak));

			/*
			 * 将原授权记录授权人ID更新，重新插入数据库
			 */
			ak.setId(null);
			ak.setKeeperId(keeper.getId());
			ak.setKeeperName(keeper.getUserName());
			ak.setUpdateDate(null);
			add(ak);
			log.info("新增授权记录:{}", CommonUtils.objToJson(ak));

			/*
			 * 查询该授权记录对应节点信息
			 */
			Integer nodeId = ak.getNodeId();
			ApplicationNode node = applicationNodeService.get(nodeId);

			/*
			 * 不处理场景：
			 * 1.节点不存在
			 * 2.非授权节点
			 * 3.节点已完成
			 * 4.节点类型未知
			 */
			if (node == null
					|| !"授权".equalsIgnoreCase(node.getName())
					|| node.getHandle() == Global.HANDLE_COMPLETE
					|| StringUtils.isBlank(node.getNodeType())) {
				return;
			}

			/*
			 * 更新节点信息，授权节点仅有1人
			 */
			node.setManagerIds(keeper.getId() + "");
			applicationNodeService.update(node);
			log.info("2-更新节点:{}", CommonUtils.objToJson(node));

			//TODO:向新的印章管理员发送短信、通知提醒
		}
	}

	/**
	 * 查询指定申请单的授权通过的授权记录
	 *
	 * @param applicationId 申请单ID
	 * @return 结果
	 */
	@Override
	public ApplicationKeeper getByApplicationOK(Integer applicationId, Integer deviceId) {
		if (applicationId != null) {
			List<ApplicationKeeper> aks = getByApplication(applicationId);
			if (aks != null && aks.size() > 0) {
				for (ApplicationKeeper ak : aks) {
					Integer _deviceId = ak.getDeviceId();
					if (_deviceId != null && _deviceId.intValue() == deviceId.intValue()) {
						return ak;
					}
				}
			}
		}
		return null;
	}
}
