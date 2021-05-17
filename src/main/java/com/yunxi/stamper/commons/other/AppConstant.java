package com.yunxi.stamper.commons.other;

public class AppConstant {

	/**
	 * 印章设备协议号
	 */
	public static final int MH_MAGIC = 42949207;
	public static final int MH_VERSION = 1;

	public static final int MHPKG_MAGIC = 0xFFFF4A52;//-46508
	public static final int MHPKG_VERSION = 1;//版本号

	public static final int FP_PERMIT_REQ = 1;// M->H 单片机MCU通知host 指纹认证已通过的请求
	public static final int FP_PERMIT_RES = 2;// H->M host返回MCU 指纹认证通过的返回
	public static final int TAKE_PIC_REQ = 3;// M->H　拍照请求
	public static final int TAKE_PIC_RES = 4;// M->H　拍照返回
	public static final int FP_RECORD_REQ = 5;// H->M　指纹录入请求
	public static final int FP_RECORD_RES = 6;// M->H　指纹录入返回
	public static final int FP_CLEAR_REQ = 7;// H->H　指纹清空的请求
	public static final int FP_CLEAR_RES = 8;// M->H　指纹清空的返回
	public static final int HOST_STATUS_REQ = 9;// M->H 核心板状态的请求
	public static final int HOST_STATUS_RES = 10;// H->M 核心板状态的返回
	public static final int MCU_DEVICE_ID_REQ = 11;// H->M 核心板唯一设备码的请求
	public static final int MCU_DEVICE_ID_RES = 12;// M->H 核心板唯一设备码的返回
	public static final int DEVICE_LOGIN_REQ = 13;// H->S 核心板通过websocket的登录请求
	public static final int DEVICE_LOGIN_RES = 14;// S->H 核心板通过websocket的登录返回
	public static final int DEVICE_ACTIVATE_REQ = 15;// S->H 服务器激活核心板的请求
	public static final int DEVICE_ACTIVATE_RES = 16;// H->S 服务器激活核心板的返回
	public static final int DEVICE_NETWORK_REQ = 17;// H->M 核心板的网络状态更新请求
	public static final int DEVICE_NETWORK_RES = 18;// M->H 核心板的网络状态更新返回

	//app 的
	public static final int IS_AUDIT_REQ = 19;//H->S 设备上传地址坐标信息
	public static final int IS_AUDIT_RES = 20;//

	//device 的
	public static final int APPLICATION_STATUS_REQ = 21;//S -> A 申请单状态推送
	public static final int APPLICATION_STATUS_RES = 22;//S -> A 申请单状态推送

	public static final int DEVICE_REG_REQ = 23;// H->S 核心板通过websocket的注册请求
	public static final int DEVICE_REG_RES = 24;// S->H 核心板通过websocket的注册返回

	public static final int CURRENT_APPLICATION_STATUS_REQ = 25;//M->H 当前申请单状态请求
	public static final int CURRENT_APPLICATION_STATUS_RES = 26;//H->M 当前申请单状态返回

	public static final int ANDROID_POWER_CONNECT_STATUS_REQ = 27;//M->H 充电状态请求
	public static final int ANDROID_POWER_CONNECT_STATUS_RES = 28;//H->M 充电状态返回

	//指令号收到后的通知
	public static final int CMD_ACK_REQ = 29;//M->H 指令号收到后的通知
	public static final int CMD_ACK_RES = 30;//H->M 指令号收到后的通知

	//印章设备信息的通知
	public static final int DEVICE_INFO_REQ = 31;//M->H 印章设备信息的通知请求
	public static final int DEVICE_INFO_RES = 32;//H->M 印章设备信息的通知返回

	//印章保存的申请单列表
	public static final int APPLICATION_LIST_REQ = 33;//M->H 印章保存的申请单列表请求
	public static final int APPLICATION_LIST_RES = 34;//H->M 印章保存的申请单列表返回

	//申请单选择
	public static final int APPLICATION_SELECT_REQ = 35;//M->H 印章保存的申请单列表请求
	public static final int APPLICATION_SELECT_RES = 36;//H->M 印章保存的申请单列表返回

	//用章记录提醒
	public static final int RECORD_NOTICE_REQ = 37;//断开wifi链接
	public static final int RECORD_NOTICE_RES = 38;//S->C 盖章后返回

	//通用APP提醒
	public static final int APP_NOTICE_REQ = 39;//目前占位
	public static final int APP_NOTICE_RES = 40;//S->C 盖章后返回

	//印章mcu重启后给host发的通知
	public static final int DEVICE_REBOOT_REQ = 41;//M->H 印章重启的通知请求
	public static final int DEVICE_REBOOT_RES = 42;//H->M 印章重启的通知返回

	public static final int CURRENT_APPLICATION_CLEAR_REQ = 43;//M->H 当前申请单状态结束请求
	public static final int CURRENT_APPLICATION_CLEAR_RES = 44;//H->M 当前申请单状态结束返回

	public static final int APPLICATION_LIST_CLEAR_REQ = 45; //S->C 清空当前申请单列表请求
	public static final int APPLICATION_LIST_CLEAR_RES = 46; //C->S 清空当前申请单列表返回

	public static final int DEVICE_UNLOCK_REQ = 47; //S->C 指纹无效的情况下，扫描解锁请求
	public static final int DEVICE_UNLOCK_RES = 48; //C->S 指纹无效的情况下，扫描解锁返回

	public static final int SLEEP_CHECK_REQ = 49; //M->H 检查是否可休眠的请求
	public static final int SLEEP_CHECK_RES = 50; //H->M 检查是否可休眠的返回

	public static final int DEVICE_SET_WIFI_REQ = 51; //S->H->M 设置wifi的请求
	public static final int DEVICE_SET_WIFI_RES = 52;//H->S->C 设置wifi的返回

	public static final int APPLICATION_END = 53;//S->H 申请单结束

	public static final int TAKE_AUDIT_PIC_REQ = 55; // M->H　审计拍照请求
	public static final int TAKE_AUDIT_PIC_RES = 56; // H->M　审计拍照返回

	public static final int SEAL_RECORD_INFO_DEL_RES = 57;//S->C 记录上传成功的返回

	public static final int OPEN_BLUETOOTH_REQ = 58; //M->H 单片机通知核心板开启蓝牙
	public static final int OPEN_BLUETOOTH_RES = 59; //H->M 核心板返回给单片机是否成功

	public static final int CLOSE_BLUETOOTH_REQ = 60; //M->H 单片机通知核心板关闭蓝牙
	public static final int CLOSE_BLUETOOTH_RES = 61; //H->M 核心板返回给单片机是否成功

	public static final int WIFI_LIST_REQ = 62; //A->C->H app发送wifi list请求，后台传到核心板
	public static final int WIFI_LIST_RES = 63; //H->C->A 核心板返回后台，返回给app

	public static final int WIFI_INFO_RES = 75; //H->C->A 核心板返回给后台网络信息，返回给APP

	public static final int DEVICE_ERR_REQ = 66; //M->H 硬件故障上传请求
	public static final int DEVICE_ERR_RES = 67; //H->S 硬件故障上传返回

	public static final int DEVICE_USED_REQ = 68;//M->H 印章使用中的通知
	public static final int DEVICE_USED_RES = 69;//H->S 印章使用中的返回

	public static final int HIGH_DEVICE_DEAL_REQ = 75;// S->H 审计指令接收
	public static final int HIGH_DEVICE_DEAL_RES = 76;// S->H 审计指令返回

	public static final int DEVICE_INIT_CLEAR_REQ = 77;//S->H 设备次数清0请求
	public static final int DEVICE_INIT_CLEAR_RES = 78;//S->H 设备次数清0返回

	public static final int DEVICE_LOGGER_FILE_UPDATE_REQ = 79;//S->H 设备日志上传请求

	public static final int SLEEP_TIME_REQ = 80;//设置休眠时间
	public static final int SLEEP_TIME_RES = 81;

	public static final int SLEEP_TIME_RETURN_REQ = 82;
	public static final int SLEEP_TIME_RETURN_RES = 83;//设置休眠时间返回

	public static final int USE_MODEL_REQ = 84;//设置使用模式
	public static final int USE_MODEL_RES = 85;

	public static final int USE_MODEL_RETURN_REQ = 86;
	public static final int USE_MODEL_RETURN_RES = 87;//设置使用模式返回

	public static final int REMOTE_LOCK_REQ = 88;//设置远程锁定
	public static final int REMOTE_LOCK_RES = 89;

	public static final int REMOTE_LOCK_RETURN_REQ = 90;
	public static final int REMOTE_LOCK_RETURN_RES = 91;//设置远程锁定返回

	public static final int REMOTE_CAMERA_SWITCH_REQ = 92;//下发摄像头开关指令
	public static final int REMOTE_CAMERA_SWITCH_RES = 93;//摄像头开关ACK

	public static final int DEVICE_MIGRATE_CALLBACK = 102;


	/**
	 * 文件上传
	 */
	public static final long FILE_MAX_SIZE = 1024 * 1024 * 3;//上传文件允许的最大值3M
	public static final double FILE_SMALL_SCALE = 0.5D;//图片压缩比例
	public static final String FILE_SMALL_PREFIX = "small-";//压缩图片前缀

	/**
	 * phone 手机短信
	 */
	public static final int SMS_COUNT = 15; // 同一手机号登录短信一天发送次数
	public static final int SMS_TIME_OUT = 300; // 同一手机号登录短信一天发送次数
	public static final int SMS_MAX_CODE = 100000; // 5位短信验证码
	public static final String SMS_SIGNNAME = "安徽云玺";//手机短信签名
	public static final String SMS_TEMPLATECODE = "SMS_121160795";//申请的短信模板编码
	public static final int freHit = 6;//推送失败的通知+发送失败的短信 重复发送次数值
	/**
	 * 阿里云短信
	 */
	public static final String ALIYUN_ACCESSKEYID = "LTAIhb8W3FQHxYK1";//阿里云KeyId
	public static final String ALIYUN_ACCESSKEYSECRET = "q1Hqv2NvdExlsG9uIhuV4PgY23qDZW";//阿里云secret
	public static final String ALIYUN_CONNECTTIMEOUT = "1000";//链接超时时间
	public static final String ALIYUN_READTIMEOUT = "1000";//超时时间，单位：毫秒
	public static final String ALIYUN_SIGNNAME = "安徽云玺";//短信签名
	public static final String ALIYUN_TEMPLATE_SEALUSER = "SMS_138064634";//用章通知提醒模板
	public static final String ALIYUN_TEMPLATE_NOTICE = "SMS_138069439";//通知提醒模板
	public static final String ALIYUN_TEMPLATE_PHONEVERCODE = "SMS_121160795";//手机验证码模板
	public static final String ALIYUN_OUTID = "";  //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者

	/**
	 * token
	 */
	public static final String ACCESSTOKEN_KEY_PRIFIX = "Authorization";

	/**
	 * 个推
	 */
	public static final String GETUI_APPID = "EG6T66ToSh7Jp3QnDRzzL7";
	public static final String GETUI_APPKEY = "WjatSRPJtp5thmwQbIskh6";
	public static final String GETUI_MASTERSECRET = "6evjMgf9NE7fZHgpOUmvH6";
	public static final String GETUI_URL = "http://sdk.open.api.igexin.com/apiex.htm";

	/**
	 * 响应体状态码
	 */
	public static final int SUCCESS = 0; //成功
	public static final int FAIL = 400;   //普通请求错误(客户端错误)
	public static final int UNLOGIN = 401;   //未登录错误(超时/过期)
	public static final int UNAUTH = 402;   //权限不足(无权限等)
	public static final int UNPASSWORDMATCH = 403;   //密码匹配错误(密码规则不匹配等)

	/**
	 * redis
	 */
	public static final int REDISKEY_TIMEOUT = 60 * 60;//Redis中存储方法级别的数据过期时间

	public static final String REDISKEY_AUTHORIZATION = "SHIRO:AUTHORIZATION:";//shiro权限管理前缀名
	public static final int REDISKEY_AUTHORIZATION_TIMEOUT = 300;//5分钟


	public static final String VERIFYCODE_PREFIX = "randomcode:";//验证码
	public static final int VERIFYCODE_TIMEOUT = 360;//6分钟

	public static final String REDISKEY_NAME_VERIFYCODE = "app@VerifyCode@:"; // redis中手机验证码的前缀名
	public static final int REDISKEY_NAME_VERIFYCODE_TIME_OUT = 300;//300秒

	public static final String WIFI_REDISKEYBY_DEVICE_ID = "WIFI:DEVICEID:";//app获取印章wifi列表
	public static final int WIFI_REDISKEYBY_DEVICE_ID_TIME_OUT = 10;//10秒

	public static final String WIFI_INFO_REDISKEYBY_DEVICE_ID = "WIFIINFO:DEVICEID:";//app获取印章wifi列表
	public static final int WIFI_INFO_REDISKEYBY_DEVICE_ID_TIME_OUT = 10;//10秒

	public static final String WEBSOCKET_DEVICE_UUID = "WEBSOCKET:USE_ON:";//印章使用状态
	public static final int WEBSOCKET_DEVICE_UUID_TIME_OUT = 1800;//30分钟

	public static final String WEBSOCKET_PINGPONG = "WEBSOCKET:ONLINE:";//印章心跳包
	public static final int WEBSOCKET_PINGPONG_TIME_OUT = 10;//10秒

	public static final String WEBSOCKET_DEVICE_LOGIN = "WEBSOCKET:DEVICE:LOGIN:";//设备登录日志过滤时间
	public static final int WEBSOCKET_DEVICE_LOGIN_TIME_OUT = 600;//10分钟

	public static final String NOTICE_REDISKEY_ID = "NOTICE:MESSAGE";//等待发送通知的时间
	public static final int NOTICE_REDISKEY_ID_TIME_OUT = 7200;//2小时

	public static final String NOTICE_TEMPLATE_REDISKEY_ID = "NOTICE:TEMPALATE:ID:";//通知模板
	public static final int NOTICE_TEMPLATE_REDISKEY_ID_TIME_OUT = 604800;//7天

	public static final String LOGINUSER_APP = "LOGINUSER:APP:";//App令牌过期时间
	public static final int LOGINUSER_APP_TIME_OUT = 604800;//7天

	public static final String LOGINUSER_WEB = "LOGINUSER:WEB:";//web令牌过期时间
	public static final int LOGINUSER_WEB_TIME_OUT = 3600;//半小时

	public static final String LOGINUSER_LOGINNAME = "LOGINUSER:LOGINNAME:";//shiro框架获取用户信息
	public static final int LOGINUSER_LOGINNAME_TIME_OUT = 60;//60秒

	public static final String PERMISSION_LIST = "PERMISSION";//数据库权限列表集合
	public static final int PERMISSION_LIST_TIME_OUT = 3600;//3600秒

	public static final String DEVICE_ORDER = "WEBSOCKET:ORDER:DEVICE:";//设备最后一条指令
	public static final int DEVICE_ORDER_TIME_OUT = 30;//60秒

	public static final String USER_ROLE = "ROLE:USER:";//用户角色key
	public static final int USER_ROLE_TIME_OUT = 30;//10秒
	/**
	 * 分页参数
	 */
	public static final Integer PARAM_PAGENOW = 0;//分页参数：默认当前第一页
	public static final Integer PARAM_PAGESIZE = 10;//分页参数：默认每页10条


	/**
	 * 申请单表 0:无状态 1:已提交 2:审批中 3:审批通过 4:审核通过 5:已用章 6:已审计 7:转交审批中 -1: 失效 -2:审批未通过 -3:审核未通过 -4:审计未通过
	 */
	public static final int APPLICATION_STATUS_AUDIT = -1; //失效
	public static final int APPLICATION_STATUS_NONE = 0;//无状态 未设置

	public static final int APPLICATION_STATUS_APPROVE_IN = 2;//审批中
	public static final int APPLICATION_STATUS_APPROVE_DENY = -2; //审批未通过
	public static final int APPLICATION_STATUS_APPROVE_ACCEPT = 3;//审批通过
	public static final int APPLICATION_STATUS_APPROVE_TRANS = 7;//审批转交中

	public static final int APPLICATION_STATUS_VERIFY_ACCEPT = 4;//审核通过
	public static final int APPLICATION_STATUS_VERIFY_DENY = -3; //审核未通过

	public static final int APPLICATION_STATUS_AUDIT_DENY = -4; //审计未通过
	public static final int APPLICATION_STATUS_AUDIT_ACCEPT = 6;//审计通过

	public static final int APPLICATION_STATUS_COMMIT = 1;//已提交
	public static final int APPLICATION_STATUS_USED = 5;//已用章

	//已结束用章 管章人点了结束 如果管章人再点推送则重新启用
	//APPLICATION_STATUS_FIN APPLICATION_STATUS = 7

	/**
	 * 审批人 0:未审批 2:审批中  3:审批通过 -2:审批拒绝 7:已转交
	 */
	public static final int MANAGER_APPLICATION_STATUS_UNKNOWN = 0; //未审批
	public static final int MANAGER_APPLICATION_IN = 2; //审批中
	public static final int MANAGER_APPLICATION_ACCEPT = 3; //审批通过
	public static final int MANAGER_APPLICATION_DENY = -2; //审批拒绝
	public static final int MANAGER_APPLICATION_TRANS = 7; //已转交

	/**
	 * 管章人 0:未授权 -5:授权中  4:授权通过 -3:授权拒绝
	 */
	public static final int APLICATION_KEEPER_STATUS_UNKNOWN = 0;//未授权
	public static final int APLICATION_KEEPER_STATUS_IN = -5;//授权中
	public static final int APLICATION_KEEPER_STATUS_ACCEPT = 4;//授权通过
	public static final int APLICATION_KEEPER_STATUS_DENY = -3;//授权拒绝

	/**
	 * 审计人 '0:未审计 -6:审计中  6:已审计  -4:审计拒绝'
	 */
	public static final int APLICATION_AUDITOR_STATUS_UNKNOWN = 0;//未审计
	public static final int APLICATION_AUDITOR_STATUS_IN = -6;//审计中
	public static final int APLICATION_AUDITOR_STATUS_ACCEPT = 6;//审计通过
	public static final int APLICATION_AUDITOR_STATUS_DENY = -4;//审计拒绝


	/**
	 * 申请单操作日志 '申请0   审批同意 1  拒绝 -1 转交审批 2 授权同意 3  拒绝-3 审计同意 4  拒绝-4',
	 */
	public static final int APPLICATION_DEAL_NEW = 0;//发起申请
	public static final int APPLICATION_DEAL_ACCEPT_MANAGER = 1; //审批同意
	public static final int APPLICATION_DEAL_DENY_MANAGER = -1; //审批拒绝
	public static final int APPLICATION_DEAL_TRANS = 2; //转交审批中
	public static final int APPLICATION_DEAL_ACCEPT_KEEPER = 3;//授权同意
	public static final int APPLICATION_DEAL_DENY_KEEPER = -3;//授权拒绝
	public static final int APPLICATION_DEAL_ACCEPT_AUDITOR = 4;//审计同意
	public static final int APPLICATION_DEAL_DENY_AUDITOR = -4;//审计拒绝


	/**
	 * 申请单-印章状态
	 */
	public static final int APPLICATION_DEVICE_NEW = 0;//印章申请
	public static final int APPLICATION_DEVICE_MANAGER_OK = 2; //审批通过
	public static final int APPLICATION_DEVICE_MANAGER_NO = 3; //审批未通过
	public static final int APPLICATION_DEVICE_KEEPER_OK = 4; //授权通过
	public static final int APPLICATION_DEVICE_KEEPER_NO = 5; //授权未通过
	public static final int APPLICATION_DEVICE_TRANS = 6;//转交
	public static final int APPLICATION_DEVICE_AUDITOR_IN = 9;//审计中
	public static final int APPLICATION_DEVICE_AUDITOR_OK = 8;//审计拒绝
	public static final int APPLICATION_DEVICE_AUDITOR_NO = 7;//审计通过

	/**
	 * 印章状态
	 */
	public static final int DEVICE_STATUS_UNKNOWN = 0;//未知状态
	public static final int DEVICE_STATUS_NORMAL = 1;//正常状态
	public static final int DEVICE_STATUS_OUT = 2;//外借状态
	public static final int DEVICE_STATUS_UNUSED = 3;//停用状态
	public static final int DEVICE_STATUS_DESTROY = 4;//销毁状态

	/**
	 * 钉钉参数
	 * 第三方企业应用，需要企业授权开通应用后才可以使用；在企业授权后，您会收到授权开通事件，正确处理后完成企业授权流程。
	 */
	public static final String CORP_ID = "dinge1653bbce9d6f0f135c2f4657eb6378f";
	public static final String SUITE_KEY = "suite2qc0lultncvu7zas";
	public static final String SUITE_SECRET = "p3Z34H7Wr3tKHphICLBojtXz0tw1pzxDUSqejbpvunb9TMZmshwid2AgjJZUP9eY";
	public static final String DING_TOKEN = "123456";
	public static final String DING_AES_KEY = "3rnsfds5x9whrzke4662ka5zvisokikegp6tnd53psn";//【创建第三方企业应用时获取的】数据加密密钥

	/**
	 * 设备迁移状态 0:未知 1:成功  2:失败
	 */
	public static final int MIGRATE_UNKNOWN = 0;
	public static final int MIGRATE_SUCCESS = 1;
	public static final int MIGRATE_FAIL = 2;
}
