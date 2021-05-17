package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.OrgNoticeTemp;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/24 0024 9:07
 */
public interface OrgNoticeTempService {
	OrgNoticeTemp getByOrgAndNoticetemp(Integer orgId, Integer noticeTempId);

	List<Integer> getByOrg(Integer orgId);

	void add(OrgNoticeTemp ont);
}
