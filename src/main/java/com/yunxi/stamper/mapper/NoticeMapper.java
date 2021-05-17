package com.yunxi.stamper.mapper;

import com.yunxi.stamper.entity.Notice;
import com.yunxi.stamper.sys.baseDao.MyMapper;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;

@Component
public interface NoticeMapper extends MyMapper<Notice> {
	//查询登录用户未查看的系统角标
	Integer selectCountByNotViewedFromLogin(Integer userId);

	/**
	 * 查询用户通知消息列表
	 *
	 * @param userId  用户ID
	 * @param title   标题关键词
	 * @param content 内容关键词
	 * @return
	 */
	List<Notice> selectByOrgAndUserAndKeyword(@NotNull Integer userId, String title, String content);

	/**
	 * 查看用户通知消息列表
	 *
	 * @param receiveId 用户ID
	 * @param title     标题关键词
	 * @param isSee     是否查看 0:未查看 1:已查看
	 * @return
	 */
	List<Notice> selectByUserAndTitleAndSee(@NotNull Integer receiveId, String title, Integer isSee);

	/**
	 * 更新通知消息的状态参数
	 * @param id 通知消息的ID
	 * @param see 状态 -1:未发送 0:发送成功 1:发送失败
	 */
	void updateSee(Integer id, int see);
}