package com.yunxi.stamper.commons.device.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.yunxi.stamper.commons.other.AppConstant;

/**
 * wifi信息响应实体包
 */
public class WifiInfoResPkg {
    @JSONField(name = "Head")
	public MHHead head;

    @JSONField(name = "Body")
    public WifiInfoRes body;

    @JSONField(name = "Crc")
    public String crc;

    public WifiInfoResPkg(){
        head = new MHHead();
        head.setVersion(AppConstant.MH_VERSION);
        head.setMagic(AppConstant.MH_MAGIC);
        body = new WifiInfoRes();
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

    public WifiInfoRes getBody() {
        return body;
    }

    public void setBody(WifiInfoRes body) {
        this.body = body;
    }

    public String getCrc() {
        return crc;
    }

    public void setCrc(String crc) {
        this.crc = crc;
    }
}