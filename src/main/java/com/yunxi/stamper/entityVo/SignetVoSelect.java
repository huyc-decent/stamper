package com.yunxi.stamper.entityVo;

import com.yunxi.stamper.entity.Signet;

import java.util.Date;

/**
 * @author zhf_10@163.com
 * @Description 印章详细信息展示实体
 * @date 2019/5/16 0016 19:57
 */
public class SignetVoSelect extends Signet {
	private int isOnline;//1:在线 0:不在线
	private String typeName;//类型描述
	private String orgName;//公司名称
	private String location;//地址
	private String longitude;//经度
	private String latitude;//纬度
	private String keeperName;//授权人名称
	private String auditorName;//审计人名称
	private String meterName;//绑定的高拍仪名称
	private String departMentName;//部门名称
	private FileEntity fileEntity;
	private Date migrateDate;	//迁移时间

	public FileEntity getFileEntity() {
		return fileEntity;
	}

	public void setFileEntity(FileEntity fileEntity) {
		this.fileEntity = fileEntity;
	}

	public Date getMigrateDate() {
		return migrateDate;
	}

	public void setMigrateDate(Date migrateDate) {
		this.migrateDate = migrateDate;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public int getIsOnline() {
		return isOnline;
	}

	public void setIsOnline(int isOnline) {
		this.isOnline = isOnline;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	@Override
	public String getOrgName() {
		return orgName;
	}

	@Override
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	@Override
	public String getKeeperName() {
		return keeperName;
	}

	@Override
	public void setKeeperName(String keeperName) {
		this.keeperName = keeperName;
	}

	@Override
	public String getAuditorName() {
		return auditorName;
	}

	@Override
	public void setAuditorName(String auditorName) {
		this.auditorName = auditorName;
	}

	public String getMeterName() {
		return meterName;
	}

	public void setMeterName(String meterName) {
		this.meterName = meterName;
	}

	public String getDepartMentName() {
		return departMentName;
	}

	public void setDepartMentName(String departMentName) {
		this.departMentName = departMentName;
	}
}
