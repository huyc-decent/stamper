package com.yunxi.stamper.sys.error.exception;

import com.yunxi.stamper.sys.error.base.PrintException;

/**
 * @author zhf_10@163.com
 * @Description 参数不正确异常
 * @date 2019/5/4 0004 0:30
 */
public class UnParamsException extends PrintException {
	public UnParamsException() {
		super("参数不正确");
	}

	public UnParamsException(String message) {
		super(message);
	}
}
