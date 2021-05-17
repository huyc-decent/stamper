package com.yunxi.stamper.entityVo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/11/29 0029 19:18
 */
@Setter
@Getter
public class OrganizationalTreeNode {
	private int id;//组织、企业、团队ID
	private String name;//组织名称
	private FileEntity logo;//组织LOGO
	private String code;//组织编码
	private Integer parentId;//父组织ID
	private String parentName;//父组织名称
	private Integer parentType;//父组织类型
	private int type;//组织类型 0:部门 1：公司 2：集团公司
	private Integer managerUserId;//负责人ID
	private String managerUserName;//负责人名称
	private Integer positionId;//负责人称谓ID
	private String positionName;//负责人称谓名称
	private Date createDate;//组织创建时间
	private String remark;//组织描述
	private String location;//组织地址

}
