package com.yunxi.stamper.sys.error.exception;

import com.yunxi.stamper.sys.error.base.PrintException;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/4 0004 0:24
 */
public class UnknownParamsException extends PrintException {
	public UnknownParamsException() {
	}

	public UnknownParamsException(String message) {
		super(message);
	}
}
