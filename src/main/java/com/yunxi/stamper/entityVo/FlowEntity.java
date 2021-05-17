package com.yunxi.stamper.entityVo;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/12/5 0005 10:46
 */
@Setter
@Getter
public class FlowEntity {
	private Integer id;//流程ID
	private String name;//流程名称
	private boolean isManager = false;//是否主管流程
	private String flowType;//流程类型

	public boolean isManager() {
		if (StringUtils.isNotBlank(flowType) && flowType.contains("manager")) {
			return true;
		}
		return isManager;
	}

}
