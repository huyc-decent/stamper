package com.yunxi.stamper.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "disassembly_record_info")
public class DisassemblyRecordInfo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 设备ID
     */
    @Column(name = "device_id")
    private Integer deviceId;

    /**
     * 拆卸时间
     */
    @Column(name = "real_time")
    private Date realTime;

    /**
     * 集团公司ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 拆卸时刻的次数值
     */
    @Column(name = "use_count")
    private Integer useCount;

    /**
     * 拆章人
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 拆章人
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 地址
     */
    private String location;

    /**
     * 附件、图片
     */
    @Column(name = "aes_file_info_id")
    private String aesFileInfoId;

    /**
     * 通知id
     */
    @Column(name = "notice_id")
    private Integer noticeId;

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
     * 获取拆卸时间
     *
     * @return real_time - 拆卸时间
     */
    public Date getRealTime() {
        return realTime;
    }

    /**
     * 设置拆卸时间
     *
     * @param realTime 拆卸时间
     */
    public void setRealTime(Date realTime) {
        this.realTime = realTime;
    }

    /**
     * 获取集团公司ID
     *
     * @return org_id - 集团公司ID
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置集团公司ID
     *
     * @param orgId 集团公司ID
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取拆卸时刻的次数值
     *
     * @return use_count - 拆卸时刻的次数值
     */
    public Integer getUseCount() {
        return useCount;
    }

    /**
     * 设置拆卸时刻的次数值
     *
     * @param useCount 拆卸时刻的次数值
     */
    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }

    /**
     * 获取拆章人
     *
     * @return user_id - 拆章人
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置拆章人
     *
     * @param userId 拆章人
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取拆章人
     *
     * @return user_name - 拆章人
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置拆章人
     *
     * @param userName 拆章人
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 获取地址
     *
     * @return location - 地址
     */
    public String getLocation() {
        return location;
    }

    /**
     * 设置地址
     *
     * @param location 地址
     */
    public void setLocation(String location) {
        this.location = location;
    }

    public String getAesFileInfoId() {
        return aesFileInfoId;
    }

    public void setAesFileInfoId(String aesFileInfoId) {
        this.aesFileInfoId = aesFileInfoId;
    }

    /**
     * 获取通知id
     *
     * @return notice_id - 通知id
     */
    public Integer getNoticeId() {
        return noticeId;
    }

    /**
     * 设置通知id
     *
     * @param noticeId 通知id
     */
    public void setNoticeId(Integer noticeId) {
        this.noticeId = noticeId;
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
        sb.append(", deviceId=").append(deviceId);
        sb.append(", realTime=").append(realTime);
        sb.append(", orgId=").append(orgId);
        sb.append(", useCount=").append(useCount);
        sb.append(", userId=").append(userId);
        sb.append(", userName=").append(userName);
        sb.append(", location=").append(location);
        sb.append(", fileInfoId=").append(aesFileInfoId);
        sb.append(", noticeId=").append(noticeId);
        sb.append(", createDate=").append(createDate);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", deleteDate=").append(deleteDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}