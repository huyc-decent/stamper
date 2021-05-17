package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.ApplicationAuditor;
import com.yunxi.stamper.entity.ApplicationNode;
import com.yunxi.stamper.entity.Signet;
import com.yunxi.stamper.entity.User;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/5 0005 17:16
 */
public interface ApplicationAuditorService {
	//查询指定申请单id 审计人id
	List<ApplicationAuditor> getByApplicationAndAuditor(Integer applicationId, Integer auditorId);

	List<ApplicationAuditor> getByApplication(Integer applicationId);

	//查询印章对应的审计记录列表(未处理完成)
	List<ApplicationAuditor> getBySignet(Integer signetId, Integer signetOrgId);

	void del(ApplicationAuditor aa);

	void add(ApplicationAuditor aa);

	void update(ApplicationAuditor aa);

	void createByNode(ApplicationNode node);

	List<ApplicationAuditor> getByApplicationAndAuditorAndNode(Integer applicationId, Integer userId, Integer nodeId);

	List<ApplicationAuditor> getByApplicationAndNode(Integer applicationId, Integer nodeId);

	/**
	 * 设备管理员发生变更审计记录
	 * @param signet 印章
	 * @param auditor 新审计员
	 */
	void updateFromSignetDate(@NotNull Signet signet,@NotNull Integer oldAuditorId,@NotNull User auditor);
}
