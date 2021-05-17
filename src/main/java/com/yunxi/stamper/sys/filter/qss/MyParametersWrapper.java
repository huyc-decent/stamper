package com.yunxi.stamper.sys.filter.qss;

import com.yunxi.stamper.commons.other.Global;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自定义Http
 */
@Slf4j
public class MyParametersWrapper extends HttpServletRequestWrapper {
	private Map<String, String[]> parameterMap = new ConcurrentHashMap<>();

	public MyParametersWrapper(HttpServletRequest request) {
		super(request);
		toDecipy(request);
	}

	/**
	 * 2.对请求体进行解密处理
	 */
	private void toDecipy(HttpServletRequest request) {
		//将请求参数转换至自定义的容器中
		Map<String, String[]> params = request.getParameterMap();
		parameterMap.putAll(params);
		if (parameterMap != null && !parameterMap.isEmpty()) {

			String ticket = getParam(parameterMap, Global.ticket);
			String ciphertext = getParam(parameterMap, Global.ciphertext);

			if (StringUtils.isNoneBlank(ticket, ciphertext)) {

				//解密处理
				Map<String, String> newParams = QuantumnUtils.quantumnDecrpy(ticket, ciphertext);

				//处理解密参数
				try {
					if (!newParams.isEmpty()) {
						wrap(newParams);
					}
				} catch (Exception e) {
					log.error("出现异常 ", e);
				}
			}
		}
	}

	/**
	 * 将新参数转换成原Request请求需要的格式 Map<String, String[]>
	 */
	private void wrap(Map<String, String> params) {
		if (params != null && !params.isEmpty()) {
			//转换成controller层需要的参数
			for (String key : params.keySet()) {
				Object value = params.get(key);
				parameterMap.put(key, new String[]{String.valueOf(value)});
			}
			log.info("********解密成功********");
		}
	}

	/**
	 * 获取参数工具方法
	 */
	private String getParam(Map<String, String[]> params, String key) {
		if (params != null && !params.isEmpty() && StringUtils.isNotBlank(key)) {
			String[] values = params.get(key);
			if (values != null && values.length > 0) {
				return values[0];
			}
		}
		return null;
	}

	/**
	 * 获取所有参数名
	 *
	 * @return 返回所有参数名
	 */
	@Override
	public Enumeration<String> getParameterNames() {
//		Vector<String> vector = new Vector<>(parameterMap.keySet());
//		return vector.elements();
		return super.getParameterNames();
	}

	/**
	 * 获取指定参数名的值，如果有多个参数时默认取第一个
	 *
	 * @param name 指定参数名
	 * @return 指定参数名的值
	 */
	@Override
	public String getParameter(String name) {
		if (StringUtils.isNotBlank(name)) {
			String[] values = parameterMap.get(name);
			if (null == values) {
				return null;
			}
			return values.length > 0 ? values[0] : super.getParameter(name);
		} else {
			return null;
		}
	}

	/**
	 * 获取指定参数名的所有值的数组
	 */
	@Override
	public String[] getParameterValues(String name) {
		if (StringUtils.isNotBlank(name)) {
			String[] values = parameterMap.get(name);
			return values != null ? values : super.getParameterValues(name);
		} else {
			return null;
		}
	}
}
