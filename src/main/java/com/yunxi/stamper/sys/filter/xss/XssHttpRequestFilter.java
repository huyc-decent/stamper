package com.yunxi.stamper.sys.filter.xss;

import com.yunxi.common.utils.ListUtils;
import org.apache.commons.lang3.CharSet;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 防止XSS攻击的过滤器
 *
 * @author zhf_10
 */
@Order(-100)
@Component
public class XssHttpRequestFilter extends OncePerRequestFilter {
	/**
	 * 排除链接
	 */
	public List<String> excludes = new ArrayList<>();

	/**
	 * xss过滤开关
	 */
	public boolean enabled = true;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		if (handleExcludeURL(request, response)) {
			filterChain.doFilter(request, response);
			return;
		}

		// 对请求进行xss过滤
		XssHttpServletRequestWrapper xssHttpServletRequestWrapper = new XssHttpServletRequestWrapper((HttpServletRequest) request);
		filterChain.doFilter(xssHttpServletRequestWrapper, response);
	}

	private boolean handleExcludeURL(HttpServletRequest request, HttpServletResponse response) {
		if (!enabled) {
			return true;
		}
		if (ListUtils.isEmpty(excludes)) {
			return false;
		}
		String url = request.getServletPath();
		for (String pattern : excludes) {
			Pattern p = Pattern.compile("^" + pattern);
			Matcher m = p.matcher(url);
			if (m.find()) {
				return true;
			}
		}
		return false;
	}
}
