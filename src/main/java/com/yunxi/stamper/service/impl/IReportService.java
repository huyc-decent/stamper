package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.commons.other.PropertiesUtil;
import com.yunxi.stamper.commons.other.RedisGlobal;
import com.yunxi.stamper.sys.context.SpringContextUtils;
import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.commons.report.HistogramVo;
import com.yunxi.stamper.commons.report.MapVo;
import com.yunxi.stamper.commons.report.KeyValue;
import com.yunxi.stamper.entity.Report;
import com.yunxi.stamper.entityVo.ReportEntity;
import com.yunxi.stamper.entityVo.StatusEntity;
import com.yunxi.stamper.entityVo.UserInfo;
import com.yunxi.stamper.mapper.ReportMapper;
import com.yunxi.stamper.service.*;
import com.yunxi.stamper.base.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/6/25 0025 17:05
 */
@Slf4j
@Service
public class IReportService extends BaseService implements ReportService {
	@Autowired
	private ReportMapper mapper;
	@Autowired
	private ApplicationService applicationService;
	@Autowired
	private SealRecordInfoService sealRecordInfoService;
	@Autowired
	private SignetService signetService;
	@Autowired
	private DepartmentService departmentService;

	private static final PropertiesUtil prop = new PropertiesUtil("cityMap.properties");

	@Override
	public Report get(Integer id) {
		Example example = new Example(Report.class);
		example.createCriteria().andEqualTo("id", id)
				.andIsNull("deleteDate");
		return mapper.selectOneByExample(example);
	}

	@Override
	@Transactional
	public void del(Report report) {
		int delCount = 0;
		if (report != null && report.getId() != null) {
			report.setDeleteDate(new Date());
			delCount = mapper.updateByPrimaryKey(report);
		}
		if (delCount != 1) {
			throw new PrintException("报表删除失败");
		}
	}

	@Override
	public List<Report> getByUser(Integer userId) {
		Example example = new Example(Report.class);
		example.createCriteria().andEqualTo("userId", userId)
				.andIsNull("deleteDate");
		example.orderBy("createDate").desc();
		return mapper.selectByExample(example);
	}

	@Override
	@Transactional
	public void update(Report report) {
		int updateCount = 0;
		if (report != null && report.getId() != null) {
			report.setUpdateDate(new Date());
			updateCount = mapper.updateByPrimaryKey(report);
		}
		if (updateCount != 1) {
			throw new PrintException("报表记录更新失败");
		}
	}

	@Override
	@Transactional
	public void add(Report report) {
		int addCount = 0;
		if (report != null) {
			report.setCreateDate(new Date());
			addCount = mapper.insert(report);
		}
		if (addCount != 1) {
			throw new PrintException("报表记录添加失败");
		}
	}

	/**
	 * 申请单数量、印章数量、使用次数 数据统计
	 *
	 * @param departmentIds 要查询的组织ID列表
	 * @return
	 */
	@Override
	public Map<String, Integer> getStatistics(Integer orgId, List<Integer> departmentIds) {
		if (orgId == null) {
			return null;
		}

		/**
		 * 查询申请单数量
		 */
		int applicaitonTotal = applicationService.getCountByDepartment(orgId, departmentIds);

		/**
		 * 查询印章数量
		 */
		int signetTotal = signetService.getCountByOrgAndDepartments(orgId, departmentIds);

		/**
		 * 查询使用总次数
		 */
		int infoTotal = sealRecordInfoService.getCountByOrgAndDepartments(orgId, departmentIds);

		Map<String, Integer> res = new HashMap<>();
		res.put("applicationTotal", applicaitonTotal);
		res.put("useTotal", infoTotal);
		res.put("deviceTotal", signetTotal);
		return res;
	}

	/**
	 * 查询地图数据
	 *
	 * @param userInfo     查询用户
	 * @param departmentId 要查询的组织ID
	 * @param province     省
	 * @param city         市
	 * @param start        开始时间
	 * @param end          结束时间
	 * @return
	 */
	@Override
	public Map<String, Object> getMapChart(UserInfo userInfo, Integer departmentId, String province, String city, Date start, Date end) {
		/**
		 * 解析参数：要搜索的组织ID列表
		 */
		List<Integer> departmentIds = null;
		if (departmentId != null) {
			departmentIds = departmentService.getChildrenIdsByOrgAndParentAndType(userInfo.getOrgId(), departmentId, null);
			departmentIds.add(departmentId);
		}

		List<MapVo> mapVos = null;//地图数据：各'详细地址'使用记录总数量、经纬度、印章名称
		List<KeyValue> KeyValues = null;//KV结构数据：各'省市区'使用记录总数量
		Map<String, Object> init = null;//页面初始化数据


		int searchNum = 0;//0:全国各省  1:全省各市 2:全市各区县

		if (StringUtils.isNotBlank(province) && province.endsWith("省")) {
			province = province.replace("省", "");
		}
		if (StringUtils.isNotBlank(city) && city.endsWith("市")) {
			city.replace("市", "");
		}

		/**
		 * 查询：全国数据
		 */
		if (StringUtils.isAllBlank(province, city)) {
			searchNum = 0;

			KeyValues = sealRecordInfoService.searchKvFromOrgAndDepartment(userInfo.getOrgId(), departmentIds, start, end, searchNum, null);

			init = initMap("china");

			if (KeyValues != null && KeyValues.size() > 0) {
				mapVos = sealRecordInfoService.searchTotalFromOrgAndDepartment(userInfo.getOrgId(), departmentIds, start, end, searchNum, null);
			}
		}

		/**
		 * 查询：全省数据
		 */
		else if (StringUtils.isNotBlank(province)) {
			searchNum = 1;
			KeyValues = sealRecordInfoService.searchKvFromOrgAndDepartment(userInfo.getOrgId(), departmentIds, start, end, searchNum, province);

			init = initMap(province);

			if (KeyValues != null && KeyValues.size() > 0) {
				mapVos = sealRecordInfoService.searchTotalFromOrgAndDepartment(userInfo.getOrgId(), departmentIds, start, end, searchNum, province);
			}
		}
		/**
		 * 查询：全市数据
		 */
		else if (StringUtils.isNotBlank(city)) {
			searchNum = 2;
			KeyValues = sealRecordInfoService.searchKvFromOrgAndDepartment(userInfo.getOrgId(), departmentIds, start, end, searchNum, city);

			init = initMap(city);

			if (KeyValues != null && KeyValues.size() > 0) {
				mapVos = sealRecordInfoService.searchTotalFromOrgAndDepartment(userInfo.getOrgId(), departmentIds, start, end, searchNum, city);
			}
		}

		/**
		 * 组装返回值参数
		 */
		Map<String, Object> res = new HashMap<>(3);
		res.put("mapVos", mapVos);
		res.put("init", init);
		res.put("data", KeyValues);

		return res;
	}

	/**
	 * 初始化指定省/市/区+对应编码
	 *
	 * @param addressName 省市区
	 * @return
	 */
	private Map<String, Object> initMap(String addressName) {
		Map<String, Object> params = new HashMap<>(2);
		params.put("addressName", addressName);
		params.put("initValue", getAddressValue(addressName));
		return params;
	}

	/**
	 * 查询指定地址的地理编码
	 *
	 * @param addressName 地址信息
	 * @return
	 */
	private Integer getAddressValue(String addressName) {
		Integer initValue = 100000;
		/**
		 * 地址为空，直接返回中国地理编码
		 */
		if (StringUtils.isBlank(addressName) || "china".equalsIgnoreCase(addressName)) {
			return initValue;
		}
		try {
			LinkedHashMap<String, String> locations = prop.getAll();
			String s = locations.get(addressName);
			if (StringUtils.isBlank(s)) {
				return initValue;
			} else {
				initValue = Integer.parseInt(s);
			}
		} catch (Exception e) {
			log.error("获取省市代码值出现错误-->addressName:" + addressName, e);
		}
		return initValue;
	}

	/**
	 * 查询柱状图数据
	 *
	 * @param userInfo     查询用户
	 * @param departmentId 要查询的组织ID
	 * @param start        开始时间
	 * @param end          结束时间
	 * @return
	 */
	@Override
	public Map<String, Object> getHistogram(UserInfo userInfo, Integer departmentId, Date start, Date end) {
		/**
		 * 解析参数：要搜索的组织ID列表
		 */
		List<Integer> departmentIds = null;
		if (departmentId != null) {
			departmentIds = departmentService.getChildrenIdsByOrgAndParentAndType(userInfo.getOrgId(), departmentId, null);
			departmentIds.add(departmentId);
		}

		List<HistogramVo> histogramVos = sealRecordInfoService.getHistogramByOrgAndDepartment(userInfo.getOrgId(), departmentIds, start, end);

		if (histogramVos == null || histogramVos.isEmpty()) {
			return null;
		}

		/**
		 * 组装返回值参数
		 */
		List<String> deviceNames = new ArrayList<>();
		List<Integer> totals = new ArrayList<>();

		for (int i = 0; i < histogramVos.size(); i++) {
			HistogramVo histogramVo = histogramVos.get(i);
			Integer deviceId = histogramVo.getDeviceId();
			Integer total = histogramVo.getTotal();
			deviceNames.add(deviceId + "");
			totals.add(total);
		}

		Map<String, Object> res = new HashMap<>();
		res.put("deviceNames", deviceNames);
		res.put("totals", totals);
		return res;
	}

	/**
	 * 查询饼状图数据
	 *
	 * @param userInfo     查询用户
	 * @param departmentId 要查询的组织ID
	 * @param start        开始时间
	 * @param end          结束时间
	 * @return
	 */
	@Override
	public List<StatusEntity> getErrorOrNormal(UserInfo userInfo, Integer departmentId, Date start, Date end) {
		/**
		 * 解析参数：要搜索的组织ID列表
		 */
		List<Integer> departmentIds = null;
		if (departmentId != null) {
			departmentIds = departmentService.getChildrenIdsByOrgAndParentAndType(userInfo.getOrgId(), departmentId, null);
			departmentIds.add(departmentId);
		}
//		List<Integer> visualDepartmentIds = userInfo.getVisualDepartmentIds();
//		if (departmentId != null) {
//			departmentIds = departmentService.getChildrenIdsByOrgAndParentAndType(userInfo.getOrgId(), departmentId, null);
//			departmentIds.add(departmentId);
//			for (int i = 0; i < departmentIds.size(); i++) {
//				Integer childrenId = departmentIds.get(i);
//				if (!visualDepartmentIds.contains(childrenId)) {
//					departmentIds.remove(i--);
//				}
//			}
//		} else {
//			departmentIds = visualDepartmentIds;
//		}

		return sealRecordInfoService.getPieChartByOrgAndDepartment(userInfo.getOrgId(), departmentIds, start, end);
	}

	/**
	 * 查询仪表盘数据
	 *
	 * @param userInfo     查询用户
	 * @param departmentId 要查询的组织ID
	 * @return
	 */
	@Override
	public Map<String, Integer> getgetTotalByOnline(UserInfo userInfo, Integer departmentId) {
		/**
		 * 解析参数：要搜索的组织ID列表
		 */
		List<Integer> departmentIds = null;
		if (departmentId != null) {
			departmentIds = departmentService.getChildrenIdsByOrgAndParentAndType(userInfo.getOrgId(), departmentId, null);
			departmentIds.add(departmentId);
		}

		/**
		 * 查询印章ID列表
		 */
		List<Integer> deviceIds = signetService.getIdByOrgAndDepartment(userInfo.getOrgId(), departmentIds);
		if (deviceIds == null || deviceIds.isEmpty()) {
			Map<String, Integer> res = new HashMap<>(2);
			res.put("online", 0);
			res.put("total", 0);
			return res;
		}

		/**
		 * 查询在线设备列表
		 */
		Set<String> onlines = new LinkedHashSet<>();
		Set<String> keys = redisUtil.keys(RedisGlobal.PING + "*");
		if (keys != null && !keys.isEmpty()) {
			for (String deviceId : keys) {
				onlines.add(deviceId.replace(RedisGlobal.PING, ""));
			}
		}

		/**
		 * 组装返回值参数
		 */
		int total = deviceIds.size();
		int online = 0;
		if (!onlines.isEmpty()) {
			for (Integer deviceId : deviceIds) {
				if (onlines.contains(deviceId + "")) {
					online++;
				}
			}
		}

		Map<String, Integer> res = new HashMap<>(2);
		res.put("online", online);
		res.put("total", total);

		return res;
	}

	/**
	 * 查询用户的报表记录
	 *
	 * @param userInfoId 用户ID
	 * @return
	 */
	@Override
	public List<ReportEntity> getReportEntitiesByUser(Integer userInfoId) {
		if (userInfoId == null) {
			return null;
		}
		SpringContextUtils.setPage();
		return mapper.selectReportEntitiesByUser(userInfoId);
	}
}
