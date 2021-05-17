package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.ErrorType;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/3 0003 15:09
 */
public interface ErrorTypeService {
	void add(ErrorType errorType);

	//查询指定使用记录id和name的错误记录列表
	List<ErrorType> getBySealRecordInfo(Integer sealRecordInfoId);

	//查询指定使用记录id的错误信息记录
	String getBySealRecordInfoId(Integer sealRecordInfoId);

	void del(ErrorType et);

	/**
	 * 异常信息列表
	 * @param infoIds 记录id列表
	 * @return
	 */
	List<ErrorType> getBySealRecordInfoIds(List<Integer> infoIds);

	void addList(Integer orgId, Integer sealRecordInfoId, List<ErrorType> errorTypes);

	ErrorType getBySealRecordInfoAndName(Integer sealRecordInfoId, String name);
}
