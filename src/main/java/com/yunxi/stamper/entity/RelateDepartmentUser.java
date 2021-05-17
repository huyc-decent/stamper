package com.yunxi.stamper.entity;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "relate_department_user")
public class RelateDepartmentUser implements Serializable {
    /**
     * 员工id
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 组织ID
     */
    @Column(name = "department_id")
    private Integer departmentId;

    private static final long serialVersionUID = 1L;

    /**
     * 获取员工id
     *
     * @return user_id - 员工id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置员工id
     *
     * @param userId 员工id
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
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
        sb.append(", userId=").append(userId);
        sb.append(", departmentId=").append(departmentId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}