package com.yunxi.stamper.commons.fileUpload;


import org.apache.commons.lang3.StringUtils;

/**
 * @author zhf_10@163.com
 * @Description 上传文件映射实体
 * @date 2019/1/31 0031 17:23
 */
public class FileEntity {
	private String uuid;//资源服务器文件ID
	//原文件名
	private String originalName;
	//新文件名
	private String fileName;
	//文件大小
	private Long size;
	//本地存储相对路径
	private String relativePath;
	//本地存储绝对路径
	private String absolutePath;
	private String hash;
	//是否缩略图
	private boolean scaling; //true:是缩略图  false:不是缩略图
	//附件状态 0:图片 1:文件
	private int isIMG = 0;
	private int error = 0;//上传状态值 0：成功 1：图片密文为空 2：秘钥为空 3：其他异常
	private String errorMsg;//上传描述

	/**
	 * 对象存储OSS需要的参数如下
	 */
	private String bucketName;//容器名称
	private Integer encryptionType;//加密类型
	private String secretKey;//秘钥

	public FileEntity(String bucketName, String fileName, Integer encryptionType, String secretKey) {
		this.fileName = fileName;
		this.bucketName = bucketName;
		this.encryptionType = encryptionType;
		this.secretKey = secretKey;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public Integer getEncryptionType() {
		return encryptionType;
	}

	public void setEncryptionType(Integer encryptionType) {
		this.encryptionType = encryptionType;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public FileEntity() {
	}

	public FileEntity(boolean scaling) {
		this.scaling = scaling;
	}

	public FileEntity(int error, String errorMsg) {
		this.error = error;
		this.errorMsg = errorMsg;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getIsIMG() {
		return isIMG;
	}

	public void setIsIMG(int isIMG) {
		this.isIMG = isIMG;
	}

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getRelativePath() {
		if (StringUtils.isNotBlank(relativePath)) {
			return relativePath.replaceAll("\\\\", "/");
		}
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

	public String getAbsolutePath() {
		if (StringUtils.isNotBlank(absolutePath)) {
			return absolutePath.replaceAll("\\\\", "/");
		}
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	public boolean isScaling() {
		return scaling;
	}

	public void setScaling(boolean scaling) {
		this.scaling = scaling;
	}
}
