package com.yunxi.stamper.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

public class Serve implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 服务名称
     */
    private String name;

    /**
     * 业务服务编码
     */
    private String code;

    /**
     * 服务描述
     */
    private String remark;

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
     * 获取服务名称
     *
     * @return name - 服务名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置服务名称
     *
     * @param name 服务名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取业务服务编码
     *
     * @return code - 业务服务编码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置业务服务编码
     *
     * @param code 业务服务编码
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取服务描述
     *
     * @return remark - 服务描述
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置服务描述
     *
     * @param remark 服务描述
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", code=").append(code);
        sb.append(", remark=").append(remark);
        sb.append(", createDate=").append(createDate);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", deleteDate=").append(deleteDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}