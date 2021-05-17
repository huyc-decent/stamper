package com.yunxi.stamper.entityVo;

import com.yunxi.stamper.entity.ApplicationNode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/6/19 0019 15:22
 */
@Setter
@Getter
public class ApplicationNodeVo extends ApplicationNode {
	private List<String> users = new ArrayList<>();//处理人名称列表
	private List<String> positions = new ArrayList<>();//处理人称谓
	private String content;//展示内容
	private String suggest;//意见
	private Date time;//处理时间
}
