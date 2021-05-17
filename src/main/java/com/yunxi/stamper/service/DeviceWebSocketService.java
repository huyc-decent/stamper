package com.yunxi.stamper.service;


import com.yunxi.stamper.websocket.core.WsSocket;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/2 0002 19:32
 */
public interface DeviceWebSocketService {
	void doWork(String message, WsSocket webSocket);

	/**
	 * 指纹录入返回
	 * ps:(以最后一次为准,将前一次进行覆盖)
	 */
	void fpRecordRes(@NotEmpty String message, @NotEmpty String uuid);

	/**
	 * 指纹清空返回结果
	 * @param message 清空返回消息体
	 */
	void fpClearRes(@NotEmpty String message, @NotEmpty String uuid);

	/**
	 * 网络状态改变返回
	 *
	 * @param message  消息体
	 * @param uuid 设备UUID
	 */
	void getWifiInfoRes(@NotEmpty String message, @NotEmpty String uuid);

	/**
	 * 印章开关锁状态的返回
	 * @param message 消息体
	 * @param deviceId 设备ID
	 */
	void deviceUsingRes(@NotEmpty String message, @NotNull Integer deviceId);

	/**
	 * 设备上传地址坐标信息
	 *
	 * @param message  消息体
	 * @param uuid 设备UUID
	 */
	void updatePosition(@NotEmpty String message, @NotEmpty String uuid);

	/**
	 * 设置休眠 状态返回
	 * {"Body":{"res":0,"sleepTime":4},"Head":{"Magic":42949207,"Cmd":83,"SerialNum":980,"Version":1}}
	 */
	void updateSleepTime(@NotEmpty String message, @NotEmpty String uuid);

	/**
	 * wifi列表返回结果
	 */
	void getWifiListRes(@NotEmpty String message, @NotEmpty String uuid);

	/**
	 * 印章通知高拍仪进行拍照
	 */
	void highDeviceOnUsing(@NotEmpty String message, @NotNull String uuid);

	/**
	 * 印章使用模式状态返回
	 * {"Body":{"res":0,"useModel":1},"Head":{"Magic":42949207,"Cmd":87,"SerialNum":672,"Version":1}}
	 */
	void updateSignetModel(@NotEmpty String message, @NotEmpty String uuid);

	/**
	 * 远程锁定功能 状态返回
	 * {"Body":{"res":0,"status":1},"Head":{"Magic":42949207,"Cmd":91,"SerialNum":901,"Version":1}}
	 */
	void updateRemoteLock(@NotEmpty String message, @NotNull String uuid);
}
