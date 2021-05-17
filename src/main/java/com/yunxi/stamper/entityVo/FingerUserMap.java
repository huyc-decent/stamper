package com.yunxi.stamper.entityVo;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/12/11 0011 21:30
 */
@Setter
@Getter
public class FingerUserMap {
	private List<Integer> userIds = new ArrayList<>();//指纹用户ID列表
	private Map<Integer,Date> userMap = new HashMap<>();//用户ID:录入时间

}
