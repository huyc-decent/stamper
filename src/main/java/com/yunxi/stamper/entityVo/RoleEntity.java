package com.yunxi.stamper.entityVo;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/12/4 0004 10:42
 */
@Setter
@Getter
public class RoleEntity {
	private Integer id;
	private String name;
	private String remark;
	private String code;
	private Integer orgId;
	private Date createDate;
	private String createName;
	private Date updateDate;
	private String updateName;
	private boolean writer = false;

	public boolean isWriter() {
		return StringUtils.isNotBlank(code) && !"admin,user,manager,keeper,auditor".contains(code);
	}

	public String getCreateName() {
		if (StringUtils.isBlank(createName) && !writer) {
			return "系统默认";
		}
		return createName;
	}

}
