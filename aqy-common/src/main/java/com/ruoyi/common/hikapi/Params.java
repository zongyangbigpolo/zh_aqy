package com.ruoyi.common.hikapi;

/**
 * @author DQC
 * @email 1183158200@qq.com
 * @date 2024/9/27 15:19
 */

public class Params {
    private String hikToken;
    private String deviceSerial;
    private String channelNo;
    private String videoLevel;
    private String[] deviceSerialList;

    private String startTime;
    private String endTime;
    private Integer   pageSize;
    private Integer     pageNo;
    public String getHikToken() {
        return hikToken;
    }

    public void setHikToken(String hikToken) {
        this.hikToken = hikToken;
    }

    public String getDeviceSerial() {
        return deviceSerial;
    }

    public void setDeviceSerial(String deviceSerial) {
        this.deviceSerial = deviceSerial;
    }

    public String getChannelNo() {
        return channelNo;
    }

    public void setChannelNo(String channelNo) {
        this.channelNo = channelNo;
    }

    public String getVideoLevel() {
        return videoLevel;
    }

    public void setVideoLevel(String videoLevel) {
        this.videoLevel = videoLevel;
    }

    public String[] getDeviceSerialList() {
        return deviceSerialList;
    }

    public void setDeviceSerialList(String[] deviceSerialList) {
        this.deviceSerialList = deviceSerialList;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }
}
