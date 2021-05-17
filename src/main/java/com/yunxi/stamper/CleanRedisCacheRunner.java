package com.yunxi.stamper;


import com.yunxi.stamper.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 项目启动时，清空原数据库清空Redis缓存
 * order:较低的值具有更高的优先级
 */
@Slf4j
@Order(100)
@Component
public class CleanRedisCacheRunner implements CommandLineRunner {

	@Autowired
	private UserInfoService userInfoService;

	@Override
	public void run(String... args) throws Exception {
		/*
			系统启动后，清除缓存中存储的用户基础信息
		 */
		log.info("***** 清空缓存 *****");
		userInfoService.clearPool();
	}

}
