package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author mz
 * @Date 2022/7/19
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceExportDto {

    /**
     * 产品ID
     */
    @NotBlank(message = "产品ID不能为空")
    private String productId;

    /**
     * 前缀
     */
    @Size(max = 4, message = "设备ID前缀不能超过4")
    private String prefix;

    /**
     * 数量
     */
    @NotNull(message = "生成数量不能为空")
    private Integer number;

}
