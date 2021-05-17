package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.AppVersion;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/28 0028 12:54
 */
public interface AppVersionService {

	AppVersion getLastVersion(String client);

	void add(AppVersion appVersion);

	AppVersion getByVersion(String version);

	void delete(AppVersion appVersion);

	List<AppVersion> getAll();
}
