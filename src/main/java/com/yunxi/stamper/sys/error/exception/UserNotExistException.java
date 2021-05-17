package com.yunxi.stamper.sys.error.exception;

import com.yunxi.stamper.sys.error.base.ToLoginException;

/**
 * @author zhf_10@163.com
 * @Description 用户不存在异常
 * @date 2019/4/26 0026 17:50
 */
public class UserNotExistException extends ToLoginException {
	public UserNotExistException() {
	}

	public UserNotExistException(String message) {
		super(message);
	}
}
