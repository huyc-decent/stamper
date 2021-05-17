package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.service.UserInfoService;
import com.yunxi.stamper.sys.context.SpringContextUtils;
import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.Role;
import com.yunxi.stamper.entityVo.RoleEntity;
import com.yunxi.stamper.entityVo.RoleVo;
import com.yunxi.stamper.mapper.RoleMapper;
import com.yunxi.stamper.service.RolePermsService;
import com.yunxi.stamper.service.RoleService;
import com.yunxi.stamper.service.UserRoleService;
import com.yunxi.stamper.base.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/7 0007 10:21
 */
@Service
public class IRoleService extends BaseService implements RoleService {
	@Autowired
	private RoleMapper mapper;
	@Autowired
	private RolePermsService rolePermsService;
	@Autowired
	private UserRoleService userRoleService;
	@Autowired
	@Lazy
	private UserInfoService userInfoService;

	/**
	 * 查询该公司所有角色列表
	 */
	@Override
	public List<Role> getByOrg(Integer orgId) {
		if (orgId != null) {
			Example example = new Example(Role.class);
			example.createCriteria().andIsNull("deleteDate")
					.andEqualTo("orgId", orgId);
			return mapper.selectByExample(example);
		}
		return null;
	}

	/**
	 * 查询指定code的角色列表
	 */
	@Override
	public List<Role> getByCode(String code) {
		if (StringUtils.isNotBlank(code)) {
			Example example = new Example(Role.class);
			example.createCriteria().andIsNull("deleteDate")
					.andEqualTo("code", code);
			return mapper.selectByExample(example);
		}
		return null;
	}

	@Override
	public List<Role> getByUserId(Integer userId) {
		if (userId != null) {
			return mapper.selectByUser(userId);
		}
		return null;
	}

	/**
	 * 查询所有角色id列表
	 *
	 * @return
	 */
	@Override
	public List<Integer> getAll() {
		return mapper.selectAllIds();
	}

	/**
	 * 查询用户拥有的角色Id列表
	 *
	 * @param userId
	 * @return
	 */
	@Override
	public List<Integer> getByUser(Integer userId) {
		if (userId != null) {
			return mapper.selectByUserId(userId);
		}
		return null;
	}

	/**
	 * 模糊搜索
	 */
	@Override
	public List<RoleEntity> getByKeywordAndOrg(String keywords, Integer orgId) {
		if (orgId != null) {
			SpringContextUtils.setPage();
			return mapper.selectByKeywordAndOrg(keywords, orgId);
		}
		return null;
	}

	@Override
	public Role getByCodeAndOrg(String code, Integer orgId) {
		if (StringUtils.isNotBlank(code) && orgId != null) {
			Example example = new Example(Role.class);
			example.createCriteria().andEqualTo("code", code)
					.andEqualTo("orgId", orgId)
					.andIsNull("deleteDate");
			return mapper.selectOneByExample(example);
		}
		return null;
	}

	/**
	 * 查询公司拥有的角色列表
	 *
	 * @param orgId
	 * @return
	 */
	@Override
	public List<RoleVo> getVoByOrg(Integer orgId) {
		if (orgId != null) {
			return mapper.selectVoByOrg(orgId);
		}
		return null;
	}

	@Override
	@Transactional
	public void update(Role role) {
		int updateCount = 0;
		if (role != null && role.getId() != null) {
			role.setUpdateDate(new Date());
			updateCount = mapper.updateByPrimaryKey(role);
		}
		if (updateCount != 1) {
			throw new PrintException("角色修改失败");
		}
	}

	@Override
	@Transactional
	public void del(Role role) {
		int delCount = 0;
		if (role != null && role.getId() != null) {
			role.setDeleteDate(new Date());
			delCount = mapper.updateByPrimaryKey(role);
		}
		if (delCount != 1) {
			throw new PrintException("角色删除失败");
		}
	}

	@Override
	public Role get(Integer roleId) {
		if (roleId != null) {
			return mapper.selectByPrimaryKey(roleId);
		}
		return null;
	}

	@Override
	@Transactional
	public void add(Role role) {
		int addCount = 0;
		if (role != null) {
			role.setId(null);
			role.setCreateDate(new Date());
			role.setDeleteDate(null);
			addCount = mapper.insert(role);
		}
		if (addCount != 1) {
			throw new PrintException("角色添加失败");
		}
	}

	@Override
	public Role getByNameAndOrg(String name, Integer orgId) {
		if (StringUtils.isNotBlank(name) && orgId != null) {
			Example example = new Example(Role.class);
			example.createCriteria().andIsNull("deleteDate")
					.andEqualTo("name", name)
					.andEqualTo("orgId", orgId);
			return mapper.selectOneByExample(example);
		}
		return null;
	}

	/**
	 * 删除角色
	 *
	 * @param role 要删除的角色信息
	 */
	@Override
	@Transactional
	public void delRole(Role role) {
		//删除该角色关联的权限信息列表
		rolePermsService.delByRole(role.getId());

		//删除该角色关联的员工信息
		userRoleService.delByRole(role.getId());

		//查询该角色对应的员工ID列表
		List<Integer> userIds = userRoleService.getUserIdsByRoleId(role.getId());

		//删除该角色
		del(role);

		//删除该角色对应的员工缓存信息
//		String[] keys = new String[userIds.size()];
//		if (userIds != null && userIds.size() > 0) {
//			for (int i = 0; i < userIds.size(); i++) {
//				Integer userId = userIds.get(i);
//				keys[i] = RedisGlobal.USER_INFO + userId;
//			}
//		}
//		redisUtil.del(keys);
		if (userIds == null || userIds.isEmpty()) {
			return;
		}

		userIds.forEach(userId -> {
			userInfoService.del(userId);
		});
	}

	/**
	 * 获取管理员角色列表
	 *
	 * @return 角色列表
	 */
	@Override
	public List<Role> getAllAdminList() {
		Example example = new Example(Role.class);
		example.createCriteria().andIsNull("deleteDate")
				.andEqualTo("code", "admin");
		return mapper.selectByExample(example);
	}
}


