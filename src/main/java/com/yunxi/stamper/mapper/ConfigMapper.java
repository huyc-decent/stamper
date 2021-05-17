package com.yunxi.stamper.mapper;


import com.yunxi.stamper.entity.Config;
import com.yunxi.stamper.entityVo.ConfigVo;
import com.yunxi.stamper.entityVo.UserConfig;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public interface ConfigMapper extends MyMapper<Config> {

	List<ConfigVo> selectByKeyword(String keyword, Set<String> deviceIds);

	List<UserConfig> selectByOrgAndKeyword(Integer orgId, String keyword);

    int updateBatch(List<Config> updateConfigs);
}