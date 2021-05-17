package com.yunxi.stamper.sys.error.exception;

import com.yunxi.stamper.sys.error.base.PrintException;

/**
 * @author zhf_10@163.com
 * @Description 上传图片不存在异常
 * @date 2019/5/5 0005 10:14
 */
public class NotImgException extends PrintException {
	public NotImgException() {
		super("图片不存在");
	}

	public NotImgException(String message) {
		super(message);
	}
}
