package com.ruoyi.common.sdrkapi;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.http.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

/**
 * @author DQC
 * @email 1183158200@qq.com
 * @date 2025/2/20 17:09
 */
@Component
@Slf4j
public class SDRKApi
{
    public static final String DEFAULT_SDRK_API_URL = "http://www.0531yun.com/";

    private static final String SDRK_API_URL_ENV = "SDRK_API_URL";
    private static final String SDRK_LOGIN_NAME_ENV = "SDRK_LOGIN_NAME";
    private static final String SDRK_PASSWORD_ENV = "SDRK_PASSWORD";

    private static RedisCache redisCache;

    @Autowired
    public SDRKApi(RedisCache redisCache)
    {
        SDRKApi.redisCache = redisCache;
    }

    /**
     * 用户登录
     */
    public static JSONObject getAccessToken() throws UnsupportedEncodingException
    {
        String params = "loginName=" + URLEncoder.encode(getRequiredEnv(SDRK_LOGIN_NAME_ENV), "UTF-8")
                + "&password=" + URLEncoder.encode(getRequiredEnv(SDRK_PASSWORD_ENV), "UTF-8");
        String response = HttpUtils.sendGet(getApiUrl() + "api/getToken", params);
        JSONObject jsonObject = JSON.parseObject(response);
        JSONObject data = jsonObject.getJSONObject("data");
        if (data != null && data.getString("token") != null)
        {
            redisCache.setCacheObject("sdrk_access_token", data.getString("token"), 2, TimeUnit.HOURS);
        }
        return jsonObject;
    }

    /**
     * 获取实时数据
     */
    public static JSONObject getRealTimeDataByDeviceAddr(String deviceAddr) throws UnsupportedEncodingException
    {
        if (redisCache.getCacheObject("sdrk_access_token") == null)
        {
            getAccessToken();
        }
        String params = "deviceAddrs=" + deviceAddr;
        String response = HttpUtils.sendGet(getApiUrl() + "api/data/getRealTimeDataByDeviceAddr", params,
                redisCache.getCacheObject("sdrk_access_token"), 2);
        log.debug("SDRK real-time response received for device {}", deviceAddr);
        return JSON.parseObject(response);
    }

    /**
     * 获取历史数据列表
     */
    public static JSONArray getHistoryList(String deviceAddr, Long nodeId, String startTime, String endTime)
            throws UnsupportedEncodingException
    {
        if (redisCache.getCacheObject("sdrk_access_token") == null)
        {
            getAccessToken();
        }
        String params = "deviceAddr=" + deviceAddr + "&nodeId=" + nodeId + "&startTime=" + startTime + "&endTime=" + endTime;
        String response = HttpUtils.sendGet(getApiUrl() + "api/data/historyList", params,
                redisCache.getCacheObject("sdrk_access_token"), 2);
        return JSONArray.parseArray(response);
    }

    private static String getApiUrl()
    {
        String apiUrl = System.getenv(SDRK_API_URL_ENV);
        if (StringUtils.isBlank(apiUrl))
        {
            return DEFAULT_SDRK_API_URL;
        }
        return apiUrl.endsWith("/") ? apiUrl : apiUrl + "/";
    }

    private static String getRequiredEnv(String name)
    {
        String value = System.getenv(name);
        if (StringUtils.isBlank(value))
        {
            throw new IllegalStateException(name + " is required before using SDRK API");
        }
        return value;
    }
}
