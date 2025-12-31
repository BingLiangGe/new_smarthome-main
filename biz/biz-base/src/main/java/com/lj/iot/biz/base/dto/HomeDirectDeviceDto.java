package com.lj.iot.biz.base.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class HomeDirectDeviceDto {

    /**
     * 家ID
     */
    @NotBlank(message = "家庭ID不能为空")
    private Long homeId;

    /**
     * 设备数据
     */
    @NotBlank(message = "设备数据不能为空")
    private String data;

    /**
     * 主键ID
     */
    private Long id;

}
