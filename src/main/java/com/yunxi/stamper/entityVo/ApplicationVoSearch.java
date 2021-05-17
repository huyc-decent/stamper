package com.yunxi.stamper.entityVo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/20 0020 19:46
 */
@Setter
@Getter
public class ApplicationVoSearch {
	private Integer applicationId;//申请单id
	private String title;//申请单标题

	/**
	 * 以下参数仅供'我的申请-搜索功能'使用
	 */
	private Integer type;//0 待完结  1:已完结
	private String keyword;//搜索关键词
	private Integer status;//申请单状态
	private Date[] date;//搜索时间区间 date[0]:开始时间  date[1]:结束时间
	private Date start;//开始时间
	private Date end;//结束时间
	private Integer userId;//发起搜索用户的id
	private Integer pageNum = 1;
	private Integer pageSize = 10;
	private boolean isPage = false;

	public void setDate(Date[] date) {
		this.date = date;
		if (date != null && date.length > 0) {
			if (date.length == 1) {
				this.start = date[0];
			}
			if (date.length == 2) {
				this.start = date[0];
				this.end = date[1];
			}
		}
	}
}
