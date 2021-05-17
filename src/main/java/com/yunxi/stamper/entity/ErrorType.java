package com.yunxi.stamper.entity;

import javax.persistence.*;
import java.util.Date;

@Table(name = "error_type")
public class ErrorType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 异常名称
     */
    private String name;

    /**
     * 信息备注
     */
    private String remark;

    /**
     * 所属的使用记录id
     */
    @Column(name = "seal_record_info_id")
    private Integer sealRecordInfoId;

    /**
     * 所属公司id
     */
    @Column(name = "org_id")
    private Integer orgId;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "update_date")
    private Date updateDate;

    @Column(name = "delete_date")
    private Date deleteDate;

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
     * 获取异常名称
     *
     * @return name - 异常名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置异常名称
     *
     * @param name 异常名称
     */
    public ErrorType setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * 获取信息备注
     *
     * @return remark - 信息备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置信息备注
     *
     * @param remark 信息备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取所属的使用记录id
     *
     * @return seal_record_info_id - 所属的使用记录id
     */
    public Integer getSealRecordInfoId() {
        return sealRecordInfoId;
    }

    /**
     * 设置所属的使用记录id
     *
     * @param sealRecordInfoId 所属的使用记录id
     */
    public void setSealRecordInfoId(Integer sealRecordInfoId) {
        this.sealRecordInfoId = sealRecordInfoId;
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
}