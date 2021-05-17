package com.yunxi.stamper.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.other.DateUtil;
import com.yunxi.stamper.commons.other.SmsGlobal;
import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.commons.sms.SendCode;
import com.yunxi.stamper.commons.sms.SmsUitls;
import com.yunxi.stamper.entity.*;
import com.yunxi.stamper.mapper.SmsMapper;
import com.yunxi.stamper.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/14 0014 23:31
 */
@Slf4j
@Service
public class ISMSService implements SMSService {
	@Autowired
	private SmsMapper mapper;
	@Autowired
	private SmsTempService smsTempService;
	@Autowired
	private OrgServeService orgServeService;
	@Autowired
	private OrgSmsTempService orgSmsTempService;
	@Autowired
	private SysUserService sysUserService;

	/**
	 * 发送短信
	 *
	 * @param receive   接收人
	 * @param smsTempId 短信模板id
	 * @param args      模板所需参数
	 */
	@Override
	public void sendSMS(User receive, Integer smsTempId, String... args) {
		if (!CommonUtils.properties.getAliyunSms().isEnabled()) {
			return;
		}

		if (receive == null) {
			log.error("X\t短信发送失败\terror:接收人不存在\ttempId:{}\targs:{}", smsTempId, (args == null || args.length == 0) ? null : Arrays.toString(args));
			return;
		}
		SmsTemp st = smsTempService.get(smsTempId);
		if (st == null) {
			log.error("X\t短信发送失败\terror:模板不存在\treceive:{}\ttempId:{}\targs:{}", receive.getId() + "_" + receive.getUserName(), smsTempId, (args == null || args.length == 0) ? null : Arrays.toString(args));
			return;
		}

		//该公司是否有短信服务
		OrgServe orgServe = orgServeService.getByOrgAndCode(receive.getOrgId(), "SMS");
		if (orgServe == null) {
			//没有短信服务，不发送
			log.error("X\t短信发送失败\terror:无短信服务\treceive:{}\ttempId:{}\targs:{}", receive.getId() + "_" + receive.getUserName(), smsTempId, (args == null || args.length == 0) ? null : Arrays.toString(args));
			return;
		}

		//该公司是否有配置该短信模板
		OrgSmsTemp ost = orgSmsTempService.getByOrgAndSmstemp(receive.getOrgId(), st.getId());
		if (ost == null) {
			//没有配置,不用发短信
			log.error("X\t短信发送失败\terror:未订阅模板\treceive:{}\ttempId:{}\targs:{}", receive.getId() + "_" + receive.getUserName(), smsTempId, (args == null || args.length == 0) ? null : Arrays.toString(args));
			return;
		}

		try {
			String phone = receive.getPhone();
			Integer sysUserId = receive.getSysUserId();
			SysUser sysUser = sysUserService.get(sysUserId);
			if (sysUser != null) {
				phone = sysUser.getPhone();
			}
			String code = st.getCode();

			Sms sms = new Sms();
			sms.setPhone(phone);
			sms.setReceiveId(receive.getId());
			sms.setTitle(st.getName());
			String content = getContent(st, args);
			sms.setContent(content);
			sms.setSmsCode(code);
			sms.setSmsArgs(JSONObject.toJSONString(args));

			//开始发送
			SendCode sendCode = SmsUitls.sendNotice(phone, code, args);
			sms.setTimes(1);
			if (StringUtils.isNotBlank(phone)) {
				if ("OK".equalsIgnoreCase(sendCode.getCode())) {
					//短信发送成功
					sms.setStatus(SmsGlobal.SEND_OK);
					sms.setError("短信发送成功");
					log.info("√\t短信发送-成功\tphone:{}\ttitle:{}\tcontent:{}", phone, st.getName(), content);
				} else {
					//短信发送失败
					sms.setStatus(SmsGlobal.SEND_FAIL);
					sms.setError(sendCode.getMsg());
					sms.setPlanTime(DateUtil.nextTime(new Date(), 5));//接下来的5分钟内进行补发
					log.info("x\t短信发送-失败\tphone:{}\ttitle:{}\tcontent:{}\terror:{}", phone, st.getName(), content, sendCode.getMsg());
				}
			} else {
				sms.setStatus(SmsGlobal.SEND_NO);
				sms.setError("用户手机号码不存在$" + JSONObject.toJSONString(receive) + "$");
				sms.setPlanTime(DateUtil.nextTime(new Date(), 5));//接下来的5分钟内进行补发
				log.info("x\t短信发送-失败\tphone:{}\ttitle:{}\tcontent:{}\terror:目标手机号码不存在", phone, st.getName(), content);
			}

			add(sms);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据参数和模板内容,组装短信内容存储数据库
	 *
	 * @param temp 模板内容
	 * @param args 参数
	 * @return 短信内容
	 */
	private String getContent(SmsTemp temp, Object[] args) {
		String content = temp.getContent();
		for (int i = 0; i < args.length; i++) {
			Object arg = args[i];
			content = content.replaceFirst("code" + i, arg.toString());
		}
		return content;
	}

	@Override
	@Transactional
	public void add(Sms sms) {
		int addCount = 0;
		if (sms != null) {
			sms.setCreateDate(new Date());
			addCount = mapper.insert(sms);
		}
		if (addCount != 1) {
			throw new PrintException("短信消息添加失败");
		}
	}

	@Override
	public Sms get(Integer smsId) {
		if (smsId != null) {
			return mapper.selectByPrimaryKey(smsId);
		}
		return null;
	}

	/**
	 * 查询大于指定时间的短信列表
	 *
	 * @param date 时间
	 * @return 短信列表
	 */
	@Override
	public List<Sms> getGreaterThanAndSendError(Date date) {
		if (date != null) {
			return mapper.selectByGreaterThanAndSendError(date);
		}
		return null;
	}

	@Override
	@Transactional
	public void update(Sms sms) {
		mapper.updateByPrimaryKeySelective(sms);
	}
}
