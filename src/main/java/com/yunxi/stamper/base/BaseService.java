package com.yunxi.stamper.base;

import com.yunxi.stamper.commons.jwt.JwtUtil;
import com.yunxi.stamper.commons.jwt.UserToken;
import com.yunxi.stamper.commons.other.Global;
import com.yunxi.stamper.commons.other.RedisUtil;
import com.yunxi.stamper.entityVo.UserInfo;
import com.yunxi.stamper.logger.threadLocal.LocalHandle;
import com.yunxi.stamper.service.UserInfoService;
import com.yunxi.stamper.sys.context.SpringContextUtils;
import com.yunxi.stamper.sys.error.base.ToLoginException;
import com.yunxi.stamper.sys.lock.RedisLock;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2020/1/3 0003 10:20
 */
@Service
public class BaseService {
	@Autowired
	protected RedisUtil redisUtil;
	@Autowired
	protected RedisLock redisLock;
	@Autowired
	protected UserInfoService userInfoService;

	/**
	 * 查询缓存redis中存的用户信息
	 */
	protected UserInfo checkUserInfo() {
		UserInfo userInfo = LocalHandle.get().getUserInfo();
		if (userInfo != null) {
			return userInfo;
		}
		UserToken token = getToken();
		return checkUserInfo(token.getUserId());
	}

	/**
	 * 查询缓存redis中存的用户信息
	 */
	private UserInfo checkUserInfo(Integer userId) {
		return userInfoService.get(userId);
	}

	/**
	 * 解析请求头中的token字符串
	 */
	protected UserToken getToken() {
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


	/**
	 * 查询请求头中的token字符串
	 */
	private String getTokenStr() {
		HttpServletRequest request = SpringContextUtils.getRequest();
		if (request == null) {
			throw new RuntimeException();
		}
		String tokenJson = request.getHeader(Global.TOKEN_PREFIX);
		if (StringUtils.isBlank(tokenJson)) {
			tokenJson = request.getParameter(Global.TOKEN_PREFIX);
			if (StringUtils.isBlank(tokenJson)) {
				throw new ToLoginException("令牌不存在");
			}
		}
		return tokenJson;
	}
}
