package com.yunxi.stamper.commons.other;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/12/25 0025 17:20
 */
public class KeyLabel {
	private String key;
	private List<Integer> label;

	public KeyLabel(String key, List<Integer> label) {
		this.key = key;
		this.label = label;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<Integer> getLabel() {
		return label;
	}

	public void setLabel(List<Integer> label) {
		this.label = label;
	}

	@Override
	public String toString() {
		String sb = "KeyLabel{" + "key='" + key + '\'' +
				", label=" + label +
				'}';
		return sb;
	}
}
