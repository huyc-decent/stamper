package com.yunxi.stamper.entityVo;

import com.yunxi.stamper.entity.DeviceMigrateLog;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2020/3/20 0020 9:50
 */
@Setter
@Getter
public class MigrateVo extends DeviceMigrateLog {
	private String deviceName;    //印章名称
	private String userName; //操作人名称
	private String deviceUUID;	//印章UUID

	private String oldOrgName;    //原公司名称
	private String newOrgName;    //新公司名称

}
