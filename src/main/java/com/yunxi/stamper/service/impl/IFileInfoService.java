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
			throw new PrintException("文件上传失败");
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
	 * 查询指定时间段、指定类型的文件列表
	 *
	 * @param now     当前时间
	 * @param pre     开始时间
	 * @param scaling 文件类型
	 * @return 结果
	 */
	@Override
	public List<FileInfo> getBetweenNowAndPreAndScaling(Date now, Date pre, int scaling) {
		return mapper.getBetweenNowAndPreAndScaling(now, pre, scaling);
	}

	/**
	 * 根据hash值查询文件对象
	 *
	 * @param hash 文件HASH
	 * @return 结果
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

		/*先查询是否存在缩率图*/
		ReduceFileInfo reduceFileInfo = reduceFileInfoService.getByFileInfo(fileId);
		if (reduceFileInfo == null) {
			/*返回原图*/
			FileInfo fileInfo = get(fileId);
			if (fileInfo != null) {
				fileEntity = new FileEntity(fileInfo);
			}
		} else {
			fileEntity = new FileEntity(reduceFileInfo);
		}

		/*
		 * 检查文件是否（国科量子）使用记录的图片，
		 * 如果是的话，取出记录中的keyindex与加密秘钥，并调用国科量子服务转换为前端需要的秘钥
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
				log.error("出现异常 ", e);
			}
		}

		return fileEntity;
	}

	/**
	 * 设备用印记录上传
	 *
	 * @param info   记录入参
	 * @param device 设备信息
	 * @param type   0:标准版 1:量子版 2:简易版 3:国科版
	 * @return 结果
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
		log.info("--\t图片上传\tdeviceId:{}\tcount:{}\ttype:{}\tfilesize:{}", deviceId, count, type, length);

		String fileName = info.getOrgId() + "_"
				+ info.getPicUseId() + "_"
				+ info.getCount() + "_"
				+ info.getType() + "_"
				+ info.getEncryptionType() + "_"
				+ System.nanoTime() + ".jpg";

		String filePath = CommonUtils.properties.getFile().getFilePath() + DirFileUtils.getFilePathV2(getOrgCode(device.getOrgId()));
		File file;

		String quantumTikets = null;
		/*国科量子加密版本*/
		String gk_key_index = null;
		if (type == 3) {
			String cryptograph = info.getFileupload();
			//国科解密
//			try {
//				ResponseEntity responseEntity = GKQSSUtil.decrypt(cryptograph, device.getUuid());
//				log.info("-\t国科解密\tresponseEntity:{}", JSONObject.toJSONString(responseEntity));
//			} catch (Exception e) {
//				log.info("x\t国科解密");
//			}
			if (StringUtils.isBlank(cryptograph)) {
				return null;
			}
			file = FileUtil.uploadFile(filePath, fileName, cryptograph.getBytes());
			gk_key_index = device.getUuid();
		}

		/*国盾量子加密版本*/
		else if (type == 1) {
			String cryptograph = info.getFileupload();
			if (StringUtils.isBlank(cryptograph)) {
				return null;
			}
			/*量子解密*/
			QuantumEntity quantumEntity = quantumService.encrypt2(info.getSkt(), cryptograph);
			file = FileUtil.uploadFile(filePath, fileName, quantumEntity.getBytes());
			quantumTikets = quantumEntity.getTicket();
		}

		//AES加密版本
		else {
			String cryptograph = info.getFileupload();
			String aesSecret = redisUtil.getStr(RedisGlobal.AES_KEY + deviceId);
			if (StringUtils.isBlank(cryptograph)) {
				return null;
			}
			String decrypt = AesUtil.decrypt(cryptograph, aesSecret);
			file = FileUtil.uploadFile(filePath, fileName, new BASE64Decoder().decodeBuffer(decrypt));
		}

		log.info("--\t图片上传\tdeviceId:{}\tcount:{}\ttype:{}\tfilesize:{}\tfile:{}", deviceId, count, type, length, file != null);

		if (file == null || !file.exists()) {
			return null;
		}

		//检查文件是否已存在
		String hashCode = MD5.md5HashCode(file);
		FileInfo fileInfo = getByHash(hashCode);
		if (fileInfo == null) {
			//存储文件信息
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
			log.info("--\t图片上传-重复hash\tdeviceId:{}\tcount:{}\ttype:{}\tfilesize:{}\tfile:{}", deviceId, count, type, length, file.getName());
		}

		/*转换记录与图片关联对象*/
		StamperPicture stamperPicture = new StamperPicture();
		stamperPicture.setSignetId(device.getId());
		stamperPicture.setFileName(fileInfo.getOriginalName());
		stamperPicture.setFileId(fileInfo.getId());
		stamperPicture.setType(info.getPictureType());
		stamperPicture.setStatus(0);
		stamperPicture.setHash(fileInfo.getHash());

		/*下面代码块主要做演示作用*/
		if (type == 1) {
			sendQssDemo(info.getFileupload(), info.getSkt(), quantumTikets, device.getId(), CommonUtils.generatorURL(fileInfo.getHost(), fileInfo.getRelativePath()));
		}

		log.info("√\t图片上传-成功\tdeviceId:{}\ttype:{}\tfilesize:{}\tfileId:{}", deviceId, type, length, fileInfo.getId());

		return stamperPicture;
	}

	/**
	 * 从缓存Redis中查询指定公司ID的组织编码
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
	 * 查询指定设备UUID的版本
	 *
	 * @param uuid 设备UUID
	 * @return 结果
	 */
	private Float getVersionByUUID(String uuid) {
		Config config = configService.getByUUID(uuid);
		Float signetV = null;
		if (config != null && StringUtils.isNotBlank(config.getVersion())) {
			String version = config.getVersion();
			try {
				signetV = Float.valueOf(version);
			} catch (NumberFormatException e) {
				log.error("出现异常 ", e);
			}
		}
		return signetV;
	}

	private void sendQssDemo(String cryptograph, String skt, String keys, Integer deviceId, String fileUrl) {
		//密文
		try {
			DemoEntity de = new DemoEntity();
			de.setDeviceId(deviceId);
			de.add(new QssDemoEntity("设备ID：" + deviceId));
			de.add(new QssDemoEntity("量子网络传输中...."));
			cryptograph = cryptograph.replaceAll("\n", "").replaceAll("\r", "");
			de.add(new QssDemoEntity("传输数据(密文):" + cryptograph.substring(0, cryptograph.length() > 400 ? 400 : cryptograph.length()) + "...(" + cryptograph.length() + "+)"));
			de.add(new QssDemoEntity("加密票据:" + skt.substring(0, skt.length() > 50 ? 50 : skt.length()) + "...(" + skt.length() + "+)"));
			de.add(new QssDemoEntity("获取会话秘钥中..."));
			de.add(new QssDemoEntity("会话秘钥:" + keys));
			de.add(new QssDemoEntity("正在解密中..."));
			de.add(new QssDemoEntity("解密成功-->"));
			de.add(new QssDemoEntity("url", fileUrl));
			de.add(new QssDemoEntity("***************************本次传输已完结*************************"));
			mqSender.sendToExchange(properties.getRabbitMq().getExchangeDemo(), de);
		} catch (Exception e) {
			log.error("演示客户端发送消息出错", e);
		}
	}
}
