package com.yunxi.stamper.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "org_space")
public class OrgSpace implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 公司ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 云空间总容量(单位:G)
     */
    @Column(name = "space_total")
    private Double spaceTotal;

    /**
     * 云空间已用容量(单位:G)
     */
    @Column(name = "space_usage")
    private Double spaceUsage;

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
     * 获取公司ID
     *
     * @return org_id - 公司ID
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置公司ID
     *
     * @param orgId 公司ID
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取云空间总容量(单位:G)
     *
     * @return space_total - 云空间总容量(单位:G)
     */
    public Double getSpaceTotal() {
        return spaceTotal;
    }

    /**
     * 设置云空间总容量(单位:G)
     *
     * @param spaceTotal 云空间总容量(单位:G)
     */
    public void setSpaceTotal(Double spaceTotal) {
        this.spaceTotal = spaceTotal;
    }

    /**
     * 获取云空间已用容量(单位:G)
     *
     * @return space_usage - 云空间已用容量(单位:G)
     */
    public Double getSpaceUsage() {
        return spaceUsage;
    }

    /**
     * 设置云空间已用容量(单位:G)
     *
     * @param spaceUsage 云空间已用容量(单位:G)
     */
    public void setSpaceUsage(Double spaceUsage) {
        this.spaceUsage = spaceUsage;
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
        sb.append(", orgId=").append(orgId);
        sb.append(", spaceTotal=").append(spaceTotal);
        sb.append(", spaceUsage=").append(spaceUsage);
        sb.append(", createDate=").append(createDate);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", deleteDate=").append(deleteDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}