package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.UserLogger;
import com.yunxi.stamper.entityVo.UserLoggerVo;
import com.yunxi.stamper.mapper.UserLoggerMapper;
import com.yunxi.stamper.service.UserLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/22 0022 13:30
 */
@Service
public class IUserLoggerService implements UserLoggerService {

	@Autowired
	private UserLoggerMapper mapper;

	/**
	 * 查询非异常操作记录
	 */
	@Override
	public List<UserLoggerVo> getByUserAndNormal(Integer orgId,Integer userId) {
		if (userId != null) {
			return mapper.selectByUserAndNormal(orgId,userId);
		}
		return null;
	}

	@Override
	@Transactional
	public void update(UserLogger ul) {
		int updateCount = 0;
		if (ul != null && ul.getId() != null) {
			ul.setUpdateDate(new Date());
			updateCount = mapper.updateByPrimaryKey(ul);
		}
		if (updateCount != 1) {
			throw new PrintException("操作日志更新失败");
		}
	}

	@Override
	@Transactional
	public void add(UserLogger ul) {
		int addCount = 0;
		if (ul != null) {
			ul.setCreateDate(new Date());
			addCount = mapper.insert(ul);
		}
		if (addCount != 1) {
			throw new PrintException("用户操作日志添加失败");
		}
	}
}
