package com.yunxi.stamper.sys.error.exception;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2021/1/4 11:10
 */
public class DeviceUnlineException extends RuntimeException {
	public DeviceUnlineException() {
	}

	public DeviceUnlineException(String message) {
		super(message);
	}
}
