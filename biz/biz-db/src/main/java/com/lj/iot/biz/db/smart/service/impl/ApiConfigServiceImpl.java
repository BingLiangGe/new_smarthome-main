package com.lj.iot.biz.db.smart.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.db.smart.entity.ApiConfig;
import com.lj.iot.biz.db.smart.entity.HotelOpen;
import com.lj.iot.biz.db.smart.mapper.ApiConfigMapper;
import com.lj.iot.biz.db.smart.mapper.HotelOpenMapper;
import com.lj.iot.biz.db.smart.service.IApiConfigService;
import com.lj.iot.biz.db.smart.service.IHotelOpenService;
import com.lj.iot.common.util.OkHttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author tyj
 * @since 2023-7-10 15:34:32
 */
@Slf4j
@Service
public class ApiConfigServiceImpl extends ServiceImpl<ApiConfigMapper, ApiConfig> implements IApiConfigService {

    // 根据实际需求调整线程池大小
    private static final int THREAD_POOL_SIZE = 10;

    @Override
    public void sendApiConfigData(JSONObject params, String pathLast) {
        List<ApiConfig> list = list();

        if (list.isEmpty()) {
            log.warn("服务配置列表为空，无需发送数据");
            return;
        }
        // 创建 ForkJoinPool
        ForkJoinPool forkJoinPool = new ForkJoinPool(THREAD_POOL_SIZE);
        // 创建一个列表，用于存储每个异步任务的 CompletableFuture 对象
        List<CompletableFuture<Void>> completableFutures = new ArrayList<>();
        // 遍历接口配置列表，每个配置启动一个异步任务发送数据
        list.forEach(config -> {
            CompletableFuture<Void> future = CompletableFuture
                    .runAsync(() -> sendApiConfigDataAsync(config, params, pathLast), forkJoinPool)
                    .exceptionally(ex -> {
                        log.warn("推送三方内容出现异常，忽略异常，继续推送下一条数据", ex);
                        return null; // 忽略异常，不中断业务逻辑
                    });
            completableFutures.add(future);
        });

        // 等待所有 CompletableFuture 完成
        CompletableFuture<Void> allOf = CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0]));
        try {
            allOf.join(); // 并行执行任务，更灵活地处理异常情况
        } catch (Exception e) {
            log.error("等待异步操作完成时出现异常", e);
        } finally {
            forkJoinPool.shutdown(); // 关闭 ForkJoinPool
        }
    }


    public void sendApiConfigDataAsync(ApiConfig config, JSONObject params, String pathLast) {
        try {
            // 发送数据到指定的接口地址，并记录日志
            OkHttpUtils.postJson(config.getApiUrl() + pathLast, params);
            log.info("推送三方内容成功 url={},参数={}", config.getApiUrl() + pathLast, params);
        } catch (IOException e) {
            // 发送失败时记录错误日志，并进行异常处理
            log.error("推送三方内容失败，url={},参数={}", config.getApiUrl() + pathLast, params, e);
            // 异常处理：可以记录日志、进行重试等
        }
    }
}
