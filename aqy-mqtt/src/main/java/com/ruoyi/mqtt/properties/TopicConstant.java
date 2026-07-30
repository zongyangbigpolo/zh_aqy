package com.ruoyi.mqtt.properties;

public class TopicConstant {

    public static final String TOPIC_DATA_RESP = "/api/v1/${deviceIdentity}/mqtt/dataResp";

    public static final String TOPIC_DATA_SUBSCRIBE = "/api/v1/${deviceIdentity}/mqtt/dataSubscribe";

    public static final String TOPIC_SEND_CMD = "$send/${deviceIdentity}/${cmd}";

    public static final String TOPIC_REPLY_CMD = "$reply/${deviceIdentity}/${cmd}";

    public static final String TOPIC_GET_CURRENT_TIME_CMD = "$response/${deviceIdentity}/${cmd}";
}
