package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.Shortcut;

import java.util.List;

public interface ShortcutService {
	void add(Shortcut shortcut);

	List<Shortcut> getAll(Integer userId);

	Shortcut get(Integer id);

	void update(Shortcut shortcut);

	void delete(Shortcut shortcut);

	void deleteAllByUserId(Integer userId);
}
