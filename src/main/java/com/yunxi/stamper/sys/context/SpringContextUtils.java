package com.yunxi.stamper.sys.context;

import com.github.pagehelper.PageHelper;
import com.yunxi.stamper.commons.other.Global;
import com.yunxi.stamper.commons.other.RedisUtil;
import com.yunxi.stamper.logger.threadLocal.LocalHandle;
import com.yunxi.stamper.sys.error.base.ToLoginException;
import com.yunxi.stamper.commons.jwt.JwtUtil;
import com.yunxi.stamper.commons.jwt.UserToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class SpringContextUtils implements ApplicationContextAware {
	private static ApplicationContext context;

	private static RedisUtil redisUtil;

	public static String getRemoteHost() {
		HttpServletRequest request = getRequest();
		if(request==null){
			throw new RuntimeException();
		}
		String remoteHost = request.getRemoteHost();
		return remoteHost;
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		SpringContextUtils.context = context;
	}

	public static ApplicationContext getContext() {
		return context;
	}

	/**
	 * 查询请求头中的token字符串
	 */
	private static String getTokenStr() {
		HttpServletRequest request = SpringContextUtils.getRequest();
		if (request == null) {
			throw new RuntimeException();
		}
		return getTokenStr(request);
	}

	private static String getTokenStr(HttpServletRequest request) {
		String tokenJson = request.getHeader(Global.TOKEN_PREFIX);
		if (StringUtils.isBlank(tokenJson)) {
			tokenJson = request.getParameter(Global.TOKEN_PREFIX);
			if (StringUtils.isBlank(tokenJson)) {
				throw new ToLoginException("令牌不存在");
			}
		}
		return tokenJson;
	}

	/**
	 * 解析请求头中的token字符串
	 */
	public static UserToken getToken() {
		UserToken userToken = LocalHandle.get().getUserToken();
		if (userToken != null) {
			return userToken;
		}

		UserToken jwt = JwtUtil.getJWT(getTokenStr());
		if (jwt != null) {
			return jwt;
		}
		throw new ToLoginException("登录超时,请重新登录");
	}

	public static boolean getPage() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new RuntimeException();
		}
		String isPage = request.getParameter("page");
		boolean toPage = false;
		if (StringUtils.isNotBlank(isPage)) {
			try {
				toPage = Boolean.parseBoolean(isPage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return toPage;
	}

	/**
	 * 从请求头中获取分页参数
	 */
	public static boolean setPage() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new RuntimeException();
		}
		String size = request.getParameter("pageSize");
		String num = request.getParameter("pageNum");
		int pageSize = 10;
		if (StringUtils.isNotBlank(size)) {
			try {
				pageSize = Integer.parseInt(size);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		int pageNum = 1;
		if (StringUtils.isNotBlank(num)) {
			try {
				pageNum = Integer.parseInt(num);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		boolean toPage = getPage();
		if (toPage) {
			PageHelper.startPage(pageNum, pageSize);
		}
		return toPage;
	}

	public static HttpServletRequest getRequest() {
		try {
			ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			if (requestAttributes != null) {
				return requestAttributes.getRequest();
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	public static String getRequestURI() {
		try {
			HttpServletRequest request = getRequest();
			if (request == null) {
				throw new RuntimeException();
			}
			String requestURI = request.getRequestURI();
			if (StringUtils.isNotBlank(requestURI) && requestURI.startsWith("//")) {
				requestURI = requestURI.substring(1);
			}
			return requestURI;
		} catch (Exception e) {
			log.error("出现异常 ", e);
			return null;
		}
	}

	public static RedisUtil getRedisUtil() {
		return redisUtil;
	}

	@Autowired
	public void setRedisUtil(RedisUtil redisUtil) {
		SpringContextUtils.redisUtil = redisUtil;
	}
}
