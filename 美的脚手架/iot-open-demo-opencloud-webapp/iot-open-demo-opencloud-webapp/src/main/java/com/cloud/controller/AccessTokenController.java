package com.cloud.controller;

import com.alibaba.fastjson.JSONObject;
import com.cloud.common.GrantType;
import com.cloud.common.ReturnCode;
import com.cloud.demo.DemoTokenService;
import com.cloud.demo.DemoUser;
import com.cloud.demo.DemoUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 授权相关操作
 */
@Controller
@RestController
public class AccessTokenController {

    @Autowired
    private DemoUserService demoUserService;

    @Autowired
    private DemoTokenService demoTokenService;

    /**
     * 点击登陆后提供的跳转地址，在/tmplates/authorize.html指定
     * @param body
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public JSONObject login(@RequestBody(required = true) String body) {
        JSONObject response = new JSONObject();
        try {
            JSONObject requestBody = JSONObject.parseObject(body);
            //从页面传递过来，给美的回调用的地址
            String state = requestBody.getString("state");
            String redirectUri = requestBody.getString("redirect_uri");
            /**
             * TODO 请补充自己的内部登陆逻辑
             * 此处为示例
             */
            String username = requestBody.getString("username");//该username仅作Demo用
            String password = requestBody.getString("password");
            DemoUser demoUser = demoUserService.userCheckAndGet(username,password);
            /**
             * TODO 请补充自己内部生成authorize_code逻辑
             * 此处为示例
             */
            String authorizationCode = demoTokenService.generateAuthorizationCode(demoUser.getUsername());
            String newUrl = redirectUri + "?code=" + authorizationCode + "&state=" + state;
            response.put("redirect_uri",newUrl);
            response.put("code", ReturnCode.SUCCESS.getCode());
        } catch (Exception e) {
            response.put("code", ReturnCode.ERROR.getCode());
            response.put("message", e.getMessage());
        }
        return response;
    }


    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    public JSONObject auth(@RequestBody(required = true) String body) {
        JSONObject response = new JSONObject();
        try {
            JSONObject requestBody = JSONObject.parseObject(body);
            String client_id = requestBody.getString("client_id");
            String client_secret = requestBody.getString("client_secret");
            String grant_type = requestBody.getString("grant_type");
            GrantType grantType = GrantType.valueOf(grant_type);
            String refresh_token = null,access_token = null;
            switch (grantType){
                case authorization_code:
                    //TODO 根据传参进行生成refresh_token和code
                    /**
                     * 示例
                     */
                    String code = requestBody.getString("code");
                    if(demoTokenService.checkAuthorizationCode(code)){//此处模拟认证成功的情况，code为登陆跳转后颁发给美的的authorization_code
                        String username = demoTokenService.getUserNameByAuthorizationCode(code);
                        refresh_token = demoTokenService.generateRefreshToken(username);
                        access_token = demoTokenService.generateAccessToken(username);
                    }else{
                        response.put("code",ReturnCode.UNAUTHORIZED.getCode());
                        response.put("message",ReturnCode.UNAUTHORIZED.getMsg());
                        return response;
                    }
                    break;
                case refresh_token:
                    //TODO 根据传参进行生成refresh_token进行刷新用户token
                    String requestRefreshToken = requestBody.getString("refresh_token");
                    if(demoTokenService.checkRefreshtoken(requestRefreshToken)){//此处模拟刷新成功的情况，refresh_token为grantType.authorization_code时生成
                        String username = demoTokenService.getUserNameByRefreshToken(requestRefreshToken);
                        access_token = demoTokenService.generateAccessToken(username);//重新生成token
                        refresh_token = requestRefreshToken;
                    }else {
                        response.put("code",ReturnCode.UNAUTHORIZED.getCode());
                        response.put("message",ReturnCode.UNAUTHORIZED.getMsg());
                        return response;
                    }
                    break;
            }
            response.put("refresh_token", refresh_token);//长期
            response.put("token_type", "bearer");
            response.put("access_token",access_token);//短期
            response.put("expires_in", 1800);//数字类型。时效只针对access_token。生成之后开始倒计时。
        } catch (Exception e) {
        }
        return response;
    }

}
