package com.yunxi.stamper.entityVo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/16 0016 17:48
 */
@Setter
@Getter
public class PushApplication {
	private Integer applicationId;//申请单id
	private String title;//申请单标题
	private Integer isQss;//加密类型
	private Integer totalCount;//总次数
	private Integer needCount;//已盖次数
	private Integer useCount;//剩余盖章次数
	private Integer signetId;//印章id
	private String userName;//申请人姓名
	private Integer userId;//申请人id

}
