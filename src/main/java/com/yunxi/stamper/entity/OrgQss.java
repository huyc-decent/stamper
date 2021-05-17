package com.yunxi.stamper.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "org_qss")
public class OrgQss implements Serializable {
    @Id
    @Column(name = "org_id")
    private Integer orgId;

    @Column(name = "qss_id")
    private Integer qssId;

    private static final long serialVersionUID = 1L;

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
     * @return qss_id
     */
    public Integer getQssId() {
        return qssId;
    }

    /**
     * @param qssId
     */
    public void setQssId(Integer qssId) {
        this.qssId = qssId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", orgId=").append(orgId);
        sb.append(", qssId=").append(qssId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}