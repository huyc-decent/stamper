package com.yunxi.stamper.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "config_error")
public class ConfigError implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 设备uuid
     */
    private String uuid;

    /**
     * 日志状态消息 如:上传成功,上传失败,无文件上传..
     */
    private String error;

    /**
     * 日志文件名称
     */
    @Column(name = "file_name")
    private String fileName;

    /**
     * 相对路径
     */
    @Column(name = "relative_path")
    private String relativePath;

    /**
     * 本地绝对路径
     */
    @Column(name = "absolute_path")
    private String absolutePath;

    private String host;

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

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    /**
     * 获取设备uuid
     *
     * @return uuid - 设备uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * 设置设备uuid
     *
     * @param uuid 设备uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * 获取日志状态消息 如:上传成功,上传失败,无文件上传..
     *
     * @return error - 日志状态消息 如:上传成功,上传失败,无文件上传..
     */
    public String getError() {
        return error;
    }

    /**
     * 设置日志状态消息 如:上传成功,上传失败,无文件上传..
     *
     * @param error 日志状态消息 如:上传成功,上传失败,无文件上传..
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * 获取日志文件名称
     *
     * @return file_name - 日志文件名称
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * 设置日志文件名称
     *
     * @param fileName 日志文件名称
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * 获取相对路径
     *
     * @return relative_path - 相对路径
     */
    public String getRelativePath() {
        return relativePath;
    }

    /**
     * 设置相对路径
     *
     * @param relativePath 相对路径
     */
    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    /**
     * 获取本地绝对路径
     *
     * @return absolute_path - 本地绝对路径
     */
    public String getAbsolutePath() {
        return absolutePath;
    }

    /**
     * 设置本地绝对路径
     *
     * @param absolutePath 本地绝对路径
     */
    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
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
        sb.append(", uuid=").append(uuid);
        sb.append(", error=").append(error);
        sb.append(", fileName=").append(fileName);
        sb.append(", relativePath=").append(relativePath);
        sb.append(", absolutePath=").append(absolutePath);
        sb.append(", createDate=").append(createDate);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", deleteDate=").append(deleteDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}