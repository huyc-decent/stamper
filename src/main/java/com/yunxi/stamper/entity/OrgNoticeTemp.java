package com.yunxi.stamper.entity;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "org_notice_temp")
public class OrgNoticeTemp implements Serializable {
    /**
     * 通知模板id
     */
    @Column(name = "notice_temp_id")
    private Integer noticeTempId;

    /**
     * 公司id
     */
    @Column(name = "org_id")
    private Integer orgId;

    private static final long serialVersionUID = 1L;

    /**
     * 获取通知模板id
     *
     * @return notice_temp_id - 通知模板id
     */
    public Integer getNoticeTempId() {
        return noticeTempId;
    }

    /**
     * 设置通知模板id
     *
     * @param noticeTempId 通知模板id
     */
    public void setNoticeTempId(Integer noticeTempId) {
        this.noticeTempId = noticeTempId;
    }

    /**
     * 获取公司id
     *
     * @return org_id - 公司id
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置公司id
     *
     * @param orgId 公司id
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", noticeTempId=").append(noticeTempId);
        sb.append(", orgId=").append(orgId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}