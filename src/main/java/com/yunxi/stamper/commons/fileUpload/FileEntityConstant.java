package com.yunxi.stamper.commons.fileUpload;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/9/11 0011 9:01
 */
public class FileEntityConstant {
	public static final int OK = 0;
	public static final int FILE_IS_ERROR = 1;//图片错误(空的，无效的等)
	public static final int SECRET_KEY_IS_ERROR = 2;//秘钥错误（不存在，无效的等）
	public static final int DECIPHER_FAILED = 3;//解密失败
}
