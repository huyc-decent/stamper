package com.yunxi.stamper.mapper;


import com.yunxi.stamper.entity.ApplicationAuditor;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import io.lettuce.core.dynamic.annotation.CommandNaming;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ApplicationAuditorMapper extends MyMapper<ApplicationAuditor> {

	/**
	 * 查询设备相关审计记录列表(不包含处理完成)
	 * @param orgId 集团ID
	 * @param deviceId 设备ID
	 * @return
	 */
	List<ApplicationAuditor> selectByOrgAndDeviceAndDealing(Integer orgId, Integer deviceId);
}