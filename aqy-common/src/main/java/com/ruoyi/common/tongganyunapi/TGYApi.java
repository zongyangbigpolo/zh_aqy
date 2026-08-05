package com.ruoyi.common.tongganyunapi;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.http.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

/**
 * @author DQC
 * @email 1183158200@qq.com
 * @date 2024/10/10 14:15
 */
@Component
public class TGYApi
{
    public static final String DEFAULT_TGY_API_URL = "http://api.cloud.tongganyun.com/";

    private static final String TGY_API_URL_ENV = "TGY_API_URL";
    private static final String TGY_USERNAME_ENV = "TGY_USERNAME";
    private static final String TGY_PASSWORD_ENV = "TGY_PASSWORD";
    private static final String TGY_GRANT_TYPE_ENV = "TGY_GRANT_TYPE";
    private static final String TGY_SCOPE_ENV = "TGY_SCOPE";

    private static RedisCache redisCache;

    @Autowired
    public TGYApi(RedisCache redisCache)
    {
        TGYApi.redisCache = redisCache;
    }

    /**
     * 用户登录
     */
    public static JSONObject getAccessToken() throws UnsupportedEncodingException
    {
        String params = "username=" + URLEncoder.encode(getRequiredEnv(TGY_USERNAME_ENV), "UTF-8")
                + "&password=" + URLEncoder.encode(getRequiredEnv(TGY_PASSWORD_ENV), "UTF-8")
                + "&grant_type=" + URLEncoder.encode(getEnvOrDefault(TGY_GRANT_TYPE_ENV, "password"), "UTF-8")
                + "&scope=" + URLEncoder.encode(getRequiredEnv(TGY_SCOPE_ENV), "UTF-8");
        String response = HttpUtils.sendPost(getApiUrl() + "token", params);
        JSONObject jsonObject = JSON.parseObject(response);
        if (jsonObject.getString("access_token") != null)
        {
            redisCache.setCacheObject("tgy_access_token", jsonObject.getString("access_token"), 12, TimeUnit.HOURS);
        }
        return jsonObject;
    }

    /**
     * 获取结构物列表
     *
     * @param id 项目id
     */
    public static JSONArray getStructures(int id) throws UnsupportedEncodingException
    {
        ensureAccessToken();
        String response = HttpUtils.sendGet(getApiUrl() + "api/projects/" + id + "/structures", null,
                redisCache.getCacheObject("tgy_access_token"), 1);
        return JSONArray.parseArray(response);
    }

    /**
     * 获取结构物中测区列表
     *
     * @param id 结构物id
     */
    public static JSONArray getMeasareas(int id) throws UnsupportedEncodingException
    {
        ensureAccessToken();
        String response = HttpUtils.sendGet(getApiUrl() + "api/structures/" + id + "/measareas", null,
                redisCache.getCacheObject("tgy_access_token"), 1);
        return JSONArray.parseArray(response);
    }

    /**
     * 获取测区下测点列表
     *
     * @param id 测区id
     */
    public static JSONArray getMeaspoints(int id) throws UnsupportedEncodingException
    {
        ensureAccessToken();
        String response = HttpUtils.sendGet(getApiUrl() + "api/measareas/" + id + "/measpoints", null,
                redisCache.getCacheObject("tgy_access_token"), 1);
        return JSONArray.parseArray(response);
    }

    /**
     * 获取结构物下数据
     *
     * @param id 测点id
     * @param st 开始时间
     * @param et 结束时间
     */
    public static JSONArray getMeaspointData(int id, Long st, Long et) throws UnsupportedEncodingException
    {
        ensureAccessToken();
        String response = HttpUtils.sendGet(getApiUrl() + "api/structures/" + id + "/data/start/" + st + "/end/" + et,
                null, redisCache.getCacheObject("tgy_access_token"), 1);
        return JSONArray.parseArray(response);
    }

    /**
     * 获取测点一段时间内的数据
     */
    public static JSONArray getAggregate(int measItemId) throws UnsupportedEncodingException
    {
        ensureAccessToken();
        String params = "relDataFlag=true";
        String response = HttpUtils.sendGet(getApiUrl() + "api/measitems/" + measItemId
                + "/rel-data/time-range/1734660000/1734660060", params,
                redisCache.getCacheObject("tgy_access_token"), 1);
        return JSONArray.parseArray(response);
    }

    public static JSONArray getWyData(Long measItemId, Long st, Long et) throws UnsupportedEncodingException
    {
        ensureAccessToken();
        String params = "relDataFlag=true";
        String response = HttpUtils.sendGet(getApiUrl() + "api/measitems/" + measItemId
                + "/rel-data/time-range/" + st + "/" + et, params,
                redisCache.getCacheObject("tgy_access_token"), 1);
        return JSONArray.parseArray(response);
    }

    /**
     * 五．获取监控事件列表
     *
     * @param id 测区id
     * @param st 开始时间
     * @param et 结束时间
     */
    public static JSONObject monitorEvents(Long id, Long st, Long et) throws UnsupportedEncodingException
    {
        ensureAccessToken();
        String params = "st=" + st + "&et=" + et;
        String response = HttpUtils.sendGet(getApiUrl() + "api/measareas/" + id + "/monitor-events", params,
                redisCache.getCacheObject("tgy_access_token"), 1);
        return JSONObject.parseObject(response);
    }

    /**
     * 六．获取监控事件详情
     *
     * @param id 测区id
     */
    public static JSONObject monitorEventsDetail(Long id) throws UnsupportedEncodingException
    {
        ensureAccessToken();
        String response = HttpUtils.sendGet(getApiUrl() + "api/monitor-events/" + id, null,
                redisCache.getCacheObject("tgy_access_token"), 1);
        return JSONObject.parseObject(response);
    }

    private static void ensureAccessToken() throws UnsupportedEncodingException
    {
        if (redisCache.getCacheObject("tgy_access_token") == null)
        {
            getAccessToken();
        }
    }

    private static String getApiUrl()
    {
        String apiUrl = System.getenv(TGY_API_URL_ENV);
        if (StringUtils.isBlank(apiUrl))
        {
            return DEFAULT_TGY_API_URL;
        }
        return apiUrl.endsWith("/") ? apiUrl : apiUrl + "/";
    }

    private static String getEnvOrDefault(String name, String defaultValue)
    {
        String value = System.getenv(name);
        return StringUtils.isBlank(value) ? defaultValue : value;
    }

    private static String getRequiredEnv(String name)
    {
        String value = System.getenv(name);
        if (StringUtils.isBlank(value))
        {
            throw new IllegalStateException(name + " is required before using TGY API");
        }
        return value;
    }
}
