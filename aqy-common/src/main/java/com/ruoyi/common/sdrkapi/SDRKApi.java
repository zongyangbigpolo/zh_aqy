package com.ruoyi.common.sdrkapi;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.tongganyunapi.TGYApi;
import com.ruoyi.common.utils.http.HttpUtils;
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
public class SDRKApi {

    public static final String SDRK_API_URL = "http://www.0531yun.com/";

    public static final String loginName = "h241011zhaq";

    public static final String password = "h241011zhaq";

//    public static final String deviceAddr = "40353024";

    private static RedisCache redisCache;

    @Autowired
    public SDRKApi(RedisCache redisCache) {
        SDRKApi.redisCache = redisCache;
    }
    /**
     * 用户登录
     */
    public static JSONObject getAccessToken() throws UnsupportedEncodingException {
        String params = "loginName=" + URLEncoder.encode(loginName, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8");
        String response = HttpUtils.sendGet(SDRK_API_URL + "api/getToken", params);
        JSONObject jsonObject = JSON.parseObject(response);
        if ( jsonObject.getJSONObject("data").getString("token")!= null) {
            redisCache.setCacheObject("sdrk_access_token", jsonObject.getJSONObject("data").getString("token"), 2, TimeUnit.HOURS);
        }
        return jsonObject;
    }

    /**
     * 获取实时数据
     * @return
     * @throws UnsupportedEncodingException
     */
    public static JSONObject getRealTimeDataByDeviceAddr(String deviceAddr) throws UnsupportedEncodingException {
        if (redisCache.getCacheObject("sdrk_access_token") == null) {
            getAccessToken();
        }
        String params = "deviceAddrs=" + deviceAddr ;
        String response = HttpUtils.sendGet(SDRK_API_URL + "api/data/getRealTimeDataByDeviceAddr", params, redisCache.getCacheObject("sdrk_access_token"),2);
        System.out.println("----------"+response);
        JSONObject jsonObject = JSON.parseObject(response);
        return jsonObject;
    }


    /**
     * 获取历史数据列表
     * @return
     * @throws UnsupportedEncodingException
     */
    public static JSONArray getHistoryList(String deviceAddr,Long nodeId,String startTime,String endTime) throws UnsupportedEncodingException {
        if (redisCache.getCacheObject("sdrk_access_token") == null) {
            getAccessToken();
        }
        String params = "deviceAddr=" + deviceAddr + "&nodeId=" + nodeId + "&startTime=" + startTime + "&endTime=" + endTime;
        String response = HttpUtils.sendGet(SDRK_API_URL + "api/data/historyList", params, redisCache.getCacheObject("sdrk_access_token"),2);
        JSONArray jsonArray = JSONArray.parseArray(response);
        return jsonArray;
    }


    public static void main(String[] args) throws UnsupportedEncodingException {
      JSONObject accessToken = getAccessToken();
//        getRealTimeDataByDeviceAddr("40353024")
    }
}
