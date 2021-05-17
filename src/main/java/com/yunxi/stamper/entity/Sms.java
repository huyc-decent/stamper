package com.yunxi.stamper.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

public class Sms implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 短信标题
     */
    private String title;

    /**
     * 短信内容
     */
    private String content;

    @Column(name = "sms_code")
    private String smsCode;

    @Column(name = "sms_args")
    private String smsArgs;

    /**
     * 接收人id
     */
    @Column(name = "receive_id")
    private Integer receiveId;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 状态 -1:未发送 0:发送成功 1:发送失败
     */
    private Integer status;

    /**
     * 发送次数
     */
    private Integer times;

    @Column(name = "plan_time")
    private Date planTime;

    /**
     * 发送失败原因
     */
    private String error;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "update_date")
    private Date updateDate;

    @Column(name = "delete_date")
    private Date deleteDate;

    private static final long serialVersionUID = 1L;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    public Date getPlanTime() {
        return planTime;
    }

    public void setPlanTime(Date planTime) {
        this.planTime = planTime;
    }

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取短信标题
     *
     * @return title - 短信标题
     */
    public String getTitle() {
        return title;
    }

    public String getSmsArgs() {
        return smsArgs;
    }

    public void setSmsArgs(String smsArgs) {
        this.smsArgs = smsArgs;
    }

    /**
     * 设置短信标题
     *
     * @param title 短信标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取短信内容
     *
     * @return content - 短信内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置短信内容
     *
     * @param content 短信内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取接收人id
     *
     * @return receive_id - 接收人id
     */
    public Integer getReceiveId() {
        return receiveId;
    }

    /**
     * 设置接收人id
     *
     * @param receiveId 接收人id
     */
    public void setReceiveId(Integer receiveId) {
        this.receiveId = receiveId;
    }

    /**
     * 获取手机号
     *
     * @return phone - 手机号
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置手机号
     *
     * @param phone 手机号
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 获取状态 -1:未发送 0:发送成功 1:发送失败
     *
     * @return status - 状态 -1:未发送 0:发送成功 1:发送失败
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置状态 -1:未发送 0:发送成功 1:发送失败
     *
     * @param status 状态 -1:未发送 0:发送成功 1:发送失败
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取发送次数
     *
     * @return times - 发送次数
     */
    public Integer getTimes() {
        return times;
    }

    /**
     * 设置发送次数
     *
     * @param times 发送次数
     */
    public void setTimes(Integer times) {
        this.times = times;
    }

    /**
     * 获取发送失败原因
     *
     * @return error - 发送失败原因
     */
    public String getError() {
        return error;
    }

    /**
     * 设置发送失败原因
     *
     * @param error 发送失败原因
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * @return create_date
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * @return update_date
     */
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * @param updateDate
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * @return delete_date
     */
    public Date getDeleteDate() {
        return deleteDate;
    }

    /**
     * @param deleteDate
     */
    public void setDeleteDate(Date deleteDate) {
        this.deleteDate = deleteDate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", title=").append(title);
        sb.append(", content=").append(content);
        sb.append(", receiveId=").append(receiveId);
        sb.append(", phone=").append(phone);
        sb.append(", status=").append(status);
        sb.append(", times=").append(times);
        sb.append(", error=").append(error);
        sb.append(", createDate=").append(createDate);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", deleteDate=").append(deleteDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}