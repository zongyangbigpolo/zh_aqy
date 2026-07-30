package com.ruoyi.framework.web.service;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.framework.config.SmsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SmsServiceImpl implements SmsService {
    @Autowired
    private SmsConfig smsConfig;

    @Override
    public boolean sendSms(Map<String, Object> param, String phone) {
        try {
            DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", smsConfig.getAccessKeyId(), smsConfig.getAccessKeySecret());
            IAcsClient client = new DefaultAcsClient(profile);
            SendSmsRequest request = new SendSmsRequest();
            request.setPhoneNumbers(phone);
            request.setSignName(smsConfig.getSignName());
            request.setTemplateCode(smsConfig.getTemplateCode());
            // 将HashMap转化为JSON字符串
            String templateParam = new ObjectMapper().writeValueAsString(param);
            request.setTemplateParam(templateParam);
            SendSmsResponse response = client.getAcsResponse(request);
            return "OK".equals(response.getCode());
        } catch (ClientException | JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
    }
}