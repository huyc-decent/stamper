package com.yunxi.stamper.demoController;

import com.yunxi.stamper.commons.other.DateUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * @author zhf_10@163.com
 * @Description
 * @date 2020/11/4 17:38
 */
@Data
public class QssDemoEntity implements Serializable {
	private String time;        //时间
	private String type = "string";    //消息类型 string:字符串  url:网址
	private String message;    //消息内容

	private static final long serialVersionUID = 1L;

	public QssDemoEntity(String message) {
		this.time = DateUtil.format(new Date());
		this.message = message;
	}

	public QssDemoEntity(String type, String message) {
		this.time = DateUtil.format(new Date());
		this.type = type;
		this.message = message;
	}
}
