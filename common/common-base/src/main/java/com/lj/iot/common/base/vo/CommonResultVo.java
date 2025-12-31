package com.lj.iot.common.base.vo;

import com.lj.iot.common.base.constant.CodeConstant;
import com.lj.iot.common.base.enums.CommonCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonResultVo<T> {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 消息
     */
    private String msg;

    /**
     * 数据
     */
    private T data;

    /**
     * 最后行动时间
     */
    private Object lastAction;

    private Object extendData;
    /**
     * 房间实时状态
     */
    private Object homeStatus;

    /**
     * 请求成功
     *
     * @param <T>
     * @return
     */
    public static <T> CommonResultVo<T> SUCCESS() {
        return CommonResultVo.<T>builder()
                .code(CodeConstant.SUCCESS)
                .msg("success")
                .build();
    }

    /**
     * 不存在
     *
     * @param <T>
     * @return
     */
    public static <T> CommonResultVo<T> NOT_EXIST() {
        return CommonResultVo.<T>builder()
                .code(CommonCodeEnum.NOT_EXIST.getCode())
                .msg("数据不存在")
                .build();
    }

    /**
     * 返回成功的结果
     *
     * @param data 需返回的结果
     * @param <T>
     * @return
     */
    public static <T> CommonResultVo<T> SUCCESS(T data) {
        return CommonResultVo.<T>builder()
                .code(CodeConstant.SUCCESS)
                .msg("success")
                .data(data)
                .build();
    }

    /**
     * 返回成功的结果
     *
     * @param data 需返回的结果
     * @param <T>
     * @return
     */
    public static <T> CommonResultVo<T> SUCCESS(T data,Object extendData) {
        return CommonResultVo.<T>builder()
                .code(CodeConstant.SUCCESS)
                .msg("success")
                .data(data)
                .extendData(extendData)
                .build();
    }


    /**
     * 返回成功的结果
     *
     * @param data 需返回的结果
     * @param <T>
     * @return
     */
    public static <T> CommonResultVo<T> HOTELSUCCESS(T data,Object homeStatus) {
        return CommonResultVo.<T>builder()
                .code(CodeConstant.SUCCESS)
                .msg("success")
                .data(data)
                .lastAction(homeStatus)
                .build();
    }

    /**
     * 返回成功的结果
     *
     * @param data 需返回的结果
     * @param <T>
     * @return
     */
    public static <T> CommonResultVo<T> HOTELSTATUSSUCCESS(T data,Object lastAction) {
        return CommonResultVo.<T>builder()
                .code(CodeConstant.SUCCESS)
                .msg("success")
                .data(data)
                .homeStatus(lastAction)
                .build();
    }

    /**
     * 请求成功返回自定义消息
     *
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> CommonResultVo<T> SUCCESS_MSG(String msg) {
        return CommonResultVo.<T>builder()
                .code(CodeConstant.SUCCESS)
                .msg(msg)
                .build();
    }

    /**
     * 请求失败
     *
     * @param <T>
     * @return
     */
    public static <T> CommonResultVo<T> FAILURE() {
        return CommonResultVo.<T>builder()
                .code(CodeConstant.FAILURE)
                .msg("failure")
                .build();
    }

    /**
     * 请求失败返回自定义消息
     *
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> CommonResultVo<T> FAILURE_MSG(String msg) {
        return CommonResultVo.<T>builder()
                .code(CodeConstant.FAILURE)
                .msg(msg)
                .build();
    }

    /**
     * 自定义错误码返回
     *
     * @param <T>
     * @return
     */
    public static <T> CommonResultVo<T> INSTANCE(Integer code, String msg) {
        return CommonResultVo.<T>builder()
                .code(code)
                .msg(msg)
                .build();
    }

    /**
     * 自定义
     *
     * @param code
     * @param msg
     * @param data
     * @param <T>
     * @return
     */
    public static <T> CommonResultVo<T> INSTANCE(Integer code, String msg, T data) {
        return CommonResultVo.<T>builder()
                .code(code)
                .msg(msg)
                .data(data)
                .build();
    }
}
