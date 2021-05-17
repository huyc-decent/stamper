package com.yunxi.stamper.sys.rabbitMq;

import com.alibaba.fastjson.JSONObject;
import com.yunxi.stamper.sys.error.base.PrintException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/7/16 0016 17:07
 */
@Component
public class MqSender {
	@Autowired
	private AmqpTemplate amqpTemplate;

	/**
	 * 向交换机中推送消息
	 *
	 * @param exchange 交换器
	 * @param obj      消息体
	 */
	public void sendToExchange(String exchange, Object obj) {
		if (obj == null || StringUtils.isBlank(obj.toString())) {
			throw new PrintException("请求参数有误");
		}
		amqpTemplate.convertAndSend(exchange, "", JSONObject.toJSONString(obj));
	}


	/**
	 * 向队列中推送消息
	 *
	 * @param routek 队列
	 * @param obj    消息体
	 */
	public void sendToQueue(String routek, Object obj) {
		if (obj == null || StringUtils.isBlank(obj.toString())) {
			throw new PrintException("请求参数有误");
		}
		amqpTemplate.convertAndSend(routek, JSONObject.toJSONString(obj));
	}

}
