package com.cloud.common;

/**
 * 所有返回码的枚举
 * 根据文档列出协商好的错误码
 * TODO 要持续补充
 */
public enum ReturnCode {
    ERROR(10001, "INTERNAL_ERROR"),
    UNAUTHORIZED(10002, "UNAUTHORIZED"),
    EXPIRED_ACCESSTOKEN_CREDENTIAL(10003, "EXPIRED_ACCESSTOKEN_CREDENTIAL"),
    INVALID_PARAMETER(10004, "INVALID_PARAMETER"),
    DEVICE_DOES_NOT_EXIST(10005, "DEVICE_DOES_NOT_EXIST"),
    OK(200, "OK"),
    IN_LIMITED(500, "服务器异常"),
    SUCCESS(0, "*****");

    private int code;
    private String msg;

    ReturnCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ReturnCode getCode(int errorCode) {
        ReturnCode[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            ReturnCode c = var1[var3];
            if (errorCode == c.getCode()) {
                return c;
            }
        }
        return ERROR;
    }

    public int getCode() {
        return this.code;
    }

    public String getStrCode() {
        return String.valueOf(this.code);
    }

    public String getMsg() {
        return this.msg;
    }


}
