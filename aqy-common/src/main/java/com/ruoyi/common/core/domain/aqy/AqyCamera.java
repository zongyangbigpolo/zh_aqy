package com.ruoyi.common.core.domain.aqy;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 监控摄像头对象 aqy_camera
 *
 * @author MXJ
 * @date 2024-10-13
 */
@Data
public class AqyCamera extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 工程项目 */
    @Excel(name = "工程项目")
    private Long projectId;

    /** 摄像头名称 */
    @Excel(name = "摄像头名称")
    private String eqmtName;

    /** 设备编号 */
    @Excel(name = "设备编号")
    private String qmtCode;

    /** IP地址 */
    @Excel(name = "IP地址")
    private String ip;

    /** 端口号 */
    @Excel(name = "端口号")
    private Integer port;

    /** 登录账号 */
    @Excel(name = "登录账号")
    private String userName;

    /** 登录密码 */
    @Excel(name = "登录密码")
    private String password;

    /** 是否显示到大屏 */
    @Excel(name = "是否显示到大屏")
    private Integer showFront;

    /** 在线状态 */
    @Excel(name = "在线状态")
    private Integer onlineStatus;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long createUid;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Integer isDelete;


    private String appKey;

    private String secret;

    /** 序列号 */
    @Excel(name = "序列号")
    private String deviceSerial;

    /** 验证码 */
    @Excel(name = "验证码")
    private String deviceCode;

    /** 摄像头链接 */
    @Excel(name = "摄像头链接")
    private String cameraUrl;

    /** 1GNSS 2普通摄像机 */
    @Excel(name = "1GNSS 2普通摄像机")
    private Long type;

    private String accessToken;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setProjectId(Long projectId)
    {
        this.projectId = projectId;
    }

    public Long getProjectId()
    {
        return projectId;
    }
    public void setEqmtName(String eqmtName)
    {
        this.eqmtName = eqmtName;
    }

    public String getEqmtName()
    {
        return eqmtName;
    }
    public void setQmtCode(String qmtCode)
    {
        this.qmtCode = qmtCode;
    }

    public String getQmtCode()
    {
        return qmtCode;
    }
    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public String getIp()
    {
        return ip;
    }
    public void setPort(Integer port)
    {
        this.port = port;
    }

    public Integer getPort()
    {
        return port;
    }
    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getUserName()
    {
        return userName;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPassword()
    {
        return password;
    }
    public void setShowFront(Integer showFront)
    {
        this.showFront = showFront;
    }

    public Integer getShowFront()
    {
        return showFront;
    }
    public void setOnlineStatus(Integer onlineStatus)
    {
        this.onlineStatus = onlineStatus;
    }

    public Integer getOnlineStatus()
    {
        return onlineStatus;
    }
    public void setCreateUid(Long createUid)
    {
        this.createUid = createUid;
    }

    public Long getCreateUid()
    {
        return createUid;
    }
    public void setIsDelete(Integer isDelete)
    {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete()
    {
        return isDelete;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("projectId", getProjectId())
            .append("eqmtName", getEqmtName())
            .append("qmtCode", getQmtCode())
            .append("ip", getIp())
            .append("port", getPort())
            .append("userName", getUserName())
            .append("password", getPassword())
            .append("showFront", getShowFront())
            .append("onlineStatus", getOnlineStatus())
            .append("createTime", getCreateTime())
            .append("createUid", getCreateUid())
            .append("isDelete", getIsDelete())
            .toString();
    }
}
