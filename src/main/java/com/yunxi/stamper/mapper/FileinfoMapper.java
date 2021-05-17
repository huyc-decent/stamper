package com.yunxi.stamper.mapper;


import com.yunxi.stamper.entity.FileInfo;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public interface FileinfoMapper extends MyMapper<FileInfo> {
	/**
	 * 查询指定时间段、指定类型的文件列表
	 * @param now
	 * @param pre
	 * @param scaling 0已压缩  1未压缩
	 * @return
	 */
	List<FileInfo> getBetweenNowAndPreAndScaling(Date now, Date pre, int scaling);

	FileInfo selectByHash(String hash);
}