package com.yunxi.stamper.mapper;


import com.yunxi.stamper.entity.Qss;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface QssMapper extends MyMapper<Qss> {
	List<String> selectByArray(Integer type);

}