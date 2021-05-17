package com.yunxi.stamper.entity;

import com.yunxi.stamper.logger.anno.LogTag;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@LogTag("组织")
public class Department implements Serializable {

	@LogTag("组织id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * 所属公司id
	 */
	@LogTag("集团id")
	@Column(name = "org_id")
	private Integer orgId;

	/**
	 * 管理员id
	 */
	@LogTag("管理员id")
	@Column(name = "manager_user_id")
	private Integer managerUserId;

	/**
	 * 上级部门id
	 */
	@LogTag("父组织id")
	@Column(name = "parent_id")
	private Integer parentId;

	/**
	 * 部门名称
	 */
	@LogTag("组织名称")
	private String name;

	/**
	 * 备注
	 */
	@LogTag("组织描述")
	private String remark;

	/**
	 * 组织编码
	 */
	@LogTag("组织编码")
	private String code;

	/**
	 * 组织LOGO
	 */
	@LogTag("组织logo")
	private String logo;

	/**
	 * 领导岗职称
	 */
	@LogTag("领导岗职称id")
	@Column(name = "position_id")
	private Integer positionId;

	/**
	 * 组织层级 值越小,层级越高
	 */
	@LogTag("组织层级")
	private Integer level;

	@Column(name = "create_date")
	private Date createDate;

	@Column(name = "update_date")
	private Date updateDate;

	@Column(name = "delete_date")
	private Date deleteDate;

	@LogTag("父组织编码链表")
	@Column(name = "parent_code")
	private String parentCode;

	@LogTag("组织地址")
	private String location;

	/**
	 * 组织类型 0:部门 1:公司 2:集团
	 */
	@LogTag("组织类型")
	@Column(name = "`type`")
	private Integer type;

	private static final long serialVersionUID = 1L;

	/**
	 * @return id
	 */
	public Integer getId() {
		return id;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @param id
	 */
	public void setId(Integer id) {
		this.id = id;
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
	 * 获取管理员id
	 *
	 * @return manager_user_id - 管理员id
	 */
	public Integer getManagerUserId() {
		return managerUserId;
	}

	/**
	 * 设置管理员id
	 *
	 * @param managerUserId 管理员id
	 */
	public void setManagerUserId(Integer managerUserId) {
		this.managerUserId = managerUserId;
	}

	/**
	 * 获取上级部门id
	 *
	 * @return parent_id - 上级部门id
	 */
	public Integer getParentId() {
		return parentId;
	}

	/**
	 * 设置上级部门id
	 *
	 * @param parentId 上级部门id
	 */
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	/**
	 * 获取部门名称
	 *
	 * @return name - 部门名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置部门名称
	 *
	 * @param name 部门名称
	 */
	public void setName(String name) {
		this.name = name;
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
	 * 获取组织LOGO
	 *
	 * @return logo - 组织LOGO
	 */
	public String getLogo() {
		return logo;
	}

	/**
	 * 设置组织LOGO
	 *
	 * @param logo 组织LOGO
	 */
	public void setLogo(String logo) {
		this.logo = logo;
	}

	/**
	 * 获取领导岗职称
	 *
	 * @return position_id - 领导岗职称
	 */
	public Integer getPositionId() {
		return positionId;
	}

	/**
	 * 设置领导岗职称
	 *
	 * @param positionId 领导岗职称
	 */
	public void setPositionId(Integer positionId) {
		this.positionId = positionId;
	}

	/**
	 * 获取组织层级 值越小,层级越高
	 *
	 * @return level - 组织层级 值越小,层级越高
	 */
	public Integer getLevel() {
		return level;
	}

	/**
	 * 设置组织层级 值越小,层级越高
	 *
	 * @param level 组织层级 值越小,层级越高
	 */
	public void setLevel(Integer level) {
		this.level = level;
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

	/**
	 * @return parent_code
	 */
	public String getParentCode() {
		return parentCode;
	}

	/**
	 * @param parentCode
	 */
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	/**
	 * 获取组织类型 0:部门 1:公司 2:集团
	 *
	 * @return type - 组织类型 0:部门 1:公司 2:集团
	 */
	public Integer getType() {
		return type;
	}

	/**
	 * 设置组织类型 0:部门 1:公司 2:集团
	 *
	 * @param type 组织类型 0:部门 1:公司 2:集团
	 */
	public void setType(Integer type) {
		this.type = type;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" [");
		sb.append("Hash = ").append(hashCode());
		sb.append(", id=").append(id);
		sb.append(", orgId=").append(orgId);
		sb.append(", managerUserId=").append(managerUserId);
		sb.append(", parentId=").append(parentId);
		sb.append(", name=").append(name);
		sb.append(", remark=").append(remark);
		sb.append(", code=").append(code);
		sb.append(", logo=").append(logo);
		sb.append(", positionId=").append(positionId);
		sb.append(", level=").append(level);
		sb.append(", createDate=").append(createDate);
		sb.append(", updateDate=").append(updateDate);
		sb.append(", deleteDate=").append(deleteDate);
		sb.append(", parentCode=").append(parentCode);
		sb.append(", type=").append(type);
		sb.append(", serialVersionUID=").append(serialVersionUID);
		sb.append("]");
		return sb.toString();
	}
}