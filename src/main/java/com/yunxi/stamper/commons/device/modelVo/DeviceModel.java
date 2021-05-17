package com.yunxi.stamper.commons.device.modelVo;

/**
 * @author zhf_10@163.com
 * @Description 模式切换实体类
 * @date 2019/8/6 0006 17:33
 */
public class DeviceModel {
	private Integer useModel;//0：关闭指纹模式 1：打开指纹模式
	private Integer res;//设备端响应专用参数 0:操作成功 1:操作失败

	public Integer getRes() {
		return res;
	}

	public void setRes(Integer res) {
		this.res = res;
	}

	public Integer getUseModel() {
		return useModel;
	}

	public void setUseModel(Integer useModel) {
		this.useModel = useModel;
	}
}
