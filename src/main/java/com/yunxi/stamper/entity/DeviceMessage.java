package com.yunxi.stamper.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "device_message")
public class DeviceMessage implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 离线消息标题
     */
    private String title;

    /**
     * 离线消息体(json格式)
     */
    private String body;

    /**
     * 离线消息推送状态 0:成功 1:失败
     */
    @Column(name = "push_status")
    private Integer pushStatus;

    /**
     * 离线消息接收的设备id
     */
    @Column(name = "recipient_id")
    private Integer recipientId;

    /**
     * 发送者id
     */
    @Column(name = "send_id")
    private Integer sendId;

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

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取离线消息标题
     *
     * @return title - 离线消息标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置离线消息标题
     *
     * @param title 离线消息标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取离线消息体(json格式)
     *
     * @return body - 离线消息体(json格式)
     */
    public String getBody() {
        return body;
    }

    /**
     * 设置离线消息体(json格式)
     *
     * @param body 离线消息体(json格式)
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * 获取离线消息推送状态 0:成功 1:失败
     *
     * @return push_status - 离线消息推送状态 0:成功 1:失败
     */
    public Integer getPushStatus() {
        return pushStatus;
    }

    /**
     * 设置离线消息推送状态 0:成功 1:失败
     *
     * @param pushStatus 离线消息推送状态 0:成功 1:失败
     */
    public void setPushStatus(Integer pushStatus) {
        this.pushStatus = pushStatus;
    }

    /**
     * 获取离线消息接收的设备id
     *
     * @return recipient_id - 离线消息接收的设备id
     */
    public Integer getRecipientId() {
        return recipientId;
    }

    /**
     * 设置离线消息接收的设备id
     *
     * @param recipientId 离线消息接收的设备id
     */
    public void setRecipientId(Integer recipientId) {
        this.recipientId = recipientId;
    }

    /**
     * 获取发送者id
     *
     * @return send_id - 发送者id
     */
    public Integer getSendId() {
        return sendId;
    }

    /**
     * 设置发送者id
     *
     * @param sendId 发送者id
     */
    public void setSendId(Integer sendId) {
        this.sendId = sendId;
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
        sb.append(", body=").append(body);
        sb.append(", pushStatus=").append(pushStatus);
        sb.append(", recipientId=").append(recipientId);
        sb.append(", sendId=").append(sendId);
        sb.append(", createDate=").append(createDate);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", deleteDate=").append(deleteDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}