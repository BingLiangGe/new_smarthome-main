package com.lj.iot.biz.base.dto;

import com.lj.iot.common.base.dto.PageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 分页
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceIdPage2Dto extends PageDto {

    /**
     * ID
     */
    private String deviceId;

    /**
     * remark
     */
    private String remark;

    /**
     * status
     */
    private Integer status;

    /**
     * action
     */
    private Integer action;
}
