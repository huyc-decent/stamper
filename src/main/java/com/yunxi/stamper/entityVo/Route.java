package com.yunxi.stamper.entityVo;

import com.yunxi.stamper.entity.Perms;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description 路由实体类
 * @date 2019/5/27 0027 10:32
 */
public class Route extends Perms {
	private List<Route> childrens;//1级菜单下的2级菜单列表

	public List<Route> getChildrens() {
		return childrens;
	}

	public void setChildrens(List<Route> childrens) {
		this.childrens = childrens;
	}
}
