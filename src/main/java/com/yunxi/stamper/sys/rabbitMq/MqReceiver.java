package com.yunxi.stamper.sys.rabbitMq;

import com.alibaba.fastjson.JSONObject;
import com.mysql.jdbc.CacheAdapter;
import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.mq.MQPKG;
import com.yunxi.stamper.commons.other.RedisGlobal;
import com.yunxi.stamper.commons.other.RedisUtil;
import com.yunxi.stamper.controller.*;
import com.yunxi.stamper.demoController.DemoEntity;
import com.yunxi.stamper.demoController.QssDemoEntity;
import com.yunxi.stamper.entity.UserLogger;
import com.yunxi.stamper.logger.service.LoggerService;
import com.yunxi.stamper.service.UserLoggerService;
import com.yunxi.stamper.websocket.container.WebSocketMap;
import com.yunxi.stamper.websocket.core.WsSocket;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * 消息队列-消费者
 */
@Slf4j
@Component
public class MqReceiver {

	@Autowired
	private SignetController signetController;
	@Autowired
	private FingerController fingerController;
	@Autowired
	private UserLoggerService userLoggerService;
	@Autowired
	private LoggerService loggerService;
	@Autowired
	private RedisUtil redisUtil;

	/**
	 * 新版日志系统
	 *
	 * @param msg 消息内容
	 */
	@RabbitListener(bindings = @QueueBinding(
			exchange = @Exchange(value = "${project.logger.exchange}", durable = "false", type = ExchangeTypes.FANOUT),
			value = @Queue(value = "${project.logger.queue}", durable = "false", autoDelete = "true")))
	public void receiverFromLogger(String msg) {
		if (!CommonUtils.properties.getLogger().isRecord()) {
			return;
		}
		try {
			loggerService.excute(msg);
		} catch (Exception e) {
			log.error("RabbitMq-接收-日志系统-异常 msg:{} ", msg, e);
		}
	}

	/**
	 * 演示系统web端与服务端消息通讯
	 *
	 * @param msg 消息内容
	 */
	@RabbitListener(bindings = @QueueBinding(
			exchange = @Exchange(value = "${project.rabbitMq.exchangeDemo}", durable = "false", type = ExchangeTypes.FANOUT),
			value = @Queue(value = "${project.rabbitMq.queueDemo}", durable = "false", autoDelete = "true")))
	public void receiverFromQssBySignetMsg(String msg) {
		try {
			DemoEntity de = JSONObject.parseObject(msg, DemoEntity.class);
			if (de == null || de.getDeviceId() == null || de.getMessage() == null || de.getMessage().isEmpty()) {
				return;
			}

			String redisKey = RedisGlobal.QSS_DEMO_KEY + de.getDeviceId();
			List<QssDemoEntity> messages = de.getMessage();
			if (messages != null && !messages.isEmpty()) {
				redisUtil.set(redisKey + ":" + UUID.randomUUID().toString(), JSONObject.toJSONString(messages), RedisGlobal.QSS_DEMO_KEY_TIMEOUT);
			}
		} catch (Exception e) {
			log.error("RabbitMq-接收-消息通讯-异常 msg:{} ", msg, e);
		}
	}

	/**
	 * 监听用户操作日志
	 * durable:消息队列是否持久化
	 * autoDelete消息队列是否删除
	 * 功能描述:
	 * 1.监听消息队列中的日志信息
	 */
	@RabbitListener(bindings = @QueueBinding(
			exchange = @Exchange(value = "${project.rabbitMq.exchangeLogs}", durable = "false", type = ExchangeTypes.FANOUT),
			value = @Queue(value = "${project.rabbitMq.queueLogs}", durable = "false", autoDelete = "true")))
	public void receiverInfoLog(String mqMailInfo) {
		if (!CommonUtils.getProperties().isInsertLogger() || StringUtils.isBlank(mqMailInfo)) {
			return;
		}

		try {
			//解析消息体，插入数据库
			UserLogger userLogger = JSONObject.parseObject(mqMailInfo, UserLogger.class);
			if (userLogger != null) {
				userLoggerService.add(userLogger);
			}
		} catch (Exception e) {
			log.error("日志记录监听器出现错误:{}", mqMailInfo, e);
		}
	}

	/**
	 * 监听消息(下发给设备的指令)
	 * durable:消息队列是否持久化
	 * autoDelete消息队列是否删除
	 */
	@RabbitListener(bindings = @QueueBinding(
			exchange = @Exchange(value = "${project.rabbitMq.exchangeOrder}", durable = "false", type = ExchangeTypes.FANOUT),
			value = @Queue(value = "${project.rabbitMq.queueOrder}", durable = "false", autoDelete = "true")))
	public void receiver(String mqpkgJson) {
		try {
			if (StringUtils.isNotBlank(mqpkgJson)) {
				MQPKG pkg = JSONObject.parseObject(mqpkgJson, MQPKG.class);
				if (pkg != null) {
					//该设备是否在当前服务器
					Integer deviceId = pkg.getDeviceId();
					WsSocket socket = WebSocketMap.get(deviceId);
					if (socket == null) {
						return;
					}

					log.info("-\tMQ-下发指令-序列号:{}\tmessage:{}", pkg.getSerialId(), CommonUtils.objJsonWithIgnoreFiled(pkg));

					int cmd = pkg.getCmd();

					switch (cmd) {
						case MqGlobal.SIGNET_UPLOAD_LOG:
							signetController._getLogFile(pkg);
							break;
						case MqGlobal.SIGNET_APPLICATION_PUSH:
							signetController.pushApplication(pkg);
							break;
						case MqGlobal.SIGNET_APPLICATION_END:
							signetController.endApplication(pkg);
							break;
						case MqGlobal.SIGNET_FINGER_ADD:
							fingerController._fingerPrint(pkg);
							break;
						case MqGlobal.SIGNET_FINGER_DEL:
							fingerController._cleanOne(pkg);
							break;
						case MqGlobal.SIGNET_FINGER_CLEAN:
							fingerController._cleanAll(pkg);
							break;
						case MqGlobal.SIGNET_WIFI_LIST:
							signetController._getWifiList(pkg);
							break;
						case MqGlobal.SIGNET_WIFI_LINK:
							signetController._setWifiLink(pkg);
							break;
						case MqGlobal.SIGNET_WIFI_CLOSE:
							signetController._closeWifiLink(pkg);
							break;
						case MqGlobal.SIGNET_UNLOCK:
							signetController._unlock(pkg);
							break;
						case MqGlobal.SINGET_NOTICE_METER:
							signetController.meterToUse(pkg);
							break;
						case MqGlobal.SIGNET_INIT:
							signetController._initializtion(pkg);
							break;
						case MqGlobal.SIGNET_OPEN_OR_CLOSE_FINGER_PATTERN:
							signetController._openPatternFinger(pkg);
							break;
						case MqGlobal.SIGNET_SET_SLEEP_TIMES:
							signetController._setDormancy(pkg);
							break;
						case MqGlobal.SIGNET_MIGRATE:
							signetController._migrate(pkg);
							break;
						case MqGlobal.SIGNET_REMOTE_LOCK:
							signetController._setRemoteLock(pkg);
							break;
						case MqGlobal.SIGNET_CAMERA_SWITCH:
							signetController._cameraSwitch(pkg);
							break;
						default:
					}
				}
			}
		} catch (Exception e) {
			log.error("RabbitMq-接收-设备指令-异常 mqpkgJson:{} ", mqpkgJson, e);
		}
	}

}
