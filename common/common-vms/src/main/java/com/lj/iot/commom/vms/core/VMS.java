package com.lj.iot.commom.vms.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.dyvmsapi20170525.Client;
import com.aliyun.dyvmsapi20170525.models.SingleCallByTtsRequest;
import com.aliyun.dyvmsapi20170525.models.SingleCallByTtsResponse;
import com.lj.iot.commom.vms.dto.VmsDto;
import com.lj.iot.commom.vms.properties.VmsProperties;
import com.lj.iot.common.base.dto.FutureDto;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

/**
 * 电话呼救
 *
 * @author mz
 * @Date 2022/7/27
 * @since 1.0.0
 */
@Slf4j
public class VMS {

    private final Client client;
    private static VMS instance;
    private final VmsProperties properties;
    private static DefaultEventExecutorGroup executors;

    protected VMS(Client client, VmsProperties properties) {
        this.client = client;
        VMS.instance = this;
        this.properties = properties;
        executors = new DefaultEventExecutorGroup(properties.getNThread());

    }

    /**
     * 异步
     *
     * @param dto
     * @param listener
     */
    public static void async(VmsDto dto, FutureListener listener) {
        Promise<Object> promise = new DefaultPromise<>(executors.next());
        promise.addListener(listener);
        executors.next().execute(() -> {
            promise.setSuccess(call(dto));
        });
    }


    public static FutureDto call(VmsDto dto) {
        JSONObject params = new JSONObject();
        String message = "家庭" + dto.getHomeName() + (dto.getRoomName() == null ? "" : "房间" + dto.getRoomName()) + "的设备" + dto.getDeviceName();
        params.put("deviceName", message);

        final SingleCallByTtsRequest request = new SingleCallByTtsRequest()
                .setTtsCode(VMS.instance.properties.getTtsCode())
                .setCalledNumber(dto.getMobile())
                .setCalledShowNumber(VMS.instance.properties.getCalledShowNumber())
                .setTtsParam(JSON.toJSONString(params));
        try {
            log.info("VMS.call: {}", JSON.toJSONString(dto));
            final SingleCallByTtsResponse response = VMS.instance.client.singleCallByTts(request);
            log.info("VMS.call: {}", JSON.toJSONString(response));
            return FutureDto.builder()
                    .body(dto)
                    .success("OK".equalsIgnoreCase(response.getBody().getCode()))
                    .code(response.getBody().getCode())
                    .message(response.getBody().getMessage())
                    .build();
        } catch (Exception e) {
            log.error("CALL.call呼叫失败", e);
            return FutureDto.builder()
                    .success(false)
                    .body(dto)
                    .code("-1")
                    .message(e.getMessage())
                    .build();
        }
    }
}
