package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.AppRotationImg;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/28 0028 12:37
 */
public interface AppRotationImgService {
	List<AppRotationImg> getList();

	AppRotationImg get(Integer id);

	void del(AppRotationImg img);

	void add(AppRotationImg img);

	AppRotationImg getByOrderNo(Integer orderNo);
}
