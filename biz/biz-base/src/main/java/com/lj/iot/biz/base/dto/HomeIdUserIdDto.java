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
public class HomeIdUserIdDto {

    /**
     * Home ID
     */
    @NotNull(message = "家ID不能为空")
    private Long homeId;

    /**
     * 用户I ID
     */
    @NotNull(message = "用户ID不能为空")
    private String userid;

}
