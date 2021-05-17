package com.yunxi.stamper.commons.sms;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.yunxi.stamper.sys.config.ProjectProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhf_10@163.com
 * @Description 短信发送工具类
 * @date 2018/11/22 0022 10:35
 */
@Slf4j
@Component
public class SmsUitls {

	private static final Logger LOGGER = LoggerFactory.getLogger(SmsUitls.class);
	private static final String ALIYUN_TEMPLATE_PHONEVERCODE = "SMS_165678514";//手机验证码模板
	public static final String ALIYUN_CONNECTTIMEOUT = "1000";//链接超时时间
	public static final String ALIYUN_READTIMEOUT = "1000";//超时时间，单位：毫秒

	private static ProjectProperties properties;

	//产品名称:云通信短信API产品,开发者无需替换
	static final String product = "Dysmsapi";
	//产品域名,开发者无需替换
	static final String domain = "dysmsapi.aliyuncs.com";

//	public static void main(String[] args) {
//		SendCode sendCode = sendNotice("18297999241", "SMS_211980059", "互联云玺", "印章11301");
//	}


	/**
	 * 发送短信通知提醒
	 * ps示例:
	 * 1. 参数  18297999241  SMS_165678537   null
	 * 发送短信为:您好,安徽云玺提醒您,有一条新的审批请求,请及时审批并处理
	 * 2. 参数  18297999241	SMS_166080509	测试用章
	 * 发送短信为:您好,安徽云玺提醒您,测试用章用章申请已被审批驳回,请注意查看
	 *
	 * @param phone 手机号
	 * @param code  短信模板编码
	 * @param args  模板需要参数
	 * @return
	 */
	public static SendCode sendNotice(String phone, String code, String... args) {
		//发送短信
		SendSmsResponse sendSmsResponse = null;
		try {
			if (StringUtils.isAnyBlank(phone, code)) {
				log.info("短信发送失败 手机号:【{}】 短信模板:【{}】 模板参数:【{}】", phone, code, Arrays.toString(args));
				return SendCode.FAILE;
			}
			try {
				String resultParams = null;
				if (args != null && args.length > 0) {
					Map<String, String> params = new HashMap<>(args.length);
					for (int i = 0; i < args.length; i++) {
						String arg = args[i];
						if (StringUtils.isNotBlank(arg) && arg.length() > 16) {
							arg = arg.substring(0, 16) + "..";
						}
						params.put("code" + i, arg);
					}
					resultParams = JSONObject.toJSONString(params);
				}
				sendSmsResponse = sendSms(phone, code, resultParams);
				String smsCode = sendSmsResponse.getCode();
				return SendCode.valueOf(smsCode);
			} catch (ClientException e) {
				log.error("出现异常 ", e);
				log.error("短信发送异常 手机号:【{}】 短信模板:【{}】 模板参数:【{}】  异常：【{}】", phone, code, Arrays.toString(args), e.getMessage());
			}
			return SendCode.FAILE;
		} catch (Exception e) {
			log.error("出现异常 ", e);
			return SendCode.FAILE;
		} finally {
			log.info("短信发送回执 手机号:【{}】 短信模板:【{}】", phone, code);
		}
	}


	/**
	 * 发送手机验证码
	 *
	 * @param phoneNumber  手机号
	 * @param identifyCode 验证码
	 * @return
	 */
	public static SendCode SendPhoneVerifyCode(String phoneNumber, String identifyCode) {
		if (StringUtils.isAnyBlank(phoneNumber, identifyCode)) {
			return SendCode.FAILE;
		}
		Map<String, String> paramMap = new HashMap<>();
		paramMap.put("code", identifyCode);

		//发送短信
		SendSmsResponse sendSmsResponse = null;
		try {
			String param = "{\"code\":\"" + paramMap.get("code") + "\"}";
			sendSmsResponse = sendSms(phoneNumber, ALIYUN_TEMPLATE_PHONEVERCODE, param);
		} catch (ClientException e) {
			log.error("出现异常 ", e);
		}

		if (sendSmsResponse == null) {
			return SendCode.FAILE;
		}
		String code = sendSmsResponse.getCode();
		int index = code.lastIndexOf('.');
		if (index > 0) {
			code = code.substring(index + 1);
		}
		LOGGER.info("手机短信回执--->" + sendSmsResponse.getMessage());
		return SendCode.valueOf(code);
	}


	/**
	 * 真正发送短信的方法
	 *
	 * @param phoneNumber   接收短信的手机号码
	 * @param templateCode  短信模板
	 * @param templateParam 模板参数
	 * @return
	 */
	private static SendSmsResponse sendSms(String phoneNumber, String templateCode, String templateParam) throws ClientException {
		//可自助调整超时时间
		System.setProperty("sun.net.client.defaultConnectTimeout", ALIYUN_CONNECTTIMEOUT);
		System.setProperty("sun.net.client.defaultReadTimeout", ALIYUN_READTIMEOUT);

		//初始化acsClient,暂不支持region化
		IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", properties.getAliyunSms().getAccessKeyId(), properties.getAliyunSms().getAccessKeySecret());
//		IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAIhb8W3FQHxYK1", "q1Hqv2NvdExlsG9uIhuV4PgY23qDZW");
		DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
		IAcsClient acsClient = new DefaultAcsClient(profile);

		//组装请求对象-具体描述见控制台-文档部分内容
		SendSmsRequest request = new SendSmsRequest();
		//必填:待发送手机号
		request.setPhoneNumbers(phoneNumber);
		//必填:短信签名-可在短信控制台中找到
		request.setSignName(properties.getAliyunSms().getSignName());
//		request.setSignName("互联云玺");
		//必填:短信模板-可在短信控制台中找到
		request.setTemplateCode(templateCode);

		if (StringUtils.isNotBlank(templateParam)) {
			//可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
			request.setTemplateParam(templateParam);
		}

		//选填-上行短信扩展码(无特殊需求用户请忽略此字段)
		//request.setSmsUpExtendCode("90997");

//		if (StringUtils.isNotBlank(SmsGlobal.ALIYUN_OUTID)) {
//			//可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
//			request.setOutId(SmsGlobal.ALIYUN_OUTID);
//		}

		//hint 此处可能会抛出异常，注意catch
		SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);

		return sendSmsResponse;
	}


	public static QuerySendDetailsResponse querySendDetails(String bizId) throws ClientException {

		//可自助调整超时时间
		System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
		System.setProperty("sun.net.client.defaultReadTimeout", "10000");

		//初始化acsClient,暂不支持region化
		IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", properties.getAliyunSms().getAccessKeyId(), properties.getAliyunSms().getAccessKeySecret());
		DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
		IAcsClient acsClient = new DefaultAcsClient(profile);

		//组装请求对象
		QuerySendDetailsRequest request = new QuerySendDetailsRequest();
		//必填-号码
		request.setPhoneNumber("15000000000");
		//可选-流水号
		request.setBizId(bizId);
		//必填-发送日期 支持30天内记录查询，格式yyyyMMdd
		SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
		request.setSendDate(ft.format(new Date()));
		//必填-页大小
		request.setPageSize(10L);
		//必填-当前页码从1开始计数
		request.setCurrentPage(1L);

		//hint 此处可能会抛出异常，注意catch
		QuerySendDetailsResponse querySendDetailsResponse = acsClient.getAcsResponse(request);

		return querySendDetailsResponse;
	}

	@Autowired
	public void setProperties(ProjectProperties properties) {
		SmsUitls.properties = properties;
	}
}
