package com.yunxi.stamper.sys.filter.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description 该实体会在项目运行时，加载application-plat.yml中的配置的忽略url路径列表
 * @date 2019/5/12 0012 0:39
 */
@Configuration
@ConfigurationProperties(prefix = "project.jwt")
public class IgnorePath {
	/***全匹配列表*/
	private List<String> ignore = new ArrayList<>();

	/***通配符列表 包含*的*/
	private List<String> wildcards = new ArrayList<>();

	public boolean isIgnore(String path) {
		if (ignore.isEmpty()) {
			return false;
		}
		if (ignore.contains(path)) {
			return true;
		}
		if (wildcards.isEmpty()) {
			return false;
		}

		for (String wildcardPath : wildcards) {
			if (((wildcardPath.endsWith("*") && path.startsWith(wildcardPath.substring(0, wildcardPath.length() - 1))))
					|| (wildcardPath.startsWith("/*") && path.endsWith(wildcardPath.substring(2)))) {
				return true;
			}
		}
		return false;
	}

	public List<String> getIgnore() {
		return ignore;
	}

	public void setIgnore(List<String> ignore) {
		if (ignore == null || ignore.isEmpty()) {
			return;
		}

		this.ignore = ignore;

		for (int i = 0; i < ignore.size(); i++) {
			String ignorePath = ignore.get(i);
			if (ignorePath.contains("*")) {
				wildcards.add(ignorePath);
				ignore.remove(i);
				i--;
			}
		}
	}
}
