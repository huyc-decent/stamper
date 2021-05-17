package com.yunxi.stamper.entityVo;


import com.yunxi.stamper.entity.Org;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/11 0011 11:29
 */
@Setter
@Getter
public class OrgVo3 extends Org {
	private List<OrgVo3> childrensOrgs;//子公司

	private List<DepartmentVo> childrenDepartMents;//子部门列表

}
