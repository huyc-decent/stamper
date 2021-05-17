package com.yunxi.stamper.websocket.core;


import com.yunxi.stamper.service.DeviceWebSocketService;
import com.yunxi.stamper.websocket.container.WebSocketMap;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/2 0002 19:13
 */
@Slf4j
@Setter
@Getter
@Component
@ServerEndpoint("/device/ws")
public class WsSocket {

	private Session session;
	private String ip;        //链接IP
	private Integer orgId;   //组织ID
	private Integer deviceId; //对端链接的设备ID
	private String uid;      //对端链接的UUID
	private boolean busy = true;        //true:使用中  false:空闲中

	private Date lastHeartbeat = null; //最后一次 接收消息/心跳 时间

	private static DeviceWebSocketService wsBizService;//业务处理对象

	@Override
	public String toString() {
		return "WsSocket{" + "session=" + session +
				", ip='" + ip + '\'' +
				", orgId=" + orgId +
				", deviceId=" + deviceId +
				", uid='" + uid + '\'' +
				", busy=" + busy +
				", lastHeartbeat=" + lastHeartbeat +
				'}';
	}

	@OnOpen
	public void onOpen(Session session) {
		this.session = session;

		//新的WS链接，如果2分钟内没有进行过任何交互(发送消息或接收消息)，则断开链接
		this.session.setMaxIdleTimeout(120 * 1000);//空闲超时 2分钟
//		this.session.getUserProperties().put(Constants.BLOCKING_SEND_TIMEOUT_PROPERTY, 20000L);//设置发送消息超时时间10s

		//解析链接对端IP
		this.ip = WsUtils.getRemoteAddress(session);

		log.info("ws链接\tip:{}\tsessionId:{}", this.ip, this.session.getId());
	}

	@OnClose
	public void onClose() {
		log.info("关闭设备链接\tdeviceId:{}\tip:{}\tuuid:{}", deviceId, ip, uid);

		//清空该设备缓存信息&容器中连接对象
		if (this.deviceId != null) {
			WebSocketMap.del(this.deviceId);
		}
	}

	@OnError
	public void onError(Session session, Throwable error) {
		log.error("设备触发事件-异常\tdeviceId:{}\tip:{}\tuuid:{}\terror:{}", deviceId, ip, uid, error.getMessage());
		error.printStackTrace();
	}

	@OnMessage
	public void onMessage(String message, Session session) throws IOException {
		//日志
		//log.error("新消息\tdeviceId:{}\tip:{}\tuuid:{}\tmessage:{}", deviceId, ip, uuid, message);

		//业务处理
		try {
			wsBizService.doWork(message, this);
		} catch (Exception e) {
			log.error("业务异常\tdeviceId:{}\tip:{}\tuuid:{}\tmessage:{}\terror:{}", deviceId, ip, uid, message, e.getMessage());
			e.printStackTrace();
		}
	}

	public static void setWsBizService(DeviceWebSocketService wsBizService) {
		WsSocket.wsBizService = wsBizService;
	}

	public void sendByBasic(String message){
		synchronized (session) {
			try {
				this.session.getBasicRemote().sendText(message);
			} catch (Exception e) {
				log.error("x\tws发送-异常\tdeviceId:{}\tip:{}\tuuid:{}\tmessage:{}\terror:{}", deviceId, ip, uid, message, e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
