package com.yunxi.stamper.commons.device.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.yunxi.stamper.commons.other.AppConstant;

/**
 * wifi响应实体包
 */
public class WifiListResPkg {
    @JSONField(name = "Head")
	public MHHead head;

    @JSONField(name = "Body")
    public WifiListRes body;

    @JSONField(name = "Crc")
    public String crc;

    public  WifiListResPkg(){
        head = new MHHead();
        head.setVersion(AppConstant.MH_VERSION);
        head.setMagic(AppConstant.MH_MAGIC);
        body = new WifiListRes();
    }

    @Override
    public String toString() {
        return "WifiListResPkg{" +
                "head=" + head +
                ", body=" + body +
                ", crc='" + crc + '\'' +
                '}';
    }

    public MHHead getHead() {
        return head;
    }

    public void setHead(MHHead head) {
        this.head = head;
    }

    public WifiListRes getBody() {
        return body;
    }

    public void setBody(WifiListRes body) {
        this.body = body;
    }

    public String getCrc() {
        return crc;
    }

    public void setCrc(String crc) {
        this.crc = crc;
    }
}