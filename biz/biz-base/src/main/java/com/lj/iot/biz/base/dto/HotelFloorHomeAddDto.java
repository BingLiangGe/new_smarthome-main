package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelFloorHomeAddDto {

    /**
     * 楼层ID
     */
    @NotNull(message = "楼层ID不能为空")
    private Long floorId;
    /**
     * 家庭名称
     */
    @NotBlank(message = "家庭名称不能为空")
    private String homeName;
}
