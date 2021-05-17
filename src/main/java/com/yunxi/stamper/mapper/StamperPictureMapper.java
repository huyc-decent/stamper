package com.yunxi.stamper.mapper;


import com.yunxi.stamper.entity.StamperPicture;
import com.yunxi.stamper.entityVo.PictureFileInfo;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface StamperPictureMapper extends MyMapper<StamperPicture> {
	StamperPicture selectByDeviceAndFileName(Integer signetId, String fileName, Integer type, String hash);

	/**
	 * 图片记录列表
	 * @param infoIds 记录ID列表
	 * @return
	 */
	List<PictureFileInfo> selectByInfoIds(List<Integer> infoIds);
}