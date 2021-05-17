package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.Signet;
import com.yunxi.stamper.entity.StamperPicture;
import com.yunxi.stamper.entityVo.SealRecordInfoVoUpload;

import javax.validation.constraints.NotNull;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/3 0003 13:26
 */
public interface SealRecordInfoAsyncService {
	//申请单模式 记录上传
	void addNormalInfo(SealRecordInfoVoUpload info, StamperPicture stamperPicture, Signet signet);

	//审计记录上传
	void addAuditInfo(SealRecordInfoVoUpload info, StamperPicture stamperPicture, Signet signet);

	//指纹模式 记录上传
	void addEasyInfo(SealRecordInfoVoUpload info, StamperPicture stamperPicture, Signet signet);

	//超过申请单次数 记录上传
	void addWarnExcessTimes(SealRecordInfoVoUpload info, StamperPicture stamperPicture, Signet signet);

	//防拆卸报警 记录上传
	void addWarnDemolish(SealRecordInfoVoUpload info, StamperPicture stamperPicture, Signet signet);

	//超时按压报警 记录上传
	void addWarnTimeoutPressing(SealRecordInfoVoUpload info, StamperPicture stamperPicture, Signet signet);

	//密码模式 记录上传
	void addPasswordInfo(SealRecordInfoVoUpload info, StamperPicture stamperPicture, Signet signet);

	//静默模式(摄像头关闭) 申请单盖章
	void addNoCameraInfo(SealRecordInfoVoUpload info, StamperPicture stamperPicture, Signet signet);

	//静默模式(摄像头关闭) 指纹盖章
	void addNoCameraInfoWithEasy(SealRecordInfoVoUpload info, Signet signet);
}
