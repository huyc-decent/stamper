package com.yunxi.stamper.mapper;


import com.yunxi.stamper.entity.ApplicationKeeper;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ApplicationKeeperMapper extends MyMapper<ApplicationKeeper> {
	/**
	 * 查询印章正在处理中的授权记录列表
	 *
	 * @param orgId    集团ID
	 * @param deviceId 印章ID
	 * @return
	 */
	List<ApplicationKeeper> selectByOrgAndDeviceAndDealing(Integer orgId, Integer deviceId);
}