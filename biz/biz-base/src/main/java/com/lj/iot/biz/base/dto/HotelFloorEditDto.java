package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class HotelFloorEditDto {

    /**
     * 楼层ID
     */
    @NotNull(message = "ID不能为空")
    private Long floorId;

    /**
     * 楼层名称
     */
    @NotNull(message = "楼层名称不能为空")
    private String floorName;
}
