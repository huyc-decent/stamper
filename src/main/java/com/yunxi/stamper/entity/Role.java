package com.yunxi.stamper.entity;

import com.yunxi.stamper.logger.anno.LogTag;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@LogTag("角色")
public class Role implements Serializable {

    @LogTag("角色id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 角色名称
     */
    @LogTag("角色名称")
    private String name;

    /**
     * 角色编码
     */
    @LogTag("角色编码")
    private String code;

    /**
     * 角色描述
     */
    @LogTag("角色描述")
    private String remark;

    /**
     * 所属组织id
     */
    @LogTag("集团id")
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 创建人id
     */
    @Column(name = "create_id")
    private Integer createId;

    @Column(name = "create_date")
    private Date createDate;

    /**
     * 更新人id
     */
    @Column(name = "update_id")
    private Integer updateId;

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
     * 获取角色名称
     *
     * @return name - 角色名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置角色名称
     *
     * @param name 角色名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取角色编码
     *
     * @return code - 角色编码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置角色编码
     *
     * @param code 角色编码
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取角色描述
     *
     * @return remark - 角色描述
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置角色描述
     *
     * @param remark 角色描述
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取所属组织id
     *
     * @return org_id - 所属组织id
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置所属组织id
     *
     * @param orgId 所属组织id
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取创建人id
     *
     * @return create_id - 创建人id
     */
    public Integer getCreateId() {
        return createId;
    }

    /**
     * 设置创建人id
     *
     * @param createId 创建人id
     */
    public void setCreateId(Integer createId) {
        this.createId = createId;
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
     * 获取更新人id
     *
     * @return update_id - 更新人id
     */
    public Integer getUpdateId() {
        return updateId;
    }

    /**
     * 设置更新人id
     *
     * @param updateId 更新人id
     */
    public void setUpdateId(Integer updateId) {
        this.updateId = updateId;
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
        sb.append(", code=").append(code);
        sb.append(", remark=").append(remark);
        sb.append(", orgId=").append(orgId);
        sb.append(", createId=").append(createId);
        sb.append(", createDate=").append(createDate);
        sb.append(", updateId=").append(updateId);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", deleteDate=").append(deleteDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}