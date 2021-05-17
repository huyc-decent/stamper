package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.Qss;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/6/10 0010 20:13
 */
public interface QssService {
	List<Qss> getAll();

	Qss get(Integer qssId);

	void del(Qss qss);

	Qss getByUrl(String url);

	Qss getByUrl(String url, Integer type);

	Qss getByName(String name);

	void add(Qss qss);

	void update(Qss qss);

	List<String> getByArray(Integer type);

}
