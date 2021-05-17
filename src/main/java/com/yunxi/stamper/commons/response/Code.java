package com.yunxi.stamper.commons.response;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/4/23 0023 13:20
 */
public enum Code {
	OK(200, "请求成功"),
	ERROR500(500, "系统错误"),
	ERROR501(501, "服务暂时不可用"),
	ERROR502(502, "令牌初始化异常"),
	ERROR503(503, "缓存服务暂时不可用"),
	ERROR510(510, "所需策略没有被满足"),
	USERNAME_IS_EMPTY(599, "个人信息不完善"),
	TO_LOGIN_EXCEPTION600(600, "登录超时,请重新登录"),
	TO_LOGIN_EXCEPTION601(601, "登录有误,请重新登录"),//令牌不正确
	TO_LOGIN_EXCEPTION604(604, "登录过期,请重新登录"),
	TO_LOGIN_EXCEPTION606(606, "账号已登出，请重新登录"),//令牌不存在
	TO_LOGIN_EXCEPTION607(607, "您的账号已在其他客户端登录"),
	TO_LOGIN_EXCEPTION608(608, "信息已更新，请重新登录"),
	TO_LOGIN_EXCEPTION609(609, "令牌无效，请重新登录"),
	TO_LOGIN_EXCEPTION610(610, "会话失效，请重新登录"),
	FAIL400(400, "请求失败"),
	FAIL403(403, "很抱歉,您无权限访问该功能"),
	FAIL401(401, "尊敬的用户,您的账户未激活,请联系公司管理员激活后才能使用"),
	FAIL402(402, "提交参数有误"),
	FAIL405(405, "不支持的请求格式"),
	FAIL404(404, "提交参数有误(undefined)"),
	FAIL406(406, "用户状态异常，请联系公司管理员更新用户状态"),
	FAIL407(407, "业务繁忙，请稍后重试"),

	//第三方错误码
	/**
	 * 接口验证对应：80000-89999
	 */
	AUTH_ORGCODE(82000, "appKey不存在"),
	AUTH_PARAMETER(81111, "appKey,appSecret参数有误"),
	AUTH_TIMEOUT(81110, "验证超时，有效时间一分钟"),
	/**
	 * 申请单对应30000-39999
	 */
	APPLICATION_PARAMETER(31111, "申请单参数有误"),
	APPLICATION_USE_COUNT(30001, "申请单申请次数值必须大于0"),
	/**
	 * 设备对应20000-29999
	 */
	DEVICE_OFFLINE_MESSAGE(20010, "设备当前不在线,指令会在设备开机后执行"),
	DEVICE_LOCK(20101, "设备已被锁定,操作失败"),
	DEVICE_TIMEOUT(21100, "设备休眠时间只能在2~10分钟"),
	DEVICE_WIFI_PARAMETER(21000, "wifi相关请求，参数值有误"),
	DEVICE_NULL(20010, "设备不存在"),
	DEVICE_FINGERADDRESS(20001, "指纹地址不能为0"),
	DEVICE_OFFLINE(20000, "设备不在线"),
	DEVICE_PARAMETER(21111, "传入参数有误"),
	VALUE_11111(11111, "参数错误"),
	RES_19999(19999, "返回值为空"),
	ORG_100001(10001, "当前组织设备数量为0"),
	ORG_10000(10000, "当前组织不存在");

	private int code;
	private String msg;

	Code(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
