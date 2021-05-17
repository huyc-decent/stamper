package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.StamperPicture;
import com.yunxi.stamper.entityVo.FileEntity;
import com.yunxi.stamper.entityVo.PictureFileInfo;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/3 0003 14:19
 */
public interface StamperPictureService {
	void add(StamperPicture stamperPicture);

	//查看使用记录指定type的图片url列表
	List<FileEntity> getBySealRecordInfoAndType(Integer sealRecordInfoId, int type);

	StamperPicture getByDeviceAndFileName(Integer signetId, String fileName, Integer type, String hash);

	StamperPicture getByDeviceAndFileName(Integer signetId, String fileName);

	/**
	 * 图片记录列表
	 *
	 * @param infoIds 记录ID列表
	 * @return
	 */
	List<PictureFileInfo> getByInfoIds(List<Integer> infoIds);

	/**
	 * 查询使用记录指定类型的关联信息
	 *
	 * @param infoId 记录ID
	 * @param type   类型
	 * @return
	 */
	List<StamperPicture> getByInfoIdAndType(Integer infoId, int type);

}
