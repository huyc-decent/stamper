package com.yunxi.stamper.service.async;

import com.alibaba.fastjson.JSONObject;
import com.yunxi.stamper.commons.other.*;
import com.yunxi.stamper.entity.*;
import com.yunxi.stamper.entityVo.SealRecordInfoVoUpload;
import com.yunxi.stamper.service.*;
import com.yunxi.stamper.sys.lock.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/3 0003 13:26
 */
@Slf4j
@Service
public class ISealRecordInfoAsyncService implements SealRecordInfoAsyncService {

	@Autowired
	private StamperPictureService stamperPictureService;
	@Autowired
	private SealRecordInfoService sealRecordInfoService;
	@Autowired
	private ErrorTypeService errorTypeService;
	@Autowired
	private SignetService signetService;
	@Autowired
	protected RedisUtil redisUtil;
	@Autowired
	private UserService userService;
	@Autowired
	private MessageTempService messageTempService;
	@Autowired
	private DisassemblyRecordInfoService disassemblyRecordInfoService;
	@Autowired
	private RedisLock redisLock;

	/**
	 * 超过申请单次数 记录上传
	 * 申请单盖章超过使用次数记录上传
	 *
	 * @param info           使用记录
	 * @param stamperPicture 图片实体
	 * @param signet         设备实体
	 */
	@Override
	@Transactional
	public void addWarnExcessTimes(SealRecordInfoVoUpload info, StamperPicture stamperPicture, Signet signet) {
		info.setDeviceID(signet.getId());
		info.setOrgId(signet.getOrgId());
		info.setDepartId(signet.getDepartmentId());
		info.setDeviceName(signet.getName());
		info.setIsAudit(0); //0:盖章 1:审计
		info.setError(-1);

		//同步用章人
		synchFingerUser(info, signet);

		//去除图片重复检查，设备端有的时候的确会出现重复图片，hash值一样的图片
		//检查图片是否重复上传
//		if (handleInfoImg(stamperPicture, signet)) return;

		//防止记录重复生成
		if (handleREPEAT(info, signet, Global.ERROR)) {
			return;
		}

		//图片处理一下
		handleIMG(info, stamperPicture);

		//异常信息处理一下
		handlTimes(info, stamperPicture, signet);

		//同步一下印章次数
		try {
			if (info.getUseCount() > signet.getCount()) {
				signet.setCount(info.getCount());
				signetService.update(signet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 超过申请单次数 异常
	 *
	 * @param info           使用记录信息
	 * @param stamperPicture 图片信息
	 * @param signet         设备信息
	 */
	private void handlTimes(SealRecordInfoVoUpload info, StamperPicture stamperPicture, Signet signet) {
		List<ErrorType> errorTypes = new LinkedList<>();

		//使用超时异常 记录
		int deviceMode = info.getDeviceMode();
		if (!Objects.equals(deviceMode, 9) && (stamperPicture == null || stamperPicture.getId() == null)) {//无使用图片
			errorTypes.add(new ErrorType().setName(Global.ERROR01));
		}
		if (StringUtils.isBlank(info.getLocation())) {//无地址
			errorTypes.add(new ErrorType().setName(Global.ERROR05));
		}
		if (StringUtils.isAllBlank(info.getIdentity(), info.getUserName()) && (info.getPicUseId() == null || info.getPicUseId() == 0)) {//无用印人
			errorTypes.add(new ErrorType().setName(Global.ERROR03));
		}
		if (info.getApplicationID() == null || info.getApplicationID() == 0) {//无申请单
			errorTypes.add(new ErrorType().setName(Global.ERROR04));
		}

		errorTypes.add(new ErrorType().setName(Global.ERROR07));
		info.setError(-1);

		if (errorTypes.size() > 0) {
			foreach(info, errorTypes);
			SealRecordInfo sealRecordInfo = noticeManager(info, signet);

			if (sealRecordInfo != null) {
				Integer error = sealRecordInfo.getError();
				if (error == null || error.intValue() != info.getError().intValue()) {
					sealRecordInfoService.updateError(info.getId(), info.getError());
				}
			} else {
				log.info("设备ID:【{}】 申请单:【{}】 次数:【{}】 超次记录更新状态失败", info.getDeviceID(), info.getApplicationID(), info.getCount());
			}
		}
	}

	private SealRecordInfo noticeManager(SealRecordInfoVoUpload info, Signet signet) {
		//通知 授权人 + 审计人
		try {
			if (signet.getKeeperId() != null) {
				messageTempService.useErrorNotice(info.getUserName(), info.getDeviceName(), signet.getKeeperId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
//		try {
//			//发送异常/告警短信消息时，如果管章人与审计人是同一个人，则只需要发送1次即可
//			if (signet.getAuditorId() != null && !Objects.equals(signet.getAuditorId(),signet.getKeeperId())) {
//				messageTempService.useErrorNotice(info.getUserName(), info.getDeviceName(), signet.getAuditorId());
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		//查询此时数据库中申请单状态
		return sealRecordInfoService.get(info.getId());
	}

	/**
	 * 防拆卸报警 记录上传
	 * 印章拆卸时,触发警告信息
	 *
	 * @param info           使用记录
	 * @param stamperPicture 图片对象信息
	 * @param signet         设备信息
	 */
	@Override
	@Transactional
	public void addWarnDemolish(SealRecordInfoVoUpload info, StamperPicture stamperPicture, Signet signet) {
		//查询用印人
		String identity = info.getIdentity();
		if (StringUtils.isBlank(identity)) {
			Integer picUseId = info.getPicUseId();
			if (picUseId != null) {
				User user = userService.get(picUseId);
				if (user != null) {
					identity = user.getUserName();
				}
			}
		}
		identity = StringUtils.isBlank(identity) ? "未知用户" : identity;

		//向管章人发送通知消息及短信
		Notice notice = null;
		try {
			notice = messageTempService.dismantleNotice(signet.getName(), signet.getKeeperId());
		} catch (Exception e) {
			e.printStackTrace();
		}

		//创建拆卸记录
		DisassemblyRecordInfo dri = new DisassemblyRecordInfo();
		dri.setDeviceId(signet.getId());
		dri.setAesFileInfoId(stamperPicture == null ? null : stamperPicture.getFileId());
		dri.setLocation(info.getLocation());
		if (notice != null) {
			dri.setNoticeId(notice.getId());
		}
		dri.setOrgId(signet.getOrgId());
		dri.setRealTime(info.getRealTime());
		dri.setUseCount(info.getCount());
		dri.setUserId(info.getPicUseId());
		dri.setUserName(identity);
		disassemblyRecordInfoService.add(dri);

		String lockKey = RedisGlobal.NOTICE_DEMOLISH + signet.getKeeperId();
		RLock lock = redisLock.lock(lockKey);
		try {
			if (notice != null) {
				//添加拆卸标记到缓存
				String key = RedisGlobal.NOTICE_DEMOLISH + signet.getKeeperId();
				redisUtil.set(key, DateUtil.format(new Date()));
				log.info("-\t拆卸记录-通知标记\tinfo:{}\tnotice:{}", CommonUtils.objJsonWithIgnoreFiled(dri), CommonUtils.objJsonWithIgnoreFiled(notice));
			}
		} catch (Exception e) {
			log.info("X\t拆卸标记添加异常\tnotice:{}\tinfo:{}", CommonUtils.objJsonWithIgnoreFiled(notice), CommonUtils.objJsonWithIgnoreFiled(dri));
		} finally {
			if (lock != null) {
				try {
					lock.unlock();
				} catch (Exception e) {
					log.error("解锁异常", e);
				}
			}
		}
	}

	/**
	 * 超时按压报警 记录上传
	 * 盖章过程中,长时间未抬起印章,会有偷盖风险,当前方法记录警告消息
	 *
	 * @param info           使用记录信息
	 * @param stamperPicture 图片信息
	 * @param signet         设备信息
	 */
	@Override
	@Transactional
	public void addWarnTimeoutPressing(SealRecordInfoVoUpload info, StamperPicture stamperPicture, Signet signet) {
		info.setDeviceID(signet.getId());
		info.setOrgId(signet.getOrgId());
		info.setDepartId(signet.getDepartmentId());
		info.setDeviceName(signet.getName());
		info.setIsAudit(2); //0:盖章 1:审计 2:按压超时
		info.setError(-1);

		//查询当前次数绑定的使用记录
		getInfoByAuditor(info, signet, Global.ERROR);

		//图片信息保存并关联记录吧
		handleIMG(info, stamperPicture);

		//异常信息处理一下
		handleTimeoutWarn(info, stamperPicture, signet);
	}

	/**
	 * 密码模式 记录上传
	 *
	 * @param info           记录信息
	 * @param stamperPicture 图片信息
	 * @param signet         印章信息
	 */
	@Override
	@Transactional
	public void addPasswordInfo(SealRecordInfoVoUpload info, StamperPicture stamperPicture, Signet signet) {
		info.setDeviceID(signet.getId());
		info.setOrgId(signet.getOrgId());
		info.setDepartId(signet.getDepartmentId());
		info.setDeviceName(signet.getName());
		info.setIsAudit(3); //0:盖章 1:审计 2:按压超时  3:密码模式
		info.setError(0);
		info.setType(4);    //使用记录类型 0:申请单模式 1:申请单模式(量子) 2:指纹模式 3:指纹模式(量子)  4:密码模式
		info.setUserName("密码掌管人");    //密码模式下,使用人默认为"密码掌管人"

		//检查图片是否重复上传
		if (stamperPicture != null
				&& StringUtils.isNotBlank(stamperPicture.getFileName())
				&& stamperPicture.getType() != null) {
			StamperPicture sp = stamperPictureService.getByDeviceAndFileName(signet.getId(), stamperPicture.getFileName(), stamperPicture.getType(), stamperPicture.getHash());
			if (sp != null) {
				return;
			}
		}

		synchronized (ISealRecordInfoAsyncService.class) {
			//查询当前次数绑定的使用记录
			SealRecordInfo localInfo = sealRecordInfoService.get(signet.getId(), info.getCount(), signet.getOrgId());
			if (localInfo == null) {
				//没查到,创建一个新的
				sealRecordInfoService.add(info);
			} else {
				//查到了,使用记录存在
				info.setId(localInfo.getId());
				info.setCreateDate(localInfo.getCreateDate());
				info.setUpdateDate(localInfo.getUpdateDate());
				info.setError(localInfo.getError());
				sealRecordInfoService.update(info);
			}

			//图片信息保存并关联记录
			if (stamperPicture != null) {
				stamperPicture.setInfoId(info.getId());
				stamperPicture.setType(0);
				stamperPictureService.add(stamperPicture);
			}
		}

		handlePasswordInfo(info, stamperPicture, signet);
	}

	/**
	 * 密码模式异常处理
	 *
	 * @param info           记录信息
	 * @param stamperPicture 图片信息
	 * @param signet         设备信息
	 */
	private void handlePasswordInfo(SealRecordInfoVoUpload info, StamperPicture stamperPicture, Signet signet) {
		List<ErrorType> errorTypes = new LinkedList<>();

		//使用超时异常 记录
		int deviceMode = info.getDeviceMode();
		if (!Objects.equals(deviceMode, 9) && (stamperPicture == null || stamperPicture.getId() == null)) {//无使用图片
			errorTypes.add(new ErrorType().setName(Global.ERROR10));
		}
		if (StringUtils.isBlank(info.getLocation())) {//无地址
			errorTypes.add(new ErrorType().setName(Global.ERROR05));
		}

		//做下保险
		synchronized (ISealRecordInfoAsyncService.class) {
			if (errorTypes.size() > 0) {
				foreach(info, errorTypes);

				//通知 授权人 + 审计人
				SealRecordInfo sealRecordInfo = noticeManager(info, signet);
				if (sealRecordInfo != null) {
					Integer error = sealRecordInfo.getError();
					if (error == null || Objects.equals(error, Global.ERROR)) {
						sealRecordInfoService.updateError(info.getId(), info.getError());
					}
				} else {
					log.info("设备ID:【{}】 申请单:【{}】 次数:【{}】 超时记录更新状态失败", info.getDeviceID(), info.getApplicationID(), info.getCount());
				}
			}
		}
	}

	private void handleTimeoutWarn(SealRecordInfoVoUpload info, StamperPicture stamperPicture, Signet signet) {
		List<ErrorType> errorTypes = new LinkedList<>();

		//如果是密码模式的超时记录的话，不需要检查用印人异常
		Integer type = info.getType();//0:申请单模式  1:申请单模式(量子)  2:指纹模式  3:指纹模式(量子)  4:密码模式
		if (!Objects.equals(type, 4)) {
			if (StringUtils.isAllBlank(info.getIdentity(), info.getUserName()) && (info.getPicUseId() == null || Objects.equals(info.getPicUseId(), 0)) && !StringUtils.equalsIgnoreCase("密码掌管人", info.getIdentity())) {//无用印人
				errorTypes.add(new ErrorType().setName(Global.ERROR03));
			}
		}

		//如果是静默模式，不需要检查图片
		int deviceMode = info.getDeviceMode();
		if (!Objects.equals(deviceMode, 9)) {
			//使用超时异常 记录
			if (stamperPicture == null || stamperPicture.getId() == null) {//无使用图片
				errorTypes.add(new ErrorType().setName(Global.ERROR10));
			}
		}

		if (StringUtils.isBlank(info.getLocation())) {//无地址
			errorTypes.add(new ErrorType().setName(Global.ERROR05));
		}

		errorTypes.add(new ErrorType().setName(Global.ERROR09));
		info.setError(-1);

		//做下保险
		synchronized (ISealRecordInfoAsyncService.class) {
			if (errorTypes.size() > 0) {
				foreach(info, errorTypes);

				//通知 授权人 + 审计人
				SealRecordInfo sealRecordInfo = noticeManager(info, signet);
				if (sealRecordInfo != null) {
					Integer error = sealRecordInfo.getError();
					if (error == null || error.intValue() != info.getError().intValue()) {
						sealRecordInfoService.updateError(info.getId(), info.getError());
					}
				} else {
					log.info("设备ID:【{}】 申请单:【{}】 次数:【{}】 超时记录更新状态失败", info.getDeviceID(), info.getApplicationID(), info.getCount());
				}
			}
		}
	}

	/**
	 * 审计记录上传
	 *
	 * @param info           用章记录
	 * @param stamperPicture 图片实体
	 * @param signet         被审计的印章实体
	 */
	@Override
	@Transactional
	public void addAuditInfo(SealRecordInfoVoUpload info, StamperPicture stamperPicture, Signet signet) {
		info.setDeviceID(signet.getId());
		info.setOrgId(signet.getOrgId());
		info.setDepartId(signet.getDepartmentId());
		info.setDeviceName(signet.getName());
		info.setIsAudit(1); //0:盖章 1:审计
		info.setError(0);

		//审计图片需要进行去重处理，防止服务端业务处理响应超时时，设备多次重复上传
		if (stamperPicture != null && StringUtils.isNotBlank(stamperPicture.getFileName())) {
			String fileName = stamperPicture.getFileName();
			StamperPicture localStamperPicture = stamperPictureService.getByDeviceAndFileName(signet.getId(), fileName);
			if (localStamperPicture != null) {
				log.info("上传重复(审计)\t设备:{}\t次数:{}\t用印人:{}", signet.getId(), info.getCount(), info.getIdentity());
				return;
			}
		}

		//查询当前次数绑定的使用记录
		getInfoByAuditor(info, signet, Global.NORMAL);

		//图片信息保存并关联记录吧
		handleIMG(info, stamperPicture);
	}

	/**
	 * 指纹模式 记录上传
	 *
	 * @param info           用章记录
	 * @param stamperPicture 照片实体
	 * @param signet         设备
	 */
	@Override
	@Transactional
	public void addEasyInfo(SealRecordInfoVoUpload info, StamperPicture stamperPicture, Signet signet) {
		info.setDeviceID(signet.getId());
		info.setOrgId(signet.getOrgId());
		info.setDepartId(signet.getDepartmentId());
		info.setDeviceName(signet.getName());
		info.setIsAudit(0); //0:盖章 1:审计
		info.setError(0);

		//同步用章人
		synchFingerUser(info, signet);

		//去除图片重复检查，设备端有的时候的确会出现重复图片，hash值一样的图片
		//检查图片是否重复上传
//		if (handleInfoImg(stamperPicture, signet)) return;

		//防止记录重复生成
		if (handleREPEAT(info, signet, Global.NORMAL)) {
			return;
		}

		//图片信息保存并关联记录
		handleIMG(info, stamperPicture);

		//异常信息处理一下
		handleFinger(info, stamperPicture, signet);

//		String key = RedisGlobal.SEAL_INFO_IMAGE + signet.getId();
//		redisUtil.set(key, new Date().getTime(), RedisGlobal.SEAL_INFO_IMAGE_TIMEOUT);

		//同步一下印章次数
		try {
			if (info.getUseCount() > signet.getCount()) {
				signet.setCount(info.getCount());
				signetService.update(signet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 指纹模式 异常信息
	 *
	 * @param info           使用记录信息
	 * @param stamperPicture 图片对象信息
	 * @param signet         设备信息
	 */
	private void handleFinger(SealRecordInfoVoUpload info, StamperPicture stamperPicture, Signet signet) {
		try {
			List<ErrorType> errorTypes = new LinkedList<>();
			int deviceMode = info.getDeviceMode();
			if (!Objects.equals(deviceMode, 9) && (stamperPicture == null || stamperPicture.getId() == null)) {//无使用图片
				errorTypes.add(new ErrorType().setName(Global.ERROR01));
				info.setError(-1);//-1:异常 0:正常 1:警告
			}
			if (info.getCount() == null) {//次数异常
				errorTypes.add(new ErrorType().setName(Global.ERROR06));
				info.setError(-1);//-1:异常 0:正常 1:警告
			}
			if (StringUtils.isBlank(info.getLocation())) {//无地址
				errorTypes.add(new ErrorType().setName(Global.ERROR05));
				info.setError(-1);//-1:异常 0:正常 1:警告
			}
			if (StringUtils.isAllBlank(info.getIdentity(), info.getUserName()) && (info.getPicUseId() == null || info.getPicUseId() == 0)) {//无用印人
				errorTypes.add(new ErrorType().setName(Global.ERROR03));
				info.setError(-1);//-1:异常 0:正常 1:警告
			}

			//做下保险
			synchronized (ISealRecordInfoAsyncService.class) {
				if (errorTypes.size() > 0) {
					foreach(info, errorTypes);

					//通知 授权人 + 审计人
					SealRecordInfo sealRecordInfo = noticeManager(info, signet);
					if (sealRecordInfo != null) {
						Integer error = sealRecordInfo.getError();
						if (error == null || error.intValue() != info.getError().intValue()) {
							sealRecordInfoService.updateError(info.getId(), info.getError());
						}
					} else {
						log.info("设备ID:【{}】 申请单:【{}】 次数:【{}】 指纹记录更新失败", info.getDeviceID(), info.getApplicationID(), info.getCount());
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 辅助方法，遍历并将异常信息存储至数据库
	 *
	 * @param info       使用记录信息
	 * @param errorTypes 需要存储的异常信息列表
	 */
	private void foreach(SealRecordInfoVoUpload info, List<ErrorType> errorTypes) {
//		for (ErrorType et : errorTypes) {
//			et.setOrgId(info.getOrgId());
//			et.setSealRecordInfoId(info.getId());
//			try {
//				errorTypeService.add(et);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
		//foreach(info, errorTypes);
		errorTypeService.addList(info.getOrgId(), info.getId(), errorTypes);
	}

	/**
	 * 申请单模式 记录上传
	 *
	 * @param info           使用记录信息
	 * @param stamperPicture 图片信息
	 * @param signet         设备信息
	 */
	@Override
	@Transactional
	public void addNormalInfo(SealRecordInfoVoUpload info, StamperPicture stamperPicture, Signet signet) {
		info.setDeviceID(signet.getId());
		info.setOrgId(signet.getOrgId());
		info.setDepartId(signet.getDepartmentId());
		info.setDeviceName(signet.getName());
		info.setIsAudit(0); //0:盖章 1:审计
		info.setError(0);

		//同步用章人
		synchFingerUser(info, signet);

		//去除图片重复检查，设备端有的时候的确会出现重复图片，hash值一样的图片
		//检查图片是否重复上传
//		if (handleInfoImg(stamperPicture, signet)) return;

		//防止记录重复生成
		if (handleREPEAT(info, signet, Global.NORMAL)) {
			return;
		}

		//保存图片记录
		handleIMG(info, stamperPicture);

		//异常信息处理一下
		handleApplicationError(info, stamperPicture, signet);

		//同步一下印章次数
		try {
			if (info.getUseCount() > signet.getCount()) {
				signet.setCount(info.getCount());
				signetService.update(signet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 处理申请单模式记录异常信息
	 *
	 * @param info           上传使用记录信息
	 * @param stamperPicture 图片信息
	 * @param signet         设备信息
	 */
	private void handleApplicationError(SealRecordInfoVoUpload info, StamperPicture stamperPicture, Signet signet) {
		try {
			List<ErrorType> errorTypes = new LinkedList<>();
			if (info.getApplicationID() == null || info.getApplicationID() == 0) {//无申请单
				errorTypes.add(new ErrorType().setName(Global.ERROR04));
				info.setError(-1);//-1:异常 0:正常 1:警告
			}
			int deviceMode = info.getDeviceMode();
			if (!Objects.equals(deviceMode, 9) && (stamperPicture == null || stamperPicture.getId() == null)) {//无使用图片
				errorTypes.add(new ErrorType().setName(Global.ERROR01));
				info.setError(-1);//-1:异常 0:正常 1:警告
			}
			if (info.getCount() == null) {//次数异常
				errorTypes.add(new ErrorType().setName(Global.ERROR06));
				info.setError(-1);//-1:异常 0:正常 1:警告
			}
			if (StringUtils.isBlank(info.getLocation())) {//无地址
				errorTypes.add(new ErrorType().setName(Global.ERROR05));
				info.setError(-1);//-1:异常 0:正常 1:警告
			}
			if (StringUtils.isAllBlank(info.getIdentity(), info.getUserName()) && (info.getPicUseId() == null || info.getPicUseId() == 0)) {//无用印人
				errorTypes.add(new ErrorType().setName(Global.ERROR03));
				info.setError(-1);//-1:异常 0:正常 1:警告
			}

			//做下保险
			synchronized (ISealRecordInfoAsyncService.class) {
				if (errorTypes.size() > 0) {
					foreach(info, errorTypes);

					//通知 授权人 + 审计人
					SealRecordInfo sealRecordInfo = noticeManager(info, signet);
					if (sealRecordInfo != null) {
						Integer error = sealRecordInfo.getError();
						if (error == null || error.intValue() != info.getError().intValue()) {
							sealRecordInfoService.updateError(info.getId(), info.getError());
						}
					} else {
						log.info("设备ID:【{}】 申请单:【{}】 次数:【{}】 申请单记录更新失败", info.getDeviceID(), info.getApplicationID(), info.getCount());
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 同步/解析用章人id和姓名
	 */
	private void synchFingerUser(SealRecordInfoVoUpload info, Signet signet) {
		try {
			Integer userId = info.getPicUseId();
			String username = info.getIdentity();

			if (userId != null && StringUtils.isNotBlank(username)) {
				info.setUserId(userId);
				info.setUserName(username);
				return;
			}

			User fingerUser = null;

			//根据用印人ID查询用印人
			if (userId != null && userId != 0) {

				//先从缓存查
				String key = RedisGlobal.IDENTITY_USERID_INFO + userId;
				Object obj = redisUtil.get(key);
				if (obj != null && StringUtils.isNotBlank(obj.toString())) {
					fingerUser = JSONObject.parseObject(obj.toString(), User.class);
				}

				//缓存没查到，调用Auth系统查询该用户
				if (fingerUser == null) {
					fingerUser = userService.get(userId);
				}

				//如果查到了，直接赋值后返回
				if (fingerUser != null && fingerUser.getId() != null && fingerUser.getOrgId().intValue() == signet.getOrgId().intValue()) {
					info.setUserId(fingerUser.getId());
					info.setUserName(fingerUser.getUserName());
					//将用户信息存储到缓存，提供下次访问效率
					redisUtil.set(key, JSONObject.toJSONString(fingerUser), RedisGlobal.IDENTITY_USERID_INFO_TIMEOUT);
					return;
				}

				//如果没查到，就走下面的用印人姓名查询
			}

			//根据用印人姓名查询用印人，此查询不准确，不建议使用
			if (StringUtils.isNotBlank(username)) {

				//先从缓存查
				String key = RedisGlobal.IDENTITY_ORG_USERNAME_INFO + signet.getOrgId() + ":" + username;
				Object obj = redisUtil.get(key);
				if (obj != null && StringUtils.isNotBlank(obj.toString())) {
					fingerUser = JSONObject.parseObject(obj.toString(), User.class);
				}

				//缓存没查到，就从Auth系统查询
				if (fingerUser == null) {
					fingerUser = userService.getByOrgAndName(signet.getOrgId(), username);
				}

				//缓存查到了，就直接赋值
				if (fingerUser != null && fingerUser.getId() != null && fingerUser.getOrgId().intValue() == signet.getOrgId().intValue()) {
					info.setUserId(fingerUser.getId());
					info.setUserName(username);
					//将用户信息存储到缓存，提供下次访问效率
					redisUtil.set(key, JSONObject.toJSONString(fingerUser), RedisGlobal.IDENTITY_ORG_USERNAME_TIMEOUT);
					return;
				}
			}
			log.info("印章ID：【{}】 次数：【{}】 用印人ID:【{}】 用印人：【{}】 用印人不存在或非设备当前公司", info.getDeviceID(), info.getCount(), info.getUserId(), info.getUserName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询当前次数绑定的使用记录
	 *
	 * @param info   使用记录信息
	 * @param signet 设备信息
	 * @param error  异常信息
	 */
	private void getInfoByAuditor(SealRecordInfoVoUpload info, Signet signet, int error) {
		synchronized (ISealRecordInfoAsyncService.class) {
			//查询这个照片绑定的使用记录
			SealRecordInfo _info = sealRecordInfoService.get(signet.getId(), info.getCount(), signet.getOrgId());
			if (_info == null) {
				//没查到,创建一个新的
				info.setDeviceID(signet.getId());
				info.setOrgId(signet.getOrgId());
				info.setDepartId(signet.getDepartmentId());
				info.setDeviceName(signet.getName());
				info.setIsAudit(1); //0:盖章 1:审计(按压超时)
				info.setError(error);
				sealRecordInfoService.add(info);
			} else {
				//查到了,使用记录存在
				info.setId(_info.getId());
				info.setCreateDate(_info.getCreateDate());
				info.setUpdateDate(_info.getUpdateDate());
				info.setError(_info.getError());
			}
		}
	}

	/**
	 * 防止记录重复生成
	 *
	 * @param info   使用记录
	 * @param signet 设备实体
	 * @return true：重复  false：未重复
	 */
	private boolean handleREPEAT(SealRecordInfoVoUpload info, Signet signet, int infoType) {
		synchronized (ISealRecordInfoAsyncService.class) {
			//根据印章id和当前使用次数，查询是否存在同样的使用记录
			SealRecordInfo _info = sealRecordInfoService.get(signet.getId(), info.getCount(), signet.getOrgId());
			if (_info != null) {
				//查出来说明被审计(或警告记录)抢先了，或者记录重复了
				Integer isAudit = _info.getIsAudit();// 0盖章 1审计 2:按压超时
				if (isAudit == 1) {
					//这个使用记录是审计(警告)时创建的,将查出的使用记录id赋值给新上传的使用记录id
					info.setId(_info.getId());
					info.setCreateDate(_info.getCreateDate());
					info.setError(_info.getError());
					sealRecordInfoService.update(info);
				} else if (isAudit == 0) {
					//重复情况就删除之前传过的图片【因为图片在记录生成前就先上传了，删除这个图片】
					//FileUtil.delFile(file);
					log.info("设备:【{}】 次数:【{}】 记录重复 记录详情:【{}】", signet.getId(), info.getCount(), JSONObject.toJSONString(_info));
					return true;
				} else {
					log.info("设备:【{}】 次数:【{}】 记录异常 记录详情:【{}】", signet.getId(), info.getCount(), JSONObject.toJSONString(_info));
					return true;
				}
			} else {
				info.setError(infoType);
				sealRecordInfoService.add(info);
			}
			return false;
		}
	}

	/**
	 * 处理图片
	 *
	 * @param info           使用记录
	 * @param stamperPicture 图片文件
	 */
	private void handleIMG(SealRecordInfoVoUpload info, StamperPicture stamperPicture) {
		if (stamperPicture != null && info != null && info.getId() != null) {
			stamperPicture.setInfoId(info.getId());
			stamperPictureService.add(stamperPicture);
		}
	}

	/**
	 * 静默模式(摄像头关闭) 记录上传
	 *
	 * @param info           记录信息
	 * @param stamperPicture 图片信息
	 * @param signet         设备信息
	 */
	@Override
	public void addNoCameraInfo(SealRecordInfoVoUpload info, StamperPicture stamperPicture, Signet signet) {
		info.setDeviceID(signet.getId());
		info.setOrgId(signet.getOrgId());
		info.setDepartId(signet.getDepartmentId());
		info.setDeviceName(signet.getName());
		info.setIsAudit(0); //0:盖章 1:审计
		info.setError(0);

		//同步用章人
		synchFingerUser(info, signet);

		//防止记录重复生成
		if (handleREPEAT(info, signet, Global.NORMAL)) {
			return;
		}

		//异常信息处理一下
		handleNoCameraInfo(info, stamperPicture, signet);

		//同步一下印章次数
		try {
			if (info.getUseCount() > signet.getCount()) {
				signet.setCount(info.getCount());
				signetService.update(signet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 静默模式(摄像头关闭) 指纹盖章
	 *
	 * @param info   记录信息
	 * @param signet 设备信息
	 */
	@Override
	@Transactional
	public void addNoCameraInfoWithEasy(SealRecordInfoVoUpload info, Signet signet) {
		info.setDeviceID(signet.getId());
		info.setOrgId(signet.getOrgId());
		info.setDepartId(signet.getDepartmentId());
		info.setDeviceName(signet.getName());
		info.setIsAudit(0); //0:盖章 1:审计
		info.setError(0);

		//同步用章人
		synchFingerUser(info, signet);

		//防止记录重复生成
		if (handleREPEAT(info, signet, Global.NORMAL)) {
			return;
		}

		//异常信息处理一下
		try {
			List<ErrorType> errorTypes = new LinkedList<>();
			if (info.getCount() == null) {//次数异常
				errorTypes.add(new ErrorType().setName(Global.ERROR06));
				info.setError(-1);//-1:异常 0:正常 1:警告
			}
			if (StringUtils.isBlank(info.getLocation())) {//无地址
				errorTypes.add(new ErrorType().setName(Global.ERROR05));
				info.setError(-1);//-1:异常 0:正常 1:警告
			}
			if (StringUtils.isAllBlank(info.getIdentity(), info.getUserName()) && (info.getPicUseId() == null || info.getPicUseId() == 0)) {//无用印人
				errorTypes.add(new ErrorType().setName(Global.ERROR03));
				info.setError(-1);//-1:异常 0:正常 1:警告
			}

			//做下保险
			synchronized (ISealRecordInfoAsyncService.class) {
				if (errorTypes.size() > 0) {
					foreach(info, errorTypes);

					//通知 授权人 + 审计人
					SealRecordInfo sealRecordInfo = noticeManager(info, signet);
					if (sealRecordInfo != null) {
						Integer error = sealRecordInfo.getError();
						if (error == null || error.intValue() != info.getError().intValue()) {
							sealRecordInfoService.updateError(info.getId(), info.getError());
						}
					} else {
						log.info("设备ID:【{}】 申请单:【{}】 次数:【{}】 静默记录更新失败", info.getDeviceID(), info.getApplicationID(), info.getCount());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		//同步一下印章次数
		try {
			if (info.getUseCount() > signet.getCount()) {
				signet.setCount(info.getCount());
				signetService.update(signet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 处理摄像头关闭状态下的记录异常信息
	 *
	 * @param info
	 * @param stamperPicture
	 * @param signet
	 */
	private void handleNoCameraInfo(SealRecordInfoVoUpload info, StamperPicture stamperPicture, Signet signet) {
		try {
			List<ErrorType> errorTypes = new LinkedList<>();
			if (info.getApplicationID() == null || info.getApplicationID() == 0) {//无申请单
				errorTypes.add(new ErrorType().setName(Global.ERROR04));
				info.setError(-1);//-1:异常 0:正常 1:警告
			}
			if (info.getCount() == null) {//次数异常
				errorTypes.add(new ErrorType().setName(Global.ERROR06));
				info.setError(-1);//-1:异常 0:正常 1:警告
			}
			if (StringUtils.isBlank(info.getLocation())) {//无地址
				errorTypes.add(new ErrorType().setName(Global.ERROR05));
				info.setError(-1);//-1:异常 0:正常 1:警告
			}
			if (StringUtils.isAllBlank(info.getIdentity(), info.getUserName()) && (info.getPicUseId() == null || info.getPicUseId() == 0)) {//无用印人
				errorTypes.add(new ErrorType().setName(Global.ERROR03));
				info.setError(-1);//-1:异常 0:正常 1:警告
			}

			//做下保险
			synchronized (ISealRecordInfoAsyncService.class) {
				if (errorTypes.size() > 0) {
					foreach(info, errorTypes);

					//通知 授权人 + 审计人
					SealRecordInfo sealRecordInfo = noticeManager(info, signet);
					if (sealRecordInfo != null) {
						Integer error = sealRecordInfo.getError();
						if (error == null || error.intValue() != info.getError().intValue()) {
							sealRecordInfoService.updateError(info.getId(), info.getError());
						}
					} else {
						log.info("设备ID:【{}】 申请单:【{}】 次数:【{}】 静默记录更新失败", info.getDeviceID(), info.getApplicationID(), info.getCount());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
