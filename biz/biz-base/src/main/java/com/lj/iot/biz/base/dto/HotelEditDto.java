package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelEditDto {

    /**
     * 酒店ID
     */
    @NotNull(message = "酒店ID不能为空")
    private Long id;

    /**
     * 酒店名
     */
    @NotBlank(message = "酒店名不能为空")
    private String hotelName;
}
