package com.cloud.demo;

import com.cloud.controller.AccessTokenController;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.UUID;

/**
 * 用户验证服务
 */
@Component
@EnableScheduling
public class DemoTokenService {

    //用hashmap简单模拟针对每个用户的授权
    /**
     * authorization_code 跟 username的关联关系
     */
    private static HashMap<String,String> authorization_code2username = new HashMap<>();

    /**
     * refresh_token 跟 username的关联关系
     */
    private static HashMap<String,String> refresh_token2username = new HashMap<>();

    /**
     *  username 跟 access_token的关联关系
     */
    private static HashMap<String,String> username2access_token = new HashMap<>();

    /**
     *  access_token 跟 username的关联关系
     */
    private static HashMap<String,String> access_token2username = new HashMap<>();

    /**
     * authorization_code 跟 username的关联关系
     */
    private static HashMap<String,String> granteeId2openUid = new HashMap<>();


    /**
     * 生成authorization_code示例
     * @param username
     * @return
     */
    public String generateAuthorizationCode(String username) {
        String authorization_code =  UUID.randomUUID().toString() ;
        authorization_code2username.put(authorization_code,username);
        return authorization_code;
    }

    /**
     * 验证authorization_code的示例
     * @param code
     * @return
     */
    public boolean checkAuthorizationCode(String code) {
        return authorization_code2username.containsKey(code);
    }

    /**
     * 获取授权用户的示例
     * @param code
     * @return
     */
    public String getUserNameByAuthorizationCode(String code) {
        String username = authorization_code2username.get(code);
        return username;
    }

    /**
     * 生成refresh_token示例
     * @param username
     * @return
     */
    public String generateRefreshToken(String username) {
        String refresh_token = UUID.randomUUID().toString();
        refresh_token2username.put(refresh_token,username);
        return  refresh_token;
    }

    /**
     * 生成access_token示例
     * @param username
     * @return
     */
    public String generateAccessToken(String username) {
        String access_token = UUID.randomUUID().toString();
        username2access_token.put(username,access_token);
        access_token2username.put(access_token,username);
        return access_token;
    }

    /**
     * 验证requestRefreshToken的示例
     * @param requestRefreshToken
     * @return
     */
    public boolean checkRefreshtoken(String requestRefreshToken) {
        return refresh_token2username.containsKey(requestRefreshToken);
    }

    /**
     * 根据refresh_token获取授权用户的示例
     * @param requestRefreshToken
     * @return
     */
    public String getUserNameByRefreshToken(String requestRefreshToken) {
        return refresh_token2username.get(requestRefreshToken);
    }

    /**
     * 根据access_token获取授权用户的示例
     * @param username
     * @return
     */
    public String getAccessTokenByUserName(String username) {
        String access_token = username2access_token.get(username);
        access_token2username.put(access_token,username);
        return access_token;
    }

    /**
     * 根据access_token获取授权用户的示例
     * @param accessToken
     * @return
     */
    public String getUserByAccessToken(String accessToken) {
        String username = access_token2username.get(accessToken);
        return username;
    }

    /**
     * 验证access_token的示例
     * @param authorization
     * @return
     */
    public boolean checkAccessToken(String authorization) {
        return authorization.startsWith("Bearer ") && access_token2username.containsKey(authorization.replaceAll("Bearer ",""));
    }

    /**
     * 关联granteeId跟username
     * @param granteeId
     * @param username
     * @return
     */
    public void granteeId2openUid(String granteeId, String username) {
        granteeId2openUid.put(granteeId,username);
    }

    /**
     * 验证clientId
     * @param clientId
     */
    public void authClientId(String clientId) {
        System.out.println(clientId);
    }

    /**
     * 模拟清除granteeId
     * @param granteeId
     */
    public void removeGranteeId(String granteeId) {
        granteeId2openUid.remove(granteeId);
    }

    public boolean checkUserAndGrantId(String username, String granteeId) {
        if(granteeId2openUid.containsKey(granteeId)){
            if(granteeId2openUid.get(granteeId).equals(username)){
                return  true;
            }
        }
        return  false;
    }

    /**
     * 模拟清除accessToken
     * @param access_token
     */
    public void cleanAccessToken(String access_token) {
        access_token2username.remove(access_token);
    }

    /**
     * 模拟清除refresh_token
     * @param refresh_token
     */
    public void cleanRefreshToken(String refresh_token) {
        refresh_token2username.remove(refresh_token);
    }
}
