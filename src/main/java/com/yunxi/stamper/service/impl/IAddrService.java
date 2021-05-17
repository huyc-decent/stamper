package com.yunxi.stamper.service.impl;


import com.yunxi.stamper.commons.other.RedisGlobal;
import com.yunxi.stamper.commons.other.RedisUtil;
import com.yunxi.stamper.entity.Addr;
import com.yunxi.stamper.mapper.AddrMapper;
import com.yunxi.stamper.service.AddrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/3 0003 1:41
 */
@Service
public class IAddrService implements AddrService {
	@Autowired
	private AddrMapper mapper;
	@Autowired
	private RedisUtil redisUtil;

	@Override
	public Addr get(Integer addrId) {
		String key = RedisGlobal.ADDR_CACHE + addrId;

		Addr addr;
		Object obj = redisUtil.get(key);
		if (obj == null) {
			addr = mapper.selectByPrimaryKey(addrId);
			if (addr != null) {
				redisUtil.set(key, addr, RedisGlobal.ADDR_CACHE_TIME_OUT);
			}
		} else {
			addr = (Addr) obj;
		}

		return addr;
	}

	@Override
	@Transactional
	public void add(Addr addr) {
		addr.setCreateDate(new Date());
		mapper.insertSelective(addr);
	}

	@Override
	public Addr getByLocation(String addr) {
		Example example = new Example(Addr.class);
		example.createCriteria().andEqualTo("location", addr);
		return mapper.selectOneByExample(example);
	}
}
