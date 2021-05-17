package com.yunxi.stamper.entity;

import com.yunxi.stamper.logger.anno.LogTag;
import lombok.extern.java.Log;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@LogTag("申请单")
public class Application implements Serializable {

	@LogTag("申请单ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 所属公司
     */
    @LogTag("集团id")
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 部门id
     */
    @LogTag("组织id")
    @Column(name = "department_id")
    private Integer departmentId;

    /**
     * 申请人id
     */
    @LogTag("申请人id")
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 申请人名称
     */
    @LogTag("申请人")
    @Column(name = "user_name")
    private String userName;

    /**
     * 申请次数
     */
    @LogTag("次数")
    @Column(name = "user_count")
    private Integer userCount;

    @LogTag("标题")
    private String title;

    @LogTag("内容")
    private String content;

    /**
     * 申请单状态 0:初始化提交 1:审批中 2:审批通过 3:审批拒绝  4:授权中 5:授权通过 6:授权拒绝  7:已推送  8:用章中 9:用章中 10:审计中 11:审计通过 12:审计拒绝 13:已失效
     */
    @LogTag("状态")
    @Column(name = "`status`")
    private Integer status;

    /**
     * 加密类型id
     */
    @Column(name = "encrypt_id")
    private Integer encryptId;

    /**
     * 审批流程id
     */
    @LogTag("审批流程id")
    @Column(name = "process_id")
    private Integer processId;

    /**
     * 当前所处于节点
     */
    @LogTag("审批节点id")
    @Column(name = "node_id")
    private Integer nodeId;

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
     * 获取所属公司
     *
     * @return org_id - 所属公司
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置所属公司
     *
     * @param orgId 所属公司
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取部门id
     *
     * @return department_id - 部门id
     */
    public Integer getDepartmentId() {
        return departmentId;
    }

    /**
     * 设置部门id
     *
     * @param departmentId 部门id
     */
    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * 获取申请人id
     *
     * @return user_id - 申请人id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置申请人id
     *
     * @param userId 申请人id
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取申请人名称
     *
     * @return user_name - 申请人名称
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置申请人名称
     *
     * @param userName 申请人名称
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 获取申请次数
     *
     * @return user_count - 申请次数
     */
    public Integer getUserCount() {
        return userCount;
    }

    /**
     * 设置申请次数
     *
     * @param userCount 申请次数
     */
    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    /**
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取申请单状态 0:初始化提交 1:审批中 2:审批通过 3:审批拒绝  4:授权中 5:授权通过 6:授权拒绝  7:已推送  8:用章中 9:用章中 10:审计中 11:审计通过 12:审计拒绝 13:已失效
     *
     * @return status - 申请单状态 0:初始化提交 1:审批中 2:审批通过 3:审批拒绝  4:授权中 5:授权通过 6:授权拒绝  7:已推送  8:用章中 9:用章中 10:审计中 11:审计通过 12:审计拒绝 13:已失效
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置申请单状态 0:初始化提交 1:审批中 2:审批通过 3:审批拒绝  4:授权中 5:授权通过 6:授权拒绝  7:已推送  8:用章中 9:用章中 10:审计中 11:审计通过 12:审计拒绝 13:已失效
     *
     * @param status 申请单状态 0:初始化提交 1:审批中 2:审批通过 3:审批拒绝  4:授权中 5:授权通过 6:授权拒绝  7:已推送  8:用章中 9:用章中 10:审计中 11:审计通过 12:审计拒绝 13:已失效
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取加密类型id
     *
     * @return encrypt_id - 加密类型id
     */
    public Integer getEncryptId() {
        return encryptId;
    }

    /**
     * 设置加密类型id
     *
     * @param encryptId 加密类型id
     */
    public void setEncryptId(Integer encryptId) {
        this.encryptId = encryptId;
    }

    /**
     * 获取审批流程id
     *
     * @return process_id - 审批流程id
     */
    public Integer getProcessId() {
        return processId;
    }

    /**
     * 设置审批流程id
     *
     * @param processId 审批流程id
     */
    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    /**
     * 获取当前所处于节点
     *
     * @return node_id - 当前所处于节点
     */
    public Integer getNodeId() {
        return nodeId;
    }

    /**
     * 设置当前所处于节点
     *
     * @param nodeId 当前所处于节点
     */
    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
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
        sb.append(", departmentId=").append(departmentId);
        sb.append(", userId=").append(userId);
        sb.append(", userName=").append(userName);
        sb.append(", userCount=").append(userCount);
        sb.append(", title=").append(title);
        sb.append(", content=").append(content);
        sb.append(", status=").append(status);
        sb.append(", encryptId=").append(encryptId);
        sb.append(", processId=").append(processId);
        sb.append(", nodeId=").append(nodeId);
        sb.append(", createDate=").append(createDate);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", deleteDate=").append(deleteDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}