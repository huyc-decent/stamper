package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.SmsTemp;
import com.yunxi.stamper.entityVo.SMSVoSelect;
import com.yunxi.stamper.mapper.SmsTempMapper;
import com.yunxi.stamper.service.SmsTempService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/14 0014 23:12
 */
@Service
public class ISmsTempService implements SmsTempService {
	@Autowired
	private SmsTempMapper mapper;

	@Override
	public List<SMSVoSelect> getAllByVo() {
		return mapper.selectByAll();
	}

	@Override
	public SmsTemp get(Integer smsTempId) {
		if (smsTempId != null) {
			return mapper.selectByPrimaryKey(smsTempId);
		}
		return null;
	}

	@Override
	public SmsTemp getByCode(String smsCode) {
		if (StringUtils.isNotBlank(smsCode)) {
			Example example = new Example(SmsTemp.class);
			example.createCriteria().andIsNull("deleteDate")
					.andEqualTo("code", smsCode);
			return mapper.selectOneByExample(example);
		}
		return null;
	}

	@Override
	public List<SmsTemp> getAll() {
		Example example = new Example(SmsTemp.class);
		example.createCriteria().andIsNull("deleteDate");
		return mapper.selectByExample(example);
	}

	@Override
	@Transactional
	public void add(SmsTemp smsTemp) {
		int addCount = 0;
		addCount = mapper.insert(smsTemp);
		if (addCount != 1) {
			throw new PrintException("短信消息模板添加失败");
		}
	}
}
