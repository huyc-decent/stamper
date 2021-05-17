package com.yunxi.stamper.entity;

import com.yunxi.stamper.logger.anno.LogTag;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@LogTag("用户")
public class User implements Serializable {
    /**
     * 员工ID
     */
    @LogTag("用户Id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 用户ID
     */
    @LogTag("账号Id")
    @Column(name = "sys_user_id")
    private Integer sysUserId;

    /**
     * 登录名
     */
    @LogTag("登录名")
    @Column(name = "login_name")
    private String loginName;

    /**
     * 用户名
     */
    @LogTag("用户名")
    @Column(name = "user_name")
    private String userName;

    /**
     * 手机号
     */
    @LogTag("手机号")
    private String phone;

    /**
     * 密码
     */
    @LogTag("密码")
    private String password;

    /**
     * 集团ID
     */
    @LogTag("集团ID")
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 组织ID
     */
    @LogTag("组织ID")
    @Column(name = "department_id")
    private Integer departmentId;

    /**
     * 账户类型 0:平台版用户 1:公司版账户 2:个人版账户 3:无权限用户(刚注册的公司用户)
     */
    @LogTag("账户类型")
    private Integer type;

    /**
     * 头像
     */
    @LogTag("头像")
    @Column(name = "head_img")
    private String headImg;

    /**
     * 个推id
     */
    @LogTag("个推id")
    private String cid;

    /**
     * 备注
     */
    @LogTag("备注")
    private String remark;

    /**
     * 状态: 1:禁用 0:正常 -1:密码安全
     */
    @LogTag("状态")
    private Integer status;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "update_date")
    private Date updateDate;

    @Column(name = "delete_date")
    private Date deleteDate;

    private static final long serialVersionUID = 1L;

    /**
     * 获取员工ID
     *
     * @return id - 员工ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置员工ID
     *
     * @param id 员工ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取用户ID
     *
     * @return sys_user_id - 用户ID
     */
    public Integer getSysUserId() {
        return sysUserId;
    }

    /**
     * 设置用户ID
     *
     * @param sysUserId 用户ID
     */
    public void setSysUserId(Integer sysUserId) {
        this.sysUserId = sysUserId;
    }

    /**
     * 获取登录名
     *
     * @return login_name - 登录名
     */
    public String getLoginName() {
        return loginName;
    }

    /**
     * 设置登录名
     *
     * @param loginName 登录名
     */
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    /**
     * 获取用户名
     *
     * @return user_name - 用户名
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置用户名
     *
     * @param userName 用户名
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 获取手机号
     *
     * @return phone - 手机号
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置手机号
     *
     * @param phone 手机号
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 获取密码
     *
     * @return password - 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     *
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取集团ID
     *
     * @return org_id - 集团ID
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置集团ID
     *
     * @param orgId 集团ID
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取组织ID
     *
     * @return department_id - 组织ID
     */
    public Integer getDepartmentId() {
        return departmentId;
    }

    /**
     * 设置组织ID
     *
     * @param departmentId 组织ID
     */
    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * 获取账户类型 0:平台版用户 1:公司版账户 2:个人版账户 3:无权限用户(刚注册的公司用户)
     *
     * @return type - 账户类型 0:平台版用户 1:公司版账户 2:个人版账户 3:无权限用户(刚注册的公司用户)
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置账户类型 0:平台版用户 1:公司版账户 2:个人版账户 3:无权限用户(刚注册的公司用户)
     *
     * @param type 账户类型 0:平台版用户 1:公司版账户 2:个人版账户 3:无权限用户(刚注册的公司用户)
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取头像
     *
     * @return head_img - 头像
     */
    public String getHeadImg() {
        return headImg;
    }

    /**
     * 设置头像
     *
     * @param headImg 头像
     */
    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    /**
     * 获取个推id
     *
     * @return cid - 个推id
     */
    public String getCid() {
        return cid;
    }

    /**
     * 设置个推id
     *
     * @param cid 个推id
     */
    public void setCid(String cid) {
        this.cid = cid;
    }

    /**
     * 获取备注
     *
     * @return remark - 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取状态: 1:禁用 0:正常 -1:密码安全
     *
     * @return status - 状态: 1:禁用 0:正常 -1:密码安全
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置状态: 1:禁用 0:正常 -1:密码安全
     *
     * @param status 状态: 1:禁用 0:正常 -1:密码安全
     */
    public void setStatus(Integer status) {
        this.status = status;
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
        sb.append(", sysUserId=").append(sysUserId);
        sb.append(", loginName=").append(loginName);
        sb.append(", userName=").append(userName);
        sb.append(", phone=").append(phone);
        sb.append(", password=").append(password);
        sb.append(", orgId=").append(orgId);
        sb.append(", departmentId=").append(departmentId);
        sb.append(", type=").append(type);
        sb.append(", headImg=").append(headImg);
        sb.append(", cid=").append(cid);
        sb.append(", remark=").append(remark);
        sb.append(", status=").append(status);
        sb.append(", createDate=").append(createDate);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", deleteDate=").append(deleteDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}