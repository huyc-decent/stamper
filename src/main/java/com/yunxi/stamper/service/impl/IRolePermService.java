package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.service.UserInfoService;
import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.Role;
import com.yunxi.stamper.entity.RolePerms;
import com.yunxi.stamper.mapper.RolePermsMapper;
import com.yunxi.stamper.service.RolePermService;
import com.yunxi.stamper.service.RoleService;
import com.yunxi.stamper.service.UserRoleService;
import com.yunxi.stamper.base.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/7 0007 10:48
 */
@Slf4j
@Service
public class IRolePermService extends BaseService implements RolePermService {

	@Autowired
	private RolePermsMapper mapper;
	@Autowired
	private RoleService roleService;
	@Autowired
	private UserRoleService userRoleService;
	@Autowired
	@Lazy
	private UserInfoService userInfoService;

	@Override
	@Transactional
	public void add(RolePerms rp) {
		int addCount = 0;
		if (rp != null && rp.getRoleId() != null && rp.getPermsId() != null) {
			addCount = mapper.insert(rp);
		}
		if (addCount != 1) {
			throw new PrintException("角色权限添加失败");
		}
	}

	@Override
	@Transactional
	public void del(RolePerms rp) {
		int delCount = 0;
		if (rp != null && rp.getRoleId() != null && rp.getPermsId() != null) {
			delCount = mapper.delete(rp);
		}
		if (delCount != 1) {
			throw new PrintException("角色权限解绑失败");
		}
	}

	/**
	 * 查询该角色绑定的权限ID列表
	 *
	 * @param roleId
	 * @return
	 */
	@Override
	public List<Integer> getByRoleId(Integer roleId) {
		if (roleId != null) {
			return mapper.selectByRoleId(roleId);
		}
		return null;
	}

	@Override
	public RolePerms get(Integer roleId, Integer permsId) {
		if (roleId != null && permsId != null) {
			Example example = new Example(RolePerms.class);
			example.createCriteria().andEqualTo("roleId", roleId)
					.andEqualTo("permsId", permsId);
			return mapper.selectOneByExample(example);
		}
		return null;
	}

	/**
	 * 为指定角色添加权限列表
	 *
	 * @param role     角色
	 * @param permsIds 权限列表
	 * @param updateAt 更新人
	 */
	@Override
	@Transactional
	public void bindPerms(Role role, Integer[] permsIds, Integer updateAt) {
		//查询该角色对应的员工ID列表
		List<Integer> userIds = userRoleService.getByRole(role.getId());

		/**
		 * 删除角色绑定的权限
		 */
		mapper.deleteByRole(role.getId());

		/**
		 * 绑定新权限列表
		 */
		if (permsIds != null && permsIds.length > 0) {
			for (int i = 0; i < permsIds.length; i++) {
				Integer permsId = permsIds[i];

				RolePerms rolePerms = new RolePerms();
				rolePerms.setRoleId(role.getId());
				rolePerms.setPermsId(permsId);

				add(rolePerms);
			}
		}

		/**
		 * 更新角色信息(修改人)
		 */
		role.setUpdateId(updateAt);
		roleService.update(role);

		/**
		 * 更新该角色对应的员工缓存信息
		 */
//		if (userIds != null && userIds.size() > 0) {
//			String[] keys = new String[userIds.size()];
//			for (int i = 0; i < userIds.size(); i++) {
//				Integer userId = userIds.get(i);
//				keys[i] = RedisGlobal.USER_INFO + userId;
//			}
//			redisUtil.del(keys);
//		}
		if (userIds == null || userIds.isEmpty()) {
			return;
		}
		userIds.forEach(userId -> {
			userInfoService.del(userId);
		});
	}
}
