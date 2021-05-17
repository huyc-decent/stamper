package com.yunxi.stamper.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yunxi.stamper.base.BaseService;
import com.yunxi.stamper.commons.md5.MD5;
import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.other.RedisGlobal;
import com.yunxi.stamper.entity.*;
import com.yunxi.stamper.entityVo.OrgServeVo;
import com.yunxi.stamper.entityVo.UserInfo;
import com.yunxi.stamper.logger.threadLocal.LocalHandle;
import com.yunxi.stamper.mapper.OrgMapper;
import com.yunxi.stamper.service.*;
import com.yunxi.stamper.sys.context.SpringContextUtils;
import com.yunxi.stamper.sys.error.base.PrintException;
import com.zengtengpeng.annotation.Lock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/6 0006 21:55
 */
@Slf4j
@Service
public class IOrgService extends BaseService implements OrgService {

	@Autowired
	private OrgMapper mapper;
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private RelateDepartmentUserService relateDepartmentUserService;
	@Autowired
	private UserService userService;
	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private RoleTempService roleTempService;
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private RolePermsService rolePermsService;
	@Autowired
	private UserRoleService userRoleService;
	@Autowired
	private PhoneIconService phoneIconService;
	@Autowired
	private UserPhoneIconService userPhoneIconService;
	@Autowired
	private PermService permService;
	@Autowired
	private ShortcutService shortcutService;
	@Autowired
	private NoticeTempService noticeTempService;
	@Autowired
	private OrgNoticeTempService orgNoticeTempService;
	@Autowired
	private DeviceTypeService deviceTypeService;
	@Autowired
	private RoleService roleService;

	/**
	 * 查询平台所有公司列表
	 */
	@Override
	public List<OrgServeVo> getByAll() {
		return mapper.selectByAll();
	}

	@Override
	public List<Org> getAll() {
		Example example = new Example(Org.class);
		example.createCriteria().andIsNull("deleteDate");
		return mapper.selectByExample(example);
	}

	/**
	 * 查询指定类型的公司数量
	 *
	 * @param type 组织类型
	 * @return 数量值
	 */
	@Override
	public int getCountByType(Integer type) {
		if (type != null) {
			Example example = new Example(Org.class);
			example.createCriteria().andEqualTo("type", type)
					.andIsNull("deleteDate");
			return mapper.selectCountByExample(example);
		}
		return 0;
	}

	@Override
	@Transactional
	public void del(Org org) {
		int delCount = 0;

		if (org != null && org.getId() != null) {
			org.setDeleteDate(new Date());
			delCount = mapper.updateByPrimaryKey(org);
		}

		if (delCount != 1) {
			throw new PrintException("公司删除失败");
		}
		//删除缓存
		redisUtil.del(RedisGlobal.ORG_INFO + org.getId());
	}

	@Override
	@Transactional
	public void update(Org org) {
		int updateCount = 0;
		if (org != null) {
			org.setUpdateDate(new Date());
			updateCount = mapper.updateByPrimaryKey(org);
		}

		if (updateCount != 1) {
			throw new PrintException("公司信息更新失败");
		}

		//更新缓存
		redisUtil.del(RedisGlobal.ORG_INFO + org.getId());
		redisUtil.set(RedisGlobal.ORG_INFO + org.getId(), JSONObject.toJSONString(org));
	}

	@Override
	public Org get(Integer id) {
		if (id == null) {
			return null;
		}

		try {
			Object orgInfo = redisUtil.get(RedisGlobal.ORG_INFO + id);
			if (orgInfo != null) {
				return JSONObject.parseObject(orgInfo.toString(), Org.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Org org = mapper.selectByPrimaryKey(id);
		if (org == null) {
			return null;
		}

		redisUtil.set(RedisGlobal.ORG_INFO + id, JSONObject.toJSONString(org));
		return org;
	}

	/**
	 * 组织列表
	 *
	 * @param keyword 组织名称关键词
	 * @return 组织列表
	 */
	@Override
	public List<Org> get(String keyword) {
		Example example = new Example(Org.class);
		example.createCriteria().andIsNull("deleteDate");
		if (StringUtils.isNotBlank(keyword)) {
			Example.Criteria andLike = example.createCriteria().andLike("name", "%" + keyword + "%");
			example.and(andLike);
		}
		return mapper.selectByExample(example);
	}

	@Override
	@Transactional
	public void add(Org org) {
		int insert = 0;
		if (org != null) {
			org.setCreateDate(new Date());
			insert = mapper.insert(org);
		}
		if (insert != 1) {
			throw new PrintException("组织添加失败");
		}

		//加入缓存
		redisUtil.set(RedisGlobal.ORG_INFO + org.getId(), JSONObject.toJSONString(org));
	}

	@Override
	public Org getByCode(String code) {
		if (StringUtils.isNotBlank(code)) {
			Example example = new Example(Org.class);
			example.createCriteria().andEqualTo("code", code)
					.andIsNull("deleteDate");
			return mapper.selectOneByExample(example);
		}
		return null;
	}

	@Override
	public List<Org> getByName(String name) {
		if (StringUtils.isBlank(name)) {
			return null;
		}
		Example example = new Example(Org.class);
		example.createCriteria()
				.andEqualTo("name", name)
				.andIsNull("deleteDate");
		return mapper.selectByExample(example);
	}

	/**
	 * 查询组织列表用于搜索印章
	 *
	 * @return 组织列表
	 */
	@Override
	public List<Org> getOrgsForSearchSignetList() {
		UserInfo userInfo = userInfoService.get(SpringContextUtils.getToken().getUserId());
		List<Org> orgs = new LinkedList<>();
		if (userInfo.getType() == 0) {
			//系统用户，可查询系统中所有组织列表
			SpringContextUtils.setPage();
			orgs = getAll();
		} else {
			//非系统用户，仅可查询所属组织信息
			Org org = get(userInfo.getOrgId());
			orgs.add(org);
		}
		return orgs;
	}

	/**
	 * 注册组织
	 *
	 * @param orgName 组名称
	 * @param phone   管理员手机号码
	 * @param orgCode 组编码
	 */
	@Override
	@Lock(keys = "#orgName + '_' + #phone + '_' + #orgCode +'_'", keyConstant = "_IorgService_regOrg")
	@Transactional
	public void regOrg(String orgName, String phone, String orgCode) {
		//创建集团
		Org org = new Org();
		org.setName(orgName.trim());
		org.setCode(orgCode.trim());
		org.setOrgType(1);
		org.setStatus(0);
		add(org);

		//创建账户
		SysUser sysUser = sysUserService.getByPhone(phone);
		if (sysUser == null) {
			sysUser = new SysUser();
			sysUser.setPhone(phone);
			sysUser.setPassword(CommonUtils.properties.getDefaultPwd());
			sysUserService.add(sysUser);
		}

		//创建管理员用户
		User admin = new User();
		admin.setUserName(orgName + "【负责人】");
		admin.setType(org.getOrgType());
		admin.setOrgId(org.getId());
		admin.setStatus(org.getStatus());
		admin.setSysUserId(sysUser.getId());
		userService.add(admin);

		//更新集团信息
		org.setManagerUserId(admin.getId());
		update(org);

		//创建组织信息
		Department department = new Department();
		department.setOrgId(org.getId());
		department.setType(2);
		department.setManagerUserId(admin.getId());
		department.setName(org.getName());
		department.setCode(org.getCode());
		department.setLogo(org.getLogo());
		department.setLevel(0);
		departmentService.add(department);

		//创建组织-用户关联信息
		RelateDepartmentUser relateDepartmentUser = new RelateDepartmentUser();
		relateDepartmentUser.setDepartmentId(department.getId());
		relateDepartmentUser.setUserId(admin.getId());
		relateDepartmentUserService.add(relateDepartmentUser);

		//创建组织角色列表
		Role adminRole = null;
		List<RoleTemp> roleTemps = roleTempService.getAll();
		if (roleTemps != null && !roleTemps.isEmpty()) {
			for (RoleTemp roleTemp : roleTemps) {
				Role role = new Role();
				role.setOrgId(org.getId());
				role.setName(roleTemp.getName());
				role.setCode(roleTemp.getCode());
				roleService.add(role);

				if ("admin".equalsIgnoreCase(roleTemp.getCode())) {
					adminRole = role;
				}

				//根据角色模板中的权限列表id,创建角色-权限关联关系
				List<Integer> permIds = CommonUtils.splitToInteger(roleTemp.getPermIds(), ",");
				if (permIds != null && permIds.size() > 0) {
					for (Integer permId : permIds) {
						RolePerms rp = new RolePerms();
						rp.setRoleId(role.getId());
						rp.setPermsId(permId);
						rolePermsService.add(rp);
					}
				}
			}
		}

		if (adminRole == null) {
			throw new PrintException("角色绑定失败,请联系管理员");
		}

		//关联管理员与属主角色
		UserRole userRole = new UserRole();
		userRole.setUserId(admin.getId());
		userRole.setRoleId(adminRole.getId());
		userRoleService.add(userRole);

		//初始化用户手机图标
		List<PhoneIcon> phoneIcons = phoneIconService.getAll();
		if (phoneIcons != null && phoneIcons.size() > 0) {
			for (PhoneIcon phoneIcon : phoneIcons) {
				//初始化手机图标
				UserPhoneIcon userPhoneIcon = new UserPhoneIcon();
				userPhoneIcon.setPhoneIconId(phoneIcon.getId());
				userPhoneIcon.setUserId(admin.getId());
				userPhoneIconService.add(userPhoneIcon);
			}
		}

		//初始化web端快捷方式
		List<Perms> permsList = permService.getAll();
		if (permsList != null && permsList.size() > 0) {
			for (Perms perms : permsList) {
				Integer isShortcut = perms.getIsShortcut();
				//如果该权限允许作为快捷方式，则为管理员创建快捷方式
				if (isShortcut != null && isShortcut == 1) {
					Shortcut shortcut = new Shortcut();
					shortcut.setName(perms.getLabel());
					shortcut.setPermsId(perms.getId());
					shortcut.setUserId(admin.getId());
					shortcut.setOrgId(admin.getOrgId());
					shortcutService.add(shortcut);
				}
			}
		}

		//初始化通知模板
		List<NoticeTemp> noticeTemps = noticeTempService.getAll();
		if (noticeTemps != null && noticeTemps.size() > 0) {
			for (NoticeTemp nt : noticeTemps) {
				OrgNoticeTemp ont = new OrgNoticeTemp();
				ont.setOrgId(org.getId());
				ont.setNoticeTempId(nt.getId());
				orgNoticeTempService.add(ont);
			}
		}

		//通知设备端,为该公司创建默认的设备类型模板
		try {
			deviceTypeService.createTypesByOrgInit(org.getId());
		} catch (Exception e) {
			log.error("出现异常 ", e);
		}
	}

	/**
	 * 查询该手机号所属的组织列表
	 *
	 * @param phone 登录名/手机号
	 * @return 组织列表
	 */
	@Override
	public List<Org> getOrgsByPhone(String phone) {
		if (StringUtils.isNotBlank(phone)) {
			return mapper.selectOrgsByPhone(phone);
		}
		return null;
	}

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
	@Override
	@Transactional
	public void updateOrg(Org org, String name, String remark, String code, Integer managerUserId, String fileId, Integer positionId, String location) {
		org.setName(name);
		org.setRemark((StringUtils.isBlank(remark) || "undefined".equalsIgnoreCase(remark) || "null".equalsIgnoreCase(remark)) ? null : remark);
		org.setCode(code);
		org.setManagerUserId(managerUserId);
		org.setLogo(fileId);
		org.setPositionId(positionId);
		org.setLocation((StringUtils.isBlank(location) || "undefined".equalsIgnoreCase(location) || "null".equalsIgnoreCase(location)) ? null : location);

		update(org);
		LocalHandle.setNewObj(org);
		LocalHandle.complete("更新集团信息");

		List<Department> roots = departmentService.getByOrgAndType(org.getId(), 2);
		Department root = roots.get(0);

		root.setName(org.getName());
		root.setRemark(org.getRemark());
		root.setCode(org.getCode());
		root.setManagerUserId(org.getManagerUserId());
		root.setLogo(org.getLogo());
		root.setPositionId(org.getPositionId());
		root.setLocation(org.getLocation());
		departmentService.update(root);

		userInfoService.refreshByOrg(org.getId());
	}

	@Override
	public Department getRoot(Integer orgId) {
		if (orgId == null) {
			return null;
		}
		return mapper.selectRoot(orgId);
	}
}
