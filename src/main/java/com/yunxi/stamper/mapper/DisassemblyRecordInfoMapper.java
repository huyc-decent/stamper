package com.yunxi.stamper.mapper;

import com.yunxi.stamper.entity.DisassemblyRecordInfo;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

@Component
public interface DisassemblyRecordInfoMapper extends MyMapper<DisassemblyRecordInfo> {
	/**
	 * 查询拆卸记录详情
	 * @param noticeId
	 * @return
	 */
	DisassemblyRecordInfo selectInfoByNoticeId(Integer noticeId);

}