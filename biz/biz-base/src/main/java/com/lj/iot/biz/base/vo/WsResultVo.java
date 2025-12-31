package com.lj.iot.biz.base.vo;

import com.lj.iot.common.base.constant.CodeConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WsResultVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 渠道
     */
    private String channel;

    /**
     * 用户ID
     */
    private List<String> userIds;

    private Long homeId;

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
     * @param data 需返回的结果
     * @param
     * @return
     */
    public static WsResultVo SUCCESS(List<String> userIds, Long homeId, String channel, Object data) {
        return WsResultVo.builder()
                .userIds(userIds)
                .homeId(homeId)
                .channel(channel)
                .code(CodeConstant.SUCCESS)
                .msg("success")
                .data(data)
                .build();
    }


    /**
     * 返回成功的结果
     *
     * @param data 需返回的结果
     * @param
     * @return
     */
    public static WsResultVo SUCCESS(String channel, Object data) {
        return WsResultVo.builder()
                .channel(channel)
                .code(CodeConstant.SUCCESS)
                .msg("success")
                .data(data)
                .build();
    }

    /**
     * 返回成功的结果
     *
     * @param data 需返回的结果
     * @param
     * @return
     */
    public static WsResultVo SUCCESS(String userId, Long homeId, String channel, Object data) {
        return WsResultVo.builder()
                .userIds(Collections.singletonList(userId))
                .homeId(homeId)
                .channel(channel)
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
    public static WsResultVo FAILURE(List<String> userIds, Long homeId, String channel, Object data) {
        return WsResultVo.builder()
                .userIds(userIds)
                .homeId(homeId)
                .channel(channel)
                .code(CodeConstant.FAILURE)
                .data(data)
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
    public static WsResultVo FAILURE(String userId, Long homeId, String channel, Object data) {
        return WsResultVo.builder()
                .userIds(Collections.singletonList(userId))
                .homeId(homeId)
                .channel(channel)
                .code(CodeConstant.FAILURE)
                .data(data)
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
    public static WsResultVo FAILURE(List<String> userIds, Long homeId, String channel, Object data, String msg) {
        return WsResultVo.builder()
                .userIds(userIds)
                .homeId(homeId)
                .channel(channel)
                .code(CodeConstant.FAILURE)
                .data(data)
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
    public static WsResultVo FAILURE(String userId, Long homeId, String channel, Object data, String msg) {
        return WsResultVo.builder()
                .userIds(Collections.singletonList(userId))
                .homeId(homeId)
                .channel(channel)
                .code(CodeConstant.FAILURE)
                .data(data)
                .msg(msg)
                .build();
    }
}
