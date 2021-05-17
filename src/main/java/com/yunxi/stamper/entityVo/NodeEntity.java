package com.yunxi.stamper.entityVo;

import com.yunxi.stamper.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/12/5 0005 14:54
 */
@Setter
@Getter
public class NodeEntity {
	private String type;//当前节点审批类型 list:依次审批  and:会签  or:或签 manager:主管审批 optionl:自选
	private List<User> managers = new LinkedList<>();//当前节点审批人列表
	private int level;//主管审批层级

}
