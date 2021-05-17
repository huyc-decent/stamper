package com.yunxi.stamper.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "application_auditor")
public class ApplicationAuditor implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 关联申请单id
     */
    @Column(name = "application_id")
    private Integer applicationId;

    /**
     * 审计id
     */
    @Column(name = "auditor_id")
    private Integer auditorId;

    @Column(name = "auditor_name")
    private String auditorName;

    /**
     * 1:审计中 2:审计同意 3:审计拒绝   4:已失效
     */
    private Integer status;

    @Column(name = "device_id")
    private Integer deviceId;

    @Column(name = "device_name")
    private String deviceName;

    /**
     * 审批节点id
     */
    @Column(name = "node_id")
    private Integer nodeId;

    /**
     * 意见备注
     */
    private String suggest;

    /**
     * 处理时间
     */
    private Date time;

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
     * 获取关联申请单id
     *
     * @return application_id - 关联申请单id
     */
    public Integer getApplicationId() {
        return applicationId;
    }

    /**
     * 设置关联申请单id
     *
     * @param applicationId 关联申请单id
     */
    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    /**
     * 获取审计id
     *
     * @return auditor_id - 审计id
     */
    public Integer getAuditorId() {
        return auditorId;
    }

    /**
     * 设置审计id
     *
     * @param auditorId 审计id
     */
    public void setAuditorId(Integer auditorId) {
        this.auditorId = auditorId;
    }

    /**
     * @return auditor_name
     */
    public String getAuditorName() {
        return auditorName;
    }

    /**
     * @param auditorName
     */
    public void setAuditorName(String auditorName) {
        this.auditorName = auditorName;
    }

    /**
     * 0:未处理 1:审计中 2:审计同意 3:审计拒绝   4:已失效
     *
     * @return status 0:未处理 1:审计中 2:审计同意 3:审计拒绝   4:已失效
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置1:审计中 2:审计同意 3:审计拒绝   4:已失效
     *
     * @param status 1:审计中 2:审计同意 3:审计拒绝   4:已失效
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return device_id
     */
    public Integer getDeviceId() {
        return deviceId;
    }

    /**
     * @param deviceId
     */
    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * @return device_name
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * @param deviceName
     */
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    /**
     * 获取审批节点id
     *
     * @return node_id - 审批节点id
     */
    public Integer getNodeId() {
        return nodeId;
    }

    /**
     * 设置审批节点id
     *
     * @param nodeId 审批节点id
     */
    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * 获取意见备注
     *
     * @return suggest - 意见备注
     */
    public String getSuggest() {
        return suggest;
    }

    /**
     * 设置意见备注
     *
     * @param suggest 意见备注
     */
    public void setSuggest(String suggest) {
        this.suggest = suggest;
    }

    /**
     * 获取处理时间
     *
     * @return time - 处理时间
     */
    public Date getTime() {
        return time;
    }

    /**
     * 设置处理时间
     *
     * @param time 处理时间
     */
    public void setTime(Date time) {
        this.time = time;
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
        sb.append(", applicationId=").append(applicationId);
        sb.append(", auditorId=").append(auditorId);
        sb.append(", auditorName=").append(auditorName);
        sb.append(", status=").append(status);
        sb.append(", deviceId=").append(deviceId);
        sb.append(", deviceName=").append(deviceName);
        sb.append(", nodeId=").append(nodeId);
        sb.append(", suggest=").append(suggest);
        sb.append(", time=").append(time);
        sb.append(", createDate=").append(createDate);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", deleteDate=").append(deleteDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}