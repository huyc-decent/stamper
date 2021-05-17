package com.yunxi.stamper.mapper;


import com.yunxi.stamper.entity.SysUser;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

@Component
public interface SysUserMapper extends MyMapper<SysUser> {
	/**
	 * 查询账号信息
	 * @param phone 登录号、手机号
	 * @return 账号信息
	 */
	SysUser selectByPhone(String phone);

	/**
	 * 查询组织下账号信息
	 *
	 * @param orgId 集团ID
	 * @param phone 登录号、手机号
	 * @return 账号信息
	 */
	SysUser selectByOrgAndPhone(Integer orgId, String phone);
}