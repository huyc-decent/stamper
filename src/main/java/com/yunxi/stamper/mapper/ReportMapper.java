package com.yunxi.stamper.mapper;

import com.yunxi.stamper.entity.Report;
import com.yunxi.stamper.entityVo.ReportEntity;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ReportMapper extends MyMapper<Report> {
	/**
	 * 查询用户的报表记录
	 * @param userInfoId 用户ID
	 * @return
	 */
	List<ReportEntity> selectReportEntitiesByUser(Integer userInfoId);
}