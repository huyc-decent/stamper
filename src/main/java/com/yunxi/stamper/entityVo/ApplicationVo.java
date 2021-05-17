package com.yunxi.stamper.entityVo;

import com.yunxi.stamper.entity.Application;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/27 0027 16:07
 */
@Setter
@Getter
public class ApplicationVo extends Application {
	private String statusName;//状态值
	private Integer alreadyCount;//已使用的次数
	private List<Map<String, Object>> devices;//该申请单的印章列表{"id":17331,"deviceName":"印章17331"}

	public String getStatusName() {
		Integer status = getStatus();
		if (status != null) {
			switch (status.intValue()) {
				case 0:
					return "提交申请";
				case 1:
					return "审批中";
				case 2:
					return "审批通过";
				case 3:
					return "审批拒绝";
				case 4:
					return "授权中";
				case 5:
					return "授权通过";
				case 6:
					return "授权拒绝";
				case 7:
					return "已推送";
				case 8:
					return "用章中";
				case 9:
					return "已用章";
				case 10:
					return "待审计";
				case 11:
					return "审计通过";
				case 12:
					return "审计拒绝";
				case 13:
					return "已失效";
				default:
					return "未知";
			}
		}
		return "未知状态";
	}

}
