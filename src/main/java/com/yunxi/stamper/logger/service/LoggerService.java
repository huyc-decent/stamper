package com.yunxi.stamper.logger.service;

import com.alibaba.fastjson.JSONObject;
import com.yunxi.stamper.commons.jwt.UserToken;
import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.entity.TsLogger;
import com.yunxi.stamper.entity.TsLoggerAttribute;
import com.yunxi.stamper.logger.anno.LogTag;
import com.yunxi.stamper.logger.model.LocalModel;
import com.yunxi.stamper.service.TsLoggerAttributeService;
import com.yunxi.stamper.service.TsLoggerService;
import com.yunxi.stamper.sys.rabbitMq.MqSender;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description 日志核心业务层
 * @date 2020/6/24 14:47
 */
@Slf4j
@Component
public class LoggerService {

	@Autowired
	private MqSender mqSender;
	@Autowired
	private TsLoggerService loggerService;
	@Autowired
	private TsLoggerAttributeService loggerAttributeService;

	/**
	 * 业务日志系统
	 *
	 * @param localModel 当前线程对象
	 */
	public void addLogger(LocalModel localModel) {
		try {
			mqSender.sendToExchange(CommonUtils.properties.getLogger().getExchange(), localModel);
		} catch (Exception e) {
			log.error("业务日志-rabbitmqt-发送异常 localModel:{}", CommonUtils.objToJson(localModel), e);
		}
	}

	@Transactional
	public void excute(String modelJson) throws IllegalAccessException, ClassNotFoundException {
		if (StringUtils.isBlank(modelJson)) {
			return;
		}
		LocalModel localModel = JSONObject.parseObject(modelJson, LocalModel.class);
		if (localModel == null || StringUtils.isBlank(localModel.getClassName()) || localModel.getLogger() == null) {
			return;
		}

		TsLogger logger = localModel.getLogger();
		UserToken userToken = localModel.getUserToken();
		if (userToken != null) {
			logger.setOperatorId(userToken.getUserId());
			logger.setOperatorName(userToken.getUserName());
		}
		loggerService.add(logger);

		if (StringUtils.isAllBlank(localModel.getOldObjJson(), localModel.getNewObjJson())) {
			return;
		}

		/*字段反射类型列表*/
		String className = localModel.getClassName();
		Class<?> aClass = Class.forName(className);
		List<Field> fields = getFields(aClass);

		/*比较新旧对象属性变化*/
		Object oldO = null;
		if (StringUtils.isNotBlank(localModel.getOldObjJson())) {
			oldO = JSONObject.parseObject(localModel.getOldObjJson(), aClass);
		}
		Object newO = null;
		if (StringUtils.isNotBlank(localModel.getNewObjJson())) {
			newO = JSONObject.parseObject(localModel.getNewObjJson(), aClass);
		}

		List<TsLoggerAttribute> attributes = new ArrayList<>();

		for (Field field : fields) {
			field.setAccessible(true);

			Object oldValue = getFieldValue(field, oldO);
			Object newValue = getFieldValue(field, newO);
			LogTag logtag = field.getAnnotation(LogTag.class);

			if (ObjectUtils.notEqual(oldValue, newValue)) {
				TsLoggerAttribute attribute = new TsLoggerAttribute();
				attribute.setNewValue(newValue == null ? "" : newValue.toString());
				attribute.setOldValue(oldValue == null ? "" : oldValue.toString());

				/*属性字段*/
				attribute.setAttributeField(field.getName());

				/*属性类型*/
				String typeName = field.getType().getTypeName();
				attribute.setAttributeType(typeName.substring(typeName.lastIndexOf('.') + 1));

				/*属性名称*/
				String value = logtag.value();
				if (StringUtils.isNotBlank(value)) {
					attribute.setAttributeName(value);
				}

				attributes.add(attribute);
			}
		}

		if (attributes.isEmpty()) {
			return;
		}
		for (TsLoggerAttribute attribute : attributes) {
			attribute.setLoggerId(logger.getId());
			loggerAttributeService.add(attribute);
		}
	}

	/**
	 * 属性字段列表
	 *
	 * @param clazz 类型
	 * @return 属性列表
	 */
	private List<Field> getFields(Class<?> clazz) {
		if (clazz == null) {
			return null;
		}

		Field[] declaredFields = clazz.getDeclaredFields();
		if (declaredFields.length == 0) {
			return null;
		}

		List<Field> fields = new ArrayList<>();
		for (Field declaredField : declaredFields) {
			/*未加注解的不需要处理*/
			LogTag logtag = declaredField.getAnnotation(LogTag.class);
			if (logtag == null) {
				continue;
			}

			fields.add(declaredField);
		}

		return fields;
	}

	private Object getFieldValue(Field field, Object obj) throws IllegalAccessException {
		if (field == null || obj == null) {
			return null;
		}
		return field.get(obj);
	}
}
