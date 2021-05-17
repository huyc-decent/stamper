package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.RelateDepartmentUser;
import com.yunxi.stamper.entity.User;
import com.yunxi.stamper.mapper.RelateDepartmentUserMapper;
import com.yunxi.stamper.service.RelateDepartmentUserService;
import com.yunxi.stamper.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/12/2 0002 17:30
 */
@Service
public class IRelateDepartmentUserService implements RelateDepartmentUserService {
	@Autowired
	private RelateDepartmentUserMapper mapper;
	@Autowired
	private UserService userService;

	@Override
	@Transactional
	public void add(RelateDepartmentUser departmentUser) {
		int addCount = 0;
		if (departmentUser != null && departmentUser.getDepartmentId() != null && departmentUser.getUserId() != null) {
			addCount = mapper.insert(departmentUser);
		}
		if (addCount != 1) {
			throw new PrintException("组织员工关联失败");
		}
	}

	/**
	 * 查询指定用户
	 *
	 * @param userId 用户ID
	 * @return
	 */
	@Override
	public List<Integer> getDepartmentIdsByUserId(Integer userId) {
		if (userId != null) {
			return mapper.selectDepartmentIdsByUserId(userId);
		}
		return null;
	}

	/**
	 * 查询员工、组织关联信息实体
	 *
	 * @param userId       员工ID
	 * @param departmentId 组织ID
	 * @return
	 */
	@Override
	public RelateDepartmentUser get(Integer userId, Integer departmentId) {
		if (userId != null && departmentId != null) {
			return mapper.selectByDepartmentAndUser(departmentId, userId);
		}
		return null;
	}

	/**
	 * 删除组织、员工关联信息
	 *
	 * @param departmentId  组织ID
	 * @param managerUserId 员工ID
	 */
	@Override
	@Transactional
	public void del(Integer departmentId, Integer managerUserId) {
		RelateDepartmentUser departmentUser = new RelateDepartmentUser();
		departmentUser.setDepartmentId(departmentId);
		departmentUser.setUserId(managerUserId);
		mapper.delete(departmentUser);
	}

	/**
	 * 删除指定组织ID列表下的关联关系
	 *
	 * @param departmentIds
	 */
	@Override
	@Transactional
	public void delByDepartmentIds(List<Integer> departmentIds) {
		if (departmentIds != null && departmentIds.size() > 0) {
			//查询组织员工列表
			List<User> users = userService.getByDepartment(departmentIds);

			for (int i = 0; i < departmentIds.size(); i++) {
				mapper.delByDepartmentIds(departmentIds);
			}
			/**
			 * 检查是否有员工没有组织关联信息，如果有将该员工信息删除
			 */
			if (users != null && users.size() > 0) {
				for (int i = 0; i < users.size(); i++) {
					User user = users.get(i);
					List<Integer> departIds = getDepartmentIdsByUserId(user.getId());
					if (departIds == null || departIds.isEmpty()) {
						userService.delEmployee(user);
					}
				}
			}
		}
	}

	/**
	 * 删除组织-员工关联信息
	 *
	 * @param userId
	 */
	@Override
	@Transactional
	public void delByEmployeeId(Integer userId) {
		mapper.delByUserId(userId);
	}
}
