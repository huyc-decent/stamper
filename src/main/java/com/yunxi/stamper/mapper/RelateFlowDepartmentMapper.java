package com.yunxi.stamper.mapper;


import com.yunxi.stamper.entity.RelateFlowDepartment;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface RelateFlowDepartmentMapper extends MyMapper<RelateFlowDepartment> {
	RelateFlowDepartment selectByDepartmentAndFlow(Integer departmentId, Integer flowId);

	List<Integer> selectDepartmentByFlow(Integer flowId);

	int insertRelate(RelateFlowDepartment rfd);

	int deleteByEntity(RelateFlowDepartment rfd);

	List<RelateFlowDepartment> selectByFlow(Integer flowId);
}