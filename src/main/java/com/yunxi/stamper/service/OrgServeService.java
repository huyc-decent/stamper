package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.OrgServe;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/23 0023 11:09
 */
public interface OrgServeService {
	void add(OrgServe os);

	void del(OrgServe orgServe);

	OrgServe getByOrgAndCode(Integer orgId, String code);

}
