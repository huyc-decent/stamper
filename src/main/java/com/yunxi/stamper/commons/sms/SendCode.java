package com.yunxi.stamper.commons.sms;

/**
 * @author zhf_10@163.com
 * @Description 发送消息回执状态码
 * @date 2018/11/22 0022 13:53
 */
public enum SendCode {
    OK("OK","请求成功"),
    FAILE("FAILE","请求失败"),
    RAM_PERMISSION_DENY("FAILE","RAM权限DENY"),
    OUT_OF_SERVICE("FAILE","业务停机"),
    PRODUCT_UN_SUBSCRIPT("FAILE","未开通云通信产品的阿里云客户"),
    PRODUCT_UNSUBSCRIBE("FAILE","产品未开通"),
    ACCOUNT_NOT_EXISTS("FAILE","账户不存在"),
    ACCOUNT_ABNORMAL("FAILE","账户异常"),
    SMS_TEMPLATE_ILLEGAL("FAILE","短信模板不合法"),
    SMS_SIGNATURE_ILLEGAL("FAILE","短信签名不合法"),
    INVALID_PARAMETERS("FAILE","参数异常"),
    SYSTEM_ERROR("FAILE","系统错误"),
    MOBILE_NUMBER_ILLEGAL("FAILE","非法手机号"),
    MOBILE_COUNT_OVER_LIMIT("FAILE","手机号码数量超过限制"),
    TEMPLATE_MISSING_PARAMETERS("FAILE","模板缺少变量"),
    BUSINESS_LIMIT_CONTROL("FAILE","业务限流"),
    INVALID_JSON_PARAM("FAILE","JSON参数不合法，只接受字符串值"),
    BLACK_KEY_CONTROL_LIMIT("FAILE","黑名单管控"),
    PARAM_LENGTH_LIMIT("FAILE","参数超出长度限制"),
    PARAM_NOT_SUPPORT_URL("FAILE","不支持URL"),
    AMOUNT_NOT_ENOUGH("FAILE","账户余额不足");


//    public static void main(String[] args) {
//        //String ok = SendCode.valueOf("AMOUNT_NOT_ENOUGH").getCode();//FAILE
//		SendCode sendCode = SendCode.valueOf("OK");
//		System.out.println(sendCode.getMsg());//请求成功
//		String msg = getMsgByCode(sendCode);//请求成功
//		System.out.println(msg);
//		String ok = SendCode.valueOf("isv.BUSINESS_LIMIT_CONTROL").getMsg();//No enum constant com.qyunxi.common.utils.sms.SendCode.isv.BUSINESS_LIMIT_CONTROL
//        System.out.println(ok);
//    }
    //code
    private String code;
    //消息
    private String msg;

    private SendCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static String getMsgByCode(SendCode sendCode) {
        String msg = "";
		String code = sendCode.getCode();
		for (SendCode result : SendCode.values()) {
            if (code.equals(result.getCode())) {
                msg = result.getMsg();
                break;
            }
        }
        return msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
