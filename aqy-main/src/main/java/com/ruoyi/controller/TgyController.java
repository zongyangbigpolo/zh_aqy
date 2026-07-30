package com.ruoyi.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.tongganyunapi.TGYApi;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

/**
 * @author DQC
 * @email 1183158200@qq.com
 * @date 2024/10/10 15:15
 * @description 同感云平台控制器
 */
@RestController
@RequestMapping("/aqy/tgy")
@Component("ryTgy")
public class TgyController {

    /**
     * 获取接口调用令牌
     */
    @PostMapping("/getAccessToken")
    public AjaxResult getAccessToken() throws UnsupportedEncodingException {
        return AjaxResult.success(TGYApi.getAccessToken());
    }

    /**
     * 获取结构物列表
     */
    @GetMapping("/getStructures/{id}")
    public AjaxResult getStructures(@PathVariable int id) throws UnsupportedEncodingException {
        return AjaxResult.success(TGYApi.getStructures(id));
    }

    /**
     * 获取结构物中测区列表
     *
     * @param id 结构物ID
     */
    @GetMapping("/getMeasareas/{id}")
    public AjaxResult getMeasareas(@PathVariable int id) throws UnsupportedEncodingException {
        return AjaxResult.success(TGYApi.getMeasareas(id));
    }

    /**
     * 获取测点列表
     *
     * @param id 测区ID
     */
    @GetMapping("/getMeaspoints/{id}/")
    public AjaxResult getMeaspoints(@PathVariable int id) throws UnsupportedEncodingException {
        return AjaxResult.success(TGYApi.getMeaspoints(id));
    }

    /**
     * 获取测点数据
     *
     * @param id 测点ID
     */
    @GetMapping("/getMeaspointData/{id}/data/start/{st}/end/{et}")
    public AjaxResult getMeaspointData(@PathVariable int id, @PathVariable Long st, @PathVariable Long et) throws UnsupportedEncodingException {
        return AjaxResult.success(TGYApi.getMeaspointData(id, st, et));
    }

    /**
     * 获取测点一段时间内的数据
     *
     */
    @GetMapping("/getAggregate/{measItemId}")
    public AjaxResult getAggregate(@PathVariable int measItemId) throws UnsupportedEncodingException {
        return AjaxResult.success(TGYApi.getAggregate(measItemId));
    }
}
