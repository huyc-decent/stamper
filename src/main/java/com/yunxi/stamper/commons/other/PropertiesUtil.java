package com.yunxi.stamper.commons.other;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Properties;

/**
 * 配置文件工具类
 */
@Slf4j
public class PropertiesUtil {
	private Properties props;

	//指定加载
	public PropertiesUtil(String fileName) {
		readProperties(fileName);
	}

	//指定加载
	public PropertiesUtil(String path, String fileName) {
		readProperties(path, fileName);
	}

	/**
	 * 加载配置文件
	 */
	private void readProperties(String path, String fileName) {
		if (props == null) {
			props = new Properties();
		}
		if (StringUtils.isBlank(path)) {
			path = "/";
		}
		File file = new File(path, fileName);
		try (FileInputStream inputStream = new FileInputStream(file)) {
			props.load(inputStream);
		} catch (Exception e) {
			log.error("加载配置文件异常 path:{} fileName:{}", path, fileName, e);
		}
	}

	/**
	 * 加载配置文件
	 */
	private void readProperties(String fileName) {
		if (props == null) {
			props = new Properties();
		}
		try (InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName)) {
			props.load(inputStream);
		} catch (Exception e) {
			log.error("加载配置文件异常 fileName:{}", fileName, e);
		}
	}

	/**
	 * 得到所有的配置信息
	 *
	 * @return
	 */
	public LinkedHashMap<String, String> getAll() {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		Enumeration<?> enu = props.propertyNames();
		while (enu.hasMoreElements()) {
			String key = (String) enu.nextElement();
			String value = props.getProperty(key);
			key = new String(key.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
			map.put(key, value);
		}
		return map;
	}

	/**
	 * 根据key读取对应的value
	 *
	 * @param key
	 * @return
	 */
	public String getStr(String key) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		String property = props.getProperty(key);
		if (StringUtils.isBlank(property)) {
			return null;
		}
		String msg = null;
		try {
			msg = new String(property.getBytes("ISO8859-1"), StandardCharsets.UTF_8);
		} catch (UnsupportedEncodingException e) {
			log.error("读取属性值异常 key:{}", key, e);
		}
		return msg;
	}

	/**
	 * 根据key读取对应的value
	 *
	 * @param key
	 * @return
	 */
	public Integer getInt(String key) {
		String property = props.getProperty(key);
		return Integer.parseInt(property);
	}
}
