package com.yunxi.stamper.mapper;


import com.yunxi.stamper.entity.ApplicationManager;
import com.yunxi.stamper.entityVo.ApplicationManagerVoSelect;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ApplicationManagerMapper extends MyMapper<ApplicationManager> {
	//查询申请单审批流程
	List<ApplicationManagerVoSelect> selectByApplication(Integer applicationId);

	//查询指定申请单+审批人 正在处理的审批记录
	ApplicationManager selectByApplicationAndManagerAndDealing(Integer applicationId, Integer userId);
}