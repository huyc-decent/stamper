package com.yunxi.stamper.service.impl;

import com.getui.push.v2.sdk.api.PushApi;
import com.yunxi.push.common.dto.PushResultDto;
import com.yunxi.push.common.model.UniPushWrap;
import com.yunxi.push.handler.ApiHandler;
import com.yunxi.push.service.ClientService;
import com.yunxi.push.service.SinglePushService;
import com.yunxi.stamper.commons.jwt.UserToken;
import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.other.SmsGlobal;
import com.yunxi.stamper.entity.Notice;
import com.yunxi.stamper.entity.NoticeTemp;
import com.yunxi.stamper.entity.OrgNoticeTemp;
import com.yunxi.stamper.entity.User;
import com.yunxi.stamper.mapper.NoticeMapper;
import com.yunxi.stamper.service.NoticeService;
import com.yunxi.stamper.service.NoticeTempService;
import com.yunxi.stamper.service.OrgNoticeTempService;
import com.yunxi.stamper.sys.context.SpringContextUtils;
import com.yunxi.stamper.sys.error.base.PrintException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/7 0007 17:07
 */
@Slf4j
@Service
public class INoticeService implements NoticeService {

	@Autowired
	private NoticeMapper mapper;
	@Autowired
	private NoticeTempService noticeTempService;
	@Autowired
	private OrgNoticeTempService orgNoticeTempService;
	@Autowired
	private SinglePushService<UniPushWrap> pushService;
	@Autowired
	private ClientService clientService;
	@Autowired
	private ApiHandler apiHandler;

	@Override
	@Transactional
	public void update(Notice notice) {
		int updateCount = 0;

		if (notice != null) {
			notice.setUpdateDate(new Date());
			updateCount = mapper.updateByPrimaryKey(notice);
		}
		if (updateCount != 1) {
			throw new PrintException("通知更新失败");
		}
	}

	/**
	 * 查询登录用户未查看的系统角标
	 */
	@Override
	public Integer getCountByNotViewedFromLogin() {
		UserToken token = SpringContextUtils.getToken();
		return mapper.selectCountByNotViewedFromLogin(token.getUserId());
	}

	/**
	 * 个推
	 *
	 * @param clientId
	 * @param title
	 * @param content
	 * @return
	 */
	private PushResultDto push(String clientId, String title, String content) {
		// 获取推送对象
		PushApi pushApi = apiHandler.push();

		// 检查客户端在线状态
		boolean online = clientService.isOnline(clientId);

		// 请求参数包装
		UniPushWrap pushWrap = new UniPushWrap();
		pushWrap.setPushApi(pushApi);
		pushWrap.setOnline(online);
		pushWrap.setClientId(clientId);
		pushWrap.setTitle(title);
		pushWrap.setContent(content);
		// 发送通知
		return pushService.push(pushWrap);
	}

	/**
	 * 发送通知
	 *
	 * @param receive      接收人
	 * @param noticeTempId 通知模板id
	 * @param args         模板所需参数
	 */
	@Override
	public Notice sendNotice(User receive, Integer noticeTempId, String... args) {
		if (receive == null) {
			log.error("X\t通知消息失败\terror:接收人不存在\tnoticeTempId:{}\targs:{}", noticeTempId, args == null ? null : Arrays.toString(args));
			return null;
		}

		//模板不存在,不用发通知
		NoticeTemp nt = noticeTempService.get(noticeTempId);
		if (nt == null) {
			return null;
		}

		//未配置该通知模板,不用发通知
		OrgNoticeTemp ont = orgNoticeTempService.getByOrgAndNoticetemp(receive.getOrgId(), nt.getId());
		if (ont == null) {
			return null;
		}

		try {
			String cid = receive.getCid();
			Notice notice = new Notice();
			notice.setTitle(nt.getName());
			notice.setContent(String.format(nt.getContent(), args));
			notice.setReceiveId(receive.getId());
			notice.setTimes(1);
			notice.setCid(cid);
			notice.setIsSee(0);

			if (StringUtils.isNotBlank(cid) && CommonUtils.properties.isPush()) {
				PushResultDto pushResultDto = null;
				String error = "推送失败";
				try {
					pushResultDto = push(cid, notice.getTitle(), notice.getContent());
				} catch (Exception e) {
					e.printStackTrace();
					error = e.getMessage();
				}
				if (pushResultDto != null) {
					error = pushResultDto.getMsg();
					if (pushResultDto.getCode() == 0) {
						notice.setStatus(SmsGlobal.SEND_OK);
					} else {
						notice.setStatus(SmsGlobal.SEND_FAIL);
					}
				}
				notice.setError(error);
			} else {
				notice.setStatus(SmsGlobal.SEND_NO);
				notice.setError("客户端个推CID不存在");
			}

			add(notice);
			return notice;
		} catch (Exception e) {
			log.error("X\t通知消息-异常\tnoticeTempId:{}\targs:{}", noticeTempId, args == null ? null : Arrays.toString(args), e);
		}
		return null;
	}

	@Override
	@Transactional
	public void add(Notice notice) {
		int addCount = 0;
		if (notice != null) {
			notice.setCreateDate(new Date());
			addCount = mapper.insert(notice);
		}
		if (addCount != 1) {
			throw new PrintException("添加通知消息失败");
		}
	}

	@Override
	public Notice get(Integer noticeId) {
		if (noticeId != null) {
			Example example = new Example(Notice.class);
			example.createCriteria().andIsNull("deleteDate")
					.andEqualTo("id", noticeId);
			return mapper.selectOneByExample(example);
		}
		return null;
	}

	/**
	 * 查询用户通知消息列表
	 *
	 * @param userId  用户ID
	 * @param title   标题关键词
	 * @param content 内容关键词
	 * @return
	 */
	@Override
	public List<Notice> getByOrgAndUserAndKeyword(@NotNull Integer userId, String title, String content) {
		if (userId == null) {
			return null;
		}
		SpringContextUtils.setPage();
		return mapper.selectByOrgAndUserAndKeyword(userId, title, content);
	}

	/**
	 * 查看用户通知消息列表
	 *
	 * @param receiveId 用户ID
	 * @param title     标题关键词
	 * @param isSee     是否查看 0:未查看 1:已查看
	 * @return
	 */
	@Override
	public List<Notice> getNoticeByUserAndTitleAndSee(@NotNull Integer receiveId, String title, Integer isSee) {
		return mapper.selectByUserAndTitleAndSee(receiveId, title, isSee);
	}

	/**
	 * 更新通知消息的状态参数
	 *
	 * @param id  通知消息的ID
	 * @param see 状态 -1:未发送 0:发送成功 1:发送失败
	 */
	@Override
	@Transactional
	public void updateSee(Integer id, int see) {
		if (id == null) {
			return;
		}
		mapper.updateSee(id, see);
	}

}
