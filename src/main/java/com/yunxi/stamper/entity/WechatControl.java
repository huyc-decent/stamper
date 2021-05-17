package com.yunxi.stamper.entity;

import com.yunxi.stamper.logger.anno.LogTag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@LogTag("小程序")
@Table(name = "wechat_control")
public class WechatControl implements Serializable {

    @Id
    @LogTag("小程序记录Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 客户组织名称
     */
    @LogTag("客户组织名称")
    @Column(name = "customer_organization_name")
    private String customerOrganizationName;

    /**
     * 客户联系人，多个以逗号分隔
     */
    @LogTag("客户联系人")
    @Column(name = "customer_name")
    private String customerName;

    /**
     * 客户联系方式,多个以逗号分隔
     */
    @LogTag("客户联系方式")
    @Column(name = "customer_phone")
    private String customerPhone;

    /**
     * 自定义的小程序openId，主要用于区分不同的客户
     */
    @LogTag("小程序openId")
    @Column(name = "customer_wx_open_id")
    private String customerWxOpenId;

    /**
     * 自定义的小程序openId，主要用于区分不同的客户
     */
    @LogTag("小程序对端服务url前缀")
    @Column(name = "customer_service_url_prefix")
    private String customerServiceUrlPrefix;

    /**
     * 维护负责人姓名,多个以逗号分隔
     */
    @LogTag("负责人姓名")
    @Column(name = "service_staff_name")
    private String serviceStaffName;

    /**
     * 维护负责人联系方式,多个以逗号分隔
     */
    @LogTag("负责人联系方式")
    @Column(name = "service_staff_phone")
    private String serviceStaffPhone;

    /**
     * 生效起始时间
     */
    @LogTag("生效起始时间")
    @Column(name = "active_time")
    private Date activeTime;

    /**
     * 生效结束时间
     */
    @LogTag("生效结束时间")
    @Column(name = "expiry_time")
    private Date expiryTime;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "update_date")
    private Date updateDate;

    @Column(name = "delete_date")
    private Date deleteDate;

    private static final long serialVersionUID = 1L;
}