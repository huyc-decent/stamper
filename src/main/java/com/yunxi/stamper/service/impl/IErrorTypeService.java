package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.ErrorType;
import com.yunxi.stamper.mapper.ErrorTypeMapper;
import com.yunxi.stamper.service.ErrorTypeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/3 0003 15:09
 */
@Service
public class IErrorTypeService implements ErrorTypeService {
	@Autowired
	private ErrorTypeMapper mapper;

	@Override
	@Transactional
	public void del(ErrorType et) {
		int delCount = 0;
		if (et != null && et.getId() != null) {
			delCount = mapper.delete(et);
		}
		if (delCount != 1) {
			throw new PrintException("使用记录错误信息移除失败");
		}
	}

	/**
	 * 查询指定使用记录id的错误信息记录
	 */
	@Override
	public String getBySealRecordInfoId(Integer sealRecordInfoId) {
		if (sealRecordInfoId == null) {
			return null;
		}
		List<ErrorType> ets = getBySealRecordInfo(sealRecordInfoId);
		if (ets == null || ets.isEmpty()) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ets.size(); i++) {
			ErrorType et = ets.get(i);
			String name = et.getName();
			String remark = et.getRemark();
			if (StringUtils.isNotBlank(name)) {
				if (StringUtils.isBlank(remark)) {
					sb.append(name).append(" ");
				} else {
					sb.append(name + "-" + remark).append(" ");
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 查询指定使用记录id的错误记录列表
	 */
	@Override
	public List<ErrorType> getBySealRecordInfo(Integer sealRecordInfoId) {
		if (sealRecordInfoId != null) {
			Example example = new Example(ErrorType.class);
			example.createCriteria().andEqualTo("sealRecordInfoId", sealRecordInfoId)
					.andIsNull("deleteDate");
			return mapper.selectByExample(example);
		}
		return null;
	}

	@Override
	@Transactional
	public void add(ErrorType errorType) {
		errorType.setCreateDate(new Date());
		mapper.insert(errorType);
	}

	/**
	 * 异常信息列表
	 *
	 * @param infoIds 记录id列表
	 * @return
	 */
	@Override
	public List<ErrorType> getBySealRecordInfoIds(List<Integer> infoIds) {
		if (infoIds == null || infoIds.isEmpty()) {
			return null;
		}
		Example example = new Example(ErrorType.class);
		example.createCriteria().andIsNull("deleteDate").andIn("sealRecordInfoId", infoIds);
		return mapper.selectByExample(example);
	}

	@Override
	public ErrorType getBySealRecordInfoAndName(Integer sealRecordInfoId, String name) {
		if (sealRecordInfoId == null || StringUtils.isBlank(name)) {
			return null;
		}
		Example example = new Example(ErrorType.class);
		example.createCriteria().andIsNull("deleteDate")
				.andEqualTo("sealRecordInfoId", sealRecordInfoId)
				.andEqualTo("name", name);
		return mapper.selectOneByExample(example);
	}

	@Override
	public void addList(Integer orgId, Integer sealRecordInfoId, List<ErrorType> errorTypes) {
		if (orgId == null || sealRecordInfoId == null || errorTypes == null || errorTypes.isEmpty()) {
			return;
		}

		for (ErrorType et : errorTypes) {
			et.setOrgId(orgId);
			et.setSealRecordInfoId(sealRecordInfoId);
			try {
				ErrorType temp = getBySealRecordInfoAndName(et.getSealRecordInfoId(), et.getName());
				if (temp != null && temp.getOrgId() == orgId.intValue()) {
					continue;
				}
				add(et);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
