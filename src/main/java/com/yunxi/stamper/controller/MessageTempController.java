package com.yunxi.stamper.controller;

import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.response.Code;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.MessageTemp;
import com.yunxi.stamper.entity.NoticeTemp;
import com.yunxi.stamper.entity.SmsTemp;
import com.yunxi.stamper.entityVo.MessageTempVo;
import com.yunxi.stamper.entityVo.UserInfo;
import com.yunxi.stamper.service.MessageTempService;
import com.yunxi.stamper.service.NoticeTempService;
import com.yunxi.stamper.service.SmsTempService;
import com.yunxi.stamper.sys.aop.annotaion.WebLogger;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description 消息系统控制层
 * @date 2019/5/23 0023 11:50
 */
@Slf4j
@Api(tags = "消息(短信)相关")
@RestController
@RequestMapping(value = "/auth/messageTemp", method = {RequestMethod.POST, RequestMethod.GET})
public class MessageTempController extends BaseController {

	@Autowired
	private MessageTempService service;
	@Autowired
	private NoticeTempService noticeTempService;
	@Autowired
	private SmsTempService smsTempService;

	/**
	 * 更新 短信/通知模板配置
	 */
	@WebLogger("更新短信/通知模板配置")
	@RequestMapping("/update")
	public ResultVO update(Integer id, String title, String remark, String code, Integer smsTempId, Integer noticeTempId) {
		MessageTemp update = new MessageTemp();
		update.setId(id);
		update.setTitle(title);
		update.setRemark(remark);
		update.setCode(code);
		update.setSmsTempId(smsTempId);
		update.setNoticeTempId(noticeTempId);
		//仅平台账户可用
		UserInfo userInfo = getUserInfo();
		if (userInfo.getType() == null || userInfo.getType() != 0) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		MessageTemp mt = service.get(update.getId());
		if (mt != null) {
			mt.setSmsTempId(update.getSmsTempId());
			mt.setNoticeTempId(update.getNoticeTempId());
			service.update(mt);
			return ResultVO.OK();
		}

		return ResultVO.FAIL(Code.FAIL402);
	}

	/**
	 * 查询平台的短信/通知模板配置
	 */
	@RequestMapping("/getAll")
	public ResultVO getAll() {
		boolean page = setPage();
		List<MessageTempVo> vos = service.getAll();
		if (vos != null && vos.size() > 0) {
			for (MessageTempVo vo : vos) {
				Integer noticeTempId = vo.getNoticeTempId();
				if (noticeTempId != null) {
					NoticeTemp noticeTemp = noticeTempService.get(noticeTempId);
					vo.setNoticeTemp(noticeTemp);
				}
				Integer smsTempId = vo.getSmsTempId();
				if (smsTempId != null) {
					SmsTemp smsTemp = smsTempService.get(smsTempId);
					vo.setSmsTemp(smsTemp);
				}
			}
		}
		return ResultVO.Page(vos, page);
	}
}
