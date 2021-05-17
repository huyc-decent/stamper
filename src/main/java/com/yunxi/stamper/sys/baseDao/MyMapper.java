package com.yunxi.stamper.sys.baseDao;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/2 0002 17:01
 */
public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {
}
