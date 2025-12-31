package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @author mz
 * @Date 2022/7/19
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusicChangeDto {

    /**
     * 设备ID
     */
    @NotBlank(message = "ID不能为空")
    private String deviceId;
    /**
     * 播放类型
     */
    @NotBlank(message = "播放类型：0,歌单，1，暂停 ，2开始,3音量")
    private String type;
    /**
     * 歌曲ID
     */
    private String musicId;
    /**
     * 音量
     */
    private String volume;
 }
