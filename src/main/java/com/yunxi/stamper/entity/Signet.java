package com.yunxi.stamper.entity;

import com.yunxi.stamper.logger.anno.LogTag;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Data
@LogTag("印章")
//@XmlRootElement(name = "signet")
//@XmlAccessorType(XmlAccessType.FIELD)
public class Signet implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "create_date")
	private Date createDate;

	@Column(name = "update_date")
	private Date updateDate;

	@Column(name = "delete_date")
	private Date deleteDate;

	/**
	 * 迁移、移交客户的时间，在服务器内部组织之间迁移成功后，该值更新
	 */
	@Column(name = "transfer_time")
	private Date transferTime;

	/**
	 * 所属公司id
	 */
	@LogTag("集团公司ID")
	@Column(name = "org_id")
	private Integer orgId;

	@LogTag("集团公司名称")
	@Column(name = "org_name")
	private String orgName;

	/**
	 * 部门ID
	 */
	@LogTag("组织ID")
	@Column(name = "department_id")
	private Integer departmentId;

	@LogTag("组织名称")
	@Column(name = "department_name")
	private String departmentName;

	/**
	 * 高拍仪id
	 */
	@Column(name = "meter_id")
	@LogTag("高拍仪ID")
	private Integer meterId;

	/**
	 * 管章人ID(所属人)
	 */
	@LogTag("管章人ID")
	@Column(name = "keeper_id")
	private Integer keeperId;

	/**
	 * 管章人名称
	 */
	@LogTag("管章人名称")
	@Column(name = "keeper_name")
	private String keeperName;

	/**
	 * 审计人id
	 */
	@LogTag("审计人id")
	@Column(name = "auditor_id")
	private Integer auditorId;

	/**
	 * 审计人名称
	 */
	@LogTag("审计人名称")
	@Column(name = "auditor_name")
	private String auditorName;

	/**
	 * 设备类型id
	 */
	@LogTag("设备类型id")
	@Column(name = "type_id")
	private Integer typeId;

	/**
	 * 印章名称
	 */
	@LogTag("名称")
	private String name;

	/**
	 * 印章备注
	 */
	@LogTag("备注")
	private String remark;

	/**
	 * 印章使用次数
	 */
	@LogTag("使用次数")
	private Integer count;

	/**
	 * 印章图标
	 */
	@LogTag("LOGO")
	private String logo;

	/**
	 * 印章唯一码
	 */
	@LogTag("UUID")
	private String uuid;

	/**
	 * sim电话卡号码
	 */
	@LogTag("号码")
	@Column(name = "sim_num")
	private String simNum;

	/**
	 * 0:安卓3G 1:安卓3G量子 2:安卓4G 3:安卓4G量子 5:Linux-4G  6:单片机简易版
	 */
	@LogTag("版本类型")
	private Integer ver;

	/**
	 * 联通物联网卡信息
	 */
	@LogTag("号码")
	private String iccid;

	/**
	 * 章身ID
	 */
	@LogTag("章身ID")
	@Column(name = "body_id")
	private String bodyId;

	/**
	 * 联通物联网卡信息
	 */
	private String imsi;

	/**
	 * 印章所在坐标id
	 */
	@LogTag("坐标ID")
	private Integer addr;

	/**
	 * 印章状态: 0:正常 1:异常 2:销毁 3:停用 4:锁定
	 */
	@LogTag("状态")
	@Column(name = "`status`")
	private Integer status;

	/**
	 * 网络状态,json包
	 */
	@LogTag("网络")
	private String network;

	/**
	 * 印章休眠时间 2-10分钟
	 */
	@LogTag("休眠时间")
	@Column(name = "sleep_time")
	private Integer sleepTime;

	/**
	 * 是否开启指纹模式: 0：false 1：true
	 */
	@LogTag("指纹模式")
	@Column(name = "finger_pattern")
	private Boolean fingerPattern;

	/**
	 * 摄像头状态 0:开启  1：关闭
	 */
	@LogTag("摄像头开关")
	private Integer camera;

	/**
	 * 序列号
	 */
	@LogTag("序列号")
	@Column(name = "sn")
	private String sn;

	/**
	 * 是否启用申请单功能  1：开启(默认)   2：关闭
	 */
	@LogTag("是否启用申请单功能")
	@Column(name = "is_enable_application")
	private Integer isEnableApplication;

	private static final long serialVersionUID = 1L;
}
