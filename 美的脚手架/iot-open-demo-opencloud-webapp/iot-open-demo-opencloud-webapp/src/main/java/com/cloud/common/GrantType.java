package com.cloud.common;

/**
 * 所有授权类型的枚举
 */
public enum GrantType {
    authorization_code("authorization_code"), refresh_token("authorization_code");
    private final String grantType;

    GrantType(String grantType) {
        this.grantType = grantType;
    }
}
