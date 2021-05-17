package com.yunxi.stamper.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "application_node")
public class ApplicationNode implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 绑定的申请单id
     */
    @Column(name = "application_id")
    private Integer applicationId;

    /**
     * 当前执行节点次序值
     */
    @Column(name = "order_no")
    private Integer orderNo;

    /**
     * 当前节点名称
     */
    private String name;

    /**
     * 当前节点标题
     */
    private String title;

    /**
     * 前端展示的图标标记 -1:等待处理 0:初始化提交 1:同意 2::拒绝 3::转交 4::取消
     */
    private Integer icon;

    /**
     * 节点处理方式 and:会签 or:或签 list:依次审批 manager:主管审批 init:初始化提交 cancel:取消申请
     */
    @Column(name = "node_type")
    private String nodeType;

    /**
     * 固定审批处理人列表
     */
    @Column(name = "manager_ids")
    private String managerIds;

    /**
     * 抽象审批组织架构层级
     */
    @Column(name = "manager_level")
    private Integer managerLevel;

    @Column(name = "department_id")
    private Integer departmentId;

    /**
     * 节点状态 -1:已处理 0:等待处理  1:未处理
     */
    private Integer handle;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "update_date")
    private Date updateDate;

    @Column(name = "delete_date")
    private Date deleteDate;

    private static final long serialVersionUID = 1L;

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
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
     * 获取绑定的申请单id
     *
     * @return application_id - 绑定的申请单id
     */
    public Integer getApplicationId() {
        return applicationId;
    }

    /**
     * 设置绑定的申请单id
     *
     * @param applicationId 绑定的申请单id
     */
    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    /**
     * 获取当前执行节点次序值
     *
     * @return order_no - 当前执行节点次序值
     */
    public Integer getOrderNo() {
        return orderNo;
    }

    /**
     * 设置当前执行节点次序值
     *
     * @param orderNo 当前执行节点次序值
     */
    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * 获取当前节点名称
     *
     * @return name - 当前节点名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置当前节点名称
     *
     * @param name 当前节点名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取当前节点标题
     *
     * @return title - 当前节点标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置当前节点标题
     *
     * @param title 当前节点标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取前端展示的图标标记 -1:等待处理 0:初始化提交 1:同意 2::拒绝 3::转交 4::取消
     *
     * @return icon - 前端展示的图标标记 -1:等待处理 0:初始化提交 1:同意 2::拒绝 3::转交 4::取消
     */
    public Integer getIcon() {
        return icon;
    }

    /**
     * 设置前端展示的图标标记 -1:等待处理 0:初始化提交 1:同意 2::拒绝 3::转交 4::取消
     *
     * @param icon 前端展示的图标标记 -1:等待处理 0:初始化提交 1:同意 2::拒绝 3::转交 4::取消
     */
    public void setIcon(Integer icon) {
        this.icon = icon;
    }

    /**
     * 获取节点处理方式 and:会签 or:或签 list:依次审批 manager:主管审批 init:初始化提交 cancel:取消申请
     *
     * @return node_type - 节点处理方式 and:会签 or:或签 list:依次审批 manager:主管审批 init:初始化提交 cancel:取消申请
     */
    public String getNodeType() {
        return nodeType;
    }

    /**
     * 设置节点处理方式 and:会签 or:或签 list:依次审批 manager:主管审批 init:初始化提交 cancel:取消申请
     *
     * @param nodeType 节点处理方式 and:会签 or:或签 list:依次审批 manager:主管审批 init:初始化提交 cancel:取消申请
     */
    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    /**
     * 获取固定审批处理人列表
     *
     * @return manager_ids - 固定审批处理人列表
     */
    public String getManagerIds() {
        return managerIds;
    }

    /**
     * 设置固定审批处理人列表
     *
     * @param managerIds 固定审批处理人列表
     */
    public void setManagerIds(String managerIds) {
        this.managerIds = managerIds;
    }

    /**
     * 获取抽象审批组织架构层级
     *
     * @return manager_level - 抽象审批组织架构层级
     */
    public Integer getManagerLevel() {
        return managerLevel;
    }

    /**
     * 设置抽象审批组织架构层级
     *
     * @param managerLevel 抽象审批组织架构层级
     */
    public void setManagerLevel(Integer managerLevel) {
        this.managerLevel = managerLevel;
    }

    /**
     * 获取节点状态 -1:已处理 0:等待处理  1:未处理
     *
     * @return handle - 节点状态 -1:已处理 0:等待处理  1:未处理
     */
    public Integer getHandle() {
        return handle;
    }

    /**
     * 设置节点状态 -1:已处理 0:处理中  1:未处理
     *
     * @param handle 节点状态 -1:已处理 0:等待处理  1:未处理
     */
    public void setHandle(Integer handle) {
        this.handle = handle;
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
        sb.append(", applicationId=").append(applicationId);
        sb.append(", orderNo=").append(orderNo);
        sb.append(", name=").append(name);
        sb.append(", title=").append(title);
        sb.append(", icon=").append(icon);
        sb.append(", nodeType=").append(nodeType);
        sb.append(", managerIds=").append(managerIds);
        sb.append(", managerLevel=").append(managerLevel);
        sb.append(", handle=").append(handle);
        sb.append(", createDate=").append(createDate);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", deleteDate=").append(deleteDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}