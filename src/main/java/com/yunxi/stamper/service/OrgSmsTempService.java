package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.OrgSmsTemp;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/24 0024 9:00
 */
public interface OrgSmsTempService {
	OrgSmsTemp getByOrgAndSmstemp(Integer orgId, Integer orgSmsTempId);

	//查询该公司启用中的短信模板id列表
	List<Integer> getByOrg(Integer orgId);

	void add(OrgSmsTemp ost);

	void del(OrgSmsTemp ost);

	/**
	 * 批量启用、禁用短信模板
	 *
	 * @param orgId   组织id
	 * @param tempIds 待更新的模板Id
	 * @param status  true:启用  flase:禁用
	 */
	void updateBulk(Integer orgId, List<Integer> tempIds, boolean status);
}
