package com.yunxi.stamper.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "ts_logger")
public class TsLogger implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 对象类型
     */
    @Column(name = "object_type")
    private String objectType;

    /**
     * 对象名称
     */
    @Column(name = "object_name")
    private String objectName;

    /**
     * 对象ID
     */
    @Column(name = "object_id")
    private Integer objectId;

    /**
     * 操作者ID
     */
    @Column(name = "operator_id")
    private Integer operatorId;

    /**
     * 操作者名称
     */
    @Column(name = "operator_name")
    private String operatorName;

    /**
     * 描述
     */
    private String content;

    /**
     * 操作时间
     */
    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "update_date")
    private Date updateDate;

    @Column(name = "delete_date")
    private Date deleteDate;

    @Column(name = "batch_id")
    private String batchId;

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
     * 获取对象类型
     *
     * @return object_type - 对象类型
     */
    public String getObjectType() {
        return objectType;
    }

    /**
     * 设置对象类型
     *
     * @param objectType 对象类型
     */
    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    /**
     * 获取对象名称
     *
     * @return object_name - 对象名称
     */
    public String getObjectName() {
        return objectName;
    }

    /**
     * 设置对象名称
     *
     * @param objectName 对象名称
     */
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    /**
     * 获取对象ID
     *
     * @return object_id - 对象ID
     */
    public Integer getObjectId() {
        return objectId;
    }

    /**
     * 设置对象ID
     *
     * @param objectId 对象ID
     */
    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }

    /**
     * 获取操作者ID
     *
     * @return operator_id - 操作者ID
     */
    public Integer getOperatorId() {
        return operatorId;
    }

    /**
     * 设置操作者ID
     *
     * @param operatorId 操作者ID
     */
    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    /**
     * 获取操作者名称
     *
     * @return operator_name - 操作者名称
     */
    public String getOperatorName() {
        return operatorName;
    }

    /**
     * 设置操作者名称
     *
     * @param operatorName 操作者名称
     */
    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    /**
     * 获取描述
     *
     * @return content - 描述
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置描述
     *
     * @param content 描述
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取操作时间
     *
     * @return create_date - 操作时间
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * 设置操作时间
     *
     * @param createDate 操作时间
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

    /**
     * @return batchId
     */
    public String getBatchId() {
        return batchId;
    }

    /**
     * @param batchId  批量操作id
     */
    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", objectType=").append(objectType);
        sb.append(", objectName=").append(objectName);
        sb.append(", objectId=").append(objectId);
        sb.append(", operatorId=").append(operatorId);
        sb.append(", operatorName=").append(operatorName);
        sb.append(", content=").append(content);
        sb.append(", createDate=").append(createDate);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", deleteDate=").append(deleteDate);
        sb.append(", batchId=").append(batchId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}