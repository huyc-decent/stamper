package com.yunxi.stamper.entityVo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author zhf_10@163.com
 * @Description 使用记录-图片相关 实体
 * @date 2020/7/9 14:01
 */
@Setter
@Getter
@ToString
public class PictureFileInfo {

	private Integer pictureId;		//stamper_picture表ID
	private Integer deviceId;		//印章ID
	private Integer infoId;			//记录ID
	private Integer type;			//记录类型 0:使用记录图片 1:审计图片 2:超出申请单次数图片 3:长按报警图片 4:拆卸警告图片

	private String fileId;			//文件ID
	private String host;			//文件所在服务器host
	private String fileName;		//文件名
	private Long size;				//文件大小
	private String relativePath;	//文件相对路径
	private String secretKey;		//文件解密国科秘钥
	private String keyIndex;		//文件解密国科KeyIndex
	private Integer fileType;		//文件类型 0:图片 1:文件

}
