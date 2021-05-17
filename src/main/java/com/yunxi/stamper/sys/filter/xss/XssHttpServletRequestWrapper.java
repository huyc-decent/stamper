package com.yunxi.stamper.sys.filter.xss;


import com.alibaba.fastjson.JSON;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


/**
 * <code>{@link XssHttpServletRequestWrapper}</code>
 *
 * @author zhf
 */

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

	/**
	 * @param request
	 */
	public XssHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	/**
	 * 覆盖getParameter方法，将参数名和参数值都做xss过滤。
	 * 如果需要获得原始的值，则通过super.getParameterValues(name)来获取
	 * getParameterNames,getParameterValues和getParameterMap也可能需要覆盖
	 */
	@Override
	public String getParameter(String parameter) {
		String value = super.getParameter(parameter);
		if (value == null) {
			return null;
		}
		return XssUtils.stripXSS(value);
	}

	@Override
	public String[] getParameterValues(String parameter) {
		String[] values = super.getParameterValues(parameter);
		if (values == null) {
			return null;
		}
		int count = values.length;
		String[] encodedValues = new String[count];
		for (int i = 0; i < count; i++) {
			encodedValues[i] = XssUtils.stripXSS(values[i]);
		}
		return encodedValues;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		Map<String, String[]> values = super.getParameterMap();
		if (values == null) {
			return null;
		}
		Map<String, String[]> result = new HashMap<>();
		for (String key : values.keySet()) {
			String encodedKey = XssUtils.stripXSS(key);
			int count = values.get(key).length;
			String[] encodedValues = new String[count];
			for (int i = 0; i < count; i++) {
				encodedValues[i] = XssUtils.stripXSS(values.get(key)[i]);
			}
			result.put(encodedKey, encodedValues);
		}
		return result;
	}

	/**
	 * 覆盖getHeader方法，将参数名和参数值都做xss过滤。
	 * 如果需要获得原始的值，则通过super.getHeaders(name)来获取
	 * getHeaderNames 也可能需要覆盖
	 */
	@Override
	public String getHeader(String name) {
		String value = super.getHeader(name);
		if (value == null) {
			return null;
		}
		return XssUtils.stripXSS(value);
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		String str = getRequestBody(super.getInputStream());
		try {
			Map<String, Object> map = JSON.parseObject(str, Map.class);
			Map<String, Object> resultMap = new HashMap<>(map.size());
			for (String key : map.keySet()) {
				Object val = map.get(key);
				if (map.get(key) instanceof String) {
					resultMap.put(key, XssUtils.stripXSS(val.toString()));
				} else {
					resultMap.put(key, val);
				}
			}
			str = JSON.toJSONString(resultMap);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		final ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes());
		return new ServletInputStream() {
			@Override
			public int read() throws IOException {
				return bais.read();
			}

			@Override
			public boolean isFinished() {
				return false;
			}

			@Override
			public boolean isReady() {
				return false;
			}

			@Override
			public void setReadListener(ReadListener listener) {
			}
		};
	}

	private String getRequestBody(InputStream stream) {
		String line = "";
		StringBuilder body = new StringBuilder();
		// 读取POST提交的数据内容
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
		try {
			while ((line = reader.readLine()) != null) {
				body.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return body.toString();
	}
}
