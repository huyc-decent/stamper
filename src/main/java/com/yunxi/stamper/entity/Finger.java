package com.yunxi.stamper.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

public class Finger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "update_date")
    private Date updateDate;

    @Column(name = "delete_by")
    private Integer deleteBy;

    @Column(name = "delete_date")
    private Date deleteDate;

    @Column(name = "delete_name")
    private String deleteName;

    /**
     * 设备id
     */
    @Column(name = "device_id")
    private Integer deviceId;

    /**
     * 设备指纹地址 1~3000
     */
    @Column(name = "addr_num")
    private Integer addrNum;

    /**
     * 发送指令的用户ID
     */
    @Column(name = "code_id")
    private Integer codeId;

    /**
     * 指纹所属人id
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 指纹显示的用户名称
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 备注
     */
    private String remarks;

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
     * 获取设备id
     *
     * @return device_id - 设备id
     */
    public Integer getDeviceId() {
        return deviceId;
    }

    /**
     * 设置设备id
     *
     * @param deviceId 设备id
     */
    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * 获取设备指纹地址 1~3000
     *
     * @return addr_num - 设备指纹地址 1~3000
     */
    public Integer getAddrNum() {
        return addrNum;
    }

    /**
     * 设置设备指纹地址 1~3000
     *
     * @param addrNum 设备指纹地址 1~3000
     */
    public void setAddrNum(Integer addrNum) {
        this.addrNum = addrNum;
    }

    /**
     * 获取发送指令的用户ID
     *
     * @return code_id - 发送指令的用户ID
     */
    public Integer getCodeId() {
        return codeId;
    }

    /**
     * 设置发送指令的用户ID
     *
     * @param codeId 发送指令的用户ID
     */
    public void setCodeId(Integer codeId) {
        this.codeId = codeId;
    }

    /**
     * 获取指纹所属人id
     *
     * @return user_id - 指纹所属人id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置指纹所属人id
     *
     * @param userId 指纹所属人id
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取指纹显示的用户名称
     *
     * @return user_name - 指纹显示的用户名称
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置指纹显示的用户名称
     *
     * @param userName 指纹显示的用户名称
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 获取备注
     *
     * @return remarks - 备注
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * 设置备注
     *
     * @param remarks 备注
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}