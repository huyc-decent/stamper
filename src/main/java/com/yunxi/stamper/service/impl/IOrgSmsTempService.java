package com.yunxi.stamper.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yunxi.stamper.commons.jwt.UserToken;
import com.yunxi.stamper.entity.OrgSmsTemp;
import com.yunxi.stamper.mapper.OrgSmsTempMapper;
import com.yunxi.stamper.service.OrgSmsTempService;
import com.yunxi.stamper.sys.context.SpringContextUtils;
import com.yunxi.stamper.sys.error.base.PrintException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/24 0024 9:00
 */
@Slf4j
@Service
public class IOrgSmsTempService implements OrgSmsTempService {
	@Autowired
	private OrgSmsTempMapper mapper;

	@Override
	@Transactional
	public void del(OrgSmsTemp ost) {
		int delCount = 0;
		if (ost != null && ost.getSmsTempId() != null && ost.getOrgId() != null) {
			UserToken token = SpringContextUtils.getToken();
			log.info("-\t短信模板-删除\ttempId:{}\torgId:{}\tlogin:{}", ost.getSmsTempId(),ost.getOrgId(), JSONObject.toJSONString(token));
			delCount = mapper.delete(ost);
		}
		if (delCount != 1) {
			throw new PrintException("公司短信模板解绑失败");
		}
	}

	@Override
	@Transactional
	public void add(OrgSmsTemp ost) {
		int addCount = 0;
		if (ost != null && ost.getOrgId() != null && ost.getSmsTempId() != null) {
			UserToken token = SpringContextUtils.getToken();
			log.info("-\t短信模板-添加\ttempId:{}\torgId:{}\tlogin:{}", ost.getSmsTempId(),ost.getOrgId(), JSONObject.toJSONString(token));
			addCount = mapper.insert(ost);
		}
		if (addCount != 1) {
			throw new PrintException("公司短信模板绑定失败");
		}
	}

	/**
	 * 查询该公司启用中的短信模板id列表
	 */
	@Override
	public List<Integer> getByOrg(Integer orgId) {
		if (orgId != null) {
			return mapper.selectSmsTempIdByOrg(orgId);
		}
		return null;
	}

	/**
	 * 查询指定公司id和模板id的短信模板实例
	 */
	@Override
	public OrgSmsTemp getByOrgAndSmstemp(Integer orgId, Integer orgSmsTempId) {
		if (orgId == null || orgSmsTempId == null) {
			return null;
		}
		Example example = new Example(OrgSmsTemp.class);
		example.createCriteria().andEqualTo("orgId", orgId)
				.andEqualTo("smsTempId", orgSmsTempId);
		return mapper.selectOneByExample(example);
	}

	/**
	 * 批量启用、禁用短信模板
	 *
	 * @param orgId   组织id
	 * @param tempIds 待更新的模板Id
	 * @param status  true:启用  flase:禁用
	 */
	@Override
	@Transactional
	public void updateBulk(Integer orgId, List<Integer> tempIds, boolean status) {

		UserToken token = SpringContextUtils.getToken();

		//批量启用短信模板
		if (status) {
			//删除该公司所有短信模板
			Example deleteExample = new Example(OrgSmsTemp.class);
			deleteExample.createCriteria().andEqualTo("orgId",orgId);
			log.info("-\t短信模板-批量删除\torgId:{}\tlogin:{}", orgId, JSONObject.toJSONString(token));
			mapper.deleteByExample(deleteExample);

			//添加新模板
			for (Integer tempId : tempIds) {
				OrgSmsTemp insertEntity = new OrgSmsTemp();
				insertEntity.setOrgId(orgId);
				insertEntity.setSmsTempId(tempId);
				add(insertEntity);
			}
		}
		//批量禁用模板
		else {
			for (Integer tempId : tempIds) {
				OrgSmsTemp deleteEntity = new OrgSmsTemp();
				deleteEntity.setOrgId(orgId);
				deleteEntity.setSmsTempId(tempId);
				del(deleteEntity);
			}
		}
	}
}
