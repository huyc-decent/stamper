package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.base.BaseService;
import com.yunxi.stamper.entity.StrategyPassword;
import com.yunxi.stamper.mapper.StrategyPasswordMapper;
import com.yunxi.stamper.service.StrategyPasswordService;
import com.yunxi.stamper.sys.error.base.PrintException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

@Service
public class IStrategyPasswordService extends BaseService implements StrategyPasswordService {
	@Autowired
	private StrategyPasswordMapper mapper;

	@Override
	@Transactional
	public StrategyPassword getByOrg(Integer orgId) {
		Example example = new Example(StrategyPassword.class);
		example.createCriteria().andIsNull("deletedate")
				.andEqualTo("orgId", orgId);
		return mapper.selectOneByExample(example);
	}

	@Override
	@Transactional
	public void add(StrategyPassword strategy) {
		int addCount = 0;
		if (strategy != null) {
			strategy.setCreatedate(new Date());
			strategy.setOrgId(-1);
			addCount = mapper.insert(strategy);
		}
		if (addCount != 1) {
			throw new PrintException("密码规则添加失败");
		}
	}

	@Override
	@Transactional
	public void update(StrategyPassword strategy) {
		int updateCount = 0;
		if (strategy != null && strategy.getId() != null) {
			strategy.setUpdatedate(new Date());
			strategy.setOrgId(-1);
			updateCount = mapper.updateByPrimaryKey(strategy);
		}

		if (updateCount != 1) {
			throw new PrintException("密码规则修改失败");
		}
	}

}
