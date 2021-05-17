package com.yunxi.stamper.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

public class Notice implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 接收人id
     */
    @Column(name = "receive_id")
    private Integer receiveId;

    /**
     * 接收人cid
     */
    private String cid;

    /**
     * 状态 -1:未发送 0:发送成功 1:发送失败
     */
    private Integer status;

    /**
     * 发送次数
     */
    private Integer times;

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

    @Column(name="is_see")
    private Integer isSee;

    private static final long serialVersionUID = 1L;

    public Integer getIsSee() {
        return isSee;
    }

    public void setIsSee(Integer isSee) {
        this.isSee = isSee;
    }

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取通知标题
     *
     * @return title - 通知标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置通知标题
     *
     * @param title 通知标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取通知内容
     *
     * @return content - 通知内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置通知内容
     *
     * @param content 通知内容
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
     * 获取接收人cid
     *
     * @return cid - 接收人cid
     */
    public String getCid() {
        return cid;
    }

    /**
     * 设置接收人cid
     *
     * @param cid 接收人cid
     */
    public void setCid(String cid) {
        this.cid = cid;
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
        sb.append(", cid=").append(cid);
        sb.append(", status=").append(status);
        sb.append(", times=").append(times);
        sb.append(", isSee=").append(isSee);
        sb.append(", error=").append(error);
        sb.append(", createDate=").append(createDate);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", deleteDate=").append(deleteDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}