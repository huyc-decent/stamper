package com.yunxi.stamper.service;

import com.yunxi.stamper.entityVo.OrganizationalTree;
import com.yunxi.stamper.entityVo.UserInfo;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/7/30 0030 13:32
 */
public interface UserInfoService {

	void add(UserInfo userInfo);

	void del(UserInfo userInfo);

	void del(Integer userId);

	UserInfo get(Integer userId);

	void clearPool();

	/**
	 * 查询指定集团的组织架构树
	 *
	 * @param orgId 集团ID
	 * @return
	 */
	OrganizationalTree generatorTreeByOrg(Integer orgId);

	/**
	 * 查询用户管理的组织ID列表(包含子组织)
	 *
	 * @param info
	 * @return
	 */
	List<Integer> getManagerDepartmentIds(UserInfo info);

	/**
	 * 刷新指定公司员工的缓存信息(如果存在该缓存)
	 *
	 * @param orgId
	 */
	void refreshByOrg(Integer orgId);
}
