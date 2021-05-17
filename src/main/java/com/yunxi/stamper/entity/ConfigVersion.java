package com.yunxi.stamper.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "config_version")
public class ConfigVersion implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 0:安卓系统更新包 1:Linux系统更新包
     */
    @Column(name = "`type`")
    private Integer type;

    /**
     * 版本号
     */
    private String version;

    /**
     * 更新地址
     */
    private String url;

    /**
     * 版本描述
     */
    private String remark;

    private String hash;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

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
     * 获取版本号
     *
     * @return version - 版本号
     */
    public String getVersion() {
        return version;
    }

    /**
     * 设置版本号
     *
     * @param version 版本号
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 获取更新地址
     *
     * @return url - 更新地址
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置更新地址
     *
     * @param url 更新地址
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取版本描述
     *
     * @return remark - 版本描述
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置版本描述
     *
     * @param remark 版本描述
     */
    public void setRemark(String remark) {
        this.remark = remark;
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


    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        String sb = "ConfigVersion{" + "id=" + id +
                ", type=" + type +
                ", version='" + version + '\'' +
                ", url='" + url + '\'' +
                ", remark='" + remark + '\'' +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                ", deleteDate=" + deleteDate +
                '}';
        return sb;
    }

}
