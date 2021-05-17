package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.SysUser;
import com.yunxi.stamper.mapper.SysUserMapper;
import com.yunxi.stamper.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/11/28 0028 14:43
 */
@Slf4j
@Service
public class ISysUserService implements SysUserService {
	@Autowired
	private SysUserMapper mapper;

	@Override
	@Transactional
	public void add(SysUser sysUser) {
		int insert = 0;
		if (sysUser != null) {
			sysUser.setCreateDate(new Date());
			sysUser.setUpdateDate(new Date());
			insert = mapper.insert(sysUser);
		}
		if (insert != 1) {
			log.info("新增账号有误\tsysUser:{}", CommonUtils.objJsonWithIgnoreFiled(sysUser));
			throw new PrintException("新增账号有误");
		}
	}

	/**
	 * 查询指定手机号的用户信息
	 *
	 * @param phone 手机号
	 * @return
	 */
	@Override
	public SysUser getByPhone(String phone) {
		if (StringUtils.isBlank(phone)) {
			return null;
		}
		return mapper.selectByPhone(phone);
	}

	@Override
	@Transactional
	public void update(SysUser sysUser) {
		int updateCount = 0;
		if (sysUser != null && sysUser.getId() != null) {
			sysUser.setUpdateDate(new Date());
			updateCount = mapper.updateByPrimaryKey(sysUser);
		}
		if (updateCount != 1) {
			throw new PrintException("用户信息更新失败");
		}
	}


	@Override
	@Transactional
	public void del(SysUser sysUser) {
		int delete = 0;
		if (sysUser != null && sysUser.getId() != null) {
			sysUser.setDeleteDate(new Date());
			delete = mapper.updateByPrimaryKey(sysUser);
		}
		if (delete != 1) {
			throw new PrintException("账号信息删除失败");
		}
	}

	/**
	 * 查询索引
	 *
	 * @param sysUserId 索引ID
	 * @return
	 */
	@Override
	public SysUser get(Integer sysUserId) {
		if (sysUserId != null) {
			Example example = new Example(SysUser.class);
			example.createCriteria().andIsNull("deleteDate")
					.andEqualTo("id", sysUserId);
			return mapper.selectOneByExample(example);
		}
		return null;
	}

	/**
	 * 查询组织下账号信息
	 *
	 * @param orgId 集团ID
	 * @param phone 登录号、手机号
	 * @return 账号信息
	 */
	@Override
	public SysUser get(Integer orgId, String phone) {
		if (orgId == null || StringUtils.isBlank(phone)) {
			return null;
		}
		return mapper.selectByOrgAndPhone(orgId, phone);
	}
}
