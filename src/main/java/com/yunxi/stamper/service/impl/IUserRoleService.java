package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.commons.other.RedisGlobal;
import com.yunxi.stamper.service.UserInfoService;
import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.UserRole;
import com.yunxi.stamper.mapper.UserRoleMapper;
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
 * @date 2019/5/7 0007 10:52
 */
@Slf4j
@SuppressWarnings("SpringJavaAutowiringInspection")
@Service
public class IUserRoleService extends BaseService implements UserRoleService {

	@Autowired
	private UserRoleMapper mapper;
	@Autowired
	@Lazy
	private UserInfoService userInfoService;
	@Override
	public List<UserRole> getByUser(Integer userId) {
		if (userId != null) {
			Example example = new Example(UserRole.class);
			example.createCriteria().andEqualTo("userId", userId);
			return mapper.selectByExample(example);
		}
		return null;
	}

	/**
	 * 查询角色id对应的用户id列表
	 *
	 * @param roleId
	 * @return
	 */
	@Override
	public List<Integer> getByRole(Integer roleId) {
		if (roleId != null) {
			return mapper.selectByRole(roleId);
		}
		return null;
	}

	@Override
	public UserRole getByUserAndRole(Integer userId, Integer roleId) {
		if (userId != null && roleId != null) {
			UserRole ur = new UserRole();
			ur.setRoleId(roleId);
			ur.setUserId(userId);
			return mapper.selectByPrimaryKey(ur);
		}
		return null;
	}

	@Override
	@Transactional
	public void add(UserRole userRole) {
		int addCount = 0;
		if (userRole != null) {
			addCount = mapper.insert(userRole);
		}
		if (addCount != 1) {
			throw new PrintException("用户-角色添加失败");
		}
	}

	@Override
	@Transactional
	public void del(UserRole userRole) {
		int delCount = 0;
		if (userRole != null && userRole.getUserId() != null && userRole.getRoleId() != null) {
			delCount = mapper.delete(userRole);
		}
		if (delCount != 1) {
			throw new PrintException("用户角色解绑失败");
		}
	}

	/**
	 * 给指定用户绑定角色列表
	 */
	@Override
	@Transactional
	public void bindRoles(Integer userId, Integer[] roleIds) {
		if (userId != null && roleIds != null && roleIds.length > 0) {
			for (int i = 0; i < roleIds.length; i++) {
				Integer roleId = roleIds[i];
				UserRole ur = new UserRole();
				ur.setUserId(userId);
				ur.setRoleId(roleId);
				add(ur);
			}

			/**
			 * 删除员工缓存信息
			 */
//			redisUtil.del(RedisGlobal.USER_INFO + userId);
			userInfoService.del(userId);
		}
	}

	/**
	 * 删除角色-员工关联信息
	 *
	 * @param userId
	 */
	@Override
	@Transactional
	public void delByUserId(Integer userId) {
		if (userId != null) {
			mapper.deleteByUserId(userId);
		}
	}

	/**
	 * 查询拥有指定角色ID的员工ID列表
	 *
	 * @param roleId 角色ID
	 * @return
	 */
	@Override
	public List<Integer> getUserIdsByRoleId(Integer roleId) {
		if (roleId != null) {
			return mapper.selectUserIdsByRoleId(roleId);
		}
		return null;
	}

	/**
	 * 删除指定角色关联的员工信息
	 *
	 * @param roleId
	 */
	@Override
	@Transactional
	public void delByRole(Integer roleId) {
		if (roleId != null) {
			mapper.deleteByRoleId(roleId);
		}
	}
}
