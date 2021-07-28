package com.cloud.controller;

import com.cloud.demo.DemoTokenService;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证相关操作
 * 授权相关接口
 * <li>通过提供https服务颁发密钥给美的</li>
 * <li>通过https请求获取美的颁发给己方的密钥</li>
 */
@Controller
@RestController
public class AuthorizeController {

    @Autowired
    private DemoTokenService demoTokenService;

    /**
     * 跳转到认证页面
     *
     * @return
     */
    @RequestMapping("/oauth2/authorize")
    public ModelAndView auth(HttpServletRequest request, HttpServletResponse response) throws Exception{
        /**
         * 美的调用授权接口给的值。
         * 需要回调美的重定向接口的时传递回来的state和redirectUri
         * 此处的示例仅提供一种思路
         * 1. 传递到值认证页面
         * 2. 认证页面成功后，传递会美的
         */
        OAuthAuthzRequest oAuthRequest = new OAuthAuthzRequest(request);
        String state = oAuthRequest.getState();
        String clientId = oAuthRequest.getClientId();
        String redirectUri = oAuthRequest.getRedirectURI();
        String responseType = oAuthRequest.getResponseType();

        //TODO 服务提供方内部逻辑
        //示例 验证client_id 是否合法
        demoTokenService.authClientId(clientId);
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("state", state);
        modelMap.put("client_id", clientId);
        modelMap.put("response_type", responseType);
        modelMap.put("redirect_uri", redirectUri);
        return new ModelAndView("authorize",modelMap);
    }

}
