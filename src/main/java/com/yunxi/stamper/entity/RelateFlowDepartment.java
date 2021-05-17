package com.yunxi.stamper.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "relate_flow_department")
public class RelateFlowDepartment implements Serializable {
    /**
     * 审批流程ID
     */
    @Id
    @Column(name = "flow_id")
    private Integer flowId;

    /**
     * 组织ID
     */
    @Column(name = "department_id")
    private Integer departmentId;

    private static final long serialVersionUID = 1L;

    /**
     * 获取审批流程ID
     *
     * @return flow_id - 审批流程ID
     */
    public Integer getFlowId() {
        return flowId;
    }

    /**
     * 设置审批流程ID
     *
     * @param flowId 审批流程ID
     */
    public void setFlowId(Integer flowId) {
        this.flowId = flowId;
    }

    /**
     * 获取组织ID
     *
     * @return department_id - 组织ID
     */
    public Integer getDepartmentId() {
        return departmentId;
    }

    /**
     * 设置组织ID
     *
     * @param departmentId 组织ID
     */
    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", flowId=").append(flowId);
        sb.append(", departmentId=").append(departmentId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}