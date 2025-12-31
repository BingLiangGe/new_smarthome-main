package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author mz
 * @Date 2022/7/19
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicMenuAddDto {

    /**
     * 音乐名称
     */
    @NotBlank(message = "音乐名称不能为空")
    private String musicName;

    /**
     * 音乐地址URL
     */
    @NotBlank(message = "音乐地址不能为空")
    private String musicUrl;
}
