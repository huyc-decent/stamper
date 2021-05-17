package com.yunxi.stamper.entityVo;

import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.other.DateUtil;
import com.yunxi.stamper.entity.FileInfo;
import com.yunxi.stamper.entity.ReduceFileInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/12/10 0010 12:40
 */
@Setter
@Getter
@ToString
public class FileEntity {
	private String fileId;//文件、图片ID
	private String name;//文件名称
	private String createDate;//文件创建时间
	private Long size;//文件大小
	private Integer type;//文件类型 0:图片 1:文件
	private String fileUrl;//文件访问路径
	private String secretKey;//国科秘钥
	private String keyIndex;//通道标识

	public FileEntity() {
	}

	public FileEntity(ReduceFileInfo fileInfo) {
		this.fileId = fileInfo.getId();
		this.name = fileInfo.getFileName();
		this.createDate = null;
		this.size = fileInfo.getSize();
		this.type = 0;
		this.fileUrl = CommonUtils.generatorURL(fileInfo.getHost(), fileInfo.getRelativePath());
	}

	public FileEntity(FileInfo fileInfo) {
		this.fileId = fileInfo.getId();
		this.name = fileInfo.getOriginalName();
		this.createDate = DateUtil.format(fileInfo.getCreateDate());
		this.size = fileInfo.getSize();
		this.type = fileInfo.getStatus();
		this.fileUrl = CommonUtils.generatorURL(fileInfo.getHost(), fileInfo.getRelativePath());
		this.secretKey = fileInfo.getSecretKey();
		this.keyIndex = fileInfo.getKeyIndex();
	}

	public FileEntity(String createDate, Long size, int type, String name, String fileId, String fileUrl) {
		this.fileId = fileId;
		this.name = name;
		this.createDate = createDate;
		this.size = size;
		this.type = type;
		this.fileUrl = fileUrl;
	}

	public FileEntity(PictureFileInfo pictureFileInfo) {
		this.fileId = pictureFileInfo.getFileId();
		this.name = pictureFileInfo.getFileName();
		this.size = pictureFileInfo.getSize();
		this.type = pictureFileInfo.getType();
		this.fileUrl = CommonUtils.generatorURL(pictureFileInfo.getHost(), pictureFileInfo.getRelativePath());
		this.secretKey = pictureFileInfo.getSecretKey();
		this.keyIndex = pictureFileInfo.getKeyIndex();
	}
}
