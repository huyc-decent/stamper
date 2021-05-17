package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.OrgNoticeTemp;
import com.yunxi.stamper.mapper.OrgNoticeTempMapper;
import com.yunxi.stamper.service.OrgNoticeTempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/24 0024 9:07
 */
@Service
public class IOrgNoticeTempService implements OrgNoticeTempService {
	@Autowired
	private OrgNoticeTempMapper mapper;

	@Override
	public OrgNoticeTemp getByOrgAndNoticetemp(Integer orgId, Integer noticeTempId) {
		if(orgId!=null&&noticeTempId!=null){
			Example example = new Example(OrgNoticeTemp.class);
			example.createCriteria().andEqualTo("orgId",orgId)
					.andEqualTo("noticeTempId",noticeTempId);
			return mapper.selectOneByExample(example);
		}
		return null;
	}

	@Override
	@Transactional
	public void add(OrgNoticeTemp ont) {
		int addCount = 0;
		if(ont!=null){
			addCount = mapper.insert(ont);
		}
		if(addCount!=1){
			throw new PrintException("通知消息模板创建失败");
		}
	}

	@Override
	public List<Integer> getByOrg(Integer orgId) {
		if (orgId != null) {
			return mapper.selectByOrg(orgId);
		}
		return null;
	}
}
