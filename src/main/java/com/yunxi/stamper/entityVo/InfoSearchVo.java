package com.yunxi.stamper.entityVo;



import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author zhf_10@163.com
 * @Description 查询印章使用记录搜索实体类
 * @date 2019/7/29 0029 16:24
 */
@Setter
@Getter
public class InfoSearchVo {

	//用印人名称
	private String userName;

	//印章id ‘印章管理’->‘使用记录’功能专用参数
	private Integer signetId;

	//申请单id  ‘审批处理’、‘授权处理’、‘审计处理’--->'查看'-->'使用记录'功能专用
	private Integer applicationId;

	//使用记录类型
	private Integer type;

	//使用记录状态
	private Integer error;

	//使用次数
	private Integer useCount;

	//地址
	private String location;

	//时间信息
	private Date[] date;
	private Date start;//开始时间
	private Date end;//结束时间

	//分页信息
	private Integer pageNum = 1;
	private Integer pageSize = 10;
	private boolean page = false;

	private UserInfo userInfo;

	public void setDate(Date[] date) {
		this.date = date;
		if (date != null) {
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
