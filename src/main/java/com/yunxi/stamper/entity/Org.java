package com.yunxi.stamper.entity;

import com.yunxi.stamper.logger.anno.LogTag;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@LogTag("集团")
public class Org implements Serializable {

    @LogTag("集团id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    /**
     * 组织名称
     */
    @LogTag("集团名称")
    private String name;

    /**
     * 组织备注
     */
    @LogTag("集团描述")
    private String remark;

    /**
     * 组织logo
     */
    @LogTag("集团logo")
    private String logo;

    /**
     * 组织地址
     */
    @LogTag("集团地址")
    private String location;

    /**
     * 组织联系方式
     */
    @LogTag("集团联系方式")
    private String phone;

    /**
     * 组织类型
     */
    @LogTag("集团类型")
    @Column(name = "org_type")
    private Integer orgType;

    /**
     * 组织编码
     */
    @LogTag("集团编码")
    private String code;

    /**
     * 公司状态 0:正常  1:停用  2:注销
     */
    @LogTag("集团状态")
    private Integer status;

    @LogTag("领导岗职称id")
    @Column(name = "position_id")
    private Integer positionId;

    /**
     * 管理人id
     */
    @LogTag("属主id")
    @Column(name = "manager_user_id")
    private Integer managerUserId;

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

    public Integer getPositionId() {
        return positionId;
    }

    public void setPositionId(Integer positionId) {
        this.positionId = positionId;
    }

    /**
     * 获取组织名称
     *
     * @return name - 组织名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置组织名称
     *
     * @param name 组织名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取组织备注
     *
     * @return remark - 组织备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置组织备注
     *
     * @param remark 组织备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取组织logo
     *
     * @return logo - 组织logo
     */
    public String getLogo() {
        return logo;
    }

    /**
     * 设置组织logo
     *
     * @param logo 组织logo
     */
    public void setLogo(String logo) {
        this.logo = logo;
    }

    /**
     * 获取组织地址
     *
     * @return location - 组织地址
     */
    public String getLocation() {
        return location;
    }

    /**
     * 设置组织地址
     *
     * @param location 组织地址
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * 获取组织联系方式
     *
     * @return phone - 组织联系方式
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置组织联系方式
     *
     * @param phone 组织联系方式
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 获取组织类型
     *
     * @return org_type - 组织类型
     */
    public Integer getOrgType() {
        return orgType;
    }

    /**
     * 设置组织类型
     *
     * @param orgType 组织类型
     */
    public void setOrgType(Integer orgType) {
        this.orgType = orgType;
    }

    /**
     * 获取组织编码
     *
     * @return code - 组织编码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置组织编码
     *
     * @param code 组织编码
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取公司状态 0:正常  1:停用  2:注销
     *
     * @return status - 公司状态 0:正常  1:停用  2:注销
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置公司状态 0:正常  1:停用  2:注销
     *
     * @param status 公司状态 0:正常  1:停用  2:注销
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取管理人id
     *
     * @return manager_user_id - 管理人id
     */
    public Integer getManagerUserId() {
        return managerUserId;
    }

    /**
     * 设置管理人id
     *
     * @param managerUserId 管理人id
     */
    public void setManagerUserId(Integer managerUserId) {
        this.managerUserId = managerUserId;
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
        String sb = "Org{" + "id=" + id +
                ", name='" + name + '\'' +
                ", remark='" + remark + '\'' +
                ", logo='" + logo + '\'' +
                ", location='" + location + '\'' +
                ", phone='" + phone + '\'' +
                ", orgType=" + orgType +
                ", code='" + code + '\'' +
                ", status=" + status +
                ", positionId=" + positionId +
                ", managerUserId=" + managerUserId +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                ", deleteDate=" + deleteDate +
                '}';
        return sb;
    }
}
