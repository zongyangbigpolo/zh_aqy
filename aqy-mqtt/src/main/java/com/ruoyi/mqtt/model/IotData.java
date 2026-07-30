package com.ruoyi.mqtt.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.utils.DateUtils;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
@Data
public class IotData implements Serializable {

  /**
   * 设备id
   */
  private String deviceId;

  /**
   * 指令类型
   */
  private String cmd;

  /**
   * 创建时间
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createTime;

  public IotData(String deviceId, String cmd){
    this.deviceId = deviceId;
    this.cmd = cmd;
    this.createTime = LocalDateTime.now();
  }
}
