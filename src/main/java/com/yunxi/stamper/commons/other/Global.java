package com.yunxi.stamper.commons.other;

import org.springframework.stereotype.Component;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/3 0003 0:23
 */
@Component
public class Global {
	public static final Object fingerAddObject = new Object();

	//默认角色Code列表
	public static final String codes = "admin,manager,auditor,keeper,user";
	//设备离线消息标题
	public static final String open_close_finger_pattern = "开启/关闭指纹模式";
	public static final String set_sleep_times = "设置休眠";
	public static final String set_remote_lock = "设置远程锁定";
	public static final String set_migrate = "设备迁移";

	public static final String sessionkeyid = "sessionkeyid";//密文索引的key
	public static final String ticket = "ticket";//秘钥的key
	public static final String ciphertext = "content_quantum";//请求体密文的key

	//设备配置信息
	public static final String defaultUUID = "0XFFFFFFFFFFFFFFFFFFFFFFFFF";//默认全局配置的uuid值

	//请求头参数
	public static final String TOKEN_PREFIX = "Authorization";//token前缀
	public static final String HEAD_VERSION = "yxVersion";//客户端版本
	public static final String US = "us";//区分app和web的标记 web端：us=web  app端：无us标记

	//使用记录状态
	public static final int ERROR = -1;//异常
	public static final int NORMAL = 0;//正常
	public static final int WARNING = 1;//警告
	public static final int UNKNOWN = 2;//未知，等待解析

	//设备状态
	public static final int DEVICE_NORMAL = 0;//正常
	public static final int DEVICE_ERROR = 1;//异常
	public static final int DEVICE_DESTROY = 2;//销毁
	public static final int DEVICE_STOP = 3;//停用
	public static final int DEVICE_LOCK = 4;//锁定

	//权限作用域
	public static final String QSS_URL = "QSS_URLS:";//全局需要加密的URL 如:移动端 QSS_URLS:1:[]  或者web端 QSS_URLS:0:[]
	public static final long QSS_URL_TIME_OUT = 1800;

	//需要校验Token和权限的路径集合
	public static final String GLOBAL_PERMS = "GLOBAL_PERMS";

	//用户相关
	public static final int USER_STATUS_NORMAL = 0;//正常
	public static final int USER_STATUS_LOCK = 1;//锁定

	//用户登录
	public static final int USER_LOGIN_TIMES = 3;//用户重试登录次数5次

	//图片(或使用记录)类型 0:申请单/指纹 1:审计 2:超次 3:超时 4:防拆  5:追加图片
	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_AUDITOR = 1;
	public static final int TYPE_OVERTIMES = 2;
	public static final int TYPE_TIMEOUT = 3;
	public static final int TYPE_DEMOLISH = 4;
	public static final int TYPE_REPLENISH = 5;

	/**
	 * 异常包含如下:
	 * 1.无用印盖章图片
	 * 2.无超时监控图片
	 * 3.无超次盖章图片
	 * 4.无用印人
	 * 5.超次盖章
	 * 6.拆卸监控
	 */
	//异常信息
	public static final String ERROR01 = "无用印盖章图片";
	public static final String ERROR03 = "无指纹用印人";
	public static final String ERROR06 = "无使用计数";
	public static final String ERROR07 = "超出申请单使用次数";
	public static final String ERROR08 = "拆卸报警";
	public static final String ERROR09 = "使用超时异常";
	public static final String ERROR10 = "无超时盖章图片";
	public static final String ERROR13 = "无超次盖章图片";
	public static final String ERROR11 = "审计驳回";

	/**
	 * 警告包含如下:
	 * 1.无申请单号
	 * 2.无地址信息
	 */
	public static final String ERROR05 = "无地址信息";
	public static final String ERROR04 = "无申请单号";

	//申请单 0:初始化提交 1:审批中 2:审批通过 3:审批拒绝  4:授权中 5:授权通过 6:授权拒绝 7:已推送  8:用章中 9:已用章 10:审计中 11:审计通过 12:审计拒绝 13:已失效
	public static final int APP_INIT = 0;
	public static final int APP_MANAGER = 1;
	public static final int APP_MANAGER_OK = 2;
	public static final int APP_MANAGER_FAIL = 3;
	public static final int APP_KEEPER = 4;
	public static final int APP_KEEPER_OK = 5;
	public static final int APP_KEEPER_FAIL = 6;
	public static final int APP_PUSHED = 7;
	public static final int APP_USEING = 8;
	public static final int APP_USERED = 9;
	public static final int APP_AUDITOR = 10;
	public static final int APP_AUDITOR_OK = 11;
	public static final int APP_AUDITOR_FAIL = 12;
	public static final int APP_CANCEL = 13;

	//审批  0:未审批 1:审批中 2:审批同意 3:审批拒绝 4:审批转交  5:已失效'
	public static final int MANAGER_INIT = 0;
	public static final int MANAGER_HANDLING = 1;
	public static final int MANAGER_SUCCESS = 2;
	public static final int MANAGER_ERROR = 3;
	public static final int MANAGER_TRANS = 4;
	public static final int MANAGER_CANCEL = 5;

	//1:授权中 2:授权同意 3:授权拒绝 4:已失效
	public static final int KEEPER_HANDLING = 1;
	public static final int KEEPER_SUCCESS = 2;
	public static final int KEEPER_ERROR = 3;

	//审计 1:审计中 2:审计同意 3:审计拒绝
	public static final int AUDITOR_HANDLING = 1;
	public static final int AUDITOR_SUCCESS = 2;
	public static final int AUDITOR_ERROR = 3;

	//审批流程 图标
	public static final int ICON_WAIT = -1;//等待处理
	public static final int ICON_INIT = 0;//初始化提交
	public static final int ICON_SUCCESS = 1;//同意
	public static final int ICON_FAIL = 2;//拒绝
	public static final int ICON_TRANS = 3;//转交
	public static final int ICON_CANCEL = 4;//取消

	//审批流程 执行状态 -1:已处理 0:处理中  1:未处理
	public static final int HANDLE_COMPLETE = -1;//已处理
	public static final int HANDLE_ING = 0;//处理中
	public static final int HANDLE_COMPLETE_NO = 1;//未处理

	//申请单 节点处理方式 and:会签 or:或签 list:依次审批 manager:主管审批 init:初始化提交 cancel:取消申请
	public static final String FLOW_INIT = "init";//初始化提交
	public static final String FLOW_AND = "and";//会签
	public static final String FLOW_OR = "or";//或签
	public static final String FLOW_LIST = "list";//依次审批
	public static final String FLOW_MANAGER = "manager";//主管审批
	public static final String FLOW_CANCEL = "cancel";//取消申请
	public static final String FLOW_OPTIONAL = "optional";//自选

	public static final String NODE_NAME_BY_MANAGER = "审批";
	public static final String NODE_NAME_BY_KEEPER = "授权";
	public static final String NODE_NAME_BY_AUDITOR = "审计";
}
