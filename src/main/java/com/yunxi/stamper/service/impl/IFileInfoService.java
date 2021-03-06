package com.yunxi.stamper.service.impl;

import com.yunxi.quantum.core.QuantumService;
import com.yunxi.quantum.dto.QuantumEntity;
import com.yunxi.stamper.base.BaseService;
import com.yunxi.stamper.commons.dir.DirFileUtils;
import com.yunxi.stamper.commons.fileUpload.FileUtil;
import com.yunxi.stamper.commons.gk.GKQSSUtil;
import com.yunxi.stamper.commons.gk.ResponseEntity;
import com.yunxi.stamper.commons.jwt.AES.AesUtil;
import com.yunxi.stamper.commons.md5.MD5;
import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.other.RedisGlobal;
import com.yunxi.stamper.commons.other.RedisUtil;
import com.yunxi.stamper.demoController.DemoEntity;
import com.yunxi.stamper.demoController.QssDemoEntity;
import com.yunxi.stamper.entity.*;
import com.yunxi.stamper.entityVo.FileEntity;
import com.yunxi.stamper.entityVo.SealRecordInfoVoUpload;
import com.yunxi.stamper.entityVo.UserInfo;
import com.yunxi.stamper.mapper.FileinfoMapper;
import com.yunxi.stamper.service.*;
import com.yunxi.stamper.sys.config.ProjectProperties;
import com.yunxi.stamper.sys.context.SpringContextUtils;
import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.sys.rabbitMq.MqSender;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Decoder;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/7/19 0019 15:09
 */
@Slf4j
@Service
public class IFileInfoService extends BaseService implements FileInfoService {
	@Autowired
	private FileinfoMapper mapper;
	@Autowired
	private ReduceFileInfoService reduceFileInfoService;
	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private ProjectProperties properties;
	@Autowired
	private QuantumService quantumService;
	@Autowired
	private OrgService orgService;
	@Autowired
	private ConfigService configService;
	@Autowired
	@Lazy
	private UserInfoService userInfoService;
	@Autowired
	private MqSender mqSender;

	@Override
	@Transactional
	public void add(FileInfo fileInfo) {
		int addCount = 0;
		if (fileInfo != null) {
			fileInfo.setId(UUID.randomUUID().toString().replace("-", ""));
			fileInfo.setCreateDate(new Date());
			if (StringUtils.isBlank(fileInfo.getHost())) {
				fileInfo.setHost(properties.getFile().getHost());
			}
			addCount = mapper.insert(fileInfo);
		}
		if (addCount != 1) {
			throw new PrintException("??????????????????");
		}
	}

	@Override
	public FileInfo get(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		Example example = new Example(FileInfo.class);
		example.createCriteria().andIsNull("deleteDate").andEqualTo("id", id);
		return mapper.selectOneByExample(example);
	}

	@Override
	@Transactional
	public void update(FileInfo fileInfo) {
		if (fileInfo == null || fileInfo.getId() == null) {
			return;
		}
		if (StringUtils.isBlank(fileInfo.getHost())) {
			fileInfo.setHost(properties.getFile().getHost());
		}
		mapper.updateByPrimaryKey(fileInfo);
	}

	/**
	 * ???????????????????????????????????????????????????
	 *
	 * @param now     ????????????
	 * @param pre     ????????????
	 * @param scaling ????????????
	 * @return ??????
	 */
	@Override
	public List<FileInfo> getBetweenNowAndPreAndScaling(Date now, Date pre, int scaling) {
		return mapper.getBetweenNowAndPreAndScaling(now, pre, scaling);
	}

	/**
	 * ??????hash?????????????????????
	 *
	 * @param hash ??????HASH
	 * @return ??????
	 */
	@Override
	public FileInfo getByHash(String hash) {
		if (StringUtils.isBlank(hash)) {
			return null;
		}
		return mapper.selectByHash(hash);
	}

	@Override
	public FileEntity getReduceImgURLByFileId(String fileId) {
		if (StringUtils.isBlank(fileId)) {
			return null;
		}

		FileEntity fileEntity = null;

		/*??????????????????????????????*/
		ReduceFileInfo reduceFileInfo = reduceFileInfoService.getByFileInfo(fileId);
		if (reduceFileInfo == null) {
			/*????????????*/
			FileInfo fileInfo = get(fileId);
			if (fileInfo != null) {
				fileEntity = new FileEntity(fileInfo);
			}
		} else {
			fileEntity = new FileEntity(reduceFileInfo);
		}

		/*
		 * ????????????????????????????????????????????????????????????
		 * ????????????????????????????????????keyindex???????????????????????????????????????????????????????????????????????????
		 */
		if (fileEntity != null && StringUtils.isNoneBlank(fileEntity.getSecretKey(), fileEntity.getKeyIndex())) {
			String secretKey = fileEntity.getSecretKey();
			String keyIndex = fileEntity.getKeyIndex();
			try {
				UserInfo userInfo = userInfoService.get(SpringContextUtils.getToken().getUserId());
				ResponseEntity responseEntity = GKQSSUtil.transCryption(keyIndex, userInfo.getPhone() + "", secretKey);
				if (responseEntity != null && StringUtils.isNotBlank(responseEntity.getValue())) {
					fileEntity.setSecretKey(responseEntity.getValue());
				}
			} catch (Exception e) {
				log.error("???????????? ", e);
			}
		}

		return fileEntity;
	}

	/**
	 * ????????????????????????
	 *
	 * @param info   ????????????
	 * @param device ????????????
	 * @param type   0:????????? 1:????????? 2:????????? 3:?????????
	 * @return ??????
	 */
	@Override
	public StamperPicture saveFile(SealRecordInfoVoUpload info, Signet device, Integer type) throws Exception {
		if (info == null) {
			return null;
		}

		int length = 0;
		Integer deviceId = null;
		Integer count = null;
		try {
			count = info.getCount();
			length = info.getFileupload().length();
			deviceId = device.getId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("--\t????????????\tdeviceId:{}\tcount:{}\ttype:{}\tfilesize:{}", deviceId, count, type, length);

		String fileName = info.getOrgId() + "_"
				+ info.getPicUseId() + "_"
				+ info.getCount() + "_"
				+ info.getType() + "_"
				+ info.getEncryptionType() + "_"
				+ System.nanoTime() + ".jpg";

		String filePath = CommonUtils.properties.getFile().getFilePath() + DirFileUtils.getFilePathV2(getOrgCode(device.getOrgId()));
		File file;

		String quantumTikets = null;
		/*????????????????????????*/
		String gk_key_index = null;
		if (type == 3) {
			String cryptograph = info.getFileupload();
			//????????????
//			try {
//				ResponseEntity responseEntity = GKQSSUtil.decrypt(cryptograph, device.getUuid());
//				log.info("-\t????????????\tresponseEntity:{}", JSONObject.toJSONString(responseEntity));
//			} catch (Exception e) {
//				log.info("x\t????????????");
//			}
			if (StringUtils.isBlank(cryptograph)) {
				return null;
			}
			file = FileUtil.uploadFile(filePath, fileName, cryptograph.getBytes());
			gk_key_index = device.getUuid();
		}

		/*????????????????????????*/
		else if (type == 1) {
			String cryptograph = info.getFileupload();
			if (StringUtils.isBlank(cryptograph)) {
				return null;
			}
			/*????????????*/
			QuantumEntity quantumEntity = quantumService.encrypt2(info.getSkt(), cryptograph);
			file = FileUtil.uploadFile(filePath, fileName, quantumEntity.getBytes());
			quantumTikets = quantumEntity.getTicket();
		}

		//AES????????????
		else {
			String cryptograph = info.getFileupload();
			String aesSecret = redisUtil.getStr(RedisGlobal.AES_KEY + deviceId);
			if (StringUtils.isBlank(cryptograph)) {
				return null;
			}
			String decrypt = AesUtil.decrypt(cryptograph, aesSecret);
			file = FileUtil.uploadFile(filePath, fileName, new BASE64Decoder().decodeBuffer(decrypt));
		}

		log.info("--\t????????????\tdeviceId:{}\tcount:{}\ttype:{}\tfilesize:{}\tfile:{}", deviceId, count, type, length, file != null);

		if (file == null || !file.exists()) {
			return null;
		}

		//???????????????????????????
		String hashCode = MD5.md5HashCode(file);
		FileInfo fileInfo = getByHash(hashCode);
		if (fileInfo == null) {
			//??????????????????
			fileInfo = new FileInfo();
			fileInfo.setFileName(fileName);
			fileInfo.setAbsolutePath(file.getAbsolutePath());
			fileInfo.setRelativePath(file.getAbsolutePath().substring(file.getAbsolutePath().indexOf(File.separator + "upload")).replace("\\", "/"));
			fileInfo.setScaling(1);
			fileInfo.setOriginalName(file.getName());
			fileInfo.setSize(file.length());
			fileInfo.setStatus(0);
			fileInfo.setHost(properties.getFile().getHost());
			fileInfo.setHash(hashCode);
			fileInfo.setSecretKey(info.getSkt());
			fileInfo.setKeyIndex(gk_key_index);
			add(fileInfo);
		} else {
			log.info("--\t????????????-??????hash\tdeviceId:{}\tcount:{}\ttype:{}\tfilesize:{}\tfile:{}", deviceId, count, type, length, file.getName());
		}

		/*?????????????????????????????????*/
		StamperPicture stamperPicture = new StamperPicture();
		stamperPicture.setSignetId(device.getId());
		stamperPicture.setFileName(fileInfo.getOriginalName());
		stamperPicture.setFileId(fileInfo.getId());
		stamperPicture.setType(info.getPictureType());
		stamperPicture.setStatus(0);
		stamperPicture.setHash(fileInfo.getHash());

		/*????????????????????????????????????*/
		if (type == 1) {
			sendQssDemo(info.getFileupload(), info.getSkt(), quantumTikets, device.getId(), CommonUtils.generatorURL(fileInfo.getHost(), fileInfo.getRelativePath()));
		}

		log.info("???\t????????????-??????\tdeviceId:{}\ttype:{}\tfilesize:{}\tfileId:{}", deviceId, type, length, fileInfo.getId());

		return stamperPicture;
	}

	/**
	 * ?????????Redis?????????????????????ID???????????????
	 */
	private String getOrgCode(Integer orgId) {
		String orgCode = null;
		Org org = orgService.get(orgId);
		if (org != null) {
			orgCode = org.getCode();
		}
		return orgCode;
	}

	/**
	 * ??????????????????UUID?????????
	 *
	 * @param uuid ??????UUID
	 * @return ??????
	 */
	private Float getVersionByUUID(String uuid) {
		Config config = configService.getByUUID(uuid);
		Float signetV = null;
		if (config != null && StringUtils.isNotBlank(config.getVersion())) {
			String version = config.getVersion();
			try {
				signetV = Float.valueOf(version);
			} catch (NumberFormatException e) {
				log.error("???????????? ", e);
			}
		}
		return signetV;
	}

	private void sendQssDemo(String cryptograph, String skt, String keys, Integer deviceId, String fileUrl) {
		//??????
		try {
			DemoEntity de = new DemoEntity();
			de.setDeviceId(deviceId);
			de.add(new QssDemoEntity("??????ID???" + deviceId));
			de.add(new QssDemoEntity("?????????????????????...."));
			cryptograph = cryptograph.replaceAll("\n", "").replaceAll("\r", "");
			de.add(new QssDemoEntity("????????????(??????):" + cryptograph.substring(0, cryptograph.length() > 400 ? 400 : cryptograph.length()) + "...(" + cryptograph.length() + "+)"));
			de.add(new QssDemoEntity("????????????:" + skt.substring(0, skt.length() > 50 ? 50 : skt.length()) + "...(" + skt.length() + "+)"));
			de.add(new QssDemoEntity("?????????????????????..."));
			de.add(new QssDemoEntity("????????????:" + keys));
			de.add(new QssDemoEntity("???????????????..."));
			de.add(new QssDemoEntity("????????????-->"));
			de.add(new QssDemoEntity("url", fileUrl));
			de.add(new QssDemoEntity("***************************?????????????????????*************************"));
			mqSender.sendToExchange(properties.getRabbitMq().getExchangeDemo(), de);
		} catch (Exception e) {
			log.error("?????????????????????????????????", e);
		}
	}
}
