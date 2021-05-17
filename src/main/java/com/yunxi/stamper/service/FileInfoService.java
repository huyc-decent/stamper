package com.yunxi.stamper.service;


import com.yunxi.stamper.entity.FileInfo;
import com.yunxi.stamper.entity.Signet;
import com.yunxi.stamper.entity.StamperPicture;
import com.yunxi.stamper.entityVo.FileEntity;
import com.yunxi.stamper.entityVo.SealRecordInfoVoUpload;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/7/19 0019 15:09
 */
public interface FileInfoService {
	FileInfo get(String id);

	void add(FileInfo fileInfo);

	/**
	 * 查询指定时间段、指定类型的文件列表
	 *
	 * @param now     当前时间
	 * @param pre     开始时间
	 * @param scaling 文件类型
	 * @return
	 */
	List<FileInfo> getBetweenNowAndPreAndScaling(Date now, Date pre, int scaling);

	/**
	 * 更新数据
	 *
	 * @param fileInfo
	 */
	void update(FileInfo fileInfo);

	/**
	 * 根据hash值查询文件对象
	 *
	 * @param hash
	 * @return
	 */
	FileInfo getByHash(String hash);

	FileEntity getReduceImgURLByFileId(String fileId);

	/**
	 * 设备用印记录上传
	 *
	 * @param info       记录入参
	 * @param device     设备信息
	 * @param type       0:标准版 1:量子版 2:简易版  3:国科版
	 * @return
	 */
	StamperPicture saveFile(SealRecordInfoVoUpload info, Signet device, Integer type) throws Exception;
}
