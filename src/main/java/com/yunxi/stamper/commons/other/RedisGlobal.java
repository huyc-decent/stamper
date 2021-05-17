package com.yunxi.stamper.commons.other;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/4/24 0024 19:51
 */
public class RedisGlobal {
	//存储量子演示消息
	public static final String QSS_DEMO_KEY = "qss:device:";	//如:qss:device:20110:${UUID}
	public static final long QSS_DEMO_KEY_TIMEOUT = 300;	//存300s

	//职称临时缓存
	public static final String POSITION_BY_USER = "POSITION:USER:";//POSITION:USER:${userId} ==> 董事长、经理
	public static final long POSITION_BY_USER_TIMEOUT = 10;

	//手机短信验证码前缀 格式:PHONE_VERIFI_CODE:1:手机号码
	public static final String PHONE_VERIFI_CODE = "PHONE_VERIFI_CODE:";//PHONE_VERIFI_CODE:${num}  num==>0:用户注册  1:修改密码   2:重置密码	3:登录  4:注册组织   5:添加员工
	public static final int PHONE_VERIFI_CODE_TIMEOUT = 300;

	//存储用户短信发送数量 key、timeout(当天23:59:59清空)
	public static final String phone_sms_total = "phone_sms_total:";//phone_sms_total:${phone}

	//存储用户短信发送间隔 key、timeout
	public static final String PHONE_SMS_LIMIT = "phone_sms_limit:";//phone_sms_limit:${phone}
	public static final int PHONE_SMS_LIMIT_TIMEOUT = 60;

	//图片验证码前缀  格式:RANDOM_VERIFI_CODE:验证码id
	public static final String RANDOM_VERIFI_CODE = "RANDOM_VERIFI_CODE:";
	public static final long RANDOM_VERIFI_CODE_TIMEOUT = 120;

	public static final String APPLICATION_ASYNC = "async_application:";//如:ASYNC_APPLICATION:${申请单ID}:${设备ID}:${已用次数}
	public static final long APPLICATION_ASYNC_TIMEOUT = 600;

	//设备wifi列表
	public static final String DEVICE_WIFI_LIST = "DEVICE_WIFI:";//如:DEVICE_WIFI:${印章id}
	public static final long DEVICE_WIFI_LIST_TIMEOUT = 30;

	//设备信息
	public static final String DEVICE_INFO = "DEVICE_INFO:";//如:DEVICE_INFO::${印章UUID}
	public static final long DEVICE_INFO_TIMEOUT = 3600;//存一个小时

	//设备心跳
	public static final String PING = "PING:";//如:PING:${印章id}
	public static final long PING_TIME_OUT = 20;//20秒

	//地址信息
	public static final String ADDR_CACHE = "ADDR:";//:ADDR:${addrId}
	public static final long ADDR_CACHE_TIME_OUT = 1800;

	//申请报表
	public static final String REPORT_USER = "REPORT:USER:";//REPORT:USER:${userId}:{时间戳}
	public static final long REPORT_USER_TIMEOUT = 10;//

	//设备阈值
	public static final String THRESHOLD_SIGNET = "DEVICE:THRESHOLD:";//如:DEVICE:THRESHOLD:${印章ID}
	public static final long THRESHOLD_SIGNET_TIME_OUT = 10;//存储10秒(专门给使用记录使用)

	//文件上传
	public static final String FILE_PREFIX_KEY = "FILE:USER:";//如:FILE:USER:{userId}:{时间戳}
	public static final long FILE_PREFIX_KEY_TIME_OUT = 60;

	//申请单创建
	public static final String APPLICATION_PREFIX_KEY = "APPLICATION:USER:";//如:APPLICATION:USER:{userId}:{时间戳}
	public static final long APPLICATION_PREFIX_KEY_TIME_OUT = 60;

	//对称密钥token
	public static final String AES_KEY = "AESKEY:DEVICE:";//如：AESKEY:DEVICE:{印章Id}

	/**
	 * 用户信息
	 */

	//登录失败记录信息
	public static final String user_login_fail_locked = "loginFail:locked:";//用户登录次数 如:loginFail:locked:${userId} == new Date()
	public static final long user_login_fail_locked_timeout = 5*60;

	//APP令牌
	public static final String USER_INFO_TOKEN_APP = "USER:INFO:TOKEN:APP:";//用户令牌 如:USER:INFO:TOKEN:APP:$(userID)
	public static final long USER_INFO_TOKEN_APP_TIMEOUT = 1000 * 60 * 60 * 24 * 7;

	//WEB令牌
	public static final String USER_INFO_TOKEN_WEB = "USER:INFO:TOKEN:WEB:";//用户令牌 如:USER:INFO:TOKEN:WEB:$(userID)
	public static final long USER_INFO_TOKEN_WEB_TIMEOUT = 1000 * 60 * 60 * 24;

	/**
	 * 申请单
	 */
	public static final String LOCK_APPLICATION = "LOCK:APPLICATION:";//锁定申请单 如:LOCK:APPLICATION:${applicationId}
	public static final long LOCK_APPLICATION_TIMEOUT = 3;//10秒

	/**
	 * 组织信息
	 */

	//公司信息 该信息会一直存储在redis中
	public static final String ORG_INFO = "ORG:INFO:";//公司信息

	//组织信息缓存
	public static final String DEPARTMENT_BY_ID = "DEPARTMENT:ID:";//DEPARTMENT:ID:${departmentId} == > {组织信息}
	public static final long DEPARTMENT_BY_ID_TIMEOUT = 3600;

	//申请单使用次数通知
	public static final String APPLICATION_IS_USER = "APPLICATION:";//申请单使用次数缓存 如:APPLICATION:${申请单ID}:${印章ID}:${次数值}
	public static final long APPLICATION_IS_USER_TIMEOUT = 60 * 60 * 24;//存1天

	/**
	 * 设备信息
	 */
	//指纹录入
	public static final String DEVICE_FINGER = "FINGER:DEVICE:";//
	public static final long DEVICE_FINGER_TIMEOUT = 300;

	//设备指令下发频率
	public static final String DEVICE_CONTROL = "DEVICE_CONTROL:";//DEVICE_CONTROL:${印章Id}
	public static final long DEVICE_CONTROL_TIMEOUT = 5;//5秒

	//用印人信息(1)，利用缓存服务器，提高用印上传速度
	public static final String IDENTITY_USERID_INFO = "IDENTITY:USERID:";//IDENTITY:USERID:{userId}
	public static final long IDENTITY_USERID_INFO_TIMEOUT = 300;//存5分钟

	//用印人信息(2)，利用缓存服务器，提高用印上传速度
	public static final String IDENTITY_ORG_USERNAME_INFO = "IDENTITY:ORG:USERNAME:";//IDENTITY:ORG:USERNAME:${orgId}:${张三}
	public static final long IDENTITY_ORG_USERNAME_TIMEOUT = 300;//存5分钟

	//拆卸弹出通知标记
	public static final String NOTICE_DEMOLISH = "demolish:";//emolish:{userId}
}
