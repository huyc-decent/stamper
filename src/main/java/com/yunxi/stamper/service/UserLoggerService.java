package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.UserLogger;
import com.yunxi.stamper.entityVo.UserLoggerVo;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/22 0022 13:30
 */
public interface UserLoggerService {
	void add(UserLogger ul);

	void update(UserLogger ul);

	//查询非异常操作记录
	List<UserLoggerVo> getByUserAndNormal(Integer orgId, Integer userId);
}
