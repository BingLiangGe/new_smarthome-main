package com.lj.iot.common.base.constant;

public class RedisConstant {

    // 项目前缀
    public static final String PRE = "_";

    // 会话TOKEN用户信息
    public static final String SESSION_TOKEN_2_USER = PRE + "session:token:user:";

    // 会话TOKEN
    public static final String SESSION_TOKEN_2_ACCOUNT = PRE + "session:token:account:";

    // 会话MOBILE
    public static final String SESSION_ACCOUNT_2_TOKEN = PRE + "session:account:token:";

    //密码校验
    public static final String password_check =PRE + "password_check:";


    //验证码校验
    public static final String code_check =PRE + "code_check:";

    //验证设备控制状态
    public static final String wait_device =PRE + "wait:device:";


    // 门锁验证状态
    public static final String wait_lock_device =PRE + "wait:lock_device:";

    // 锁定主控
    public static final String wait_lock_reply_device =PRE + "wait:lock_repply_device:";
}
