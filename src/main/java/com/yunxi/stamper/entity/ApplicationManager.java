package com.yunxi.stamper.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "application_manager")
public class ApplicationManager implements Serializable {
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
     * 审批节点id
     */
    @Column(name = "node_id")
    private Integer nodeId;

    /**
     * 审批人id
     */
    @Column(name = "manager_id")
    private Integer managerId;

    /**
     * 审批人名称
     */
    @Column(name = "manager_name")
    private String managerName;

    /**
     * 哪个用户转交的
     */
    @Column(name = "from_user_id")
    private Integer fromUserId;

    /**
     * 转交给哪个用户
     */
    @Column(name = "push_user_id")
    private Integer pushUserId;

    /**
     * 转交人名称
     */
    @Column(name = "push_user_name")
    private String pushUserName;

    /**
     * 执行状态 1:审批中 2:审批同意 3:审批拒绝 4:审批转交
     */
    private Integer status;

    /**
     * 意见备注
     */
    private String suggest;

    /**
     * 审批同意/拒绝/转交 具体时间
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
     * 获取审批人id
     *
     * @return manager_id - 审批人id
     */
    public Integer getManagerId() {
        return managerId;
    }

    /**
     * 设置审批人id
     *
     * @param managerId 审批人id
     */
    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    /**
     * 获取审批人名称
     *
     * @return manager_name - 审批人名称
     */
    public String getManagerName() {
        return managerName;
    }

    /**
     * 设置审批人名称
     *
     * @param managerName 审批人名称
     */
    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    /**
     * 获取哪个用户转交的
     *
     * @return from_user_id - 哪个用户转交的
     */
    public Integer getFromUserId() {
        return fromUserId;
    }

    /**
     * 设置哪个用户转交的
     *
     * @param fromUserId 哪个用户转交的
     */
    public void setFromUserId(Integer fromUserId) {
        this.fromUserId = fromUserId;
    }

    /**
     * 获取转交给哪个用户
     *
     * @return push_user_id - 转交给哪个用户
     */
    public Integer getPushUserId() {
        return pushUserId;
    }

    /**
     * 设置转交给哪个用户
     *
     * @param pushUserId 转交给哪个用户
     */
    public void setPushUserId(Integer pushUserId) {
        this.pushUserId = pushUserId;
    }

    /**
     * 获取转交人名称
     *
     * @return push_user_name - 转交人名称
     */
    public String getPushUserName() {
        return pushUserName;
    }

    /**
     * 设置转交人名称
     *
     * @param pushUserName 转交人名称
     */
    public void setPushUserName(String pushUserName) {
        this.pushUserName = pushUserName;
    }

    /**
     * 获取执行状态 1:审批中 2:审批同意 3:审批拒绝 4:审批转交
     *
     * @return status - 执行状态 1:审批中 2:审批同意 3:审批拒绝 4:审批转交
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置执行状态 1:审批中 2:审批同意 3:审批拒绝 4:审批转交
     *
     * @param status 执行状态 1:审批中 2:审批同意 3:审批拒绝 4:审批转交
     */
    public void  setStatus(Integer status) {
        this.status = status;
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
     * 获取审批同意/拒绝/转交 具体时间
     *
     * @return time - 审批同意/拒绝/转交 具体时间
     */
    public Date getTime() {
        return time;
    }

    /**
     * 设置审批同意/拒绝/转交 具体时间
     *
     * @param time 审批同意/拒绝/转交 具体时间
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
        sb.append(", nodeId=").append(nodeId);
        sb.append(", managerId=").append(managerId);
        sb.append(", managerName=").append(managerName);
        sb.append(", fromUserId=").append(fromUserId);
        sb.append(", pushUserId=").append(pushUserId);
        sb.append(", pushUserName=").append(pushUserName);
        sb.append(", status=").append(status);
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