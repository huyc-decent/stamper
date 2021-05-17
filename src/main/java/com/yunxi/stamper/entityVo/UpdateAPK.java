package com.yunxi.stamper.entityVo;

import lombok.Data;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/7/2 0002 15:01
 */
@Data
public class UpdateAPK {
	private float version;//最新版本号
	private String url;//更新该版本的URL地址
}
