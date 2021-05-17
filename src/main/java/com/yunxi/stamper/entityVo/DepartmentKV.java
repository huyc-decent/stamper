package com.yunxi.stamper.entityVo;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/12/11 0011 12:40
 */
@Setter
@Getter
public class DepartmentKV {
	private int id;//组织ID
	private String name;//组织名称
	private int type;//0：部门 1：公司 2：集团公司

	public DepartmentKV() {
	}

	public DepartmentKV(int id, int type) {
		this.id = id;
		this.type = type;
	}

	public DepartmentKV(int id, String name, int type) {
		this.id = id;
		this.name = name;
		this.type = type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof DepartmentKV)) {
			return false;
		}
		DepartmentKV that = (DepartmentKV) o;
		return id == that.id &&
				type == that.type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, type);
	}
}
