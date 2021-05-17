package com.yunxi.stamper.controller;

import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.DisassemblyRecordInfo;
import com.yunxi.stamper.entity.Signet;
import com.yunxi.stamper.entityVo.DisassemblyRecordInfoEntity;
import com.yunxi.stamper.entityVo.FileEntity;
import com.yunxi.stamper.service.DisassemblyRecordInfoService;
import com.yunxi.stamper.service.FileInfoService;
import com.yunxi.stamper.service.SignetService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhf_10@163.com
 * @Description 拆卸记录控制层
 * @date 2020/3/3 0003 16:44
 */
@Slf4j
@Api(tags = "拆卸记录相关")
@RestController
@RequestMapping(value = "/device/disassemblyRecordInfo", method = {RequestMethod.POST, RequestMethod.GET})
public class DisassemblyRecordInfoController extends BaseController {

	@Autowired
	private DisassemblyRecordInfoService service;
	@Autowired
	private FileInfoService fileInfoService;
	@Autowired
	private SignetService signetService;

	/**
	 * 查询该通知消息对应的'设备拆卸记录'详情
	 *
	 * @param noticeId 通知ID
	 * @return 结果
	 */
	@GetMapping("/getInfoByNoticeId")
	public ResultVO getInfoByNoticeId(@RequestParam("noticeId") Integer noticeId) {
		//查询拆卸记录详情
		DisassemblyRecordInfo info = service.getInfoByNoticeId(noticeId);

		//查询设备名称
		String deivceName = null;
		Integer deviceId = info.getDeviceId();
		if (deviceId != null) {
			Signet signet = signetService.get(deviceId);
			if (signet != null) {
				deivceName = signet.getName();
			}
		}

		//查询对应的附件
		FileEntity fileEntity = fileInfoService.getReduceImgURLByFileId(info.getAesFileInfoId());
		if (fileEntity != null) {
			DisassemblyRecordInfoEntity entity = new DisassemblyRecordInfoEntity();
			BeanUtils.copyProperties(info, entity);
			entity.setFileEntity(fileEntity);
			entity.setDeviceName(deivceName);
			return ResultVO.OK(entity);
		}

		return ResultVO.OK(info);
	}

}
