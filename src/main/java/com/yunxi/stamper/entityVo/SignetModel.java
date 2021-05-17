package com.yunxi.stamper.entityVo;

/**
 * @author zhf_10@163.com
 * @Description 设备使用模式实例
 * @date 2019/8/6 0006 15:58
 */
public class SignetModel {
	private Integer useModel;//0：关闭指纹模式 1：打开指纹模式  2:打开章头指令

	public SignetModel(Integer useModel) {
		this.useModel = useModel;
	}

	public Integer getUseModel() {
		return useModel;
	}

	public void setUseModel(Integer useModel) {
		this.useModel = useModel;
	}
}
