package com.yunxi.stamper.service.async;

import com.yunxi.stamper.websocket.container.WebSocketMap;
import com.yunxi.stamper.entity.DeviceMessage;
import com.yunxi.stamper.entity.Signet;
import com.yunxi.stamper.service.DeviceAsyncService;
import com.yunxi.stamper.service.DeviceMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/2 0002 21:12
 */
@Slf4j
@Service
public class IDeviceAsyncService implements DeviceAsyncService {

	@Autowired
	private DeviceMessageService deviceMessageService;

	/**
	 * 异步推送离线消息
	 */
	@Override
	@Async
	public void pushUnline(Signet signet) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			log.error("出现异常 ", e);
		}
		if (signet != null && WebSocketMap.get(signet.getId()) != null) {
			//TODO:推送离线消息
			List<DeviceMessage> deviceMessages = deviceMessageService.getBySignet(signet.getId());
			if (deviceMessages != null && deviceMessages.size() > 0) {
				for (int i = 0; i < deviceMessages.size(); i++) {
					DeviceMessage dm = deviceMessages.get(i);
					if (dm == null) {
						continue;
					}
					if (StringUtils.isBlank(dm.getBody())) {
						dm.setPushStatus(0);
						deviceMessageService.update(dm);
						continue;
					}
					try {
						WebSocketMap.sendAes(signet.getId(), dm.getBody());
						//指令下发成功
						dm.setPushStatus(0);
						deviceMessageService.update(dm);
						log.info("成功===>离线消息{}:设备{} 消息标题：【{}】 消息内容：【{}】", i + 1, dm.getRecipientId(), dm.getTitle(), dm.getBody());
						Thread.sleep(1000);
					} catch (Exception e) {
						log.info("失败===>离线消息{}:设备{} 消息标题：【{}】 消息内容：【{}】", i + 1, dm.getRecipientId(), dm.getTitle(), dm.getBody());
					}
				}
				return;
			}
			log.info("离线消息不存在===>设备：【{}】", signet.getId());
		}
	}
}
