package com.yunxi.stamper.entity;

import javax.persistence.*;
import java.util.Date;

@Table(name = "device_type_temp")
public class DeviceTypeTemp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 设备类型名称
     */
    private String name;

    /**
     * 设备类型描述
     */
    private String remark;

    @Column(name = "create_by")
    private Integer createBy;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "create_name")
    private String createName;

    @Column(name = "update_by")
    private Integer updateBy;

    @Column(name = "update_date")
    private Date updateDate;

    @Column(name = "update_name")
    private String updateName;

    @Column(name = "delete_by")
    private Integer deleteBy;

    @Column(name = "delete_date")
    private Date deleteDate;

    @Column(name = "delete_name")
    private String deleteName;

    private String field0;

    private String field1;

    private String field2;

    private String field3;

    private String field4;

    private String field5;

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
     * @return create_by
     */
    public Integer getCreateBy() {
        return createBy;
    }

    /**
     * @param createBy
     */
    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
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
     * @return create_name
     */
    public String getCreateName() {
        return createName;
    }

    /**
     * @param createName
     */
    public void setCreateName(String createName) {
        this.createName = createName;
    }

    /**
     * @return update_by
     */
    public Integer getUpdateBy() {
        return updateBy;
    }

    /**
     * @param updateBy
     */
    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
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
     * @return update_name
     */
    public String getUpdateName() {
        return updateName;
    }

    /**
     * @param updateName
     */
    public void setUpdateName(String updateName) {
        this.updateName = updateName;
    }

    /**
     * @return delete_by
     */
    public Integer getDeleteBy() {
        return deleteBy;
    }

    /**
     * @param deleteBy
     */
    public void setDeleteBy(Integer deleteBy) {
        this.deleteBy = deleteBy;
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

    /**
     * @return delete_name
     */
    public String getDeleteName() {
        return deleteName;
    }

    /**
     * @param deleteName
     */
    public void setDeleteName(String deleteName) {
        this.deleteName = deleteName;
    }

    /**
     * @return field0
     */
    public String getField0() {
        return field0;
    }

    /**
     * @param field0
     */
    public void setField0(String field0) {
        this.field0 = field0;
    }

    /**
     * @return field1
     */
    public String getField1() {
        return field1;
    }

    /**
     * @param field1
     */
    public void setField1(String field1) {
        this.field1 = field1;
    }

    /**
     * @return field2
     */
    public String getField2() {
        return field2;
    }

    /**
     * @param field2
     */
    public void setField2(String field2) {
        this.field2 = field2;
    }

    /**
     * @return field3
     */
    public String getField3() {
        return field3;
    }

    /**
     * @param field3
     */
    public void setField3(String field3) {
        this.field3 = field3;
    }

    /**
     * @return field4
     */
    public String getField4() {
        return field4;
    }

    /**
     * @param field4
     */
    public void setField4(String field4) {
        this.field4 = field4;
    }

    /**
     * @return field5
     */
    public String getField5() {
        return field5;
    }

    /**
     * @param field5
     */
    public void setField5(String field5) {
        this.field5 = field5;
    }
}