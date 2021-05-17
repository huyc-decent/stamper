package com.yunxi.stamper.commons.device.modelVo;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/7/23 0023 10:45
 */
public class DeviceInit {
	private Integer initCount;//印章迁移时,初始化次数值
	private String initOrgName;//印章迁移时,初始化LED显示屏右上角公司名称
	private Integer initOrgId;	//迁移的目标公司ID,设备需作为全局变量
	private Integer taskId;		//迁移记录ID，唯一键，设备迁移完成/取消后，需将该值返还给服务端

	private String oldOrgName;//原公司名称
	private String newOrgName;//新公司名称

	public Integer getInitOrgId() {
		return initOrgId;
	}

	public void setInitOrgId(Integer initOrgId) {
		this.initOrgId = initOrgId;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public String getOldOrgName() {
		return oldOrgName;
	}

	public void setOldOrgName(String oldOrgName) {
		this.oldOrgName = oldOrgName;
	}

	public String getNewOrgName() {
		return newOrgName;
	}

	public void setNewOrgName(String newOrgName) {
		this.newOrgName = newOrgName;
	}

	public String getInitOrgName() {
		return initOrgName;
	}

	public void setInitOrgName(String initOrgName) {
		this.initOrgName = initOrgName;
	}

	public Integer getInitCount() {
		return initCount;
	}

	public void setInitCount(Integer initCount) {
		this.initCount = initCount;
	}
}
