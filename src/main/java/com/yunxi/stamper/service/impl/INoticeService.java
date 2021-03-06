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
			throw new PrintException("??????????????????");
		}
	}

	/**
	 * ??????????????????????????????????????????
	 */
	@Override
	public Integer getCountByNotViewedFromLogin() {
		UserToken token = SpringContextUtils.getToken();
		return mapper.selectCountByNotViewedFromLogin(token.getUserId());
	}

	/**
	 * ??????
	 *
	 * @param clientId
	 * @param title
	 * @param content
	 * @return
	 */
	private PushResultDto push(String clientId, String title, String content) {
		// ??????????????????
		PushApi pushApi = apiHandler.push();

		// ???????????????????????????
		boolean online = clientService.isOnline(clientId);

		// ??????????????????
		UniPushWrap pushWrap = new UniPushWrap();
		pushWrap.setPushApi(pushApi);
		pushWrap.setOnline(online);
		pushWrap.setClientId(clientId);
		pushWrap.setTitle(title);
		pushWrap.setContent(content);
		// ????????????
		return pushService.push(pushWrap);
	}

	/**
	 * ????????????
	 *
	 * @param receive      ?????????
	 * @param noticeTempId ????????????id
	 * @param args         ??????????????????
	 */
	@Override
	public Notice sendNotice(User receive, Integer noticeTempId, String... args) {
		if (receive == null) {
			log.error("X\t??????????????????\terror:??????????????????\tnoticeTempId:{}\targs:{}", noticeTempId, args == null ? null : Arrays.toString(args));
			return null;
		}

		//???????????????,???????????????
		NoticeTemp nt = noticeTempService.get(noticeTempId);
		if (nt == null) {
			return null;
		}

		//????????????????????????,???????????????
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
				String error = "????????????";
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
				notice.setError("???????????????CID?????????");
			}

			add(notice);
			return notice;
		} catch (Exception e) {
			log.error("X\t????????????-??????\tnoticeTempId:{}\targs:{}", noticeTempId, args == null ? null : Arrays.toString(args), e);
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
			throw new PrintException("????????????????????????");
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
	 * ??????????????????????????????
	 *
	 * @param userId  ??????ID
	 * @param title   ???????????????
	 * @param content ???????????????
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
	 * ??????????????????????????????
	 *
	 * @param receiveId ??????ID
	 * @param title     ???????????????
	 * @param isSee     ???????????? 0:????????? 1:?????????
	 * @return
	 */
	@Override
	public List<Notice> getNoticeByUserAndTitleAndSee(@NotNull Integer receiveId, String title, Integer isSee) {
		return mapper.selectByUserAndTitleAndSee(receiveId, title, isSee);
	}

	/**
	 * ?????????????????????????????????
	 *
	 * @param id  ???????????????ID
	 * @param see ?????? -1:????????? 0:???????????? 1:????????????
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
