package com.lj.iot.biz.db.smart.service;

import com.lj.iot.biz.base.dto.HomeDirectDeviceDto;
import com.lj.iot.biz.db.smart.entity.HomeDirectDevice;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  直连设备服务类
 * </p>
 *
 * @author xm
 * @since 2022-12-30
 */
public interface IHomeDirectDeviceService extends IService<HomeDirectDevice> {

    HomeDirectDevice add(HomeDirectDeviceDto dto, String uId);

    HomeDirectDevice edit(HomeDirectDeviceDto dto, String uId);

    HomeDirectDevice get(Long homeId);
}
