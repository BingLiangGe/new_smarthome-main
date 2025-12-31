package com.lj.iot.biz.base.dto;


import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@ToString
@Data
public class AppleBindDTO implements Serializable {

    /**
     * apple授权登录码
     */
    @NotBlank(message = "授权登录码为空")
    private String identityToken;
}
