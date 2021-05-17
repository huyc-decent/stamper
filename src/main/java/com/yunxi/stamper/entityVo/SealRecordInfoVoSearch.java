package com.yunxi.stamper.entityVo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/20 0020 20:45
 */
@Data
public class SealRecordInfoVoSearch {
	/**
	 * APP端搜索指定使用记录信息
	 */
	private Integer infoId;//要查询的使用记录id,在App端,查询上一个或下一个时有值

	/**
	 * 搜索人的信息
	 */
	private Integer orgId;//要查询的公司id
	private List<Integer> departmentIds;//要查询的部门
	private Integer searchUserId;//查询用户的id
	private String searchUserName;//查询用户名称
	private Integer searchClient;//查询客户端  0:web端 1:移动端

	/**
	 * WEB端搜索条件
	 */
	private List<Integer> applicationId;//申请单id列表(WEB端)
	private List<Integer> signetId;//印章id列表(WEB端)
	private List<Integer> userId;//用印人id列表(WEB端)

	/**
	 * 搜索条件
	 */
	private String title;//申请单标题(APP端)
	private String signetName;//印章名称(APP端)
	private Integer deviceType;//印章类型(APP端)
	private String userName;//用印人名称(APP端)
	private Integer error;//正常/异常/警告(WEB端)
	private Integer type;//申请单默认/指纹模式(WEB端)

	private Date[] date;
	private Date start;//开始时间
	private Date end;//结束时间

	private Integer pageNum = 1;
	private Integer pageSize = 10;
	private boolean isPage = false;

	public Integer getSearchClient() {
		return searchClient;
	}

	public void setSearchClient(Integer searchClient) {
		this.searchClient = searchClient;
	}

	public Integer getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(Integer deviceType) {
		this.deviceType = deviceType;
	}

	public boolean isPage() {
		return isPage;
	}

	public void setPage(boolean page) {
		isPage = page;
	}

	public String getSearchUserName() {
		return searchUserName;
	}

	public void setSearchUserName(String searchUserName) {
		this.searchUserName = searchUserName;
	}

	public Integer getSearchUserId() {
		return searchUserId;
	}

	public void setSearchUserId(Integer searchUserId) {
		this.searchUserId = searchUserId;
	}

	public List<Integer> getDepartmentIds() {
		return departmentIds;
	}

	public void setDepartmentIds(List<Integer> departmentIds) {
		this.departmentIds = departmentIds;
	}

	public Integer getOrgId() {
		return orgId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	public Integer getInfoId() {
		return infoId;
	}

	public void setInfoId(Integer infoId) {
		this.infoId = infoId;
	}

	public List<Integer> getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(List<Integer> applicationId) {
		this.applicationId = applicationId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Integer> getSignetId() {
		return signetId;
	}

	public void setSignetId(List<Integer> signetId) {
		this.signetId = signetId;
	}

	public String getSignetName() {
		return signetName;
	}

	public void setSignetName(String signetName) {
		this.signetName = signetName;
	}

	public List<Integer> getUserId() {
		return userId;
	}

	public void setUserId(List<Integer> userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getError() {
		return error;
	}

	public void setError(Integer error) {
		this.error = error;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Date[] getDate() {
		return date;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public void setDate(Date[] date) {
		this.date = date;
		if (date != null) {
			if (date.length == 1) {
				this.start = date[0];
			}
			if (date.length == 2) {
				this.start = date[0];
				this.end = date[1];
			}
		}
	}
}
