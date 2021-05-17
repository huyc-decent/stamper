package com.yunxi.stamper.entityVo;



import com.yunxi.stamper.entity.Org;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/6/14 0014 11:18
 */
@Setter
@Getter
public class OrgStructure extends Org {
	private List<DepartmentVo> childrens;//子公司、部门
	private int type=0;//组织类型 公司、部门
	private String managerUserName;

}
