package com.yunxi.stamper.mapper;


import com.yunxi.stamper.entity.ApplicationNode;
import com.yunxi.stamper.entityVo.ApplicationNodeVo;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ApplicationNodeMapper extends MyMapper<ApplicationNode> {
	/**
	 * 审批节点列表
	 *
	 * @param applicationId 申请单ID
	 * @return
	 */
	List<ApplicationNodeVo> selectByApplication(Integer applicationId);

	/**
	 * 节点信息
	 *
	 * @param applicationId 申请单ID
	 * @param orderNo       节点序号
	 * @return
	 */
	Integer selectByNextNode(Integer applicationId, Integer orderNo);

	/**
	 * 查询设备未处理的节点记录列表
	 *
	 * @param orgId       集团ID
	 * @param deviceId    设备ID
	 * @param oldKeeperId 节点名称
	 * @return
	 */
	List<ApplicationNode> selectNoKeeperHandleByOrgAndDeviceAndOldKeeperId(Integer orgId, Integer deviceId, Integer oldKeeperId);

	/**
	 * 查询设备未处理的节点记录列表
	 *
	 * @param orgId        集团ID
	 * @param deviceId     设备ID
	 * @param oldAuditorId 节点名称
	 * @return
	 */
	List<ApplicationNode> selectNoAuditorHandleByOrgAndDeviceAndOldAuditorId(Integer orgId, Integer deviceId, Integer oldAuditorId);
}