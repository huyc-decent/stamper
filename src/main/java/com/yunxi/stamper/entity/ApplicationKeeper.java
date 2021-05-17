package com.yunxi.stamper.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "application_keeper")
public class ApplicationKeeper implements Serializable {
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
     * 这条记录处理人(管章人)id
     */
    @Column(name = "keeper_id")
    private Integer keeperId;

    /**
     * 授权人名称
     */
    @Column(name = "keeper_name")
    private String keeperName;

    /**
     * 所管理的章
     */
    @Column(name = "device_id")
    private Integer deviceId;

    /**
     * 印章名称
     */
    @Column(name = "device_name")
    private String deviceName;

    /**
     * 1:授权中 2:授权同意 3:授权拒绝 4:已失效
     */
    private Integer status;

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
     * 获取这条记录处理人(管章人)id
     *
     * @return keeper_id - 这条记录处理人(管章人)id
     */
    public Integer getKeeperId() {
        return keeperId;
    }

    /**
     * 设置这条记录处理人(管章人)id
     *
     * @param keeperId 这条记录处理人(管章人)id
     */
    public void setKeeperId(Integer keeperId) {
        this.keeperId = keeperId;
    }

    /**
     * 获取授权人名称
     *
     * @return keeper_name - 授权人名称
     */
    public String getKeeperName() {
        return keeperName;
    }

    /**
     * 设置授权人名称
     *
     * @param keeperName 授权人名称
     */
    public void setKeeperName(String keeperName) {
        this.keeperName = keeperName;
    }

    /**
     * 获取所管理的章
     *
     * @return device_id - 所管理的章
     */
    public Integer getDeviceId() {
        return deviceId;
    }

    /**
     * 设置所管理的章
     *
     * @param deviceId 所管理的章
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
     * 获取1:授权中 2:授权同意 3:授权拒绝 4:已失效
     *
     * @return status - 1:授权中 2:授权同意 3:授权拒绝 4:已失效
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置1:授权中 2:授权同意 3:授权拒绝 4:已失效
     *
     * @param status 1:授权中 2:授权同意 3:授权拒绝 4:已失效
     */
    public void setStatus(Integer status) {
        this.status = status;
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
        sb.append(", keeperId=").append(keeperId);
        sb.append(", keeperName=").append(keeperName);
        sb.append(", deviceId=").append(deviceId);
        sb.append(", deviceName=").append(deviceName);
        sb.append(", status=").append(status);
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