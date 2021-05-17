package com.yunxi.stamper.sys.error.exception;

import com.yunxi.stamper.sys.error.base.PrintException;
import com.zengtengpeng.excepiton.LockException;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2020/6/22 16:49
 */
public class LockTimeoutException extends LockException {
	public LockTimeoutException() {
	}

	public LockTimeoutException(String message) {
		super(message);
	}
}
