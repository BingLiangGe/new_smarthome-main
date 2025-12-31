package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class WeChatLoginDto {

    /**
     * 代码
     */

    private String code;

    /**
     * 用户ID
     */
    //@NotBlank(message = "用户ID不能为空")
    private String userId;

    /**
     * 电话号码
     */
    private String mobile;

    /**
     * 电话号码
     */
    private String openid;


    private String smscode;
}
