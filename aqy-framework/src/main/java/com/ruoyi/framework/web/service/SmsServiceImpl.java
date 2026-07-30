package com.ruoyi.framework.web.service;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.config.SmsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SmsServiceImpl implements SmsService {
    private static final Logger log = LoggerFactory.getLogger(SmsServiceImpl.class);

    @Autowired
    private SmsConfig smsConfig;

    @Override
    public boolean sendSms(Map<String, Object> param, String phone) {
        if (StringUtils.isBlank(phone)) {
            log.warn("短信发送失败，手机号为空");
            return false;
        }
        if (StringUtils.isBlank(smsConfig.getAccessKeyId()) || StringUtils.isBlank(smsConfig.getAccessKeySecret())
                || StringUtils.isBlank(smsConfig.getSignName()) || StringUtils.isBlank(smsConfig.getTemplateCode())) {
            log.warn("短信发送失败，阿里云短信配置不完整");
            return false;
        }
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
            boolean success = "OK".equals(response.getCode());
            if (!success) {
                log.warn("短信发送失败，phone={}, code={}, message={}", phone, response.getCode(), response.getMessage());
            }
            return success;
        } catch (ClientException | JsonProcessingException e) {
            log.error("短信发送异常，phone={}", phone, e);
            return false;
        }
    }
}