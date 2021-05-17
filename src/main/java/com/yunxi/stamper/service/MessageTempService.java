package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.MessageTemp;
import com.yunxi.stamper.entity.Notice;
import com.yunxi.stamper.entityVo.MessageTempVo;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/23 0023 11:34
 */
public interface MessageTempService {
	MessageTemp getByCode(String code);

	List<MessageTempVo> getAll();

	void update(MessageTemp mt);

	MessageTemp get(Integer id);

	/**
	 * 申请用章后,通知审批人审批
	 * 短信:您好,${code0}提醒您,您有一条新的审批请求,来自${code1}申请人${code2}的申请单${code3},请及时处理。
	 * 通知:您有一条新的审批请求,来自申请人%s的申请单%s,请及时处理
	 *
	 * @param title     申请标题
	 * @param userName  申请人
	 * @param receiveId 接收人ID
	 */
	void approvalNotice(String title, String userName, Integer receiveId);

	/**
	 * 审批拒绝后,通知申请人
	 * 短信:您好,${code0}提醒您,您的申请单${code1}已被审批人${code2}审批驳回。
	 * 通知:您的申请单%s已被审批人%s审批通过
	 *
	 * @param title       申请标题
	 * @param managerName 审批人
	 * @param receiveId   接收人ID
	 */
	void managerFAILNotice(String title, String managerName, Integer receiveId);

	/**
	 * 审批通过后,通知申请人
	 * 短信:您好,${code0}提醒您,您的申请单${code1}已被审批人${code2}审批通过。
	 * 通知:您的申请单%s已被审批人%s审批通过
	 *
	 * @param title       申请标题
	 * @param managerName 审批人
	 * @param receiveId   接收人ID
	 */
	void managerOKNotice(String title, String managerName, Integer receiveId);

	/**
	 * 审批转交后,通知申请人
	 * 短信:您好,${code0}提醒您,您的申请单${code1}已被审批人${code2}转交给${code3}审批,请耐心等待。
	 * 通知:您的申请单%s已被审批人%s转交给%s审批,请耐心等待
	 *
	 * @param title       申请标题
	 * @param managerName 审批人
	 * @param transName   转交人
	 * @param receiveId   接收人ID
	 */
	void approvalTransferNotice(String title, String managerName, String transName, Integer receiveId);

	/**
	 * 审批转交后,通知被转交审批人
	 * 短信:您好,${code0}提醒您,您有一条来自${code1}审批人${code2}转交的审批请求,请及时处理。
	 * 通知:您有一条新的审批转交请求,来自审批人%s,请及时处理
	 *
	 * @param orgName   转交人的组织名称
	 * @param userName  审批人
	 * @param receiveId 接收人ID
	 */
	void transferManagerNotice(String orgName, String userName, Integer receiveId);

	/**
	 * 审批完成后,通知授权人授权
	 * 短信:您好,${code0}提醒您,您有一条新的授权请求,来自${code1}申请人${code2}的申请单${code3},请及时处理。
	 * 通知:您有一条新的授权请求,来自申请人%s的申请单%s,请及时处理
	 *
	 * @param title     申请标题
	 * @param userName  申请人
	 * @param orgName   申请人组织名称
	 * @param receiveId 接收人ID
	 */
	void authorizationNotice(String orgName, String userName, String title, Integer receiveId);

	/**
	 * 授权拒绝,通知申请人
	 * 短信:您好,${code0}提醒您,您的申请单${code1}已被管章人${code2}授权驳回。
	 * 通知:您的申请单%s已被管章人%s授权驳回
	 *
	 * @param title      申请标题
	 * @param keeperName 管章人
	 * @param receiveId  接收人ID
	 */
	void keeperFAILNotice(String title, String keeperName, Integer receiveId);

	/**
	 * 授权通过,通知申请人
	 * 短信:您好,${code0}提醒您,您的申请单${code1}已被管章人${code2}授权通过。
	 * 通知:您的申请单%s已被管章人%s授权通过
	 *
	 * @param title      申请标题
	 * @param keeperName 管章人
	 * @param receiveId  接收人ID
	 */
	void keeperOKNotice(String title, String keeperName, Integer receiveId);

	/**
	 * 授权完成后,通知审计员审计
	 * 短信:您好,${code0}提醒您,您有一条新的审计请求,来自${code1}申请人${code2}的申请单${code3},请及时处理。
	 * 通知:您有一条新的审计请求,来自申请人%s的申请单%s,请及时处理
	 *
	 * @param orgName   组织名称
	 * @param userName  申请人
	 * @param title     申请标题
	 * @param receiveId 接收人ID
	 */
	void auditorNotice(String orgName, String userName, String title, Integer receiveId);

	/**
	 * 审计通过,通知申请人
	 * 短信:您好,${code0}提醒您,您的申请单${code1}已被审计员${code2}审计通过。
	 * 通知:您的申请单%s已被审计员%s审计通过
	 *
	 * @param title       申请标题
	 * @param auditorName 审计人
	 * @param receiveId   接收人ID
	 */
	void auditorOKNotice(String title, String auditorName, Integer receiveId);

	/**
	 * 审计拒绝,通知申请人
	 * 短信:您好,${code0}提醒您,您的申请单${code1}已被审计员${code2}审计驳回。
	 * 通知:您的申请单%s已被审计员%s审计通过
	 *
	 * @param title       申请标题
	 * @param auditorName 审计人
	 * @param receiveId   接收人ID
	 */
	void auditorFAILNotice(String title, String auditorName, Integer receiveId);

	/**
	 * 设备信息更新时
	 * 短信:您好,${code0}提醒您,${code1}设备${code2}已被管理员${code3}更新,请至后台系统进行查看。
	 * 通知:贵公司设备%s已被管理员%s更新,请至后台系统进行查看
	 *
	 * @param orgName    组织名称
	 * @param deviceName 印章名称
	 * @param userName   管理员
	 * @param receiveId  接收人ID
	 */
	void updateDeviceNotice(String orgName, String deviceName, String userName, Integer receiveId);

	/**
	 * 盖章出现异常或警告
	 * 短信:您好,${code0}提醒您,${code1}使用印章${code2}时出现警告或异常,请及时处理。
	 * 通知:%s使用印章%s时出现警告或异常,请及时处理
	 *
	 * @param userName   用印人
	 * @param deviceName 印章名称
	 * @param receiveId  接收人ID
	 */
	void useErrorNotice(String userName, String deviceName, Integer receiveId);

	/**
	 * 指纹录入成功后,给予录入人提醒
	 * 短信:您好,${code0}提醒您,您的指纹已在${code1}设备录入成功。
	 * 通知:您的指纹已与%s设备绑定,您现在可以使用指纹解锁设备
	 *
	 * @param deviceName 审批人
	 * @param receiveId  接收人ID
	 */
	void addFingerNotice(String deviceName, Integer receiveId);

	/**
	 * 指纹清空成功后,给予管章人提醒
	 * 短信:您好，${code0}提醒您，${code1}设备指纹清空成功。
	 *
	 * @param deviceName 审批人
	 * @param receiveId  接收人ID
	 */
	void clearFingerNotice(String deviceName, Integer receiveId);

	/**
	 * 设备触发拆卸警告，向管章人发送短信及消息通知
	 * 短信：您好,${code0}提醒您,设备${code1}印章已经出现拆卸报警,请及时处理。
	 * 通知：【%s】对设备【%s】进行拆卸动作,请及时处理
	 *
	 * @param deviceName 设备名称
	 * @param receiveId  管章人ID
	 * @return 通知消息
	 */
	Notice dismantleNotice(String deviceName, Integer receiveId);

	void deleteDeviceNotice(String deviceName, String userName, Integer receiveId);

	void auditorOKToBossNotice(String title, Integer receiveId);

}
