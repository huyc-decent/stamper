package com.yunxi.stamper.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "app_version")
public class AppVersion implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * 版本号
	 */
	private String version;

	/**
	 * 版本描述
	 */
	private String remark;

	/**
	 * 当前版本安卓下载路径
	 */
	private String android;

	/**
	 * 当前版本ios下载路径
	 */
	private String ios;

	/**
	 * 补充文件大小，用于下载更新时，进度条处理
	 */
	private Long fileSize;

	@Column(name = "create_date")
	private Date createDate;

	@Column(name = "update_date")
	private Date updateDate;

	@Column(name = "delete_date")
	private Date deleteDate;

	private static final long serialVersionUID = 1L;

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	/**
	 * @return id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * 获取版本号
	 *
	 * @return version - 版本号
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * 设置版本号
	 *
	 * @param version 版本号
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * 获取版本描述
	 *
	 * @return remark - 版本描述
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * 设置版本描述
	 *
	 * @param remark 版本描述
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * 获取当前版本安卓下载路径
	 *
	 * @return android - 当前版本安卓下载路径
	 */
	public String getAndroid() {
		return android;
	}

	/**
	 * 设置当前版本安卓下载路径
	 *
	 * @param android 当前版本安卓下载路径
	 */
	public void setAndroid(String android) {
		this.android = android;
	}

	/**
	 * 获取当前版本ios下载路径
	 *
	 * @return ios - 当前版本ios下载路径
	 */
	public String getIos() {
		return ios;
	}

	/**
	 * 设置当前版本ios下载路径
	 *
	 * @param ios 当前版本ios下载路径
	 */
	public void setIos(String ios) {
		this.ios = ios;
	}

	/**
	 * @return create_date
	 */
	public Date getCreateDate() {
		return createDate;
	}

	/**
	 * @param createDate
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	/**
	 * @return update_date
	 */
	public Date getUpdateDate() {
		return updateDate;
	}

	/**
	 * @param updateDate
	 */
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	/**
	 * @return delete_date
	 */
	public Date getDeleteDate() {
		return deleteDate;
	}

	/**
	 * @param deleteDate
	 */
	public void setDeleteDate(Date deleteDate) {
		this.deleteDate = deleteDate;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" [");
		sb.append("Hash = ").append(hashCode());
		sb.append(", id=").append(id);
		sb.append(", version=").append(version);
		sb.append(", remark=").append(remark);
		sb.append(", android=").append(android);
		sb.append(", ios=").append(ios);
		sb.append(", createDate=").append(createDate);
		sb.append(", updateDate=").append(updateDate);
		sb.append(", deleteDate=").append(deleteDate);
		sb.append(", serialVersionUID=").append(serialVersionUID);
		sb.append("]");
		return sb.toString();
	}
}
