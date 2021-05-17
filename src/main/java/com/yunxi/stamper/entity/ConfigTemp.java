package com.yunxi.stamper.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "config_temp")
public class ConfigTemp implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 配置模板名称
     */
    private String name;

    /**
     * 描述
     */
    private String remark;

    /**
     * 配置服务器URL(比如:获取设备配置信息等)
     */
    @Column(name = "config_url")
    private String configUrl;

    /**
     * 业务服务器URL(比如:websocket心跳链接等)
     */
    @Column(name = "service_url")
    private String serviceUrl;

    /**
     * 回调地址URL(比如:指纹录入响应回调,删除回调等...)
     */
    @Column(name = "third_url")
    private String thirdUrl;

    /**
     * (申请单)使用记录上传URL
     */
    @Column(name = "seal_record_info_normal_url")
    private String sealRecordInfoNormalUrl;

    /**
     * 审计记录上传URL
     */
    @Column(name = "seal_record_info_auditor_url")
    private String sealRecordInfoAuditorUrl;

    /**
     * (指纹模式)审计记录上传URL
     */
    @Column(name = "seal_record_info_easy_url")
    private String sealRecordInfoEasyUrl;

    /**
     * 后台版本标记 0:旧版本后台  1:新版本后台
     */
    private Integer status;

    /**
     * 模板类型  0:默认模板 1:自定义模板
     */
    private Integer type;

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
     * 获取配置模板名称
     *
     * @return name - 配置模板名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置配置模板名称
     *
     * @param name 配置模板名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取描述
     *
     * @return remark - 描述
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置描述
     *
     * @param remark 描述
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取配置服务器URL(比如:获取设备配置信息等)
     *
     * @return config_url - 配置服务器URL(比如:获取设备配置信息等)
     */
    public String getConfigUrl() {
        return configUrl;
    }

    /**
     * 设置配置服务器URL(比如:获取设备配置信息等)
     *
     * @param configUrl 配置服务器URL(比如:获取设备配置信息等)
     */
    public void setConfigUrl(String configUrl) {
        this.configUrl = configUrl;
    }

    /**
     * 获取业务服务器URL(比如:websocket心跳链接等)
     *
     * @return service_url - 业务服务器URL(比如:websocket心跳链接等)
     */
    public String getServiceUrl() {
        return serviceUrl;
    }

    /**
     * 设置业务服务器URL(比如:websocket心跳链接等)
     *
     * @param serviceUrl 业务服务器URL(比如:websocket心跳链接等)
     */
    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    /**
     * 获取回调地址URL(比如:指纹录入响应回调,删除回调等...)
     *
     * @return third_url - 回调地址URL(比如:指纹录入响应回调,删除回调等...)
     */
    public String getThirdUrl() {
        return thirdUrl;
    }

    /**
     * 设置回调地址URL(比如:指纹录入响应回调,删除回调等...)
     *
     * @param thirdUrl 回调地址URL(比如:指纹录入响应回调,删除回调等...)
     */
    public void setThirdUrl(String thirdUrl) {
        this.thirdUrl = thirdUrl;
    }

    /**
     * 获取(申请单)使用记录上传URL
     *
     * @return seal_record_info_normal_url - (申请单)使用记录上传URL
     */
    public String getSealRecordInfoNormalUrl() {
        return sealRecordInfoNormalUrl;
    }

    /**
     * 设置(申请单)使用记录上传URL
     *
     * @param sealRecordInfoNormalUrl (申请单)使用记录上传URL
     */
    public void setSealRecordInfoNormalUrl(String sealRecordInfoNormalUrl) {
        this.sealRecordInfoNormalUrl = sealRecordInfoNormalUrl;
    }

    /**
     * 获取审计记录上传URL
     *
     * @return seal_record_info_auditor_url - 审计记录上传URL
     */
    public String getSealRecordInfoAuditorUrl() {
        return sealRecordInfoAuditorUrl;
    }

    /**
     * 设置审计记录上传URL
     *
     * @param sealRecordInfoAuditorUrl 审计记录上传URL
     */
    public void setSealRecordInfoAuditorUrl(String sealRecordInfoAuditorUrl) {
        this.sealRecordInfoAuditorUrl = sealRecordInfoAuditorUrl;
    }

    /**
     * 获取(指纹模式)审计记录上传URL
     *
     * @return seal_record_info_easy_url - (指纹模式)审计记录上传URL
     */
    public String getSealRecordInfoEasyUrl() {
        return sealRecordInfoEasyUrl;
    }

    /**
     * 设置(指纹模式)审计记录上传URL
     *
     * @param sealRecordInfoEasyUrl (指纹模式)审计记录上传URL
     */
    public void setSealRecordInfoEasyUrl(String sealRecordInfoEasyUrl) {
        this.sealRecordInfoEasyUrl = sealRecordInfoEasyUrl;
    }

    /**
     * 获取后台版本标记 0:旧版本后台  1:新版本后台
     *
     * @return status - 后台版本标记 0:旧版本后台  1:新版本后台
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置后台版本标记 0:旧版本后台  1:新版本后台
     *
     * @param status 后台版本标记 0:旧版本后台  1:新版本后台
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取模板类型  0:默认模板 1:自定义模板
     *
     * @return type - 模板类型  0:默认模板 1:自定义模板
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置模板类型  0:默认模板 1:自定义模板
     *
     * @param type 模板类型  0:默认模板 1:自定义模板
     */
    public void setType(Integer type) {
        this.type = type;
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
        sb.append(", name=").append(name);
        sb.append(", remark=").append(remark);
        sb.append(", configUrl=").append(configUrl);
        sb.append(", serviceUrl=").append(serviceUrl);
        sb.append(", thirdUrl=").append(thirdUrl);
        sb.append(", sealRecordInfoNormalUrl=").append(sealRecordInfoNormalUrl);
        sb.append(", sealRecordInfoAuditorUrl=").append(sealRecordInfoAuditorUrl);
        sb.append(", sealRecordInfoEasyUrl=").append(sealRecordInfoEasyUrl);
        sb.append(", status=").append(status);
        sb.append(", type=").append(type);
        sb.append(", createDate=").append(createDate);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", deleteDate=").append(deleteDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}