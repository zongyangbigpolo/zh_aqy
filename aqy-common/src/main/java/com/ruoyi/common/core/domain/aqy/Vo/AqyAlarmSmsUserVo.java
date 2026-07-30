package com.ruoyi.common.core.domain.aqy.Vo;

/**
 * 报警短信接收人用户选项。
 *
 * @author Copilot
 */
public class AqyAlarmSmsUserVo {
    private Long userId;

    private String userName;

    private String nickName;

    private String phonenumber;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
}
