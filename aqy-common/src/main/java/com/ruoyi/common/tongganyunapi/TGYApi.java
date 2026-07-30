package com.ruoyi.common.tongganyunapi;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.hikapi.Params;
import com.ruoyi.common.utils.http.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

/**
 * @author DQC
 * @email 1183158200@qq.com
 * @date 2024/10/10 14:15
 */

@Component
public class TGYApi {
    public static final String TGY_API_URL = "http://api.cloud.tongganyun.com/";

    public static final String userName = "zhdx001";

    public static final String password = "38f0021de613fe170daf023a6d4c838d";

    public static final String grantType = "password";

    public static final String scope = "2de31614-a2ac-477a-8f5d-bbd233956f28";
    private static RedisCache redisCache;

    @Autowired
    public TGYApi(RedisCache redisCache) {
        TGYApi.redisCache = redisCache;
    }

    /**
     * 用户登录
     */
    public static JSONObject getAccessToken() throws UnsupportedEncodingException {
        String params = "username=" + URLEncoder.encode(userName, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8") + "&grant_type=" + URLEncoder.encode(grantType, "UTF-8") + "&scope=" + URLEncoder.encode(scope, "UTF-8");
        String response = HttpUtils.sendPost(TGY_API_URL + "token", params);
        JSONObject jsonObject = JSON.parseObject(response);
        if (jsonObject.getString("access_token") != null) {
            redisCache.setCacheObject("tgy_access_token", jsonObject.getString("access_token"), 12, TimeUnit.HOURS);
        }
        return jsonObject;
    }

    /**
     * 获取结构物列表
     *
     * @param id 项目id
     */
    public static JSONArray getStructures(int id) throws UnsupportedEncodingException {
        if (redisCache.getCacheObject("tgy_access_token") == null) {
            getAccessToken();
        }
        String response = HttpUtils.sendGet(TGY_API_URL + "api/projects/" + id + "/structures", null, redisCache.getCacheObject("tgy_access_token"),1);
        JSONArray jsonArray = JSONArray.parseArray(response);
        return jsonArray;
    }


    /**
     * 获取结构物中测区列表
     *
     * @param id 结构物id
     */
    public static JSONArray getMeasareas(int id) throws UnsupportedEncodingException {
        if (redisCache.getCacheObject("tgy_access_token") == null) {
            getAccessToken();
        }
        String response = HttpUtils.sendGet(TGY_API_URL + "api/structures/" + id + "/measareas", null, redisCache.getCacheObject("tgy_access_token"),1);
        JSONArray jsonArray = JSONArray.parseArray(response);
        return jsonArray;
    }

    /**
     * 获取测区下测点列表
     *
     * @param id 测区id
     */
    public static JSONArray getMeaspoints(int id) throws UnsupportedEncodingException {
        if (redisCache.getCacheObject("tgy_access_token") == null) {
            getAccessToken();
        }
        String response = HttpUtils.sendGet(TGY_API_URL + "api/measareas/" + id + "/measpoints", null, redisCache.getCacheObject("tgy_access_token"),1);
        JSONArray jsonArray = JSONArray.parseArray(response);
        return jsonArray;
    }

    /**
     * 获取结构物下数据
     *
     * @param id 测点id st 开始时间 et 结束时间
     */
    public static JSONArray getMeaspointData(int id, Long st, Long et) throws UnsupportedEncodingException {
        if (redisCache.getCacheObject("tgy_access_token") == null) {
            getAccessToken();
        }
        String response = HttpUtils.sendGet(TGY_API_URL + "api/structures/" + id + "/data/start/" + st + "/end/" + et, null, redisCache.getCacheObject("tgy_access_token"),1);
        JSONArray jsonArray = JSONArray.parseArray(response);
        return jsonArray;
    }

    /**
     * 获取测点一段时间内的数据
     *
     */
    public static JSONArray getAggregate(int measItemId) throws UnsupportedEncodingException {
        if (redisCache.getCacheObject("tgy_access_token") == null) {
            getAccessToken();
        }
        String params = "relDataFlag=true";
        String response = HttpUtils.sendGet(TGY_API_URL + "api/measitems/"+measItemId+"/rel-data/time-range/"+"1734660000/"+"1734660060", params, redisCache.getCacheObject("tgy_access_token"),1);
        JSONArray jsonArray = JSONArray.parseArray(response);
        return jsonArray;
    }


    public static JSONArray getWyData(Long measItemId, Long st, Long et) throws UnsupportedEncodingException {
        if (redisCache.getCacheObject("tgy_access_token") == null) {
            getAccessToken();
        }

        String params = "relDataFlag=true";
        String response = HttpUtils.sendGet(TGY_API_URL + "api/measitems/"+measItemId+"/rel-data/time-range/"+st+"/"+et, params, redisCache.getCacheObject("tgy_access_token"),1);
        JSONArray jsonArray = JSONArray.parseArray(response);
        return jsonArray;
    }



    /**
     * 五．获取监控事件列表
     * @param id 测区id st 开始时间 et 结束时间
     */
    public static JSONObject monitorEvents(Long id, Long st, Long et) throws UnsupportedEncodingException {
        if (redisCache.getCacheObject("tgy_access_token") == null) {
            getAccessToken();
        }

        String params = "st="+st+"&et="+et;
        String response = HttpUtils.sendGet(TGY_API_URL + "api/measareas/"+id+"/monitor-events", params, redisCache.getCacheObject("tgy_access_token"),1);
        JSONObject jsonObject = JSONObject.parseObject(response);
        return jsonObject;
    }



    /**
     * 六．获取监控事件详情
     * @param id 测区id st 开始时间 et 结束时间
     */
    public static JSONObject monitorEventsDetail(Long id) throws UnsupportedEncodingException {
        if (redisCache.getCacheObject("tgy_access_token") == null) {
            getAccessToken();
        }
        String response = HttpUtils.sendGet(TGY_API_URL + "api/monitor-events/"+id, null, redisCache.getCacheObject("tgy_access_token"),1);
        JSONObject jsonObject = JSONObject.parseObject(response);
        return jsonObject;
    }


}





//    public static JSONObject getAggregate(int measItemId) throws UnsupportedEncodingException {
//        if (redisCache.getCacheObject("tgy_access_token") == null) {
//            getAccessToken();
//        }
//        String params = "startTime=1728519165000&endTime=1728526365000&window=1&relative=true&removeAnomal=true";
//        String response = HttpUtils.sendGet(TGY_API_URL + "api/measitems/"+measItemId+"/data/aggregate", params, redisCache.getCacheObject("tgy_access_token"));
//        JSONObject jsonObject = JSONObject.parseObject(response);
//        return jsonObject;
//    }
//}
