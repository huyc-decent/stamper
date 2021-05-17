package com.yunxi.stamper.entityVo;

import com.yunxi.stamper.entity.MessageTemp;
import com.yunxi.stamper.entity.NoticeTemp;
import com.yunxi.stamper.entity.SmsTemp;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/27 0027 18:04
 */
@Setter
@Getter
public class MessageTempVo extends MessageTemp {
	private SmsTemp smsTemp;
	private NoticeTemp noticeTemp;

}
