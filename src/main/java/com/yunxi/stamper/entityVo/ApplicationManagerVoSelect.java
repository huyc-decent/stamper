package com.yunxi.stamper.entityVo;

import com.yunxi.stamper.entity.ApplicationManager;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/14 0014 17:55
 */
@Setter
@Getter
public class ApplicationManagerVoSelect extends ApplicationManager {
	private String statusName;//状态描述

	public String getStatusName() {
		//0:未审批 1:审批中 2:审批同意 3:审批拒绝 4:审批转交
		switch (this.getStatus()) {
			case 0:
				return "未审批";
			case 1:
				return "审批中";
			case 2:
				return "审批同意";
			case 3:
				return "审批拒绝";
			case 4:
				return "审批转交";
			default:
		}
		return "未知状态";
	}
}
