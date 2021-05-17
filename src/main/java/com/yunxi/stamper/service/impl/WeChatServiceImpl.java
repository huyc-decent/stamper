package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.entity.WechatControl;
import com.yunxi.stamper.mapper.WechatControlMapper;
import com.yunxi.stamper.service.WeChatService;
import com.yunxi.stamper.sys.error.base.PrintException;
import lombok.extern.slf4j.Slf4j;
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
 * @date 2021/3/10 13:29
 */
@Slf4j
@Service
public class WeChatServiceImpl implements WeChatService {
	@Autowired
	private WechatControlMapper mapper;

	@Override
	@Transactional
	public void add(WechatControl wechat) {
		int insert = 0;
		if (wechat != null) {
			wechat.setId(null);
			wechat.setCreateDate(new Date());
			wechat.setUpdateDate(new Date());
			wechat.setDeleteDate(null);
			insert = mapper.insert(wechat);
		}
		if (insert != 1) {
			log.info("x\t小程序添加有误\t数据:{}", CommonUtils.objJsonWithIgnoreFiled(wechat));
			throw new PrintException("小程序添加有误");
		}
	}

	@Override
	public WechatControl get(Integer wechatId) {
		if (wechatId == null) {
			return null;
		}
		return mapper.selectByPrimaryKey(wechatId);
	}

	@Override
	@Transactional
	public void delete(WechatControl wechat) {
		int delete = 0;
		if (wechat != null) {
			wechat.setDeleteDate(new Date());
			delete = mapper.updateByPrimaryKeySelective(wechat);
		}
		if (delete != 1) {
			log.info("x\t小程序删除有误\t数据:{}", CommonUtils.objJsonWithIgnoreFiled(wechat));
			throw new PrintException("小程序删除有误");
		}
	}

	@Override
	@Transactional
	public void update(WechatControl wechat) {
		int update = 0;
		if (wechat != null) {
			wechat.setUpdateDate(new Date());
			update = mapper.updateByPrimaryKey(wechat);
		}
		if (update != 1) {
			log.info("x\t小程序更新有误\t数据:{}", CommonUtils.objJsonWithIgnoreFiled(wechat));
			throw new PrintException("小程序更新有误");
		}
	}

	@Override
	public List<WechatControl> list() {
		Example example = new Example(WechatControl.class);
		example.createCriteria().andIsNull("deleteDate");
		return mapper.selectByExample(example);
	}

	@Override
	public WechatControl getByOpenId(String openId) {
		if (StringUtils.isBlank(openId)) {
			return null;
		}
		Example example = new Example(WechatControl.class);
		example.createCriteria().andIsNull("deleteDate")
				.andEqualTo("customerWxOpenId", openId);
		return mapper.selectOneByExample(example);
	}
}
