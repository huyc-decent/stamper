package com.yunxi.stamper.controller;


import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.jwt.UserToken;
import com.yunxi.stamper.commons.response.Code;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.NoticeTemp;
import com.yunxi.stamper.entity.OrgNoticeTemp;
import com.yunxi.stamper.entity.Serve;
import com.yunxi.stamper.entity.SmsTemp;
import com.yunxi.stamper.entityVo.NoticeVoSelect;
import com.yunxi.stamper.service.NoticeTempService;
import com.yunxi.stamper.service.OrgNoticeService;
import com.yunxi.stamper.service.ServeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/23 0023 16:51
 */
@Slf4j
@Api(tags = "集团<==>通知")
@RestController
@RequestMapping("/auth/orgNotice")
public class OrgNoticeController extends BaseController {

	@Autowired
	private OrgNoticeService service;
	@Autowired
	private NoticeTempService noticeTempService;
	@Autowired
	private ServeService serveService;


	@ApiOperation(value = "批量启用、禁用", notes = "批量启用、禁用")
	@PostMapping("/updateBulk")
	public ResultVO updateBulk(@RequestParam Integer[] tempIds, @RequestParam Boolean status) {
		UserToken token = getToken();

		//检查模板是否存在
		List<NoticeTemp> temps = noticeTempService.getAll();
		if (temps == null || temps.isEmpty()) {
			return ResultVO.FAIL("通知模板列表为空");
		}

		//批量更新
		service.updateBulk(token.getOrgId(), Arrays.asList(tempIds), status);
		return ResultVO.OK();
	}

	/**
	 * 更新公司通知模板状态
	 */
	@RequestMapping("/update")
	@Transactional
	public ResultVO update(@RequestParam("noticeTempId") Integer noticeTempId,
						   @RequestParam("isUse") boolean isUse) {
		NoticeTemp nt = noticeTempService.get(noticeTempId);
		if (nt != null) {
			UserToken token = getToken();
			if (isUse) {
				OrgNoticeTemp on = new OrgNoticeTemp();
				on.setNoticeTempId(noticeTempId);
				on.setOrgId(token.getOrgId());
				service.add(on);
				return ResultVO.OK();
			} else {
				OrgNoticeTemp on = service.getByOrgAndNotice(token.getOrgId(), noticeTempId);
				service.del(on);
				return ResultVO.OK();
			}
		}
		return ResultVO.FAIL(Code.FAIL402);
	}

	@ApiOperation(value = "查询通知模板列表", notes = "查询通知模板列表", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageNum", value = "当前页(默认:1)", dataType = "int"),
			@ApiImplicitParam(name = "pageSize", value = "每页数(默认:10)", dataType = "int"),
			@ApiImplicitParam(name = "page", value = "是否分页", dataType = "boolean", defaultValue = "true")
	})
	@GetMapping("/getByOwner")
	public ResultVO getByOwner() {
		UserToken token = getToken();

		//查询系统所有通知模板列表
		boolean page = setPage();
		List<NoticeVoSelect> allNoticeTemps = noticeTempService.getAllByVo();
		if (allNoticeTemps == null || allNoticeTemps.isEmpty()) {
			return ResultVO.OK();
		}

		//查询该集团启动的通知模板列表
		List<Integer> orgOpenNoticeTempIds = service.getByOrg(token.getOrgId());

		//如果该集团通知模板开启了，则展示'启用'状态
		for (NoticeVoSelect notice : allNoticeTemps) {
			//优化 通知话术
			try {
				String content = notice.getContent();
				content = content.replaceAll(properties.getNoticeSplitRegex(), "xxx");
				notice.setContent(content);
			} catch (Exception e) {
				e.printStackTrace();
			}
			notice.setUse(orgOpenNoticeTempIds != null && !orgOpenNoticeTempIds.isEmpty() && orgOpenNoticeTempIds.contains(notice.getId()));
		}
		return ResultVO.Page(allNoticeTemps, page);
	}
}
