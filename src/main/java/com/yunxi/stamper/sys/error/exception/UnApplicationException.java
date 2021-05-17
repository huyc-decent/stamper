package com.yunxi.stamper.sys.error.exception;

import com.yunxi.stamper.sys.error.base.PrintException;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/5 0005 13:59
 */
public class UnApplicationException extends PrintException {
	public UnApplicationException() {
		super("申请单不存在");
	}

	public UnApplicationException(String message) {
		super(message);
	}
}
