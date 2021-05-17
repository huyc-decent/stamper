package com.yunxi.stamper.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "sms_temp")
public class SmsTemp implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 短信模板名称
     */
    private String name;

    /**
     * 短信模板内容
     */
    private String content;

    /**
     * 短信模板唯一码
     */
    private String code;

    /**
     * 短信模板描述
     */
    private String remark;

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
     * 获取短信模板名称
     *
     * @return name - 短信模板名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置短信模板名称
     *
     * @param name 短信模板名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取短信模板内容
     *
     * @return content - 短信模板内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置短信模板内容
     *
     * @param content 短信模板内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取短信模板唯一码
     *
     * @return code - 短信模板唯一码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置短信模板唯一码
     *
     * @param code 短信模板唯一码
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取短信模板描述
     *
     * @return remark - 短信模板描述
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置短信模板描述
     *
     * @param remark 短信模板描述
     */
    public void setRemark(String remark) {
        this.remark = remark;
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
        sb.append(", name=").append(name);
        sb.append(", content=").append(content);
        sb.append(", code=").append(code);
        sb.append(", remark=").append(remark);
        sb.append(", createDate=").append(createDate);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", deleteDate=").append(deleteDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}