package com.lj.iot.biz.base.dto;


import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@ToString
@Data
public class AppleLoginDTO implements Serializable {

    /**
     * apple授权登录码
     */
    @NotBlank(message = "授权登录码为空")
    private String identityToken;

    @NotBlank(message = "手机号为空")
    private String mobile;

    //@NotBlank(message = "验证码为空")
    private String code;
}
