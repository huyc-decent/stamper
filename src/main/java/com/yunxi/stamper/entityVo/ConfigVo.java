package com.yunxi.stamper.entityVo;

import com.yunxi.stamper.entity.Config;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/6/28 0028 17:08
 */
@Setter
@Getter
public class ConfigVo extends Config {
	private String deviceName;
	private String deviceUUID;
	private Integer deviceId;
	private String iccid;
	private String bodyId;
	private String location;
	private String orgName;
	private Date birthdayTime;
	private Integer camera;//摄像头状态  0:开启  1:关闭

	private boolean useDefault = false;//true:使用默认配置 false:个性化配置
	private boolean online = false;//true:在线 false:不在线
}
