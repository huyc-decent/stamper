package com.yunxi.stamper.entityVo;


import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/11/27 0027 11:14
 */
@Setter
@Getter
public class InfoByApplication {
	private Integer id;//使用记录ID
	private String userName;//用印人姓名
	private Integer error;//警告状态
	private Integer useCount;//使用次数
	private String location;//用印地址

	private Integer version = 0; //0:老版本 1：新版本

	private String fileIds;//图片ID列表
	private String fileNames;//图片名称列表
	private String fileTypes;//图片状态列表  0:使用记录图片 1:审计图片 2:超出申请单次数图片 3:长按报警图片 4:拆卸警告图片
	private String fileCreates;//文件创建时间

	private List<FileEntity> infoList = new LinkedList<>();//使用记录列表
	private List<FileEntity> auditorList = new LinkedList<>();//审计记录列表
	private List<FileEntity> excessTimesList = new LinkedList<>();//超出申请单次数图片
	private List<FileEntity> overTimeList = new LinkedList<>();//超出申请单次数图片
	private List<FileEntity> replenishList = new LinkedList<>();//追加图片

}
