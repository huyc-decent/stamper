package com.yunxi.stamper.commons.device.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.yunxi.stamper.commons.device.modelVo.LoginApplication;
import lombok.Data;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/1/14 0014 17:18
 */
@Data
public class DeviceLoginInfo {
	@JSONField(name = "Stm32UUID")
	private String stm32UUID;

	@JSONField(name = "DeviceID")
	private int deviceID;

	@JSONField(name = "IMEI")
	private String iMEI;//imei　码

	@JSONField(name = "Tel")
	private String tel;//卡的手机号

	@JSONField(name = "ICCID")
	private String iCCID;//卡的iccid

	@JSONField(name = "IMSI")
	private String iMSI;//卡的imsi号

	@JSONField(name = "Addr")
	private String addr;//定位到的地址

	@JSONField(name = "AddrPoi")
	private String addrPoi;//其他可能的地点

	@JSONField(name = "UseCount")
	private int useCount;

	private Integer orgId;    //设备当前所在组织ID

	@JSONField(name = "UserID")
	private int userID;

	@JSONField(name = "UserName")
	private String userName;

	@JSONField(name = "SimNum")
	private String simNum;//电话卡

	@JSONField(name = "DeviceType")
	private int deviceType;//设备类型 0:印章 1:高拍仪

	@JSONField(name = "Latitude")
	private String latitude;//纬度

	@JSONField(name = "Longitude")
	private String longitude;//经度

	@JSONField(name = "LocationDescribe")
	private String locationDescribe;//位置描述

	@JSONField(name = "UsedStatus")
	private Integer usedStatus = 0;//开锁状态(开锁/关锁) 0:关锁中 1:已解锁

	private Integer ver = 0;//0:安卓3G 1:安卓3G量子 2:安卓4G 3:安卓4G量子 5:Linux-4G  6:单片机简易版

	private Boolean cameraPreviewStatus;//用户控制相机的预览功能是否使能 (1 用户使能预览,false 用户关闭相机预览）
	private Integer deviceMode;//使用模式 1 申请单模式、2指纹模式、3锁定模式、4装章模式 5密码 6OTA 7休眠 8产测
	private Integer deviceNetType;//网络状态(0 离线 1 3G/4G 2 WiFi)
	private Integer deviceSleepTimes;//休眠时间
	private Integer isEnableApplication;//1:启用申请单功能  2:关闭申请单功能
	private String sn;//序列号

	//对称加密密钥
	@JSONField(name = "SymmetricKey")
	private String symmetricKey;

	private List<LoginApplication> loginApplicationInfo;//印章最近1次申请单已使用记录

}
