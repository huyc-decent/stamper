package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.ApplicationKeeper;
import com.yunxi.stamper.entity.ApplicationNode;
import com.yunxi.stamper.entity.Signet;
import com.yunxi.stamper.entity.User;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description 授权处理业务层
 * @date 2019/5/5 0005 13:48
 */
public interface ApplicationKeeperService {
	void add(ApplicationKeeper ak);

	void update(ApplicationKeeper applicationKeeper);

	//查询指定申请单id
	List<ApplicationKeeper> getByApplication(Integer applicationId);

	//查询指定申请单id+授权人Id
	List<ApplicationKeeper> getByApplicationAndKeeper(Integer applicationId, Integer keeperId);

	//查询印章的授权记录(正在授权中)
	List<ApplicationKeeper> getBySignet(Integer signetId, Integer signetOrgId);

	void del(ApplicationKeeper ak);

	//根据授权节点,创建授权记录
	void createByNode(ApplicationNode node);

	List<ApplicationKeeper> getByApplicationAndNode(Integer applicationId, Integer nodeId);

	/**
	 * 设备管理员发生变更授权记录
	 * @param signet 设备
	 * @param keeper 新设备管理员
	 */
	void updateFromSignetDate(@NotNull Signet signet,@NotNull Integer oldKeeperId,@NotNull User keeper);

	ApplicationKeeper getByApplicationOK(Integer applicationId,Integer deviceId);
}
