package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 语音识别记录
 * </p>
 *
 * @author xm
 * @since 2022-10-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("speech_record")
public class SpeechRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 家庭ID
     */
    private Long homeId;

    /**
     * 意图名
     */
    private String intentName;


    /**
     * 识别文本
     */
    private String text;

    /**
     * 回复文本
     */
    private String answer;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createTime;
}
