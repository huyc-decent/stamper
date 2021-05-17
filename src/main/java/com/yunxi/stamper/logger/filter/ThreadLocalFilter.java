package com.yunxi.stamper.logger.filter;

import com.yunxi.stamper.logger.model.LocalModel;
import com.yunxi.stamper.logger.service.LoggerService;
import com.yunxi.stamper.logger.threadLocal.LocalHandle;
import com.yunxi.stamper.sys.filter.auth.IgnorePath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;

@Slf4j
@Order(2)
@Component
public class ThreadLocalFilter implements Filter {

	@Autowired
	private LoggerService loggerService;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			chain.doFilter(request, response);
		} finally {
			/*清空本地线程*/
			LocalModel localModel = LocalHandle.get();

			/*处理当前用户日志信息*/
			if (localModel != null && localModel.isComplete()) {
				loggerService.addLogger(localModel);
			}

			/*清空本地线程对象*/
			LocalHandle.clear();
		}
	}

	@Override
	public void destroy() {

	}
}