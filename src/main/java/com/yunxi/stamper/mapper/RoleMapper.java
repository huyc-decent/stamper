package com.yunxi.stamper.mapper;

import com.yunxi.stamper.entity.Role;
import com.yunxi.stamper.entityVo.RoleEntity;
import com.yunxi.stamper.entityVo.RoleVo;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface RoleMapper extends MyMapper<Role> {
	//查询用户拥有的角色id列表
	List<Integer> selectByUserId(Integer userId);

	//查询用户拥有的角色列表
	List<Role> selectByUser(Integer userId);

	//查询所有角色id列表
	List<Integer> selectAllIds();

	//查询公司拥有的角色列表(包含创建人+更新人)
	List<RoleVo> selectVoByOrg(Integer orgId);

	/**
	 * 查询角色列表
	 * @param keywords 搜索关键词
	 * @param orgId 集团ID
	 * @return
	 */
	List<RoleEntity> selectByKeywordAndOrg(String keywords, Integer orgId);
}