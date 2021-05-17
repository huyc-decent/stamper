package com.yunxi.stamper.entityVo;

import com.yunxi.stamper.entity.DisassemblyRecordInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2020/3/3 0003 16:49
 */
@Setter
@Getter
public class DisassemblyRecordInfoEntity extends DisassemblyRecordInfo {

	private String deviceName;
	private FileEntity fileEntity;
}
