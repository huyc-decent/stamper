package com.yunxi.stamper.demoController;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/8/8 0008 12:45
 */
@Setter
@Getter
public class DemoEntity {
	private Integer deviceId;
	private List<QssDemoEntity> message = new LinkedList<>();//消息内容
	public void add(QssDemoEntity entity) {
		this.message.add(entity);
	}
}
