package com.yunxi.stamper.entityVo;

import com.yunxi.stamper.entity.Application;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/14 0014 15:50
 */
@Slf4j
@Setter
@Getter
public class ApplicationVoSelect extends Application {
	private String statusName;//状态值
	private List<DeviceSelectVo> devices;//印章列表
	private List<ApplicationManagerVoSelect> applicationManagers;//审批流程
	private List<FileEntity> fileEntities;//申请单附件列表(新版)
	private List<UseCountVo> useCountVos;//印章列表使用次数
	private boolean isManager = false;//标记作用,该申请单是否当前用户审批 true:显示待处理  false:显示查看
	private boolean isKeeper = false;//标记作用,该申请单是否当前用户授权 true:显示待处理  false:显示查看
	private boolean isAuditor = false;//标记作用,该申请单是否当前用户审计 true:显示待处理  false:显示查看
	private List<String> deviceNames;//该申请单的印章名称列表

	//0:初始化提交 1:审批中 2:审批通过 3:审批拒绝  4:授权中 5:授权通过 6:授权拒绝 7:已推送  8:用章中 9:已用章 10:审计中 11:审计通过 12:审计拒绝 13:已失效
	public String getStatusName() {
		switch (this.getStatus()) {
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
		}
		return "未知状态";
	}

	public void setDeviceNames(Object deviceNames) {
		try {
			Object obj = deviceNames;
			if (obj != null) {
				if (obj instanceof String) {
					String[] split = ((String) obj).split(",");
					if (split != null && split.length > 0) {
						this.deviceNames = Arrays.asList(split);
					}
				}
			} else if (obj instanceof List) {
				this.deviceNames = (List) obj;
			}
		} catch (Exception e) {
			log.error("出现异常 ", e);
		}
	}

}
