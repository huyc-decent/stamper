package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.other.RedisGlobal;
import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.Department;
import com.yunxi.stamper.entity.Position;
import com.yunxi.stamper.mapper.PositionMapper;
import com.yunxi.stamper.service.DepartmentService;
import com.yunxi.stamper.service.PositionService;
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
 * @date 2019/11/25 0025 14:39
 */
@Slf4j
@Service
public class IPositionService extends BaseService implements PositionService {
	@Autowired
	private PositionMapper mapper;
	@Autowired
	private DepartmentService departmentService;

	@Override
	@Transactional
	public void add(Position position) {
		int addCount = 0;
		if (position != null) {
			position.setCreateDate(new Date());
			addCount = mapper.insert(position);
		}
		if (addCount != 1) {
			throw new PrintException("职称添加失败");
		}
	}


	@Override
	@Transactional
	public void del(Position position) {
		int delCount = 0;
		if (position != null && position.getId() != null) {
			position.setDeleteDate(new Date());
			delCount = mapper.updateByPrimaryKey(position);
		}
		if (delCount != 1) {
			throw new PrintException("职称删除失败");
		}
	}

	/**
	 * 查询指定组织、名称的职称
	 *
	 * @param orgId 组织ID
	 * @param name  职称名称
	 * @return
	 */
	@Override
	public Position getByOrgIdAndName(Integer orgId, String name) {
		if (orgId != null && StringUtils.isNotBlank(name)) {
			Example example = new Example(Position.class);
			example.createCriteria().andIsNull("deleteDate").andEqualTo("orgId", orgId).andEqualTo("name", name);
			return mapper.selectOneByExample(example);
		}
		return null;
	}

	/**
	 * 添加职称
	 *
	 * @param orgId    组织ID
	 * @param createAt 添加人ID
	 * @param name     职称名称
	 */
	@Override
	@Transactional
	public void addPosition(Integer orgId, Integer createAt, String name) {
		Position position = new Position();
		position.setOrgId(orgId);
		position.setCreateAt(createAt);
		position.setName(name);
		add(position);
	}

	/**
	 * 查询指定组织下的所有职称列表
	 *
	 * @param orgId 组织ID
	 * @return
	 */
	@Override
	public List<Position> getByOrgId(Integer orgId) {
		if (orgId != null) {
			Example example = new Example(Position.class);
			example.createCriteria().andIsNull("deleteDate").andEqualTo("orgId", orgId);
			return mapper.selectByExample(example);
		}
		return null;
	}

	/**
	 * 删除职称
	 *
	 * @param deleteAtOrgId 组织ID
	 * @param deleteAt      操作人ID
	 * @param positionId    职称ID
	 */
	@Override
	@Transactional
	public void delPosition(Integer deleteAtOrgId, Integer deleteAt, Integer positionId) {
		/**
		 * 删除职称
		 */
		Position position = get(positionId);
		position.setDeleteAt(deleteAt);
		del(position);

		/**
		 * 置空该职称关联的组织
		 */
		List<Department> departments = departmentService.getByOrgAndPosition(deleteAtOrgId, position.getId());
		if (departments == null || departments.isEmpty()) {
			return;
		}
		for (int i = 0; i < departments.size(); i++) {
			Department department = departments.get(i);
			department.setPositionId(null);
			departmentService.update(department);
		}
	}


	@Override
	public Position get(Integer positionId) {
		if (positionId != null) {
			Example example = new Example(Position.class);
			example.createCriteria().andIsNull("deleteDate").andEqualTo("id", positionId);
			return mapper.selectOneByExample(example);
		}
		return null;
	}

	/**
	 * 查询用户职称
	 *
	 * @param orgId  集团ID
	 * @param userId 员工ID
	 * @return
	 */
	@Override
	public String getPositionByOrgAndUser(Integer orgId, Integer userId) {
		if (orgId == null || userId == null) {
			return null;
		}

		/**
		 * 先查缓存
		 */
		String key = RedisGlobal.POSITION_BY_USER + userId;
		Object positionRedis = redisUtil.get(key);
		if (positionRedis != null) {
			return positionRedis.toString();
		}

		/**
		 * 查数据库
		 */
		List<Position> positions = mapper.selectPostionByOrgAndUser(orgId, userId);
		if (positions != null && positions.size() > 0) {
			Set<String> sb = new HashSet<>();
			for (int i = 0; i < positions.size(); i++) {
				Position position = positions.get(i);
				String name = position.getName();
				sb.add(name);
			}

			String positionNames = CommonUtils.listToString(sb);
			if (StringUtils.isNotBlank(positionNames)) {
				redisUtil.set(key, positionNames, RedisGlobal.POSITION_BY_USER_TIMEOUT);
			}
			return positionNames;
		}

		return null;
	}
}
