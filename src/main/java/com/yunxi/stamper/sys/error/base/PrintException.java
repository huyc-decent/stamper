package com.yunxi.stamper.sys.error.base;

/**
 * @author zhf_10@163.com
 * @Description 需要将异常信息展示的异常(基类)
 * @date 2019/5/4 0004 0:25
 */
public class PrintException extends RuntimeException{
	public PrintException() {
	}

	public PrintException(String message) {
		super(message);
	}
}
