package com.yunxi.stamper.entityVo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/12/27 0027 14:25
 */
@Setter
@Getter
public class ReportEntity {
	private Integer id;    //报表记录ID
	private Date createDate;    //申请时间
	private String fileName;    //报表名称
	private Integer status;        //报表生成状态
	private String fileUrl;        //报表下载链接
	private String host;    //报表所在服务器host


}
