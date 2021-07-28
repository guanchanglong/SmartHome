package com.cloud.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cloud.common.NamespaceEnum;
import com.cloud.common.vo.ApplianceVo;
import com.cloud.common.vo.CommonRequest;
import com.cloud.common.vo.SubscriptionHeader;
import com.cloud.demo.*;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
@RestController
public class DemoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoController.class);

    @Autowired
    private DemoTokenService demoTokenService;

    @Autowired
    private DemoApplianceService demoApplianceService;

    @Autowired
    private DemoUserService demoUserService;

    @Autowired
    private DemoConfig demoConfig;

    //演示token过期时使用。请勿实现
    @RequestMapping(value = "/tokenclear", method = RequestMethod.POST)
    public void cleanAccessToken(@RequestParam("token") String token) {
        demoTokenService.cleanAccessToken(token);
    }

    //演示Refreshtoken过期时使用。请勿实现
    @RequestMapping(value = "/demo/refreshtokenclear", method = RequestMethod.POST)
    public void cleanRefreshToken(@RequestParam("refreshtoken") String refreshtoken) {
        demoTokenService.cleanRefreshToken(refreshtoken);
    }

    //演示用户设备增加。请勿实现
    @RequestMapping(value = "/addAppliance", method = RequestMethod.POST)
    public void addAppliance(@RequestParam("mobile") String mobile,@RequestBody(required = true) String body) {
        ApplianceVo vo = JSONObject.parseObject(body,ApplianceVo.class);
        demoApplianceService.addAppliance(mobile,vo);
        notifyMeiju(mobile,vo,NamespaceEnum.ApplianceAdd.name());
    }

    //演示用户设备减少。请勿实现
    @RequestMapping(value = "/delAppliance", method = RequestMethod.POST)
    public void delAppliance(@RequestParam("mobile") String mobile,@RequestParam("applianceCode") String applianceCode) {
        demoApplianceService.delAppliance(mobile,applianceCode);
        notifyMeiju(mobile,new ApplianceVo(){{setApplianceCode(applianceCode);}},NamespaceEnum.ApplianceDelete.name());
    }

    //演示用户设备减少。请勿实现
    @RequestMapping(value = "/v1/base2pro/user/appliance/bind/report", method = RequestMethod.POST)
    public JSONObject bindReport() {
        JSONObject obj = new JSONObject();
        obj.put("errorCode",0);
        obj.put("result",null);
        obj.put("msg",null);
        return obj;
    }

    //演示设备消息订阅。请勿实现
    @RequestMapping(value = "/v1/base2pro/appliance/data/report", method = RequestMethod.POST)
    public JSONObject applianceDataReport(@RequestParam Map<String, Object> params) {

        String dataString = JSON.toJSONString(params);

        JSONObject obj = new JSONObject();
        obj.put("errorCode",0);
        obj.put("result",null);
        obj.put("msg", "ok");
        LOGGER.info("recv appliance data report from message-report-appliance: " + dataString);
        return obj;
    }


    /**
     * 演示
     * <li>设备增加通知美的</li>
     * <li>设备减少通知美的</li>
     * 其中设备状态变更
     * @param mobile
     * @param vo
     * @return
     */
    public void notifyMeiju(String mobile,ApplianceVo vo,String operation){
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)//设置链接超时
                .writeTimeout(10, TimeUnit.SECONDS) // 设置写数据超时
                .readTimeout(30, TimeUnit.SECONDS) // 设置读数据超时
                .build();
        MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
        NamespaceEnum targetNamespace = NamespaceEnum.valueOf(operation);
        DemoUser user = demoUserService.getUser(mobile);
        CommonRequest request = new CommonRequest();
        SubscriptionHeader header = new SubscriptionHeader();
        ArrayList<ApplianceVo> applianceList = new ArrayList<>();
        Response response = null;
        try {
            switch (targetNamespace){
                case  ApplianceAdd:
                    header.setReqId(UUID.randomUUID().toString());
                    header.setClientId(demoConfig.clientId);
                    header.setNamespace(NamespaceEnum.ApplianceAdd.name());
                    header.setTimestamp(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()));
                    header.setOpenUid(user.getOpenUid());
                    request.setHeader(header);
                    applianceList.add(vo);
                    request.getPayload().put("applianceList", applianceList);
                    Request request2 = new Request.Builder().url(demoConfig.midea_iot)
                            .post(okhttp3.RequestBody.create(MEDIA_TYPE, JSONObject.toJSONString(request))).build();
                    response = okHttpClient.newCall(request2).execute();
                    if (response.isSuccessful()) {
                        System.out.println("通知增加返回成功了：" + response.body().toString());
                    } else {
                        //TODO 记录点日志，发送个告警之类
                        System.out.println("返回失败了：" + response.code());
                    }
                    break;
                case  ApplianceDelete:
                    header.setReqId(UUID.randomUUID().toString());
                    header.setClientId(demoConfig.clientId);
                    header.setNamespace(NamespaceEnum.ApplianceDelete.name());
                    header.setTimestamp(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()));
                    header.setOpenUid(user.getOpenUid());
                    request.setHeader(header);
                    request.getPayload().put("applianceCodes", new ArrayList<String>(){{add(vo.getApplianceCode());}});
                    Request delReq = new Request.Builder().url(demoConfig.midea_iot)
                            .post(okhttp3.RequestBody.create(MEDIA_TYPE, JSONObject.toJSONString(request))).build();
                    response = okHttpClient.newCall(delReq).execute();
                    if (response.isSuccessful()) {
                        System.out.println("通知删除返回成功了：" + response.body().toString());
                    } else {
                        //TODO 记录点日志，发送个告警之类
                        System.out.println("返回失败了：" + response.code());
                    }
                    break;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {

        }
    }
}
