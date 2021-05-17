package com.yunxi.stamper.entityVo;

import com.yunxi.stamper.entity.SealRecordInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/6/13 0013 10:31
 */
@Setter
@Getter
@ToString
public class SealRecordInfoNextAndPre extends SealRecordInfo {
	private Integer next;
	private Integer pre;
	private String exception;//异常信息
	private List<FileEntity> useUrl;//盖章图片
	private List<FileEntity> auditorUrls;//审计照片
	private List<FileEntity> warnUrls;//警告照片
	private List<FileEntity> numUrls;//超次照片
	private List<FileEntity> replenishUrls;//追加照片

	public List<FileEntity> getUseUrl() {
		return useUrl == null || useUrl.isEmpty() ? null : useUrl;
	}

	public List<FileEntity> getAuditorUrls() {
		return auditorUrls == null || auditorUrls.isEmpty() ? null : auditorUrls;
	}

	public List<FileEntity> getWarnUrls() {
		return warnUrls == null || warnUrls.isEmpty() ? null : warnUrls;
	}

	public List<FileEntity> getNumUrls() {
		return numUrls == null || numUrls.isEmpty() ? null : numUrls;
	}

	public List<FileEntity> getReplenishUrls() {
		return replenishUrls == null || replenishUrls.isEmpty() ? null : replenishUrls;
	}
}
