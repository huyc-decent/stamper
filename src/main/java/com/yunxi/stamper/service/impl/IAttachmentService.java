package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.Attachment;
import com.yunxi.stamper.entityVo.AttachmentFile;
import com.yunxi.stamper.mapper.AttachmentMapper;
import com.yunxi.stamper.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/5 0005 10:12
 */
@Service
public class IAttachmentService implements AttachmentService {
	@Autowired
	private AttachmentMapper mapper;

	@Override
	@Transactional
	public void del(Attachment attachment) {
		int delCount = 0;
		if (attachment != null && attachment.getId() != null) {
			attachment.setDeleteDate(new Date());
			delCount = mapper.updateByPrimaryKey(attachment);
		}
		if (delCount != 1) {
			throw new PrintException("附件删除失败");
		}
	}

	/**
	 * 附件列表
	 * @param applicationId 申请单ID
	 * @return 结果
	 */
	@Override
	public List<AttachmentFile> getFileByApplication(Integer applicationId) {
		if (applicationId == null) {
			return null;
		}
		return mapper.selectByApplication(applicationId);
	}

	/**
	 * 附件列表
	 *
	 * @param applicationId 申请单ID
	 * @return 结果
	 */
	@Override
	public List<Attachment> getByApplication(Integer applicationId) {
		if (applicationId == null) {
			return null;
		}
		Example example = new Example(Attachment.class);
		example.createCriteria()
				.andEqualTo("applicationId", applicationId)
				.andIsNull("deleteDate");
		return mapper.selectByExample(example);
	}

	@Override
	@Transactional
	public void add(Attachment attachment) {
		int addCount = 0;
		if (attachment != null) {
			attachment.setCreateDate(new Date());
			addCount = mapper.insert(attachment);
		}
		if (addCount != 1) {
			throw new PrintException("附件添加失败");
		}
	}

	/**
	 * 附件ID列表
	 * @param applicationId 申请单ID
	 * @return 结果
	 */
	@Override
	public List<String> getFileIdsByApplicationId(Integer applicationId) {
		List<Attachment> attachments = getByApplication(applicationId);
		if (attachments == null || attachments.isEmpty()) {
			return null;
		}
		List<String> fileIds = new ArrayList<>();

		for (Attachment attachment : attachments) {
			String fileId = attachment.getFileId();
			fileIds.add(fileId);
		}

		return fileIds;
	}
}
