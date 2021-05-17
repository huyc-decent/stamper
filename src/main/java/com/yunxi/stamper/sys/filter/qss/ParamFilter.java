package com.yunxi.stamper.sys.filter.qss;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/6/3 0003 15:49
 */
@Order(3)
@Component
public class ParamFilter extends OncePerRequestFilter {
	@Override
	protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain filterChain) throws ServletException, IOException {
		//走解密流程-->封装request
		MyParametersWrapper myParametersWrapper = new MyParametersWrapper(request);
		filterChain.doFilter(myParametersWrapper, response);
	}

}
