package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.Serve;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/23 0023 17:37
 */
public interface ServeService {
	Serve getByCode(String code);

	//查询该公司短信服务实例
	Serve getSMSByOrg(Integer orgId);

	List<Serve> getAll();

}
