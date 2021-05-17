package com.yunxi.stamper.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(name = "org_sms_temp")
public class OrgSmsTemp implements Serializable {
    /**
     * 短信模板id
     */
    @Column(name = "sms_temp_id")
    private Integer smsTempId;

    /**
     * 组织id
     */
    @Column(name = "org_id")
    private Integer orgId;

    private static final long serialVersionUID = 1L;
}