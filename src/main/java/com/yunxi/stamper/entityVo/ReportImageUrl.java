package com.yunxi.stamper.entityVo;

import lombok.Data;

/**
 * @author zhf_10@163.com
 * @Description 报表图片对象信息
 * @date 2020/11/4 11:30
 */
@Data
public class ReportImageUrl {

	private String url;        //图片地址
	private String type;    //图片类型

	public ReportImageUrl() {
	}

	public ReportImageUrl(String url, String type) {
		this.url = url;
		this.type = type;
	}
}
