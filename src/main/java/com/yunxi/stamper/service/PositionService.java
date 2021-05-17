package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.Position;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/11/29 0029 17:10
 */
public interface PositionService {

	Position get(Integer positionId);

	void del(Position position);

	void add(Position position);

	void delPosition(Integer deleteAtOrgId, Integer deleteAt, Integer positionId);

	void addPosition(Integer orgId, Integer createAt, String name);

	List<Position> getByOrgId(Integer orgId);

	Position getByOrgIdAndName(Integer orgId, String name);

	String getPositionByOrgAndUser(Integer orgId,Integer userId);
}
