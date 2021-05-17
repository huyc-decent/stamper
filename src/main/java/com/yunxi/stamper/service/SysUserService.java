package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.SysUser;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/11/28 0028 14:43
 */
public interface SysUserService {

	/**
	 * 添加用户
	 * @param sysUser 用户信息
	 */
	void add(SysUser sysUser);

	/**
	 * 查询指定手机号的用户信息
	 * @param phone 手机号
	 * @return
	 */
	SysUser getByPhone(String phone);

	/**
	 * 查询索引
	 * @param sysUserId 索引ID
	 * @return
	 */
	SysUser get(Integer sysUserId);

	/**
	 * 查询组织下账号信息
	 *
	 * @param orgId 集团ID
	 * @param phone 登录号、手机号
	 * @return 账号信息
	 */
	SysUser get(Integer orgId, String phone);

	/**
	 * 更新用户信息
	 * @param sysUser
	 */
	void update(SysUser sysUser);

	void del(SysUser sysUser);

}
