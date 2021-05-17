package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.Signet;

/**
 * @author zhf_10@163.com
 * @Description 异步接口
 * @date 2019/5/2 0002 21:12
 */
public interface DeviceAsyncService {
	//推送离线申请单
	void pushUnline(Signet signet);
}
