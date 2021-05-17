package com.yunxi.stamper.websocket;

import com.yunxi.stamper.service.DeviceWebSocketService;
import com.yunxi.stamper.websocket.core.WsSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/1 0001 19:13
 */
@Slf4j
@Component
public class WebSocketInit implements CommandLineRunner {

	@Autowired
	private DeviceWebSocketService deviceWebSocketService;

	@Override
	public void run(String... args) throws Exception {
		log.info("...........初始化Websocket容器...........");
		WsSocket.setWsBizService(deviceWebSocketService);
		log.info("...........初始化Websocket容器完成...........");
	}
}
