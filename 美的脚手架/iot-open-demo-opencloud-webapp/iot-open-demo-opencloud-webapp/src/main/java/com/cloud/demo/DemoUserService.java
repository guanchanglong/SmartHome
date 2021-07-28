package com.cloud.demo;

import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Demo用户服务
 * 假设DB里面有4个用户<code>DemoUser</code>
 */
@Component
public class DemoUserService {
    private static HashMap<String,DemoUser> users = new HashMap<String,DemoUser>(){{
        put("13511111111",new DemoUser("aaaa-111111111","13511111111","123456"));
        put("13522222222",new DemoUser("aaaa-111111112","13522222222","123456"));
        put("13533333333",new DemoUser("aaaa-111111113","13533333333","123456"));
        put("13588888888",new DemoUser("aaaa-111111118","13588888888","123456"));
    }};

    /**
     * 校验用户在不在，在就返回
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public DemoUser userCheckAndGet(String username,String password) throws Exception {
        if(users.containsKey(username)){
            DemoUser user = users.get(username);
            if(user.getPassword().equals(password)){
                return user;
            }
        }
        throw new Exception("用户不存在或密码错误");
    }

    /**
     * 获取用户
     * @param username
     * @return
     */
    public DemoUser getUser(String username){
        return users.get(username);
    }

}
