package com.lj.iot.biz.db.smart.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.biz.db.smart.entity.ApiConfig;
import com.lj.iot.biz.db.smart.entity.HotelOpen;

/**
 * <p>
 *  服务配置
 * </p>
 *
 * @author tyj
 * @since 2023-7-10 15:34:58
 */
public interface IApiConfigService extends IService<ApiConfig> {

    /**
     * 第三方统一发送如入口
     * @param params
     */
    void sendApiConfigData(JSONObject params,String pathLast);
}
