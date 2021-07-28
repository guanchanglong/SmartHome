package com.cloud.demo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cloud.common.NamespaceEnum;
import com.cloud.common.vo.CommonRequest;
import com.cloud.common.vo.SubscriptionHeader;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 用户订阅计划
 */
@Component("demoSubscriptionSchedule")
@EnableScheduling
public class DemoSubscriptionSchedule {

    private static HashMap<String, JSONArray> subscrption = new HashMap<>();

    @Autowired
    private DemoUserService demoUserService;

    @Autowired
    private DemoConfig demoConfig;

    /**
     * 模拟器的逻辑为每60s触发一次
     * OpenUID为 {@link com.cloud.controller.CloudOperationController#operation(String, String)} 生成
     * Namespace 为 {@link NamespaceEnum}中的ApplianceStateChange
     */
    @Scheduled(fixedRate = 60 * 1000L)
    public void notiyMideaCloud() {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)//设置链接超时
                .writeTimeout(10, TimeUnit.SECONDS) // 设置写数据超时
                .readTimeout(30, TimeUnit.SECONDS) // 设置读数据超时
                .build();
        MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
        //根据文档匹配相应的接口格式
        subscrption.forEach((v, k) -> {
            k.forEach(applianceCode -> {
                try {
                    DemoUser user = demoUserService.getUser(v);
                    CommonRequest request = new CommonRequest();
                    SubscriptionHeader header = new SubscriptionHeader();
                    header.setReqId(UUID.randomUUID().toString());
                    header.setClientId(demoConfig.clientId);
                    header.setNamespace(NamespaceEnum.ApplianceStateChange.name());
                    header.setTimestamp(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()));
                    header.setOpenUid(user.getOpenUid());
                    request.setHeader(header);
                    request.getPayload().put("applianceCode", k);
                    request.getPayload().put("onlineStatus", random01());
                    JSONObject status = new JSONObject();
                    status.put("power", 1 > Integer.parseInt(random01()) ? "on" : "off");
                    status.put("light", 1 > Integer.parseInt(random01()) ? "on" : "off");
                    status.put("dry", 1 > Integer.parseInt(random01()) ? "on" : "off");
                    request.getPayload().put("status", status);
                    //创建访问Midea_iotURI底下的请求
                    Request request2 = new Request.Builder().url(demoConfig.midea_iot)
                            .post(RequestBody.create(MEDIA_TYPE, JSONObject.toJSONString(request))).build();
                    Response response = okHttpClient.newCall(request2).execute();
                    if (response.isSuccessful()) {
                        System.out.println("返回成功了：" + response.body().toString());
                    } else {
                        //TODO 记录点日志，发送个告警之类
                        System.out.println("返回失败了：" + response.code());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }

    public void addSubscription(String username, JSONArray applianceCode) {
        JSONArray array = null;
        if (subscrption.containsKey(username)) {
            array = subscrption.get(username);
            applianceCode.addAll(array);
        }
        HashSet set = new HashSet(array);
        array.clear();
        array.addAll(set);
        subscrption.put(username, array);
    }

    public void delSubscription(String username, JSONArray applianceCode) {
        JSONArray array = null;
        if (subscrption.containsKey(username)) {
            array = subscrption.get(username);
            array.removeAll(applianceCode);
        }
        subscrption.put(username, array);
    }

    //假设状态变更
    private String random01() {
        int rd = Math.random() > 0.5 ? 1 : 0;
        return String.valueOf(rd);
    }

}
