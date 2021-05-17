package com.yunxi.stamper.commons.enums;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2021/3/17 14:52
 */
public enum ApplicationStatusEnum {
	INIT(0, "提交"),
	APPROVAL_PROCESSING(1, "审批中"),
	APPROVAL_CONSENT(2, "审批通过"),
	APPROVAL_DENIED(3, "审批拒绝"),
	AUTHORIZE_PROCESSING(4, "授权中"),
	AUTHORIZE_CONSENT(5, "授权通过"),
	AUTHORIZE_DENIED(6, "授权拒绝"),
	ISSUE_ORDERS(7, "已推送"),
	IN_USE(8, "用章中"),
	USE_COMPLETED(9, "已用章"),
	AUDIT_PROCESSING(10, "审计中"),
	AUDIT_CONSENT(11, "审计通过"),
	AUDIT_DENIED(12, "审计拒绝"),
	CANCEL(13, "已失效");

	private int code;
	private String msg;

	ApplicationStatusEnum(int code, String msg) {
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
