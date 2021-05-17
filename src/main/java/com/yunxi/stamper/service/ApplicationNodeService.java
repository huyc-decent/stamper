package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.Application;
import com.yunxi.stamper.entity.ApplicationManager;
import com.yunxi.stamper.entity.ApplicationNode;
import com.yunxi.stamper.entity.User;
import com.yunxi.stamper.entityVo.ApplicationNodeVo;
import com.yunxi.stamper.entityVo.UserInfo;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/6/19 0019 14:31
 */
public interface ApplicationNodeService {

	void add(ApplicationNode applicationNode);

	void update(ApplicationNode node);

	void del(ApplicationNode node);

	ApplicationNode get(Integer nodeId);

	/**
	 * 节点信息
	 *
	 * @param applicationId 申请单ID
	 * @param orderNo       节点序号
	 * @return
	 */
	ApplicationNode getByApplicationAndOrderNo(Integer applicationId, Integer orderNo);


	/**
	 * 节点列表
	 *
	 * @param applicationId 申请单id
	 * @return
	 */
	List<ApplicationNodeVo> getByApplication(Integer applicationId);


	/**
	 * 节点列表
	 *
	 * @param applicationId 申请单ID
	 * @param orderNo       节点序号
	 * @return
	 */
	List<ApplicationNode> getByApplicationAndGreaterThanOrderNo(Integer applicationId, Integer orderNo);

	/**
	 * 节点列表
	 *
	 * @param applicationId 申请单ID
	 * @param handle        节点状态
	 * @return
	 */
	List<ApplicationNode> getByApplicationAndHandle(Integer applicationId, Integer handle);


	/**
	 * 节点ID
	 *
	 * @param applicationId 申请单ID
	 * @param orderNo       节点序号
	 * @return
	 */
	Integer getNextNode(Integer applicationId, Integer orderNo);

	/**
	 * 审批人ID列表
	 *
	 * @param applicationId 申请单ID
	 * @return
	 */
	List<Integer> getManagersByApplication(Integer applicationId);

	/**
	 * 审批转交
	 *
	 * @param userInfo    操作人
	 * @param pushUser    被转交人
	 * @param application 申请单
	 * @param suggest     意见
	 * @param node        当前流程节点
	 */
	void managerTrans(UserInfo userInfo, User pushUser, Application application, String suggest, ApplicationNode node);

	/**
	 * 未处理节点列表
	 *
	 * @param orgId       集团ID
	 * @param deviceId    设备ID
	 * @param oldKeeperId 授权人ID
	 * @return
	 */
	List<ApplicationNode> getNoKeeperHandleByOrgAndDeviceAndOldKeeperId(Integer orgId, Integer deviceId, Integer oldKeeperId);

	/**
	 * 未处理节点列表
	 *
	 * @param orgId        集团ID
	 * @param deviceId     设备ID
	 * @param oldAuditorId 审计人ID
	 * @return
	 */
	List<ApplicationNode> getNoAuditorHandleByOrgAndDeviceAndOldAuditorId(Integer orgId, Integer deviceId, Integer oldAuditorId);
}
