package com.yunxi.stamper.mapper;


import com.yunxi.stamper.entity.Position;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface PositionMapper extends MyMapper<Position> {
	/**
	 * 查询用户职称
	 *
	 * @param orgId  集团ID
	 * @param userId 员工ID
	 * @return
	 */
	List<Position> selectPostionByOrgAndUser(Integer orgId, Integer userId);
}