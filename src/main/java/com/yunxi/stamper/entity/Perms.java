package com.yunxi.stamper.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

public class Perms implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 权限名称
     */
    private String label;

    /**
     * 权限描述
     */
    private String remark;

    /**
     * 权限访问url,该权限主要针对哪个url
     */
    private String url;

    /**
     * 权限级别 0:顶级(平台级) 1:普通(公司级) 2:个人(个人版)  3:通用(所有人共享)
     */
    private Integer level;

    /**
     * 父级权限id,子权限不指定的情况下,默认拥有所有子级权限
     */
    @Column(name = "parent_id")
    private Integer parentId;

    /**
     * 权限唯一标识编码,指代当前权限,不可重复
     */
    private String code;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 展示次序
     */
    @Column(name = "order_no")
    private Integer orderNo;

    /**
     * 权限类型  1:菜单 2:按钮 3:功能URL
     */
    private Integer type;

    /**
     * 默认用作快捷方式展示，0不用，1用
     */
    @Column(name = "is_shortcut")
    private Integer isShortcut;

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
     * 获取权限名称
     *
     * @return label - 权限名称
     */
    public String getLabel() {
        return label;
    }

    /**
     * 设置权限名称
     *
     * @param label 权限名称
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * 获取权限描述
     *
     * @return remark - 权限描述
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置权限描述
     *
     * @param remark 权限描述
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取权限访问url,该权限主要针对哪个url
     *
     * @return url - 权限访问url,该权限主要针对哪个url
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置权限访问url,该权限主要针对哪个url
     *
     * @param url 权限访问url,该权限主要针对哪个url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取权限级别 0:顶级(平台级) 1:普通(公司级) 2:个人(个人版)  3:通用(所有人共享)
     *
     * @return level - 权限级别 0:顶级(平台级) 1:普通(公司级) 2:个人(个人版)  3:通用(所有人共享)
     */
    public Integer getLevel() {
        return level;
    }

    /**
     * 设置权限级别 0:顶级(平台级) 1:普通(公司级) 2:个人(个人版)  3:通用(所有人共享)
     *
     * @param level 权限级别 0:顶级(平台级) 1:普通(公司级) 2:个人(个人版)  3:通用(所有人共享)
     */
    public void setLevel(Integer level) {
        this.level = level;
    }

    /**
     * 获取父级权限id,子权限不指定的情况下,默认拥有所有子级权限
     *
     * @return parent_id - 父级权限id,子权限不指定的情况下,默认拥有所有子级权限
     */
    public Integer getParentId() {
        return parentId;
    }

    /**
     * 设置父级权限id,子权限不指定的情况下,默认拥有所有子级权限
     *
     * @param parentId 父级权限id,子权限不指定的情况下,默认拥有所有子级权限
     */
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    /**
     * 获取权限唯一标识编码,指代当前权限,不可重复
     *
     * @return code - 权限唯一标识编码,指代当前权限,不可重复
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置权限唯一标识编码,指代当前权限,不可重复
     *
     * @param code 权限唯一标识编码,指代当前权限,不可重复
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取菜单图标
     *
     * @return icon - 菜单图标
     */
    public String getIcon() {
        return icon;
    }

    /**
     * 设置菜单图标
     *
     * @param icon 菜单图标
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * 获取展示次序
     *
     * @return order_no - 展示次序
     */
    public Integer getOrderNo() {
        return orderNo;
    }

    /**
     * 设置展示次序
     *
     * @param orderNo 展示次序
     */
    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * 获取权限类型  1:菜单 2:按钮 3:功能URL
     *
     * @return type - 权限类型  1:菜单 2:按钮 3:功能URL
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置权限类型  1:菜单 2:按钮 3:功能URL
     *
     * @param type 权限类型  1:菜单 2:按钮 3:功能URL
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

    public Integer getIsShortcut() {
        return isShortcut;
    }

    public void setIsShortcut(Integer isShortcut) {
        this.isShortcut = isShortcut;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", label=").append(label);
        sb.append(", remark=").append(remark);
        sb.append(", url=").append(url);
        sb.append(", level=").append(level);
        sb.append(", parentId=").append(parentId);
        sb.append(", code=").append(code);
        sb.append(", icon=").append(icon);
        sb.append(", orderNo=").append(orderNo);
        sb.append(", type=").append(type);
        sb.append(", createDate=").append(createDate);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", deleteDate=").append(deleteDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}