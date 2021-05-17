package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.DisassemblyRecordInfo; /**
 * @author zhf_10@163.com
 * @Description
 * @date 2020/3/3 0003 10:13
 */
public interface DisassemblyRecordInfoService {
	void add(DisassemblyRecordInfo dri);

	/**
	 * 查询拆卸记录详情
	 * @param noticeId
	 * @return
	 */
	DisassemblyRecordInfo getInfoByNoticeId(Integer noticeId);
}
