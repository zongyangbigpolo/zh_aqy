package com.ruoyi.framework.web.service;

import java.util.Map;

public interface SmsService {
    boolean sendSms(Map<String, Object> param, String phone);
}