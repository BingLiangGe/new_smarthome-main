package com.lj.iot.biz.base.vo;

import com.alibaba.fastjson.JSON;
import com.lj.iot.common.base.constant.CodeConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MqttResultVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    private String id;

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
    private Object data;

    /**
     * 返回成功的结果
     *
     * @param
     * @param
     * @return
     */
    public static MqttResultVo SUCCESS(String id) {
        return MqttResultVo.builder()
                .id(id)
                .code(CodeConstant.SUCCESS)
                .msg("success")
                .build();
    }

    /**
     * 返回成功的结果
     *
     * @param data 需返回的结果
     * @param
     * @return
     */
    public static MqttResultVo SUCCESS(String id, Object data) {
        return MqttResultVo.builder()
                .id(id)
                .code(CodeConstant.SUCCESS)
                .msg("success")
                .data(data)
                .build();
    }

    /**
     * 请求失败返回自定义消息
     *
     * @param
     * @param
     * @return
     */
    public static MqttResultVo FAILURE(String id) {
        return MqttResultVo.builder()
                .id(id)
                .code(CodeConstant.FAILURE)
                .msg("failure")
                .build();
    }

    /**
     * 请求失败返回自定义消息
     *
     * @param
     * @param
     * @return
     */
    public static MqttResultVo FAILURE(String id, Object data) {
        return MqttResultVo.builder()
                .id(id)
                .code(CodeConstant.FAILURE)
                .msg("failure")
                .build();
    }

    /**
     * 请求失败返回自定义消息
     *
     * @param
     * @param
     * @return
     */
    public static MqttResultVo FAILURE_MSG(String id, String msg) {
        return MqttResultVo.builder()
                .id(id)
                .code(CodeConstant.FAILURE)
                .msg(msg)
                .build();
    }
    /**
     * 请求失败返回自定义消息
     *
     * @param
     * @param
     * @return
     */
    public static MqttResultVo FAILURE_MSG(String id, Integer code,String msg) {
        return MqttResultVo.builder()
                .id(id)
                .code(code)
                .msg(msg)
                .build();
    }

    /**
     * 请求失败返回自定义消息
     *
     * @param
     * @param
     * @return
     */
    public static MqttResultVo FAILURE_MSG(String id, Integer code,String msg,Object data) {
        return MqttResultVo.builder()
                .id(id)
                .code(code)
                .msg(msg)
                .data(data)
                .build();
    }
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
