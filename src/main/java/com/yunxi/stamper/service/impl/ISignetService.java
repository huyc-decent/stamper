package com.yunxi.stamper.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yunxi.common.utils.ListUtils;
import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.other.RedisGlobal;
import com.yunxi.stamper.logger.threadLocal.LocalHandle;
import com.yunxi.stamper.service.*;
import com.yunxi.stamper.sys.context.SpringContextUtils;
import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.*;
import com.yunxi.stamper.entityVo.*;
import com.yunxi.stamper.mapper.SignetMapper;
import com.yunxi.stamper.base.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/2 0002 19:40
 */
@Slf4j
@Service
public class ISignetService extends BaseService implements SignetService {

	@Autowired
	private SignetMapper mapper;
	@Autowired
	private ApplicationKeeperService applicationKeeperService;
	@Autowired
	private ApplicationAuditorService applicationAuditorService;
	@Autowired
	private MessageTempService messageTempService;
	@Autowired
	private OrgService orgService;
	@Autowired
	@Lazy
	private UserInfoService userInfoService;

	/**
	 * 新增印章
	 *
	 * @param signet
	 */
	@Override
	@Transactional
	public void add(Signet signet) {
		int addCount = 0;
		if (signet != null) {
			signet.setCreateDate(new Date());
			addCount = mapper.insert(signet);
		}
		if (addCount != 1) {
			throw new PrintException("印章注册失败");
		}

		/***更新缓存*/
		try {
			String key = RedisGlobal.DEVICE_INFO + signet.getUuid();
			redisUtil.set(key, JSONObject.toJSONString(signet), RedisGlobal.DEVICE_INFO_TIMEOUT);
		} catch (Exception e) {
			log.error("出现异常 ", e);
		}
	}

	/**
	 * 更新印章
	 *
	 * @param signet
	 */
	@Override
	@Transactional
	public void update(Signet signet) {
		int update = 0;
		if (signet != null && signet.getId() != null) {
			signet.setUpdateDate(new Date());
			if (StringUtils.isBlank(signet.getNetwork())) {
				signet.setNetwork("");
			}
			update = mapper.updateByPrimaryKey(signet);
		}
		if (update != 1) {
			throw new PrintException("印章信息更新失败");
		}

		/***更新缓存*/
		try {
			String key = RedisGlobal.DEVICE_INFO + signet.getUuid();
			redisUtil.set(key, JSONObject.toJSONString(signet), RedisGlobal.DEVICE_INFO_TIMEOUT);
		} catch (Exception e) {
			log.error("出现异常 ", e);
		}
	}

	/**
	 * 更新设备摄像头状态
	 *
	 * @param deviceId 设备ID
	 * @param status   0：开启  1：关闭
	 */
	@Override
	@Transactional
	public void updateCamera(Integer deviceId, int status) {
		Signet device = new Signet();
		device.setId(deviceId);
		device.setCamera(status);
		device.setUpdateDate(new Date());
		mapper.updateByPrimaryKeySelective(device);
	}

	/**
	 * 更新印章
	 *
	 * @param signet     原印章信息
	 * @param bodyId     章身ID
	 * @param name       新名称
	 * @param fileInfo   新LOGO
	 * @param keeper     新印章管理员
	 * @param auditor    新印章审计员
	 * @param department 新所属组织
	 * @param deviceType 新设备类型
	 */
	@Override
	@Transactional
	public void updateSignet(Signet signet, String bodyId, String name, FileInfo fileInfo, User keeper, User auditor, Department department, DeviceType deviceType, Integer meterId, String remark) {
		LocalHandle.setOldObj(signet);

		/**
		 * 备份印章原信息
		 */
		Integer oldKeeperId = signet.getKeeperId();
		Integer oldAuditorId = signet.getAuditorId();

		/**
		 * 更新印章信息
		 */
		signet.setName(name.trim());
		signet.setMeterId(meterId);
		signet.setRemark(remark);
		if (bodyId != null) {
			signet.setBodyId(bodyId);
		}
		if (fileInfo != null) {
			signet.setLogo(fileInfo.getId());
		} else {
			signet.setLogo(null);
		}
		if (keeper != null) {
			signet.setKeeperId(keeper.getId());
			signet.setKeeperName(keeper.getUserName());
		} else {
			signet.setKeeperId(null);
			signet.setKeeperName(null);
		}
		if (auditor != null) {
			signet.setAuditorId(auditor.getId());
			signet.setAuditorName(auditor.getUserName());
		} else {
			signet.setAuditorName(null);
			signet.setAuditorId(null);
		}
		if (department != null) {
			signet.setDepartmentId(department.getId());
			signet.setDepartmentName(department.getName());
		} else {
			signet.setDepartmentId(null);
			signet.setDepartmentName(null);
		}
		if (deviceType != null) {
			signet.setTypeId(deviceType.getId());
		} else {
			signet.setTypeId(null);
		}
		update(signet);

		/**
		 * 修改原印章管理员的审批单-->新管章人
		 */
		Integer newKeeperId = signet.getKeeperId();
		if (!CommonUtils.isEquals(oldKeeperId, newKeeperId)) {
			if (newKeeperId != null) {
				applicationKeeperService.updateFromSignetDate(signet, oldKeeperId, keeper);
			}
		}

		/**
		 * 修改原印章审计员的审批单-->新审计人
		 */
		Integer newAuditorId = signet.getAuditorId();
		if (!CommonUtils.isEquals(oldAuditorId, newAuditorId)) {
			if (newAuditorId != null) {
				applicationAuditorService.updateFromSignetDate(signet, oldAuditorId, auditor);
			}
		}


		/**
		 * 计算需要发送通知的员工ID列表
		 */
		Set<Integer> idsByNotice = new HashSet<>();
		idsByNotice.add(oldKeeperId);
		idsByNotice.add(oldAuditorId);
		idsByNotice.add(newKeeperId);
		idsByNotice.add(newAuditorId);

		/**
		 * 发送通知+短信
		 */
//		UserInfo userInfo = SpringContextUtils.getUserInfo();
		UserInfo userInfo = userInfoService.get(SpringContextUtils.getToken().getUserId());
		if (!idsByNotice.isEmpty()) {
			for (Integer userId : idsByNotice) {
				if (userId != null) {
					Integer orgId = signet.getOrgId();
					Org org = orgService.get(orgId);
					String orgName = org.getName();
					messageTempService.updateDeviceNotice(orgName, signet.getName(), userInfo.getUserName(), userId);
				}
			}
		}

		LocalHandle.setNewObj(signet);
		LocalHandle.complete("更新印章信息");
	}

	/**
	 * 删除印章
	 *
	 * @param signet
	 */
	@Override
	@Transactional
	public void del(Signet signet) {
		int delCount = 0;
		if (signet != null && signet.getId() != null) {
			signet.setDeleteDate(new Date());
			delCount = mapper.updateByPrimaryKey(signet);
		}
		if (delCount != 1) {
			throw new PrintException("删除印章失败");
		}

		//删除缓存
		try {
			String key = RedisGlobal.DEVICE_INFO + signet.getUuid();
			redisUtil.del(key);
		} catch (Exception e) {
			log.error("出现异常 ", e);
		}
	}

	/**
	 * 印章信息
	 *
	 * @param signetId 印章ID
	 * @return
	 */
	@Override
	public Signet get(Integer signetId) {
		if (signetId == null) {
			return null;
		}
		return mapper.selectByPrimaryKey(signetId);
	}

	/**
	 * 印章信息
	 *
	 * @param uuid 印章UUID
	 * @return
	 */
	@Override
	public Signet getByUUID(String uuid) {

		if (StringUtils.isBlank(uuid)) {
			return null;
		}

		//先从缓存取
		String key = RedisGlobal.DEVICE_INFO + uuid;
		Object signetObj = redisUtil.get(key);
		if (signetObj != null && StringUtils.isNotBlank(signetObj.toString())) {
			Signet signet = JSONObject.parseObject(signetObj.toString(), Signet.class);
			return signet;
		}

		Example example = new Example(Signet.class);
		example.createCriteria()
				.andIsNull("deleteDate")
				.andEqualTo("uuid", uuid);
		Signet signet = mapper.selectOneByExample(example);

		//存到缓存
		if (signet != null) {
			redisUtil.set(key, JSONObject.toJSONString(signet), RedisGlobal.DEVICE_INFO_TIMEOUT);
		}

		return signet;
	}

	/**
	 * 印章信息
	 *
	 * @param name  印章名称
	 * @param orgId 集团ID
	 * @return
	 */
	@Override
	public Signet getByName(String name, Integer orgId) {
		if (StringUtils.isBlank(name) || orgId == null) {
			return null;
		}
		Example example = new Example(Signet.class);
		example.createCriteria().andEqualTo("orgId", orgId)
				.andIsNull("deleteDate")
				.andEqualTo("name", name);
		return mapper.selectOneByExample(example);
	}

	/**
	 * 印章信息
	 *
	 * @param orgId    集团ID
	 * @param deviceId 印章ID
	 * @return
	 */
	@Override
	public Signet getByOrgAndDevice(Integer orgId, Integer deviceId) {
		if (orgId == null || deviceId == null) {
			return null;
		}
		Example example = new Example(Signet.class);
		example.createCriteria().andIsNull("deleteDate")
				.andEqualTo("orgId", orgId)
				.andEqualTo("id", deviceId);
		return mapper.selectOneByExample(example);
	}

	/**
	 * 印章列表
	 *
	 * @param orgId 集团ID
	 * @return
	 */
	@Override
	public List<Signet> getByOrg(Integer orgId) {
		if (orgId == null) {
			return null;
		}
		SpringContextUtils.setPage();
		return mapper.selectByOrg(orgId);
	}

	/**
	 * 印章列表
	 *
	 * @param orgId         公司ID
	 * @param type          设备类型
	 * @param keeperName    管章人姓名
	 * @param deviceName    印章名称
	 * @param departmentIds 组织ID列表
	 * @param keeperId      管章人ID
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getByType(Integer orgId, Integer type, String keeperName, String deviceName, List<Integer> departmentIds, Integer keeperId) {
		if (orgId == null) {
			return null;
		}
		return mapper.selectByType(orgId, type, keeperName, deviceName, departmentIds, keeperId);
	}

	/**
	 * 所有印章的位置信息列表
	 *
	 * @param orgId 集团ID
	 * @return
	 */
	@Override
	public List<Location> getLocationByOrg(Integer orgId) {
		if (orgId == null) {
			return null;
		}
		return mapper.selectLocationByOrg(orgId);
	}

	/**
	 * 印章列表
	 *
	 * @param orgId         集团ID
	 * @param keeperId      管章人ID,如果是集团属主，该值为null
	 * @param keyword       印章名称、ID关键词
	 * @param departmentIds 要查询的组织ID列表
	 * @return
	 */
	@Override
	public List<SignetEntity> getByOwner(Integer orgId, Integer keeperId, String keyword, List<Integer> departmentIds, Set<String> onlines) {
		if (orgId == null) {
			return null;
		}
		List<SignetEntity> signetEntities = null;
		SpringContextUtils.setPage();
		if (onlines == null || onlines.isEmpty()) {
			signetEntities = mapper.selectByOwner(orgId, keeperId, keyword, departmentIds);
		} else {
			signetEntities = mapper.selectByOwnerAndOnlie(orgId, keeperId, keyword, departmentIds, onlines);
		}
		return signetEntities;
	}

	/**
	 * 印章列表
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 组织ID列表
	 * @return
	 */
	@Override
	public List<Signet> getSignetByOrgAndDepartments(Integer orgId, List<Integer> departmentIds, Integer userId) {
		if (orgId == null) {
			return null;
		}
		SpringContextUtils.setPage();
		return mapper.selectSignetByOrgAndDepartments(orgId, departmentIds, userId);
	}

	/**
	 * 印章总数
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 组织ID列表
	 * @return
	 */
	@Override
	public int getCountByOrgAndDepartments(Integer orgId, List<Integer> departmentIds) {
		if (orgId == null) {
			return 0;
		}
		return mapper.selectCountByOrgAndDepartment(orgId, departmentIds);
	}

	/**
	 * 印章ID列表
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 组织ID列表
	 * @return
	 */
	@Override
	public List<Integer> getIdByOrgAndDepartment(Integer orgId, List<Integer> departmentIds) {
		if (orgId == null) {
			return null;
		}
		return mapper.selectIdByOrgAndDepartment(orgId, departmentIds);
	}

	@Override
	public List<Integer> getByBodyId(String bodyId) {
		if (StringUtils.isBlank(bodyId)) {
			return null;
		}
		Example example = new Example(Signet.class);
		example.createCriteria().andIsNull("deleteDate")
				.andEqualTo("bodyId", bodyId);
		List<Signet> signets = mapper.selectByExample(example);
		if (signets == null || signets.isEmpty()) {
			return null;
		}
		List<Integer> deviceIds = new LinkedList<>();
		signets.forEach(device -> {
			deviceIds.add(device.getId());
		});
		return deviceIds;
	}

	/**
	 * 印章列表
	 *
	 * @param orgId   集团ID
	 * @param keyword 搜索关键词
	 * @return
	 */
	@Override
	public List<Signet> getList(Integer orgId, String keyword) {
		if (orgId == null) {
			return null;
		}

		Example example = new Example(Signet.class);
		example.createCriteria().andIsNull("deleteDate")
				.andEqualTo("orgId", orgId);

		if (StringUtils.isNotBlank(keyword)) {
			Example.Criteria criteria = example.createCriteria().orLike("id", "%" + keyword + "%")
					.orLike("name", "%" + keyword + "%")
					.orLike("remark", "%" + keyword + "%")
					.orLike("uuid", "%" + keyword + "%");
			example.and(criteria);
		}

		List<Signet> signets = mapper.selectByExample(example);
		return signets;
	}

	/**
	 * 印章列表
	 *
	 * @param departmentIds 组织ID
	 * @return 印章列表
	 */
	@Override
	public List<Signet> getByDepartment(List<Integer> departmentIds) {
		if (departmentIds == null || departmentIds.isEmpty()) {
			return null;
		}
		Example example = new Example(Signet.class);
		example.createCriteria().andIsNull("deleteDate")
				.andIn("departmentId", departmentIds);
		return mapper.selectByExample(example);
	}

	@Override
	@Transactional
	public void addBatch(List<Signet> signets) {
		int addCount = 0;
		signets.forEach(signet -> signet.setCreateDate(new Date()));
		addCount = mapper.insertList(signets);
		if (addCount == 0) {
			throw new PrintException("印章注册失败");
		}

		/***更新缓存*/
		try {
			signets.forEach(signet -> {
				String key = RedisGlobal.DEVICE_INFO + signet.getUuid();
				redisUtil.set(key, JSONObject.toJSONString(signet), RedisGlobal.DEVICE_INFO_TIMEOUT);
			});
		} catch (Exception e) {
			log.error("出现异常 ", e);
		}
	}

	@Override
	@Transactional
	public void updateBatch(List<Signet> signets) {
		int update = 0;
		signets.forEach(signet -> signet.setUpdateDate(new Date()));
		update = mapper.updateBatch(signets);
		if (update == 0) {
			throw new PrintException("印章信息更新失败");
		}

		/***更新缓存*/
		try {
			signets.forEach(signet -> {
				String key = RedisGlobal.DEVICE_INFO + signet.getUuid();
				redisUtil.set(key, JSONObject.toJSONString(signet), RedisGlobal.DEVICE_INFO_TIMEOUT);
			});
		} catch (Exception e) {
			log.error("出现异常 ", e);
		}
	}

	/**
	 * 查询设备列表
	 *
	 * @param orgId              集团ID
	 * @param departmentIds      组织ID
	 * @param isApplicationAbled 印章是否支持申请单
	 * @return 印章列表
	 */
	@Override
	public List<Signet> get(Integer orgId, List<Integer> departmentIds, Integer isApplicationAbled) {
		if (orgId == null || departmentIds == null || departmentIds.isEmpty()) {
			return null;
		}

		Example example = new Example(Signet.class);
		example.createCriteria().andIsNull("deleteDate")
				.andEqualTo("orgId", orgId)
				.andIn("departmentId", departmentIds);

		if (isApplicationAbled != null) {
			Example.Criteria applicationAbled = example.createCriteria().andEqualTo("isEnableApplication", isApplicationAbled);
			example.and(applicationAbled);
		}

		return mapper.selectByExample(example);
	}

	/**
	 * 查询设备列表
	 *
	 * @param orgId         租户Id
	 * @param keeperId      管章人Id
	 * @param departmentIds 组织Id
	 * @param onlineIds     在线设备Id
	 * @param keyword       关键词
	 * @return
	 */
	@Override
	public List<Signet> find(Integer orgId, Integer keeperId, List<Integer> departmentIds, List<String> onlineIds, String keyword) {
		if (ListUtils.isEmpty(onlineIds)) {
			Example example = new Example(Signet.class);
			example.createCriteria().andIsNull("deleteDate")
					.andEqualTo("orgId", orgId);
			if (ListUtils.isNotEmpty(departmentIds)) {
				Example.Criteria departmentId = example.createCriteria().andIn("departmentId", departmentIds);
				example.and(departmentId);
			}
			if (StringUtils.isNotBlank(keyword)) {
				keyword = keyword.trim();
				Example.Criteria criteria = example.createCriteria().orLike("name", "%" + keyword + "%")
						.orLike("id", "%" + keyword + "%");
				example.and(criteria);
			}
			if (keeperId != null) {
				Example.Criteria equal = example.createCriteria().andEqualTo("keeperId", keeperId);
				example.and(equal);
			}
			example.orderBy("id");
			return mapper.selectByExample(example);
		}
		return mapper.find(orgId, keeperId, departmentIds, onlineIds, keyword);
	}
}
