package com.lj.iot.common.base.exception;

import com.lj.iot.common.base.enums.CommonCodeEnum;

public class CommonException extends RuntimeException {
    private Integer code;
    private String msg;

    public CommonException(Integer code, String message) {
        super(message);
        this.msg = message;
        this.code = code;
    }

    public static CommonException SUCCESS() {
        return new CommonException(CommonCodeEnum.SUCCESS.getCode(), "OK");
    }

    public static CommonException FAILURE() {
        return new CommonException(CommonCodeEnum.FAILURE.getCode(), "FAILURE");
    }

    public static CommonException FAILURE(String message) {
        return new CommonException(CommonCodeEnum.FAILURE.getCode(), message);
    }

    public static CommonException ERROR(String message) {
        return new CommonException(CommonCodeEnum.FAILURE.getCode(), message);
    }


    public static CommonException INSTANCE(Integer code, String message) {
        return new CommonException(code, message);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
