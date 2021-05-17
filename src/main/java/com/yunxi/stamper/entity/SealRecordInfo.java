package com.yunxi.stamper.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(name = "seal_record_info")
public class SealRecordInfo implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "create_date")
	private Date createDate;

	/**
	 * 设备id
	 */
	@Column(name = "device_id")
	private Integer deviceId;

	/**
	 * 印章名称
	 */
	@Column(name = "device_name")
	private String deviceName;

	/**
	 * 真实使用时间
	 */
	@Column(name = "real_time")
	private Date realTime;

	/**
	 * 所属公司id
	 */
	@Column(name = "org_id")
	private Integer orgId;

	/**
	 * 部门ID
	 */
	@Column(name = "depart_id")
	private Integer departId;

	/**
	 * 申请单ID
	 */
	@Column(name = "application_id")
	private Integer applicationId;

	/**
	 * 允许使用的次数(用户申请的次数)
	 */
	@Column(name = "access_count")
	private Integer accessCount;

	/**
	 * 当前使用次数
	 */
	@Column(name = "use_count")
	private Integer useCount;

	/**
	 * 用引人id
	 */
	@Column(name = "user_id")
	private Integer userId;

	/**
	 * 用印人名称
	 */
	@Column(name = "user_name")
	private String userName;

	/**
	 * 使用记录类型  0:申请单模式  1:申请单模式(量子)  2:指纹模式  3:指纹模式(量子)  4:密码模式
	 */
	private Integer type;

	/**
	 * 地址id
	 */
	private String location;

	/**
	 * 备注字段
	 */
	private String remark;

	/**
	 * skt量子解密码
	 */
	private String skt;

	/**
	 * 标记  0:该条记录是盖章上传创建的  1:该条记录是审计上传创建的
	 */
	@Column(name = "is_audit")
	private Integer isAudit;

	/**
	 * 标记异常/正常  -1:异常 0:正常 1:警告
	 */
	private Integer error;

	/**
	 * 是否存储天翼云 0:非天翼云 1:是天翼云
	 */
	@Column(name = "is_oos")
	private Integer isOos;

	@Column(name = "update_date")
	private Date updateDate;

	@Column(name = "delete_date")
	private Date deleteDate;

	private static final long serialVersionUID = 1L;
}
