package com.yunxi.stamper.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "role_perms")
public class RolePerms implements Serializable {
    @Id
    @Column(name = "role_id")
    private Integer roleId;

    @Id
    @Column(name = "perms_id")
    private Integer permsId;

    private static final long serialVersionUID = 1L;

    /**
     * @return role_id
     */
    public Integer getRoleId() {
        return roleId;
    }

    /**
     * @param roleId
     */
    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    /**
     * @return perms_id
     */
    public Integer getPermsId() {
        return permsId;
    }

    /**
     * @param permsId
     */
    public void setPermsId(Integer permsId) {
        this.permsId = permsId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", roleId=").append(roleId);
        sb.append(", permsId=").append(permsId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}