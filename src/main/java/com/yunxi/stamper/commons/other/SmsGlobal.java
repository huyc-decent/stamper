package com.yunxi.stamper.commons.other;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/4/24 0024 19:50
 */
public class SmsGlobal {
	/**
	 * 阿里云短信
	 */
	public static final String ALIYUN_ACCESSKEYID = "LTAIhb8W3FQHxYK1";//阿里云KeyId
	public static final String ALIYUN_ACCESSKEYSECRET = "q1Hqv2NvdExlsG9uIhuV4PgY23qDZW";//阿里云secret

	public static final String ALIYUN_CONNECTTIMEOUT = "1000";//链接超时时间
	public static final String ALIYUN_READTIMEOUT = "1000";//超时时间，单位：毫秒
	public static final String ALIYUN_SIGNNAME = "安徽云玺";//短信签名
	public static final String ALIYUN_TEMPLATE_PHONEVERCODE = "SMS_165678514";//手机验证码模板
	public static final String ALIYUN_OUTID = "";  //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者

	/**
	 * 短信验证码参数
	 */
	public static final int SMS_COUNT = 15; 					// 同一手机号登录短信一天发送次数
	public static final int SMS_TIME_OUT = 300; 				// 同一手机号登录短信一天发送次数
	public static final int SMS_MAX_CODE = 100000; 				// 5位短信验证码
	public static final String SMS_SIGNNAME = "安徽云玺";		//手机短信签名
	public static final String SMS_TEMPLATECODE = "SMS_121160795";//申请的短信模板编码
	public static final int freHit = 6;							//推送失败的通知+发送失败的短信 重复发送次数值

	/**
	 * 短信发送状态码
	 * -1:未发送 0:发送成功 1:发送失败
	 */
	public static final int SEND_OK = 0;
	public static final int SEND_FAIL = 1;
	public static final int SEND_NO = -1;

}
