package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.WechatControl;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2021/3/10 13:29
 */
public interface WeChatService {
	void add(WechatControl wechatControl);

	WechatControl get(Integer wechatId);

	void delete(WechatControl wechat);

	void update(WechatControl wechat);

	List<WechatControl> list();

	WechatControl getByOpenId(String openId);
}
