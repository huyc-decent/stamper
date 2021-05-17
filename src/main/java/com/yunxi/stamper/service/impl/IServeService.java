package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.entity.Serve;
import com.yunxi.stamper.mapper.ServeMapper;
import com.yunxi.stamper.service.ServeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/23 0023 17:37
 */
@Service
public class IServeService implements ServeService {
	@Autowired
	private ServeMapper mapper;

	@Override
	public List<Serve> getAll() {
		Example example = new Example(Serve.class);
		example.createCriteria().andIsNull("deleteDate");
		return mapper.selectByExample(example);
	}

	/**
	 * 查询该公司短信服务实例
	 */
	@Override
	public Serve getSMSByOrg(Integer orgId) {
		if(orgId!=null){
			return mapper.selectSMSByOrg(orgId);
		}
		return null;
	}

	@Override
	public Serve getByCode(String code) {
		if (StringUtils.isNotBlank(code)) {
			Example example = new Example(Serve.class);
			example.createCriteria().andEqualTo("code", code);
			return mapper.selectOneByExample(example);
		}
		return null;
	}
}
