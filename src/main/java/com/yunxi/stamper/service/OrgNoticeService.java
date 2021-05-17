package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.OrgNoticeTemp;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/23 0023 16:52
 */
public interface OrgNoticeService {
	List<Integer> getByOrg(Integer orgId);

	void add(OrgNoticeTemp on);

	void del(OrgNoticeTemp on);

	void updateBulk(Integer orgId, List<Integer> asList, Boolean status);

	OrgNoticeTemp getByOrgAndNotice(Integer orgId, Integer noticeTempId);

}
