package com.yunxi.stamper.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "device_migrate_log")
public class DeviceMigrateLog implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 设备ID
     */
    @Column(name = "device_id")
    private Integer deviceId;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "src_host")
    private String srcHost;

    @Column(name = "dest_host")
    private String destHost;

    /**
     * 操作人ID
     */
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "old_org_id")
    private Integer oldOrgId;

    @Column(name = "new_org_id")
    private Integer newOrgId;

    @Column(name = "migrate_status")
    private Integer migrateStatus;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "update_date")
    private Date updateDate;

    @Column(name = "delete_date")
    private Date deleteDate;

    private static final long serialVersionUID = 1L;


    public Integer getMigrateStatus() {
        return migrateStatus;
    }

    public void setMigrateStatus(Integer migrateStatus) {
        this.migrateStatus = migrateStatus;
    }

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
     * 获取设备ID
     *
     * @return device_id - 设备ID
     */
    public Integer getDeviceId() {
        return deviceId;
    }

    /**
     * 设置设备ID
     *
     * @param deviceId 设备ID
     */
    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * 获取操作人ID
     *
     * @return user_id - 操作人ID
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置操作人ID
     *
     * @param userId 操作人ID
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * @return old_org_id
     */
    public Integer getOldOrgId() {
        return oldOrgId;
    }

    /**
     * @param oldOrgId
     */
    public void setOldOrgId(Integer oldOrgId) {
        this.oldOrgId = oldOrgId;
    }

    /**
     * @return new_org_id
     */
    public Integer getNewOrgId() {
        return newOrgId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSrcHost() {
        return srcHost;
    }

    public void setSrcHost(String srcHost) {
        this.srcHost = srcHost;
    }

    public String getDestHost() {
        return destHost;
    }

    public void setDestHost(String destHost) {
        this.destHost = destHost;
    }

    /**
     * @param newOrgId
     */
    public void setNewOrgId(Integer newOrgId) {
        this.newOrgId = newOrgId;
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
        String sb = "DeviceMigrateLog{" + "id=" + id +
                ", deviceId=" + deviceId +
                ", uuid='" + uuid + '\'' +
                ", srcHost='" + srcHost + '\'' +
                ", destHost='" + destHost + '\'' +
                ", userId=" + userId +
                ", oldOrgId=" + oldOrgId +
                ", newOrgId=" + newOrgId +
                ", migrateStatus=" + migrateStatus +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                ", deleteDate=" + deleteDate +
                '}';
        return sb;
    }
}
