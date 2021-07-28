package com.cloud.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 用户配置
 */
@Component
public class DemoConfig {

    /**
     * 这个是美的的通知的地址，一般来说都是不会有变化
     * 回调地址
     */
    @Value("${demo.mideaUri}")
    public String midea_iot;

    @Value("${demo.clientId}")
    public String clientId;

//    public String getMidea_iot() {
//        return midea_iot;
//    }
//
//    public void setMidea_iot(String midea_iot) {
//        this.midea_iot = midea_iot;
//    }
//
//    public String getClientId() {
//        return clientId;
//    }
//
//    public void setClientId(String clientId) {
//        this.clientId = clientId;
//    }
}
