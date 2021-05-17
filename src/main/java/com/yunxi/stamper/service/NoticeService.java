package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.Notice;
import com.yunxi.stamper.entity.User;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/7 0007 17:07
 */
public interface NoticeService {
	Notice get(Integer noticeId);

	void add(Notice notice);

	void update(Notice notice);

	//发送通知
	Notice sendNotice(User receive, Integer noticeTempId, String... args);

	Integer getCountByNotViewedFromLogin();

	/**
	 * 查询用户通知消息列表
	 * @param userId 用户ID
	 * @param title 标题关键词
	 * @param content 内容关键词
	 * @return
	 */
	List<Notice> getByOrgAndUserAndKeyword(@NotNull Integer userId, String title, String content);

	/**
	 * 查看用户通知消息列表
	 * @param receiveId 用户ID
	 * @param title 标题关键词
	 * @param isSee 是否查看 0:未查看 1:已查看
	 * @return
	 */
	List<Notice> getNoticeByUserAndTitleAndSee(@NotNull Integer receiveId, String title ,Integer isSee);

	/**
	 * 更新通知消息的状态参数
	 * @param id 通知消息的ID
	 * @param see 状态 -1:未发送 0:发送成功 1:发送失败
	 */
	void updateSee(Integer id, int see);
}
