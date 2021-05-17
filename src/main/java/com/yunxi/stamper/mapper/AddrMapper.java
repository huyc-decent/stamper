package com.yunxi.stamper.mapper;


import com.yunxi.stamper.entity.Addr;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import io.lettuce.core.dynamic.annotation.CommandNaming;
import org.springframework.stereotype.Component;

@Component
public interface AddrMapper extends MyMapper<Addr> {
}