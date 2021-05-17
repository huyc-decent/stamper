package com.yunxi.stamper.commons.device;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/1/15 0015 13:59
 */
public class ResultVoD {
	public Object data;
	public int error;//返回的状态码 0表示成功
	public String msg;//Ret 不为0 时 返回的错误信息

	public static ResultVoD ok(int cmd, Object res) {
		ResultVoD vod = new ResultVoD();
		vod.setError(0);
		vod.setMsg("");
		vod.setData(res);
		return vod;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
