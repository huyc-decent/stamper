package com.yunxi.stamper.entityVo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/6/6 0006 14:56
 */
@Slf4j
@Data
public class FlowVoAddEntity {
	private List<FlowVoAddEntityKV> user;//当前节点审批人列表
	private boolean visible = false;
	private String title = "审批";
	private String type;
	private String managerJson;

	public List<FlowVoAddEntityKV> getUser() {
		if (StringUtils.isNotBlank(getManagerJson())) {
			try {
				return JSONObject.parseObject(getManagerJson(), ArrayList.class);
			} catch (Exception e) {
				log.error("出现异常 ", e);
			}
		}
		return user;
	}
}
