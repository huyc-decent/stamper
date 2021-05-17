package com.yunxi.stamper.base;


import com.github.pagehelper.PageHelper;
import com.yunxi.quantum.core.QuantumService;
import com.yunxi.stamper.commons.jwt.JwtUtil;
import com.yunxi.stamper.commons.jwt.UserToken;
import com.yunxi.stamper.commons.other.Global;
import com.yunxi.stamper.commons.other.RedisGlobal;
import com.yunxi.stamper.commons.other.RedisUtil;
import com.yunxi.stamper.entityVo.UserInfo;
import com.yunxi.stamper.logger.threadLocal.LocalHandle;
import com.yunxi.stamper.service.OrgService;
import com.yunxi.stamper.service.UserInfoService;
import com.yunxi.stamper.sys.config.ProjectProperties;
import com.yunxi.stamper.sys.context.SpringContextUtils;
import com.yunxi.stamper.sys.error.base.ToLoginException;
import com.yunxi.stamper.sys.lock.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/4/26 0026 17:36
 */
@Slf4j
public class BaseController {

	@Autowired
	protected RedisUtil redisUtil;
	@Autowired
	protected OrgService orgService;
	@Autowired
	protected ProjectProperties properties;
	@Autowired
	protected QuantumService quantumService;
	@Autowired
	protected RedisLock redisLock;
	@Autowired
	protected UserInfoService userInfoService;

	protected boolean setPage(boolean page, Integer pageSize, Integer pageNum) {
		if (page) {
			pageNum = pageNum == null || pageNum <= 0 ? 1 : pageNum;
			pageSize = pageSize == null || pageSize <= 0 ? 10 : pageSize;
			PageHelper.startPage(pageNum, pageSize);
			return true;
		}
		return false;
	}

	/**
	 * 从请求头中获取分页参数
	 */
	protected boolean setPage() {
		HttpServletRequest request = SpringContextUtils.getRequest();
		if (request == null) {
			throw new RuntimeException();
		}
		String size = request.getParameter("pageSize");
		String num = request.getParameter("pageNum");
		String isPage = request.getParameter("page");
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
		boolean toPage = false;
		if (StringUtils.isNotBlank(isPage)) {
			try {
				toPage = Boolean.parseBoolean(isPage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (toPage) {
			PageHelper.startPage(pageNum, pageSize);
		}
		return toPage;
	}

	/**
	 * 查询请求客户端的版本号
	 *
	 * @return 客户端版本号
	 */
	public static float getVersion() {
		HttpServletRequest request = SpringContextUtils.getRequest();
		if (request == null) {
			throw new RuntimeException();
		}
		String header = request.getHeader(Global.HEAD_VERSION);
		String version = StringUtils.isBlank(header) ? request.getParameter(Global.HEAD_VERSION) : header;
		if (StringUtils.isBlank(version)) {
			return 0;
		}

		float ver = 0;
		try {
			ver = Float.parseFloat(version);
		} catch (NumberFormatException e) {
			log.error("请求头版本信息解析出错", e);
		}
		return ver;
	}

	/**
	 * 判断是否是App发起的请求
	 * ps:需要前端在web端和app端加上一个标识符,如:us
	 *
	 * @return false:web端请求  true:app端请求
	 */
	public static boolean isApp() {
		Boolean isApp = LocalHandle.get().getIsApp();
		if (isApp != null) {
			return isApp;
		}

		HttpServletRequest request = SpringContextUtils.getRequest();
		if (request == null) {
			throw new RuntimeException();
		}
		String us = request.getHeader("us");
		return !(StringUtils.isNotBlank(us) && "web".equalsIgnoreCase(us));
	}

	/**
	 * 查询缓存redis中存的用户信息
	 */
	protected UserInfo getUserInfo() {
		UserInfo userInfo = LocalHandle.get().getUserInfo();
		if (userInfo != null) {
			return userInfo;
		}
		UserToken token = getToken();
		return getUserInfo(token.getUserId());
	}

	/**
	 * 查询缓存redis中存的用户信息
	 */
	private UserInfo getUserInfo(Integer userId) {
		return userInfoService.get(userId);
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
	 * 查询指定印章状态
	 * return  null:不在线  0:在线、关锁 1:在线、开锁
	 */
	protected Integer isOnline(Integer deviceId) {
		if (deviceId != null) {
			String key = RedisGlobal.PING + deviceId;
			String status = redisUtil.getStr(key);
			if (StringUtils.isNotBlank(status)) {
				try {
					return Boolean.parseBoolean(status) ? 1 : 0;
				} catch (NumberFormatException e) {
					log.error("出现异常 ", e);
				}
			}
		}
		return null;
	}
}
