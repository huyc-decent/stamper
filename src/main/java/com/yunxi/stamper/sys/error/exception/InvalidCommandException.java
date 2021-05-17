package com.yunxi.stamper.sys.error.exception;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2021/1/4 11:10
 */
public class InvalidCommandException extends RuntimeException {
	public InvalidCommandException() {
	}

	public InvalidCommandException(String message) {
		super(message);
	}
}
