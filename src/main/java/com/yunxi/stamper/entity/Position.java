package com.yunxi.stamper.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

public class Position implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 所属组织
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 职称
     */
    private String name;

    /**
     * 创建人ID
     */
    @Column(name = "create_at")
    private Integer createAt;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "update_at")
    private Integer updateAt;

    @Column(name = "update_date")
    private Date updateDate;

    @Column(name = "delete_at")
    private Integer deleteAt;

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
     * 获取所属组织
     *
     * @return org_id - 所属组织
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置所属组织
     *
     * @param orgId 所属组织
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取职称
     *
     * @return name - 职称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置职称
     *
     * @param name 职称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取创建人ID
     *
     * @return create_at - 创建人ID
     */
    public Integer getCreateAt() {
        return createAt;
    }

    /**
     * 设置创建人ID
     *
     * @param createAt 创建人ID
     */
    public void setCreateAt(Integer createAt) {
        this.createAt = createAt;
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
     * @return update_at
     */
    public Integer getUpdateAt() {
        return updateAt;
    }

    /**
     * @param updateAt
     */
    public void setUpdateAt(Integer updateAt) {
        this.updateAt = updateAt;
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
     * @return delete_at
     */
    public Integer getDeleteAt() {
        return deleteAt;
    }

    /**
     * @param deleteAt
     */
    public void setDeleteAt(Integer deleteAt) {
        this.deleteAt = deleteAt;
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
        sb.append(", orgId=").append(orgId);
        sb.append(", name=").append(name);
        sb.append(", createAt=").append(createAt);
        sb.append(", createDate=").append(createDate);
        sb.append(", updateAt=").append(updateAt);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", deleteAt=").append(deleteAt);
        sb.append(", deleteDate=").append(deleteDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}