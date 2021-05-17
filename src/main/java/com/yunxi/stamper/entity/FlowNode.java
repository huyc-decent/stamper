package com.yunxi.stamper.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "flow_node")
public class FlowNode implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 所属的流程id
     */
    @Column(name = "flow_id")
    private Integer flowId;

    /**
     * 审批次序 0-n
     */
    @Column(name = "order_no")
    private Integer orderNo;

    /**
     * 当前节点 审批人列表{id:name}json字符串
     */
    @Column(name = "manager_json")
    private String managerJson;

    /**
     * 对应的审批类型
     */
    private String type;

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
     * 获取所属的流程id
     *
     * @return flow_id - 所属的流程id
     */
    public Integer getFlowId() {
        return flowId;
    }

    /**
     * 设置所属的流程id
     *
     * @param flowId 所属的流程id
     */
    public void setFlowId(Integer flowId) {
        this.flowId = flowId;
    }

    /**
     * 获取审批次序 0-n
     *
     * @return order_no - 审批次序 0-n
     */
    public Integer getOrderNo() {
        return orderNo;
    }

    /**
     * 设置审批次序 0-n
     *
     * @param orderNo 审批次序 0-n
     */
    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * 获取当前节点 审批人列表{id:name}json字符串
     *
     * @return manager_json - 当前节点 审批人列表{id:name}json字符串
     */
    public String getManagerJson() {
        return managerJson;
    }

    /**
     * 设置当前节点 审批人列表{id:name}json字符串
     *
     * @param managerJson 当前节点 审批人列表{id:name}json字符串
     */
    public void setManagerJson(String managerJson) {
        this.managerJson = managerJson;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        sb.append(", flowId=").append(flowId);
        sb.append(", orderNo=").append(orderNo);
        sb.append(", managerJson=").append(managerJson);
        sb.append(", type=").append(type);
        sb.append(", createDate=").append(createDate);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", deleteDate=").append(deleteDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}