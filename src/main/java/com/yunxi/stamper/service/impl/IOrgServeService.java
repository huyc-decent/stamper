package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.OrgServe;
import com.yunxi.stamper.mapper.OrgServeMapper;
import com.yunxi.stamper.service.OrgServeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/23 0023 11:09
 */
@Service
public class IOrgServeService implements OrgServeService {

	@Autowired
	private OrgServeMapper mapper;

	@Override
	public OrgServe getByOrgAndCode(Integer orgId, String code) {
		if (orgId != null && StringUtils.isNotBlank(code)) {
			return mapper.selectByOrgAndCode(orgId, code);
		}
		return null;
	}

	@Override
	@Transactional
	public void del(OrgServe os) {
		if (os != null && os.getServeId() != null && os.getOrgId() != null) {
			mapper.delete(os);
		}
	}

	@Override
	@Transactional
	public void add(OrgServe os) {
		int addCount = 0;
		if (os != null && os.getOrgId() != null && os.getServeId() != null) {
			addCount = mapper.insert(os);
		}
		if(addCount!=1){
			throw new PrintException("公司服务绑定失败");
		}
	}
}
