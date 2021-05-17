package com.yunxi.stamper;


import com.yunxi.stamper.commons.other.Global;
import com.yunxi.stamper.commons.other.RedisUtil;
import com.yunxi.stamper.entity.Perms;
import com.yunxi.stamper.service.PermService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 项目启动时，加载权限列表
 * order:较低的值具有更高的优先级
 */
@Slf4j
@Order(102)
@Component
public class InitializePermissionsRunner implements CommandLineRunner {

	@Autowired
	private PermService permService;
	@Autowired
	private RedisUtil redisUtil;

	@Override
	public void run(String... args) throws Exception {
		/*
			系统启动后，将库中权限列表(所有需要校验权限的URL)加载进缓存中
		 */
		log.info("***** 资源权限加载 *****");

		List<Perms> all = permService.getAll();//需要校验的路径URL

		List<String> paths = new LinkedList<>();
		for (Perms perms : all) {
			String urls = perms.getUrl();
			if (StringUtils.isBlank(urls)) {
				continue;
			}

			if (urls.contains(",")) {
				String[] urlArr = urls.split(",");
				Collections.addAll(paths, urlArr);
			} else {
				paths.add(urls);
			}
		}

		redisUtil.set(Global.GLOBAL_PERMS, paths);
		log.info("初始化-->权限加载....已完成");
	}
}
