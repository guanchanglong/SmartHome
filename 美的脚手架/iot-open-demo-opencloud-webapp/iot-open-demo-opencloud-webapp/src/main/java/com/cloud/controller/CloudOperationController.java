package com.cloud.controller;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.cloud.common.NamespaceEnum;
import com.cloud.common.ReturnCode;
import com.cloud.common.vo.ApplianceStatusVo;
import com.cloud.common.vo.ApplianceVo;
import com.cloud.common.vo.CommonRequest;
import com.cloud.common.vo.CommonResponse;
import com.cloud.demo.DemoApplianceService;
import com.cloud.demo.DemoTokenService;
import com.cloud.demo.DemoUser;
import com.cloud.demo.DemoUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 统一接口相关操作
 * 提供给美的的设备相关接口,包括
 * <li>获取用户设备列表/li>
 * <li>设备订阅/li>
 * <li>取消设备订阅/li>
 * <li>取消设备订阅/li>
 * <li>设备状态查询/li>
 */
@Controller
@RestController
public class CloudOperationController {

    @Autowired
    private DemoTokenService demoTokenService;

    @Autowired
    private DemoUserService demoUserService;

    @Autowired
    private DemoApplianceService demoApplianceService;

    @RequestMapping(value = "/cloud2cloud/operation",method= RequestMethod.POST)
    public CommonResponse operation(@RequestBody(required = true) String body,
                                       @RequestHeader(value = "Authorization", required = true ) String authorization){
        CommonResponse response = new CommonResponse();
        try {
            CommonRequest request = JSONObject.parseObject(body,CommonRequest.class);
            response.setHeader(request.getHeader());
            //TODO 增加对authrization的校验
            if(!demoTokenService.checkAccessToken(authorization)){//模拟校验的逻辑
                // 返回认证失败的错
                response.getPayload().put("code",ReturnCode.UNAUTHORIZED.getCode());
                response.getPayload().put("message",ReturnCode.UNAUTHORIZED.getMsg());
                return response;
            }
            authorization = authorization.replaceAll("Bearer ","");
            NamespaceEnum targetNamespace = NamespaceEnum.valueOf(request.getHeader().getNamespace());
            String granteeId = request.getHeader().getGranteeId();
            String username = demoTokenService.getUserByAccessToken(authorization);
            DemoUser user = demoUserService.getUser(username);
            switch (targetNamespace){
                case  UserAcceptGrant:
                    //TODO 美的拿grantId来换openUid
                    /**
                     * Demo的逻辑为
                     * 1 根据Authorization,即accessToken。获取该token的用户
                     * 2 根据美的给的granteeId，生成一个openUid给美的。作为交换，
                     * 该openUid类似与该用户的主键，即使相应的token失效或者重新认证，重新获取后也不会变
                     * 该openUid用来给美的发送请求，比如消息订阅的状态变更
                     */
                    demoTokenService.granteeId2openUid(granteeId,username);
                    response.getPayload().put("openUid",user.getOpenUid());//DEMO使用username作为openUid返回
                    break;
                case  UserCancelGrant:
                    /**
                     * 取消授权
                     * Demo的逻辑为直接解除美的给的granteeId跟openUid的关系
                     */
                    demoTokenService.removeGranteeId(granteeId);
                    response.getPayload().put("code",ReturnCode.SUCCESS.getCode());//DEMO使用username作为openUid返回
                    response.getPayload().put("message",ReturnCode.SUCCESS.getMsg());
                    break;
                case  ApplianceDiscovery:
                    /**
                     * 获取设备列表
                     * Demo的逻辑:
                     * 1.判断token的权限,需要用户同意过后的权限。这里为granteeId
                     * 2.根据用户名获取appliance列表,返回约定的格式
                     */
                    if(demoTokenService.checkUserAndGrantId(username,granteeId)){
                        List<ApplianceVo> applianceList = demoApplianceService.getUserDeviceList(username);
                        response.getPayload().put("code",ReturnCode.SUCCESS.getCode());//DEMO使用username作为openUid返回
                        response.getPayload().put("message",ReturnCode.SUCCESS.getMsg());
                        response.getPayload().put("applianceList",applianceList);
                    }else {
                        // TODO 需要加新的错误码
                    }
                    break;
                case  ApplianceSubscribe:
                    /**
                     * 设备订阅
                     * Demo的逻辑:
                     *  1. 判断token的权限,需要用户同意过后的权限。这里为granteeId
                     *  2. 记录下订阅的设备applianceCode
                     *  3. 第一订阅设备A和B,第二次订阅了B和C。那么美的IoT会保存A,B,C保存交集。
                     */
                    if(demoTokenService.checkUserAndGrantId(username,granteeId)){
                        demoApplianceService.subscribeApplianceList(username,request.getPayload().getJSONArray("applianceCodes"));
                    }else {
                        // TODO 需要加新的错误码
                    }
                    break;
                case  ApplianceUnsubscribe:
                    /**
                     * 设备取消订阅
                     * Demo的逻辑:
                     * 1.判断token的权限,需要用户同意过后的权限。这里为granteeId
                     */
                    if(demoTokenService.checkUserAndGrantId(username,granteeId)) {
                        demoApplianceService.unSubscribeApplianceList(username,request.getPayload().getJSONArray("applianceCodes"));
                    }else {

                    }
                    break;
                case  ApplianceControl:
                    /**
                     * 设备取消订阅
                     * Demo的逻辑:
                     * 1.判断token的权限,需要用户同意过后的权限。这里为granteeId
                     * 2.对user与设备的关系都应该做校验
                     */
                    if(demoTokenService.checkUserAndGrantId(username,granteeId)) {
                        JSONObject command = request.getPayload();
                        demoApplianceService.controlAppliance(username, command);
                    }else {

                    }
                    break;
                case  ApplianceState:
                    /**
                     * 设备状态获取
                     * Demo的逻辑:
                     * 1.判断token的权限,需要用户同意过后的权限。这里为granteeId
                     *
                     */
                    if(demoTokenService.checkUserAndGrantId(username,granteeId)) {
                        List<ApplianceStatusVo> applianceStatusList = demoApplianceService.getApplianceStatusByList(username, request.getPayload().getJSONArray("applianceCodes"));
                        response.getPayload().put("applianceList",applianceStatusList);
                    }else{

                    }
                    break;
            }
            response.getPayload().put("code",ReturnCode.SUCCESS.getCode());//DEMO使用username作为openUid返回
            response.getPayload().put("message",ReturnCode.SUCCESS.getMsg());
            return response;
        }catch (Exception e){
            if(e instanceof JSONException){
                response.getPayload().put("code",ReturnCode.INVALID_PARAMETER.getCode());
                response.getPayload().put("message",ReturnCode.INVALID_PARAMETER.getMsg());
            }
            else if(e.getMessage().contains("Appliance")){
                response.getPayload().put("code",ReturnCode.DEVICE_DOES_NOT_EXIST.getCode());
                response.getPayload().put("message",ReturnCode.DEVICE_DOES_NOT_EXIST.getMsg());
            }else {
                response.getPayload().put("code",ReturnCode.ERROR.getCode());
                response.getPayload().put("message",ReturnCode.ERROR.getMsg());
            }
            return response;
        }
    }
}

//http://localhost:8010/demo-opencloud/oauth2/authorize?client_id=%22test%22&state=%222222%22&response_type=code&redirect_uri=https://www.baidu.com/