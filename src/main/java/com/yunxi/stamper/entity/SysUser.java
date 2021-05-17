package com.yunxi.stamper.entity;

import com.yunxi.stamper.logger.anno.LogTag;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@LogTag("账号")
@Table(name = "sys_user")
public class SysUser implements Serializable {
    /**
     * 所属用户ID
     */
    @LogTag("账号Id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 登录号(手机号)
     */
    @LogTag("手机号码")
    private String phone;

    /**
     * 登录密码
     */
    @LogTag("登录密码")
    private String password;

    @LogTag("默认集团Id")
    @Column(name = "default_org_id")
    private Integer defaultOrgId;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "update_date")
    private Date updateDate;

    @Column(name = "delete_date")
    private Date deleteDate;

    private static final long serialVersionUID = 1L;

    public Integer getDefaultOrgId() {
        return defaultOrgId;
    }

    public void setDefaultOrgId(Integer defaultOrgId) {
        this.defaultOrgId = defaultOrgId;
    }

    /**
     * 获取所属用户ID
     *
     * @return id - 所属用户ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置所属用户ID
     *
     * @param id 所属用户ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取登录号(手机号)
     *
     * @return phone - 登录号(手机号)
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置登录号(手机号)
     *
     * @param phone 登录号(手机号)
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 获取登录密码
     *
     * @return password - 登录密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置登录密码
     *
     * @param password 登录密码
     */
    public void setPassword(String password) {
        this.password = password;
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
        sb.append(", phone=").append(phone);
        sb.append(", password=").append(password);
        sb.append(", createDate=").append(createDate);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", deleteDate=").append(deleteDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}