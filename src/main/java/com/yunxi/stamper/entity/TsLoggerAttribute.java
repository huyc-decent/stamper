package com.yunxi.stamper.entity;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "ts_logger_attribute")
public class TsLoggerAttribute implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 关联日志ID
     */
    @Column(name = "logger_id")
    private Integer loggerId;

    /**
     * 旧值
     */
    @Column(name = "old_value")
    private String oldValue;

    /**
     * 新值
     */
    @Column(name = "new_value")
    private String newValue;

    /**
     * 属性名称
     */
    @Column(name = "attribute_name")
    private String attributeName;

    /**
     * 属性类型
     */
    @Column(name = "attribute_type")
    private String attributeType;

    /**
     * 属性字段名
     */
    @Column(name = "attribute_field")
    private String attributeField;

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
     * 获取关联日志ID
     *
     * @return logger_id - 关联日志ID
     */
    public Integer getLoggerId() {
        return loggerId;
    }

    /**
     * 设置关联日志ID
     *
     * @param loggerId 关联日志ID
     */
    public void setLoggerId(Integer loggerId) {
        this.loggerId = loggerId;
    }

    /**
     * 获取旧值
     *
     * @return old_value - 旧值
     */
    public String getOldValue() {
        return oldValue;
    }

    /**
     * 设置旧值
     *
     * @param oldValue 旧值
     */
    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    /**
     * 获取新值
     *
     * @return new_value - 新值
     */
    public String getNewValue() {
        return newValue;
    }

    /**
     * 设置新值
     *
     * @param newValue 新值
     */
    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    /**
     * 获取属性名称
     *
     * @return attribute_name - 属性名称
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * 设置属性名称
     *
     * @param attributeName 属性名称
     */
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    /**
     * 获取属性类型
     *
     * @return attribute_type - 属性类型
     */
    public String getAttributeType() {
        return attributeType;
    }

    /**
     * 设置属性类型
     *
     * @param attributeType 属性类型
     */
    public void setAttributeType(String attributeType) {
        this.attributeType = attributeType;
    }

    /**
     * 获取属性字段名
     *
     * @return attribute_field - 属性字段名
     */
    public String getAttributeField() {
        return attributeField;
    }

    /**
     * 设置属性字段名
     *
     * @param attributeField 属性字段名
     */
    public void setAttributeField(String attributeField) {
        this.attributeField = attributeField;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", loggerId=").append(loggerId);
        sb.append(", oldValue=").append(oldValue);
        sb.append(", newValue=").append(newValue);
        sb.append(", attributeName=").append(attributeName);
        sb.append(", attributeType=").append(attributeType);
        sb.append(", attributeField=").append(attributeField);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}