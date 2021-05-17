package com.yunxi.stamper.entityVo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/16 0016 18:09
 */
@Setter
@Getter
public class EndApplication {
	private Integer applicationId;//申请单id
	private String title;//申请单标题
	private Integer deviceId;//设备id
	private String userName;//推送人姓名
	private Integer userId;//推送人id

}
