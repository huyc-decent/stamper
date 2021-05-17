package com.yunxi.stamper.commons.other;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * URL处理工具类
 *
 */
@Slf4j
public final class URLUtil {

	/**
     * 和javascript中的encodeURIComponent方法效果相同
     *
     * @param component
     * @return
     */
    public static String encodeURIComponent(String component) {
        String result = null;

        try {
            result = URLEncoder.encode(component, "UTF-8")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%7E", "~");
        } catch (UnsupportedEncodingException e) {
            log.error("出现异常", e);
            result = component;
        }

        return result;
    }

    /**
     * 将URL中的查询参数部分解析成键值对
     *
     * @param queryString URL中的查询参数部分，不含前缀'?'
     * @return
     */
    public static Map<String, String> splitQuery(String queryString) {
        final Map<String, String> query_pairs = new ConcurrentHashMap<String, String>();
        final String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf('=');
            String key;
            try {
                key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
                final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1),
                                                                                            "UTF-8") : null;
                if (!key.isEmpty()) {
                    query_pairs.put(key, value);
                }
            } catch (UnsupportedEncodingException e) {
                log.error("出现异常", e);
            }
        }
        return query_pairs;
    }

}
