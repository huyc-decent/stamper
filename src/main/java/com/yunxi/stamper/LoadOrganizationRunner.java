package com.yunxi.stamper;


import com.yunxi.stamper.commons.other.RedisGlobal;
import com.yunxi.stamper.commons.other.RedisUtil;
import com.yunxi.stamper.entity.Department;
import com.yunxi.stamper.service.DepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 项目启动时，加载组织架构
 * order:较低的值具有更高的优先级
 */
@Slf4j
@Order(103)
@Component
public class LoadOrganizationRunner implements CommandLineRunner {

	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private RedisUtil redisUtil;

	@Override
	public void run(String... args) throws Exception {
		/*
			系统启动后，将系统中所有组织列表加载进缓存
		 */
		log.info("***** 组织架构缓存 *****");
		List<Department> departments = departmentService.getAll();
		if (departments == null || departments.isEmpty()) {
			return;
		}
		for (Department department : departments) {
			String key = RedisGlobal.DEPARTMENT_BY_ID + department.getId();
			redisUtil.set(key, department, RedisGlobal.DEPARTMENT_BY_ID_TIMEOUT);
		}
	}
}
