package com.yunxi.stamper.logger.threadLocal;

import com.alibaba.fastjson.JSONObject;
import com.yunxi.stamper.commons.jwt.UserToken;
import com.yunxi.stamper.entity.TsLogger;
import com.yunxi.stamper.entityVo.UserInfo;
import com.yunxi.stamper.logger.anno.LogTag;
import com.yunxi.stamper.logger.model.LocalModel;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

public class LocalHandle {

	private final static ThreadLocal<LocalModel> holder = new ThreadLocal<>();

	public static void remove() {
		holder.remove();
	}

	public static void setToken(UserToken userToken) {
		get().setUserToken(userToken);
	}

	/**
	 * 在ThreadLocal中存储当前用户信息
	 *
	 * @param userInfo 当前请求用户的相关信息
	 */
	public static void setOperator(UserInfo userInfo) {
		if (userInfo == null) {
			return;
		}

		//设置用户信息
		LocalModel localModel = get();
		localModel.setUserInfo(userInfo);

		//初始化用户日志对象
		TsLogger logger = localModel.getLogger();
		if (logger == null) {
			logger = new TsLogger();
			localModel.setLogger(logger);
		}
		logger.setOperatorId(userInfo.getId());
		logger.setOperatorName(userInfo.getUserName());
	}

	/**
	 * 业务日志-新对象
	 *
	 * @param obj 操作的对象
	 */
	public static void setNewObj(Object obj) {
		if (obj == null) {
			return;
		}

		//如果日志信息不存在，则创建
		LocalModel localModel = get();
		TsLogger logger = localModel.getLogger();
		if (logger == null) {
			logger = new TsLogger();
			localModel.setLogger(logger);
		}

		//如果日志名称不存在，则创建
		String objectName = logger.getObjectName();
		if (StringUtils.isBlank(objectName)) {
			objectName = getObjName(obj);
			logger.setObjectName(objectName);
		}

		//如果对象名称不存在，则创建
		String className = localModel.getClassName();
		if (StringUtils.isBlank(className)) {
			className = obj.getClass().getName();
			localModel.setClassName(className);
		}

		logger.setObjectType(obj.getClass().getTypeName());
		localModel.setNewObjJson(JSONObject.toJSONString(obj));
	}

	/**
	 * 业务日志-旧对象
	 *
	 * @param obj 操作的对象
	 */
	public static void setOldObj(Object obj) {
		if (obj == null) {
			return;
		}

		//如果日志信息不存在，则创建
		LocalModel localModel = get();
		TsLogger logger = localModel.getLogger();
		if (logger == null) {
			logger = new TsLogger();
			localModel.setLogger(logger);
		}


		logger.setObjectId(getId(obj));

		String objectName = logger.getObjectName();
		if (StringUtils.isBlank(objectName)) {
			objectName = getObjName(obj);
			logger.setObjectName(objectName);
		}

		String className = localModel.getClassName();
		if (StringUtils.isBlank(className)) {
			className = obj.getClass().getName();
			localModel.setClassName(className);
		}


		logger.setObjectType(obj.getClass().getTypeName());
		localModel.setOldObjJson(JSONObject.toJSONString(obj));
	}

	/**
	 * 对象名称
	 *
	 * @param obj 操作的对象
	 * @return 对象名称
	 */
	private static String getObjName(Object obj) {
		if (obj == null) {
			return null;
		}
		Class<?> objClass = obj.getClass();
		LogTag logTag = objClass.getAnnotation(LogTag.class);
		String objName = obj.getClass().getTypeName();
		if (logTag != null && StringUtils.isNotBlank(logTag.value())) {
			objName = logTag.value();
		}
		return objName;
	}

	/**
	 * 对象id
	 *
	 * @param object 操作的对象
	 * @return 解析对象的ID属性
	 */
	private static Integer getId(Object object) {
		if (object == null) {
			return null;
		}
		Class<?> clazz = object.getClass();
		Field idField = null;
		try {
			idField = clazz.getDeclaredField("id");
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		if (idField == null) {
			return null;
		}
		idField.setAccessible(true);
		Integer id = null;
		try {
			Object o = idField.get(object);
			if (o != null) {
				id = Integer.parseInt(o.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return id;
	}

	/**
	 * 本地线程-业务相关对象
	 *
	 * @return 返回当前线程对象
	 */
	public static LocalModel get() {
		LocalModel localModel = holder.get();
		if (localModel == null) {
			localModel = new LocalModel();
			holder.set(localModel);
		}
		return localModel;
	}

	/**
	 * 批量操作ids
	 *
	 * @param ids 批量操作对象ids
	 */
	public static void setbatchId(String ids) {
		LocalModel localModel = get();
		TsLogger logger = localModel.getLogger();
		if (logger == null) {
			logger = new TsLogger();
		}

		logger.setBatchId(ids);
		localModel.setLogger(logger);

	}

	/**
	 * 业务完成
	 */
	public static void complete() {
		get().setComplete(true);
	}

	/**
	 * 业务完成
	 *
	 * @param content 业务描述
	 */
	public static void complete(String content) {
		if (StringUtils.isBlank(content)) {
			complete();
		}

		LocalModel localModel = get();
		TsLogger logger = localModel.getLogger();
		if (logger == null) {
			logger = new TsLogger();
		}

		logger.setContent(content);
		localModel.setLogger(logger);

		localModel.setComplete(true);
	}

	/**
	 * 清空本地线程
	 */
	public static void clear() {
		holder.remove();
	}

}

