package com.yunxi.stamper.entityVo;

import com.yunxi.stamper.entity.SmsTemp;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/23 0023 16:09
 */
public class SMSVoSelect extends SmsTemp{
	private boolean isUse = false;//短信模板是否启用

	public boolean isUse() {
		return isUse;
	}

	public void setUse(boolean use) {
		isUse = use;
	}
}
