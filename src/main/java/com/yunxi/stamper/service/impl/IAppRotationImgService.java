package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.AppRotationImg;
import com.yunxi.stamper.mapper.AppRotationImgMapper;
import com.yunxi.stamper.service.AppRotationImgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/28 0028 12:38
 */
@Service
public class IAppRotationImgService implements AppRotationImgService {
	@Autowired
	private AppRotationImgMapper mapper;

	@Override
	public AppRotationImg get(Integer id) {
		if (id == null) {
			return null;
		}
		Example example = new Example(AppRotationImg.class);
		example.createCriteria().andIsNull("deleteDate").andEqualTo("id", id);
		return mapper.selectOneByExample(example);
	}


	@Override
	public AppRotationImg getByOrderNo(Integer orderNo) {
		if (orderNo == null) {
			return null;
		}
		Example example = new Example(AppRotationImg.class);
		example.createCriteria().andIsNull("deleteDate").andEqualTo("orderNo", orderNo);
		return mapper.selectOneByExample(example);
	}

	@Override
	@Transactional
	public void add(AppRotationImg img) {
		int addCount = 0;
		if (img != null) {
			img.setCreateDate(new Date());
			addCount = mapper.insert(img);
		}
		if (addCount != 1) {
			throw new PrintException("轮播图添加失败");
		}
	}

	@Override
	@Transactional
	public void del(AppRotationImg img) {
		int delCount = 0;
		if (img != null && img.getId() != null) {
			img.setDeleteDate(new Date());
			delCount = mapper.updateByPrimaryKey(img);
		}
		if (delCount != 1) {
			throw new PrintException("轮播图删除失败");
		}
	}

	@Override
	public List<AppRotationImg> getList() {
		Example example = new Example(AppRotationImg.class);
		example.createCriteria().andIsNull("deleteDate");
		example.orderBy("orderNo");
		return mapper.selectByExample(example);
	}
}
