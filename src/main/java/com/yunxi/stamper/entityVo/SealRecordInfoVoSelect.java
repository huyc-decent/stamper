package com.yunxi.stamper.entityVo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/20 0020 16:38
 */
@Setter
@Getter
public class SealRecordInfoVoSelect {
	private int id;//使用记录id
	private Integer applicationId;//申请单id
	private String deviceName;//印章名称
	private String identity;//用印人名称
	private String title;//申请单标题
	private String content;//申请说明
	private String userName;//申请人名称
	private Date time;//用印时间
	private Integer count;//用印次数
	private Integer type;//使用方式 0:标准版 1:量子版 2:简易版
	private String location;//用印地址
	private Integer error;//使用记录状态 -1:异常 0:正常 1:警告
	private String errorName;//异常信息
	private String remark;//记录备注信息
	private List<FileEntity> applicationUrls;//申请图片
	private List<FileEntity> applicationAtts;//申请附件
	private List<FileEntity> useUrls;//盖章照片
	private List<FileEntity> auditorUrls;//审计照片
	private List<FileEntity> warnUrls;//拆卸图片
	private List<FileEntity> overTimesUrls;//超次图片
	private List<FileEntity> timeOutUrls;//超时照片
	private List<FileEntity> replenishUrls;//追加照片
	private Integer isOos = 0;//是否天翼云 0:非天翼云 1:是天翼云

	public void setApplicationUrls(List<FileEntity> applicationUrls) {
		if (applicationUrls != null && !applicationUrls.isEmpty()) {
			this.applicationUrls = applicationUrls;
		}
	}

	public void setApplicationAtts(List<FileEntity> applicationAtts) {
		if (applicationAtts != null && !applicationAtts.isEmpty()) {
			this.applicationAtts = applicationAtts;
		}
	}

	public void setUseUrls(List<FileEntity> useUrls) {
		if (useUrls != null && !useUrls.isEmpty()) {
			this.useUrls = useUrls;
		}
	}

	public void setAuditorUrls(List<FileEntity> auditorUrls) {
		if (auditorUrls != null && auditorUrls.size() > 0) {
			this.auditorUrls = auditorUrls;
		}
	}

	public void setWarnUrls(List<FileEntity> warnUrls) {
		if (warnUrls != null && warnUrls.size() > 0) {
			this.warnUrls = warnUrls;
		}
	}

	public void setOverTimesUrls(List<FileEntity> overTimesUrls) {
		if (overTimesUrls != null && overTimesUrls.size() > 0) {
			this.overTimesUrls = overTimesUrls;
		}
	}

	public void setTimeOutUrls(List<FileEntity> timeOutUrls) {
		if (timeOutUrls != null && timeOutUrls.size() > 0) {
			this.timeOutUrls = timeOutUrls;
		}
	}

	public Integer getIsOos() {
		return isOos;
	}

	public void setIsOos(Integer isOos) {
		this.isOos = isOos;
	}
}
