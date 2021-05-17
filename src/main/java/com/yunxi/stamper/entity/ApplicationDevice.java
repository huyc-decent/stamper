package com.yunxi.stamper.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "application_device")
public class ApplicationDevice implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "update_date")
    private Date updateDate;

    @Column(name = "delete_date")
    private Date deleteDate;

    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 申请单id
     */
    @Column(name = "application_id")
    private Integer applicationId;

    /**
     * 印章id
     */
    @Column(name = "device_id")
    private Integer deviceId;

    /**
     * 印章名称
     */
    @Column(name = "device_name")
    private String deviceName;

    /**
     * 管章人id
     */
    @Column(name = "keeper_id")
    private Integer keeperId;

    /**
     * 管章人名称
     */
    @Column(name = "keeper_name")
    private String keeperName;

    /**
     * 审计人id
     */
    @Column(name = "auditor_id")
    private Integer auditorId;

    /**
     * 审计人名称
     */
    @Column(name = "auditor_name")
    private String auditorName;

    /**
     * 印章申请次数
     */
    @Column(name = "user_count")
    private Integer userCount;

    /**
     * 已使用次数
     */
    @Column(name = "already_count")
    private Integer alreadyCount;

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

    /**
     * @return org_id
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * @param orgId
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取申请单id
     *
     * @return application_id - 申请单id
     */
    public Integer getApplicationId() {
        return applicationId;
    }

    /**
     * 设置申请单id
     *
     * @param applicationId 申请单id
     */
    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    /**
     * 获取印章id
     *
     * @return device_id - 印章id
     */
    public Integer getDeviceId() {
        return deviceId;
    }

    /**
     * 设置印章id
     *
     * @param deviceId 印章id
     */
    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * 获取印章名称
     *
     * @return device_name - 印章名称
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * 设置印章名称
     *
     * @param deviceName 印章名称
     */
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    /**
     * 获取管章人id
     *
     * @return keeper_id - 管章人id
     */
    public Integer getKeeperId() {
        return keeperId;
    }

    /**
     * 设置管章人id
     *
     * @param keeperId 管章人id
     */
    public void setKeeperId(Integer keeperId) {
        this.keeperId = keeperId;
    }

    /**
     * 获取管章人名称
     *
     * @return keeper_name - 管章人名称
     */
    public String getKeeperName() {
        return keeperName;
    }

    /**
     * 设置管章人名称
     *
     * @param keeperName 管章人名称
     */
    public void setKeeperName(String keeperName) {
        this.keeperName = keeperName;
    }

    /**
     * 获取审计人id
     *
     * @return auditor_id - 审计人id
     */
    public Integer getAuditorId() {
        return auditorId;
    }

    /**
     * 设置审计人id
     *
     * @param auditorId 审计人id
     */
    public void setAuditorId(Integer auditorId) {
        this.auditorId = auditorId;
    }

    /**
     * 获取审计人名称
     *
     * @return auditor_name - 审计人名称
     */
    public String getAuditorName() {
        return auditorName;
    }

    /**
     * 设置审计人名称
     *
     * @param auditorName 审计人名称
     */
    public void setAuditorName(String auditorName) {
        this.auditorName = auditorName;
    }

    /**
     * 获取印章申请次数
     *
     * @return user_count - 印章申请次数
     */
    public Integer getUserCount() {
        return userCount;
    }

    /**
     * 设置印章申请次数
     *
     * @param userCount 印章申请次数
     */
    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    /**
     * 获取已使用次数
     *
     * @return already_count - 已使用次数
     */
    public Integer getAlreadyCount() {
        return alreadyCount;
    }

    /**
     * 设置已使用次数
     *
     * @param alreadyCount 已使用次数
     */
    public void setAlreadyCount(Integer alreadyCount) {
        this.alreadyCount = alreadyCount;
    }

    @Override
    public String toString() {
        String sb = "ApplicationDevice{" + "id=" + id +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                ", deleteDate=" + deleteDate +
                ", orgId=" + orgId +
                ", applicationId=" + applicationId +
                ", deviceId=" + deviceId +
                ", deviceName='" + deviceName + '\'' +
                ", keeperId=" + keeperId +
                ", keeperName='" + keeperName + '\'' +
                ", auditorId=" + auditorId +
                ", auditorName='" + auditorName + '\'' +
                ", userCount=" + userCount +
                ", alreadyCount=" + alreadyCount +
                '}';
        return sb;
    }
}
