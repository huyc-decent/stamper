package com.yunxi.stamper.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(name = "phone_icon")
public class PhoneIcon implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 图标(示例:icon-shenqingyongzhang)
     */
    private String code;

    /**
     * 图标名称
     */
    private String name;

    /**
     * 是否被选择，0为未选择，1为已选择
     */
    private Integer type;

    /**
     * 图标颜色(示例:background:#4CD7CD)
     */
    private String color;

    /**
     * 链接地址
     */
    private String url;

    /**
     * 组名
     */
    @Column(name = "group_name")
    private String groupName;

    /**
     * 属于哪个分组
     */
    @Column(name = "group_id")
    private Integer groupId;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "update_date")
    private Date updateDate;

    @Column(name = "delete_date")
    private Date deleteDate;

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
			return true;
		}
        if (!(o instanceof PhoneIcon)) {
			return false;
		}
        if (!super.equals(o)) {
			return false;
		}

        PhoneIcon phoneIcon = (PhoneIcon) o;

        return id.equals(phoneIcon.id);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }
}
