package com.yunxi.stamper.websocket.core;


import org.apache.commons.lang3.StringUtils;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * websocket工具
 */
public class WsUtils {

    private static final String FIELD_NAME = "base#socketWrapper#socket#sc#remoteAddress";

    /**
     * 解析链接对端的IP地址
     *
     * @param session websocket链接
     * @return
     */
    public static String getRemoteAddress(Session session) {
        if (session == null) {
			return null;
		}

        InetSocketAddress addr;
        try {
            RemoteEndpoint.Async async = session.getAsyncRemote();

            //在Tomcat 8.0.x版本有效
            //addr = (InetSocketAddress) getFieldInstance(async,"base#sos#socketWrapper#socket#sc#remoteAddress");
            //在Tomcat 8.5以上版本有效
            addr = (InetSocketAddress) getFieldInstance(async);
            return addr == null ? null : addr.toString();
        } catch (Exception e) {
        }

        return null;
    }

    private static Object getFieldInstance(Object obj) {
        if (StringUtils.isBlank(WsUtils.FIELD_NAME)) {
			return null;
		}
        String[] fields = WsUtils.FIELD_NAME.split("#");
        for (String field : fields) {
            obj = getField(obj, obj.getClass(), field);
            if (obj == null) {
                return null;
            }
        }
        return obj;
    }

    private static Object getField(Object obj, Class<?> clazz, String fieldName) {
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                Field field;
                field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(obj);
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static String parseHeartText(String message) {
        if (message.contains("-")) {
            return message.split("-")[1];
        }
        return null;
    }
}
