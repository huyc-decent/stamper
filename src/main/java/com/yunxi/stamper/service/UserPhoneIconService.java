package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.UserPhoneIcon;

import java.util.List;

public interface UserPhoneIconService {
	List<UserPhoneIcon> getAll(Integer userId);

	void add(UserPhoneIcon userPhoneIcon);

	void delete(UserPhoneIcon userPhoneIcon);

	void deleteAllByUserId(Integer userId);
}
