package com.yunxi.stamper.sys.error.base;

/**
 * @author zhf_10@163.com
 * @Description 需要去登录的异常(基类)
 * @date 2019/5/4 0004 0:24
 */
public class ToLoginException extends RuntimeException {
	public ToLoginException() {
		super("登录超时,请重新登录");
	}

	public ToLoginException(String message) {
		super(message);
	}
}
