package com.yunxi.stamper.service.impl;

import com.yunxi.stamper.entity.PhoneIcon;
import com.yunxi.stamper.mapper.PhoneIconMapper;
import com.yunxi.stamper.service.PhoneIconService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class IPhoneIconService implements PhoneIconService {
    @Autowired
    private PhoneIconMapper mapper;
    @Override
    public List<PhoneIcon> getAll() {
        Example example=new Example(PhoneIcon.class);
        example.createCriteria().andIsNull("deleteDate");
        return mapper.selectByExample(example);
    }

    @Override
    public List<PhoneIcon> getPhoneIconByGroupId(Integer groupId) {
        if(groupId!=null){
            Example example=new Example(PhoneIcon.class);
            example.createCriteria().andIsNull("deleteDate").andEqualTo("groupId",groupId);
            return mapper.selectByExample(example);
        }
        return null;
    }

    @Override
    public PhoneIcon get(Integer id) {
        if(id!=null){
            Example example=new Example(PhoneIcon.class);
            example.createCriteria().andIsNull("deleteDate").andEqualTo("id",id);
            return mapper.selectOneByExample(example);
        }
        return null;
    }
}
