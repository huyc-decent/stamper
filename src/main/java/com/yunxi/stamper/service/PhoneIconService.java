package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.PhoneIcon;

import java.util.List;

public interface PhoneIconService {
	List<PhoneIcon> getAll();

	List<PhoneIcon> getPhoneIconByGroupId(Integer groupId);

	PhoneIcon get(Integer id);
}
