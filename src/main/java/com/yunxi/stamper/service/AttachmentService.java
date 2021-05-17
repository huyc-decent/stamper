package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.Attachment;
import com.yunxi.stamper.entityVo.AttachmentFile;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/5 0005 10:12
 */
public interface AttachmentService {
	void add(Attachment attachment);

	//查询申请单附件
	List<Attachment> getByApplication(Integer applicationId);

	List<AttachmentFile> getFileByApplication(Integer applicationId);

	void del(Attachment attachment);

	List<String> getFileIdsByApplicationId(Integer applicationId);
}
