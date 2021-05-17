package com.yunxi.stamper.websocket.core;

import lombok.Data;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2020/11/19 22:07
 */
@Data
public class WsMessage {
	private int cmd;//协议号
	private String uuid;//设备UUID
	private String encrypt;//密文

	private String context;//解密后的明文内容

	public WsMessage() {
	}

	public WsMessage(String context, int cmd) {
		this.cmd = cmd;
		this.context = context;
	}
}
