package com.yunxi.stamper.sys.rabbitMq;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/7/16 0016 17:12
 */
public class MqGlobal {
	/***下发命令码 0：通知设备端上传日志 1：申请单推送 2：申请单结束 3：指纹录入 4：指纹删除 5：指纹清空 6：wifi列表获取 7：WiFi链接 8：WiFi断开 9：手动解锁 10:通知高拍仪拍照 11:设备清次(初始化) 12:远程锁定 13:设置休眠时间 14:开启/关闭指纹模式 15:设备迁*/

	// 0：通知设备端上传日志
	public static final int SIGNET_UPLOAD_LOG = 100;
	// 1：申请单推送
	public static final int SIGNET_APPLICATION_PUSH = 101;
	// 2：申请单结束
	public static final int SIGNET_APPLICATION_END = 102;
	// 3：指纹录入
	public static final int SIGNET_FINGER_ADD = 103;
	// 4：指纹删除
	public static final int SIGNET_FINGER_DEL = 104;
	// 5：指纹清空
	public static final int SIGNET_FINGER_CLEAN = 105;
	// 6：wifi列表获取
	public static final int SIGNET_WIFI_LIST = 106;
	// 7：WiFi链接
	public static final int SIGNET_WIFI_LINK = 107;
	// 8：WiFi断开
	public static final int SIGNET_WIFI_CLOSE = 108;
	// 9：手动解锁
	public static final int SIGNET_UNLOCK = 109;
	// 10:通知高拍仪拍照
	public static final int SINGET_NOTICE_METER = 110;
	// 11:设备清次(初始化)
	public static final int SIGNET_INIT = 111;
	// 12:远程锁定
	public static final int SIGNET_REMOTE_LOCK = 112;
	// 13:设置休眠时间
	public static final int SIGNET_SET_SLEEP_TIMES = 113;
	// 14:开启/关闭指纹模式
	public static final int SIGNET_OPEN_OR_CLOSE_FINGER_PATTERN = 114;
	// 15:设备迁移
	public static final int SIGNET_MIGRATE = 115;
	// 16:摄像头开关
	public static final int SIGNET_CAMERA_SWITCH = 116;
}
