package com.yunxi.stamper.controller;

import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.fileUpload.FileEntity;
import com.yunxi.stamper.commons.fileUpload.FileUtil;
import com.yunxi.stamper.commons.other.AppConstant;
import com.yunxi.stamper.commons.other.Global;
import com.yunxi.stamper.commons.other.RedisGlobal;
import com.yunxi.stamper.commons.response.Code;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.*;
import com.yunxi.stamper.entityVo.ConfigVo;
import com.yunxi.stamper.entityVo.UpdateAPK;
import com.yunxi.stamper.entityVo.UserInfo;
import com.yunxi.stamper.logger.threadLocal.LocalHandle;
import com.yunxi.stamper.service.*;
import com.yunxi.stamper.sys.config.ProjectProperties;
import com.yunxi.stamper.sys.context.SpringContextUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/6/28 0028 11:41
 */
@Slf4j
@Api(tags = "设备配置")
@RestController
@RequestMapping(value = "/device/config", method = {RequestMethod.POST, RequestMethod.GET})
public class ConfigController extends BaseController {

	@Autowired
	private ConfigService service;
	@Autowired
	private ConfigErrorService configErrorService;
	@Autowired
	private ConfigVersionService configVersionService;
	@Autowired
	private SignetService signetService;
	@Autowired
	private DeviceMigrateLogService deviceMigrateLogService;
	@Autowired
	private ProjectProperties projectProperties;
	@Autowired
	private OrgService orgService;

	@ApiOperation(value = "功能列表", notes = "查询设备功能列表")
	@GetMapping("/enabledList")
	public ResultVO enabledList(@RequestParam String uuid) {
		//检查操作权限
		UserInfo userInfo = getUserInfo();
		Integer type = userInfo.getType();
		if (!Objects.equals(type, 0)) {
			return ResultVO.FAIL("无权限操作");
		}

		//查询设备配置信息
		Config config = service.getByUUID(uuid);
		if (config == null) {
			//如果为空的话,则使用默认配置创建一个新的配置信息
			Config defaultConfig = service.getDefaultConfig();
			defaultConfig.setUuid(uuid);
			defaultConfig.setCreateDate(new Date());
			defaultConfig.setUpdateDate(new Date());
			defaultConfig.setId(null);
			service.add(defaultConfig);
			config = defaultConfig;
		}
		Map<String, Object> map = new HashMap<>();
		map.put("uuid", config.getUuid());
		map.put("applicationEnabled", config.getIsEnableApplication());
		map.put("productionTest", config.getIsProductionTest());

		return ResultVO.OK(map);
	}

	@ApiOperation(value = "设备使能_申请单", notes = "开启或关闭设备的申请单功能")
	@PostMapping("/enabledApplication")
	public ResultVO enabledApplication(@RequestParam String uuid, @RequestParam Integer enabled) throws Exception {
		//检查操作权限
		UserInfo userInfo = getUserInfo();
		Integer type = userInfo.getType();
		if (!Objects.equals(type, 0)) {
			return ResultVO.FAIL("无权限操作");
		}
		//修改数据库
		Config config = service.getByUUID(uuid);
		if (config == null) {
			//如果为空的话,则使用默认配置创建一个新的配置信息
			Config defaultConfig = service.getDefaultConfig();
			defaultConfig.setUuid(uuid);
			defaultConfig.setCreateDate(new Date());
			defaultConfig.setUpdateDate(new Date());
			defaultConfig.setId(null);
			service.add(defaultConfig);
			config = defaultConfig;
		}
		LocalHandle.setOldObj(config);
		config.setIsEnableApplication(enabled);
		service.update(config);
		LocalHandle.setNewObj(config);
		LocalHandle.complete("更新设备配置");
		return ResultVO.OK("success");
	}

	@ApiOperation(value = "设备使能_产测", notes = "开启或关闭设备的产测功能")
	@PostMapping("/enabledProductionTest")
	public ResultVO enabledProductionTest(@RequestParam String uuid, @RequestParam Integer enabled) throws Exception {
		//检查操作权限
		UserInfo userInfo = getUserInfo();
		Integer type = userInfo.getType();
		if (!Objects.equals(type, 0)) {
			return ResultVO.FAIL("无权限操作");
		}

		//修改数据库
		Config config = service.getByUUID(uuid);
		if (config == null) {
			//如果为空的话,则使用默认配置创建一个新的配置信息
			Config defaultConfig = service.getDefaultConfig();
			defaultConfig.setUuid(uuid);
			defaultConfig.setCreateDate(new Date());
			defaultConfig.setUpdateDate(new Date());
			defaultConfig.setId(null);
			service.add(defaultConfig);
			config = defaultConfig;
		}
		LocalHandle.setOldObj(config);

		config.setIsProductionTest(enabled);
		service.update(config);

		LocalHandle.setNewObj(config);
		LocalHandle.complete("更新设备配置");
		return ResultVO.OK("success");
	}

	/**
	 * 更新APK(设备端专用)
	 *
	 * @param uuid 设备UUID
	 * @return json
	 */
	@RequestMapping("/updateAPK")
	public UpdateAPK updateAPK(@RequestParam(value = "uuid") String uuid) {
		UpdateAPK apk = new UpdateAPK();
		Config config = service.getByUUID(uuid);
		//uuid不存在,或不存在该uuid的配置,则返回全局默认配置
		if (config == null) {
			config = service.getDefaultConfig();
		}

		String version = config.getVersion();
		String versionUrl = config.getVersionUrl();
		if (StringUtils.isNoneBlank(version, versionUrl)) {
			try {
				apk.setVersion(Float.parseFloat(config.getVersion()));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			apk.setUrl(config.getVersionUrl());
		}

		Signet signet = signetService.getByUUID(uuid);
		if (signet != null) {
			log.info("√\t校验apk版本\tid:{}\t设备名:{}\t设备次数:{}\t最新版本号:{}", signet.getId(), signet.getName(), signet.getCount(), apk.getVersion());
		} else {
			log.info("X\t校验apk版本\tuuid:{}\t更新APK:{}", uuid, apk);
		}
		return apk;
	}

	/**
	 * 将设备配置重置为默认配置
	 *
	 * @param uuid 设备UUID
	 * @return json
	 */
	@RequestMapping("/reset")
	public ResultVO reset(@RequestParam(value = "uuid") String uuid) {
		//仅平台账户可用
		UserInfo userInfo = getUserInfo();
		if (userInfo.getType() != 0) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		if (StringUtils.isBlank(uuid) || uuid.equalsIgnoreCase(Global.defaultUUID)) {
			return ResultVO.FAIL("该设备配置信息无法重置");
		}
		Config config = service.getByUUID(uuid);
		if (config == null) {
			return ResultVO.FAIL("暂无该设备配置信息,无法重置");
		}
		service.del(config);
		return ResultVO.OK("已重置成功,该设备当前已使用默认配置");
	}

	/**
	 * 更新APK版本
	 *
	 * @param uuid      设备UUID
	 * @param versionId null:代表删除
	 * @return 结果
	 */
	@RequestMapping("/addOrUpdateVersion")
	@Transactional
	public ResultVO addOrUpdateVersion(@RequestParam("uuid") String uuid, @RequestParam(value = "id", required = false) Integer versionId) {
		//仅平台账户可用
		UserInfo userInfo = getUserInfo();
		if (userInfo.getType() != 0) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		//如果不是删除版本的话,版本号id必须不能为空
		ConfigVersion configVersion = null;
		if (versionId != null) {
			configVersion = configVersionService.get(versionId);
			if (configVersion == null) {
				return ResultVO.FAIL("该APK版本不存在");
			}
		}
		//查询指定设备的配置信息
		Config config = service.getByUUID(uuid);
		if (config == null) {
			//如果为空的话,则使用默认配置创建一个新的配置信息
			Config defaultConfig = service.getDefaultConfig();
			defaultConfig.setUuid(uuid);
			defaultConfig.setCreateDate(new Date());
			defaultConfig.setUpdateDate(new Date());
			defaultConfig.setId(null);
			service.add(defaultConfig);
			config = defaultConfig;
		}

		String version = null;
		String url = null;
		String remark = null;
		if (configVersion != null) {
			version = configVersion.getVersion();
			url = configVersion.getUrl();
			remark = configVersion.getRemark();
		}
		config.setVersion(version);
		config.setVersionUrl(url);
		config.setApkName(remark);
		service.update(config);
		return ResultVO.OK("版本已同步");
	}

	/**
	 * 版本切换
	 *
	 * @param uuid   印章uuid
	 * @param status 0：旧版本 1：新版本
	 * @return 结果
	 */
	@RequestMapping("/changeVersions")
	public ResultVO changeVersions(@RequestParam("uuid") String uuid, @RequestParam("status") Integer status) {
		//仅平台账户可用
		UserInfo userInfo = getUserInfo();
		if (userInfo.getType() != 0) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		Config config = service.getByUUID(uuid);
		if (config != null) {
			LocalHandle.setOldObj(config);
			config.setStatus(status);
			service.update(config);
		} else {
			//查询默认配置
			config = service.getDefaultConfig();

			config.setUuid(uuid);
			config.setId(null);
			config.setStatus(status);
			service.add(config);

			LocalHandle.setOldObj(config);

			//查询印章是否存在
			Signet signet = signetService.getByUUID(uuid);
			if (signet == null) {
				//创建新印章
				signet = new Signet();
				signet.setUuid(uuid);
				signet.setOrgId(-1);
				signetService.add(signet);

				signet.setName("印章(新" + signet.getId() + ")");
				signetService.update(signet);
			}
		}

		LocalHandle.setNewObj(config);
		LocalHandle.complete("版本切换(新/旧)");

		return ResultVO.OK();
	}

	/**
	 * 批量版本切换
	 *
	 * @param uuids  印章uuids
	 * @param status 0：旧版本 1：新版本
	 * @return 结果
	 */
	@RequestMapping("/changeVersionsBatch")
	public ResultVO changeVersionsBatch(@RequestParam("uuids") String uuids, @RequestParam("status") Integer status) {
		//仅平台账户可用
		UserInfo userInfo = getUserInfo();
		if (userInfo.getType() != 0) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		/*遍历数据*/
		String[] uuidString = uuids.split(",");
		List<Config> addConfigs = new ArrayList<>();
		List<Config> updateConfigs = new ArrayList<>();
		List<Signet> signets = new ArrayList<>();

		if (uuidString.length > 0) {
			for (String uuid : uuidString) {
				if (StringUtils.isEmpty(uuid)) {
					continue;
				}
				Config config = service.getByUUID(uuid);
				if (config != null) {
					LocalHandle.setOldObj(config);
					config.setStatus(status);
					updateConfigs.add(config);
				} else {
					//查询默认配置
					config = service.getDefaultConfig();
					config.setUuid(uuid);
					config.setId(null);
					config.setStatus(status);
					addConfigs.add(config);

					LocalHandle.setOldObj(config);

					//查询印章是否存在
					Signet signet = signetService.getByUUID(uuid);
					if (signet == null) {
						//创建新印章
						signet = new Signet();
						signet.setUuid(uuid);
						signet.setOrgId(-1);
						signets.add(signet);
					}
				}

				LocalHandle.setNewObj(config);
			}
		}

		/*插入数据*/
		try {
			if (!addConfigs.isEmpty()) {
				service.addBatch(addConfigs);
			}
			if (!updateConfigs.isEmpty()) {
				service.updateBatch(updateConfigs);
			}
			if (!signets.isEmpty()) {
				signetService.addBatch(signets);
			}
			/*批量更新数据*/
			signets.forEach(signet -> {
				signet.setUpdateDate(new Date());
				if (StringUtils.isBlank(signet.getNetwork())) {
					signet.setNetwork("");
				}
				signet.setName("印章(新" + signet.getId() + ")");
			});
			if (!signets.isEmpty()) {
				signetService.updateBatch(signets);
			}
		} catch (Exception e) {
			log.error("出现异常 ", e);
		}

		/*添加日志*/
		updateConfigs.addAll(addConfigs);
		Integer[] ids = new Integer[updateConfigs.size()];
		for (int i = 0; i < updateConfigs.size(); i++) {
			ids[i] = updateConfigs.get(i).getId();
		}
		LocalHandle.setbatchId(Arrays.toString(ids));
		LocalHandle.complete("批量版本切换(新/旧)");

		return ResultVO.OK();
	}

	/**
	 * 添加/更新 印章的配置信息
	 *
	 * @param uuid        设备UUID
	 * @param configIp    配置服务器HOST
	 * @param svrIp       业务服务器IP
	 * @param svrHost     SOCKET服务器HOST
	 * @param qssPin      量子pin码
	 * @param qssQkud     量子qkud码
	 * @param qssQssc     量子qssc码
	 * @param wifiSsid    wifi名称
	 * @param wifiPwd     WiFi密码
	 * @param firmwareVer 硬件版本号
	 * @param bodyId      章身ID
	 * @return 结果
	 */
	@RequestMapping("/addOrUpdateConfig")
	public ResultVO addOrUpdateConfig(@RequestParam String uuid,
									  @RequestParam String configIp,
									  @RequestParam String svrIp,
									  @RequestParam String svrHost,
									  @RequestParam Integer status,
									  @RequestParam(required = false) String qssPin,
									  @RequestParam(required = false) String qssQkud,
									  @RequestParam(required = false) String qssQssc,
									  @RequestParam(required = false) String wifiSsid,
									  @RequestParam(required = false) String wifiPwd,
									  @RequestParam(required = false) Double firmwareVer,
									  @RequestParam String bodyId) {
		//仅平台账户可用
		UserInfo userInfo = getUserInfo();
		if (userInfo.getType() != 0) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		/*章身ID不能重复*/
		Signet signet = signetService.getByUUID(uuid);
		List<Integer> deviceIds = signetService.getByBodyId(bodyId);
		if (deviceIds != null && !deviceIds.isEmpty() && !deviceIds.contains(signet.getId())) {
			return ResultVO.FAIL("章身ID重复");
		}

		/*更新设备'章身ID'*/
		if (signet != null && !StringUtils.equals(signet.getBodyId(), bodyId)) {
			signet.setBodyId(bodyId);
			signetService.update(signet);
		}

		if (StringUtils.isBlank(uuid)) {
			return ResultVO.FAIL("设备UUID不能为空");
		}
		if (StringUtils.isBlank(configIp)) {
			return ResultVO.FAIL("配置服务器IP不能为空");
		}
		if (StringUtils.isBlank(svrIp)) {
			return ResultVO.FAIL("业务服务器IP不能为空");
		}
		if (StringUtils.isBlank(svrHost)) {
			return ResultVO.FAIL("业务服务器HOST不能为空");
		}

		Config config = service.getByUUID(uuid);
		LocalHandle.setOldObj(config);

		Config orcConfig = new Config();
		BeanUtils.copyProperties(config, orcConfig);

		/*更新单片机文件hash值*/
		try {
			ConfigVersion cv = null;
			if (firmwareVer != null) {
				cv = configVersionService.getByVersion(firmwareVer + "", 2);
			}
			if (cv != null) {
				config.setFirmwareHash(cv.getHash());
				config.setFirmwareRemark(cv.getRemark());
				config.setFirmwareUrl(cv.getUrl());
				config.setFirmwareVer(firmwareVer);
			} else {
				config.setFirmwareVer(null);
				config.setFirmwareUrl(null);
				config.setFirmwareRemark(null);
				config.setFirmwareHash(null);
			}
		} catch (Exception e) {
			log.error("出现异常 ", e);
		}

		config.setConfigIp(configIp);
		config.setSvrIp(svrIp);
		config.setSvrHost(svrHost);
		config.setStatus(status);
		config.setWifiPwd(wifiPwd);
		config.setWifiSsid(wifiSsid);
		config.setQssPin(qssPin);
		config.setQssQkud(qssQkud);
		config.setQssQssc(qssQssc);

		config.setId(config.getId());
		config.setCreateDate(config.getCreateDate());
		service.update(config);

		/*新增迁移记录*/
		try {
			addMigrate(orcConfig, config);
		} catch (Exception e) {
			log.error("出现异常 ", e);
		}

		LocalHandle.setNewObj(config);
		LocalHandle.complete("更新印章配置");
		return ResultVO.OK("更新成功");
	}

	/**
	 * 批量更新 印章的配置信息
	 *
	 * @param uuidList    设备UUID列表，用逗号分隔
	 * @param configIp    配置服务器HOST
	 * @param svrIp       业务服务器IP
	 * @param svrHost     SOCKET服务器HOST
	 * @param qssPin      量子pin码
	 * @param qssQkud     量子qkud码
	 * @param qssQssc     量子qssc码
	 * @param wifiSsid    wifi名称
	 * @param wifiPwd     WiFi密码
	 * @param firmwareVer 硬件版本号
	 * @param bodyIdList  章身ID列表，用逗号分隔
	 * @return 结果
	 */
	@RequestMapping("/updateConfigBatch")
	public ResultVO updateConfigBatch(@RequestParam String uuidList,
									  @RequestParam(required = false) String configIp,
									  @RequestParam(required = false) String svrIp,
									  @RequestParam(required = false) String svrHost,
									  @RequestParam Integer status,
									  @RequestParam(required = false) String qssPin,
									  @RequestParam(required = false) String qssQkud,
									  @RequestParam(required = false) String qssQssc,
									  @RequestParam(required = false) String wifiSsid,
									  @RequestParam(required = false) String wifiPwd,
									  @RequestParam(required = false) Double firmwareVer,
									  @RequestParam String bodyIdList) {
		//仅平台账户可用
		UserInfo userInfo = getUserInfo();
		if (userInfo.getType() != 0) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		/*根据逗号拆分字符串*/
		List<ConfigVo> configVos = new ArrayList<>();

		String[] uuids = uuidList.split(",");
		if (uuids.length > 0) {
			for (int i = 0; i < uuids.length; i++) {
				if (configVos.size() <= i) {
					ConfigVo configVo = new ConfigVo();
					configVo.setUuid(uuids[i]);
					configVos.add(configVo);
				}
			}
		}

		String[] bodyIds = bodyIdList.split(",");
		if (bodyIds.length > 0) {
			for (int i = 0; i < bodyIds.length; i++) {
				if (configVos.size() <= i) {
					ConfigVo configVo = new ConfigVo();
					configVo.setBodyId(bodyIds[i]);
					configVos.add(configVo);
				} else {
					ConfigVo configVo = configVos.get(i);
					configVo.setBodyId(bodyIds[i]);
				}
			}
		}

		/*将数据转入List对象*/
		configVos.forEach(configVo -> {
			configVo.setConfigIp(configIp);
			configVo.setSvrIp(svrIp);
			configVo.setSvrHost(svrHost);
			configVo.setStatus(status);
			configVo.setQssPin(qssPin);
			configVo.setQssQssc(qssQssc);
			configVo.setQssQkud(qssQkud);
			configVo.setWifiSsid(wifiSsid);
			configVo.setWifiPwd(wifiPwd);
			configVo.setFirmwareVer(firmwareVer);
		});

		List<Signet> signets = new ArrayList<>();
		List<Config> updateConfigs = new ArrayList<>();
		List<Map<String, Object>> maps = new ArrayList<>();
		/*检查数据是否正确*/
		for (ConfigVo configVo : configVos) {
			/*章身ID不能重复*/
			Signet signet = signetService.getByUUID(configVo.getUuid());
			if (signet == null) {
				return ResultVO.FAIL("设备UUID:" + configVo.getUuid() + " 印章不存在");
			}
			List<Integer> deviceIds = signetService.getByBodyId(configVo.getBodyId());
			if (deviceIds != null && !deviceIds.isEmpty() && !deviceIds.contains(signet.getId())) {
				return ResultVO.FAIL("章身ID:" + configVo.getBodyId() + "重复");
			}

			/*更新设备'章身ID'*/
			if (!StringUtils.equals(signet.getBodyId(), configVo.getBodyId())) {
				signet.setBodyId(configVo.getBodyId());
				signets.add(signet);
			}

			if (StringUtils.isBlank(configVo.getUuid())) {
				return ResultVO.FAIL("设备UUID不能为空");
			}
			if (StringUtils.isBlank(configVo.getConfigIp())) {
				return ResultVO.FAIL("配置服务器IP不能为空");
			}
			if (StringUtils.isBlank(configVo.getSvrIp())) {
				return ResultVO.FAIL("业务服务器IP不能为空");
			}
			if (StringUtils.isBlank(configVo.getSvrHost())) {
				return ResultVO.FAIL("业务服务器HOST不能为空");
			}

			Config config = service.getByUUID(configVo.getUuid());
			if (config == null) {
				return ResultVO.FAIL("设备UUID:" + configVo.getUuid() + "的配置不存在");
			}
			LocalHandle.setOldObj(config);

			Config orcConfig = new Config();
			BeanUtils.copyProperties(config, orcConfig);

			/*更新单片机文件hash值*/
			try {
				ConfigVersion cv = null;
				if (configVo.getFirmwareVer() != null) {
					cv = configVersionService.getByVersion(configVo.getFirmwareVer() + "", 2);
				}
				if (cv != null) {
					config.setFirmwareHash(cv.getHash());
					config.setFirmwareRemark(cv.getRemark());
					config.setFirmwareUrl(cv.getUrl());
					config.setFirmwareVer(configVo.getFirmwareVer());
				} else {
					config.setFirmwareVer(null);
					config.setFirmwareUrl(null);
					config.setFirmwareRemark(null);
					config.setFirmwareHash(null);
				}
			} catch (Exception e) {
				log.error("出现异常 ", e);
			}

			config.setConfigIp(configVo.getConfigIp());
			config.setSvrIp(configVo.getSvrIp());
			config.setSvrHost(configVo.getSvrHost());
			config.setStatus(configVo.getStatus());
			config.setWifiPwd(configVo.getWifiPwd());
			config.setWifiSsid(configVo.getWifiSsid());
			config.setQssPin(configVo.getQssPin());
			config.setQssQkud(configVo.getQssQkud());
			config.setQssQssc(configVo.getQssQssc());
			config.setId(config.getId());

			/*保存对象用于插入数据库*/
			Map<String, Object> map = new HashMap<>();
			map.put("old", orcConfig);
			map.put("new", config);
			maps.add(map);
			updateConfigs.add(config);

			LocalHandle.setNewObj(config);
		}

		/*插入数据*/
		try {
			if (!signets.isEmpty()) {
				signetService.updateBatch(signets);
			}
			if (!maps.isEmpty()) {
				addMigrateBatch(maps);
			}
			if (!updateConfigs.isEmpty()) {
				service.updateBatch(updateConfigs);
			}
		} catch (Exception e) {
			log.error("出现异常 ", e);
		}

		/*添加日志*/
		Integer[] ids = new Integer[updateConfigs.size()];
		for (int i = 0; i < updateConfigs.size(); i++) {
			ids[i] = updateConfigs.get(i).getId();
		}
		LocalHandle.setbatchId(Arrays.toString(ids));
		LocalHandle.complete("批量更新印章配置");

		return ResultVO.OK("更新成功");
	}

	/**
	 * 检查是否迁移了服务器，如果迁移了，新增迁移记录
	 *
	 * @param srcConfig  原配置
	 * @param destConfig 新配置
	 */
	private void addMigrate(Config srcConfig, Config destConfig) {
		String svrHost = srcConfig.getSvrHost();
		String destHost = destConfig.getSvrHost();

		if (!StringUtils.equalsIgnoreCase(svrHost, destHost)) {
			Signet signet = signetService.getByUUID(srcConfig.getUuid());
			UserInfo info = getUserInfo();

			//新增迁移日志
			DeviceMigrateLog log = new DeviceMigrateLog();
			log.setDeviceId(signet.getId());
			log.setUuid(signet.getUuid());
			log.setUserId(info.getId());
			log.setNewOrgId(null);
			log.setOldOrgId(signet.getOrgId());
			log.setSrcHost(srcConfig.getSvrHost());
			log.setDestHost(destConfig.getSvrHost());

			log.setMigrateStatus(AppConstant.MIGRATE_UNKNOWN);
			deviceMigrateLogService.add(log);
		}
	}

	/**
	 * 批量检查是否迁移了服务器，如果迁移了，新增迁移记录
	 *
	 * @param mapList 原配置、新配置 列表
	 */
	private void addMigrateBatch(List<Map<String, Object>> mapList) {
		List<DeviceMigrateLog> logs = new ArrayList<>();
		for (Map<String, Object> map : mapList) {
			//原配置
			Config srcConfig = (Config) map.get("old");
			//新配置
			Config destConfig = (Config) map.get("new");

			String svrHost = srcConfig.getSvrHost();
			String destHost = destConfig.getSvrHost();

			if (!StringUtils.equalsIgnoreCase(svrHost, destHost)) {
				Signet signet = signetService.getByUUID(srcConfig.getUuid());
				UserInfo info = getUserInfo();

				//新增迁移日志
				DeviceMigrateLog log = new DeviceMigrateLog();
				log.setDeviceId(signet.getId());
				log.setUuid(signet.getUuid());

				log.setUserId(info.getId());

				log.setNewOrgId(null);
				log.setOldOrgId(signet.getOrgId());

				log.setSrcHost(srcConfig.getSvrHost());
				log.setDestHost(destConfig.getSvrHost());

				log.setMigrateStatus(AppConstant.MIGRATE_UNKNOWN);
				logs.add(log);

			}
		}
		if (!logs.isEmpty()) {
			deviceMigrateLogService.addBatch(logs);
		}
	}

	private String encordingChineseAndSpace(String str) {
		if (StringUtils.isNotBlank(str)) {
			try {
				str = str.replace(" ", "");
				str = URLEncoder.encode(str, "utf-8");
			} catch (Exception e) {
				log.error("出现异常 ", e);
			}
		}
		return str;
	}

	/**
	 * 查询指定uuid的日志信息
	 */
	@RequestMapping("/getLogByUUID")
	public ResultVO getLogByUUID(@RequestParam("uuid") String uuid) {
		if (StringUtils.isNotBlank(uuid)) {
			//仅平台账户可用
			UserInfo userInfo = getUserInfo();
			if (userInfo.getType() != 0) {
				return ResultVO.FAIL(Code.FAIL403);
			}

			boolean page = setPage();
			List<ConfigError> errors = configErrorService.getByUUID(uuid);
			for (ConfigError error : errors) {
				String host = error.getHost();
				if (StringUtils.isNotBlank(host) && !host.startsWith("http")) {
					if (StringUtils.contains(host, "qstamper")) {
						error.setHost("https://" + host);
					} else {
						error.setHost("http://" + host);
					}
				}
			}
			return ResultVO.Page(errors, page);
		}
		return ResultVO.FAIL(Code.FAIL402);
	}

	/**
	 * 设备日志上传
	 */
	@RequestMapping("/addConfigError")
	public ResultVO addConfigError(@RequestParam(required = false) String uuid, @RequestParam(required = false, defaultValue = "开机日志上传成功") String type, MultipartFile fileupload) {
		long filesize = 0;
		try {
			filesize = fileupload.getSize();
		} catch (Exception e) {
			log.info("日志文件异常\te:{}", e.getMessage());
		}
		log.info("--\t设备日志上传\tuuid:{}\ttype:{}\tfilesize:{}", uuid, type, filesize);

		HttpServletRequest request = SpringContextUtils.getRequest();
		if (request == null) {
			throw new RuntimeException();
		}
		Signet signet = null;
		if (StringUtils.isNotBlank(uuid)) {
			signet = signetService.getByUUID(uuid);
		}

		ConfigError configError = new ConfigError();
		//保存日志
		if (fileupload != null) {
			FileEntity entity = FileUtil.update(fileupload, "configLogs");
			if (entity != null) {
				configError.setFileName(entity.getFileName());
				configError.setAbsolutePath(entity.getAbsolutePath());
				configError.setRelativePath(entity.getRelativePath());
				configError.setError(type);
				configError.setHost(projectProperties.getFile().getHost());
				log.info("设备ID：{} 开机日志上传成功", signet == null ? 0 : signet.getId());
			} else {
				configError.setError("保存开机日志文件失败(filesize:" + filesize + ")");
				log.info("设备ID：{} 开机保存日志文件失败 filesize:{}", signet == null ? 0 : signet.getId(), filesize);
			}
		} else {
			configError.setError("无开机日志文件");
			log.info("设备ID：{} 无开机日志文件", signet == null ? 0 : signet.getId());
		}

		configError.setUuid(uuid);
		configErrorService.add(configError);
		return ResultVO.OK();
	}

	/**
	 * 查询指定uuid的设备配置信息(设备专用)
	 */
	@RequestMapping("/getConfigByUUID")
	@Transactional
	public ResultVO getConfigByUUID(@RequestParam(value = "uuid", required = false) String uuid) {
		if (StringUtils.isBlank(uuid)) {
			return ResultVO.FAIL("UUID不能为空");
		}
		Config config = service.getByUUID(uuid);
		Signet signet = null;
		if (config == null) {
			//查询默认配置
			config = service.getDefaultConfig();

			//查询印章是否存在
			signet = signetService.getByUUID(uuid);
			if (signet == null) {
				//创建新印章
				signet = new Signet();
				signet.setUuid(uuid);
				signet.setOrgId(projectProperties.getDefaultOrgId());
				signet.setCount(0);
				signet.setOrgName(getOrgName());
				signetService.add(signet);

				signet.setName("印章(新" + signet.getId() + ")");
				signetService.update(signet);
			}

			//创建新配置信息
			config.setId(null);
			config.setUuid(uuid);
			service.add(config);
		}

		Map<String, Object> configMap = new HashMap<>();

		//配置基本信息
		configMap.put("id", config.getId());
		configMap.put("uuid", config.getUuid());
		configMap.put("type", config.getType());
		configMap.put("status", config.getStatus());
		configMap.put("qssPin", config.getQssPin());
		configMap.put("qssQkud", config.getQssQkud());
		configMap.put("qssQssc", config.getQssQssc());
		configMap.put("wifiSsid", config.getWifiSsid());
		configMap.put("wifiPwd", config.getWifiPwd());
		configMap.put("configIp", config.getConfigIp());
		configMap.put("svrHost", config.getSvrHost());
		configMap.put("svrIp", config.getSvrIp());
		configMap.put("version", config.getVersion());
		configMap.put("apkName", config.getApkName());
		configMap.put("versionUrl", config.getVersionUrl());
		configMap.put("isOos", config.getIsOos());//是否使用天翼云 0:不适用天翼云 1:使用天翼云
		configMap.put("isEnableApplication", config.getIsEnableApplication());//是否启用申请单功能
		configMap.put("isProductionTest", config.getIsProductionTest());//是否产测版本

		//单片机版本
		configMap.put("firmwareVer", config.getFirmwareVer());
		configMap.put("firmwareUrl", config.getFirmwareUrl());
		configMap.put("firmwareRemark", config.getFirmwareRemark());
		configMap.put("firmwareHash", config.getFirmwareHash());

		//给印章拼接好 `获取配置服务器接口`，`更新APK接口`，`上传错误日志接口` 3个接口
		configMap.put("deviceConfigIp", config.getConfigIp() + "/device/config/getConfigByUUID");
		configMap.put("updateApkIp", config.getConfigIp() + "/device/config/updateAPK");
		configMap.put("errorLogIp", config.getConfigIp() + "/device/config/addConfigError");

		return ResultVO.OK(configMap);
	}

	/**
	 * 从缓存Redis中查询指定公司ID的组织编码
	 */
	private String getOrgName() {
		//查询该设备所属公司信息
		String orgName = null;
		Org org = orgService.get(projectProperties.getDefaultOrgId());
		if (org != null) {
			orgName = org.getName();
		}
		return orgName;
	}

	@RequestMapping("/getDefaultConfig")
	public ResultVO getDefaultConfig() {
		//仅平台账户可用
		UserInfo userInfo = getUserInfo();
		if (userInfo.getType() != 0) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		Config defaultConfig = service.getDefaultConfig();
		ConfigVo vo = new ConfigVo();
		BeanUtils.copyProperties(defaultConfig, vo);
		vo.setDeviceName("全局配置");
		vo.setDeviceUUID(defaultConfig.getUuid());
		return ResultVO.OK(vo);
	}

	/**
	 * 获取平台所有印章配置列表信息
	 */
	@RequestMapping("/getConfigList")
	public ResultVO getConfigList(@RequestParam(value = "keyword", required = false) String keyword) {
		//仅平台账户可用
		UserInfo userInfo = getUserInfo();
		if (userInfo.getType() != 0) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		//查询在线设备列表
		String prefix = RedisGlobal.PING;
		Set<String> keys = redisUtil.keys(prefix + "*");
		Set<String> deviceIds = new LinkedHashSet<>();
		if (keys != null && !keys.isEmpty()) {
			for (String deviceId : keys) {
				deviceIds.add(deviceId.replace(prefix, ""));
			}
		}

		boolean page = setPage();
		List<ConfigVo> vos = service.getByKeyword(keyword, deviceIds);
		if (vos != null && vos.size() > 0) {

			Config defaultConfig = service.getDefaultConfig();
			for (ConfigVo vo : vos) {
				vo.setUuid(vo.getDeviceUUID());
				if (vo.getId() == null) {
					//出厂日期
					String uuid = vo.getUuid();
					Signet signet = signetService.getByUUID(uuid);
					if (signet != null) {
						vo.setBirthdayTime(signet.getCreateDate());
					}

					//该设备没有配置,使用默认配置
					vo.setUseDefault(true);
					vo.setUpdateDate(defaultConfig.getUpdateDate());
					vo.setStatus(defaultConfig.getStatus());

					//量子相关
					vo.setQssPin(defaultConfig.getQssPin());
					vo.setQssQkud(defaultConfig.getQssQkud());
					vo.setQssQssc(defaultConfig.getQssQssc());

					//服务器相关
					vo.setConfigIp(defaultConfig.getConfigIp());
					vo.setSvrHost(defaultConfig.getSvrHost());
					vo.setSvrIp(defaultConfig.getSvrIp());

					//wifi相关
					vo.setWifiSsid(defaultConfig.getWifiSsid());
					vo.setWifiPwd(defaultConfig.getWifiPwd());

					//APK相关
					vo.setApkName(defaultConfig.getApkName());
					vo.setVersionUrl(defaultConfig.getVersionUrl());
					vo.setVersion(defaultConfig.getVersion());

					//单片机相关
					vo.setFirmwareRemark(defaultConfig.getFirmwareRemark());
					vo.setFirmwareUrl(defaultConfig.getFirmwareUrl());
					vo.setFirmwareHash(defaultConfig.getFirmwareHash());
					vo.setFirmwareVer(defaultConfig.getFirmwareVer());
				}

				//是否在线
				vo.setOnline(isOnline(vo.getDeviceId()) != null);
			}
		}
		return ResultVO.Page(vos, page);
	}
}
