package com.yunxi.stamper.entity;

import com.yunxi.stamper.logger.anno.LogTag;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@LogTag("印章配置")
public class Config implements Serializable {

    @LogTag("配置ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 设备uuid
     */
    @LogTag("设备UUID")
    private String uuid;

    /**
     * 0:默认配置(仅只有1个)  1:印章配置  2:高拍仪配置
     */
    private Integer type;
    /**
     * 后台版本标记 0:旧版本后台  1:新版本后台
     */
    @LogTag("版本类型(新/旧)")
    @Column(name = "`status`")
    private Integer status;
    /**
     * 量子配置id
     */
    @LogTag("量子配置pin")
    @Column(name = "qss_pin")
    private String qssPin;

    @LogTag("固件HASH")
    @Column(name = "firmware_hash")
    private String firmwareHash;

    public String getFirmwareHash() {
        return firmwareHash;
    }

    public void setFirmwareHash(String firmwareHash) {
        this.firmwareHash = firmwareHash;
    }

    @LogTag("量子配置qkud")
    @Column(name = "qss_qkud")
    private String qssQkud;

    @LogTag("量子配置qssc")
    @Column(name = "qss_qssc")
    private String qssQssc;

    /**
     * wifi名称
     */
    @LogTag("wifi名称")
    @Column(name = "wifi_ssid")
    private String wifiSsid;

    /**
     * WiFi密码
     */
    @LogTag("wifi密码")
    @Column(name = "wifi_pwd")
    private String wifiPwd;

    /**
     * 配置服务器ip 如:http://117.50.76.172:8080/
     */
    @LogTag("配置中心IP")
    @Column(name = "config_ip")
    private String configIp;

    /**
     * 业务服务器host 如:117.50.76.172:8080/device
     */
    @LogTag("业务系统IP")
    @Column(name = "svr_host")
    private String svrHost;

    /**
     * 业务服务器ip 如:117.50.76.172:8080
     */
    @LogTag("通讯系统IP")
    @Column(name = "svr_ip")
    private String svrIp;

    /**
     * 当前版本号
     */
    @LogTag("安卓版本")
    private String version;

    /**
     * APK文件名称
     */
    @LogTag("安卓APK名称")
    @Column(name = "apk_name")
    private String apkName;

    /**
     * 当前版本更新地址
     */
    @LogTag("安卓APK地址")
    @Column(name = "version_url")
    private String versionUrl;

    /**
     * 单片机版本
     */
    @LogTag("固件版本")
    @Column(name = "firmware_ver")
    private Double firmwareVer;

    /**
     * 单片机更新文件URL
     */
    @LogTag("固件地址")
    @Column(name = "firmware_url")
    private String firmwareUrl;

    @LogTag("固件描述")
    @Column(name = "firmware_remark")
    private String firmwareRemark;

    public String getFirmwareRemark() {
        return firmwareRemark;
    }

    public void setFirmwareRemark(String firmwareRemark) {
        this.firmwareRemark = firmwareRemark;
    }

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "update_date")
    private Date updateDate;

    @Column(name = "delete_date")
    private Date deleteDate;

    /**
     * 是否使用天翼云 0:不适用天翼云 1:使用天翼云
     */
    @LogTag("是否使用天翼云")
    @Column(name = "is_oos")
    private Integer isOos;

    /**
     * 是否启用申请单功能  1：开启(默认)   2：关闭
     */
    @LogTag("是否启用申请单功能")
    @Column(name = "is_enable_application")
    private Integer isEnableApplication;

    /**
     * 是否开机产测模式  1：开启(默认)  2：关闭
     */
    @LogTag("是否进入产测模式")
    @Column(name = "is_production_test")
    private Integer isProductionTest;

    /**
     * 设备序列号
     */
    @LogTag("设备序列号")
    @Column(name = "sn")
    private String sn;

    public Integer getIsEnableApplication() {
        return isEnableApplication;
    }

    public void setIsEnableApplication(Integer isEnableApplication) {
        this.isEnableApplication = isEnableApplication;
    }

    public Integer getIsProductionTest() {
        return isProductionTest;
    }

    public void setIsProductionTest(Integer isProductionTest) {
        this.isProductionTest = isProductionTest;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public Integer getIsOos() {
        return isOos;
    }

    public void setIsOos(Integer isOos) {
        this.isOos = isOos;
    }

    private static final long serialVersionUID = 1L;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取设备uuid
     *
     * @return uuid - 设备uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * 设置设备uuid
     *
     * @param uuid 设备uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取0:默认配置(仅只有1个)  1:印章配置  2:高拍仪配置
     *
     * @return type - 0:默认配置(仅只有1个)  1:印章配置  2:高拍仪配置
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置0:默认配置(仅只有1个)  1:印章配置  2:高拍仪配置
     *
     * @param type 0:默认配置(仅只有1个)  1:印章配置  2:高拍仪配置
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取量子配置id
     *
     * @return qss_pin - 量子配置id
     */
    public String getQssPin() {
        return qssPin;
    }

    /**
     * 设置量子配置id
     *
     * @param qssPin 量子配置id
     */
    public void setQssPin(String qssPin) {
        this.qssPin = qssPin;
    }

    /**
     * @return qss_qkud
     */
    public String getQssQkud() {
        return qssQkud;
    }

    /**
     * @param qssQkud
     */
    public void setQssQkud(String qssQkud) {
        this.qssQkud = qssQkud;
    }

    /**
     * @return qss_qssc
     */
    public String getQssQssc() {
        return qssQssc;
    }

    /**
     * @param qssQssc
     */
    public void setQssQssc(String qssQssc) {
        this.qssQssc = qssQssc;
    }

    /**
     * 获取wifi名称
     *
     * @return wifi_ssid - wifi名称
     */
    public String getWifiSsid() {
        return wifiSsid;
    }

    /**
     * 设置wifi名称
     *
     * @param wifiSsid wifi名称
     */
    public void setWifiSsid(String wifiSsid) {
        this.wifiSsid = wifiSsid;
    }

    /**
     * 获取WiFi密码
     *
     * @return wifi_pwd - WiFi密码
     */
    public String getWifiPwd() {
        return wifiPwd;
    }

    /**
     * 设置WiFi密码
     *
     * @param wifiPwd WiFi密码
     */
    public void setWifiPwd(String wifiPwd) {
        this.wifiPwd = wifiPwd;
    }

    /**
     * 获取配置服务器ip 如:http://117.50.76.172:8080/
     *
     * @return config_ip - 配置服务器ip 如:http://117.50.76.172:8080/
     */
    public String getConfigIp() {
        return configIp;
    }

    /**
     * 设置配置服务器ip 如:http://117.50.76.172:8080/
     *
     * @param configIp 配置服务器ip 如:http://117.50.76.172:8080/
     */
    public void setConfigIp(String configIp) {
        this.configIp = configIp;
    }

    /**
     * 获取业务服务器host 如:117.50.76.172:8080/device
     *
     * @return svr_host - 业务服务器host 如:117.50.76.172:8080/device
     */
    public String getSvrHost() {
        return svrHost;
    }

    /**
     * 设置业务服务器host 如:117.50.76.172:8080/device
     *
     * @param svrHost 业务服务器host 如:117.50.76.172:8080/device
     */
    public void setSvrHost(String svrHost) {
        this.svrHost = svrHost;
    }

    /**
     * 获取业务服务器ip 如:117.50.76.172:8080
     *
     * @return svr_ip - 业务服务器ip 如:117.50.76.172:8080
     */
    public String getSvrIp() {
        return svrIp;
    }

    /**
     * 设置业务服务器ip 如:117.50.76.172:8080
     *
     * @param svrIp 业务服务器ip 如:117.50.76.172:8080
     */
    public void setSvrIp(String svrIp) {
        this.svrIp = svrIp;
    }

    /**
     * 获取当前版本号
     *
     * @return version - 当前版本号
     */
    public String getVersion() {
        return version;
    }

    /**
     * 设置当前版本号
     *
     * @param version 当前版本号
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 获取APK文件名称
     *
     * @return apk_name - APK文件名称
     */
    public String getApkName() {
        return apkName;
    }

    /**
     * 设置APK文件名称
     *
     * @param apkName APK文件名称
     */
    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    /**
     * 获取当前版本更新地址
     *
     * @return version_url - 当前版本更新地址
     */
    public String getVersionUrl() {
        return versionUrl;
    }

    /**
     * 设置当前版本更新地址
     *
     * @param versionUrl 当前版本更新地址
     */
    public void setVersionUrl(String versionUrl) {
        this.versionUrl = versionUrl;
    }

    /**
     * @return create_date
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * @return update_date
     */
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * @param updateDate
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * @return delete_date
     */
    public Date getDeleteDate() {
        return deleteDate;
    }

    /**
     * @param deleteDate
     */
    public void setDeleteDate(Date deleteDate) {
        this.deleteDate = deleteDate;
    }

    @Override
    public String toString() {
        String sb = "Config{" + "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", qssPin='" + qssPin + '\'' +
                ", qssQkud='" + qssQkud + '\'' +
                ", qssQssc='" + qssQssc + '\'' +
                ", wifiSsid='" + wifiSsid + '\'' +
                ", wifiPwd='" + wifiPwd + '\'' +
                ", configIp='" + configIp + '\'' +
                ", svrHost='" + svrHost + '\'' +
                ", svrIp='" + svrIp + '\'' +
                ", version='" + version + '\'' +
                ", apkName='" + apkName + '\'' +
                ", versionUrl='" + versionUrl + '\'' +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                ", deleteDate=" + deleteDate +
                ", isOos=" + isOos +
                '}';
        return sb;
    }

    public Double getFirmwareVer() {
        return firmwareVer;
    }

    public void setFirmwareVer(Double firmwareVer) {
        this.firmwareVer = firmwareVer;
    }

    public String getFirmwareUrl() {
        return firmwareUrl;
    }

    public void setFirmwareUrl(String firmwareUrl) {
        this.firmwareUrl = firmwareUrl;
    }
}
