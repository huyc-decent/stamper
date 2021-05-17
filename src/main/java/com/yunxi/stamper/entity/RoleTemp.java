package com.yunxi.stamper.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "role_temp")
public class RoleTemp implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 角色类型名称
     */
    private String name;

    /**
     * 类型编码
     */
    private String code;

    /**
     * 默认关联的权限id列表,以逗号分隔
     */
    @Column(name = "perm_ids")
    private String permIds;

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
     * 获取角色类型名称
     *
     * @return name - 角色类型名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置角色类型名称
     *
     * @param name 角色类型名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取类型编码
     *
     * @return code - 类型编码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置类型编码
     *
     * @param code 类型编码
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取默认关联的权限id列表,以逗号分隔
     *
     * @return perm_ids - 默认关联的权限id列表,以逗号分隔
     */
    public String getPermIds() {
        return permIds;
    }

    /**
     * 设置默认关联的权限id列表,以逗号分隔
     *
     * @param permIds 默认关联的权限id列表,以逗号分隔
     */
    public void setPermIds(String permIds) {
        this.permIds = permIds;
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
        sb.append(", permIds=").append(permIds);
        sb.append(", createDate=").append(createDate);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", deleteDate=").append(deleteDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}