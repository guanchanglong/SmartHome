package com.cloud.common;

/**
 * 根据文档，列出协商好的namespace事件
 */
public enum NamespaceEnum {
    UserAcceptGrant("UserAcceptGrant")//授权
    ,UserCancelGrant("UserCancelGrant")//取消授权
    , ApplianceDiscovery("ApplianceDiscovery")//获取用户设备列表
    ,ApplianceSubscribe("ApplianceSubscribe")//设备订阅
    ,ApplianceUnsubscribe("ApplianceUnsubscribe")//取消设备订阅
    ,ApplianceControl("ApplianceControl")//设备控制
    ,ApplianceState("ApplianceState")//设备状态查询
    ,ApplianceStateChange("ApplianceStateChange")//设备状态推送
    ,ApplianceAdd("ApplianceAdd")//用户增加设备
    ,ApplianceDelete("ApplianceDelete")//用户减少设备
    ;
    private final String namespace;

    NamespaceEnum(String namespace){
        this.namespace = namespace;
    }

    public String getNamespace() {
        return namespace;
    }

}
