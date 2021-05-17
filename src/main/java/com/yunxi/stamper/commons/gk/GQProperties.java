package com.yunxi.stamper.commons.gk;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhf_10@163.com
 * @Description 量子相关配置参数(国科)
 * @date 2020/6/3 9:35
 */
@Setter
@Getter
@ToString
@Component
@ConfigurationProperties(prefix = "project.gkqss")
public class GQProperties {

	/***国科接口量子服务IP*/
	private String qssHost = "http://60.173.247.191:9095";

	/***国科接口量子应用ID*/
	private String appId = "000";

	/***加密字符串最大长度*/
	private int encMaxLen = 1024;

	/***解密字符串最大长度*/
	private int decMaxLen = 1024;


}
