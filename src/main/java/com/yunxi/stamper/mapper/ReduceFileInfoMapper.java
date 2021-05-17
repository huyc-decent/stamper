package com.yunxi.stamper.mapper;


import com.yunxi.stamper.entity.ReduceFileInfo;
import com.yunxi.stamper.sys.baseDao.MyMapper;

public interface ReduceFileInfoMapper extends MyMapper<ReduceFileInfo> {
	/**
	 * 查询指定原图ID的缩率图信息
	 * @param fileId 原图ID
	 * @return
	 */
	ReduceFileInfo selectByFileInfoId(String fileId);
}