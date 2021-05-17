package com.yunxi.stamper.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

public class Flow implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 流程名称
     */
    private String name;

    /**
     * 流程描述
     */
    private String remark;

    /**
     * 所属公司id
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 创建人id
     */
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "user_name")
    private String userName;

    /**
     * 状态0:启用 1:禁用
     */
    private Integer status;

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
     * 获取流程名称
     *
     * @return name - 流程名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置流程名称
     *
     * @param name 流程名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取流程描述
     *
     * @return remark - 流程描述
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置流程描述
     *
     * @param remark 流程描述
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取所属公司id
     *
     * @return org_id - 所属公司id
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置所属公司id
     *
     * @param orgId 所属公司id
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取创建人id
     *
     * @return user_id - 创建人id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置创建人id
     *
     * @param userId 创建人id
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * @return user_name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 获取状态0:启用 1:禁用
     *
     * @return status - 状态0:启用 1:禁用
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置状态0:启用 1:禁用
     *
     * @param status 状态0:启用 1:禁用
     */
    public void setStatus(Integer status) {
        this.status = status;
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
        sb.append(", remark=").append(remark);
        sb.append(", orgId=").append(orgId);
        sb.append(", userId=").append(userId);
        sb.append(", userName=").append(userName);
        sb.append(", status=").append(status);
        sb.append(", createDate=").append(createDate);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", deleteDate=").append(deleteDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}