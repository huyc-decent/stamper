package com.yunxi.stamper.entityVo;

import com.yunxi.stamper.entity.Department;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @author zhf_10@163.com
 * @Description Department表字段parentCode实体类
 * @date 2019/11/29 0029 17:21
 */
@Setter
@Getter
public class ParentCode {
	private int id;//组织ID
	private int level;//组织层级 0:根级(无父级) 1:第1级

	public ParentCode() {
	}

	public ParentCode(int id, int level) {
		this.id = id;
		this.level = level;
	}

	public ParentCode(Department department) {
		this.id = department.getId();
		this.level = department.getLevel();
	}

	@Override
	public String toString() {
		return "ParentCode{" + "id=" + id +
				", level=" + level +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ParentCode)) {
			return false;
		}
		ParentCode that = (ParentCode) o;
		return id == that.id &&
				level == that.level;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, level);
	}
}
