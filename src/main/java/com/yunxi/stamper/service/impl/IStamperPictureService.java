package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.other.Global;
import com.yunxi.stamper.entity.StamperPicture;
import com.yunxi.stamper.entityVo.FileEntity;
import com.yunxi.stamper.entityVo.PictureFileInfo;
import com.yunxi.stamper.mapper.StamperPictureMapper;
import com.yunxi.stamper.service.FileInfoService;
import com.yunxi.stamper.service.StamperPictureService;
import com.yunxi.stamper.sys.error.base.PrintException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/3 0003 14:19
 */
@Slf4j
@Service
public class IStamperPictureService implements StamperPictureService {
	@Autowired
	private StamperPictureMapper mapper;
	@Autowired
	private FileInfoService fileInfoService;

	/**
	 * 查看使用记录指定type的图片url列表
	 *
	 * @param sealRecordInfoId
	 * @param type             图片类型 0:使用记录图片 1:审计图片 2:超出申请单次数图片 3:长按报警图片 4:拆卸警告图片  5追加图片
	 * @return
	 */
	@Override
	public List<FileEntity> getBySealRecordInfoAndType(Integer sealRecordInfoId, int type) {
		if (sealRecordInfoId == null || type < Global.TYPE_NORMAL || type > Global.TYPE_REPLENISH) {
			return null;
		}

		Example example = new Example(StamperPicture.class);
		example.createCriteria().andIsNull("deleteDate")
				.andEqualTo("infoId", sealRecordInfoId)
				.andEqualTo("status", 0)
				.andEqualTo("type", type);

		List<StamperPicture> sps = mapper.selectByExample(example);
		if (sps == null || sps.isEmpty()) {
			return null;
		}
		List<FileEntity> res = new ArrayList<>();

		for (int i = 0; i < sps.size(); i++) {
			StamperPicture sp = sps.get(i);
			FileEntity entity = null;

			String fileId = sp.getFileId();
			String aesFileId = sp.getAesFileId();

			if (StringUtils.isNotBlank(fileId)) {
				/***旧版使用fileId字段标记图片信息*/
				entity = fileInfoService.getReduceImgURLByFileId(fileId);//根据原图ID，查询图片缩略图

			} else if (StringUtils.isNotBlank(aesFileId)) {
				/***新版使用aesFileId字段标记图片信息*/
				entity = fileInfoService.getReduceImgURLByFileId(aesFileId);//根据密文文件ID，查询图片缩略图

			} else {
				continue;
			}
			res.add(entity);
		}
		return res;
	}

	@Transactional
	public void update(StamperPicture st) {
		st.setUpdateDate(new Date());
		mapper.updateByPrimaryKey(st);
	}

	@Override
	@Transactional
	public void add(StamperPicture stamperPicture) {
		stamperPicture.setCreateDate(new Date());
		int insert = mapper.insert(stamperPicture);
		if (insert != 1) {
			throw new PrintException("图片记录关联信息增加失败\t" + CommonUtils.objToJson(stamperPicture));
		}
	}

	@Override
	public StamperPicture getByDeviceAndFileName(Integer signetId, String fileName, Integer type, String hash) {
		if (signetId == null || StringUtils.isBlank(fileName) || type == null) {
			return null;
		}
		return mapper.selectByDeviceAndFileName(signetId, fileName, type, hash);
	}

	@Override
	public StamperPicture getByDeviceAndFileName(Integer signetId, String fileName) {
		if (signetId == null || StringUtils.isBlank(fileName)) {
			return null;
		}
		Example example = new Example(StamperPicture.class);
		example.createCriteria().andIsNull("deleteDate")
				.andEqualTo("signetId", signetId)
				.andEqualTo("fileName", fileName);
		StamperPicture stamperPicture = null;
		try {
			stamperPicture = mapper.selectOneByExample(example);
		} catch (Exception e) {
			log.info("xx\t记录图片关联信息查询出错\tsignetId:{}\tfileName:{}\terror:{}", signetId, fileName, e.getMessage());
			e.printStackTrace();
		}
		return stamperPicture;
	}

	/**
	 * 图片记录列表
	 *
	 * @param infoIds 记录ID列表
	 * @return
	 */
	@Override
	public List<PictureFileInfo> getByInfoIds(List<Integer> infoIds) {
		if (infoIds == null || infoIds.isEmpty()) {
			return null;
		}
		return mapper.selectByInfoIds(infoIds);
	}

	/**
	 * 查询使用记录指定类型的关联信息
	 *
	 * @param infoId 记录ID
	 * @param type   类型
	 * @return
	 */
	@Override
	public List<StamperPicture> getByInfoIdAndType(Integer infoId, int type) {
		if (infoId != null) {
			return null;
		}
		Example example = new Example(StamperPicture.class);
		example.createCriteria().andIsNull("deleteDate")
				.andEqualTo("infoId", infoId)
				.andEqualTo("status", 0)
				.andEqualTo("type", type);
		return mapper.selectByExample(example);
	}
}
