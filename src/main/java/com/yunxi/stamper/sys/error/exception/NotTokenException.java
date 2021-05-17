package com.yunxi.stamper.sys.error.exception;

import com.yunxi.stamper.sys.error.base.ToLoginException;

/**
 * @author zhf_10@163.com
 * @Description token不存在异常
 * @date 2019/4/26 0026 17:39
 */
public class NotTokenException extends ToLoginException {
	public NotTokenException() {
		super("令牌不存在,请重新登录");
	}

	public NotTokenException(String message) {
		super(message);
	}
}
