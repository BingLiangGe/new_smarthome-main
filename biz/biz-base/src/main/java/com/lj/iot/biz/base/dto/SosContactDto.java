package com.lj.iot.biz.base.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SosContactDto {

    /**
     * 主键ID 新增不传，修改必填
     */
    private Long id;

    /**
     * 联系人类型
     */
    @NotNull(message = "联系人类型不能为空")
    private String contactType;

    /**
     * 家庭id
     */
    @NotNull(message = "家ID不能为空")
    private Long homeId;

    /**
     * 电话号码
     */
    @NotNull(message = "电话号码不能为空")
    private String phoneNumber;

    /**
     * 联系人名称
     */
    @NotNull(message = "联系人名称不能为空")
    private String username;
}
