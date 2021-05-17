package com.yunxi.stamper.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "user_logger")
public class UserLogger implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 操作用户所属公司id
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 操作用户id
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 操作用户名称
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 客户端方式 0:浏览器 1:App
     */
    private String client;

    /**
     * 请求参数
     */
    private String args;

    /**
     * 请求URL
     */
    private String url;

    /**
     * 请求ip
     */
    private String ip;

    /**
     * 响应状态码 0:成功 1:失败 2:异常
     */
    private Integer status;

    /**
     * 响应描述 ['密码错误','无权限','响应成功'...]
     */
    private String remark;

    /**
     * 异常信息
     */
    private String error;

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
     * 获取操作用户所属公司id
     *
     * @return org_id - 操作用户所属公司id
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置操作用户所属公司id
     *
     * @param orgId 操作用户所属公司id
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取操作用户id
     *
     * @return user_id - 操作用户id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置操作用户id
     *
     * @param userId 操作用户id
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取操作用户名称
     *
     * @return user_name - 操作用户名称
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置操作用户名称
     *
     * @param userName 操作用户名称
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 获取客户端方式 0:浏览器 1:App
     *
     * @return client - 客户端方式 0:浏览器 1:App
     */
    public String getClient() {
        return client;
    }

    /**
     * 设置客户端方式 0:浏览器 1:App
     *
     * @param client 客户端方式 0:浏览器 1:App
     */
    public void setClient(String client) {
        this.client = client;
    }

    /**
     * 获取请求参数
     *
     * @return args - 请求参数
     */
    public String getArgs() {
        return args;
    }

    /**
     * 设置请求参数
     *
     * @param args 请求参数
     */
    public void setArgs(String args) {
        this.args = args;
    }

    /**
     * 获取请求URL
     *
     * @return url - 请求URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置请求URL
     *
     * @param url 请求URL
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取请求ip
     *
     * @return ip - 请求ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * 设置请求ip
     *
     * @param ip 请求ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * 获取响应状态码 0:成功 1:失败 2:异常
     *
     * @return status - 响应状态码 0:成功 1:失败 2:异常
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置响应状态码 0:成功 1:失败 2:异常
     *
     * @param status 响应状态码 0:成功 1:失败 2:异常
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取响应描述 ['密码错误','无权限','响应成功'...]
     *
     * @return remark - 响应描述 ['密码错误','无权限','响应成功'...]
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置响应描述 ['密码错误','无权限','响应成功'...]
     *
     * @param remark 响应描述 ['密码错误','无权限','响应成功'...]
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取异常信息
     *
     * @return error - 异常信息
     */
    public String getError() {
        return error;
    }

    /**
     * 设置异常信息
     *
     * @param error 异常信息
     */
    public void setError(String error) {
        this.error = error;
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
        sb.append(", orgId=").append(orgId);
        sb.append(", userId=").append(userId);
        sb.append(", userName=").append(userName);
        sb.append(", client=").append(client);
        sb.append(", args=").append(args);
        sb.append(", url=").append(url);
        sb.append(", ip=").append(ip);
        sb.append(", status=").append(status);
        sb.append(", remark=").append(remark);
        sb.append(", error=").append(error);
        sb.append(", createDate=").append(createDate);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", deleteDate=").append(deleteDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}