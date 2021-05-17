package com.yunxi.stamper.commons.device.modelVo;

/**
 * @author zhf_10@163.com
 * @Description 设备迁移成功后返回实体类
 * @date 2020/3/19 0019 10:50
 */
public class DeviceMigrateCallback {
	private Integer taskId;	//迁移记录ID
	private Integer status;	//迁移状态 0:未知状态 1:迁移成功  2:迁移失败

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
