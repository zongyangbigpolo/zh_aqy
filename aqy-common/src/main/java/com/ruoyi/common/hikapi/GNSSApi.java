package com.ruoyi.common.hikapi;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.sdrkapi.SDRKApi;
import com.ruoyi.common.utils.http.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.concurrent.TimeUnit;

/**
 * @author DQC
 * @email 1183158200@qq.com
 * @date 2024/9/26 16:01
 */
@Component
public class GNSSApi {

    public static final String appKey = "648366335081832";

    public static final String secretKey = "BXZuFgXwAAYXMJmhJqUO";

    private static RedisCache redisCache;

    @Autowired
    public GNSSApi(RedisCache redisCache) {
        GNSSApi.redisCache = redisCache;
    }
    /**
     * 获取接口调用令牌
     */
    public static JSONObject getAccessToken() {
        String url = "https://ghdopen.hikyun.com/artemis/oauth/token";
        String params = "client_id=" + appKey + "&client_secret=" + secretKey;
        String response =  HttpUtils.sendPost(url, params);
        JSONObject jsonObject = JSON.parseObject(response);
        if (jsonObject.getString("access_token") != null) {
            redisCache.setCacheObject("hik_access_token", jsonObject.getString("access_token"), 12, TimeUnit.HOURS);
        }
        return jsonObject;
    }


    /**
     * 获取Web端视频监控画面的地址
     */
    public static JSONObject getWebVideoUrl(@RequestBody Params params) {
        String url = "https://ghdopen.hikyun.com/artemis/api/ghd/v1/video/live/web";
        String response  = HttpUtils.sendPost(url, params);
        JSONObject jsonObject = JSON.parseObject(response);
        return jsonObject;
    }
    /**
     * 查询设备详情
     */
    public static JSONObject byDeviceSerial(@RequestBody Params params) {
        String url = "https://ghdopen.hikyun.com/artemis/api/ghd/v1/device/byDeviceSerial";
        String response  = HttpUtils.sendPost(url, params);
        JSONObject jsonObject = JSON.parseObject(response);
        return jsonObject;
    }

    /**
     * 查询历史监测数据
     */
    public static JSONObject getHistoryMonitorData(@RequestBody Params params) {
        if (redisCache.getCacheObject("hik_access_token") == null) {
            getAccessToken();
        }
        params.setHikToken(redisCache.getCacheObject("hik_access_token"));
        String url = " https://ghdopen.hikyun.com/artemis/api/ghd/v1/monitorData/realTime/query/page";
        String response  = HttpUtils.sendPost(url, params);
        JSONObject jsonObject = JSON.parseObject(response);
        return jsonObject;
    }

}
