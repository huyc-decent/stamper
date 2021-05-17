package com.yunxi.stamper.entityVo;

import lombok.Data;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/12/19 0019 13:27
 */
@Data
public class StatusEntity {
	private String name;
	private Integer value;
	private Integer error;

	public StatusEntity() {
	}

	public StatusEntity(Integer error, Integer value) {
		this.error = error;
		this.value = value;
	}

	public String getName() {
		if (error != null) {
			switch (error.intValue()) {
				case -1:
					return "异常";
				case 0:
					return "正常";
				case 1:
					return "警告";
				default:
			}
		}
		return null;
	}
}
