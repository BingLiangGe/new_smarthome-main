package com.lj.iot.common.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginVo<T> {

    /**
     * 账号
     */
    private String account;

    /**
     * token
     */
    private String token;

    /**
     * 用户信息
     */
    private T userInfo;

    /**
     * 权限
     */
    private List<String> perms;

    /**
     * 额外参数
     */
    private Object params;
}