package com.yunxi.stamper.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "message_temp")
public class MessageTemp implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息描述
     */
    private String remark;

    /**
     * 消息唯一码
     */
    private String code;

    /**
     * 短信模板id
     */
    @Column(name = "sms_temp_id")
    private Integer smsTempId;

    /**
     * 通知模板id
     */
    @Column(name = "notice_temp_id")
    private Integer noticeTempId;

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
     * 获取消息标题
     *
     * @return title - 消息标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置消息标题
     *
     * @param title 消息标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取消息描述
     *
     * @return remark - 消息描述
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置消息描述
     *
     * @param remark 消息描述
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取消息唯一码
     *
     * @return code - 消息唯一码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置消息唯一码
     *
     * @param code 消息唯一码
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取短信模板id
     *
     * @return sms_temp_id - 短信模板id
     */
    public Integer getSmsTempId() {
        return smsTempId;
    }

    /**
     * 设置短信模板id
     *
     * @param smsTempId 短信模板id
     */
    public void setSmsTempId(Integer smsTempId) {
        this.smsTempId = smsTempId;
    }

    /**
     * 获取通知模板id
     *
     * @return notice_temp_id - 通知模板id
     */
    public Integer getNoticeTempId() {
        return noticeTempId;
    }

    /**
     * 设置通知模板id
     *
     * @param noticeTempId 通知模板id
     */
    public void setNoticeTempId(Integer noticeTempId) {
        this.noticeTempId = noticeTempId;
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
        sb.append(", remark=").append(remark);
        sb.append(", code=").append(code);
        sb.append(", smsTempId=").append(smsTempId);
        sb.append(", noticeTempId=").append(noticeTempId);
        sb.append(", createDate=").append(createDate);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", deleteDate=").append(deleteDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}