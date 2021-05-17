package com.yunxi.stamper.entityVo;

import com.yunxi.stamper.entity.Flow;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/7/10 0010 16:46
 */
@Setter
@Getter
public class FlowVoSelect extends Flow {
	private Set<String> managers = new HashSet<>();//审批人列表
	private Set<String> departmentNames = new HashSet<>();//所属组织名称列表
}
