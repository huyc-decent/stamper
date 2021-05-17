package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.ReduceFileInfo;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/12/4 0004 15:50
 */
public interface ReduceFileInfoService {
	/**
	 * 查询指定原图的缩略图信息
	 * @param fileId 原图ID
	 * @return
	 */
	ReduceFileInfo getByFileInfo(String fileId);

	/**
	 * 添加缩率图信息
	 * @param reduceFileInfo 缩率图信息
	 */
	void add(ReduceFileInfo reduceFileInfo);

	ReduceFileInfo get(String fileId);

}
