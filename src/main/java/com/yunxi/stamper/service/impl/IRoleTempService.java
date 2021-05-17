package com.yunxi.stamper.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.RoleTemp;
import com.yunxi.stamper.mapper.RoleTempMapper;
import com.yunxi.stamper.service.RoleTempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/7 0007 14:32
 */
@Service
public class IRoleTempService implements RoleTempService {
	@Autowired
	private RoleTempMapper mapper;

	@Override
	@Transactional
	public void add(RoleTemp roleTemp) {
		int addCount = 0;
		if (roleTemp != null) {
			roleTemp.setCreateDate(new Date());
			addCount = mapper.insert(roleTemp);
		}
		if (addCount != 1) {
			throw new PrintException("角色模板添加失败");
		}
	}

	@Override
	public List<RoleTemp> getAll() {
		Example example = new Example(RoleTemp.class);
		example.createCriteria().andIsNull("deleteDate");
		return mapper.selectByExample(example);
	}

	@Override
	@Transactional
	public void update(RoleTemp roleTemp) {
		int update = 0;
		if (roleTemp != null) {
			roleTemp.setUpdateDate(new Date());
			update = mapper.updateByPrimaryKey(roleTemp);
		}
		if (update != 1) {
			throw new PrintException("角色模板更新失败\ttext:" + (roleTemp == null ? "" : JSONObject.toJSONString(roleTemp)));
		}
	}
}
