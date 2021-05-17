package com.yunxi.stamper.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

public class Attachment implements Serializable {
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
	 * 申请单id
	 */
	@Column(name = "application_id")
	private Integer applicationId;

	@Column(name = "file_id")
	private String fileId;

	@Column(name = "bucket_name")
	private String bucketName;

	/**
	 * 新文件名
	 */
	@Column(name = "file_name")
	private String fileName;

	/**
	 * 天翼云文件真实名称
	 */
	private String name;

	@Column(name = "secret_key")
	private String secretKey;

	@Column(name = "verif_code")
	private String verifCode;

	/**
	 * 0是缩略图  1不是缩略图
	 */
	private Integer scaling;

	/**
	 * 附件类型 0:图片 1:文件
	 */
	@Column(name = "`status`")
	private Integer status;

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

	/**
	 * 获取申请单id
	 *
	 * @return application_id - 申请单id
	 */
	public Integer getApplicationId() {
		return applicationId;
	}

	/**
	 * 设置申请单id
	 *
	 * @param applicationId 申请单id
	 */
	public void setApplicationId(Integer applicationId) {
		this.applicationId = applicationId;
	}

	/**
	 * @return file_id
	 */
	public String getFileId() {
		return fileId;
	}

	/**
	 * @param fileId
	 */
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	/**
	 * @return bucket_name
	 */
	public String getBucketName() {
		return bucketName;
	}

	/**
	 * @param bucketName
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取新文件名
	 *
	 * @return file_name - 新文件名
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * 设置新文件名
	 *
	 * @param fileName 新文件名
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return secret_key
	 */
	public String getSecretKey() {
		return secretKey;
	}

	/**
	 * @param secretKey
	 */
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	/**
	 * 获取0是缩略图  1不是缩略图
	 *
	 * @return scaling - 0是缩略图  1不是缩略图
	 */
	public Integer getScaling() {
		return scaling;
	}

	/**
	 * 设置0是缩略图  1不是缩略图
	 *
	 * @param scaling 0是缩略图  1不是缩略图
	 */
	public void setScaling(Integer scaling) {
		this.scaling = scaling;
	}

	/**
	 * 获取附件类型 0:图片 1:文件
	 *
	 * @return status - 附件类型 0:图片 1:文件
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * 设置附件类型 0:图片 1:文件
	 *
	 * @param status 附件类型 0:图片 1:文件
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getVerifCode() {
		return verifCode;
	}

	public void setVerifCode(String verifCode) {
		this.verifCode = verifCode;
	}

	@Override
	public String toString() {
		String sb = "Attachment{" + "id=" + id +
				", createDate=" + createDate +
				", updateDate=" + updateDate +
				", deleteDate=" + deleteDate +
				", applicationId=" + applicationId +
				", fileId='" + fileId + '\'' +
				", bucketName='" + bucketName + '\'' +
				", fileName='" + fileName + '\'' +
				", name='" + name + '\'' +
				", secretKey='" + secretKey + '\'' +
				", scaling=" + scaling +
				", status=" + status +
				", verifCode=" + verifCode +
				'}';
		return sb;
	}
}
