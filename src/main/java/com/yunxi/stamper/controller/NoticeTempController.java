package com.yunxi.stamper.controller;


import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.response.Code;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.NoticeTemp;
import com.yunxi.stamper.service.NoticeTempService;
import com.yunxi.stamper.sys.aop.annotaion.WebLogger;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/7 0007 18:20
 */
@Slf4j
@Api(tags = "通知模板相关")
@RestController
@RequestMapping(value = "/auth/noticeTemp", method = {RequestMethod.POST, RequestMethod.GET})
public class NoticeTempController extends BaseController {

	@Autowired
	private NoticeTempService service;

	/**
	 * 添加消息模板
	 */
	@WebLogger("新增消息模板")
	@RequestMapping("/add")
	public ResultVO add(String name, String content, String code) {
		if (StringUtils.isBlank(name)) {
			return ResultVO.FAIL("消息模板不能为空");
		}
		if (StringUtils.isBlank(code)) {
			return ResultVO.FAIL("模板编码不能为空");
		}
		NoticeTemp nt = service.getByCode(code);
		if (nt != null) {
			return ResultVO.FAIL("该模板编码已存在");
		}
		nt = service.getByName(name);
		if (nt != null) {
			return ResultVO.FAIL("该模板名称已存在");
		}
		NoticeTemp noticeTemp = new NoticeTemp();
		noticeTemp.setName(name);
		noticeTemp.setContent(content);
		noticeTemp.setCode(code);
		service.add(noticeTemp);
		return ResultVO.OK();
	}

	/**
	 * 删除消息模板
	 */
	@WebLogger("删除消息模板")
	@RequestMapping("/del")
	public ResultVO del(@RequestParam("id") Integer noticeTempId) {
		if (noticeTempId != null) {
			NoticeTemp noticeTemp = service.get(noticeTempId);
			if (noticeTemp != null) {
				service.del(noticeTemp);
			}
		}
		return ResultVO.FAIL(Code.FAIL402);
	}

	/**
	 * 修改消息模板
	 */
	@WebLogger("更新消息模板")
	@RequestMapping("/update")
	public ResultVO update(Integer id, String name, String content, String code) {
		NoticeTemp noticeTemp = service.get(id);
		if (noticeTemp != null) {
			if (StringUtils.isBlank(name)) {
				return ResultVO.FAIL("模板名称不能为空");
			}
			if (StringUtils.isBlank(code)) {
				return ResultVO.FAIL("模板编码不能为空");
			}
			NoticeTemp byName = service.getByName(name);
			if (byName != null && byName.getId().intValue() != id) {
				return ResultVO.FAIL("模板名称已存在");
			}
			NoticeTemp byCode = service.getByCode(code);
			if (byCode != null && byCode.getId().intValue() != id) {
				return ResultVO.FAIL("模板编码已存在");
			}
			noticeTemp.setName(name);
			noticeTemp.setContent(content);
			noticeTemp.setCode(code);
			service.update(noticeTemp);
			return ResultVO.OK();
		}
		return ResultVO.FAIL(Code.FAIL402);
	}

	/**
	 * 查询消息模板
	 */
	@RequestMapping("/get")
	public ResultVO get(@RequestParam("id") Integer noticeTempId) {
		if (noticeTempId != null) {
			NoticeTemp noticeTemp = service.get(noticeTempId);
			return ResultVO.OK(noticeTemp);
		}
		return ResultVO.FAIL(Code.FAIL402);
	}

	/**
	 * 查询消息模板列表
	 */
	@RequestMapping("/getList")
	public ResultVO getList() {
		boolean page = setPage();
		List<NoticeTemp> all = service.getAll();
		return ResultVO.Page(all, page);
	}
}
