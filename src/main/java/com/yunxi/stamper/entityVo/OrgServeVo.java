package com.yunxi.stamper.entityVo;

import com.yunxi.stamper.entity.Org;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/23 0023 17:24
 */
@Setter
@Getter
public class OrgServeVo extends Org {
	private boolean isSMS = false;//短信服务是否开通
	private boolean isBase = false;//设备基础服务
	private boolean isQss = false;//量子服务
	private boolean isOos = false;//天翼云存储
	private Double spaceUsage = null;//已使用容量
}
