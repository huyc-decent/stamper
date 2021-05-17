package com.yunxi.stamper.entityVo;

import com.yunxi.stamper.commons.other.DateUtil;
import com.yunxi.stamper.commons.other.Global;
import com.yunxi.stamper.entity.Application;
import com.yunxi.stamper.entity.ErrorType;
import com.yunxi.stamper.entity.SealRecordInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description 使用记录报表实体
 * @date 2020/8/27 10:08
 */
@Setter
@Getter
@ToString
public class SealRecordInfoReport {
	/***印章ID*/
	private Integer deviceId;
	/***印章名称*/
	private String deviceName;

	/***用印时间*/
	private String realTime;
	/***用印次数*/
	private Integer count;
	/***用印人*/
	private String userName;
	/***用印地址*/
	private String location;
	/***记录类型*/
	private String type;
	/***记录Id*/
	private Integer id;

	/***用印异常信息*/
	private String error;

	/***申请标题*/
	private String title;
	/***申请内容*/
	private String content;
	/***申请次数*/
	private Integer total;

	/***图片信息列表*/
	private List<ReportImageUrl> imageUrls = new ArrayList<>();

	/***申请人*/
	private String applicationUserName;
	/***审批人*/
	private List<String> managerUsernames;
	/***授权人*/
	private String keepername;
	/***审计人*/
	private String auditorname;

	public SealRecordInfoReport() {
	}

	/**
	 * 报表列表构造器
	 *
	 * @param sealRecordInfo 记录信息
	 * @param application    申请单信息
	 * @param errors         错误信息
	 * @param useUrls        用印图片
	 * @param auditorUrls    审计图片
	 * @param excessUrls     超次图片
	 * @param timeoutUrls    超时图片
	 * @param replenishUrls  追加图片
	 */
	public SealRecordInfoReport(SealRecordInfo sealRecordInfo, Application application, List<ErrorType> errors, List<FileEntity> useUrls, List<FileEntity> auditorUrls, List<FileEntity> excessUrls, List<FileEntity> timeoutUrls, List<FileEntity> replenishUrls) {
		/*印章相关*/
		this.id = sealRecordInfo.getId();
		this.deviceId = sealRecordInfo.getDeviceId();
		this.deviceName = sealRecordInfo.getDeviceName();

		/*记录相关*/
		this.realTime = DateUtil.format(sealRecordInfo.getRealTime());
		this.count = sealRecordInfo.getUseCount();
		this.userName = sealRecordInfo.getUserName();
		this.location = sealRecordInfo.getLocation();
		Integer type = sealRecordInfo.getType();
		switch (type) {
			case 0:
				this.type = "申请单模式";
				break;
			case 1:
				this.type = "申请单模式(量子)";
				break;
			case 2:
				this.type = "指纹模式";
				break;
			case 3:
				this.type = "指纹模式(量子)";
				break;
			case 4:
				this.type = "密码模式";
				break;
			default:
		}

		/*异常相关*/
		if (errors != null && !errors.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			errors.forEach(error -> {
				/*审计异常*/
				String name = error.getName();
				if (StringUtils.equals(name, Global.ERROR11)) {
					name = name + "(" + error.getRemark() + ")";
				}
				/*异常信息拼接一下*/
				sb.append(name).append(",");
			});
			String errMsg = sb.toString();
			this.error = errMsg.endsWith(",") ? errMsg.substring(0, errMsg.length() - 1) : errMsg;
		}

		//申请单相关
		this.title = application == null ? null : application.getTitle();
		this.total = application == null ? null : application.getUserCount();
		this.content = application == null ? null : application.getContent();

		//用印图片
		if (useUrls != null && !useUrls.isEmpty()) {
			useUrls.forEach(file -> {
				if (file != null) {
					String fileUrl = file.getFileUrl();
					this.imageUrls.add(new ReportImageUrl(fileUrl, "用印图片"));
				}
			});
		}

		//审计图片
		if (auditorUrls != null && !auditorUrls.isEmpty()) {
			auditorUrls.forEach(file -> {
				if (file != null) {
					String fileUrl = file.getFileUrl();
					this.imageUrls.add(new ReportImageUrl(fileUrl, "审计图片"));
				}
			});
		}

		//超次图片
		if (excessUrls != null && !excessUrls.isEmpty()) {
			excessUrls.forEach(file -> {
				if (file != null) {
					String fileUrl = file.getFileUrl();
					this.imageUrls.add(new ReportImageUrl(fileUrl, "超次图片"));
				}
			});
		}

		//超时图片
		if (timeoutUrls != null && !timeoutUrls.isEmpty()) {
			timeoutUrls.forEach(file -> {
				if (file != null) {
					String fileUrl = file.getFileUrl();
					this.imageUrls.add(new ReportImageUrl(fileUrl, "超时图片"));
				}
			});
		}

		//追加图片
		if (replenishUrls != null && !replenishUrls.isEmpty()) {
			replenishUrls.forEach(file -> {
				String fileUrl = file.getFileUrl();
				this.imageUrls.add(new ReportImageUrl(fileUrl, "追加图片"));
			});
		}
	}
}
