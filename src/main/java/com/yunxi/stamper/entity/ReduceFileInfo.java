package com.yunxi.stamper.entity;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "reduce_file_info")
public class ReduceFileInfo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    /**
     * 原图ID
     */
    @Id
    @Column(name = "file_info_id")
    private String fileInfoId;

    /**
     * 本地图片名
     */
    @Column(name = "file_name")
    private String fileName;

    /**
     * 图片大小
     */
    private Long size;

    /**
     * 本地存储相对路径
     */
    @Column(name = "relative_path")
    private String relativePath;

    /**
     * 本地存储绝对路径
     */
    @Column(name = "absolute_path")
    private String absolutePath;

    private String host;

    private static final long serialVersionUID = 1L;

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取原图ID
     *
     * @return file_info_id - 原图ID
     */
    public String getFileInfoId() {
        return fileInfoId;
    }

    /**
     * 设置原图ID
     *
     * @param fileInfoId 原图ID
     */
    public void setFileInfoId(String fileInfoId) {
        this.fileInfoId = fileInfoId;
    }

    /**
     * 获取本地图片名
     *
     * @return file_name - 本地图片名
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * 设置本地图片名
     *
     * @param fileName 本地图片名
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * 获取图片大小
     *
     * @return size - 图片大小
     */
    public Long getSize() {
        return size;
    }

    /**
     * 设置图片大小
     *
     * @param size 图片大小
     */
    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * 获取本地存储相对路径
     *
     * @return relative_path - 本地存储相对路径
     */
    public String getRelativePath() {
        return relativePath;
    }

    /**
     * 设置本地存储相对路径
     *
     * @param relativePath 本地存储相对路径
     */
    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    /**
     * 获取本地存储绝对路径
     *
     * @return absolute_path - 本地存储绝对路径
     */
    public String getAbsolutePath() {
        return absolutePath;
    }

    /**
     * 设置本地存储绝对路径
     *
     * @param absolutePath 本地存储绝对路径
     */
    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public String toString() {
        return "ReduceFileInfo{" + "id='" + id + '\'' +
                ", fileInfoId='" + fileInfoId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", size=" + size +
                ", relativePath='" + relativePath + '\'' +
                ", absolutePath='" + absolutePath + '\'' +
                ", host='" + host + '\'' +
                '}';
    }
}
