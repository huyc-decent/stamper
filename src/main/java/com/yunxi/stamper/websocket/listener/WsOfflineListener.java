package com.yunxi.stamper.websocket.listener;

import com.yunxi.stamper.commons.other.RedisGlobal;
import com.yunxi.stamper.entity.User;
import com.yunxi.stamper.websocket.container.WebSocketMap;
import com.yunxi.stamper.websocket.core.WsSocket;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


/**
 * Websocket通道心跳超时监听器，此处使用redis缓存过期监听器作为websocket掉线处理
 */
@Slf4j
@Component
public class WsOfflineListener extends KeyExpirationEventMessageListener {

	public WsOfflineListener(RedisMessageListenerContainer redisMessageListenerContainer) {
		super(redisMessageListenerContainer);
	}

	/**
	 * 针对redis数据失效事件的订阅，当监听到数据失效事件触发时，进行数据处理
	 * ps：此处该方法主要监听WS版本的设备离线，当设备上线时，会在redis中存储一个对应的心跳记录
	 * 每次WS的设备发送消息时，会更新该消息过期时间，当长时间未接收到WS设备消息(业务消息、指令消息以及心跳消息)时，
	 * 服务端默认为该设备离线
	 *
	 * @param message 存储在redis中的缓存key
	 * @param pattern
	 */
	@Override
	public void onMessage(Message message, byte[] pattern) {
		//仅监听设备心跳信息
		String expiredKey = message.toString();
		if (!expiredKey.startsWith(RedisGlobal.PING)) {
			return;
		}

		//截取设备该心跳对应的UUID
		String deviceId = expiredKey.replace(RedisGlobal.PING, "");

		if (StringUtils.isBlank(deviceId) || "null".equals(deviceId)) {
			return;
		}

		//日志
		log.info("-\t设备离线\tdeviceId:{}", deviceId);
	}
}
