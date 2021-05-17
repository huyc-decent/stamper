package com.yunxi.stamper.commons.device.msg;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/1/17 0017 10:33
 */
public class Body {
	private int ApplicationID;
	private String ApplicationToken;

	public int getApplicationID() {
		return ApplicationID;
	}

	public void setApplicationID(int applicationID) {
		ApplicationID = applicationID;
	}

	public String getApplicationToken() {
		return ApplicationToken;
	}

	public void setApplicationToken(String applicationToken) {
		ApplicationToken = applicationToken;
	}

	@Override
	public String toString() {
		return "Body{" +
				"ApplicationID=" + ApplicationID +
				", ApplicationToken='" + ApplicationToken + '\'' +
				'}';
	}
}
