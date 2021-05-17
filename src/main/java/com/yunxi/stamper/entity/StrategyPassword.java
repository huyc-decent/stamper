package com.yunxi.stamper.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "strategy_password")
public class StrategyPassword implements Serializable {
    /**
     * 编号
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String remark;

    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 最小长度
     */
    @Column(name = "len_min")
    private Integer lenMin;

    /**
     * 最大长度
     */
    @Column(name = "len_max")
    private Integer lenMax;

    /**
     * 有效期
     */
    @Column(name = "time_max")
    private Date timeMax;

    /**
     * 首字母标记 0:数字 1:大写英文 2:小写英文 3:特殊字符
     */
    @Column(name = "first_char")
    private Integer firstChar;

    /**
     * 大写标记 1启用大写  0禁用大写
     */
    @Column(name = "upper_status")
    private Integer upperStatus;

    /**
     * 小写标记 1启用小写 0:禁用大写
     */
    @Column(name = "lower_status")
    private Integer lowerStatus;

    /**
     * 数字标记 1启用数字 0禁用数字
     */
    @Column(name = "num_status")
    private Integer numStatus;

    /**
     * 非字母字符标记 1启用字符  0禁用字符
     */
    @Column(name = "char_status")
    private Integer charStatus;

    /**
     * 创建人编号 当前用户ID
     */
    @Column(name = "creatorId")
    private Integer creatorid;

    /**
     * 创建人姓名
     */
    @Column(name = "creatorName")
    private String creatorname;

    /**
     * 创建日期
     */
    @Column(name = "createDate")
    private Date createdate;

    @Column(name = "updateDate")
    private Date updatedate;

    @Column(name = "deleteDate")
    private Date deletedate;

    private static final long serialVersionUID = 1L;

    /**
     * 获取编号
     *
     * @return id - 编号
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置编号
     *
     * @param id 编号
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取名称
     *
     * @return name - 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     *
     * @param name 名称
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
     * @return org_id
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * @param orgId
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取最小长度
     *
     * @return len_min - 最小长度
     */
    public Integer getLenMin() {
        return lenMin;
    }

    /**
     * 设置最小长度
     *
     * @param lenMin 最小长度
     */
    public void setLenMin(Integer lenMin) {
        this.lenMin = lenMin;
    }

    /**
     * 获取最大长度
     *
     * @return len_max - 最大长度
     */
    public Integer getLenMax() {
        return lenMax;
    }

    /**
     * 设置最大长度
     *
     * @param lenMax 最大长度
     */
    public void setLenMax(Integer lenMax) {
        this.lenMax = lenMax;
    }

    /**
     * 获取有效期
     *
     * @return time_max - 有效期
     */
    public Date getTimeMax() {
        return timeMax;
    }

    /**
     * 设置有效期
     *
     * @param timeMax 有效期
     */
    public void setTimeMax(Date timeMax) {
        this.timeMax = timeMax;
    }

    /**
     * 获取首字母标记 0:数字 1:大写英文 2:小写英文 3:特殊字符
     *
     * @return first_char - 首字母标记 0:数字 1:大写英文 2:小写英文 3:特殊字符
     */
    public Integer getFirstChar() {
        return firstChar;
    }

    /**
     * 设置首字母标记 0:数字 1:大写英文 2:小写英文 3:特殊字符
     *
     * @param firstChar 首字母标记 0:数字 1:大写英文 2:小写英文 3:特殊字符
     */
    public void setFirstChar(Integer firstChar) {
        this.firstChar = firstChar;
    }

    /**
     * 获取大写标记 1启用大写  0禁用大写
     *
     * @return upper_status - 大写标记 1启用大写  0禁用大写
     */
    public Integer getUpperStatus() {
        return upperStatus;
    }

    /**
     * 设置大写标记 1启用大写  0禁用大写
     *
     * @param upperStatus 大写标记 1启用大写  0禁用大写
     */
    public void setUpperStatus(Integer upperStatus) {
        this.upperStatus = upperStatus;
    }

    /**
     * 获取小写标记 1启用小写 0:禁用大写
     *
     * @return lower_status - 小写标记 1启用小写 0:禁用大写
     */
    public Integer getLowerStatus() {
        return lowerStatus;
    }

    /**
     * 设置小写标记 1启用小写 0:禁用大写
     *
     * @param lowerStatus 小写标记 1启用小写 0:禁用大写
     */
    public void setLowerStatus(Integer lowerStatus) {
        this.lowerStatus = lowerStatus;
    }

    /**
     * 获取数字标记 1启用数字 0禁用数字
     *
     * @return num_status - 数字标记 1启用数字 0禁用数字
     */
    public Integer getNumStatus() {
        return numStatus;
    }

    /**
     * 设置数字标记 1启用数字 0禁用数字
     *
     * @param numStatus 数字标记 1启用数字 0禁用数字
     */
    public void setNumStatus(Integer numStatus) {
        this.numStatus = numStatus;
    }

    /**
     * 获取非字母字符标记 1启用字符  0禁用字符
     *
     * @return char_status - 非字母字符标记 1启用字符  0禁用字符
     */
    public Integer getCharStatus() {
        return charStatus;
    }

    /**
     * 设置非字母字符标记 1启用字符  0禁用字符
     *
     * @param charStatus 非字母字符标记 1启用字符  0禁用字符
     */
    public void setCharStatus(Integer charStatus) {
        this.charStatus = charStatus;
    }

    /**
     * 获取创建人编号 当前用户ID
     *
     * @return creatorId - 创建人编号 当前用户ID
     */
    public Integer getCreatorid() {
        return creatorid;
    }

    /**
     * 设置创建人编号 当前用户ID
     *
     * @param creatorid 创建人编号 当前用户ID
     */
    public void setCreatorid(Integer creatorid) {
        this.creatorid = creatorid;
    }

    /**
     * 获取创建人姓名
     *
     * @return creatorName - 创建人姓名
     */
    public String getCreatorname() {
        return creatorname;
    }

    /**
     * 设置创建人姓名
     *
     * @param creatorname 创建人姓名
     */
    public void setCreatorname(String creatorname) {
        this.creatorname = creatorname;
    }

    /**
     * 获取创建日期
     *
     * @return createDate - 创建日期
     */
    public Date getCreatedate() {
        return createdate;
    }

    /**
     * 设置创建日期
     *
     * @param createdate 创建日期
     */
    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    /**
     * @return updateDate
     */
    public Date getUpdatedate() {
        return updatedate;
    }

    /**
     * @param updatedate
     */
    public void setUpdatedate(Date updatedate) {
        this.updatedate = updatedate;
    }

    /**
     * @return deleteDate
     */
    public Date getDeletedate() {
        return deletedate;
    }

    /**
     * @param deletedate
     */
    public void setDeletedate(Date deletedate) {
        this.deletedate = deletedate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", remark=").append(remark);
        sb.append(", orgId=").append(orgId);
        sb.append(", lenMin=").append(lenMin);
        sb.append(", lenMax=").append(lenMax);
        sb.append(", timeMax=").append(timeMax);
        sb.append(", firstChar=").append(firstChar);
        sb.append(", upperStatus=").append(upperStatus);
        sb.append(", lowerStatus=").append(lowerStatus);
        sb.append(", numStatus=").append(numStatus);
        sb.append(", charStatus=").append(charStatus);
        sb.append(", creatorid=").append(creatorid);
        sb.append(", creatorname=").append(creatorname);
        sb.append(", createdate=").append(createdate);
        sb.append(", updatedate=").append(updatedate);
        sb.append(", deletedate=").append(deletedate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}