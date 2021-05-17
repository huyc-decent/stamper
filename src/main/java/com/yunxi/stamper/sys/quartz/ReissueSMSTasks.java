package com.yunxi.stamper.sys.quartz;

import com.alibaba.fastjson.JSONObject;
import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.other.DateUtil;
import com.yunxi.stamper.commons.other.SmsGlobal;
import com.yunxi.stamper.commons.sms.SendCode;
import com.yunxi.stamper.commons.sms.SmsUitls;
import com.yunxi.stamper.entity.Sms;
import com.yunxi.stamper.entity.SysUser;
import com.yunxi.stamper.entity.User;
import com.yunxi.stamper.service.SMSService;
import com.yunxi.stamper.service.SysUserService;
import com.yunxi.stamper.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * springboot简单定时任务
 */
@Slf4j
@Component
@Configurable
@EnableScheduling   //启动定时任务
public class ReissueSMSTasks {

	@Autowired
	private SMSService smsService;
	@Autowired
	private UserService userService;
	@Autowired
	private SysUserService sysUserService;

	/**
	 * 每1分钟查询1次,补发失败的短信
	 */
	@Scheduled(fixedRate = 1000 * 60)
	public void reissueSMS() {
		if (!CommonUtils.getProperties().getAliyunSms().isReissuedEnabled()) {
			return;
		}

		List<Sms> smsList = smsService.getGreaterThanAndSendError(new Date());
		if (smsList == null || smsList.isEmpty()) {
			return;
		}

		int successNum = 0;
		int errorNum = 0;
		int total = smsList.size();

		for (Sms sms : smsList) {
			String phone = sms.getPhone();
			if (StringUtils.isBlank(phone)) {
				//查询该用户手机号码
				Integer receiveId = sms.getReceiveId();
				User user = userService.get(receiveId);
				if (user == null) {
					continue;
				}
				SysUser sysUser = sysUserService.get(user.getSysUserId());
				if (sysUser == null) {
					continue;
				}
				if (StringUtils.isBlank(sysUser.getPhone())) {
					continue;
				}
				phone = sysUser.getPhone();
			}

			if (StringUtils.isNotBlank(phone) && StringUtils.isNotBlank(sms.getSmsCode())) {
				Integer times = sms.getTimes();
				List<String> args = JSONObject.parseArray(sms.getSmsArgs(), String.class);

				SendCode sendCode = SmsUitls.sendNotice(phone, sms.getSmsCode(), args.toArray(new String[1]));

				times++;
				sms.setPhone(phone);
				sms.setTimes(times);
				if (StringUtils.isNotBlank(phone)) {
					if ("OK".equalsIgnoreCase(sendCode.getCode())) {
						//短信发送成功
						sms.setStatus(SmsGlobal.SEND_OK);
						sms.setError("√短信补发成功");

						log.info("√短信补发成功√ 手机号:【{}】 短信内容:【{}】", phone, sms.getContent());
						successNum++;
					} else {
						//短信发送失败
						sms.setStatus(SmsGlobal.SEND_FAIL);
						sms.setError(sendCode.getMsg());

						//接下来的5分钟内进行补发
						if (sms.getPlanTime() == null) {
							sms.setPlanTime(DateUtil.nextTime(new Date(), 5));
						}
						log.info("×短信补发失败1× 手机号:【{}】 短信内容:【{}】", phone, sms.getContent());
						errorNum++;
					}
				} else {
					sms.setStatus(SmsGlobal.SEND_NO);
					sms.setError("用户手机号码不存在$" + JSONObject.toJSONString(sms.getReceiveId()) + "$");

					//接下来的5分钟内进行补发
					if (sms.getPlanTime() == null) {
						sms.setPlanTime(DateUtil.nextTime(new Date(), 5));
					}

					log.info("×短信补发失败× 手机号:【{}】 短信内容:【{}】原因：【用户手机号码不存在】", phone, sms.getContent());
					errorNum++;
				}

				smsService.update(sms);
			}
		}

		log.info("*********************补发短信 时间：{} 总数：{} 成功：{}  失败：{}*********************", DateUtil.format(new Date()), total, successNum, errorNum);
	}
}
