package com.yunxi.stamper.entityVo;

import com.yunxi.stamper.commons.enums.ApplicationStatusEnum;
import lombok.Data;

import java.util.Date;
import java.util.Objects;

/**
 * @author zhf_10@163.com
 * @Description 转换类，前端申请单报表申请转换类
 * @date 2021/3/17 16:29
 */
@Data
public class DtoApplicationForm {
	private Integer id;            //申请单ID
	private Date createDate;    //申请时间
	private Integer totalCount;    //申请次数
	private String managerUsername;    //审批人名称
	private String keeperUsername;    //授权人名称
	private String auditorUsername;    //审计人名称
	private String applicationUsername;    //申请人名称
	private String title;    //申请单标题
	private String content;    //申请单描述
	private String deviceName;    //设备名称
	private String useCount;    //已用次数
	private Integer status;    //申请单状态

	public String getStatus() {
		String msg = null;
		for (ApplicationStatusEnum applicationStatusEnum : ApplicationStatusEnum.values()) {
			if (Objects.equals(applicationStatusEnum.getCode(), this.status)) {
				msg = applicationStatusEnum.getMsg();
				break;
			}
		}
		return msg;
	}
}
