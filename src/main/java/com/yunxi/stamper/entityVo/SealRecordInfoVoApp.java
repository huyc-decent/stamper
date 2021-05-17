package com.yunxi.stamper.entityVo;


import com.yunxi.stamper.entity.SealRecordInfo;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/6/26 0026 22:12
 */
@Data
public class SealRecordInfoVoApp extends SealRecordInfo {
	private List<FileEntity> useUrls;//盖章照片
	private List<FileEntity> auditorUrls;//审计照片
	private List<FileEntity> warnUrls;//警告照片
	private List<FileEntity> replenishList;//追加图片

	private String errorMsg;//异常信息

	public void setUseUrls(List<FileEntity> useUrls) {
		if (useUrls != null && useUrls.size() > 0) {
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


	public void setReplenishList(List<FileEntity> replenishList) {
		if (replenishList != null && replenishList.size() > 0) {
			this.replenishList = replenishList;
		}
	}
}
