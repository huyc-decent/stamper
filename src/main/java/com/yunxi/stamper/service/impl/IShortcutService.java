package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.Shortcut;
import com.yunxi.stamper.mapper.ShortcutMapper;
import com.yunxi.stamper.service.ShortcutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class IShortcutService implements ShortcutService {
	@Autowired
	private ShortcutMapper mapper;

	@Override
	@Transactional
	public void add(Shortcut shortcut) {
		int addCount = 0;

		if (shortcut != null) {
			shortcut.setId(null);
			shortcut.setCreateDate(new Date());
			addCount = mapper.insert(shortcut);
		}
		if (addCount != 1) {
			throw new PrintException("快捷方式更新失败");
		}
	}

	@Override
	public List<Shortcut> getAll(Integer userId) {
		Example example = new Example(Shortcut.class);
		example.createCriteria().andIsNull("deleteDate").andEqualTo("userId", userId);
		return mapper.selectByExample(example);
	}

	@Override
	public Shortcut get(Integer id) {
		if (id != null) {
			Example example = new Example(Shortcut.class);
			example.createCriteria().andIsNull("deleteDate").andEqualTo("id", id);
			return mapper.selectOneByExample(example);
		}
		return null;
	}

	@Override
	@Transactional
	public void update(Shortcut shortcut) {
		try {
			if (shortcut != null) {
				mapper.updateByPrimaryKey(shortcut);
			}
		} catch (Exception e) {
			throw new PrintException(e.getMessage());
		}
	}

	@Override
	public void delete(Shortcut shortcut) {
		try {
			if (shortcut != null) {
				mapper.delete(shortcut);
			}
		} catch (Exception e) {
			throw new PrintException(e.getMessage());
		}
	}

	@Override
	@Transactional
	public void deleteAllByUserId(Integer userId) {
		try {
			if (userId != null) {
				Example example = new Example(Shortcut.class);
				example.createCriteria().andEqualTo("userId", userId);
				mapper.deleteByExample(example);
			}
		} catch (Exception e) {
			throw new PrintException(e.getMessage());
		}
	}
}
