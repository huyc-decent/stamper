package com.yunxi.stamper.sys.filter.auth;

import com.alibaba.fastjson.JSONObject;
import com.yunxi.stamper.commons.jwt.JwtUtil;
import com.yunxi.stamper.commons.jwt.UserToken;
import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.other.Global;
import com.yunxi.stamper.commons.other.RedisGlobal;
import com.yunxi.stamper.commons.other.RedisUtil;
import com.yunxi.stamper.commons.response.Code;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entityVo.UserInfo;
import com.yunxi.stamper.logger.model.LocalModel;
import com.yunxi.stamper.logger.threadLocal.LocalHandle;
import com.yunxi.stamper.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description 对请求进行校验
 * @date 2019/12/23 0023 9:54
 */
@Slf4j
@Order(1)
@Component
public class AuthFilter extends OncePerRequestFilter {

	@Autowired
	private IgnorePath ignoredPath;
	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private UserInfoService userInfoService;

	/**
	 * 需要校验Token和权限的路径集合
	 */
	private static List<String> GLOBAL_URLS = null;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
		String method = request.getMethod();
		if (HttpMethod.OPTIONS.name().equals(method)) {
			returnCors(request, response);
			chain.doFilter(request, response);
			return;
		}

		//请求时间戳
		long startTimes = System.currentTimeMillis();

		//请求路径
		String path = request.getRequestURI();

		//请求端地址
		String hostName = request.getHeader("X-real-ip");
		hostName = StringUtils.isBlank(hostName) ? request.getRemoteHost() : hostName;

		//请求用户ID
		Integer userId = null;

		//请求用户名称
		String userName = null;

		String authorization = null;
		try {
			/*
			 	如果用户访问 http://yx.qstamper.com/、websocket请求、需要忽略校验token的请求,就直接放过去
			 */
			if (ignoredPath.isIgnore(path)) {
				chain.doFilter(request, response);
				return;
			}

			/*
				校验token有效性
			 */
			authorization = getParameterByRequest(request, Global.TOKEN_PREFIX);
			if (StringUtils.isBlank(authorization)) {
				returnJson(response, ResultVO.ERROR(Code.TO_LOGIN_EXCEPTION610));
				return;
			}

			boolean validate = JwtUtil.validate(authorization);
			if (!validate) {
				returnJson(response, ResultVO.ERROR(Code.TO_LOGIN_EXCEPTION609));
				return;
			}

			/*
				查询用户信息
			 */
			UserToken userToken = getUserToken(authorization);
			UserInfo userInfo = userInfoService.get(userToken.getUserId());
			if (userInfo == null) {
				returnJson(response, ResultVO.ERROR(Code.TO_LOGIN_EXCEPTION608));
				return;
			}

			LocalModel localModel = LocalHandle.get();
			localModel.setUserToken(userToken);
			localModel.setUserInfo(userInfo);
			localModel.setHost(hostName);

			userId = userInfo.getId();
			userName = userInfo.getUserName();

			/*
				检验用户账户状态
			 */
			if (userInfo.getStatus() == Global.USER_STATUS_LOCK) {
				returnJson(response, ResultVO.FAIL("您的账户已被管理员停用,请联系贵公司管理员启用!"));
				return;
			}

			/*
				个人信息是否完善
			 */
			String us = getParameterByRequest(request, Global.US);
			boolean app = StringUtils.isBlank(us);

			localModel.setIsApp(app);

			if (StringUtils.isNotBlank(path) && StringUtils.isBlank(userInfo.getUserName())) {
				if (app && "/auth/user/getPhoneIcons".equalsIgnoreCase(path)) {
					returnJson(response, ResultVO.FAIL(Code.USERNAME_IS_EMPTY));
					return;
				} else if (!app && !StringUtils.equalsAnyIgnoreCase(path, "/auth/user/get", "/auth/user/myUpdate", "/file/fileInfo/uploadFile")) {
					returnJson(response, ResultVO.FAIL(Code.USERNAME_IS_EMPTY));
					return;
				}
			}

			/*
			  缓存中之前存储的令牌是否还存在
			  如果用户是登录后进来的，缓存中一定存在他的令牌缓存，如果缓存中不存在，则是令牌过期了，需要客户重新登录
			 */
			Object tokenOBJ = redisUtil.get((app ? RedisGlobal.USER_INFO_TOKEN_APP : RedisGlobal.USER_INFO_TOKEN_WEB) + userInfo.getId());
			if (tokenOBJ == null || StringUtils.isBlank(tokenOBJ.toString())) {
				returnJson(response, ResultVO.FAIL(Code.TO_LOGIN_EXCEPTION604));
				log.error("times:{}ms\tpath:{}\tmethod:{}\tuserId:{}\tuserName:{}\thost:{}\tuserToken:{}",
						System.currentTimeMillis() - startTimes,
						path, method,
						userId, userName,
						hostName,
						authorization);
				return;
			}

			/*
			  如果用户提交的令牌与缓存中的不一致，
			  则是其他客户端重新登录生成过新的令牌，当前登录失效，需要客户重新登录1次
			 */
			if (!tokenOBJ.toString().equalsIgnoreCase(authorization)) {
				returnJson(response, ResultVO.FAIL(Code.TO_LOGIN_EXCEPTION607));
				log.error("times:{}ms\tpath:{}\tmethod:{}\tuserId:{}\tuserName:{}\thost:{}\tuserToken:{}\tlocalToken:{}",
						System.currentTimeMillis() - startTimes,
						path,
						method,
						userId,
						userName,
						hostName,
						authorization, tokenOBJ.toString());
				return;
			}

			/*如果用户是系统管理员，可以直接走,因为系统管理员拥有所有权限*/
			if (userInfo.isOwner()) {
				chain.doFilter(request, response);
				return;
			}
			/*如果路径不需要校验权限，可以直接走*/
			if (GLOBAL_URLS == null || GLOBAL_URLS.isEmpty()) {
				refreshUrls();
			}
			if (!GLOBAL_URLS.contains(path)) {
				chain.doFilter(request, response);
				return;
			}

			/*检查权限*/
			boolean isAccess = userInfo.getPermsUrls().contains(path);
			if (isAccess) {
				chain.doFilter(request, response);
				return;
			}

			returnJson(response, ResultVO.FAIL(Code.FAIL403));

		} catch (Exception e) {
			log.error("出现异常 path:{}\tmethod:{}\tuserId:{}\tuserName:{}\thost:{}\ttoken:{}", path, method, userId, userName, hostName, authorization, e);
			chain.doFilter(request, response);
		} finally {
			/*
			 	记录请求时间、操作人、结果、host日志
			 */
			if (!path.startsWith("/static")) {
				log.info("times:{}ms\tpath:{}\tmethod:{}\tuserId:{}\tuserName:{}\thost:{}",
						System.currentTimeMillis() - startTimes,
						path, method,
						userId, userName,
						hostName);
			}
		}
	}

	private String getParameterByRequest(HttpServletRequest request, String key) {
		String parameter = request.getHeader(key);
		if (StringUtils.isBlank(parameter)) {
			parameter = request.getParameter(key);
		}
		return parameter;
	}

	/**
	 * 从缓存取全局需要校验Token和权限的路径集合
	 */
	private void refreshUrls() {
		GLOBAL_URLS = (List) redisUtil.get(Global.GLOBAL_PERMS);
	}

	private UserToken getUserToken(String authorization) {
		return JwtUtil.getJWT(authorization);
	}

	private void returnCors(HttpServletRequest request, HttpServletResponse response) {
		//解决跨域的问题
		response.setHeader("Access-Control-Allow-Origin", "https://qstamper.com,http://localhost");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Headers", "DNT,X-Mx-ReqToken,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Content-Length, Authorization, Accept,X-Requested-With,X-App-Id, X-Token,us");
		response.setHeader("Access-Control-Allow-Methods", "PUT,POST,GET,DELETE,OPTIONS");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setStatus(HttpServletResponse.SC_OK);
	}


	private void returnJson(HttpServletResponse response, ResultVO resultVO) {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=utf-8");
		try (PrintWriter writer = response.getWriter()) {
			writer.print(JSONObject.toJSONString(resultVO));
		} catch (IOException e) {
			log.error("response error", e);
		}
	}
}
