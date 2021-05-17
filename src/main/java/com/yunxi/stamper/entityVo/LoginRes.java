package com.yunxi.stamper.entityVo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/10/25 0025 11:16
 */
@Setter
@Getter
public class LoginRes {
	private String userName;//用户名
	private String loginName;//登录名(当前版本登录名不存在)
	private String phone;//手机号
	private String token;//本次登录令牌
	private String headImg;//用户头像url
	private FileEntity logo;//所属公司LOGO
	private String orgName;//公司名称
	private int type;//用户类型
	private int userId;//用户ID
	private int orgId;//公司ID
	private int qss;//量子加密服务  0:没有 1:有
	private boolean oos = false;//对象存储服务  true:有  false:没有

}
