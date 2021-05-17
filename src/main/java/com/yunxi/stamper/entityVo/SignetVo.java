package com.yunxi.stamper.entityVo;

import com.yunxi.stamper.entity.Signet;

/**
 * @author zhf_10@163.com
 * @Description 印章分页列表展示实体, 普通信息展示
 * @date 2019/5/16 0016 19:23
 */
public class SignetVo extends Signet {
    private String location;//印章地址
    private boolean isOnLine = false;//true:在线 false:不在线
    private String typeName;//类型描述
    private boolean isMine = true;//true:是本公司的章可以操作  false:非本公司的章,不能操作
    private Integer thresholdValue;//阈值

    public Integer getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(Integer thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public boolean isOnLine() {
        return isOnLine;
    }

    public void setOnLine(boolean onLine) {
        isOnLine = onLine;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
