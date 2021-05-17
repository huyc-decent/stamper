package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.Department;
import com.yunxi.stamper.entity.Org;
import com.yunxi.stamper.entityVo.OrgServeVo;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/6 0006 21:55
 */
public interface OrgService {
	/**
	 * @param name
	 * @return
	 */
	List<Org> getByName(String name);

	/**
	 * @param code
	 * @return
	 */
	Org getByCode(String code);

	/**
	 * @param org
	 */
	void add(Org org);

	/**
	 * @param org
	 */
	void update(Org org);

	/**
	 * @param org
	 */
	void del(Org org);

	/**
	 * @param id
	 * @return
	 */
	Org get(Integer id);

	/**
	 * 组织列表
	 *
	 * @param keyword 组织名称关键词
	 * @return 组织列表
	 */
	List<Org> get(String keyword);

	//查询指定类型的公司数量

	/**
	 * @param type
	 * @return
	 */
	int getCountByType(Integer type);

	/**
	 * @return
	 */
	List<Org> getAll();

	//查询平台所有公司列表

	/**
	 * @return
	 */
	List<OrgServeVo> getByAll();

	/**
	 * 查询组织列表用于搜索印章
	 */
	List<Org> getOrgsForSearchSignetList();

	/**
	 * 注册组织
	 *
	 * @param orgName 组织名称
	 * @param phone   管理员手机号码
	 * @param orgCode 公司编码
	 */
	void regOrg(String orgName, String phone, String orgCode);

	/**
	 * 查询该手机号所属的组织列表
	 *
	 * @param phone 登录名/手机号
	 * @return
	 */
	List<Org> getOrgsByPhone(String phone);

	/**
	 * 更新集团公司信息
	 *
	 * @param org           要更新的集团源信息
	 * @param name          新名称
	 * @param remark        新简介
	 * @param code          新编码
	 * @param managerUserId 新负责人
	 * @param fileId        新LOGO图片ID
	 * @param positionId    新负责人称谓
	 * @param location      新地址
	 */
	void updateOrg(Org org, String name, String remark, String code, Integer managerUserId, String fileId, Integer positionId, String location);

	/**
	 * 查询集团root组织
	 *
	 * @param orgId
	 * @return
	 */
	Department getRoot(Integer orgId);

}
