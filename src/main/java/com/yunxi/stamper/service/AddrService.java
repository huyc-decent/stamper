package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.Addr;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/3 0003 1:41
 */
public interface AddrService {
	Addr getByLocation(String addr);

	void add(Addr addr);

	Addr get(Integer addrId);

}
