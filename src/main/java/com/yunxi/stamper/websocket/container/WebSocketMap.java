package com.yunxi.stamper.websocket.container;


import com.yunxi.stamper.commons.jwt.AES.AesUtil;
import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.other.RedisGlobal;
import com.yunxi.stamper.commons.other.RedisUtil;
import com.yunxi.stamper.sys.error.exception.DeviceUnlineException;
import com.yunxi.stamper.sys.error.exception.InvalidCommandException;
import com.yunxi.stamper.websocket.core.WsSocket;
import com.zengtengpeng.annotation.Lock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.websocket.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.websocket.CloseReason;
import javax.websocket.RemoteEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/1 0001 18:16
 */
@Slf4j
@Component
public class WebSocketMap {

	private static final ConcurrentHashMap<Integer, WsSocket> map = new ConcurrentHashMap<>();
	private static RedisUtil redisUtil;

	@Autowired
	public void setRedisUtil(RedisUtil redisUtil) {
		WebSocketMap.redisUtil = redisUtil;
	}

	/**
	 * 正常情况下,websocket版本的设备，在建立链接后，会在大约每3~5秒手动发送1次‘ping’消息，服务端接收到该信息时，会回复‘pong’消息，
	 * 而在每次接收到设备websocket的消息时，会去更新websocket中的lastHeartbeat属性
	 * 当服务端容器WebSocketMap中的链接数达到2000阈值时，调用此方法，进行有效链接检查
	 * 当连接对象的lastHeartbeat大于20s时，认为该链接未无效链接，断开该链接
	 */
	@Scheduled(fixedDelay = 1000 * 60)
	public static void checkConnection() {
		if (map.isEmpty()) {
			return;
		}
		//遍历容器，清除无效链接
		Iterator<Map.Entry<Integer, WsSocket>> its = map.entrySet().iterator();
		while (its.hasNext()) {
			Map.Entry<Integer, WsSocket> en = its.next();
			Integer deviceId = en.getKey();
			WsSocket websocket = en.getValue();

			//设备在约定时间(正常情况下规定10s内必须有心跳，此处以60s作为检测阈值)内进行了`合法`的数据交互，即属于正常行为，否则进行通讯检测
			Date lastHeartbeat = websocket.getLastHeartbeat();
			if (lastHeartbeat != null && (System.currentTimeMillis() - lastHeartbeat.getTime() <= 60000)) {
				continue;
			}

			log.info("--\t关闭无效链接\tdeviceId:{}", deviceId);
			try {
				its.remove();
				websocket.getSession().close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "无效链接!"));
			} catch (Exception e) {
				log.error("删除客户端链接异常\tdeviceId:{}\terror:{}", deviceId, e.getMessage());
			}

		}
	}

	/**
	 * 将设备链接对象加入map
	 *
	 * @param deviceId 设备ID
	 * @param ws       设备链接
	 */
	public static void add(Integer deviceId, WsSocket ws) {
		if (deviceId == null || ws == null) {
			log.error("X\tWsMap添加有误-deviceId:{}\tws:{}", deviceId, ws == null);
			return;
		}

		//key相同时，检查2个链接的sessionId是否相同,如果相同，则是一个链接，如果不相同，则关闭旧的链接
		if (map.containsKey(deviceId)) {
			WsSocket oldWs = map.get(deviceId);
			String oldSessionId = oldWs.getSession().getId();
			Integer oldDeviceId = oldWs.getDeviceId();

			String newSessionId = ws.getSession().getId();
			if (StringUtils.equals(oldSessionId, newSessionId)) {
				return;
			}

			log.info("x\tWsMap重复链接-关闭旧链接\tsessionId:{}\tdeviceId:{}", oldSessionId, oldDeviceId);
			close(deviceId);
		}

		//连接数大于2000阈值时，关闭无效链接
		int size = map.size();
		if (size >= 2000) {
			log.error("xx\tWS链接>=2000!无法加入新链接");
			try {
				ws.getSession().close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "链接数>=2000!无法创建新链接"));
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				checkConnection();
			}
			return;
		}

		//加入容器管理
		map.put(deviceId, ws);
		log.info("ws链接\t总链接:{}", map.size());
	}

	/**
	 * 从map中删除设备链接对象
	 *
	 * @param deviceId 设备ID
	 */
	public static void del(Integer deviceId) {
		if (deviceId == null) {
			return;
		}
		log.info("关闭设备链接-清空缓存\tdeviceId:{}", deviceId);
		redisUtil.del(RedisGlobal.PING + deviceId);
		close(deviceId);
	}

	private static void close(Integer deviceId) {
		WsSocket socket = map.remove(deviceId);
		if (socket != null) {
			try {
				socket.getSession().close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "服务端关闭该链接!"));
				log.info("关闭设备链接-关闭链接\tdeviceId:{}", deviceId);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static WsSocket get(Integer deviceId) {
		return map.get(deviceId);
	}

	/**
	 * 发送普通消息
	 *
	 * @param deviceId 设备ID
	 * @param message  消息内容
	 * @throws IOException 异常
	 */
	public static void send(Integer deviceId, String message) throws IOException {
		WsSocket webSocket = map.get(deviceId);
		if (webSocket == null) {
			log.info("--\t设备不在线\tdeviceId:{}", deviceId);
			redisUtil.del(RedisGlobal.PING + deviceId);
			throw new DeviceUnlineException();
		}
		webSocket.sendByBasic(message);
//		try {
//			webSocket.getSession().getBasicRemote().sendText(message);
//		} catch (IOException e) {
//			log.error("xx\t指令下发失败\tdeviceId:{}\tmessage:{}\terror:{}", deviceId, message, e.getMessage());
//			throw new InvalidCommandException();
//		}
	}

	/**
	 * 发送加密消息
	 *
	 * @param deviceId 设备ID
	 * @param message  消息内容
	 * @throws Exception 异常
	 */
	public static void sendAes(Integer deviceId, String message) throws Exception {
		//查询设备密钥
		String aesKey = redisUtil.getStr(RedisGlobal.AES_KEY + deviceId);
		if (StringUtils.isBlank(aesKey)) {
			log.info("--\t设备不在线\tdeviceId:{}", deviceId);
			redisUtil.del(RedisGlobal.PING + deviceId);
			throw new DeviceUnlineException();
		}
		log.info("-\tws-下发指令\tdeviceId:{}\tmessage:{}", deviceId, message);
		//加密消息
		message = AesUtil.encrypt(message, aesKey);
		send(deviceId, message);
	}

}
