package com.yunxi.stamper.mapper;

import com.yunxi.stamper.entity.Department;
import com.yunxi.stamper.entity.Org;
import com.yunxi.stamper.entityVo.OrgServeVo;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface OrgMapper extends MyMapper<Org> {

	//查询平台所有公司
	List<OrgServeVo> selectByAll();

	/**
	 * 查询该手机号所属的组织列表
	 * @param phone 手机号码
	 * @return
	 */
	List<Org> selectOrgsByPhone(String phone);

	/**
	 * 查询集团root组织
	 * @param orgId
	 * @return
	 */
	Department selectRoot(Integer orgId);
}