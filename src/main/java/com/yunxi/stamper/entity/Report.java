package com.yunxi.stamper.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

public class Report implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * 申请人id
	 */
	@Column(name = "user_id")
	private Integer userId;

	/**
	 * 公司id
	 */
	@Column(name = "org_id")
	private Integer orgId;

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	/**
	 * 报表状态 0:处理中 1:已完成 2:已下载 3:出现异常
	 */
	@Column(name = "`status`")
	private Integer status;

	/**
	 * 申请条件参数
	 */
	@Column(name = "`restrict`")
	private String restrict;

	/**
	 * 正常/异常信息
	 */
	private String error;

	/**
	 * 相对路径
	 */
	@Column(name = "relative_path")
	private String relativePath;

	@Column(name = "create_date")
	private Date createDate;

	@Column(name = "update_date")
	private Date updateDate;

	@Column(name = "delete_date")
	private Date deleteDate;

	private static final long serialVersionUID = 1L;

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
	 * 获取申请人id
	 *
	 * @return user_id - 申请人id
	 */
	public Integer getUserId() {
		return userId;
	}

	/**
	 * 设置申请人id
	 *
	 * @param userId 申请人id
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	/**
	 * 获取公司id
	 *
	 * @return org_id - 公司id
	 */
	public Integer getOrgId() {
		return orgId;
	}

	/**
	 * 设置公司id
	 *
	 * @param orgId 公司id
	 */
	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	/**
	 * 获取报表状态 0:处理中 1:已完成 2:已下载 3:出现异常
	 *
	 * @return status - 报表状态 0:处理中 1:已完成 2:已下载 3:出现异常
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * 设置报表状态 0:处理中 1:已完成 2:已下载 3:出现异常
	 *
	 * @param status 报表状态 0:处理中 1:已完成 2:已下载 3:出现异常
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * 获取申请条件参数
	 *
	 * @return restrict - 申请条件参数
	 */
	public String getRestrict() {
		return restrict;
	}

	/**
	 * 设置申请条件参数
	 *
	 * @param restrict 申请条件参数
	 */
	public void setRestrict(String restrict) {
		this.restrict = restrict;
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

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
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

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	/**
	 * 绝对路径
	 */
	@Column(name = "absolute_path")
	private String absolutePath;

	/**
	 * 文件名
	 */
	@Column(name = "file_name")
	private String fileName;

	private String host;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String toString() {
		String sb = "Report{" + "id=" + id +
				", userId=" + userId +
				", orgId=" + orgId +
				", status=" + status +
				", restrict='" + restrict + '\'' +
				", error='" + error + '\'' +
				", relativePath='" + relativePath + '\'' +
				", createDate=" + createDate +
				", updateDate=" + updateDate +
				", deleteDate=" + deleteDate +
				", absolutePath='" + absolutePath + '\'' +
				", fileName='" + fileName + '\'' +
				", host='" + host + '\'' +
				'}';
		return sb;
	}
}
