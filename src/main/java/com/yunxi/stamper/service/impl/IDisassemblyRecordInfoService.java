package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.DisassemblyRecordInfo;
import com.yunxi.stamper.mapper.DisassemblyRecordInfoMapper;
import com.yunxi.stamper.service.DisassemblyRecordInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2020/3/3 0003 10:13
 */
@Service
public class IDisassemblyRecordInfoService implements DisassemblyRecordInfoService {

	@Autowired
	private DisassemblyRecordInfoMapper mapper;

	@Override
	@Transactional
	public void add(DisassemblyRecordInfo dri) {
		int addCount = 0;
		if (dri != null) {
			dri.setCreateDate(new Date());
			addCount = mapper.insert(dri);
		}
		if (addCount != 1) {
			throw new PrintException("拆卸记录添加失败-->dri:" + dri.toString());
		}
	}

	/**
	 * 查询拆卸记录详情
	 * @param noticeId
	 * @return
	 */
	@Override
	public DisassemblyRecordInfo getInfoByNoticeId(Integer noticeId) {
		if(noticeId!=null){
			return mapper.selectInfoByNoticeId(noticeId);
		}
		return null;
	}
}
