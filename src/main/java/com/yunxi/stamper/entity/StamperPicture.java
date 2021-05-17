package com.yunxi.stamper.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(name = "stamper_picture")
public class StamperPicture implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * 印章id
	 */
	@Column(name = "signet_id")
	private Integer signetId;

	/**
	 * 所属使用记录id
	 */
	@Column(name = "info_id")
	private Integer infoId;

	@Column(name = "file_id")
	private String fileId;

	@Column(name = "aes_file_id")
	private String aesFileId;

	/**
	 * 本地文件名(天翼云文件名称)
	 */
	@Column(name = "file_name")
	private String fileName;

	private String hash;

	/**
	 * 文件临时访问路径
	 */
	@Column(name = "file_url")
	private String fileUrl;

	/**
	 * 对象存储容器名称
	 */
	@Column(name = "bucket_name")
	private String bucketName;

	/**
	 * 加密类型 0:普通文件 1:对称加密文件 2:非对称加密文件  3:量子加密文件
	 */
	@Column(name = "encryption_type")
	private Integer encryptionType;

	/**
	 * 对称解密秘钥(量子加密票据)
	 */
	@Column(name = "secret_key")
	private String secretKey;

	/**
	 * 状态: 0:正常图片 1:缩略图
	 */
	private Integer status;

	/**
	 * 图片类型 0:使用记录图片 1:审计图片 2:超出申请单次数图片 3:长按报警图片 4:拆卸警告图片 5:追加图片
	 */
	private Integer type;

	/**
	 * fileUrl过期时间
	 */
	@Column(name = "file_url_expire_date")
	private Date fileUrlExpireDate;

	@Column(name = "create_date")
	private Date createDate;

	/**
	 * 该字段仅作为追加类型记录时，记录追加人id使用
	 */
	@Column(name = "create_by")
	private Integer createBy;

	@Column(name = "update_date")
	private Date updateDate;

	@Column(name = "delete_date")
	private Date deleteDate;

	private static final long serialVersionUID = 1L;
}
