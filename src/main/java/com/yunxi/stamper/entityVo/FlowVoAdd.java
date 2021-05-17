package com.yunxi.stamper.entityVo;

import com.yunxi.stamper.entity.Flow;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/6/6 0006 14:55
 */
@Data
public class FlowVoAdd{
	private String name;
	private String remark;
	private Integer orgId;
	private Integer userId;
	private String userName;
	private Integer status;
	private List<FlowVoAddEntity> flowNodeList;//审批流程节点列表
	private List<Integer> departmentIds;//所属组织列表
}
