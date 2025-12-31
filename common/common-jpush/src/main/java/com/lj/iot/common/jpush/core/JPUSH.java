package com.lj.iot.common.jpush.core;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.lj.iot.common.base.dto.FutureDto;
import com.lj.iot.common.jpush.dto.Alert;
import com.lj.iot.common.jpush.dto.JPushDto;
import com.lj.iot.common.jpush.properties.JPushProperties;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

/**
 * @author mz
 * @Date 2022/7/27
 * @since 1.0.0
 */
@Slf4j
public class JPUSH {

    private static DefaultEventExecutorGroup executors;

    private final JPushClient client;

    private static JPUSH instance;

    protected JPUSH(JPushClient jPushClient, JPushProperties properties) {
        this.client = jPushClient;
        instance = this;
        executors = new DefaultEventExecutorGroup(properties.getNThread());
    }

    /**
     * 异步
     *
     * @param dto
     * @param listener
     */
    public static void async(JPushDto dto, FutureListener listener) {
        Promise<Object> promise = new DefaultPromise<>(executors.next());
        promise.addListener(listener);
        executors.next().execute(() -> {
//            FutureDto futureDto = new FutureDto();
//            futureDto.setBody(dto);
//            futureDto.setSuccess(push(dto));
            promise.setSuccess(push(dto));
        });
    }

    public static FutureDto push(JPushDto dto) {

        final PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.alias(dto.getAlias()))
                .setOptions(Options.newBuilder().setApnsProduction(true).build())
                .setNotification(Notification.newBuilder()
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setAlert(dto.getAlert())
                                .build())
                        .addPlatformNotification(IosNotification.newBuilder()
                                .setAlert(dto.getAlert())
                                .incrBadge(1)
                                .setSound("default")
                                .setMutableContent(true)
                                .build())
                        .build())
                .build();

        try {
            log.info("result string------");
            return FutureDto.builder()
                    .success(true)
                    .body(dto)
                    .code(0 + "")
                    .message("success")
                    .build();
        } catch (Exception e) {
            log.error("push error", e);
            return FutureDto.builder()
                    .body(dto)
                    .success(false)
                    .code("-1")
                    .message(e.getMessage())
                    .build();
        }
    }
}
