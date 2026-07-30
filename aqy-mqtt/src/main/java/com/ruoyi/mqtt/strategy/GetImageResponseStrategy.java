package com.ruoyi.mqtt.strategy;

import cn.hutool.json.JSONUtil;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.domain.aqy.AqyEquipment;
import com.ruoyi.common.core.domain.aqy.AqyEquipmentImage;
import com.ruoyi.common.core.domain.aqy.AqyMqttCmdMessage;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.file.ImageUtils;
import com.ruoyi.service.IAqyEquipmentImageService;
import com.ruoyi.service.IAqyEquipmentService;
import com.ruoyi.service.IAqyMqttCmdMessageService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 远程抓取图片(机器视觉测量仪)
 * @Author：MXJ
 * @Date：2024/10/11 16:41
 */
@Slf4j
@Component
public class GetImageResponseStrategy implements MessageHandlerStrategy{
    @Autowired
    private IAqyMqttCmdMessageService mqttCmdMessageService;

    @Autowired
    private IAqyEquipmentImageService equipmentImageService;

    @Autowired
    private IAqyEquipmentService aqyEquipmentService;


    @Override
    public void handle(String payload) {
        log.info("GetImageResponseStrategy收到消息:" + payload);
        ImageResultData bean = JSONUtil.toBean(payload, ImageResultData.class);
        if(bean != null) {
            AqyMqttCmdMessage mqttCmdMessage = mqttCmdMessageService.selectAqyMqttCmdMessageByMsgId(bean.getMsgId(), bean.getCmd());
            mqttCmdMessage.setReplyRest("抓取图片成功");
            mqttCmdMessageService.updateAqyMqttCmdMessage(mqttCmdMessage);
            String imagePath = saveImageInDisk(bean.getData().getImage());

            List<AqyEquipment> aqyEquipmentList = aqyEquipmentService.selectAqyEquipmentByCode(mqttCmdMessage.getEqmtCode());
            if(aqyEquipmentList != null){
                for (AqyEquipment aqyEquipment : aqyEquipmentList) {
                    AqyEquipmentImage aqyEquipmentImage = new AqyEquipmentImage();
                    aqyEquipmentImage.setEquipmentId(aqyEquipment.getId());
                    aqyEquipmentImage.setImageUrl(imagePath);
                    aqyEquipmentImage.setCreateTime(DateUtils.getNowDate());
                    equipmentImageService.insertAqyEquipmentImage(aqyEquipmentImage);
                }
            }
        }
    }

    private String saveImageInDisk(String base64Image){
        String[] nowDate = DateUtils.getDate().split("-");
        String ext = "png";
        String path = RuoYiConfig.getProfile() + "/upload/" + nowDate[0] + "/" + nowDate[1] + "/" + nowDate[2] + "/" + DateUtils.dateTimeNow() + "." + ext;
        ImageUtils.writeImage(base64Image, path, ext);
        return path;
    }

    @Override
    public String getTopicPattern() {
        return "\\$reply/.*/getImg";
    }

    private String topic;

    @Override
    public void setTopic(String topic) {
        this.topic = topic;
    }
}

@Data
class ImageResultData {
    private String cmd;

    private CatchImageData data;

    private Integer msgId;
}

@Data
class CatchImageData{
    private String captureTime;

    private String format;

    private Integer hParam;

    private String image;

    private Integer wParam;
}

