package com.cloud.demo;

import java.util.HashMap;

/**
 * Demo示例，用户
 */
public class DemoUser {
    private String openUid;
    private String username;
    private String password;

    public DemoUser(String openUid, String username,String password){
        this.openUid = openUid;
        this.username = username;
        this.password = password;
    }



    public String getOpenUid() {
        return openUid;
    }

    public void setOpenUid(String openUid) {
        this.openUid = openUid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
