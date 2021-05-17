package com.yunxi.stamper.mapper;


import com.yunxi.stamper.entity.Attachment;
import com.yunxi.stamper.entityVo.AttachmentFile;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AttachmentMapper extends MyMapper<Attachment> {
	List<AttachmentFile> selectByApplication(Integer applicationId);
}