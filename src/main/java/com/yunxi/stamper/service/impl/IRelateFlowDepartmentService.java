package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.RelateFlowDepartment;
import com.yunxi.stamper.mapper.RelateFlowDepartmentMapper;
import com.yunxi.stamper.service.RelateFlowDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/12/18 0018 14:18
 */
@Service
public class IRelateFlowDepartmentService implements RelateFlowDepartmentService {
	@Autowired
	private RelateFlowDepartmentMapper mapper;

	@Override
	public RelateFlowDepartment get(Integer departmentId, Integer flowId) {
		if (departmentId != null && flowId != null) {
			return mapper.selectByDepartmentAndFlow(departmentId, flowId);
		}
		return null;
	}

	@Override
	@Transactional
	public void add(RelateFlowDepartment rfd) {
		int addCount = 0;
		if (rfd != null) {
			addCount = mapper.insertRelate(rfd);
		}
		if (addCount != 1) {
			throw new PrintException("审批流程添加失败");
		}
	}

	@Override
	@Transactional
	public void del(RelateFlowDepartment rfd) {
		int delCount = 0;
		if (rfd != null
				&& rfd.getDepartmentId() != null
				&& rfd.getFlowId() != null) {
			delCount = mapper.deleteByEntity(rfd);
		}
		if (delCount != 1) {
			throw new PrintException("审批流程移除失败");
		}
	}

	/**
	 * 查询组织ID列表
	 *
	 * @param flowId 审批流程ID
	 * @return
	 */
	@Override
	public List<Integer> getDepartmentByFlow(Integer flowId) {
		if (flowId == null) {
			return null;
		}
		return mapper.selectDepartmentByFlow(flowId);
	}

	@Override
	public List<RelateFlowDepartment> getByFlow(Integer flowId) {
		if(flowId==null) {
			return null;
		}
		return mapper.selectByFlow(flowId);
	}

}
