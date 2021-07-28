package com.cloud.common.vo;

import com.alibaba.fastjson.JSONObject;

/**
 * 公用的返回
 */
public class CommonResponse {
    private Header header;
    //TODO 需要确认
    private JSONObject payload = new JSONObject();

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public JSONObject getPayload() {
        return payload;
    }

    public void setPayload(JSONObject payload) {
        this.payload = payload;
    }
}
