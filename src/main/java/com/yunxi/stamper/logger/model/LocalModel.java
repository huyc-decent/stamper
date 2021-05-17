package com.yunxi.stamper.logger.model;

import com.yunxi.stamper.commons.jwt.UserToken;
import com.yunxi.stamper.entity.TsLogger;
import com.yunxi.stamper.entityVo.UserInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * @author zhf_10@163.com
 * @Description 本地线程中承载的用户相关信息，如：当前用户信息、token令牌、日志信息等
 * @date 2020/6/24 14:47
 */
@Setter
@Getter
@ToString
public class LocalModel {

	private TsLogger logger;
	private String className;
	private String oldObjJson;
	private String newObjJson;
	private boolean isComplete = false;

	private UserToken userToken;
	private UserInfo userInfo;
	private Boolean isApp;
	private String host;
}
