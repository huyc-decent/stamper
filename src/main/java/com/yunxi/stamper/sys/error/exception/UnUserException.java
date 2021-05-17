package com.yunxi.stamper.sys.error.exception;

import com.yunxi.stamper.sys.error.base.PrintException;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/5 0005 13:42
 */
public class UnUserException extends PrintException {
	public UnUserException() {
		super("指定用户不存在");
	}

	public UnUserException(String message) {
		super(message);
	}
}
