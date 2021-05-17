package com.yunxi.stamper.commons.device.model;

public class JsonDeviceDirResPkg {
    public DeviceDirRes data;
    public int error;
    public String msg;

    public static JsonDeviceDirResPkg ok(DeviceDirRes data){
		JsonDeviceDirResPkg pkg = new JsonDeviceDirResPkg();
		pkg.setData(data);
		pkg.setError(0);
		pkg.setMsg("");
		return pkg;
	}

	public static JsonDeviceDirResPkg fail(String msg){
		JsonDeviceDirResPkg pkg = new JsonDeviceDirResPkg();
		pkg.setData(null);
		pkg.setError(-1);
		pkg.setMsg(msg);
		return pkg;
	}

    public DeviceDirRes getData() {
        return data;
    }

    public void setData(DeviceDirRes data) {
        this.data = data;
    }

    public void setError(int error) {
        this.error = error;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getError() {
        return error;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "JsonDeviceDirResPkg{" +
                "data=" + data +
                ", error=" + error +
                ", msg='" + msg + '\'' +
                '}';
    }
}