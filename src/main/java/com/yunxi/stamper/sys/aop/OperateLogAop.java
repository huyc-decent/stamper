package com.yunxi.stamper.sys.aop;

import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.other.Global;
import com.yunxi.stamper.commons.other.RedisUtil;
import com.yunxi.stamper.commons.jwt.JwtUtil;
import com.yunxi.stamper.commons.jwt.UserToken;
import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.entity.UserLogger;
import com.yunxi.stamper.sys.rabbitMq.MqSender;
import com.yunxi.stamper.sys.aop.annotaion.WebLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @author zhf_10@163.com
 * @Description 用户操作日志记录
 * @date 2019/3/26 0026 16:02
 */
@Slf4j
@Aspect
@Component
public class OperateLogAop {
	@Autowired
	protected RedisUtil redisUtil;
	@Autowired
	private MqSender mqsender;

	/**
	 * web端+App端相关切入点
	 */
	@Pointcut("execution(public * com.yunxi.stamper.controller.*Controller.*(..))")
	public void log() {
	}

	/**
	 * 日志AOP记录
	 */
	@Around("log()")
	public Object doBefore(ProceedingJoinPoint joinPoint) throws Throwable {
		Object target = joinPoint.getTarget();
		String methodName = joinPoint.getSignature().getName();
		Class[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameterTypes();

		//通过反射获得拦截的method
		Method method = target.getClass().getMethod(methodName, parameterTypes);
		if (method == null) {
			return joinPoint.proceed();
		}

		UserLogger userLogger = null;
		WebLogger annotation = method.getAnnotation(WebLogger.class);
		Object returnVal = null;
		if (annotation == null) {
			/**
			 * 如果方法上没有注解@WebLogger，就不管
			 */
			return joinPoint.proceed();
		} else {

			ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			HttpServletRequest request = attributes.getRequest();
			/**
			 * 创建操作日志实体
			 */
			try {
				userLogger = createLogger(request, joinPoint);
			} catch (Exception e) {
			}

			/**
			 * 根据注解中的key值,获取请求方法中指定索引(key)的值,组装成value值,存储到数据库
			 */
			String keyParam = getKeyParam(annotation, joinPoint);

			/**
			 * 执行具体的请求操作
			 */
			try {
				returnVal = joinPoint.proceed();
			} catch (Throwable throwable) {

				/**
				 * 请求出现异常,更新日志实体参数
				 */
				try {
					loggerFail(userLogger, throwable, keyParam);
				} catch (Exception e) {
					log.error("出现异常 ", e);
				}

				throwable.printStackTrace();

				/**
				 * 将异常抛出去
				 */
				throw throwable;
			}

			/**
			 * 将日志提交到消息队列
			 */
			try {
				if (userLogger != null) {
					userLogger.setStatus(0);
					userLogger.setRemark(keyParam);
					mqsender.sendToExchange(CommonUtils.getProperties().getRabbitMq().getExchangeLogs(), userLogger);
				}
			} catch (Exception e) {
				log.error("出现异常 ", e);
			}

			return returnVal;

		}
	}

	/**
	 * 请求出错
	 */
	private void loggerFail(UserLogger ul, Throwable throwable, String value) {
		try {
			if (ul != null) {
				ul.setStatus(2);
				ul.setRemark(value + "出现错误");
				ul.setError(throwable.getMessage());
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 根据注解中的key值,获取请求方法中指定索引(key)的值,组装成value值,存储到数据库
	 */
	private String getKeyParam(WebLogger annotation, ProceedingJoinPoint joinPoint) {
		String value = annotation.value();
		int index = annotation.key();
		if (StringUtils.isNotBlank(value) && value.contains("#") && index >= 0) {
			try {
				Object keyParam = joinPoint.getArgs()[index];
				if (keyParam != null && StringUtils.isNotBlank(keyParam.toString())) {
					value = value.replace("#", keyParam.toString()).trim();
				}
			} catch (Exception e) {
				log.error("出现异常 ", e);
			}
		}
		return value;
	}

	/**
	 * 创建操作日志
	 */
	private UserLogger createLogger(HttpServletRequest request, ProceedingJoinPoint joinPoint) {
		//请求URL
		String url = request.getRequestURI();

		//客户端IP
		String remoteAddrIP = request.getRemoteAddr();

		//操作客户实体
		UserToken userToken = null;
		try {
			userToken = getToken(request);
		} catch (Exception e) {
		}
		if (userToken == null) {
			return null;
		}

		//创建日志实体
		UserLogger ul = new UserLogger();

		//请求客户端类型
		boolean app = false;
		try {
			app = BaseController.isApp();
		} catch (Exception e) {
		}

		//记录请求客户端类型
		ul.setClient(app ? "移动端" : "浏览器");

		//记录请求IP
		ul.setIp(remoteAddrIP);

		//记录请求参数
		try {
			ul.setArgs(CommonUtils.getArgsByReq(joinPoint));
		} catch (Exception e) {
			//请求参数记录出错
			ul.setArgs(joinPoint.toString());
		}

		//记录请求URL
		ul.setUrl(url);
		ul.setUserId(userToken.getUserId());
		ul.setUserName(userToken.getUserName());
		ul.setOrgId(userToken.getOrgId());
		return ul;
	}


	private UserToken getToken(HttpServletRequest request) {
		String tokenJson = request.getHeader(Global.TOKEN_PREFIX);
		if (StringUtils.isBlank(tokenJson)) {
			tokenJson = request.getParameter(Global.TOKEN_PREFIX);
		}
		UserToken token = JwtUtil.getJWT(tokenJson);
		return token;
	}
}
