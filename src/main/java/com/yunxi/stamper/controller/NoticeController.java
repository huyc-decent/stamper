package com.yunxi.stamper.controller;

import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.other.EmojiFilter;
import com.yunxi.stamper.commons.jwt.UserToken;
import com.yunxi.stamper.commons.other.RedisGlobal;
import com.yunxi.stamper.commons.response.Code;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.Notice;
import com.yunxi.stamper.entityVo.UserInfo;
import com.yunxi.stamper.service.NoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/7 0007 18:39
 */
@Slf4j
@Api(tags = "通知相关")
@RestController
@RequestMapping(value = "/auth/notice", method = {RequestMethod.POST, RequestMethod.GET})
public class NoticeController extends BaseController {

	@Autowired
	private NoticeService service;

	/**
	 * 查询消息
	 */
	@RequestMapping("/get")
	public ResultVO get(@RequestParam("id") Integer noticeId) {
		if (noticeId != null) {
			Notice notice = service.get(noticeId);
			return ResultVO.OK(notice);
		}
		return ResultVO.FAIL(Code.FAIL402);
	}

	@ApiOperation(value = "拉取设备拆卸通知弹出消息列表", notes = "拉取设备拆卸通知弹出消息列表", httpMethod = "GET")
	@GetMapping("/getDisassemblyRecordInfoList")
	public ResultVO getDisassemblyRecordInfoList() {
		UserToken token = getToken();
		Integer userId = token.getUserId();
		String lockKey = RedisGlobal.NOTICE_DEMOLISH + userId;
		RLock lock = redisLock.lock(lockKey);
		try {
			//检查缓存是否有拆卸记录通知标记
			String key = RedisGlobal.NOTICE_DEMOLISH + userId;
			String value = redisUtil.getStr(key);
			if (StringUtils.isBlank(value)) {
				return ResultVO.OK();
			}

			//有标记查询数据库
			List<Notice> notices = service.getNoticeByUserAndTitleAndSee(userId, "设备拆卸", 0);
			if (notices == null || notices.isEmpty()) {
				return ResultVO.OK(notices);
			}

			//将该通知消息列表设置为查看状态
			for (Notice notice : notices) {
				notice.setIsSee(1);
				try {
					service.update(notice);
				} catch (Exception e) {
					log.error("更新拆卸记录出现异常 notice:{}", CommonUtils.objToJson(notice), e);
				}
			}

			//将标记删除
			redisUtil.del(key);

			return ResultVO.OK(notices);
		} catch (Exception e) {
			log.error("拆卸通知列表拉取异常", e);
		} finally {
			if (lock != null) {
				try {
					lock.unlock();
				} catch (Exception e) {
					log.error("解锁异常", e);
				}
			}
		}
		return ResultVO.OK();
	}

	@ApiOperation(value = "查询系统消息", notes = "查询系统消息", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageNum", value = "当前页(默认:1)", dataType = "int"),
			@ApiImplicitParam(name = "pageSize", value = "每页数(默认:10)", dataType = "int"),
			@ApiImplicitParam(name = "page", value = "是否分页", dataType = "boolean", defaultValue = "true"),
			@ApiImplicitParam(name = "title", value = "标题", dataType = "string"),
			@ApiImplicitParam(name = "content", value = "内容", dataType = "string")
	})
	@GetMapping("/getNoticesByUser")
	public ResultVO getNoticesByUser(@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
									 @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
									 @RequestParam(value = "page", required = false, defaultValue = "true") boolean isPage,
									 @RequestParam(value = "title", required = false) String title,
									 @RequestParam(value = "content", required = false) String content) {
		/*
		 * 参数校验：标题
		 */
		if (StringUtils.isNotBlank(title)) {
			if (EmojiFilter.containsEmoji(title)) {
				return ResultVO.FAIL("标题不能包含特殊字符");
			}
		}
		/*
		 * 参数校验：内容
		 */
		if (StringUtils.isNotBlank(content)) {
			if (EmojiFilter.containsEmoji(content)) {
				return ResultVO.FAIL("内容不能包含特殊字符");
			}
		}


		UserToken token = getToken();
		List<Notice> notices = service.getByOrgAndUserAndKeyword(token.getUserId(), title, content);

		if (isApp() && notices != null && notices.size() > 0) {
			for (Notice n : notices) {
				Integer isSee = n.getIsSee();
				if (isSee == null || isSee != 0) {
					continue;
				}
				service.updateSee(n.getId(),1);
			}
		}

		return ResultVO.Page(notices, isPage);
	}

	/**
	 * 查询登录用户未查看的系统角标
	 */
	@RequestMapping("/getCountByNotViewedFromLogin")
	public ResultVO getCountByNotViewedFromLogin() {
		Integer count = service.getCountByNotViewedFromLogin();
		return ResultVO.OK(count == null || count == 0 ? "" : count);
	}

	//用户点击查看系统消息
	@GetMapping("/checkNotice")
	public ResultVO checkNotice(@RequestParam("noticeId") Integer noticeId) {
		Notice notice = service.get(noticeId);

		//仅接收人本人可操作
		UserToken token = getToken();
		if (notice.getReceiveId() == null || token.getUserId().intValue() != notice.getReceiveId()) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		notice.setIsSee(1);
		service.update(notice);
		return ResultVO.OK(notice);
	}
}
