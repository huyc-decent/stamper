package com.yunxi.stamper.sys.error;

import com.auth0.jwt.exceptions.InvalidClaimException;
import com.yunxi.common.exception.file.CustomFileNotExistException;
import com.yunxi.common.exception.file.CustomFileTooBigException;
import com.yunxi.stamper.commons.response.Code;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.sys.context.SpringContextUtils;
import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.sys.error.base.ToLoginException;
import com.yunxi.stamper.sys.error.exception.UserNotExistException;
import com.zengtengpeng.excepiton.LockException;
import io.lettuce.core.RedisConnectionException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.net.SocketTimeoutException;


/**
 * controller层全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(LockException.class)
	public ResultVO lockException(LockException e) {
		return ResultVO.FAIL(Code.FAIL407);
	}

	@ExceptionHandler(CustomFileNotExistException.class)
	@ResponseBody
	public ResultVO handleCustomFileNotExistException(CustomFileNotExistException e) {
		printURI(e, "文件不存在");
		return ResultVO.FAIL("文件不存在");
	}

	@ExceptionHandler(CustomFileTooBigException.class)
	@ResponseBody
	public ResultVO handleCustomFileTooBigException(CustomFileTooBigException e) {
		printURI(e, "文件过大");
		return ResultVO.FAIL("文件过大");
	}

	/**
	 * 需要用户登录的异常
	 */
	@ExceptionHandler(ToLoginException.class)
	public ResultVO notToken(Exception e) {
		printURI(e, "用户登录异常");
		return ResultVO.FAIL(Code.TO_LOGIN_EXCEPTION606);
	}

	/**
	 * 需要用户登录的异常(token解析出现错误)
	 */
	@ExceptionHandler(InvalidClaimException.class)
	public ResultVO invalitToken(InvalidClaimException e) {
		printURI(e, "token解析异常");
		return ResultVO.ERROR(Code.TO_LOGIN_EXCEPTION604);
	}

	/**
	 * 需要提醒用户消息的异常(需要展示错误消息给前台)
	 */
	@ExceptionHandler(PrintException.class)
	public ResultVO notLogin(Exception e) {
		e.printStackTrace();
		return ResultVO.FAIL(Code.FAIL400);
	}

	/**
	 * 请求参数缺失异常
	 */
	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseBody
	public ResultVO handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
		printURI(e, "请求参数异常");
		return ResultVO.FAIL(Code.FAIL402);
	}

	/**
	 * 请求参数格式不支持
	 */
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	@ResponseBody
	public ResultVO handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
		printURI(e, "参数格式不支持异常");
		return ResultVO.FAIL(Code.FAIL405);
	}


	/**
	 * 请求参数转换异常
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseBody
	public ResultVO handleMissingServletRequestParameterException(MethodArgumentTypeMismatchException e) {
		printURI(e, "参数转换异常");
		return ResultVO.FAIL(Code.FAIL402);
	}

	private void printURI(Exception e, String message) {
		String requestURI = null;
		try {
			requestURI = SpringContextUtils.getRequestURI();
		} catch (Exception e1) {
		}
		LOGGER.error(message + "-->" + requestURI, e);
	}

	/**
	 * redis链接异常
	 */
	@ExceptionHandler(RedisConnectionException.class)
	@ResponseBody
	public ResultVO handleRedisConnectionException(RedisConnectionException e) {
		printURI(e, "Redis链接异常");
		return ResultVO.FAIL("缓存服务器暂时不可用");
	}

	/**
	 * 用户不存在
	 */
	@ExceptionHandler(UserNotExistException.class)
	@ResponseBody
	public ResultVO handleUserNotExistException(UserNotExistException e) {
		printURI(e, "用户无效异常");
		return ResultVO.FAIL(Code.TO_LOGIN_EXCEPTION604);
	}

	/**
	 * 文件超过设置最大值
	 */
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	@ResponseBody
	public ResultVO handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
		printURI(e, "文件过大异常");
		return ResultVO.FAIL("文件过大");
	}

	/**
	 * 请求参数缺失异常(undefined)
	 */
	@ExceptionHandler(BindException.class)
	@ResponseBody
	public ResultVO handleBindException(BindException e) {
		printURI(e, "参数缺失异常(undefined)");
		return ResultVO.FAIL(Code.FAIL402);
	}

	/**
	 * 请求超时异常(undefined)
	 */
	@ExceptionHandler(SocketTimeoutException.class)
	@ResponseBody
	public ResultVO handleSocketTimeoutException(SocketTimeoutException e) {
		printURI(e, "请求超时异常");
		return ResultVO.FAIL("请求超时,请稍后重试");
	}

	/**
	 * 其他未处理的异常
	 */
	@ExceptionHandler(Exception.class)
	public ResultVO other(Exception e) {
		printURI(e, "其他未知异常");
		return ResultVO.ERROR(Code.ERROR500);
	}

}
