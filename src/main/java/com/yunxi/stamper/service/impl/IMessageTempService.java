package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.entity.Org;
import com.yunxi.stamper.service.*;
import com.yunxi.stamper.sys.config.ProjectProperties;
import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.MessageTemp;
import com.yunxi.stamper.entity.Notice;
import com.yunxi.stamper.entity.User;
import com.yunxi.stamper.entityVo.MessageTempVo;
import com.yunxi.stamper.mapper.MessageTempMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/23 0023 11:34
 */
@Slf4j
@Service
public class IMessageTempService implements MessageTempService {
	@Autowired
	private MessageTempMapper mapper;
	@Autowired
	private UserService userService;
	@Autowired
	private NoticeService noticeService;
	@Autowired
	private SMSService smsService;
	@Autowired
	private OrgService orgService;
	@Autowired
	private ProjectProperties properties;

	@Override
	public MessageTemp get(Integer id) {
		if (id != null) {
			return mapper.selectByPrimaryKey(id);
		}
		return null;
	}

	@Override
	@Transactional
	public void update(MessageTemp mt) {
		int updateCount = 0;
		if (mt != null && mt.getId() != null) {
			mt.setUpdateDate(new Date());
			updateCount = mapper.updateByPrimaryKey(mt);
		}
		if (updateCount != 1) {
			throw new PrintException("短信/通知模板更新失败");
		}
	}

	@Override
	public List<MessageTempVo> getAll() {
		return mapper.selectByAll();
	}

	@Override
	public MessageTemp getByCode(String code) {
		if (StringUtils.isNotBlank(code)) {
			Example example = new Example(MessageTemp.class);
			example.createCriteria().andIsNull("deleteDate")
					.andEqualTo("code", code);
			return mapper.selectOneByExample(example);
		}
		return null;
	}

	/**
	 * 申请用章后,通知审批人审批
	 * 短信:您好,${code0}提醒您,您有一条新的审批请求,来自${code1}申请人${code2}的申请单${code3},请及时处理。
	 * 通知:您有一条新的审批请求,来自申请人%s的申请单%s,请及时处理
	 *
	 * @param title     申请标题
	 * @param userName  申请人
	 * @param receiveId 接收人ID
	 */
	@Override
	public void approvalNotice(String title, String userName, Integer receiveId) {
		String tempCode = "SP001";
		String logSuffix = String.format("receiveId:%d\ttitle:%s\tuserName:%s", receiveId, title, userName);

		User receiver = userService.get(receiveId);
		if (receiver == null) {
			log.info("x\t消息失败-接收人不存在{}\t{}", tempCode, logSuffix);
			return;
		}

		MessageTemp mt = getByCode(tempCode);
		if (mt == null) {
			log.info("-\t消息忽略-未订阅{}\treceiver:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix);
			return;
		}

		//发送短信
		try {
			Integer orgId = receiver.getOrgId();
			Org org = orgService.get(orgId);
			Integer smsTempId = mt.getSmsTempId();
			smsService.sendSMS(receiver, smsTempId, properties.getAliyunSms().getSignName(), org.getName(), userName, title);
		} catch (Exception e) {
			log.info("x\t短信失败{}\treceive:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix, e);
		}

		//发送通知
		try {
			Integer noticeTempId = mt.getNoticeTempId();
			noticeService.sendNotice(receiver, noticeTempId, userName, title);
		} catch (Exception e) {
			log.info("x\t通知失败{}\treceive:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix, e);
		}
	}

	/**
	 * 审批转交后,通知申请人
	 * 短信:您好,${code0}提醒您,您的申请单${code1}已被审批人${code2}转交给${code3}审批,请耐心等待。
	 * 通知:您的申请单%s已被审批人%s转交给%s审批,请耐心等待
	 *
	 * @param title       申请标题
	 * @param managerName 审批人
	 * @param transName   转交人
	 * @param receiveId   接收人ID
	 */
	@Override
	public void approvalTransferNotice(String title, String managerName, String transName, Integer receiveId) {
		String tempCode = "SP002";
		String logSuffix = String.format("receiveId:%d\ttitle:%s\tmanagerName:%s\ttransName:%s", receiveId, title, managerName, transName);

		User receiver = userService.get(receiveId);
		if (receiver == null) {
			log.info("x\t消息失败-接收人不存在{}\t{}", tempCode, logSuffix);
			return;
		}

		MessageTemp mt = getByCode(tempCode);
		if (mt == null) {
			log.info("-\t消息忽略-未订阅{}\treceiver:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix);
			return;
		}

		//发送短信
		try {
			Integer smsTempId = mt.getSmsTempId();
			smsService.sendSMS(receiver, smsTempId, properties.getAliyunSms().getSignName(), title, managerName, transName);
		} catch (Exception e) {
			log.info("x\t短信失败{}\treceive:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix, e);
		}

		//发送通知
		try {
			Integer noticeTempId = mt.getNoticeTempId();
			noticeService.sendNotice(receiver, noticeTempId, title, managerName, transName);
		} catch (Exception e) {
			log.info("x\t通知失败{}\treceive:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix, e);
		}
	}

	/**
	 * 审批通过后,通知申请人
	 * 短信:您好,${code0}提醒您,您的申请单${code1}已被审批人${code2}审批通过。
	 * 通知:您的申请单%s已被审批人%s审批通过
	 *
	 * @param title       申请标题
	 * @param managerName 审批人
	 * @param receiveId   接收人ID
	 */
	@Override
	public void managerOKNotice(String title, String managerName, Integer receiveId) {
		String tempCode = "SP003";
		String logSuffix = String.format("receiveId:%d\ttitle:%s\tmanagerName:%s", receiveId, title, managerName);

		User receiver = userService.get(receiveId);
		if (receiver == null) {
			log.info("x\t消息失败-接收人不存在{}\t{}", tempCode, logSuffix);
			return;
		}

		MessageTemp mt = getByCode(tempCode);
		if (mt == null) {
			log.info("-\t消息忽略-未订阅{}\treceiver:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix);
			return;
		}

		try {
			Integer smsTempId = mt.getSmsTempId();
			smsService.sendSMS(receiver, smsTempId, properties.getAliyunSms().getSignName(), title, managerName);
		} catch (Exception e) {
			log.info("x\t短信失败{}\treceive:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix, e);
		}

		try {
			Integer noticeTempId = mt.getNoticeTempId();
			noticeService.sendNotice(receiver, noticeTempId, title, managerName);
		} catch (Exception e) {
			log.info("x\t通知失败{}\treceive:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix, e);
		}
	}

	/**
	 * 审批拒绝后,通知申请人
	 * 短信:您好,${code0}提醒您,您的申请单${code1}已被审批人${code2}审批驳回。
	 * 通知:您的申请单%s已被审批人%s审批通过
	 *
	 * @param title       申请标题
	 * @param managerName 审批人
	 * @param receiveId   接收人ID
	 */
	@Override
	public void managerFAILNotice(String title, String managerName, Integer receiveId) {
		String tempCode = "SP004";
		String logSuffix = String.format("receiveId:%d\ttitle:%s\tmanagerName:%s", receiveId, title, managerName);

		User receiver = userService.get(receiveId);
		if (receiver == null) {
			log.info("x\t消息失败-接收人不存在{}\t{}", tempCode, logSuffix);
			return;
		}

		MessageTemp mt = getByCode(tempCode);
		if (mt == null) {
			log.info("-\t消息忽略-未订阅{}\treceiver:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix);
			return;
		}

		//发送短信
		try {
			Integer smsTempId = mt.getSmsTempId();
			smsService.sendSMS(receiver, smsTempId, properties.getAliyunSms().getSignName(), title, managerName);
		} catch (Exception e) {
			log.info("x\t短信失败{}\treceive:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix, e);
		}

		//发送通知
		try {
			Integer noticeTempId = mt.getNoticeTempId();
			noticeService.sendNotice(receiver, noticeTempId, title, managerName);
		} catch (Exception e) {
			log.info("x\t通知失败{}\treceive:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix, e);
		}
	}

	/**
	 * 审批完成后,通知授权人授权
	 * 短信:您好,${code0}提醒您,您有一条新的授权请求,来自${code1}申请人${code2}的申请单${code3},请及时处理。
	 * 通知:您有一条新的授权请求,来自申请人%s的申请单%s,请及时处理
	 *
	 * @param title     申请标题
	 * @param userName  申请人
	 * @param orgName   申请人组织名称
	 * @param receiveId 接收人ID
	 */
	@Override
	public void authorizationNotice(String orgName, String userName, String title, Integer receiveId) {
		String tempCode = "SQ001";
		String logSuffix = String.format("receiveId:%d\torgName:%s\tuserName:%s\ttitle:%s", receiveId, orgName, userName, title);

		User receiver = userService.get(receiveId);
		if (receiver == null) {
			log.info("x\t消息失败-接收人不存在{}\t{}", tempCode, logSuffix);
			return;
		}

		MessageTemp mt = getByCode(tempCode);
		if (mt == null) {
			log.info("-\t消息忽略-未订阅{}\treceiver:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix);
			return;
		}

		//发送短信
		try {
			Integer smsTempId = mt.getSmsTempId();
			smsService.sendSMS(receiver, smsTempId, properties.getAliyunSms().getSignName(), orgName, userName, title);
		} catch (Exception e) {
			log.info("x\t短信失败{}\treceive:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix, e);
		}

		//发送通知
		try {
			Integer noticeTempId = mt.getNoticeTempId();
			noticeService.sendNotice(receiver, noticeTempId, title, userName);
		} catch (Exception e) {
			log.info("x\t通知失败{}\treceive:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix, e);
		}
	}

	/**
	 * 授权通过,通知申请人
	 * 短信:您好,${code0}提醒您,您的申请单${code1}已被管章人${code2}授权通过。
	 * 通知:您的申请单%s已被管章人%s授权通过
	 *
	 * @param title      申请标题
	 * @param keeperName 管章人
	 * @param receiveId  接收人ID
	 */
	@Override
	public void keeperOKNotice(String title, String keeperName, Integer receiveId) {
		String tempCode = "SQ003";
		String logSuffix = String.format("receiveId:%d\ttitle:%s\tkeeperName:%s", receiveId, title, keeperName);

		User receiver = userService.get(receiveId);
		if (receiver == null) {
			log.info("x\t消息失败-接收人不存在{}\t{}", tempCode, logSuffix);
			return;
		}

		MessageTemp mt = getByCode(tempCode);
		if (mt == null) {
			log.info("-\t消息忽略-未订阅{}\treceiver:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix);
			return;
		}

		//发送短信
		try {
			Integer smsTempId = mt.getSmsTempId();
			smsService.sendSMS(receiver, smsTempId, properties.getAliyunSms().getSignName(), title, keeperName);
		} catch (Exception e) {
			log.info("x\t短信失败{}\treceive:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix, e);
		}

		//发送通知
		try {
			Integer noticeTempId = mt.getNoticeTempId();
			noticeService.sendNotice(receiver, noticeTempId, title, keeperName);
		} catch (Exception e) {
			log.info("x\t通知失败{}\treceive:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix, e);
		}
	}

	/**
	 * 授权拒绝,通知申请人
	 * 短信:您好,${code0}提醒您,您的申请单${code1}已被管章人${code2}授权驳回。
	 * 通知:您的申请单%s已被管章人%s授权驳回
	 *
	 * @param title      申请标题
	 * @param keeperName 管章人
	 * @param receiveId  接收人ID
	 */
	@Override
	public void keeperFAILNotice(String title, String keeperName, Integer receiveId) {
		String tempCode = "SQ002";
		String logSuffix = String.format("receiveId:%d\ttitle:%s\tkeeperName:%s", receiveId, title, keeperName);

		User receiver = userService.get(receiveId);
		if (receiver == null) {
			log.info("x\t消息失败-接收人不存在{}\t{}", tempCode, logSuffix);
			return;
		}

		MessageTemp mt = getByCode(tempCode);
		if (mt == null) {
			log.info("-\t消息忽略-未订阅{}\treceiver:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix);
			return;
		}

		//发送短信
		try {
			Integer smsTempId = mt.getSmsTempId();
			smsService.sendSMS(receiver, smsTempId, properties.getAliyunSms().getSignName(), title, keeperName);
		} catch (Exception e) {
			log.info("x\t短信失败{}\treceive:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix, e);
		}

		//发送通知
		try {
			Integer noticeTempId = mt.getNoticeTempId();
			noticeService.sendNotice(receiver, noticeTempId, title, keeperName);
		} catch (Exception e) {
			log.info("x\t通知失败{}\treceive:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix, e);
		}
	}

	/**
	 * 授权完成后,通知审计员审计
	 * 短信:您好,${code0}提醒您,您有一条新的审计请求,来自${code1}申请人${code2}的申请单${code3},请及时处理。
	 * 通知:您有一条新的审计请求,来自申请人%s的申请单%s,请及时处理
	 *
	 * @param orgName   组织名称
	 * @param userName  申请人
	 * @param title     申请标题
	 * @param receiveId 接收人ID
	 */
	@Override
	public void auditorNotice(String orgName, String userName, String title, Integer receiveId) {
		String tempCode = "SJ001";
		String logSuffix = String.format("receiveId:%d\torgName:%s\tuserName:%s\ttitle:%s", receiveId, orgName, userName, title);

		User receiver = userService.get(receiveId);
		if (receiver == null) {
			log.info("x\t消息失败-接收人不存在{}\t{}", tempCode, logSuffix);
			return;
		}

		MessageTemp mt = getByCode(tempCode);
		if (mt == null) {
			log.info("-\t消息忽略-未订阅{}\treceiver:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix);
			return;
		}

		//发送短信
		try {
			Integer smsTempId = mt.getSmsTempId();
			smsService.sendSMS(receiver, smsTempId, properties.getAliyunSms().getSignName(), orgName, userName, title);
		} catch (Exception e) {
			log.info("x\t短信失败{}\treceive:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix, e);
		}

		//发送通知
		try {
			Integer noticeTempId = mt.getNoticeTempId();
			noticeService.sendNotice(receiver, noticeTempId, userName, title);
		} catch (Exception e) {
			log.info("x\t通知失败{}\treceive:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix, e);
		}
	}

	/**
	 * 审计通过,通知申请人
	 * 短信:您好,${code0}提醒您,您的申请单${code1}已被审计员${code2}审计通过。
	 * 通知:您的申请单%s已被审计员%s审计通过
	 *
	 * @param title       申请标题
	 * @param auditorName 审计人
	 * @param receiveId   接收人ID
	 */
	@Override
	public void auditorOKNotice(String title, String auditorName, Integer receiveId) {
		String tempCode = "SJ002";
		String logSuffix = String.format("receiveId:%d\ttitle:%s\tauditorName:%s", receiveId, title, auditorName);

		User receiver = userService.get(receiveId);
		if (receiver == null) {
			log.info("x\t消息失败-接收人不存在{}\t{}", tempCode, logSuffix);
			return;
		}

		MessageTemp mt = getByCode(tempCode);
		if (mt == null) {
			log.info("-\t消息忽略-未订阅{}\treceiver:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix);
			return;
		}

		//发送短信
		try {
			Integer smsTempId = mt.getSmsTempId();
			smsService.sendSMS(receiver, smsTempId, properties.getAliyunSms().getSignName(), title, auditorName);
		} catch (Exception e) {
			log.info("x\t短信失败{}\treceive:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix, e);
		}

		//发送通知
		try {
			Integer noticeTempId = mt.getNoticeTempId();
			noticeService.sendNotice(receiver, noticeTempId, title, auditorName);
		} catch (Exception e) {
			log.info("x\t通知失败{}\treceive:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix, e);
		}
	}

	/**
	 * 审计拒绝,通知申请人
	 * 短信:您好,${code0}提醒您,您的申请单${code1}已被审计员${code2}审计驳回。
	 * 通知:您的申请单%s已被审计员%s审计通过
	 *
	 * @param title       申请标题
	 * @param auditorName 审计人
	 * @param receiveId   接收人ID
	 */
	@Override
	public void auditorFAILNotice(String title, String auditorName, Integer receiveId) {
		String tempCode = "SJ003";
		String logSuffix = String.format("receiveId:%d\ttitle:%s\tauditorName:%s", receiveId, title, auditorName);

		User receiver = userService.get(receiveId);
		if (receiver == null) {
			log.info("x\t消息失败-接收人不存在{}\t{}", tempCode, logSuffix);
			return;
		}

		MessageTemp mt = getByCode(tempCode);
		if (mt == null) {
			log.info("-\t消息忽略-未订阅{}\treceiver:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix);
			return;
		}

		//发送短信
		try {
			Integer smsTempId = mt.getSmsTempId();
			smsService.sendSMS(receiver, smsTempId, properties.getAliyunSms().getSignName(), title, auditorName);
		} catch (Exception e) {
			log.info("x\t短信失败{}\treceive:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix, e);
		}

		//发送通知
		try {
			Integer noticeTempId = mt.getNoticeTempId();
			noticeService.sendNotice(receiver, noticeTempId, title, auditorName);
		} catch (Exception e) {
			log.info("x\t通知失败{}\treceive:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix, e);
		}
	}

	/**
	 * 盖章出现异常或警告
	 * 短信:您好,${code0}提醒您,${code1}使用印章${code2}时出现警告或异常,请及时处理。
	 * 通知:%s使用印章%s时出现警告或异常,请及时处理
	 *
	 * @param userName   用印人
	 * @param deviceName 印章名称
	 * @param receiveId  接收人ID
	 */
	@Override
	public void useErrorNotice(String userName, String deviceName, Integer receiveId) {
		String tempCode = "YZ001";
		String logSuffix = String.format("receiveId:%d\tdeviceName:%s\tuserName:%s", receiveId, deviceName, userName);

		User receiver = userService.get(receiveId);
		if (receiver == null) {
			log.info("x\t消息失败-接收人不存在{}\t{}", tempCode, logSuffix);
			return;
		}

		MessageTemp mt = getByCode(tempCode);
		if (mt == null) {
			log.info("-\t消息忽略-未订阅{}\treceiver:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix);
			return;
		}

		//发送短信
		try {
			Integer smsTempId = mt.getSmsTempId();
			smsService.sendSMS(receiver, smsTempId, properties.getAliyunSms().getSignName(), userName, deviceName);
		} catch (Exception e) {
			log.info("x\t短信失败{}\treceive:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix, e);
		}

		//发送通知
		try {
			Integer noticeTempId = mt.getNoticeTempId();
			noticeService.sendNotice(receiver, noticeTempId, userName, deviceName);
		} catch (Exception e) {
			log.info("x\t通知失败{}\treceive:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix, e);
		}
	}

	/**
	 * 设备信息更新时
	 * 短信:您好,${code0}提醒您,${code1}设备${code2}已被管理员${code3}更新,请至后台系统进行查看。
	 * 通知:贵公司设备%s已被管理员%s更新,请至后台系统进行查看
	 *
	 * @param orgName    组织名称
	 * @param deviceName 印章名称
	 * @param userName   管理员
	 * @param receiveId  接收人ID
	 */
	@Override
	public void updateDeviceNotice(String orgName, String deviceName, String userName, Integer receiveId) {
		String tempCode = "SB001";
		String logSuffix = String.format("receiveId:%d\torgName:%s\tdeviceName:%s\tuserName:%s", receiveId, orgName, deviceName, userName);

		User receiver = userService.get(receiveId);
		if (receiver == null) {
			log.info("x\t消息失败-接收人不存在{}\t{}", tempCode, logSuffix);
			return;
		}

		MessageTemp mt = getByCode(tempCode);
		if (mt == null) {
			log.info("-\t消息忽略-未订阅{}\treceiver:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix);
			return;
		}

		//发送短信
		try {
			Integer smsTempId = mt.getSmsTempId();
			smsService.sendSMS(receiver, smsTempId, properties.getAliyunSms().getSignName(), orgName, deviceName, userName);
		} catch (Exception e) {
			log.info("x\t短信失败{}\treceive:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix, e);
		}

		//发送通知
		try {
			Integer noticeTempId = mt.getNoticeTempId();
			noticeService.sendNotice(receiver, noticeTempId, deviceName, userName);
		} catch (Exception e) {
			log.info("x\t通知失败{}\treceive:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix, e);
		}
	}

	/**
	 * 设备删除时
	 * 短信:您好,安徽云玺提醒您,贵公司设备${code0}已被管理员${code1}移除,详情请联系管理员咨询
	 * 通知:贵公司设备%s已被管理员%s移除,详情请联系管理员咨询
	 *
	 * @param deviceName 印章名称
	 * @param userName   管理员
	 * @param receiveId  接收人ID
	 * @return
	 */
	@Override
	public void deleteDeviceNotice(String deviceName,
								   String userName,
								   Integer receiveId) {
		//接收人
		User receiver = userService.get(receiveId);
		MessageTemp sq001 = getByCode("SB003");
		if (sq001 != null) {
			Integer smsTempId = sq001.getSmsTempId();
			Integer noticeTempId = sq001.getNoticeTempId();
			smsService.sendSMS(receiver, smsTempId, deviceName, userName);
			noticeService.sendNotice(receiver, noticeTempId, deviceName, userName);
		}
	}


	/**
	 * 审批转交后,通知被转交审批人
	 * 短信:您好,${code0}提醒您,您有一条来自${code1}审批人${code2}转交的审批请求,请及时处理。
	 * 通知:您有一条新的审批转交请求,来自审批人%s,请及时处理
	 *
	 * @param orgName   转交人的组织名称
	 * @param userName  审批人
	 * @param receiveId 接收人ID
	 */
	@Override
	public void transferManagerNotice(String orgName, String userName, Integer receiveId) {
		String tempCode = "SP005";
		String logSuffix = String.format("receiveId:%d\tuserName:%s", receiveId, userName);

		User receiver = userService.get(receiveId);
		if (receiver == null) {
			log.info("x\t消息失败-接收人不存在{}\t{}", tempCode, logSuffix);
			return;
		}

		MessageTemp mt = getByCode(tempCode);
		if (mt == null) {
			log.info("-\t消息忽略-未订阅{}\treceiver:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix);
			return;
		}

		//发送短信
		try {
			Integer smsTempId = mt.getSmsTempId();
			smsService.sendSMS(receiver, smsTempId, properties.getAliyunSms().getSignName(), orgName, userName);
		} catch (Exception e) {
			log.info("x\t短信失败{}\treceive:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix, e);
		}

		//发送通知
		try {
			Integer noticeTempId = mt.getNoticeTempId();
			noticeService.sendNotice(receiver, noticeTempId, userName);
		} catch (Exception e) {
			log.info("x\t通知失败{}\treceive:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix, e);
		}
	}

	/**
	 * 指纹录入成功后,给予录入人提醒
	 * 短信:您好,${code0}提醒您,您的指纹已在${code1}设备录入成功。
	 * 通知:您的指纹已与%s设备绑定,您现在可以使用指纹解锁设备
	 *
	 * @param deviceName 审批人
	 * @param receiveId  接收人ID
	 */
	@Override
	public void addFingerNotice(String deviceName, Integer receiveId) {
		String tempCode = "ZW001";
		String logSuffix = String.format("receiveId:%d\tdeviceName:%s", receiveId, deviceName);

		User receiver = userService.get(receiveId);
		if (receiver == null) {
			log.info("x\t消息失败-接收人不存在{}\t{}", tempCode, logSuffix);
			return;
		}

		MessageTemp mt = getByCode(tempCode);
		if (mt == null) {
			log.info("-\t消息忽略-未订阅{}\treceiver:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix);
			return;
		}

		//发送短信
		try {
			Integer smsTempId = mt.getSmsTempId();
			smsService.sendSMS(receiver, smsTempId, properties.getAliyunSms().getSignName(), deviceName);
		} catch (Exception e) {
			log.info("x\t短信失败{}\treceive:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix, e);
		}

		//发送通知
		try {
			Integer noticeTempId = mt.getNoticeTempId();
			noticeService.sendNotice(receiver, noticeTempId, deviceName);
		} catch (Exception e) {
			log.info("x\t通知失败{}\treceive:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix, e);
		}
	}

	/**
	 * 审计完成后,通知公司管理员
	 * 短信:您好,安徽云玺提醒您,贵公司有新的用章申请${code0},请至云玺App查看
	 * 通知:贵公司有新的用章申请${code0},请至云玺App查看
	 *
	 * @param title     申请人
	 * @param receiveId 接收人ID
	 * @return
	 */
	@Override
	public void auditorOKToBossNotice(String title,
									  Integer receiveId) {
		//接收人
		User receiver = userService.get(receiveId);
		MessageTemp sq001 = getByCode("SJ004");
		if (sq001 != null) {
			Integer smsTempId = sq001.getSmsTempId();
			Integer noticeTempId = sq001.getNoticeTempId();
			smsService.sendSMS(receiver, smsTempId, title);
			noticeService.sendNotice(receiver, noticeTempId, title);
		}
	}

	/**
	 * 设备触发拆卸警告，向管章人发送短信及消息通知
	 * 短信：您好,${code0}提醒您,设备${code1}印章已经出现拆卸报警,请及时处理。
	 * 通知：【%s】对设备【%s】进行拆卸动作,请及时处理
	 *
	 * @param deviceName 设备名称
	 * @param receiveId  管章人ID
	 * @return 通知消息
	 */
	@Override
	public Notice dismantleNotice(String deviceName, Integer receiveId) {
		String tempCode = "CX01";
		String logSuffix = String.format("receiveId:%d\tdeviceName:%s", receiveId, deviceName);

		User receiver = userService.get(receiveId);
		if (receiver == null) {
			log.info("x\t消息失败-接收人不存在{}\t{}", tempCode, logSuffix);
			return null;
		}

		MessageTemp mt = getByCode(tempCode);
		if (mt == null) {
			log.info("-\t消息忽略-未订阅{}\treceiver:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix);
			return null;
		}

		//发送短信
		try {
			Integer smsTempId = mt.getSmsTempId();
			smsService.sendSMS(receiver, smsTempId, properties.getAliyunSms().getSignName(), deviceName);
		} catch (Exception e) {
			log.info("x\t短信失败{}\treceive:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix, e);
		}

		//发送通知
		try {
			Integer noticeTempId = mt.getNoticeTempId();
			return noticeService.sendNotice(receiver, noticeTempId, deviceName);
		} catch (Exception e) {
			log.info("x\t通知失败{}\treceive:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix, e);
		}

		return null;
	}

	/**
	 * 指纹清空成功后,给予管章人提醒
	 * 短信:您好，${code0}提醒您，${code1}设备指纹清空成功。
	 *
	 * @param deviceName 审批人
	 * @param receiveId  接收人ID
	 */
	@Override
	public void clearFingerNotice(String deviceName, Integer receiveId) {
		String tempCode = "ZW002";
		String logSuffix = String.format("receiveId:%d\tdeviceName:%s", receiveId, deviceName);

		User receiver = userService.get(receiveId);
		if (receiver == null) {
			log.info("x\t消息失败-接收人不存在{}\t{}", tempCode, logSuffix);
			return;
		}

		MessageTemp mt = getByCode(tempCode);
		if (mt == null) {
			log.info("-\t消息忽略-未订阅{}\treceiver:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix);
			return;
		}

		//发送短信
		try {
			Integer smsTempId = mt.getSmsTempId();
			smsService.sendSMS(receiver, smsTempId, properties.getAliyunSms().getSignName(), deviceName);
		} catch (Exception e) {
			log.info("x\t短信失败{}\treceive:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix, e);
		}

		//发送通知
		try {
			Integer noticeTempId = mt.getNoticeTempId();
			noticeService.sendNotice(receiver, noticeTempId, deviceName);
		} catch (Exception e) {
			log.info("x\t通知失败{}\treceive:{}\t{}", tempCode, CommonUtils.objJsonWithIgnoreFiled(receiver), logSuffix, e);
		}
	}
}
