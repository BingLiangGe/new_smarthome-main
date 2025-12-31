package com.lj.iot.biz.base.dto;

import com.lj.iot.common.base.dto.PageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 分页
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeIdPageDto extends PageDto {

    /**
     * 家ID
     */
    @NotNull(message = "家ID不能为空")
    private Long homeId;
}
