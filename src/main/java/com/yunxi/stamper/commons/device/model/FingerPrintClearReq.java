package com.yunxi.stamper.commons.device.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 指纹清空指令
 */
public class FingerPrintClearReq {
    @JSONField(name = "UserID")
    public int userID;//0:全清  非0:请指定id

    @JSONField(name = "DeviceID")
    public int deviceID; //设备id

    @JSONField(name = "FingerAddr")
    public int FingerAddr;//0:全清  非0:清指定地址

    @JSONField(name = "CodeID")
    public int CodeID; //下发指令用户id

	public int FingerId;//指纹id

    public String CodeName;//下发指定用户姓名

    public int FingerUserId;//删除的指纹所属人id

	public String FingerUserName;//删除的指纹所属人姓名

    //清空指定
    public static FingerPrintClearReq clearnOne(int deviceID, int userID, int fingerAddr){
        FingerPrintClearReq req = new FingerPrintClearReq();
        req.setDeviceID(deviceID);
        req.setUserID(userID);
        req.setFingerAddr(fingerAddr);
        req.setCodeID(0);
        return req;
    }

    //清空全部
    public static FingerPrintClearReq clearnAll(int deviceID){
        FingerPrintClearReq req = new FingerPrintClearReq();
        req.setDeviceID(deviceID);
        req.setUserID(0);
        req.setFingerAddr(0);
        req.setCodeID(0);
        return req;
    }

	public int getFingerId() {
		return FingerId;
	}

	public void setFingerId(int fingerId) {
		FingerId = fingerId;
	}

	public int getFingerAddr() {
        return FingerAddr;
    }

    public void setFingerAddr(int fingerAddr) {
        FingerAddr = fingerAddr;
    }

    public int getCodeID() {
        return CodeID;
    }

    public void setCodeID(int codeID) {
        CodeID = codeID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(int deviceID) {
        this.deviceID = deviceID;
    }

	public String getCodeName() {
		return CodeName;
	}

	public void setCodeName(String codeName) {
		CodeName = codeName;
	}

	public int getFingerUserId() {
		return FingerUserId;
	}

	public void setFingerUserId(int fingerUserId) {
		FingerUserId = fingerUserId;
	}

	public String getFingerUserName() {
		return FingerUserName;
	}

	public void setFingerUserName(String fingerUserName) {
		FingerUserName = fingerUserName;
	}

	@Override
    public String toString() {
        return "FingerPrintClearReq{" +
                "userID=" + userID +
                ", deviceID=" + deviceID +
                '}';
    }
}
