package com.yunxi.stamper.sys.filter.xss;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.owasp.esapi.ESAPI;
import org.springframework.web.util.HtmlUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * XSS工具类
 *
 * @author zhf_10
 */
public class XssUtils {

	/**
	 * xss匹配
	 */
	private static final List<Pattern> PATTERNS = new ArrayList<>();

	static {
		PATTERNS.add(Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE));
		PATTERNS.add(Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
		PATTERNS.add(Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
		PATTERNS.add(Pattern.compile("</script>", Pattern.CASE_INSENSITIVE));
		PATTERNS.add(Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
		PATTERNS.add(Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
		PATTERNS.add(Pattern.compile("expression\\\\((.*?)\\\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
		PATTERNS.add(Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE));
		PATTERNS.add(Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE));
		PATTERNS.add(Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
		PATTERNS.add(Pattern.compile(".*<.*", Pattern.CASE_INSENSITIVE));
	}

	public static String stripXSS(String value) {
		if (value != null) {
			value = value.trim();
			//注意：若前端使用get方式提交经过encodeURI的中文，此处会乱码
			value = ESAPI.encoder().canonicalize(value);
			ESAPI.encoder().encodeForHTML(value);

			// XSS脚本过滤
			for (Pattern pattern : PATTERNS) {
				value = pattern.matcher(value).replaceAll("");
			}

			// SQL注入过滤
			value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
			value = value.replaceAll("%3C", "&lt;").replaceAll("%3E", "&gt;");
			value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
			value = value.replaceAll("%28", "&#40;").replaceAll("%29", "&#41;");
			value = value.replaceAll("'", "&#39;");
			value = value.replaceAll("eval\\((.*)\\)", "");
			value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
			value = value.replaceAll("script", "");

			value = Jsoup.clean(value, Whitelist.relaxed()).trim();
		}
		return value;
	}

	public static Object xssObj(Object obj) {
		try {
			// 获取返回对象类所有字段
			List<Field> fields = new ArrayList<>(Arrays.asList(obj.getClass().getDeclaredFields()));
			// 获取返回对象的父类的所有字段，（如果有的话，根据自己情况删减）
			List<Field> parentFields = new ArrayList<>(Arrays.asList(obj.getClass().getSuperclass().getDeclaredFields()));
			// 合并父类字段和本类字段（如果有的话，根据自己情况删减）
			fields.addAll(parentFields);

			// 维护一个以转义的名单列表，避免同一字段重复被转义，因为父类和子类可能存在相同的字段名（没有父类的情况可删减）
			List<String> nameList = new ArrayList<>();

			for (Field field : fields) { //遍历所有属性
				String type = field.getGenericType().getTypeName();
				String name = field.getName(); //获取属性的名字
				if (type.equals(String.class.getTypeName()) && !nameList.contains(name)) {
					// 如果使用到了这个字段，则添加到名单中，以免下次重复被使用
					nameList.add(name);

					// 根据javaBean规范，set或get方法字段的首字母大写
					name = name.substring(0, 1).toUpperCase() + name.substring(1);

					Method getMethod = null;
					Method setMethod = null;
					try {
						// 获取本类的get、set方法
						getMethod = obj.getClass().getMethod("get" + name);
						setMethod = obj.getClass().getMethod("set" + name, new Class[]{String.class});
					} catch (NoSuchMethodException e) {
						try {
							// 如果没有获取到则去父类获取（如果有的话，根据自己情况删减）
							getMethod = obj.getClass().getSuperclass().getMethod("get" + name);
							setMethod = obj.getClass().getSuperclass().getMethod("set" + name, new Class[]{String.class});
						} catch (NoSuchMethodException e1) {
							e1.printStackTrace();
						}
					}

					if (getMethod != null && setMethod != null) {
						String value = null;
						try {
							// 通过get方法获取到值
							value = (String) getMethod.invoke(obj);
							if (StringUtils.isBlank(value)) {
								continue;
							}
							// 对字段进行html编码，然后通过set方法再塞回去
							String s = HtmlUtils.htmlEscape(value);
							s = ESAPI.encoder().encodeForHTML(s);
							setMethod.invoke(obj, s);
						} catch (IllegalAccessException | InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}


}
