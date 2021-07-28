package com.cloud.common.vo;

/**
 * 设备对象
 * 设备列表里面需要返回的字段
 */
public class ApplianceVo {
    private String applianceCode;
    private String sn8;
    private String onlineStatus;
    private String type;
    private String name;

    public String getApplianceCode() {
        return applianceCode;
    }

    public void setApplianceCode(String applianceCode) {
        this.applianceCode = applianceCode;
    }

    public String getSn8() {
        return sn8;
    }

    public void setSn8(String sn8) {
        this.sn8 = sn8;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
