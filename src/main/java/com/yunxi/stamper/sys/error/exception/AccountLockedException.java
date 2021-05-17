package com.yunxi.stamper.sys.error.exception;

import com.yunxi.stamper.sys.error.base.PrintException;

/**
 * @author zhf_10@163.com
 * @Description 账号锁定异常
 * @date 2020/6/23 18:03
 */
public class AccountLockedException extends PrintException {
	public AccountLockedException() {
	}

	public AccountLockedException(String message) {
		super(message);
	}
}
