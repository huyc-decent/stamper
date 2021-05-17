package com.yunxi.stamper.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@ToString
public class FileInfo implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String id;

	/**
	 * 上传源服务地址
	 */
	@Column(name = "upload_host")
	private String uploadHost;

	/**
	 * 原文件名
	 */
	@Column(name = "original_name")
	private String originalName;

	/**
	 * 新文件名
	 */
	@Column(name = "file_name")
	private String fileName;

	/**
	 * 文件大小
	 */
	private Long size;

	/**
	 * 本地存储相对路径
	 */
	@Column(name = "relative_path")
	private String relativePath;

	/**
	 * 本地存储绝对路径
	 */
	@Column(name = "absolute_path")
	private String absolutePath;

	/**
	 * 附件类型 0:图片 1:文件
	 */
	private Integer status;

	/**
	 * 0压缩  1未压缩
	 */
	private Integer scaling;

	/**
	 * 秘钥(国科秘钥)
	 */
	@Column(name = "secret_key")
	private String secretKey;

	/**
	 * 通道标识(设备UUID)
	 */
	@Column(name = "key_index")
	private String keyIndex;

	@Column(name = "create_date")
	private Date createDate;

	@Column(name = "update_date")
	private Date updateDate;

	@Column(name = "delete_date")
	private Date deleteDate;

	/**
	 * 文件hash值
	 */
	private String hash;

	private String host;

	private static final long serialVersionUID = 1L;

}