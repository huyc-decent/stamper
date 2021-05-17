package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.Meter;
import com.yunxi.stamper.mapper.MeterMapper;
import com.yunxi.stamper.service.MeterService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/9 0009 15:39
 */
@Service
public class IMeterService implements MeterService {

	@Autowired
	private MeterMapper mapper;


	@Override
	@Transactional
	public void add(Meter meter) {
		int addCount = 0;

		if(meter!=null) {
			meter.setCreateDate(new Date());
			addCount = mapper.insert(meter);

		}
		if(addCount!=1){
			throw new PrintException("高拍仪添加失败");
		}
	}

	@Override
	public Meter getByUUID(String uuid) {
		if (StringUtils.isNotBlank(uuid)) {
			Example example = new Example(Meter.class);
			example.createCriteria().andIsNull("deleteDate")
					.andEqualTo("uuid", uuid);
			return mapper.selectOneByExample(example);
		}
		return null;
	}

	@Override
	@Transactional
	public void del(Meter meter) {
		int delCount = 0;
		if (meter != null && meter.getId() != null) {
			meter.setDeleteDate(new Date());
			delCount = mapper.updateByPrimaryKey(meter);
		}
		if (delCount != 1) {
			throw new PrintException("高拍仪删除失败");
		}
	}

	@Override
	@Transactional
	public void update(Meter meter) {
		int update = 0;
		if (meter != null) {
			meter.setUpdateDate(new Date());
			update = mapper.updateByPrimaryKey(meter);
		}

		if (update != 1) {
			throw new PrintException("高拍仪更新失败");
		}
	}


	/**
	 * 查询公司高拍仪
	 *
	 * @param orgId
	 * @return
	 */
	@Override
	public List<Meter> getByOrg(Integer orgId) {
		if (orgId != null) {
			Example example = new Example(Meter.class);
			example.createCriteria().andIsNull("deleteDate")
					.andEqualTo("orgId", orgId);
			return mapper.selectByExample(example);
		}
		return null;
	}

	/**
	 * 查询公司高拍仪
	 *
	 * @param orgIds
	 * @return
	 */
	@Override
	public List<Meter> getByOrg(List<Integer> orgIds) {
		if (orgIds != null && orgIds.size() > 0) {
			Example example = new Example(Meter.class);
			example.createCriteria().andIsNull("deleteDate")
					.andIn("orgId", orgIds);
			return mapper.selectByExample(example);
		}
		return null;
	}

	/**
	 * 查询所属人的高拍仪
	 *
	 * @param userId
	 * @return
	 */
	@Override
	public List<Meter> getByUser(Integer userId) {
		if (userId != null) {
			Example example = new Example(Meter.class);
			example.createCriteria().andIsNull("deleteDate")
					.andEqualTo("ownerId", userId);
			return mapper.selectByExample(example);
		}
		return null;
	}

	@Override
	public Meter get(Integer meterId) {
		if (meterId != null) {
			Example example = new Example(Meter.class);
			example.createCriteria().andIsNull("deleteDate")
					.andEqualTo("id", meterId);
			return mapper.selectOneByExample(example);
		}
		return null;
	}
}
