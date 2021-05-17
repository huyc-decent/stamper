package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.Meter;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/9 0009 15:39
 */
public interface MeterService {
	Meter get(Integer meterId);

	//查询所属人的高拍仪
	List<Meter> getByUser(Integer userId);

	//查询公司高拍仪
	List<Meter> getByOrg(List<Integer> orgIds);

	//查询公司高拍仪
	List<Meter> getByOrg(Integer orgId);

	//迁移高拍仪
	void update(Meter meter);

	void del(Meter meter);

	Meter getByUUID(String uuid);

	void add(Meter meter);
}
