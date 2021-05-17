package com.yunxi.stamper.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

public class Meter implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "update_by")
    private Integer updateBy;

    @Column(name = "update_date")
    private Date updateDate;

    @Column(name = "update_name")
    private String updateName;

    @Column(name = "delete_by")
    private Integer deleteBy;

    @Column(name = "delete_date")
    private Date deleteDate;

    @Column(name = "delete_name")
    private String deleteName;

    /**
     * 设备类型id
     */
    @Column(name = "device_type_id")
    private Integer deviceTypeId;

    /**
     * 所属公司id
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 部门ID
     */
    @Column(name = "depart_id")
    private Integer departId;

    /**
     * 管理员(所属人)id
     */
    @Column(name = "owner_id")
    private Integer ownerId;

    /**
     * 高拍仪名称
     */
    private String name;

    /**
     * 描述
     */
    private String remark;

    /**
     * 高拍仪图标
     */
    private String logo;

    /**
     * 印章唯一码
     */
    @Column(name = "device_uuid")
    private String deviceUuid;

    /**
     * sim电话卡号码
     */
    @Column(name = "sim_num")
    private String simNum;

    /**
     * 印章所在坐标id
     */
    @Column(name = "addr_id")
    private Integer addrId;

    /**
     * 高拍仪状态 0:正常 1:异常 2:销毁 3:停用
     */
    private Integer status;

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
     * @return update_by
     */
    public Integer getUpdateBy() {
        return updateBy;
    }

    /**
     * @param updateBy
     */
    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
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
     * @return update_name
     */
    public String getUpdateName() {
        return updateName;
    }

    /**
     * @param updateName
     */
    public void setUpdateName(String updateName) {
        this.updateName = updateName;
    }

    /**
     * @return delete_by
     */
    public Integer getDeleteBy() {
        return deleteBy;
    }

    /**
     * @param deleteBy
     */
    public void setDeleteBy(Integer deleteBy) {
        this.deleteBy = deleteBy;
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

    /**
     * @return delete_name
     */
    public String getDeleteName() {
        return deleteName;
    }

    /**
     * @param deleteName
     */
    public void setDeleteName(String deleteName) {
        this.deleteName = deleteName;
    }

    /**
     * 获取设备类型id
     *
     * @return device_type_id - 设备类型id
     */
    public Integer getDeviceTypeId() {
        return deviceTypeId;
    }

    /**
     * 设置设备类型id
     *
     * @param deviceTypeId 设备类型id
     */
    public void setDeviceTypeId(Integer deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
    }

    /**
     * 获取所属公司id
     *
     * @return org_id - 所属公司id
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置所属公司id
     *
     * @param orgId 所属公司id
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取部门ID
     *
     * @return depart_id - 部门ID
     */
    public Integer getDepartId() {
        return departId;
    }

    /**
     * 设置部门ID
     *
     * @param departId 部门ID
     */
    public void setDepartId(Integer departId) {
        this.departId = departId;
    }

    /**
     * 获取管理员(所属人)id
     *
     * @return owner_id - 管理员(所属人)id
     */
    public Integer getOwnerId() {
        return ownerId;
    }

    /**
     * 设置管理员(所属人)id
     *
     * @param ownerId 管理员(所属人)id
     */
    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * 获取高拍仪名称
     *
     * @return name - 高拍仪名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置高拍仪名称
     *
     * @param name 高拍仪名称
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
     * 获取高拍仪图标
     *
     * @return logo - 高拍仪图标
     */
    public String getLogo() {
        return logo;
    }

    /**
     * 设置高拍仪图标
     *
     * @param logo 高拍仪图标
     */
    public void setLogo(String logo) {
        this.logo = logo;
    }

    /**
     * 获取印章唯一码
     *
     * @return device_uuid - 印章唯一码
     */
    public String getDeviceUuid() {
        return deviceUuid;
    }

    /**
     * 设置印章唯一码
     *
     * @param deviceUuid 印章唯一码
     */
    public void setDeviceUuid(String deviceUuid) {
        this.deviceUuid = deviceUuid;
    }

    /**
     * 获取sim电话卡号码
     *
     * @return sim_num - sim电话卡号码
     */
    public String getSimNum() {
        return simNum;
    }

    /**
     * 设置sim电话卡号码
     *
     * @param simNum sim电话卡号码
     */
    public void setSimNum(String simNum) {
        this.simNum = simNum;
    }

    /**
     * 获取印章所在坐标id
     *
     * @return addr_id - 印章所在坐标id
     */
    public Integer getAddrId() {
        return addrId;
    }

    /**
     * 设置印章所在坐标id
     *
     * @param addrId 印章所在坐标id
     */
    public void setAddrId(Integer addrId) {
        this.addrId = addrId;
    }

    /**
     * 获取高拍仪状态 0:正常 1:异常 2:销毁 3:停用
     *
     * @return status - 高拍仪状态 0:正常 1:异常 2:销毁 3:停用
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置高拍仪状态 0:正常 1:异常 2:销毁 3:停用
     *
     * @param status 高拍仪状态 0:正常 1:异常 2:销毁 3:停用
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", createDate=").append(createDate);
        sb.append(", updateBy=").append(updateBy);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", updateName=").append(updateName);
        sb.append(", deleteBy=").append(deleteBy);
        sb.append(", deleteDate=").append(deleteDate);
        sb.append(", deleteName=").append(deleteName);
        sb.append(", deviceTypeId=").append(deviceTypeId);
        sb.append(", orgId=").append(orgId);
        sb.append(", departId=").append(departId);
        sb.append(", ownerId=").append(ownerId);
        sb.append(", name=").append(name);
        sb.append(", remark=").append(remark);
        sb.append(", logo=").append(logo);
        sb.append(", deviceUuid=").append(deviceUuid);
        sb.append(", simNum=").append(simNum);
        sb.append(", addrId=").append(addrId);
        sb.append(", status=").append(status);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}