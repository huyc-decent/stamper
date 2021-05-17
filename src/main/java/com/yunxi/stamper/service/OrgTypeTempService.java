package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.OrgTypeTemp;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/7 0007 15:20
 */
public interface OrgTypeTempService {
	OrgTypeTemp get(Integer id);

	void del(OrgTypeTemp orgTypeTemp);

	OrgTypeTemp getByName(String name);

	OrgTypeTemp getByCode(String code);

	void add(OrgTypeTemp temp);

	void update(OrgTypeTemp temp);

	List<OrgTypeTemp> getAll();
}
