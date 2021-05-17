package com.yunxi.stamper.mapper;

import com.yunxi.stamper.entity.Signet;
import com.yunxi.stamper.entityVo.Location;
import com.yunxi.stamper.entityVo.SignetDetail;
import com.yunxi.stamper.entityVo.SignetEntity;
import com.yunxi.stamper.entityVo.SignetVo;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public interface SignetMapper extends MyMapper<Signet> {
	/**
	 * 查询指定公司所有印章位置信息
	 *
	 * @param orgId 集团ID
	 * @return
	 */
	List<Location> selectLocationByOrg(Integer orgId);

	/**
	 * 搜索印章列表
	 *
	 * @param orgId         集团ID
	 * @param type          设备类型
	 * @param keeperName    管章人姓名
	 * @param deviceName    设备名称
	 * @param departmentIds 组织ID列表
	 * @param keeperId      管章人ID
	 * @return
	 */
	List<Map<String, Object>> selectByType(Integer orgId, Integer type, String keeperName, String deviceName, List<Integer> departmentIds, Integer keeperId);

	/**
	 * 查询搜索印章列表
	 *
	 * @param orgId         集团ID
	 * @param keyword       印章名称、ID关键词
	 * @param departmentIds 要查询的组织ID列表
	 * @return
	 */
	List<SignetEntity> selectByOwner(Integer orgId, Integer keeperId, String keyword, Collection<Integer> departmentIds);


	/**
	 * 查询印章信息列表
	 *
	 * @param orgId         集团ID
	 * @param keeperId      管章人ID
	 * @param keyword       关键词
	 * @param departmentIds 组织ID列表
	 * @param onlines       在线印章ID列表
	 * @return
	 */
	List<SignetEntity> selectByOwnerAndOnlie(Integer orgId, Integer keeperId, String keyword, Collection<Integer> departmentIds, Collection<String> onlines);

	/**
	 * 查询集团、指定组织列表下的印章列表信息
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 组织ID列表
	 * @return
	 */
	List<Signet> selectSignetByOrgAndDepartments(Integer orgId, List<Integer> departmentIds, Integer userId);

	/**
	 * 查询集团印章列表
	 *
	 * @param orgId 集团ID
	 * @return
	 */
	List<Signet> selectByOrg(Integer orgId);

	/**
	 * 查询印章总数量
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 组织ID列表
	 * @return
	 */
	int selectCountByOrgAndDepartment(Integer orgId, List<Integer> departmentIds);

	/**
	 * 查询印章ID列表
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 组织ID列表
	 * @return
	 */
	List<Integer> selectIdByOrgAndDepartment(Integer orgId, List<Integer> departmentIds);

	/**
	 * 批量更新印章
	 * @param signets  印章信息列表
	 * @return
	 */
    int updateBatch(List<Signet> signets);

	/**
	 * 查询设备列表(在线设备放在上面)
	 *
	 * @param orgId         租户Id
	 * @param keeperId      管章人Id
	 * @param departmentIds 组织Id
	 * @param onlineIds     在线设备Id
	 * @param keyword       关键词
	 * @return
	 */
	List<Signet> find(Integer orgId, Integer keeperId, List<Integer> departmentIds, List<String> onlineIds, String keyword);
}
