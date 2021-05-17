package com.yunxi.stamper;


import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.other.Global;
import com.yunxi.stamper.commons.other.RedisUtil;
import com.yunxi.stamper.entity.Perms;
import com.yunxi.stamper.entity.Role;
import com.yunxi.stamper.entity.RolePerms;
import com.yunxi.stamper.entity.RoleTemp;
import com.yunxi.stamper.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目启动时，刷新默认角色权限模板
 * order:较低的值具有更高的优先级
 */
@Slf4j
@Order(101)
@Component
public class FlushPermissionsRunner implements CommandLineRunner {

	@Autowired
	private PermService permService;
	@Autowired
	private RoleTempService roleTempService;
	@Autowired
	private RolePermService rolePermService;
	@Autowired
	private RoleService roleService;

	@Override
	@Transactional
	public void run(String... args) throws Exception {
		/*
			系统启动后，将 角色-权限 模板取出，比对系统角色是否完整，如果有新增权限，则增加
		 */
		log.info("***** 角色模板检查 *****");
		List<RoleTemp> roleTempList = roleTempService.getAll();
		if (roleTempList == null || roleTempList.isEmpty()) {
			log.info("x\t角色模板-不存在");
			return;
		}

		List<Perms> permsList = permService.getAll();
		List<Integer> list = permsList.stream().map(Perms::getId).collect(Collectors.toList());

		for (RoleTemp roleTemp : roleTempList) {
			String roleTempPermIds = roleTemp.getPermIds();
			String code = roleTemp.getCode();
			String name = roleTemp.getName();

			if (StringUtils.isBlank(roleTempPermIds)) {
				log.info("x\t角色模板-未配置\tname:{}\tcode:{}", name, code);
				continue;
			}

			List<Integer> permIdList = CommonUtils.splitToInteger(roleTempPermIds, ",");
			if (permIdList == null || permIdList.isEmpty()) {
				log.info("x\t角色模板-未配置\tname:{}\tcode:{}\tpermIds:{}", name, code, roleTempPermIds);
				continue;
			}

			List<Role> roleList = roleService.getByCode(code);
			if (roleList == null || roleList.isEmpty()) {
				log.info("x\t角色模板-无角色列表\tname:{}\tcode:{}", name, code);
				continue;
			}

			for (Role role : roleList) {
				//检查是否需要新增 角色-权限 关联关系记录
				List<Integer> rolePermIdList = rolePermService.getByRoleId(role.getId());//该角色已存在的权限Id列表
				for (Integer permId : permIdList) {//遍历模板列表
					if (!list.contains(permId)) {//不存在的权限忽略掉
						continue;
					}
					if (rolePermIdList.contains(permId)) {//已拥有的权限忽略掉
						continue;
					}
					//新增 角色-权限 关联记录
					RolePerms rolePerms = new RolePerms();
					rolePerms.setPermsId(permId);
					rolePerms.setRoleId(role.getId());
					rolePermService.add(rolePerms);
					rolePermIdList.add(permId);
				}
			}
		}
	}
}
