package com.yunxi.stamper.mapper;


import com.yunxi.stamper.entity.Threshold;
import com.yunxi.stamper.entityVo.ThresholdEntity;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ThresholdMapper extends MyMapper<Threshold> {
	/**
	 * 查询管理的印章阈值列表
	 *
	 * @param orgId         集团ID
	 * @return
	 */
	List<ThresholdEntity> selectByOrgAndDepartmentAndKeeper(Integer orgId,String keyword);
}