package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.OrgTypeTemp;
import com.yunxi.stamper.mapper.OrgTypeTempMapper;
import com.yunxi.stamper.service.OrgTypeTempService;
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
 * @date 2019/5/7 0007 15:21
 */
@Service
public class IOrgTypeTempService implements OrgTypeTempService {

	@Autowired
	private OrgTypeTempMapper mapper;

	@Override
	public List<OrgTypeTemp> getAll() {
		Example example = new Example(OrgTypeTemp.class);
		example.createCriteria().andIsNull("deleteDate");
		return mapper.selectByExample(example);
	}

	@Override
	@Transactional
	public void update(OrgTypeTemp temp) {
		int updateCount = 0;

		temp.setUpdateDate(new Date());
		temp.setDeleteDate(new Date());
		updateCount = mapper.updateByPrimaryKey(temp);

		if (updateCount != 1) {
			throw new PrintException("组织类型模板修改失败");
		}
	}

	@Override
	@Transactional
	public void add(OrgTypeTemp temp) {
		int addCount = 0;
		if(temp!=null) {
			temp.setCreateDate(new Date());
			addCount = mapper.insert(temp);
		}
		if (addCount != 1) {
			throw new PrintException("组织类型模板添加失败");
		}
	}

	@Override
	public OrgTypeTemp getByCode(String code) {
		if (StringUtils.isNotBlank(code)) {
			Example example = new Example(OrgTypeTemp.class);
			example.createCriteria().andIsNull("deleteDate")
					.andEqualTo("code", code);
			return mapper.selectOneByExample(example);
		}
		return null;
	}

	@Override
	public OrgTypeTemp getByName(String name) {
		if (StringUtils.isNotBlank(name)) {
			Example example = new Example(OrgTypeTemp.class);
			example.createCriteria().andIsNull("deleteDate")
					.andEqualTo("name", name);
			return mapper.selectOneByExample(example);
		}
		return null;
	}

	@Override
	@Transactional
	public void del(OrgTypeTemp orgTypeTemp) {
		int delCount = 0;
		orgTypeTemp.setDeleteDate(new Date());
		delCount = mapper.updateByPrimaryKey(orgTypeTemp);
		if (delCount != 1) {
			throw new PrintException("组织类型模板删除失败");
		}
	}

	@Override
	public OrgTypeTemp get(Integer id) {
		if (id != null) {
			return mapper.selectByPrimaryKey(id);
		}
		return null;
	}
}
