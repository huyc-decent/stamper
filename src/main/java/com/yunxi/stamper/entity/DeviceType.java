package com.yunxi.stamper.entity;

import javax.persistence.*;
import java.util.Date;

@Table(name = "device_type")
public class DeviceType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 设备类型描述
     */
    private String remark;

    /**
     * 类型所属公司
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 设备类型名称
     */
    private String name;

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
     * 获取设备类型描述
     *
     * @return remark - 设备类型描述
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置设备类型描述
     *
     * @param remark 设备类型描述
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取类型所属公司
     *
     * @return org_id - 类型所属公司
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置类型所属公司
     *
     * @param orgId 类型所属公司
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取设备类型名称
     *
     * @return name - 设备类型名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置设备类型名称
     *
     * @param name 设备类型名称
     */
    public void setName(String name) {
        this.name = name;
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