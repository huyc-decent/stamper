package com.yunxi.stamper.service;


import com.yunxi.stamper.entity.Report;
import com.yunxi.stamper.entityVo.SealRecordInfoVoSearch;
import com.yunxi.stamper.entityVo.UserInfo;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/6/25 0025 20:12
 */
public interface ReportAsyncService {
	void downloadReport(SealRecordInfoVoSearch search, UserInfo info, Report report);
}
