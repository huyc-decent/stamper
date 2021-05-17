package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.entity.OrgNoticeTemp;
import com.yunxi.stamper.entity.OrgSmsTemp;
import com.yunxi.stamper.mapper.OrgNoticeTempMapper;
import com.yunxi.stamper.service.OrgNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/23 0023 16:52
 */
@Service
public class IOrgNoticeService implements OrgNoticeService {

	@Autowired
	private OrgNoticeTempMapper mapper;

	@Override
	public OrgNoticeTemp getByOrgAndNotice(Integer orgId, Integer noticeTempId) {
		Example example = new Example(OrgNoticeTemp.class);
		example.createCriteria().andEqualTo("orgId", orgId)
				.andEqualTo("noticeTempId", noticeTempId);
		return mapper.selectOneByExample(example);
	}

	@Override
	@Transactional
	public void del(OrgNoticeTemp on) {
		if (on != null && on.getOrgId() != null && on.getNoticeTempId() != null) {
			mapper.delete(on);
		}
	}

	@Override
	@Transactional
	public void add(OrgNoticeTemp on) {
		if (on != null) {
			mapper.insert(on);
		}
	}

	@Override
	@Transactional
	public void updateBulk(Integer orgId, List<Integer> tempIds, Boolean status) {
		//批量启用通知模板
		if (status) {
			//删除该公司所有通知模板
			Example deleteExample = new Example(OrgNoticeTemp.class);
			OrgNoticeTemp deleteEntity = new OrgNoticeTemp();
			deleteEntity.setOrgId(orgId);
			mapper.deleteByExample(deleteExample);

			List<OrgNoticeTemp> insertList = new ArrayList<>();
			for (Integer tempId : tempIds) {
				OrgNoticeTemp insertEntity = new OrgNoticeTemp();
				insertEntity.setOrgId(orgId);
				insertEntity.setNoticeTempId(tempId);
				insertList.add(insertEntity);
			}
			mapper.insertList(insertList);
		}
		//批量禁用模板
		else {
			for (Integer tempId : tempIds) {
				OrgNoticeTemp deleteEntity = new OrgNoticeTemp();
				deleteEntity.setOrgId(orgId);
				deleteEntity.setNoticeTempId(tempId);
				mapper.delete(deleteEntity);
			}
		}
	}

	/**
	 * 查询公司拥有的通知模板列表id
	 *
	 * @param orgId
	 * @return
	 */
	@Override
	public List<Integer> getByOrg(Integer orgId) {
		if (orgId != null) {
			return mapper.selectByOrg(orgId);
		}
		return null;
	}
}
