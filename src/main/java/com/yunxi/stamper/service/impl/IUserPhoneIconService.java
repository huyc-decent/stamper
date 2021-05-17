package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.UserPhoneIcon;
import com.yunxi.stamper.mapper.UserPhoneIconMapper;
import com.yunxi.stamper.service.UserPhoneIconService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class IUserPhoneIconService implements UserPhoneIconService {
    @Autowired
    private UserPhoneIconMapper mapper;
    @Override
    public List<UserPhoneIcon> getAll(Integer userId) {
        Example example=new Example(UserPhoneIcon.class);
        example.createCriteria().andEqualTo("userId",userId);
        List<UserPhoneIcon> userPhoneIcons = mapper.selectByExample(example);
        return userPhoneIcons;
    }

    @Override
    @Transactional
    public void add(UserPhoneIcon userPhoneIcon) {
        try {
            if(userPhoneIcon!=null){
                mapper.insert(userPhoneIcon);
            }
        }catch (Exception e){
            throw new PrintException(e.getMessage());
        }
    }

    @Override
    public void delete(UserPhoneIcon userPhoneIcon) {
        try {
            if(userPhoneIcon!=null){
                mapper.delete(userPhoneIcon);
            }
        }catch (Exception e){
            throw new PrintException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteAllByUserId(Integer userId) {
        try {
            if(userId!=null){
                Example example=new Example(UserPhoneIcon.class);
                example.createCriteria().andEqualTo("userId",userId);
                mapper.deleteByExample(example);
            }
        }catch (Exception e){
            throw new PrintException(e.getMessage());
        }
    }
}
