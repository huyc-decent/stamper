package com.yunxi.stamper.entity;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "org_serve")
public class OrgServe implements Serializable {
    @Column(name = "org_id")
    private Integer orgId;

    @Column(name = "serve_id")
    private Integer serveId;

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
     * @return serve_id
     */
    public Integer getServeId() {
        return serveId;
    }

    /**
     * @param serveId
     */
    public void setServeId(Integer serveId) {
        this.serveId = serveId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", orgId=").append(orgId);
        sb.append(", serveId=").append(serveId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}